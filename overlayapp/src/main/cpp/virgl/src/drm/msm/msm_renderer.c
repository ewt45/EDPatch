/*
 * Copyright 2022 Google LLC
 * SPDX-License-Identifier: MIT
 */

#include <errno.h>
#include <fcntl.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <sys/mman.h>
#include <sys/types.h>

#include <xf86drm.h>

#include "virgl_context.h"
#include "virgl_util.h"
#include "virglrenderer.h"

#include "util/anon_file.h"
#include "util/hash_table.h"
#include "util/macros.h"
#include "util/os_file.h"
#include "util/u_atomic.h"
#include "util/u_thread.h"

#include "drm_fence.h"

#include "msm_drm.h"
#include "msm_proto.h"
#include "msm_renderer.h"

static unsigned nr_timelines;

/**
 * A single context (from the PoV of the virtio-gpu protocol) maps to
 * a single drm device open.  Other drm/msm constructs (ie. submitqueue)
 * are opaque to the protocol.
 *
 * Typically each guest process will open a single virtio-gpu "context".
 * The single drm device open maps to an individual msm_gem_address_space
 * on the kernel side, providing GPU address space isolation between
 * guest processes.
 *
 * GEM buffer objects are tracked via one of two id's:
 *  - resource-id:  global, assigned by guest kernel
 *  - blob-id:      context specific, assigned by guest userspace
 *
 * The blob-id is used to link the bo created via MSM_CCMD_GEM_NEW and
 * the get_blob() cb.  It is unused in the case of a bo that is imported
 * from another context.  An object is added to the blob table in GEM_NEW
 * and removed in ctx->get_blob() (where it is added to resource_table).
 * By avoiding having an obj in both tables, we can safely free remaining
 * entries in either hashtable at context teardown.
 */
struct msm_context {
   struct virgl_context base;

   struct msm_shmem *shmem;
   uint8_t *rsp_mem;
   uint32_t rsp_mem_sz;

   struct msm_ccmd_rsp *current_rsp;

   int fd;

   struct hash_table *blob_table;
   struct hash_table *resource_table;

   /**
    * Maps submit-queue id to ring_idx
    */
   struct hash_table *sq_to_ring_idx_table;

   int eventfd;

   /**
    * Indexed by ring_idx-1, which is the same as the submitqueue priority+1.
    * On the kernel side, there is some drm_sched_entity per {drm_file, prio}
    * tuple, and the sched entity determines the fence timeline, ie. submits
    * against a single sched entity complete in fifo order.
    */
   struct drm_timeline timelines[];
};
DEFINE_CAST(virgl_context, msm_context)

#define valid_payload_len(req) ((req)->len <= ((req)->hdr.len - sizeof(*(req))))

static struct hash_entry *
table_search(struct hash_table *ht, uint32_t key)
{
   /* zero is not a valid key for u32_keys hashtable: */
   if (!key)
      return NULL;
   return _mesa_hash_table_search(ht, (void *)(uintptr_t)key);
}

static int
gem_info(struct msm_context *mctx, uint32_t handle, uint32_t param, uint64_t *val)
{
   struct drm_msm_gem_info args = {
      .handle = handle,
      .info = param,
      .value = *val,
   };
   int ret;

   ret = drmCommandWriteRead(mctx->fd, DRM_MSM_GEM_INFO, &args, sizeof(args));
   if (ret)
      return ret;

   *val = args.value;
   return 0;
}

static int
gem_close(int fd, uint32_t handle)
{
   struct drm_gem_close close_req = {
      .handle = handle,
   };
   return drmIoctl(fd, DRM_IOCTL_GEM_CLOSE, &close_req);
}

struct msm_object {
   uint32_t blob_id;
   uint32_t res_id;
   uint32_t handle;
   uint32_t flags;
   uint32_t size;
   bool exported   : 1;
   bool exportable : 1;
   struct virgl_resource *res;
};

static struct msm_object *
msm_object_create(uint32_t handle, uint32_t flags, uint32_t size)
{
   struct msm_object *obj = calloc(1, sizeof(*obj));

   if (!obj)
      return NULL;

   obj->handle = handle;
   obj->flags = flags;
   obj->size = size;

   return obj;
}

static bool
valid_blob_id(struct msm_context *mctx, uint32_t blob_id)
{
   /* must be non-zero: */
   if (blob_id == 0)
      return false;

   /* must not already be in-use: */
   if (table_search(mctx->blob_table, blob_id))
      return false;

   return true;
}

static void
msm_object_set_blob_id(struct msm_context *mctx, struct msm_object *obj, uint32_t blob_id)
{
   assert(valid_blob_id(mctx, blob_id));

   obj->blob_id = blob_id;
   _mesa_hash_table_insert(mctx->blob_table, (void *)(uintptr_t)obj->blob_id, obj);
}

static bool
valid_res_id(struct msm_context *mctx, uint32_t res_id)
{
   return !table_search(mctx->resource_table, res_id);
}

static void
msm_object_set_res_id(struct msm_context *mctx, struct msm_object *obj, uint32_t res_id)
{
   assert(valid_res_id(mctx, res_id));

   obj->res_id = res_id;
   _mesa_hash_table_insert(mctx->resource_table, (void *)(uintptr_t)obj->res_id, obj);
}

static void
msm_remove_object(struct msm_context *mctx, struct msm_object *obj)
{
   drm_dbg("obj=%p, blob_id=%u, res_id=%u", obj, obj->blob_id, obj->res_id);
   _mesa_hash_table_remove_key(mctx->resource_table, (void *)(uintptr_t)obj->res_id);
}

