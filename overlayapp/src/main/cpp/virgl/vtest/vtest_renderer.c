/**************************************************************************
 *
 * Copyright (C) 2015 Red Hat Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.  IN NO EVENT SHALL
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR
 * OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 *
 **************************************************************************/

#ifdef HAVE_CONFIG_H
#include "config.h"
#endif

#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <time.h>
#include <unistd.h>
#include <fcntl.h>
#include <limits.h>

#include "virgl_hw.h"
#include "virglrenderer.h"

#include <sys/uio.h>
#include <sys/socket.h>
#include <sys/mman.h>

#include <epoxy/egl.h>
#include <pthread.h>
#include <vrend_renderer.h>

#ifdef HAVE_EVENTFD_H
#include <sys/eventfd.h>
#endif

#include "vtest.h"
#include "vtest_shm.h"
#include "vtest_protocol.h"

#include "util.h"
#include "util/u_debug.h"
#include "util/u_double_list.h"
#include "util/u_math.h"
#include "util/u_memory.h"
#include "util/u_pointer.h"
#include "util/u_hash_table.h"

#define VTEST_MAX_SYNC_QUEUE_COUNT 64

#ifdef  ANDROID_JNI
#include <jni.h>
#include <android/native_window.h>
#include <android/native_window_jni.h>
#include <android/log.h>

#define printf(...) __android_log_print(ANDROID_LOG_DEBUG, "virgl", __VA_ARGS__)
#define perror(...) __android_log_print(ANDROID_LOG_DEBUG, "virgl", __VA_ARGS__)
#endif

#define FL_GLX (1<<1)
#define FL_GLES (1<<2)
#define FL_OVERLAY (1<<3)
#define FL_MULTITHREAD (1<<4)

extern struct vrend_context *overlay_ctx;

int dxtn_decompress; //DXTn (S3TC) decompress

static char sock_path[128];
static int flags;

pthread_mutex_t mutex = PTHREAD_MUTEX_INITIALIZER;
EGLDisplay disp;

struct dt_record
{
    uint32_t used;
    EGLSurface egl_surf;
    int fb_id;
    int res_id;
#ifdef ANDROID_JNI
    jobject java_surf;
#endif
};

struct vtest_resource {
   struct list_head head;

   uint32_t server_res_id;
   uint32_t res_id;

   struct iovec iov;
};

struct vtest_sync {
   struct list_head head;

   int sync_id;
   int refcount;

   uint64_t value;
};

struct vtest_sync_queue {
   struct list_head submits;
};

struct vtest_sync_queue_submit {
   struct list_head head;

   struct vtest_sync_queue *sync_queue;

   uint32_t count;
   struct vtest_sync **syncs;
   uint64_t *values;
};

struct vtest_sync_wait {
   struct list_head head;

   int fd;

   uint32_t flags;
   uint64_t valid_before;

   uint32_t count;
   struct vtest_sync **syncs;
   uint64_t *values;

   uint32_t signaled_count;
};

struct vtest_context {
   struct list_head head;

   int ctx_id;

   struct vtest_input *input;
   int out_fd;

   char *debug_name;

   unsigned protocol_version;
   unsigned capset_id;
   bool context_initialized;

   struct util_hash_table *resource_table;
   struct util_hash_table *sync_table;

   struct vtest_sync_queue sync_queues[VTEST_MAX_SYNC_QUEUE_COUNT];

   struct list_head sync_waits;
};

struct vtest_renderer {
   // pipe
   int fd;
   int flags;

   // egl
   EGLDisplay egl_display;
   EGLConfig egl_conf;
   EGLContext egl_ctx;
   EGLSurface egl_fake_surf;

   // renderer
   int ctx_id;

   const char *rendernode_name;
   bool multi_clients;
   uint32_t ctx_flags;

   uint32_t max_length;

   struct dt_record dts[32];
#ifdef ANDROID_JNI
   struct jni_s
   {
       jclass cls;
       JNIEnv *env;
       jmethodID create;
       jmethodID get_surface;
       jmethodID set_rect;
       jmethodID destroy;
   } jni;
#endif
   struct util_hash_table *resource_table;

   int implicit_fence_submitted;
   int implicit_fence_completed;

   struct list_head active_contexts;
   struct list_head free_contexts;
   int next_context_id;

   struct list_head free_resources;
   int next_resource_id;

   struct list_head free_syncs;
   int next_sync_id;

   struct vtest_context *current_context;
};

struct vtest_input *input;
struct vtest_renderer *r0;

/*
 * VCMD_RESOURCE_BUSY_WAIT is used to wait GPU works (VCMD_SUBMIT_CMD) or CPU
 * works (VCMD_TRANSFER_GET2).  A fence is needed only for GPU works.
 */
static void vtest_create_implicit_fence(struct vtest_renderer *renderer)
{
   virgl_renderer_create_fence(++renderer->implicit_fence_submitted, 0);
}

static void vtest_write_implicit_fence(UNUSED void *cookie, uint32_t fence_id_in)
{
   struct vtest_renderer *renderer = (struct vtest_renderer*)cookie;
   renderer->implicit_fence_completed = fence_id_in;
}

static void vtest_signal_sync_queue(struct vtest_sync_queue *queue,
                                    struct vtest_sync_queue_submit *to_submit);

static void vtest_write_context_fence(UNUSED void *cookie,
                                      UNUSED uint32_t ctx_id,
                                      UNUSED uint64_t queue_id,
                                      ///void *fence_cookie)
                                      uint64_t fence_id)
{
   ///struct vtest_sync_queue_submit *submit = fence_cookie;
   struct vtest_sync_queue_submit *submit = (void*)(uintptr_t)fence_id;
   vtest_signal_sync_queue(submit->sync_queue, submit);
}

static int vtest_get_drm_fd(void *cookie)
{
   int fd = -1;
   struct vtest_renderer *renderer = (struct vtest_renderer*)cookie;
   if (!renderer->rendernode_name)
      return -1;
   fd = open(renderer->rendernode_name, O_RDWR | O_CLOEXEC | O_NOCTTY | O_NONBLOCK);
   if (fd == -1)
      fprintf(stderr, "Unable to open rendernode '%s' falling back to default search\n",
              renderer->rendernode_name);
   return fd;
}

static struct virgl_renderer_callbacks renderer_cbs = {
   .version = VIRGL_RENDERER_CALLBACKS_VERSION,
   .write_fence = vtest_write_implicit_fence,
   .get_drm_fd = vtest_get_drm_fd,
   .write_context_fence = vtest_write_context_fence,
};


static struct vtest_renderer renderer = {
   .max_length = UINT_MAX,
   .next_context_id = 1,
   .next_resource_id = 1,
   .next_sync_id = 1,
};

static struct vtest_resource *vtest_new_resource(struct vtest_renderer *r, uint32_t client_res_id)
{
   struct vtest_resource *res;

   if (LIST_IS_EMPTY(&r->free_resources)) {
      res = malloc(sizeof(*res));
      if (!res) {
         return NULL;
      }

      res->server_res_id = r->next_resource_id++;
   } else {
      res = LIST_ENTRY(struct vtest_resource, r->free_resources.next, head);
      list_del(&res->head);
   }

   res->res_id = client_res_id ? client_res_id : res->server_res_id;
   res->iov.iov_base = NULL;
   res->iov.iov_len = 0;

   return res;
}

static void vtest_unref_resource(struct vtest_resource *res)
{
   /* virgl_renderer_ctx_detach_resource and virgl_renderer_resource_detach_iov
    * are implied
    */
   virgl_renderer_resource_unref(res->res_id);

   if (res->iov.iov_base)
      munmap(res->iov.iov_base, res->iov.iov_len);

   list_add(&res->head, &r0->free_resources);
}

static struct vtest_sync *vtest_new_sync(uint64_t value)
{
   struct vtest_sync *sync;

   if (LIST_IS_EMPTY(&renderer.free_syncs)) {
      sync = malloc(sizeof(*sync));
      if (!sync) {
         return NULL;
      }

      sync->sync_id = renderer.next_sync_id++;
   } else {
      sync = LIST_ENTRY(struct vtest_sync, renderer.free_syncs.next, head);
      list_del(&sync->head);
   }

   sync->refcount = 1;
   sync->value = value;

   return sync;
}

static struct vtest_sync *vtest_ref_sync(struct vtest_sync *sync)
{
   sync->refcount++;
   return sync;
}

static void vtest_unref_sync(struct vtest_sync *sync)
{
   assert(sync->refcount);
   sync->refcount--;
   if (sync->refcount)
      return;

   list_add(&sync->head, &renderer.free_syncs);
}

static void vtest_free_sync_queue_submit(struct vtest_sync_queue_submit *submit)
{
   uint32_t i;
   for (i = 0; i < submit->count; i++)
      vtest_unref_sync(submit->syncs[i]);
   free(submit);
}

static void vtest_free_sync_wait(struct vtest_sync_wait *wait)
{
   uint32_t i;

   for (i = 0; i < wait->count; i++) {
      if (wait->syncs[i])
         vtest_unref_sync(wait->syncs[i]);
   }
   close(wait->fd);
   free(wait);
}

static unsigned
u32_hash_func(void *key)
{
   intptr_t ip = pointer_to_intptr(key);
   return (unsigned)(ip & 0xffffffff);
}

static int
u32_compare_func(void *key1, void *key2)
{
   if (key1 < key2) {
      return -1;
   } else if (key1 > key2) {
      return 1;
   } else {
      return 0;
   }
}

static void
resource_destroy_func(void *value)
{
   struct vtest_resource *res = value;
   vtest_unref_resource(res);
}

static void
sync_destroy_func(void *value)
{
   struct vtest_sync *sync = value;
   vtest_unref_sync(sync);
}

static int vtest_block_write(struct vtest_renderer *r, void *buf, int size)
{
   char *ptr = buf;
   int left;
   int ret;
   left = size;

   do {
      ret = write(r->fd, ptr, left);
      if (ret < 0) {
         return -errno;
      }

      left -= ret;
      ptr += ret;
   } while (left);

   return size;
}

int vtest_block_read(struct vtest_input *input0, void *buf, int size)
{
   int fd = input0->data.fd;
   char *ptr = buf;
   int left;
   int ret;
   static int savefd = -1;

   left = size;
   do {
      ret = read(fd, ptr, left);
      if (ret <= 0) {
         return ret == -1 ? -errno : 0;
      }

      left -= ret;
      ptr += ret;
   } while (left);

   if (getenv("VTEST_SAVE")) {
      if (savefd == -1) {
         savefd = open(getenv("VTEST_SAVE"),
                       O_CLOEXEC|O_CREAT|O_WRONLY|O_TRUNC|O_DSYNC, S_IRUSR|S_IWUSR);
         if (savefd == -1) {
            perror("error opening save file");
            exit(1);
         }
      }
      if (write(savefd, buf, size) != size) {
         perror("failed to save");
         exit(1);
      }
   }

   return size;
}

