/*
 * Copyright 2021 Google LLC
 * SPDX-License-Identifier: MIT
 */

#include "render_context.h"

#include <sys/mman.h>

#include "util/u_thread.h"
#include "virgl_util.h"
#include "virglrenderer.h"
#include "vrend_iov.h"

#include "render_virgl.h"

static bool
render_context_import_resource(struct render_context *ctx,
                               const struct render_context_op_import_resource_request *req,
                               int res_fd)
{
   const uint32_t res_id = req->res_id;
   const enum virgl_resource_fd_type fd_type = req->fd_type;
   const uint64_t size = req->size;

   if (fd_type == VIRGL_RESOURCE_FD_INVALID || !size) {
      render_log("failed to attach invalid resource %d", res_id);
      return false;
   }

   uint32_t import_fd_type;
   switch (fd_type) {
   case VIRGL_RESOURCE_FD_DMABUF:
      import_fd_type = VIRGL_RENDERER_BLOB_FD_TYPE_DMABUF;
      break;
   case VIRGL_RESOURCE_FD_OPAQUE:
      import_fd_type = VIRGL_RENDERER_BLOB_FD_TYPE_OPAQUE;
      break;
   case VIRGL_RESOURCE_FD_SHM:
      import_fd_type = VIRGL_RENDERER_BLOB_FD_TYPE_SHM;
      break;
   default:
      import_fd_type = 0;
      break;
   }
   const struct virgl_renderer_resource_import_blob_args import_args = {
      .res_handle = res_id,
      .blob_mem = VIRGL_RENDERER_BLOB_MEM_HOST3D,
      .fd_type = import_fd_type,
      .fd = res_fd,
      .size = size,
   };

   int ret = virgl_renderer_resource_import_blob(&import_args);
   if (ret) {
      render_log("failed to import blob resource %d (%d)", res_id, ret);
      return false;
   }

   virgl_renderer_ctx_attach_resource(ctx->ctx_id, res_id);

   return true;
}

void
render_context_update_timeline(struct render_context *ctx,
                               uint32_t ring_idx,
                               uint32_t seqno)
{
   /* this can be called by the context's main thread and sync threads */
   atomic_store(&ctx->shmem_timelines[ring_idx], seqno);
   if (ctx->fence_eventfd >= 0)
      write_eventfd(ctx->fence_eventfd, 1);
}

static bool
render_context_init_virgl_context(struct render_context *ctx,
                                  const struct render_context_op_init_request *req,
                                  int shmem_fd,
                                  int fence_eventfd)
{
   const int timeline_count = req->shmem_size / sizeof(*ctx->shmem_timelines);

   void *shmem_ptr = mmap(NULL, req->shmem_size, PROT_WRITE, MAP_SHARED, shmem_fd, 0);
   if (shmem_ptr == MAP_FAILED)
      return false;

   int ret = virgl_renderer_context_create_with_flags(ctx->ctx_id, req->flags,
                                                      ctx->name_len, ctx->name);
   if (ret) {
      munmap(shmem_ptr, req->shmem_size);
      return false;
   }

   ctx->shmem_fd = shmem_fd;
   ctx->shmem_size = req->shmem_size;
   ctx->shmem_ptr = shmem_ptr;
   ctx->shmem_timelines = shmem_ptr;

   for (int i = 0; i < timeline_count; i++)
      atomic_store(&ctx->shmem_timelines[i], 0);

   ctx->timeline_count = timeline_count;

   ctx->fence_eventfd = fence_eventfd;

   return true;
}