static struct msm_object *
msm_retrieve_object_from_blob_id(struct msm_context *mctx, uint64_t blob_id)
{
   assert((blob_id >> 32) == 0);
   uint32_t id = blob_id;
   struct hash_entry *entry = table_search(mctx->blob_table, id);
   if (!entry)
      return NULL;
   struct msm_object *obj = entry->data;
   _mesa_hash_table_remove(mctx->blob_table, entry);
   return obj;
}

static struct msm_object *
msm_get_object_from_res_id(struct msm_context *mctx, uint32_t res_id)
{
   const struct hash_entry *entry = table_search(mctx->resource_table, res_id);
   return likely(entry) ? entry->data : NULL;
}

static uint32_t
handle_from_res_id(struct msm_context *mctx, uint32_t res_id)
{
   struct msm_object *obj = msm_get_object_from_res_id(mctx, res_id);
   if (!obj)
      return 0;    /* zero is an invalid GEM handle */
   return obj->handle;
}

static bool
has_cached_coherent(int fd)
{
   struct drm_msm_gem_new new_req = {
      .size = 0x1000,
      .flags = MSM_BO_CACHED_COHERENT,
   };

   /* Do a test allocation to see if cached-coherent is supported: */
   if (!drmCommandWriteRead(fd, DRM_MSM_GEM_NEW, &new_req, sizeof(new_req))) {
      gem_close(fd, new_req.handle);
      return true;
   }

   return false;
}

static int
get_param64(int fd, uint32_t param, uint64_t *value)
{
   struct drm_msm_param req = {
      .pipe = MSM_PIPE_3D0,
      .param = param,
   };
   int ret;

   *value = 0;

   ret = drmCommandWriteRead(fd, DRM_MSM_GET_PARAM, &req, sizeof(req));
   if (ret)
      return ret;

   *value = req.value;

   return 0;
}

static int
get_param32(int fd, uint32_t param, uint32_t *value)
{
   uint64_t v64;
   int ret = get_param64(fd, param, &v64);
   *value = v64;
   return ret;
}

/**
 * Probe capset params.
 */
int
msm_renderer_probe(int fd, struct virgl_renderer_capset_drm *capset)
{
   drm_log("");

   /* Require MSM_SUBMIT_FENCE_SN_IN: */
   if (capset->version_minor < 9) {
      drm_log("Host kernel too old");
      return -ENOTSUP;
   }

   capset->wire_format_version = 2;
   capset->u.msm.has_cached_coherent = has_cached_coherent(fd);

   get_param32(fd, MSM_PARAM_PRIORITIES, &capset->u.msm.priorities);
   get_param64(fd, MSM_PARAM_VA_START,   &capset->u.msm.va_start);
   get_param64(fd, MSM_PARAM_VA_SIZE,    &capset->u.msm.va_size);
   get_param32(fd, MSM_PARAM_GPU_ID,     &capset->u.msm.gpu_id);
   get_param32(fd, MSM_PARAM_GMEM_SIZE,  &capset->u.msm.gmem_size);
   get_param64(fd, MSM_PARAM_GMEM_BASE,  &capset->u.msm.gmem_base);
   get_param64(fd, MSM_PARAM_CHIP_ID,    &capset->u.msm.chip_id);
   get_param32(fd, MSM_PARAM_MAX_FREQ,   &capset->u.msm.max_freq);

   nr_timelines = capset->u.msm.priorities;

   drm_log("wire_format_version: %u", capset->wire_format_version);
   drm_log("version_major:       %u", capset->version_major);
   drm_log("version_minor:       %u", capset->version_minor);
   drm_log("version_patchlevel:  %u", capset->version_patchlevel);
   drm_log("has_cached_coherent: %u", capset->u.msm.has_cached_coherent);
   drm_log("priorities:          %u", capset->u.msm.priorities);
   drm_log("va_start:            0x%0" PRIx64, capset->u.msm.va_start);
   drm_log("va_size:             0x%0" PRIx64, capset->u.msm.va_size);
   drm_log("gpu_id:              %u", capset->u.msm.gpu_id);
   drm_log("gmem_size:           %u", capset->u.msm.gmem_size);
   drm_log("gmem_base:           0x%0" PRIx64, capset->u.msm.gmem_base);
   drm_log("chip_id:             0x%0" PRIx64, capset->u.msm.chip_id);
   drm_log("max_freq:            %u", capset->u.msm.max_freq);

   if (!capset->u.msm.va_size) {
      drm_log("Host kernel does not support userspace allocated IOVA");
      return -ENOTSUP;
   }

   return 0;
}

static void
resource_delete_fxn(struct hash_entry *entry)
{
   free((void *)entry->data);
}

static void
msm_renderer_destroy(struct virgl_context *vctx)
{
   struct msm_context *mctx = to_msm_context(vctx);

   for (unsigned i = 0; i < nr_timelines; i++)
      drm_timeline_fini(&mctx->timelines[i]);

   close(mctx->eventfd);

   if (mctx->shmem)
      munmap(mctx->shmem, sizeof(*mctx->shmem));

   _mesa_hash_table_destroy(mctx->resource_table, resource_delete_fxn);
   _mesa_hash_table_destroy(mctx->blob_table, resource_delete_fxn);
   _mesa_hash_table_destroy(mctx->sq_to_ring_idx_table, NULL);

   close(mctx->fd);
   free(mctx);
}

