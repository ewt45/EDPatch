/*
 * Copyright 2021 Google LLC
 * SPDX-License-Identifier: MIT
 */

#include "render_virgl.h"

#include "virglrenderer.h"

#include "render_context.h"

struct render_virgl render_virgl_internal = {
#ifdef ENABLE_RENDER_SERVER_WORKER_THREAD
   .struct_mutex = _MTX_INITIALIZER_NP,
   .dispatch_mutex = _MTX_INITIALIZER_NP,
#endif
   .init_count = 0,
};

static struct render_virgl *
render_virgl_lock_struct(void)
{
#ifdef ENABLE_RENDER_SERVER_WORKER_THREAD
   mtx_lock(&render_virgl_internal.struct_mutex);
#endif
   return &render_virgl_internal;
}

static void
render_virgl_unlock_struct(void)
{
#ifdef ENABLE_RENDER_SERVER_WORKER_THREAD
   mtx_unlock(&render_virgl_internal.struct_mutex);
#endif
}

static struct render_context *
render_virgl_lookup_context(uint32_t ctx_id)
{
   const struct render_virgl *virgl = render_virgl_lock_struct();
   struct render_context *ctx = NULL;

#ifdef ENABLE_RENDER_SERVER_WORKER_THREAD
   list_for_each_entry (struct render_context, iter, &virgl->contexts, head) {
      if (iter->ctx_id == ctx_id) {
         ctx = iter;
         break;
      }
   }
#else
   assert(list_is_singular(&virgl->contexts));
   ctx = list_first_entry(&virgl->contexts, struct render_context, head);
   assert(ctx->ctx_id == ctx_id);
   (void)ctx_id;
#endif

   render_virgl_unlock_struct();

   return ctx;
}

static void
render_virgl_debug_callback(const char *fmt, va_list ap)
{
   char buf[1024];
   vsnprintf(buf, sizeof(buf), fmt, ap);
   render_log(buf);
}

static void
render_virgl_cb_write_context_fence(UNUSED void *cookie,
                                    uint32_t ctx_id,
                                    uint64_t queue_id,
                                    uint64_t fence_id)
{
   struct render_context *ctx = render_virgl_lookup_context(ctx_id);
   assert(ctx);

   const uint32_t ring_idx = queue_id;
   const uint32_t seqno = (uint32_t)fence_id;
   render_context_update_timeline(ctx, ring_idx, seqno);
}

static const struct virgl_renderer_callbacks render_virgl_cbs = {
   .version = VIRGL_RENDERER_CALLBACKS_VERSION,
   .write_context_fence = render_virgl_cb_write_context_fence,
};

void
render_virgl_add_context(struct render_context *ctx)
{
   struct render_virgl *virgl = render_virgl_lock_struct();
   list_addtail(&ctx->head, &virgl->contexts);
   render_virgl_unlock_struct();
}

void
render_virgl_remove_context(struct render_context *ctx)
{
   render_virgl_lock_struct();
   list_del(&ctx->head);
   render_virgl_unlock_struct();
}

void
render_virgl_fini(void)
{
   struct render_virgl *virgl = render_virgl_lock_struct();

   if (virgl->init_count) {
      virgl->init_count--;
      if (!virgl->init_count) {
         render_virgl_lock_dispatch();
         virgl_renderer_cleanup(virgl);
         render_virgl_unlock_dispatch();
      }
   }

   render_virgl_unlock_struct();
}

bool
render_virgl_init(uint32_t init_flags)
{
   /* we only care if virgl and/or venus are enabled */
   init_flags &= VIRGL_RENDERER_VENUS | VIRGL_RENDERER_NO_VIRGL;

   /* always use sync thread and async fence cb for low latency */
   init_flags |= VIRGL_RENDERER_THREAD_SYNC | VIRGL_RENDERER_ASYNC_FENCE_CB |
                 VIRGL_RENDERER_USE_EXTERNAL_BLOB;

   struct render_virgl *virgl = render_virgl_lock_struct();

   if (virgl->init_count) {
      if (virgl->init_flags != init_flags) {
         render_log("failed to re-initialize with flags 0x%x", init_flags);
         goto fail;
      }
   } else {
      render_virgl_lock_dispatch();
      virgl_set_debug_callback(render_virgl_debug_callback);
      int ret = virgl_renderer_init(virgl, init_flags,
                                    (struct virgl_renderer_callbacks *)&render_virgl_cbs);
      render_virgl_unlock_dispatch();
      if (ret) {
         render_log("failed to initialize virglrenderer");
         goto fail;
      }

      list_inithead(&virgl->contexts);
      virgl->init_flags = init_flags;
   }

   virgl->init_count++;

   render_virgl_unlock_struct();

   return true;

fail:
   render_virgl_unlock_struct();
   return false;
}