static int vtest_send_fd(int socket_fd, int fd)
{
    struct iovec iovec;
    char buf[CMSG_SPACE(sizeof(int))];
    char c = 0;
    struct msghdr msgh = { 0 };
    memset(buf, 0, sizeof(buf));

    iovec.iov_base = &c;
    iovec.iov_len = sizeof(char);

    msgh.msg_name = NULL;
    msgh.msg_namelen = 0;
    msgh.msg_iov = &iovec;
    msgh.msg_iovlen = 1;
    msgh.msg_control = buf;
    msgh.msg_controllen = sizeof(buf);
    msgh.msg_flags = 0;

    struct cmsghdr *cmsg = CMSG_FIRSTHDR(&msgh);
    cmsg->cmsg_level = SOL_SOCKET;
    cmsg->cmsg_type = SCM_RIGHTS;
    cmsg->cmsg_len = CMSG_LEN(sizeof(int));

    *((int *) CMSG_DATA(cmsg)) = fd;

    int size = sendmsg(socket_fd, &msgh, 0);
    if (size < 0) {
      return report_failure("Failed to send fd", -EINVAL);
    }

    return 0;
}

int vtest_buf_read(struct vtest_input *input, void *buf, int size)
{
   struct vtest_buffer *inbuf = input->data.buffer;
   if (size > inbuf->size) {
      return 0;
   }

   memcpy(buf, inbuf->buffer, size);
   inbuf->buffer += size;
   inbuf->size -= size;

   return size;
}

int vtest_init_renderer(bool multi_clients,
                        int ctx_flags,
                        const char *render_device)
{
   int ret;

   renderer.rendernode_name = render_device;
   list_inithead(&renderer.active_contexts);
   list_inithead(&renderer.free_contexts);
   list_inithead(&renderer.free_resources);
   list_inithead(&renderer.free_syncs);

   ctx_flags |= VIRGL_RENDERER_THREAD_SYNC |
                VIRGL_RENDERER_USE_EXTERNAL_BLOB;
   ret = virgl_renderer_init(&renderer, ctx_flags, &renderer_cbs);
   if (ret) {
      fprintf(stderr, "failed to initialise renderer.\n");
      return -1;
   }

   renderer.multi_clients = multi_clients;
   renderer.ctx_flags = ctx_flags;

   return 0;
}

static void vtest_free_context(struct vtest_context *ctx, bool cleanup);

void vtest_cleanup_renderer(void)
{
   if (renderer.next_context_id > 1) {
      struct vtest_context *ctx, *tmp;

      LIST_FOR_EACH_ENTRY_SAFE(ctx, tmp, &renderer.active_contexts, head) {
         vtest_destroy_context(ctx);
      }
      LIST_FOR_EACH_ENTRY_SAFE(ctx, tmp, &renderer.free_contexts, head) {
         vtest_free_context(ctx, true);
      }
      list_inithead(&renderer.active_contexts);
      list_inithead(&renderer.free_contexts);

      renderer.next_context_id = 1;
      renderer.current_context = NULL;
   }

   if (renderer.next_resource_id > 1) {
      struct vtest_resource *res, *tmp;

      LIST_FOR_EACH_ENTRY_SAFE(res, tmp, &renderer.free_resources, head) {
         free(res);
      }
      list_inithead(&renderer.free_resources);

      renderer.next_resource_id = 1;
   }

   if (renderer.next_sync_id > 1) {
      struct vtest_sync *sync, *tmp;

      LIST_FOR_EACH_ENTRY_SAFE(sync, tmp, &renderer.free_syncs, head) {
         assert(!sync->refcount);
         free(sync);
      }
      list_inithead(&renderer.free_syncs);

      renderer.next_sync_id = 1;
   }

   virgl_renderer_cleanup(&renderer);
}

static struct vtest_context *vtest_new_context(struct vtest_input *input,
                                               int out_fd)
{
   struct vtest_context *ctx;

   if (LIST_IS_EMPTY(&renderer.free_contexts)) {
      uint32_t i;

      ctx = malloc(sizeof(*ctx));
      if (!ctx) {
         return NULL;
      }

      ctx->resource_table = util_hash_table_create(u32_hash_func,
                                                   u32_compare_func,
                                                   resource_destroy_func);
      if (!ctx->resource_table) {
         free(ctx);
         return NULL;
      }

      ctx->sync_table = util_hash_table_create(u32_hash_func,
                                               u32_compare_func,
                                               sync_destroy_func);
      if (!ctx->sync_table) {
         util_hash_table_destroy(ctx->resource_table);
         free(ctx);
         return NULL;
      }

      for (i = 0; i < VTEST_MAX_SYNC_QUEUE_COUNT; i++) {
         struct vtest_sync_queue *queue = &ctx->sync_queues[i];
         list_inithead(&queue->submits);
      }

      list_inithead(&ctx->sync_waits);

      ctx->ctx_id = renderer.next_context_id++;
   } else {
      ctx = LIST_ENTRY(struct vtest_context, renderer.free_contexts.next, head);
      list_del(&ctx->head);
   }

   ctx->input = input;
   ctx->out_fd = out_fd;

   ctx->debug_name = NULL;
   /* By default we support version 0 unless VCMD_PROTOCOL_VERSION is sent */
   ctx->protocol_version = 0;
   ctx->capset_id = 0;
   ctx->context_initialized = false;

   return ctx;
}

static void vtest_free_context(struct vtest_context *ctx, bool cleanup)
{
   if (cleanup) {
      util_hash_table_destroy(ctx->resource_table);
      util_hash_table_destroy(ctx->sync_table);
      free(ctx);
   } else {
      list_add(&ctx->head, &renderer.free_contexts);
   }
}

int vtest_create_context(struct vtest_input *input, int out_fd,
                         uint32_t length, struct vtest_context **out_ctx)
{
   struct vtest_context *ctx;
   char *vtestname;
   int ret;

   if (length > 1024 * 1024) {
      return -1;
   }

   ctx = vtest_new_context(input, out_fd);
   if (!ctx) {
      return -1;
   }

   vtestname = calloc(1, length + 1);
   if (!vtestname) {
      ret = -1;
      goto err;
   }

   ret = ctx->input->read(ctx->input, vtestname, length);
   if (ret != (int)length) {
      ret = -1;
      goto err;
   }

   ctx->debug_name = vtestname;

   list_addtail(&ctx->head, &renderer.active_contexts);
   *out_ctx = ctx;

   return 0;

err:
   free(vtestname);
   vtest_free_context(ctx, false);
   return ret;
}

int vtest_lazy_init_context(struct vtest_context *ctx)
{
   int ret;

   if (ctx->context_initialized)
      return 0;

   if (renderer.multi_clients && ctx->protocol_version < 3)
      return report_failed_call("protocol version too low", -EINVAL);

   if (ctx->capset_id) {
      ret = virgl_renderer_context_create_with_flags(ctx->ctx_id,
                                                     ctx->capset_id,
                                                     strlen(ctx->debug_name),
                                                     ctx->debug_name);
   } else {
      ret = virgl_renderer_context_create(ctx->ctx_id,
                                          strlen(ctx->debug_name),
                                          ctx->debug_name);
   }
   ctx->context_initialized = (ret == 0);

   return ret;
}

void vtest_destroy_context(struct vtest_context *ctx)
{
   struct vtest_sync_wait *wait, *wait_tmp;
   uint32_t i;

   if (renderer.current_context == ctx) {
      renderer.current_context = NULL;
   }
   list_del(&ctx->head);

   for (i = 0; i < VTEST_MAX_SYNC_QUEUE_COUNT; i++) {
      struct vtest_sync_queue *queue = &ctx->sync_queues[i];
      struct vtest_sync_queue_submit *submit, *submit_tmp;

      LIST_FOR_EACH_ENTRY_SAFE(submit, submit_tmp, &queue->submits, head)
         vtest_free_sync_queue_submit(submit);
      list_inithead(&queue->submits);
   }

   LIST_FOR_EACH_ENTRY_SAFE(wait, wait_tmp, &ctx->sync_waits, head) {
      list_del(&wait->head);
      vtest_free_sync_wait(wait);
   }
   list_inithead(&ctx->sync_waits);

   free(ctx->debug_name);
   if (ctx->context_initialized)
      virgl_renderer_context_destroy(ctx->ctx_id);
   util_hash_table_clear(ctx->resource_table);
   util_hash_table_clear(ctx->sync_table);
   vtest_free_context(ctx, false);
}

void vtest_poll_context(struct vtest_context *ctx)
{
   virgl_renderer_context_poll(ctx->ctx_id);
}

int vtest_get_context_poll_fd(struct vtest_context *ctx)
{
   return virgl_renderer_context_get_poll_fd(ctx->ctx_id);
}

void vtest_set_current_context(struct vtest_context *ctx)
{
   renderer.current_context = ctx;
}

static struct vtest_context *vtest_get_current_context(void)
{
   return renderer.current_context;
}

int vtest_ping_protocol_version(struct vtest_renderer *r, UNUSED uint32_t length_dw)
{
   //struct vtest_context *ctx = vtest_get_current_context();
   uint32_t hdr_buf[VTEST_HDR_SIZE];
   int ret;

   hdr_buf[VTEST_CMD_LEN] = VCMD_PING_PROTOCOL_VERSION_SIZE;
   hdr_buf[VTEST_CMD_ID] = VCMD_PING_PROTOCOL_VERSION;
   ret = vtest_block_write(r, hdr_buf, sizeof(hdr_buf));
   if (ret < 0) {
      return ret;
   }

   return 0;
}

int vtest_protocol_version(struct vtest_renderer *r, UNUSED uint32_t length_dw)
{
   //struct vtest_context *ctx = vtest_get_current_context();
   uint32_t hdr_buf[VTEST_HDR_SIZE];
   uint32_t version_buf[VCMD_PROTOCOL_VERSION_SIZE];
   unsigned version;
   int ret;

   ret = input->read(input, &version_buf, sizeof(version_buf));
   if (ret != sizeof(version_buf))
      return -1;

   version = MIN2(version_buf[VCMD_PROTOCOL_VERSION_VERSION],
                  VTEST_PROTOCOL_VERSION);

   /*
    * We've deprecated protocol version 1. All of it's called sites are being
    * moved protocol version 2. If the server supports version 2 and the guest
    * supports verison 1, fall back to version 0.
    */
   if (version == 1) {
      printf("Older guest Mesa detected, fallbacking to protocol version 0\n");
      version = 0;
   }

   /* Protocol version 2 requires shm support. */
   if (!vtest_shm_check()) {
      printf("Shared memory not supported, fallbacking to protocol version 0\n");
      version = 0;
   }
   else {
       printf("Shared memory supported \n");
   }

   printf("Mesa protocol version %i \n", version);

   //if (renderer.multi_clients && version < 3)
      //return report_failed_call("protocol version too low", -EINVAL);

   //ctx->protocol_version = version;

   hdr_buf[VTEST_CMD_LEN] = VCMD_PROTOCOL_VERSION_SIZE;
   hdr_buf[VTEST_CMD_ID] = VCMD_PROTOCOL_VERSION;

   version_buf[VCMD_PROTOCOL_VERSION_VERSION] = version;

   ret = vtest_block_write(r, hdr_buf, sizeof(hdr_buf));
   if (ret < 0) {
      return ret;
   }

   ret = vtest_block_write(r, version_buf, sizeof(version_buf));
   if (ret < 0) {
      return ret;
   }

   return 0;
}