static void
msm_renderer_attach_resource(struct virgl_context *vctx, struct virgl_resource *res)
{
   struct msm_context *mctx = to_msm_context(vctx);
   struct msm_object *obj = msm_get_object_from_res_id(mctx, res->res_id);

   drm_dbg("obj=%p, res_id=%u", obj, res->res_id);

   if (!obj) {
      int fd;
      enum virgl_resource_fd_type fd_type = virgl_resource_export_fd(res, &fd);

      /* If importing a dmabuf resource created by another context (or
       * externally), then import it to create a gem obj handle in our
       * context:
       */
      if (fd_type == VIRGL_RESOURCE_FD_DMABUF) {
         uint32_t handle;
         int ret;

         ret = drmPrimeFDToHandle(mctx->fd, fd, &handle);
         if (ret) {
            drm_log("Could not import: %s", strerror(errno));
            close(fd);
            return;
         }

         /* lseek() to get bo size */
         int size = lseek(fd, 0, SEEK_END);
         if (size < 0)
            drm_log("lseek failed: %d (%s)", size, strerror(errno));
         close(fd);

         obj = msm_object_create(handle, 0, size);
         if (!obj)
            return;

         msm_object_set_res_id(mctx, obj, res->res_id);

         drm_dbg("obj=%p, res_id=%u, handle=%u", obj, obj->res_id, obj->handle);
      } else {
         if (fd_type != VIRGL_RESOURCE_FD_INVALID)
            close(fd);
         return;
      }
   }

   obj->res = res;
}

static void
msm_renderer_detach_resource(struct virgl_context *vctx, struct virgl_resource *res)
{
   struct msm_context *mctx = to_msm_context(vctx);
   struct msm_object *obj = msm_get_object_from_res_id(mctx, res->res_id);

   drm_dbg("obj=%p, res_id=%u", obj, res->res_id);

   if (!obj || (obj->res != res))
      return;

   if (res->fd_type == VIRGL_RESOURCE_FD_SHM) {
      munmap(mctx->shmem, sizeof(*mctx->shmem));

      mctx->shmem = NULL;
      mctx->rsp_mem = NULL;
      mctx->rsp_mem_sz = 0;

      /* shmem resources don't have an backing host GEM bo:, so bail now: */
      return;
   }

   msm_remove_object(mctx, obj);

   gem_close(mctx->fd, obj->handle);

   free(obj);
}

static enum virgl_resource_fd_type
msm_renderer_export_opaque_handle(struct virgl_context *vctx, struct virgl_resource *res,
                                  int *out_fd)
{
   struct msm_context *mctx = to_msm_context(vctx);
   struct msm_object *obj = msm_get_object_from_res_id(mctx, res->res_id);
   int ret;

   drm_dbg("obj=%p, res_id=%u", obj, res->res_id);

   if (!obj) {
      drm_log("invalid res_id %u", res->res_id);
      return VIRGL_RESOURCE_FD_INVALID;
   }

   if (!obj->exportable) {
      /* crosvm seems to like to export things it doesn't actually need an
       * fd for.. don't let it spam our fd table!
       */
      return VIRGL_RESOURCE_FD_INVALID;
   }

   ret = drmPrimeHandleToFD(mctx->fd, obj->handle, DRM_CLOEXEC | DRM_RDWR, out_fd);
   if (ret) {
      drm_log("failed to get dmabuf fd: %s", strerror(errno));
      return VIRGL_RESOURCE_FD_INVALID;
   }

   return VIRGL_RESOURCE_FD_DMABUF;
}

static int
msm_renderer_transfer_3d(UNUSED struct virgl_context *vctx,
                         UNUSED struct virgl_resource *res,
                         UNUSED const struct vrend_transfer_info *info,
                         UNUSED int transfer_mode)
{
   drm_log("unsupported");
   return -1;
}

static int
msm_renderer_get_blob(struct virgl_context *vctx, uint32_t res_id, uint64_t blob_id,
                      uint64_t blob_size, uint32_t blob_flags,
                      struct virgl_context_blob *blob)
{
   struct msm_context *mctx = to_msm_context(vctx);

   drm_dbg("blob_id=%" PRIu64 ", res_id=%u, blob_size=%" PRIu64 ", blob_flags=0x%x",
           blob_id, res_id, blob_size, blob_flags);

   if ((blob_id >> 32) != 0) {
      drm_log("invalid blob_id: %" PRIu64, blob_id);
      return -EINVAL;
   }