static bool
render_context_create_resource(struct render_context *ctx,
                               const struct render_context_op_create_resource_request *req,
                               enum virgl_resource_fd_type *out_fd_type,
                               uint32_t *out_map_info,
                               int *out_res_fd)
{
   const uint32_t res_id = req->res_id;
   const struct virgl_renderer_resource_create_blob_args blob_args = {
      .res_handle = res_id,
      .ctx_id = ctx->ctx_id,
      .blob_mem = VIRGL_RENDERER_BLOB_MEM_HOST3D,
      .blob_flags = req->blob_flags,
      .blob_id = req->blob_id,
      .size = req->blob_size,
   };
   int ret = virgl_renderer_resource_create_blob(&blob_args);
   if (ret) {
      render_log("failed to create blob resource");
      return false;
   }

   uint32_t map_info;
   ret = virgl_renderer_resource_get_map_info(res_id, &map_info);
   if (ret) {
      /* properly set map_info when the resource has no map cache info */
      map_info = VIRGL_RENDERER_MAP_CACHE_NONE;
   }

   uint32_t fd_type;
   int res_fd;
   ret = virgl_renderer_resource_export_blob(res_id, &fd_type, &res_fd);
   if (ret) {
      virgl_renderer_resource_unref(res_id);
      return false;
   }

   /* RENDER_CONTEXT_OP_CREATE_RESOURCE implies attach and proxy will not send
    * RENDER_CONTEXT_OP_IMPORT_RESOURCE to attach the resource again.
    */
   virgl_renderer_ctx_attach_resource(ctx->ctx_id, res_id);

   switch (fd_type) {
   case VIRGL_RENDERER_BLOB_FD_TYPE_DMABUF:
      *out_fd_type = VIRGL_RESOURCE_FD_DMABUF;
      break;
   case VIRGL_RENDERER_BLOB_FD_TYPE_OPAQUE:
      *out_fd_type = VIRGL_RESOURCE_FD_OPAQUE;
      break;
   case VIRGL_RENDERER_BLOB_FD_TYPE_SHM:
      *out_fd_type = VIRGL_RESOURCE_FD_SHM;
      break;
   default:
      *out_fd_type = 0;
   }

   *out_map_info = map_info;
   *out_res_fd = res_fd;

   return true;
}

static bool
render_context_dispatch_submit_fence(struct render_context *ctx,
                                     const union render_context_op_request *req,
                                     UNUSED const int *fds,
                                     UNUSED int fd_count)
{
   /* always merge fences */
   assert(!(req->submit_fence.flags & ~VIRGL_RENDERER_FENCE_FLAG_MERGEABLE));
   const uint32_t flags = VIRGL_RENDERER_FENCE_FLAG_MERGEABLE;
   const uint32_t ring_idx = req->submit_fence.ring_index;
   const uint32_t seqno = req->submit_fence.seqno;

   assert(ring_idx < (uint32_t)ctx->timeline_count);
   int ret = virgl_renderer_context_create_fence(ctx->ctx_id, flags, ring_idx, seqno);

   return !ret;
}

static bool
render_context_dispatch_submit_cmd(struct render_context *ctx,
                                   const union render_context_op_request *req,
                                   UNUSED const int *fds,
                                   UNUSED int fd_count)
{
   const int ndw = req->submit_cmd.size / sizeof(uint32_t);
   void *cmd = (void *)req->submit_cmd.cmd;
   if (req->submit_cmd.size > sizeof(req->submit_cmd.cmd)) {
      cmd = malloc(req->submit_cmd.size);
      if (!cmd)
         return true;

      const size_t inlined = sizeof(req->submit_cmd.cmd);
      const size_t remain = req->submit_cmd.size - inlined;

      memcpy(cmd, req->submit_cmd.cmd, inlined);
      if (!render_socket_receive_data(&ctx->socket, (char *)cmd + inlined, remain)) {
         free(cmd);
         return false;
      }
   }

   int ret = virgl_renderer_submit_cmd(cmd, ctx->ctx_id, ndw);

   if (cmd != req->submit_cmd.cmd)
      free(cmd);

   const struct render_context_op_submit_cmd_reply reply = {
      .ok = !ret,
   };
   if (!render_socket_send_reply(&ctx->socket, &reply, sizeof(reply)))
      return false;

   return true;
}