int vtest_get_param(UNUSED uint32_t length_dw)
{
   struct vtest_context *ctx = vtest_get_current_context();
   uint32_t get_param_buf[VCMD_GET_PARAM_SIZE];
   uint32_t resp_buf[VTEST_HDR_SIZE + 2];
   uint32_t param;
   uint32_t *resp;
   int ret;

   ret = ctx->input->read(ctx->input, get_param_buf, sizeof(get_param_buf));
   if (ret != sizeof(get_param_buf))
      return -1;

   param = get_param_buf[VCMD_GET_PARAM_PARAM];

   resp_buf[VTEST_CMD_LEN] = 2;
   resp_buf[VTEST_CMD_ID] = VCMD_GET_PARAM;
   resp = &resp_buf[VTEST_CMD_DATA_START];
   switch (param) {
   case VCMD_PARAM_MAX_SYNC_QUEUE_COUNT:
      resp[0] = true;
      /* TODO until we have a timerfd */
#ifdef HAVE_EVENTFD_H
      if (!getenv("VIRGL_DISABLE_MT"))
         resp[1] = VTEST_MAX_SYNC_QUEUE_COUNT;
      else
         resp[1] = 0;
#else
      resp[1] = 0;
#endif
      break;
   default:
      resp[0] = false;
      resp[1] = 0;
      break;
   }

   ret = vtest_block_write(ctx->out_fd, resp_buf, sizeof(resp_buf));
   if (ret < 0)
      return -1;

   return 0;
}

int vtest_get_capset(UNUSED uint32_t length_dw)
{
   struct vtest_context *ctx = vtest_get_current_context();
   uint32_t get_capset_buf[VCMD_GET_CAPSET_SIZE];
   uint32_t resp_buf[VTEST_HDR_SIZE + 1];
   uint32_t id;
   uint32_t version;
   uint32_t max_version;
   uint32_t max_size;
   void *caps;
   int ret;

   ret = ctx->input->read(ctx->input, get_capset_buf, sizeof(get_capset_buf));
   if (ret != sizeof(get_capset_buf))
      return -1;

   id = get_capset_buf[VCMD_GET_CAPSET_ID];
   version = get_capset_buf[VCMD_GET_CAPSET_VERSION];

   virgl_renderer_get_cap_set(id, &max_version, &max_size);

   /* unsupported id or version */
   if ((!max_version && !max_size) || version > max_version) {
      resp_buf[VTEST_CMD_LEN] = 1;
      resp_buf[VTEST_CMD_ID] = VCMD_GET_CAPSET;
      resp_buf[VTEST_CMD_DATA_START] = false;
      return vtest_block_write(ctx->out_fd, resp_buf, sizeof(resp_buf));
   }

   if (max_size % 4)
      return -EINVAL;

   caps = malloc(max_size);
   if (!caps)
      return -ENOMEM;

   virgl_renderer_fill_caps(id, version, caps);

   resp_buf[VTEST_CMD_LEN] = 1 + max_size / 4;
   resp_buf[VTEST_CMD_ID] = VCMD_GET_CAPSET;
   resp_buf[VTEST_CMD_DATA_START] = true;
   ret = vtest_block_write(ctx->out_fd, resp_buf, sizeof(resp_buf));
   if (ret >= 0)
      ret = vtest_block_write(ctx->out_fd, caps, max_size);

   free(caps);
   return ret >= 0 ? 0 : ret;
}

int vtest_context_init(UNUSED uint32_t length_dw)
{
   struct vtest_context *ctx = vtest_get_current_context();
   uint32_t context_init_buf[VCMD_CONTEXT_INIT_SIZE];
   uint32_t capset_id;
   int ret;

   ret = ctx->input->read(ctx->input, context_init_buf, sizeof(context_init_buf));
   if (ret != sizeof(context_init_buf))
      return -1;

   capset_id = context_init_buf[VCMD_CONTEXT_INIT_CAPSET_ID];
   if (!capset_id)
      return -EINVAL;

   if (ctx->context_initialized) {
      return ctx->capset_id == capset_id ? 0 : -EINVAL;
   }

   ctx->capset_id = capset_id;

   return vtest_lazy_init_context(ctx);
}

int vtest_send_caps2(struct vtest_renderer *r, UNUSED uint32_t length_dw)
{
   //struct vtest_context *ctx = vtest_get_current_context();
   uint32_t hdr_buf[2];
   void *caps_buf;
   int ret;
   uint32_t max_ver, max_size;

   virgl_renderer_get_cap_set(2, &max_ver, &max_size);

   if (max_size == 0) {
      return -1;
   }

   caps_buf = malloc(max_size);
   if (!caps_buf) {
      return -1;
   }

   virgl_renderer_fill_caps(2, 1, caps_buf);

   hdr_buf[0] = max_size + 1;
   hdr_buf[1] = 2;
   ret = vtest_block_write(r, hdr_buf, 8);
   if (ret < 0) {
      goto end;
   }

   vtest_block_write(r, caps_buf, max_size);
   if (ret < 0) {
      goto end;
   }

end:
   free(caps_buf);
   return 0;
}

int vtest_send_caps(struct vtest_renderer *r, UNUSED uint32_t length_dw)
{
   //struct vtest_context *ctx = vtest_get_current_context();
   uint32_t  max_ver, max_size;
   void *caps_buf;
   uint32_t hdr_buf[2];
   int ret;

   virgl_renderer_get_cap_set(1, &max_ver, &max_size);

   caps_buf = malloc(max_size);
   if (!caps_buf) {
      return -1;
   }

   virgl_renderer_fill_caps(1, 1, caps_buf);

   hdr_buf[0] = max_size + 1;
   hdr_buf[1] = 1;
   ret = vtest_block_write(r, hdr_buf, 8);
   if (ret < 0) {
      goto end;
   }

   vtest_block_write(r, caps_buf, max_size);
   if (ret < 0) {
      goto end;
   }

end:
   free(caps_buf);
   return 0;
}

static int vtest_create_resource_decode_args(struct vtest_renderer *r,
                                             struct virgl_renderer_resource_create_args *args)
{
   uint32_t res_create_buf[VCMD_RES_CREATE_SIZE];
   int ret;

   ret = input->read(input, &res_create_buf,
                          sizeof(res_create_buf));
   if (ret != sizeof(res_create_buf)) {
      return -1;
   }

   args->handle = res_create_buf[VCMD_RES_CREATE_RES_HANDLE];
   args->target = res_create_buf[VCMD_RES_CREATE_TARGET];
   args->format = res_create_buf[VCMD_RES_CREATE_FORMAT];
   args->bind = res_create_buf[VCMD_RES_CREATE_BIND];

   args->width = res_create_buf[VCMD_RES_CREATE_WIDTH];
   args->height = res_create_buf[VCMD_RES_CREATE_HEIGHT];
   args->depth = res_create_buf[VCMD_RES_CREATE_DEPTH];
   args->array_size = res_create_buf[VCMD_RES_CREATE_ARRAY_SIZE];
   args->last_level = res_create_buf[VCMD_RES_CREATE_LAST_LEVEL];
   args->nr_samples = res_create_buf[VCMD_RES_CREATE_NR_SAMPLES];
   args->flags = 0;

   return 0;
}

static int vtest_create_resource_decode_args2(struct vtest_renderer *r,
                                              struct virgl_renderer_resource_create_args *args,
                                              size_t *shm_size)
{
   uint32_t res_create_buf[VCMD_RES_CREATE2_SIZE];
   int ret;

   ret = input->read(input, &res_create_buf,
                          sizeof(res_create_buf));
   if (ret != sizeof(res_create_buf)) {
      return -1;
   }

   args->handle = res_create_buf[VCMD_RES_CREATE2_RES_HANDLE];
   args->target = res_create_buf[VCMD_RES_CREATE2_TARGET];
   args->format = res_create_buf[VCMD_RES_CREATE2_FORMAT];
   args->bind = res_create_buf[VCMD_RES_CREATE2_BIND];

   args->width = res_create_buf[VCMD_RES_CREATE2_WIDTH];
   args->height = res_create_buf[VCMD_RES_CREATE2_HEIGHT];
   args->depth = res_create_buf[VCMD_RES_CREATE2_DEPTH];
   args->array_size = res_create_buf[VCMD_RES_CREATE2_ARRAY_SIZE];
   args->last_level = res_create_buf[VCMD_RES_CREATE2_LAST_LEVEL];
   args->nr_samples = res_create_buf[VCMD_RES_CREATE2_NR_SAMPLES];
   args->flags = 0;

   *shm_size = res_create_buf[VCMD_RES_CREATE2_DATA_SIZE];

   return 0;
}

static int vtest_create_resource_setup_shm(struct vtest_resource *res,
                                           size_t size)
{
   int fd;
   void *ptr;

   fd = vtest_new_shm(res->res_id, size);
   if (fd < 0)
      return report_failed_call("vtest_new_shm", fd);

   ptr = mmap(NULL, size, PROT_WRITE | PROT_READ, MAP_SHARED, fd, 0);
   if (ptr == MAP_FAILED) {
      close(fd);
      return -1;
   }

   res->iov.iov_base = ptr;
   res->iov.iov_len = size;

   return fd;
}