   /* blob_id of zero is reserved for the shmem buffer: */
   if (blob_id == 0) {
      int fd;

      if (blob_flags != VIRGL_RENDERER_BLOB_FLAG_USE_MAPPABLE) {
         drm_log("invalid blob_flags: 0x%x", blob_flags);
         return -EINVAL;
      }

      if (mctx->shmem) {
         drm_log("There can be only one!");
         return -EINVAL;
      }

      fd = os_create_anonymous_file(blob_size, "msm-shmem");
      if (fd < 0) {
         drm_log("Failed to create shmem file: %s", strerror(errno));
         return -ENOMEM;
      }

      int ret = fcntl(fd, F_ADD_SEALS, F_SEAL_SEAL | F_SEAL_SHRINK | F_SEAL_GROW);
      if (ret) {
         drm_log("fcntl failed: %s", strerror(errno));
         close(fd);
         return -ENOMEM;
      }

      mctx->shmem = mmap(NULL, blob_size, PROT_WRITE | PROT_READ, MAP_SHARED, fd, 0);
      if (mctx->shmem == MAP_FAILED) {
         drm_log("shmem mmap failed: %s", strerror(errno));
         close(fd);
         return -ENOMEM;
      }

      mctx->shmem->rsp_mem_offset = sizeof(*mctx->shmem);

      uint8_t *ptr = (uint8_t *)mctx->shmem;
      mctx->rsp_mem = &ptr[mctx->shmem->rsp_mem_offset];
      mctx->rsp_mem_sz = blob_size - mctx->shmem->rsp_mem_offset;

      blob->type = VIRGL_RESOURCE_FD_SHM;
      blob->u.fd = fd;
      blob->map_info = VIRGL_RENDERER_MAP_CACHE_CACHED;

      return 0;
   }

   if (!valid_res_id(mctx, res_id)) {
      drm_log("Invalid res_id %u", res_id);
      return -EINVAL;
   }

   struct msm_object *obj = msm_retrieve_object_from_blob_id(mctx, blob_id);

   /* If GEM_NEW fails, we can end up here without a backing obj: */
   if (!obj) {
      drm_log("No object");
      return -ENOENT;
   }

   /* a memory can only be exported once; we don't want two resources to point
    * to the same storage.
    */
   if (obj->exported) {
      drm_log("Already exported!");
      return -EINVAL;
   }

   msm_object_set_res_id(mctx, obj, res_id);

   if (blob_flags & VIRGL_RENDERER_BLOB_FLAG_USE_SHAREABLE) {
      int fd, ret;

      ret = drmPrimeHandleToFD(mctx->fd, obj->handle, DRM_CLOEXEC | DRM_RDWR, &fd);
      if (ret) {
         drm_log("Export to fd failed");
         return -EINVAL;
      }

      blob->type = VIRGL_RESOURCE_FD_DMABUF;
      blob->u.fd = fd;
   } else {
      blob->type = VIRGL_RESOURCE_OPAQUE_HANDLE;
      blob->u.opaque_handle = obj->handle;
   }

   if (obj->flags & MSM_BO_CACHED_COHERENT) {
      blob->map_info = VIRGL_RENDERER_MAP_CACHE_CACHED;
   } else {
      blob->map_info = VIRGL_RENDERER_MAP_CACHE_WC;
   }

   obj->exported = true;
   obj->exportable = !!(blob_flags & VIRGL_RENDERER_BLOB_FLAG_USE_MAPPABLE);

   return 0;
}

static void *
msm_context_rsp_noshadow(struct msm_context *mctx, const struct msm_ccmd_req *hdr)
{
   return &mctx->rsp_mem[hdr->rsp_off];
}

static void *
msm_context_rsp(struct msm_context *mctx, const struct msm_ccmd_req *hdr, unsigned len)
{
   unsigned rsp_mem_sz = mctx->rsp_mem_sz;
   unsigned off = hdr->rsp_off;

   if ((off > rsp_mem_sz) || (len > rsp_mem_sz - off)) {
      drm_log("invalid shm offset: off=%u, len=%u (shmem_size=%u)", off, len, rsp_mem_sz);
      return NULL;
   }

   struct msm_ccmd_rsp *rsp = msm_context_rsp_noshadow(mctx, hdr);

   assert(len >= sizeof(*rsp));

   /* With newer host and older guest, we could end up wanting a larger rsp struct
    * than guest expects, so allocate a shadow buffer in this case rather than
    * having to deal with this in all the different ccmd handlers.  This is similar
    * in a way to what drm_ioctl() does.
    */
   if (len > rsp->len) {
      rsp = malloc(len);
      if (!rsp)
         return NULL;
      rsp->len = len;
   }

   mctx->current_rsp = rsp;

   return rsp;
}

static int
msm_ccmd_nop(UNUSED struct msm_context *mctx, UNUSED const struct msm_ccmd_req *hdr)
{
   return 0;
}

static int
msm_ccmd_ioctl_simple(struct msm_context *mctx, const struct msm_ccmd_req *hdr)
{
   const struct msm_ccmd_ioctl_simple_req *req = to_msm_ccmd_ioctl_simple_req(hdr);
   unsigned payload_len = _IOC_SIZE(req->cmd);
   unsigned req_len = size_add(sizeof(*req), payload_len);

   if (hdr->len != req_len) {
      drm_log("%u != %u", hdr->len, req_len);
      return -EINVAL;
   }

   /* Apply a reasonable upper bound on ioctl size: */
   if (payload_len > 128) {
      drm_log("invalid ioctl payload length: %u", payload_len);
      return -EINVAL;
   }

   /* Allow-list of supported ioctls: */
   unsigned iocnr = _IOC_NR(req->cmd) - DRM_COMMAND_BASE;
   switch (iocnr) {
   case DRM_MSM_GET_PARAM:
   case DRM_MSM_SUBMITQUEUE_NEW:
   case DRM_MSM_SUBMITQUEUE_CLOSE:
      break;
   default:
      drm_log("invalid ioctl: %08x (%u)", req->cmd, iocnr);
      return -EINVAL;
   }

   struct msm_ccmd_ioctl_simple_rsp *rsp;
   unsigned rsp_len = sizeof(*rsp);

   if (req->cmd & IOC_OUT)
      rsp_len = size_add(rsp_len, payload_len);

   rsp = msm_context_rsp(mctx, hdr, rsp_len);

   if (!rsp)
      return -ENOMEM;

   /* Copy the payload because the kernel can write (if IOC_OUT bit
    * is set) and to avoid casting away the const:
    */
   char payload[payload_len];
   memcpy(payload, req->payload, payload_len);

   rsp->ret = drmIoctl(mctx->fd, req->cmd, payload);

   if (req->cmd & IOC_OUT)
      memcpy(rsp->payload, payload, payload_len);

   if (iocnr == DRM_MSM_SUBMITQUEUE_NEW && !rsp->ret) {
      struct drm_msm_submitqueue *args = (void *)payload;

      drm_dbg("submitqueue %u, prio %u", args->id, args->prio);

      _mesa_hash_table_insert(mctx->sq_to_ring_idx_table, (void *)(uintptr_t)args->id,
                              (void *)(uintptr_t)args->prio);
   }

   return 0;
}

