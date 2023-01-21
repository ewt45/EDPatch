/*
 * Copyright 2021 Google LLC
 * SPDX-License-Identifier: MIT
 */

#ifndef RENDER_VIRGL_H
#define RENDER_VIRGL_H

#include "render_common.h"

#ifdef ENABLE_RENDER_SERVER_WORKER_THREAD
#include "c11/threads.h"
#endif

/* Workers call into virglrenderer.  When they are processes, not much care is
 * required.  We just want to be careful that the server process might have
 * initialized viglrenderer before workers are forked.
 *
 * But when workers are threads, we need to grab a lock to protect
 * virglrenderer.
 *
 * TODO skip virglrenderer.h and go straight to vkr_renderer.h.  That allows
 * us to remove this file.
 */
struct render_virgl {
#ifdef ENABLE_RENDER_SERVER_WORKER_THREAD
   /* this protects the struct */
   mtx_t struct_mutex;
   /* this protects virglrenderer */
   mtx_t dispatch_mutex;
#endif

   /* for nested initialization */
   int init_count;
   uint32_t init_flags;

   struct list_head contexts;
};

extern struct render_virgl render_virgl_internal;

bool
render_virgl_init(uint32_t init_flags);

void
render_virgl_fini(void);

void
render_virgl_add_context(struct render_context *ctx);

void
render_virgl_remove_context(struct render_context *ctx);

static inline void
render_virgl_lock_dispatch(void)
{
#ifdef ENABLE_RENDER_SERVER_WORKER_THREAD
   mtx_lock(&render_virgl_internal.dispatch_mutex);
#endif
}

static inline void
render_virgl_unlock_dispatch(void)
{
#ifdef ENABLE_RENDER_SERVER_WORKER_THREAD
   mtx_unlock(&render_virgl_internal.dispatch_mutex);
#endif
}

#endif /* RENDER_VIRGL_H */