static int vtest_create_resource_internal(struct vtest_renderer *r,
                                          uint32_t cmd_id,
                                          struct virgl_renderer_resource_create_args *args,
                                          size_t shm_size)
{
   struct vtest_resource *res;
   int ret;

   /*if (ctx->protocol_version >= 3) {
      if (args->handle)
         return -EINVAL;
   } else {*/
      // Check that the handle doesn't already exist.
      if (util_hash_table_get(r->resource_table, intptr_to_pointer(args->handle))) {
         return -EEXIST;
      }
   //}

   res = vtest_new_resource(r, args->handle);
   if (!res)
      return -ENOMEM;
   args->handle = res->res_id;

   ret = virgl_renderer_resource_create(args, NULL, 0);
   if (ret) {
      vtest_unref_resource(res);
      return report_failed_call("virgl_renderer_resource_create", ret);
   }

   virgl_renderer_ctx_attach_resource(r->ctx_id, res->res_id);

   /*if (ctx->protocol_version >= 3) {
      uint32_t resp_buf[VTEST_HDR_SIZE + 1] = {
         [VTEST_CMD_LEN] = 1,
         [VTEST_CMD_ID] = cmd_id,
         [VTEST_CMD_DATA_START] = res->res_id,
      };
      ret = vtest_block_write(ctx->out_fd, resp_buf, sizeof(resp_buf));
      if (ret < 0) {
         vtest_unref_resource(res);
         return ret;
      }
   }*/

   /* no shm for v1 resources or v2 multi-sample resources */
   if (shm_size) {
      int fd;

      fd = vtest_create_resource_setup_shm(res, shm_size);
      if (fd < 0) {
         vtest_unref_resource(res);
         return -ENOMEM;
      }

      ret = vtest_send_fd(r->fd, fd);
      if (ret < 0) {
         close(fd);
         vtest_unref_resource(res);
         return report_failed_call("vtest_send_fd", ret);
      }

      /* Closing the file descriptor does not unmap the region. */
      close(fd);

      virgl_renderer_resource_attach_iov(res->res_id, &res->iov, 1);
   }

   util_hash_table_set(r->resource_table, intptr_to_pointer(res->res_id), res);

   return 0;
}

int vtest_create_resource(struct vtest_renderer *r, UNUSED uint32_t length_dw)
{
   //struct vtest_context *ctx = vtest_get_current_context();
   struct virgl_renderer_resource_create_args args;
   int ret;

   ret = vtest_create_resource_decode_args(r, &args);
   if (ret < 0) {
      return ret;
   }

   return vtest_create_resource_internal(r, VCMD_RESOURCE_CREATE, &args, 0);
}

int vtest_create_resource2(struct vtest_renderer *r, UNUSED uint32_t length_dw)
{
   //struct vtest_context *ctx = vtest_get_current_context();
   struct virgl_renderer_resource_create_args args;
   size_t shm_size;
   int ret;

   ret = vtest_create_resource_decode_args2(r, &args, &shm_size);
   if (ret < 0) {
      return ret;
   }

   return vtest_create_resource_internal(r, VCMD_RESOURCE_CREATE2, &args, shm_size);
}

int vtest_resource_create_blob(UNUSED uint32_t length_dw)
{
   struct vtest_context *ctx = vtest_get_current_context();
   uint32_t res_create_blob_buf[VCMD_RES_CREATE_BLOB_SIZE];
   uint32_t resp_buf[VTEST_HDR_SIZE + 1];
   struct virgl_renderer_resource_create_blob_args args;
   struct vtest_resource *res;
   int fd;
   int ret;

   ret = ctx->input->read(ctx->input, res_create_blob_buf,
                          sizeof(res_create_blob_buf));
   if (ret != sizeof(res_create_blob_buf))
      return -1;

   memset(&args, 0, sizeof(args));
   args.blob_mem = res_create_blob_buf[VCMD_RES_CREATE_BLOB_TYPE];
   args.blob_flags = res_create_blob_buf[VCMD_RES_CREATE_BLOB_FLAGS];
   args.size = res_create_blob_buf[VCMD_RES_CREATE_BLOB_SIZE_LO];
   args.size |= (uint64_t)res_create_blob_buf[VCMD_RES_CREATE_BLOB_SIZE_HI] << 32;
   args.blob_id = res_create_blob_buf[VCMD_RES_CREATE_BLOB_ID_LO];
   args.blob_id |= (uint64_t)res_create_blob_buf[VCMD_RES_CREATE_BLOB_ID_HI] << 32;

   res = vtest_new_resource(NULL, 0);
   if (!res)
      return -ENOMEM;

   args.res_handle = res->res_id;
   args.ctx_id = ctx->ctx_id;

   switch (args.blob_mem) {
   case VIRGL_RENDERER_BLOB_MEM_GUEST:
   case VIRGL_RENDERER_BLOB_MEM_HOST3D_GUEST:
      fd = vtest_create_resource_setup_shm(res, args.size);
      if (fd < 0) {
         vtest_unref_resource(res);
         return -ENOMEM;
      }

      args.iovecs = &res->iov;
      args.num_iovs = 1;
      break;
   case VIRGL_RENDERER_BLOB_MEM_HOST3D:
      fd = -1;
      break;
   default:
      return -EINVAL;
   }

   ret = virgl_renderer_resource_create_blob(&args);
   if (ret) {
      if (fd >= 0)
         close(fd);
      vtest_unref_resource(res);
      return report_failed_call("virgl_renderer_resource_create_blob", ret);
   }

   /* export blob */
   if (args.blob_mem == VIRGL_RENDERER_BLOB_MEM_HOST3D) {
      uint32_t fd_type;
      ret = virgl_renderer_resource_export_blob(res->res_id, &fd_type, &fd);
      if (ret) {
         vtest_unref_resource(res);
         return report_failed_call("virgl_renderer_resource_export_blob", ret);
      }
      if (fd_type != VIRGL_RENDERER_BLOB_FD_TYPE_DMABUF &&
          fd_type != VIRGL_RENDERER_BLOB_FD_TYPE_SHM) {
         close(fd);
         vtest_unref_resource(res);
         return report_failed_call("virgl_renderer_resource_export_blob", -EINVAL);
      }
   }

   virgl_renderer_ctx_attach_resource(ctx->ctx_id, res->res_id);

   resp_buf[VTEST_CMD_LEN] = 1;
   resp_buf[VTEST_CMD_ID] = VCMD_RESOURCE_CREATE_BLOB;
   resp_buf[VTEST_CMD_DATA_START] = res->res_id;
   ret = vtest_block_write(ctx->out_fd, resp_buf, sizeof(resp_buf));
   if (ret < 0) {
      close(fd);
      vtest_unref_resource(res);
      return ret;
   }

   ret = vtest_send_fd(ctx->out_fd, fd);
   if (ret < 0) {
      close(fd);
      vtest_unref_resource(res);
      return report_failed_call("vtest_send_fd", ret);
   }

   /* Closing the file descriptor does not unmap the region. */
   close(fd);

   util_hash_table_set(ctx->resource_table, intptr_to_pointer(res->res_id), res);

   return 0;
}

int vtest_resource_unref(struct vtest_renderer *r, UNUSED uint32_t length_dw)
{
   //struct vtest_context *ctx = vtest_get_current_context();
   uint32_t res_unref_buf[VCMD_RES_UNREF_SIZE];
   int ret;
   uint32_t handle;

   ret = input->read(input, &res_unref_buf,
                          sizeof(res_unref_buf));
   if (ret != sizeof(res_unref_buf)) {
      return -1;
   }

   handle = res_unref_buf[VCMD_RES_UNREF_RES_HANDLE];
   util_hash_table_remove(r->resource_table, intptr_to_pointer(handle));

   return 0;
}

int vtest_submit_cmd(struct vtest_renderer *r, uint32_t length_dw)
{
   //struct vtest_context *ctx = vtest_get_current_context();
   uint32_t *cbuf;
   int ret;

   if (length_dw > r->max_length / 4) {
      return -1;
   }

   cbuf = malloc(length_dw * 4);
   if (!cbuf) {
      return -1;
   }

   ret = input->read(input, cbuf, length_dw * 4);
   if (ret != (int)length_dw * 4) {
      free(cbuf);
      return -1;
   }

   ret = virgl_renderer_submit_cmd(cbuf, r->ctx_id, length_dw);

   free(cbuf);
   if (ret)
      return -1;

   vtest_create_implicit_fence(r);
   return 0;
}

struct vtest_transfer_args {
   uint32_t handle;
   uint32_t level;
   uint32_t stride;
   uint32_t layer_stride;
   struct virgl_box box;
   uint32_t offset;
};

static int vtest_transfer_decode_args(struct vtest_renderer *r,
                                      struct vtest_transfer_args *args,
                                      uint32_t *data_size)
{
   uint32_t thdr_buf[VCMD_TRANSFER_HDR_SIZE];
   int ret;

   ret = input->read(input, thdr_buf, sizeof(thdr_buf));
   if (ret != sizeof(thdr_buf)) {
      return -1;
   }

   args->handle = thdr_buf[VCMD_TRANSFER_RES_HANDLE];
   args->level = thdr_buf[VCMD_TRANSFER_LEVEL];
   args->stride = thdr_buf[VCMD_TRANSFER_STRIDE];
   args->layer_stride = thdr_buf[VCMD_TRANSFER_LAYER_STRIDE];
   args->box.x = thdr_buf[VCMD_TRANSFER_X];
   args->box.y = thdr_buf[VCMD_TRANSFER_Y];
   args->box.z = thdr_buf[VCMD_TRANSFER_Z];
   args->box.w = thdr_buf[VCMD_TRANSFER_WIDTH];
   args->box.h = thdr_buf[VCMD_TRANSFER_HEIGHT];
   args->box.d = thdr_buf[VCMD_TRANSFER_DEPTH];
   args->offset = 0;

   *data_size = thdr_buf[VCMD_TRANSFER_DATA_SIZE];

   if (*data_size > r->max_length) {
      return -ENOMEM;
   }

   return 0;
}

static int vtest_transfer_decode_args2(struct vtest_renderer *r,
                                       struct vtest_transfer_args *args)
{
   uint32_t thdr_buf[VCMD_TRANSFER2_HDR_SIZE];
   int ret;

   ret = input->read(input, thdr_buf, sizeof(thdr_buf));
   if (ret != sizeof(thdr_buf)) {
      return -1;
   }

   args->handle = thdr_buf[VCMD_TRANSFER2_RES_HANDLE];
   args->level = thdr_buf[VCMD_TRANSFER2_LEVEL];
   args->stride = 0;
   args->layer_stride = 0;
   args->box.x = thdr_buf[VCMD_TRANSFER2_X];
   args->box.y = thdr_buf[VCMD_TRANSFER2_Y];
   args->box.z = thdr_buf[VCMD_TRANSFER2_Z];
   args->box.w = thdr_buf[VCMD_TRANSFER2_WIDTH];
   args->box.h = thdr_buf[VCMD_TRANSFER2_HEIGHT];
   args->box.d = thdr_buf[VCMD_TRANSFER2_DEPTH];
   args->offset = thdr_buf[VCMD_TRANSFER2_OFFSET];

   return 0;
}