static int
msm_ccmd_gem_new(struct msm_context *mctx, const struct msm_ccmd_req *hdr)
{
   const struct msm_ccmd_gem_new_req *req = to_msm_ccmd_gem_new_req(hdr);
   int ret = 0;

   if (!valid_blob_id(mctx, req->blob_id)) {
      drm_log("Invalid blob_id %u", req->blob_id);
      ret = -EINVAL;
      goto out_error;
   }

   /*
    * First part, allocate the GEM bo:
    */
   struct drm_msm_gem_new gem_new = {
      .size = req->size,
      .flags = req->flags,
   };

   ret = drmCommandWriteRead(mctx->fd, DRM_MSM_GEM_NEW, &gem_new, sizeof(gem_new));
   if (ret) {
      drm_log("GEM_NEW failed: %d (%s)", ret, strerror(errno));
      goto out_error;
   }

   /*
    * Second part, set the iova:
    */
   uint64_t iova = req->iova;
   ret = gem_info(mctx, gem_new.handle, MSM_INFO_SET_IOVA, &iova);
   if (ret) {
      drm_log("SET_IOVA failed: %d (%s)", ret, strerror(errno));
      goto out_close;
   }

   /*
    * And then finally create our msm_object for tracking the resource,
    * and add to blob table:
    */
   struct msm_object *obj = msm_object_create(gem_new.handle, req->flags, req->size);

   if (!obj) {
      ret = -ENOMEM;
      goto out_close;
   }

   msm_object_set_blob_id(mctx, obj, req->blob_id);

   drm_dbg("obj=%p, blob_id=%u, handle=%u, iova=%" PRIx64, obj, obj->blob_id,
           obj->handle, iova);

   return 0;

out_close:
   gem_close(mctx->fd, gem_new.handle);
out_error:
   if (mctx->shmem)
      mctx->shmem->async_error++;
   return ret;
}

static int
msm_ccmd_gem_set_iova(struct msm_context *mctx, const struct msm_ccmd_req *hdr)
{
   const struct msm_ccmd_gem_set_iova_req *req = to_msm_ccmd_gem_set_iova_req(hdr);
   struct msm_object *obj = msm_get_object_from_res_id(mctx, req->res_id);
   int ret = 0;

   if (!obj) {
      drm_log("Could not lookup obj: res_id=%u", req->res_id);
      ret = -ENOENT;
      goto out_error;
   }

   uint64_t iova = req->iova;
   ret = gem_info(mctx, obj->handle, MSM_INFO_SET_IOVA, &iova);
   if (ret) {
      drm_log("SET_IOVA failed: %d (%s)", ret, strerror(errno));
      goto out_error;
   }

   drm_dbg("obj=%p, blob_id=%u, handle=%u, iova=%" PRIx64, obj, obj->blob_id,
           obj->handle, iova);

   return 0;

out_error:
   if (mctx->shmem)
      mctx->shmem->async_error++;
   return 0;
}

static int
msm_ccmd_gem_cpu_prep(struct msm_context *mctx, const struct msm_ccmd_req *hdr)
{
   const struct msm_ccmd_gem_cpu_prep_req *req = to_msm_ccmd_gem_cpu_prep_req(hdr);
   struct msm_ccmd_gem_cpu_prep_rsp *rsp = msm_context_rsp(mctx, hdr, sizeof(*rsp));

   if (!rsp)
      return -ENOMEM;

   struct drm_msm_gem_cpu_prep args = {
      .handle = handle_from_res_id(mctx, req->res_id),
      .op = req->op | MSM_PREP_NOSYNC,
   };

   rsp->ret = drmCommandWrite(mctx->fd, DRM_MSM_GEM_CPU_PREP, &args, sizeof(args));

   return 0;
}

static int
msm_ccmd_gem_set_name(struct msm_context *mctx, const struct msm_ccmd_req *hdr)
{
   const struct msm_ccmd_gem_set_name_req *req = to_msm_ccmd_gem_set_name_req(hdr);

   struct drm_msm_gem_info args = {
      .handle = handle_from_res_id(mctx, req->res_id),
      .info = MSM_INFO_SET_NAME,
      .value = VOID2U64(req->payload),
      .len = req->len,
   };

   if (!valid_payload_len(req))
      return -EINVAL;

   int ret = drmCommandWrite(mctx->fd, DRM_MSM_GEM_INFO, &args, sizeof(args));
   if (ret)
      drm_log("ret=%d, len=%u, name=%.*s", ret, req->len, req->len, req->payload);

   return 0;
}