static bool
render_context_dispatch_create_resource(struct render_context *ctx,
                                        const union render_context_op_request *req,
                                        UNUSED const int *fds,
                                        UNUSED int fd_count)
{
   struct render_context_op_create_resource_reply reply = {
      .fd_type = VIRGL_RESOURCE_FD_INVALID,
   };
   int res_fd;
   bool ok = render_context_create_resource(ctx, &req->create_resource, &reply.fd_type,
                                            &reply.map_info, &res_fd);
   if (!ok)
      return render_socket_send_reply(&ctx->socket, &reply, sizeof(reply));

   ok =
      render_socket_send_reply_with_fds(&ctx->socket, &reply, sizeof(reply), &res_fd, 1);
   close(res_fd);

   return ok;
}

static bool
render_context_dispatch_destroy_resource(UNUSED struct render_context *ctx,
                                         const union render_context_op_request *req,
                                         UNUSED const int *fds,
                                         UNUSED int fd_count)
{
   virgl_renderer_resource_unref(req->destroy_resource.res_id);
   return true;
}

static bool
render_context_dispatch_import_resource(struct render_context *ctx,
                                        const union render_context_op_request *req,
                                        const int *fds,
                                        int fd_count)
{
   if (fd_count != 1) {
      render_log("failed to attach resource with fd_count %d", fd_count);
      return false;
   }

   /* classic 3d resource with valid size reuses the blob import path here */
   return render_context_import_resource(ctx, &req->import_resource, fds[0]);
}

static bool
render_context_dispatch_init(struct render_context *ctx,
                             const union render_context_op_request *req,
                             const int *fds,
                             int fd_count)
{
   if (fd_count != 1 && fd_count != 2)
      return false;

   const int shmem_fd = fds[0];
   const int fence_eventfd = fd_count == 2 ? fds[1] : -1;
   return render_context_init_virgl_context(ctx, &req->init, shmem_fd, fence_eventfd);
}

static bool
render_context_dispatch_nop(UNUSED struct render_context *ctx,
                            UNUSED const union render_context_op_request *req,
                            UNUSED const int *fds,
                            UNUSED int fd_count)
{
   return true;
}

struct render_context_dispatch_entry {
   size_t expect_size;
   int max_fd_count;
   bool (*dispatch)(struct render_context *ctx,
                    const union render_context_op_request *req,
                    const int *fds,
                    int fd_count);
};