static int vtest_transfer_get_internal(struct vtest_renderer *r,
                                       struct vtest_transfer_args *args,
                                       uint32_t data_size,
                                       bool do_transfer)
{
   struct vtest_resource *res;
   struct iovec data_iov;
   int ret = 0;

   res = util_hash_table_get(r->resource_table,
                             intptr_to_pointer(args->handle));
   if (!res) {
      return report_failed_call("util_hash_table_get", -ESRCH);
   }

   if (data_size) {
      data_iov.iov_len = data_size;
      data_iov.iov_base = malloc(data_size);
      if (!data_iov.iov_base) {
         return -ENOMEM;
      }
   } else {
      if (args->offset >= res->iov.iov_len) {
         return report_failure("offset larger then length of backing store", -EFAULT);
      }
   }

   if (do_transfer) {
      ret = virgl_renderer_transfer_read_iov(res->res_id,
                                             r->ctx_id,
                                             args->level,
                                             args->stride,
                                             args->layer_stride,
                                             &args->box,
                                             args->offset,
                                             data_size ? &data_iov : NULL,
                                             data_size ? 1 : 0);
      if (ret) {
         report_failed_call("virgl_renderer_transfer_read_iov", ret);
      }
   } else if (data_size) {
      memset(data_iov.iov_base, 0, data_iov.iov_len);
   }

   if (data_size) {
      ret = vtest_block_write(r, data_iov.iov_base, data_iov.iov_len);
      if (ret > 0)
         ret = 0;

      free(data_iov.iov_base);
   }

   return ret;
}

static int vtest_transfer_put_internal(struct vtest_renderer *r,
                                       struct vtest_transfer_args *args,
                                       uint32_t data_size,
                                       bool do_transfer)
{
   struct vtest_resource *res;
   struct iovec data_iov;
   int ret = 0;

   res = util_hash_table_get(r->resource_table,
                             intptr_to_pointer(args->handle));
   if (!res) {
      return report_failed_call("util_hash_table_get", -ESRCH);
   }

   if (data_size) {
      data_iov.iov_len = data_size;
      data_iov.iov_base = malloc(data_size);
      if (!data_iov.iov_base) {
         return -ENOMEM;
      }

      ret = input->read(input, data_iov.iov_base, data_iov.iov_len);
      if (ret < 0) {
         return ret;
      }
   }

   if (do_transfer) {
      ret = virgl_renderer_transfer_write_iov(res->res_id,
                                              r->ctx_id,
                                              args->level,
                                              args->stride,
                                              args->layer_stride,
                                              &args->box,
                                              args->offset,
                                              data_size ? &data_iov : NULL,
                                              data_size ? 1 : 0);
      if (ret) {
         report_failed_call("virgl_renderer_transfer_write_iov", ret);
      }
   }

   if (data_size) {
      free(data_iov.iov_base);
   }

   return ret;
}

int vtest_transfer_get(struct vtest_renderer *r, UNUSED uint32_t length_dw)
{
   //struct vtest_context *ctx = vtest_get_current_context();
   int ret;
   struct vtest_transfer_args args;
   uint32_t data_size;

   ret = vtest_transfer_decode_args(r, &args, &data_size);
   if (ret < 0) {
      return ret;
   }

   return vtest_transfer_get_internal(r, &args, data_size, true);
}

int vtest_transfer_get_nop(UNUSED uint32_t length_dw)
{
   struct vtest_context *ctx = vtest_get_current_context();
   int ret;
   struct vtest_transfer_args args;
   uint32_t data_size;

   ret = vtest_transfer_decode_args(ctx, &args, &data_size);
   if (ret < 0) {
      return ret;
   }

   return vtest_transfer_get_internal(ctx, &args, data_size, false);
}

int vtest_transfer_put(struct vtest_renderer *r, UNUSED uint32_t length_dw)
{
   //struct vtest_context *ctx = vtest_get_current_context();
   int ret;
   struct vtest_transfer_args args;
   uint32_t data_size;

   ret = vtest_transfer_decode_args(r, &args, &data_size);
   if (ret < 0) {
      return ret;
   }

   return vtest_transfer_put_internal(r, &args, data_size, true);
}

int vtest_transfer_put_nop(UNUSED uint32_t length_dw)
{
   struct vtest_context *ctx = vtest_get_current_context();
   int ret;
   struct vtest_transfer_args args;
   uint32_t data_size;

   ret = vtest_transfer_decode_args(ctx, &args, &data_size);
   if (ret < 0) {
      return ret;
   }

   return vtest_transfer_put_internal(ctx, &args, data_size, false);
}

int vtest_transfer_get2(struct vtest_renderer *r, UNUSED uint32_t length_dw)
{
   //struct vtest_context *ctx = vtest_get_current_context();
   int ret;
   struct vtest_transfer_args args;

   ret = vtest_transfer_decode_args2(r, &args);
   if (ret < 0) {
      return ret;
   }

   return vtest_transfer_get_internal(r, &args, 0, true);
}

int vtest_transfer_get2_nop(UNUSED uint32_t length_dw)
{
   struct vtest_context *ctx = vtest_get_current_context();
   int ret;
   struct vtest_transfer_args args;

   ret = vtest_transfer_decode_args2(ctx, &args);
   if (ret < 0) {
      return ret;
   }

   return vtest_transfer_get_internal(ctx, &args, 0, false);
}

int vtest_transfer_put2(struct vtest_renderer *r, UNUSED uint32_t length_dw)
{
   //struct vtest_context *ctx = vtest_get_current_context();
   int ret;
   struct vtest_transfer_args args;

   ret = vtest_transfer_decode_args2(r, &args);
   if (ret < 0) {
      return ret;
   }

   return vtest_transfer_put_internal(r, &args, 0, true);
}

int vtest_transfer_put2_nop(UNUSED uint32_t length_dw)
{
   struct vtest_context *ctx = vtest_get_current_context();
   int ret;
   struct vtest_transfer_args args;

   ret = vtest_transfer_decode_args2(ctx, &args);
   if (ret < 0) {
      return ret;
   }

   return vtest_transfer_put_internal(ctx, &args, 0, false);
}

int vtest_resource_busy_wait(struct vtest_renderer *r, UNUSED uint32_t length_dw)
{
   //struct vtest_context *ctx = vtest_get_current_context();
   uint32_t bw_buf[VCMD_BUSY_WAIT_SIZE];
   int ret, fd;
   int flags0;
   uint32_t hdr_buf[VTEST_HDR_SIZE];
   uint32_t reply_buf[1];
   bool busy = false;

   ret = input->read(input, &bw_buf, sizeof(bw_buf));
   if (ret != sizeof(bw_buf)) {
      return -1;
   }

   /* clients often send VCMD_PING_PROTOCOL_VERSION followed by
    * VCMD_RESOURCE_BUSY_WAIT with handle 0 to figure out if
    * VCMD_PING_PROTOCOL_VERSION is supported.  We need to make a special case
    * for that.
    */

   //if (!ctx->context_initialized && bw_buf[VCMD_BUSY_WAIT_HANDLE])
      //return -1;

   /*  handle = bw_buf[VCMD_BUSY_WAIT_HANDLE]; unused as of now */
   flags0 = bw_buf[VCMD_BUSY_WAIT_FLAGS];

   do {
      busy = r->implicit_fence_completed !=
             r->implicit_fence_submitted;
      if (!busy || !(flags0 & VCMD_BUSY_WAIT_FLAG_WAIT))
         break;

      /* TODO this is bad when there are multiple clients */
      fd = virgl_renderer_get_poll_fd();
      if (fd != -1) {
         vtest_wait_for_fd_read(fd);
      }
      virgl_renderer_poll();
   } while (true);

   hdr_buf[VTEST_CMD_LEN] = 1;
   hdr_buf[VTEST_CMD_ID] = VCMD_RESOURCE_BUSY_WAIT;
   reply_buf[0] = busy ? 1 : 0;

   ret = vtest_block_write(r, hdr_buf, sizeof(hdr_buf));
   if (ret < 0) {
      return ret;
   }

   ret = vtest_block_write(r, reply_buf, sizeof(reply_buf));
   if (ret < 0) {
      return ret;
   }

   return 0;
}

int vtest_resource_busy_wait_nop(UNUSED uint32_t length_dw)
{
   struct vtest_context *ctx = vtest_get_current_context();
   uint32_t bw_buf[VCMD_BUSY_WAIT_SIZE];
   uint32_t reply_buf[VTEST_HDR_SIZE + 1];
   int ret;

   ret = ctx->input->read(ctx->input, &bw_buf, sizeof(bw_buf));
   if (ret != sizeof(bw_buf)) {
      return -1;
   }

   reply_buf[VTEST_CMD_LEN] = 1;
   reply_buf[VTEST_CMD_ID] = VCMD_RESOURCE_BUSY_WAIT;
   reply_buf[VTEST_CMD_DATA_START] = 0;

   ret = vtest_block_write(ctx->out_fd, reply_buf, sizeof(reply_buf));
   if (ret < 0) {
      return ret;
   }

   return 0;
}

void vtest_poll_resource_busy_wait(void)
{
   /* poll the implicit fences */
   virgl_renderer_poll();
}

static uint64_t vtest_gettime(uint32_t offset_ms)
{
   const uint64_t ns_per_ms = 1000000;
   const uint64_t ns_per_s = ns_per_ms * 1000;
   struct timespec ts;
   uint64_t ns;

   if (offset_ms > INT32_MAX)
      return UINT64_MAX;

   clock_gettime(CLOCK_MONOTONIC, &ts);
   ns = ns_per_s * ts.tv_sec + ts.tv_nsec;

   return ns + ns_per_ms * offset_ms;
}

/* TODO this is slow */
static void vtest_signal_sync(struct vtest_sync *sync, uint64_t value)
{
   struct vtest_context *ctx;
   uint64_t now;

   if (sync->value >= value) {
      sync->value = value;
      return;
   }
   sync->value = value;

   now = vtest_gettime(0);

   LIST_FOR_EACH_ENTRY(ctx, &renderer.active_contexts, head) {
      struct vtest_sync_wait *wait, *tmp;
      LIST_FOR_EACH_ENTRY_SAFE(wait, tmp, &ctx->sync_waits, head) {
         bool is_ready = false;
         uint32_t i;

         /* garbage collect */
         if (wait->valid_before < now) {
            list_del(&wait->head);
            vtest_free_sync_wait(wait);
            continue;
         }

         for (i = 0; i < wait->count; i++) {
            if (wait->syncs[i] != sync || wait->values[i] > value)
               continue;

            vtest_unref_sync(wait->syncs[i]);
            wait->syncs[i] = NULL;

            wait->signaled_count++;
            if (wait->signaled_count == wait->count ||
                (wait->flags & VCMD_SYNC_WAIT_FLAG_ANY)) {
               is_ready = true;
               break;
            }
         }

         if (is_ready) {
            const uint64_t val = 1;

            list_del(&wait->head);
            write(wait->fd, &val, sizeof(val));
            vtest_free_sync_wait(wait);
         }
      }
   }
}