static void
msm_dump_submit(struct drm_msm_gem_submit *req)
{
#ifndef NDEBUG
   drm_log("  flags=0x%x, queueid=%u", req->flags, req->queueid);
   for (unsigned i = 0; i < req->nr_bos; i++) {
      struct drm_msm_gem_submit_bo *bos = U642VOID(req->bos);
      struct drm_msm_gem_submit_bo *bo = &bos[i];
      drm_log("  bos[%d]: handle=%u, flags=%x", i, bo->handle, bo->flags);
   }
   for (unsigned i = 0; i < req->nr_cmds; i++) {
      struct drm_msm_gem_submit_cmd *cmds = U642VOID(req->cmds);
      struct drm_msm_gem_submit_cmd *cmd = &cmds[i];
      drm_log("  cmd[%d]: type=%u, submit_idx=%u, submit_offset=%u, size=%u", i,
              cmd->type, cmd->submit_idx, cmd->submit_offset, cmd->size);
   }
#else
   (void)req;
#endif
}

static int
msm_ccmd_gem_submit(struct msm_context *mctx, const struct msm_ccmd_req *hdr)
{
   const struct msm_ccmd_gem_submit_req *req = to_msm_ccmd_gem_submit_req(hdr);

   size_t sz = sizeof(*req);
   sz = size_add(sz, size_mul(req->nr_bos,  sizeof(struct drm_msm_gem_submit_bo)));
   sz = size_add(sz, size_mul(req->nr_cmds, sizeof(struct drm_msm_gem_submit_cmd)));

   /* Normally kernel would validate out of bounds situations and return -EFAULT,
    * but since we are copying the bo handles, we need to validate that the
    * guest can't trigger us to make an out of bounds memory access:
    */
   if (sz > hdr->len) {
      drm_log("out of bounds: nr_bos=%u, nr_cmds=%u", req->nr_bos, req->nr_cmds);
      return -ENOSPC;
   }

   const unsigned bo_limit = 8192 / sizeof(struct drm_msm_gem_submit_bo);
   bool bos_on_stack = req->nr_bos < bo_limit;
   struct drm_msm_gem_submit_bo _bos[bos_on_stack ? req->nr_bos : 0];
   struct drm_msm_gem_submit_bo *bos;

   if (bos_on_stack) {
      bos = _bos;
   } else {
      bos = malloc(req->nr_bos * sizeof(bos[0]));
      if (!bos)
         return -ENOMEM;
   }

   memcpy(bos, req->payload, req->nr_bos * sizeof(bos[0]));

   for (uint32_t i = 0; i < req->nr_bos; i++)
      bos[i].handle = handle_from_res_id(mctx, bos[i].handle);

   struct drm_msm_gem_submit args = {
      .flags = req->flags | MSM_SUBMIT_FENCE_FD_OUT | MSM_SUBMIT_FENCE_SN_IN,
      .fence = req->fence,
      .nr_bos = req->nr_bos,
      .nr_cmds = req->nr_cmds,
      .bos = VOID2U64(bos),
      .cmds = VOID2U64(&req->payload[req->nr_bos * sizeof(struct drm_msm_gem_submit_bo)]),
      .queueid = req->queue_id,
   };

   int ret = drmCommandWriteRead(mctx->fd, DRM_MSM_GEM_SUBMIT, &args, sizeof(args));
   drm_dbg("fence=%u, ret=%d", args.fence, ret);

   if (unlikely(ret)) {
      drm_log("submit failed: %s", strerror(errno));
      msm_dump_submit(&args);
      if (mctx->shmem)
         mctx->shmem->async_error++;
   } else {
      const struct hash_entry *entry =
            table_search(mctx->sq_to_ring_idx_table, args.queueid);

      if (!entry) {
         drm_log("unknown submitqueue: %u", args.queueid);
         goto out;
      }

      unsigned prio = (uintptr_t)entry->data;

      drm_timeline_set_last_fence_fd(&mctx->timelines[prio], args.fence_fd);
   }

out:
   if (!bos_on_stack)
      free(bos);
   return 0;
}

static int
msm_ccmd_gem_upload(struct msm_context *mctx, const struct msm_ccmd_req *hdr)
{
   const struct msm_ccmd_gem_upload_req *req = to_msm_ccmd_gem_upload_req(hdr);
   uint64_t offset;
   int ret;

   if (req->pad || !valid_payload_len(req)) {
      drm_log("Invalid upload ccmd");
      return -EINVAL;
   }

   uint32_t handle = handle_from_res_id(mctx, req->res_id);
   ret = gem_info(mctx, handle, MSM_INFO_GET_OFFSET, &offset);
   if (ret) {
      drm_log("alloc failed: %s", strerror(errno));
      return ret;
   }

   uint8_t *map =
      mmap(0, req->len + req->off, PROT_READ | PROT_WRITE, MAP_SHARED, mctx->fd, offset);
   if (map == MAP_FAILED) {
      drm_log("mmap failed: %s", strerror(errno));
      return -ENOMEM;
   }

   memcpy(&map[req->off], req->payload, req->len);

   munmap(map, req->len);

   return 0;
}