static const struct render_context_dispatch_entry
   render_context_dispatch_table[RENDER_CONTEXT_OP_COUNT] = {
#define RENDER_CONTEXT_DISPATCH(NAME, name, max_fd)                                      \
   [RENDER_CONTEXT_OP_##                                                                 \
      NAME] = { .expect_size = sizeof(struct render_context_op_##name##_request),        \
                .max_fd_count = (max_fd),                                                \
                .dispatch = render_context_dispatch_##name }
      RENDER_CONTEXT_DISPATCH(NOP, nop, 0),
      RENDER_CONTEXT_DISPATCH(INIT, init, 2),
      RENDER_CONTEXT_DISPATCH(CREATE_RESOURCE, create_resource, 0),
      RENDER_CONTEXT_DISPATCH(IMPORT_RESOURCE, import_resource, 1),
      RENDER_CONTEXT_DISPATCH(DESTROY_RESOURCE, destroy_resource, 0),
      RENDER_CONTEXT_DISPATCH(SUBMIT_CMD, submit_cmd, 0),
      RENDER_CONTEXT_DISPATCH(SUBMIT_FENCE, submit_fence, 0),
#undef RENDER_CONTEXT_DISPATCH
   };

static bool
render_context_dispatch(struct render_context *ctx)
{
   union render_context_op_request req;
   size_t req_size;
   int req_fds[8];
   int req_fd_count;
   if (!render_socket_receive_request_with_fds(&ctx->socket, &req, sizeof(req), &req_size,
                                               req_fds, ARRAY_SIZE(req_fds),
                                               &req_fd_count))
      return false;

   assert((unsigned int)req_fd_count <= ARRAY_SIZE(req_fds));

   if (req.header.op >= RENDER_CONTEXT_OP_COUNT) {
      render_log("invalid context op %d", req.header.op);
      goto fail;
   }

   const struct render_context_dispatch_entry *entry =
      &render_context_dispatch_table[req.header.op];
   if (entry->expect_size != req_size || entry->max_fd_count < req_fd_count) {
      render_log("invalid request size (%zu) or fd count (%d) for context op %d",
                 req_size, req_fd_count, req.header.op);
      goto fail;
   }

   render_virgl_lock_dispatch();
   const bool ok = entry->dispatch(ctx, &req, req_fds, req_fd_count);
   render_virgl_unlock_dispatch();
   if (!ok) {
      render_log("failed to dispatch context op %d", req.header.op);
      goto fail;
   }

   return true;

fail:
   for (int i = 0; i < req_fd_count; i++)
      close(req_fds[i]);
   return false;
}

static bool
render_context_run(struct render_context *ctx)
{
   while (true) {
      if (!render_context_dispatch(ctx))
         return false;
   }

   return true;
}

static void
render_context_fini(struct render_context *ctx)
{
   render_virgl_lock_dispatch();
   /* destroy the context first to join its sync threads and ring threads */
   virgl_renderer_context_destroy(ctx->ctx_id);
   render_virgl_unlock_dispatch();

   render_virgl_remove_context(ctx);

   if (ctx->shmem_ptr)
      munmap(ctx->shmem_ptr, ctx->shmem_size);
   if (ctx->shmem_fd >= 0)
      close(ctx->shmem_fd);

   if (ctx->fence_eventfd >= 0)
      close(ctx->fence_eventfd);

   if (ctx->name)
      free(ctx->name);

   render_socket_fini(&ctx->socket);
}

static void
render_context_set_thread_name(uint32_t ctx_id, const char *ctx_name)
{
   char thread_name[16];
   snprintf(thread_name, ARRAY_SIZE(thread_name), "virgl-%d-%s", ctx_id, ctx_name);
   u_thread_setname(thread_name);
}

static bool
render_context_init_name(struct render_context *ctx,
                         uint32_t ctx_id,
                         const char *ctx_name)
{
   ctx->name_len = strlen(ctx_name);
   ctx->name = malloc(ctx->name_len + 1);
   if (!ctx->name)
      return false;

   strcpy(ctx->name, ctx_name);

   render_context_set_thread_name(ctx_id, ctx_name);

#ifdef _GNU_SOURCE
   /* Sets the guest app executable name used by mesa to load app-specific driver
    * configuration. */
   program_invocation_name = ctx->name;
   program_invocation_short_name = ctx->name;
#endif

   return true;
}

static bool
render_context_init(struct render_context *ctx, const struct render_context_args *args)
{
   memset(ctx, 0, sizeof(*ctx));
   ctx->ctx_id = args->ctx_id;
   render_socket_init(&ctx->socket, args->ctx_fd);
   ctx->shmem_fd = -1;
   ctx->fence_eventfd = -1;

   if (!render_context_init_name(ctx, args->ctx_id, args->ctx_name))
      return false;

   render_virgl_add_context(ctx);

   return true;
}

bool
render_context_main(const struct render_context_args *args)
{
   struct render_context ctx;

   assert(args->valid && args->ctx_id && args->ctx_fd >= 0);

   if (!render_virgl_init(args->init_flags)) {
      close(args->ctx_fd);
      return false;
   }

   if (!render_context_init(&ctx, args)) {
      render_virgl_fini();
      close(args->ctx_fd);
      return false;
   }

   const bool ok = render_context_run(&ctx);
   render_context_fini(&ctx);

   render_virgl_fini();

   return ok;
}