static void vtest_signal_sync_queue(struct vtest_sync_queue *queue,
                                    struct vtest_sync_queue_submit *to_submit)
{
   struct vtest_sync_queue_submit *submit, *tmp;

   LIST_FOR_EACH_ENTRY_SAFE(submit, tmp, &queue->submits, head) {
      uint32_t i;

      list_del(&submit->head);

      for (i = 0; i < submit->count; i++) {
         vtest_signal_sync(submit->syncs[i], submit->values[i]);
         vtest_unref_sync(submit->syncs[i]);
      }
      free(submit);

      if (submit == to_submit)
         break;
   }
}

int vtest_sync_create(UNUSED uint32_t length_dw)
{
   struct vtest_context *ctx = vtest_get_current_context();
   uint32_t sync_create_buf[VCMD_SYNC_CREATE_SIZE];
   uint32_t resp_buf[VTEST_HDR_SIZE + 1];
   uint64_t value;
   struct vtest_sync *sync;
   int ret;

   ret = ctx->input->read(ctx->input, sync_create_buf, sizeof(sync_create_buf));
   if (ret != sizeof(sync_create_buf))
      return -1;

   value = sync_create_buf[VCMD_SYNC_CREATE_VALUE_LO];
   value |= (uint64_t)sync_create_buf[VCMD_SYNC_CREATE_VALUE_HI] << 32;

   sync = vtest_new_sync(value);
   if (!sync)
      return -ENOMEM;

   resp_buf[VTEST_CMD_LEN] = 1;
   resp_buf[VTEST_CMD_ID] = VCMD_SYNC_CREATE;
   resp_buf[VTEST_CMD_DATA_START] = sync->sync_id;
   ret = vtest_block_write(ctx->out_fd, resp_buf, sizeof(resp_buf));
   if (ret < 0) {
      vtest_unref_sync(sync);
      return ret;
   }

   util_hash_table_set(ctx->sync_table, intptr_to_pointer(sync->sync_id), sync);

   return 0;
}

int vtest_sync_unref(UNUSED uint32_t length_dw)
{
   struct vtest_context *ctx = vtest_get_current_context();
   uint32_t sync_unref_buf[VCMD_SYNC_UNREF_SIZE];
   uint32_t sync_id;
   int ret;

   ret = ctx->input->read(ctx->input, &sync_unref_buf,
                          sizeof(sync_unref_buf));
   if (ret != sizeof(sync_unref_buf)) {
      return -1;
   }

   sync_id = sync_unref_buf[VCMD_SYNC_UNREF_ID];
   util_hash_table_remove(ctx->sync_table, intptr_to_pointer(sync_id));

   return 0;
}

int vtest_sync_read(UNUSED uint32_t length_dw)
{
   struct vtest_context *ctx = vtest_get_current_context();
   uint32_t sync_read_buf[VCMD_SYNC_READ_SIZE];
   uint32_t resp_buf[VTEST_HDR_SIZE + 2];
   uint32_t sync_id;
   struct vtest_sync *sync;
   int ret;

   ret = ctx->input->read(ctx->input, &sync_read_buf,
                          sizeof(sync_read_buf));
   if (ret != sizeof(sync_read_buf)) {
      return -1;
   }

   sync_id = sync_read_buf[VCMD_SYNC_READ_ID];

   sync = util_hash_table_get(ctx->sync_table, intptr_to_pointer(sync_id));
   if (!sync)
      return -EEXIST;

   resp_buf[VTEST_CMD_LEN] = 2;
   resp_buf[VTEST_CMD_ID] = VCMD_SYNC_READ;
   resp_buf[VTEST_CMD_DATA_START] = (uint32_t)sync->value;
   resp_buf[VTEST_CMD_DATA_START + 1] = (uint32_t)(sync->value >> 32);

   ret = vtest_block_write(ctx->out_fd, resp_buf, sizeof(resp_buf));
   if (ret < 0)
      return ret;

   return 0;
}

static uint32_t vtest_sync_decode_id_and_value(const uint32_t *data,
                                               uint32_t index,
                                               uint64_t *value)
{
   data += index * 3;

   /* 32-bit sync id followed by 64-bit sync value */
   *value = (uint64_t)data[1];
   *value |= (uint64_t)data[2] << 32;
   return data[0];
}

int vtest_sync_write(UNUSED uint32_t length_dw)
{
   struct vtest_context *ctx = vtest_get_current_context();
   uint32_t sync_write_buf[VCMD_SYNC_WRITE_SIZE];
   uint32_t sync_id;
   uint64_t value;
   struct vtest_sync *sync;
   int ret;

   ret = ctx->input->read(ctx->input, &sync_write_buf,
                          sizeof(sync_write_buf));
   if (ret != sizeof(sync_write_buf)) {
      return -1;
   }

   sync_id = vtest_sync_decode_id_and_value(sync_write_buf, 0, &value);

   sync = util_hash_table_get(ctx->sync_table, intptr_to_pointer(sync_id));
   if (!sync)
      return -EEXIST;

   vtest_signal_sync(sync, value);

   return 0;
}

static int vtest_sync_wait_init(struct vtest_sync_wait *wait,
                                struct vtest_context *ctx,
                                uint32_t flags,
                                uint32_t timeout,
                                const uint32_t *syncs,
                                uint32_t sync_count)
{
   uint32_t i;

#ifdef HAVE_EVENTFD_H
   wait->fd = eventfd(0, EFD_CLOEXEC | EFD_NONBLOCK);
#else
   /* TODO pipe */
   wait->fd = -1;
#endif
   if (wait->fd < 0)
      return -ENODEV;

   wait->flags = flags;
   wait->valid_before = vtest_gettime(timeout);

   wait->count = 0;
   wait->signaled_count = 0;
   for (i = 0; i < sync_count; i++) {
      struct vtest_sync *sync;
      uint32_t sync_id;
      uint64_t value;

      sync_id = vtest_sync_decode_id_and_value(syncs, i, &value);

      sync = util_hash_table_get(ctx->sync_table, intptr_to_pointer(sync_id));
      if (!sync)
         break;

      /* skip signaled */
      if (sync->value < value) {
         wait->syncs[wait->count] = vtest_ref_sync(sync);
         wait->values[wait->count] = value;
         wait->count++;
      }
   }

   if (i < sync_count) {
      vtest_free_sync_wait(wait);
      return -EEXIST;
   }

   return 0;
}

int vtest_sync_wait(uint32_t length_dw)
{
   struct vtest_context *ctx = vtest_get_current_context();
   uint32_t resp_buf[VTEST_HDR_SIZE];
   uint32_t sync_count;
   uint32_t *sync_wait_buf;
   uint32_t flags;
   uint32_t timeout;
   struct vtest_sync_wait *wait;
   bool is_ready;
   int ret;

   if (length_dw > renderer.max_length / 4)
      return -EINVAL;

   if ((length_dw - 2) % 3)
      return -EINVAL;
   sync_count = (length_dw - 2) / 3;

   sync_wait_buf = malloc(length_dw * 4);
   if (!sync_wait_buf)
      return -ENOMEM;

   ret = ctx->input->read(ctx->input, sync_wait_buf, length_dw * 4);
   if (ret != (int)length_dw * 4) {
      free(sync_wait_buf);
      return -1;
   }

   flags = sync_wait_buf[VCMD_SYNC_WAIT_FLAGS];
   timeout = sync_wait_buf[VCMD_SYNC_WAIT_TIMEOUT];

   wait = malloc(sizeof(*wait) +
                 sizeof(*wait->syncs) * sync_count +
                 sizeof(*wait->values) * sync_count);
   if (!wait) {
      free(sync_wait_buf);
      return -ENOMEM;
   }
   wait->syncs = (void *)&wait[1];
   wait->values = (void *)&wait->syncs[sync_count];

   ret = vtest_sync_wait_init(wait, ctx, flags, timeout,
         sync_wait_buf + 2, sync_count);
   free(sync_wait_buf);

   if (ret) {
      free(wait);
      return ret;
   }

   is_ready = !wait->count;
   if ((wait->flags & VCMD_SYNC_WAIT_FLAG_ANY) && wait->count < sync_count)
      is_ready = true;

   if (is_ready) {
      const uint64_t val = 1;
      write(wait->fd, &val, sizeof(val));
   }

   resp_buf[VTEST_CMD_LEN] = 0;
   resp_buf[VTEST_CMD_ID] = VCMD_SYNC_WAIT;
   ret = vtest_block_write(ctx->out_fd, resp_buf, sizeof(resp_buf));
   if (ret >= 0)
      ret = vtest_send_fd(ctx->out_fd, wait->fd);

   if (ret || is_ready || !timeout)
      vtest_free_sync_wait(wait);
   else
      list_addtail(&wait->head, &ctx->sync_waits);

   return ret;
}

static int vtest_submit_cmd2_batch(struct vtest_context *ctx,
                                   const struct vcmd_submit_cmd2_batch *batch,
                                   const uint32_t *cmds,
                                   const uint32_t *syncs)
{
   struct vtest_sync_queue_submit *submit = NULL;
   uint32_t i;
   int ret;

   ret = virgl_renderer_submit_cmd((void *)cmds, ctx->ctx_id, batch->cmd_size);
   if (ret)
      return -EINVAL;

   if (!batch->sync_count)
      return 0;

   if (batch->flags & VCMD_SUBMIT_CMD2_FLAG_SYNC_QUEUE) {
      submit = malloc(sizeof(*submit) +
                      sizeof(*submit->syncs) * batch->sync_count +
                      sizeof(*submit->values) * batch->sync_count);
      if (!submit)
         return -ENOMEM;

      submit->count = batch->sync_count;
      submit->syncs = (void *)&submit[1];
      submit->values = (void *)&submit->syncs[batch->sync_count];
   }

   for (i = 0; i < batch->sync_count; i++) {
      struct vtest_sync *sync;
      uint32_t sync_id;
      uint64_t value;

      sync_id = vtest_sync_decode_id_and_value(syncs, i, &value);

      sync = util_hash_table_get(ctx->sync_table, intptr_to_pointer(sync_id));
      if (!sync)
         break;

      if (submit) {
         submit->syncs[i] = vtest_ref_sync(sync);
         submit->values[i] = value;
      } else {
         vtest_signal_sync(sync, value);
      }
   }

   if (i < batch->sync_count) {
      if (submit) {
         submit->count = i;
         vtest_free_sync_queue_submit(submit);
      }
      return -EEXIST;
   }

   if (submit) {
      struct vtest_sync_queue *queue = &ctx->sync_queues[batch->sync_queue_index];

      submit->sync_queue = queue;
      ret = virgl_renderer_context_create_fence(ctx->ctx_id,
                                                VIRGL_RENDERER_FENCE_FLAG_MERGEABLE,
                                                batch->sync_queue_id,
                                                //submit);
                                                (uintptr_t)submit);
      if (ret) {
         vtest_free_sync_queue_submit(submit);
         return ret;
      }

      list_addtail(&submit->head, &queue->submits);
   }

   return 0;
}