static int
msm_ccmd_submitqueue_query(struct msm_context *mctx, const struct msm_ccmd_req *hdr)
{
   const struct msm_ccmd_submitqueue_query_req *req =
      to_msm_ccmd_submitqueue_query_req(hdr);
   struct msm_ccmd_submitqueue_query_rsp *rsp =
      msm_context_rsp(mctx, hdr, size_add(sizeof(*rsp), req->len));

   if (!rsp)
      return -ENOMEM;

   struct drm_msm_submitqueue_query args = {
      .data = VOID2U64(rsp->payload),
      .id = req->queue_id,
      .param = req->param,
      .len = req->len,
   };

   rsp->ret =
      drmCommandWriteRead(mctx->fd, DRM_MSM_SUBMITQUEUE_QUERY, &args, sizeof(args));

   rsp->out_len = args.len;

   return 0;
}

static int
msm_ccmd_wait_fence(struct msm_context *mctx, const struct msm_ccmd_req *hdr)
{
   const struct msm_ccmd_wait_fence_req *req = to_msm_ccmd_wait_fence_req(hdr);
   struct msm_ccmd_wait_fence_rsp *rsp = msm_context_rsp(mctx, hdr, sizeof(*rsp));

   if (!rsp)
      return -ENOMEM;

   struct timespec t;

   /* Use current time as timeout, to avoid blocking: */
   clock_gettime(CLOCK_MONOTONIC, &t);

   struct drm_msm_wait_fence args = {
      .fence = req->fence,
      .queueid = req->queue_id,
      .timeout =
         {
            .tv_sec = t.tv_sec,
            .tv_nsec = t.tv_nsec,
         },
   };

   rsp->ret = drmCommandWrite(mctx->fd, DRM_MSM_WAIT_FENCE, &args, sizeof(args));

   return 0;
}

static int
msm_ccmd_set_debuginfo(struct msm_context *mctx, const struct msm_ccmd_req *hdr)
{
   const struct msm_ccmd_set_debuginfo_req *req = to_msm_ccmd_set_debuginfo_req(hdr);

   size_t sz = sizeof(*req);
   sz = size_add(sz, req->comm_len);
   sz = size_add(sz, req->cmdline_len);

   if (sz > hdr->len) {
      drm_log("out of bounds: comm_len=%u, cmdline_len=%u", req->comm_len, req->cmdline_len);
      return -ENOSPC;
   }

   struct drm_msm_param set_comm = {
      .pipe = MSM_PIPE_3D0,
      .param = MSM_PARAM_COMM,
      .value = VOID2U64(&req->payload[0]),
      .len = req->comm_len,
   };

   drmCommandWriteRead(mctx->fd, DRM_MSM_SET_PARAM, &set_comm, sizeof(set_comm));

   struct drm_msm_param set_cmdline = {
      .pipe = MSM_PIPE_3D0,
      .param = MSM_PARAM_CMDLINE,
      .value = VOID2U64(&req->payload[req->comm_len]),
      .len = req->cmdline_len,
   };

   drmCommandWriteRead(mctx->fd, DRM_MSM_SET_PARAM, &set_cmdline, sizeof(set_cmdline));

   return 0;
}