int vtest_submit_cmd2(uint32_t length_dw)
{
   struct vtest_context *ctx = vtest_get_current_context();
   uint32_t *submit_cmd2_buf;
   uint32_t batch_count;
   uint32_t i;
   int ret;

   if (length_dw > renderer.max_length / 4)
      return -EINVAL;

   submit_cmd2_buf = malloc(length_dw * 4);
   if (!submit_cmd2_buf)
      return -ENOMEM;

   ret = ctx->input->read(ctx->input, submit_cmd2_buf, length_dw * 4);
   if (ret != (int)length_dw * 4) {
      free(submit_cmd2_buf);
      return -1;
   }

   batch_count = submit_cmd2_buf[VCMD_SUBMIT_CMD2_BATCH_COUNT];
   if (VCMD_SUBMIT_CMD2_BATCH_COUNT + 8 * batch_count > length_dw) {
      free(submit_cmd2_buf);
      return -EINVAL;
   }

   for (i = 0; i < batch_count; i++) {
      const struct vcmd_submit_cmd2_batch batch = {
         .flags = submit_cmd2_buf[VCMD_SUBMIT_CMD2_BATCH_FLAGS(i)],
         .cmd_offset = submit_cmd2_buf[VCMD_SUBMIT_CMD2_BATCH_CMD_OFFSET(i)],
         .cmd_size = submit_cmd2_buf[VCMD_SUBMIT_CMD2_BATCH_CMD_SIZE(i)],
         .sync_offset = submit_cmd2_buf[VCMD_SUBMIT_CMD2_BATCH_SYNC_OFFSET(i)],
         .sync_count = submit_cmd2_buf[VCMD_SUBMIT_CMD2_BATCH_SYNC_COUNT(i)],
         .sync_queue_index = submit_cmd2_buf[VCMD_SUBMIT_CMD2_BATCH_SYNC_QUEUE_INDEX(i)],
         .sync_queue_id = submit_cmd2_buf[VCMD_SUBMIT_CMD2_BATCH_SYNC_QUEUE_ID_LO(i)] |
                          (uint64_t)submit_cmd2_buf[VCMD_SUBMIT_CMD2_BATCH_SYNC_QUEUE_ID_HI(i)] << 32,
      };
      const uint32_t *cmds = &submit_cmd2_buf[batch.cmd_offset];
      const uint32_t *syncs = &submit_cmd2_buf[batch.sync_offset];

      if (batch.cmd_offset + batch.cmd_size > length_dw ||
          batch.sync_offset + batch.sync_count * 3 > length_dw ||
          batch.sync_queue_index >= VTEST_MAX_SYNC_QUEUE_COUNT) {
         free(submit_cmd2_buf);
         return -EINVAL;
      }

      ret = vtest_submit_cmd2_batch(ctx, &batch, cmds, syncs);
      if (ret) {
         free(submit_cmd2_buf);
         return ret;
      }
   }

   free(submit_cmd2_buf);

   return 0;
}

void vtest_set_max_length(uint32_t length)
{
   renderer.max_length = length;
}

//TODO: EGL

static bool vtest_egl_init(struct vtest_renderer *d, bool surfaceless, bool gles)
{
   static EGLint conf_att[] = {
           EGL_SURFACE_TYPE, EGL_WINDOW_BIT,
           EGL_RENDERABLE_TYPE, EGL_OPENGL_BIT,
           EGL_RED_SIZE, 1,
           EGL_GREEN_SIZE, 1,
           EGL_BLUE_SIZE, 1,
           EGL_ALPHA_SIZE, 0,
           EGL_NONE,
   };
   static const EGLint ctx_att[] = {
           EGL_CONTEXT_CLIENT_VERSION, 2,
           EGL_NONE
   };
   EGLBoolean b;
   EGLenum api;
   EGLint major, minor, n;
   const char *extension_list;

   if (gles)
      conf_att[3] = EGL_OPENGL_ES_BIT;

   if (surfaceless) {
      conf_att[1] = EGL_PBUFFER_BIT;
   }

   const char *client_extensions = eglQueryString (NULL, EGL_EXTENSIONS);

   d->egl_display =  eglGetDisplay(EGL_DEFAULT_DISPLAY);

   if (!d->egl_display)
      goto fail;

   b = eglInitialize(d->egl_display, &major, &minor);
   if (!b)
      goto fail;

   extension_list = eglQueryString(d->egl_display, EGL_EXTENSIONS);

   printf("EGL major/minor: %d.%d\n", major, minor);
   printf("EGL version: %s\n", eglQueryString(d->egl_display, EGL_VERSION));
   printf("EGL vendor: %s\n", eglQueryString(d->egl_display, EGL_VENDOR));
   printf("EGL extensions: %s\n", extension_list);

   if (gles)
      api = EGL_OPENGL_ES_API;
   else
      api = EGL_OPENGL_API;
   b = eglBindAPI(api);
   if (!b)
      goto fail;

   b = eglChooseConfig(d->egl_display, conf_att, &d->egl_conf,
                       1, &n);

   if (!b || n != 1)
      goto fail;

   d->egl_ctx = eglCreateContext(d->egl_display,
                                 d->egl_conf,
                                 EGL_NO_CONTEXT,
                                 ctx_att);
   if (!d->egl_ctx)
      goto fail;

   static EGLint const window_attribute_list[] = {
           EGL_RENDER_BUFFER, EGL_BACK_BUFFER,
           EGL_NONE,
   };

   struct vtest_renderer *r = d;
   jobject surf = (*r->jni.env)->CallStaticObjectMethod(r->jni.env, r->jni.cls, r->jni.get_surface,(*r->jni.env)->CallStaticObjectMethod(r->jni.env, r->jni.cls, r->jni.create, 0, 0, 0, 0));
   if(surf == 0)exit(0);
   ANativeWindow *window = ANativeWindow_fromSurface(r->jni.env, surf);
   int format;
   eglGetConfigAttrib(d->egl_display, d->egl_conf, EGL_NATIVE_VISUAL_ID, &format);
   ANativeWindow_setBuffersGeometry(window, 0, 0, format);
   d->egl_fake_surf = eglCreateWindowSurface(d->egl_display, d->egl_conf, window, 0);

   eglMakeCurrent(d->egl_display, d->egl_fake_surf, d->egl_fake_surf, d->egl_ctx);
   return true;
    fail:
   return false;
}

static virgl_renderer_gl_context vtest_egl_create_context(void *cookie, int scanout_idx, struct virgl_renderer_gl_ctx_param *param)
{
   struct vtest_renderer *ve = cookie;
   EGLContext eglctx;
   EGLint ctx_att[] = {
           EGL_CONTEXT_CLIENT_VERSION, param->major_ver,
           EGL_CONTEXT_MINOR_VERSION_KHR, param->minor_ver,
           EGL_NONE
   };


   eglctx = eglCreateContext(ve->egl_display,
                             ve->egl_conf,
                             param->shared ? eglGetCurrentContext() :
                             ve->egl_ctx,
                             ctx_att);

   //printf("create_context %d %d %d %d %x\n", scanout_idx, param->shared, param->major_ver, param->minor_ver, eglctx);

   return (virgl_renderer_gl_context)eglctx;
}

static void vtest_egl_destroy_context(void *cookie, virgl_renderer_gl_context ctx)
{
   struct vtest_renderer *ve = cookie;
   EGLContext eglctx = (EGLContext)ctx;

   //printf("destroy_context %x\n", ctx);

   eglDestroyContext(ve->egl_display, eglctx);
}

static int vtest_egl_make_context_current(void *cookie, int scanout_idx, virgl_renderer_gl_context ctx)
{
   struct vtest_renderer *ve = cookie;
   EGLContext eglctx = (EGLContext)ctx;
   if( ctx == ve->egl_ctx )
      return eglMakeCurrent(ve->egl_display, ve->egl_fake_surf ,ve->egl_fake_surf, eglctx);
   else
      return eglMakeCurrent(ve->egl_display, EGL_NO_SURFACE, EGL_NO_SURFACE, eglctx);

}

struct virgl_renderer_callbacks vtest_cbs = {
        .version = VIRGL_RENDERER_CALLBACKS_VERSION,
        .write_fence = vtest_write_implicit_fence,
        .create_gl_context = vtest_egl_create_context,
        .destroy_gl_context = vtest_egl_destroy_context,
        .make_current = vtest_egl_make_context_current,
};

//TODO: DT

static void vtest_dt_destroy(struct vtest_renderer *r, struct dt_record *dt)
{
#ifdef ANDROID_JNI
   if( dt->java_surf )
      (*r->jni.env)->CallStaticVoidMethod(r->jni.env, r->jni.cls, r->jni.destroy, dt->java_surf);
   dt->java_surf = 0;
#endif

   if( dt->egl_surf )
   {
      eglMakeCurrent( r->egl_display, r->egl_fake_surf, r->egl_fake_surf, r->egl_ctx);
      eglDestroySurface(r->egl_display, dt->egl_surf);
      dt->egl_surf = 0;
   }
}

static void vtest_dt_flush(struct vtest_renderer *r, struct dt_record *dt, int handle, int x, int y, int w, int h)
{
   eglMakeCurrent(r->egl_display, dt->egl_surf, dt->egl_surf, r->egl_ctx);

   if(!dt->fb_id)
      glGenFramebuffersEXT(1,&dt->fb_id);

   glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, dt->fb_id );

   // use internal API here to get texture id
   if( handle != dt->res_id)
   {
      struct vrend_resource *res;

      res = vrend_renderer_ctx_res_lookup(overlay_ctx, handle);

      glFramebufferTexture2DEXT(GL_FRAMEBUFFER_EXT, GL_COLOR_ATTACHMENT0_EXT, GL_TEXTURE_2D, res->id, 0);

      dt->res_id = handle;
   }

   glReadBuffer(GL_COLOR_ATTACHMENT0_EXT);

   glBindFramebufferEXT(GL_DRAW_FRAMEBUFFER_EXT, 0);
   glBlitFramebuffer(x,y+h,w+x,y,x,y,w+x,h+y,GL_COLOR_BUFFER_BIT,GL_NEAREST);

   eglSwapBuffers(r->egl_display, dt->egl_surf);
}