static const struct ccmd {
   const char *name;
   int (*handler)(struct msm_context *mctx, const struct msm_ccmd_req *hdr);
   size_t size;
} ccmd_dispatch[] = {
#define HANDLER(N, n)                                                                    \
   [MSM_CCMD_##N] = {#N, msm_ccmd_##n, sizeof(struct msm_ccmd_##n##_req)}
   HANDLER(NOP, nop),
   HANDLER(IOCTL_SIMPLE, ioctl_simple),
   HANDLER(GEM_NEW, gem_new),
   HANDLER(GEM_SET_IOVA, gem_set_iova),
   HANDLER(GEM_CPU_PREP, gem_cpu_prep),
   HANDLER(GEM_SET_NAME, gem_set_name),
   HANDLER(GEM_SUBMIT, gem_submit),
   HANDLER(GEM_UPLOAD, gem_upload),
   HANDLER(SUBMITQUEUE_QUERY, submitqueue_query),
   HANDLER(WAIT_FENCE, wait_fence),
   HANDLER(SET_DEBUGINFO, set_debuginfo),
};

static int
submit_cmd_dispatch(struct msm_context *mctx, const struct msm_ccmd_req *hdr)
{
   int ret;

   if (hdr->cmd >= ARRAY_SIZE(ccmd_dispatch)) {
      drm_log("invalid cmd: %u", hdr->cmd);
      return -EINVAL;
   }

   const struct ccmd *ccmd = &ccmd_dispatch[hdr->cmd];

   if (!ccmd->handler) {
      drm_log("no handler: %u", hdr->cmd);
      return -EINVAL;
   }

   drm_dbg("%s: hdr={cmd=%u, len=%u, seqno=%u, rsp_off=0x%x)", ccmd->name, hdr->cmd,
           hdr->len, hdr->seqno, hdr->rsp_off);

   /* If the request length from the guest is smaller than the expected
    * size, ie. newer host and older guest, we need to make a copy of
    * the request with the new fields at the end zero initialized.
    */
   if (ccmd->size > hdr->len) {
      uint8_t buf[ccmd->size];

      memcpy(&buf[0], hdr, hdr->len);
      memset(&buf[hdr->len], 0, ccmd->size - hdr->len);

      ret = ccmd->handler(mctx, (struct msm_ccmd_req *)buf);
   } else {
      ret = ccmd->handler(mctx, hdr);
   }

   if (ret) {
      drm_log("%s: dispatch failed: %d (%s)", ccmd->name, ret, strerror(errno));
      return ret;
   }

   /* If the response length from the guest is smaller than the
    * expected size, ie. newer host and older guest, then a shadow
    * copy is used, and we need to copy back to the actual rsp
    * buffer.
    */
   struct msm_ccmd_rsp *rsp = msm_context_rsp_noshadow(mctx, hdr);
   if (mctx->current_rsp && (mctx->current_rsp != rsp)) {
      unsigned len = rsp->len;
      memcpy(rsp, mctx->current_rsp, len);
      rsp->len = len;
      free(mctx->current_rsp);
   }
   mctx->current_rsp = NULL;

   /* Note that commands with no response, like SET_DEBUGINFO, could
    * be sent before the shmem buffer is allocated:
    */
   if (mctx->shmem) {
      /* TODO better way to do this?  We need ACQ_REL semanatics (AFAIU)
       * to ensure that writes to response buffer are visible to the
       * guest process before the update of the seqno.  Otherwise we
       * could just use p_atomic_set.
       */
      uint32_t seqno = hdr->seqno;
      p_atomic_xchg(&mctx->shmem->seqno, seqno);
   }

   return 0;
}

static int
msm_renderer_submit_cmd(struct virgl_context *vctx, const void *_buffer, size_t size)
{
   struct msm_context *mctx = to_msm_context(vctx);
   const uint8_t *buffer = _buffer;

   while (size >= sizeof(struct msm_ccmd_req)) {
      const struct msm_ccmd_req *hdr = (const struct msm_ccmd_req *)buffer;

      /* Sanity check first: */
      if ((hdr->len > size) || (hdr->len < sizeof(*hdr)) || (hdr->len % 4)) {
         drm_log("bad size, %u vs %zu (%u)", hdr->len, size, hdr->cmd);
         return -EINVAL;
      }

      if (hdr->rsp_off % 4) {
         drm_log("bad rsp_off, %u", hdr->rsp_off);
         return -EINVAL;
      }

      int ret = submit_cmd_dispatch(mctx, hdr);
      if (ret) {
         drm_log("dispatch failed: %d (%u)", ret, hdr->cmd);
         return ret;
      }

      buffer += hdr->len;
      size -= hdr->len;
   }

   if (size > 0) {
      drm_log("bad size, %zu trailing bytes", size);
      return -EINVAL;
   }

   return 0;
}

static int
msm_renderer_get_fencing_fd(struct virgl_context *vctx)
{
   struct msm_context *mctx = to_msm_context(vctx);
   return mctx->eventfd;
}

static void
msm_renderer_retire_fences(UNUSED struct virgl_context *vctx)
{
   /* No-op as VIRGL_RENDERER_ASYNC_FENCE_CB is required */
}

static int
msm_renderer_submit_fence(struct virgl_context *vctx, uint32_t flags, uint64_t queue_id,
                          uint64_t fence_id)
{
   struct msm_context *mctx = to_msm_context(vctx);

   drm_dbg("flags=0x%x, queue_id=%" PRIu64 ", fence_id=%" PRIu64, flags,
           queue_id, fence_id);

   /* timeline is queue_id-1 (because queue_id 0 is host CPU timeline) */
   if (queue_id > nr_timelines) {
      drm_log("invalid queue_id: %" PRIu64, queue_id);
      return -EINVAL;
   }

   /* ring_idx zero is used for the guest to synchronize with host CPU,
    * meaning by the time ->submit_fence() is called, the fence has
    * already passed.. so just immediate signal:
    */
   if (queue_id == 0) {
      vctx->fence_retire(vctx, queue_id, fence_id);
      return 0;
   }

   return drm_timeline_submit_fence(&mctx->timelines[queue_id - 1], flags, fence_id);
}

struct virgl_context *
msm_renderer_create(int fd)
{
   struct msm_context *mctx;

   drm_log("");

   mctx = calloc(1, sizeof(*mctx) + (nr_timelines * sizeof(mctx->timelines[0])));
   if (!mctx)
      return NULL;

   mctx->fd = fd;

   /* Indexed by blob_id, but only lower 32b of blob_id are used: */
   mctx->blob_table = _mesa_hash_table_create_u32_keys(NULL);
   /* Indexed by res_id: */
   mctx->resource_table = _mesa_hash_table_create_u32_keys(NULL);
   /* Indexed by submitqueue-id: */
   mctx->sq_to_ring_idx_table = _mesa_hash_table_create_u32_keys(NULL);

   mctx->eventfd = create_eventfd(0);

   for (unsigned i = 0; i < nr_timelines; i++) {
      unsigned ring_idx = i + 1; /* ring_idx 0 is host CPU */
      drm_timeline_init(&mctx->timelines[i], &mctx->base, "msm-sync", mctx->eventfd,
                        ring_idx);
   }

   mctx->base.destroy = msm_renderer_destroy;
   mctx->base.attach_resource = msm_renderer_attach_resource;
   mctx->base.detach_resource = msm_renderer_detach_resource;
   mctx->base.export_opaque_handle = msm_renderer_export_opaque_handle;
   mctx->base.transfer_3d = msm_renderer_transfer_3d;
   mctx->base.get_blob = msm_renderer_get_blob;
   mctx->base.submit_cmd = msm_renderer_submit_cmd;
   mctx->base.get_fencing_fd = msm_renderer_get_fencing_fd;
   mctx->base.retire_fences = msm_renderer_retire_fences;
   mctx->base.submit_fence = msm_renderer_submit_fence;

   return &mctx->base;
}