static void vtest_dt_set_rect(struct vtest_renderer *r, struct dt_record *dt, int visible, int x, int y, int w, int h)
{
#ifdef ANDROID_JNI
   (*r->jni.env)->CallStaticVoidMethod(r->jni.env, r->jni.cls, r->jni.set_rect, dt->java_surf,x,y,w,h,visible);
#endif
}

static void vtest_dt_create(struct vtest_renderer *r, struct dt_record *dt, int drawable, int x, int y, int w, int h)
{
#ifdef ANDROID_JNI
   if(!dt->egl_surf)
   {
      dt->java_surf = (*r->jni.env)->CallStaticObjectMethod(r->jni.env, r->jni.cls, r->jni.create, x, y, w, h);
      jobject surf = (*r->jni.env)->CallStaticObjectMethod(r->jni.env, r->jni.cls, r->jni.get_surface, dt->java_surf);
      ANativeWindow *window = ANativeWindow_fromSurface(r->jni.env, surf);
      int format;
      eglGetConfigAttrib(r->egl_display, r->egl_conf, EGL_NATIVE_VISUAL_ID, &format);
      ANativeWindow_setBuffersGeometry(window, 0, 0, format);
      dt->egl_surf = eglCreateWindowSurface(r->egl_display, r->egl_conf, window, 0);
      //r->egl_drawable_surf = r->egl_fake_surf;
   }
#endif
}

static int vtest_dt_cmd(struct vtest_renderer *r)
{
   uint32_t flush_buf[VCMD_DT_SIZE];
   int ret;
   uint32_t cmd, x, y, w, h, handle;
   uint32_t drawable, id;

   ret = input->read(input, &flush_buf, sizeof(flush_buf));
   if (ret != sizeof(flush_buf))
      return -1;

   //EGLContext ctx = eglGetCurrentContext();

   drawable = flush_buf[VCMD_DT_DRAWABLE];
   x = flush_buf[VCMD_DT_X];
   y = flush_buf[VCMD_DT_Y];
   w = flush_buf[VCMD_DT_WIDTH];
   h = flush_buf[VCMD_DT_HEIGHT];
   id = flush_buf[VCMD_DT_ID];
   cmd = flush_buf[VCMD_DT_CMD];
   handle = flush_buf[VCMD_DT_HANDLE];

   //printf("dt_cmd %d %d %d %d %d %d %d %d\n", cmd, x, y, w, h, id, handle, drawable);

   struct dt_record *dt = &r->dts[id];

   if( cmd == VCMD_DT_CMD_CREATE )
      vtest_dt_create(r, dt, drawable, x, y, w, h);
   else if(cmd == VCMD_DT_CMD_DESTROY)
      vtest_dt_destroy(r, dt);
   else if(cmd == VCMD_DT_CMD_SET_RECT)
      vtest_dt_set_rect(r, dt, drawable, x, y, w, h);
   if( cmd == VCMD_DT_CMD_FLUSH )
      vtest_dt_flush(r, dt, handle, x, y, w, h);
   return 0;
}

//TODO: RENDERER

static int vtest_create_renderer(struct vtest_renderer *r, uint32_t length)
{
   char *vtestname;
   int ret;
   int ctx = 0;

   if (getenv("VTEST_USE_EGL_SURFACELESS")) {
      if (r->flags & FL_GLX) {
         fprintf(stderr, "Cannot use surfaceless with GLX.\n");
         return -1;
      }
      ctx |= VIRGL_RENDERER_USE_SURFACELESS;
   }

   if (r->flags & FL_GLES) {
      if (r->flags & FL_GLX) {
         fprintf(stderr, "Cannot use GLES with GLX.\n");
         return -1;
      }
      ctx |= VIRGL_RENDERER_USE_GLES;
   }

   if( !(r->flags & FL_GLX) )
      vtest_egl_init(r, false, (ctx & VIRGL_RENDERER_USE_GLES) != 0);

   ret = virgl_renderer_init(r, ctx | VIRGL_RENDERER_THREAD_SYNC, &vtest_cbs);
   if (ret) {
      fprintf(stderr, "failed to initialise renderer.\n");
      return -1;
   }

   vtestname = calloc(1, length + 1);
   if (!vtestname)
      return -1;

   ret = input->read(input, vtestname, length);
   if (ret != (int)length) {
      ret = -1;
      goto end;
   }

   ret = virgl_renderer_context_create(r->ctx_id, strlen(vtestname), vtestname);

    end:
   free(vtestname);
   return ret;
}

static void vtest_destroy_renderer(struct vtest_renderer *r)
{
   int i;

   for( i = 0; i < 32; i++)
      vtest_dt_destroy(r, &r->dts[i]);

   virgl_renderer_context_destroy(r->ctx_id);
   virgl_renderer_cleanup(r);
}

void *create_renderer(int in_fd, int ctx_id)
{
   struct vtest_renderer *r = calloc(1, sizeof(struct vtest_renderer));

   r->ctx_id = ctx_id;
   r->fd = in_fd;

   r->max_length = UINT_MAX;
   r->next_resource_id = 1;
   list_inithead(&r->free_resources);
   r->resource_table = util_hash_table_create(u32_hash_func,
                                              u32_compare_func,
                                              resource_destroy_func);

   input = calloc(1, sizeof(struct vtest_input));

   input->data.fd = in_fd;
   input->read = vtest_block_read;

   return r;
}

int renderer_loop(void *d)
{
   int ret;
   uint32_t header[VTEST_HDR_SIZE];
   bool inited = false;
   struct vtest_renderer *r = d;
   EGLContext ctx = 0;
   EGLSurface surf = 0;

again:
   ret = vtest_wait_for_fd_read(r->fd);
   if (ret < 0)
      goto fail;

   ret = input->read(input, &header, sizeof(header));

   if (ret == 8) {
      if (!inited) {
         if (header[1] != VCMD_CREATE_RENDERER)
            goto fail;
         ret = vtest_create_renderer(r, header[0]);
         inited = true;
      }
      vtest_poll_resource_busy_wait();

      switch (header[1]) {
         // Protocol version 1:
         case VCMD_GET_CAPS:
            ret = vtest_send_caps(r, header[0]);
              break;
         case VCMD_RESOURCE_CREATE:
            ret = vtest_create_resource(r, header[0]);
              break;
         case VCMD_RESOURCE_UNREF:
            ret = vtest_resource_unref(r, header[0]);
              break;
         case VCMD_SUBMIT_CMD:
            ret = vtest_submit_cmd(r, header[0]);
              break;
         case VCMD_TRANSFER_GET:
            ret = vtest_transfer_get(r, header[0]);
              break;
         case VCMD_TRANSFER_PUT:
            ret = vtest_transfer_put(r, header[0]);
              break;
         case VCMD_RESOURCE_BUSY_WAIT:
            ret = vtest_resource_busy_wait(r, header[0]);
              break;
         case VCMD_GET_CAPS2:
            ret = vtest_send_caps2(r, header[0]);
              break;
         case VCMD_DT_COMMAND:
            ret = vtest_dt_cmd(r);
              break;

         // Protocol version 2:
         case VCMD_PROTOCOL_VERSION:
            ret = vtest_protocol_version(r, header[0]);
              break;
         case VCMD_PING_PROTOCOL_VERSION:
            ret = vtest_ping_protocol_version(r, header[0]);
              break;
         case VCMD_RESOURCE_CREATE2:
            ret = vtest_create_resource2(r, header[0]);
              break;
         case VCMD_TRANSFER_GET2:
            ret = vtest_transfer_get2(r, header[0]);
              break;
         case VCMD_TRANSFER_PUT2:
            ret = vtest_transfer_put2(r, header[0]);
              break;
         default:
            break;
      }

      if (ret < 0) {
         goto fail;
      }
      goto again;
   }
   if (ret <= 0) {
      goto fail;
   }
fail:
   //fprintf(stderr, "socket failed - closing renderer\n");
   printf("socket failed - closing renderer\n");

   vtest_destroy_renderer(r);
   close(r->fd);
   free(r);

   return 0;
}

//TODO: JNI

#ifdef ANDROID_JNI
JNIEXPORT jint JNICALL Java_com_mittorn_virgloverlay_common_overlay_nativeOpen(JNIEnv *env, jclass cls)
{
   disp = eglGetDisplay(EGL_DEFAULT_DISPLAY);
   return vtest_open_socket(sock_path);
}

JNIEXPORT void JNICALL Java_com_mittorn_virgloverlay_common_overlay_nativeUnlink(JNIEnv *env, jclass cls)
{
unlink(sock_path);
}
JNIEXPORT jint JNICALL Java_com_mittorn_virgloverlay_common_overlay_nativeAccept(JNIEnv *env, jclass cls, jint fd)
{
   return wait_for_socket_accept(fd);
}

JNIEXPORT void JNICALL Java_com_mittorn_virgloverlay_common_overlay_nativeSettings(JNIEnv *env, jclass cls, jstring settings)
{
char *utf = (*env)->GetStringUTFChars(env, settings, 0);
int var1, var2, var3;
if (utf) {
FILE *f = fopen(utf, "r");
if(!f)exit(1);
fscanf(f, "%d %d %d %d", &var1, &var2, &var3, &dxtn_decompress);
(*env)->ReleaseStringUTFChars(env, settings, utf);
}
}

JNIEXPORT jint JNICALL Java_com_mittorn_virgloverlay_common_overlay_nativeInit(JNIEnv *env, jclass cls, jstring settings)
{
   char *utf = (*env)->GetStringUTFChars(env, settings, 0);
   if (utf) {
      FILE *f = fopen(utf, "r");
      if(!f)exit(1);
      fscanf(f, "%d %[^ ]", &flags, sock_path);
      printf("'%d', '%s'", flags, sock_path);
      (*env)->ReleaseStringUTFChars(env, settings, utf);
   }
   return flags & FL_MULTITHREAD;
}

JNIEXPORT void JNICALL Java_com_mittorn_virgloverlay_common_overlay_nativeRun(JNIEnv *env, jclass cls, jint fd)
{
static int ctx_id;
ctx_id++;
r0 = create_renderer(fd, ctx_id);
r0->jni.env = env;
r0->jni.cls = cls;
r0->jni.create = (*env)->GetStaticMethodID(env,cls, "create", "(IIII)Landroid/view/SurfaceView;");
r0->jni.get_surface = (*env)->GetStaticMethodID(env,cls, "get_surface", "(Landroid/view/SurfaceView;)Landroid/view/Surface;");
r0->jni.set_rect = (*env)->GetStaticMethodID(env,cls, "set_rect", "(Landroid/view/SurfaceView;IIIII)V");
r0->jni.destroy = (*env)->GetStaticMethodID(env,cls, "destroy", "(Landroid/view/SurfaceView;)V");
r0->flags = flags;

renderer_loop(r0);
}
#endif