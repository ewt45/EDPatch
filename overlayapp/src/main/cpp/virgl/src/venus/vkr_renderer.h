/*
 * Copyright 2020 Google LLC
 * SPDX-License-Identifier: MIT
 */

#ifndef VKR_RENDERER_H
#define VKR_RENDERER_H

#include "config.h"

#include <stddef.h>
#include <stdint.h>

#include "util/os_misc.h"
#include "virgl_util.h"

#define VKR_RENDERER_THREAD_SYNC (1u << 0)
#define VKR_RENDERER_ASYNC_FENCE_CB (1u << 1)
#define VKR_RENDERER_RENDER_SERVER (1u << 2)

struct virgl_context;

#ifdef ENABLE_VENUS

int
vkr_renderer_init(uint32_t flags);

void
vkr_renderer_fini(void);

void
vkr_renderer_reset(void);

size_t
vkr_get_capset(void *capset);

struct virgl_context *
vkr_context_create(size_t debug_len, const char *debug_name);

#else /* ENABLE_VENUS */

#include <stdio.h>

static inline int
vkr_renderer_init(UNUSED uint32_t flags)
{
   virgl_log("Vulkan support was not enabled in virglrenderer\n");
   return -1;
}

static inline void
vkr_renderer_fini(void)
{
}

static inline void
vkr_renderer_reset(void)
{
}

static inline size_t
vkr_get_capset(UNUSED void *capset)
{
   return 0;
}

static inline struct virgl_context *
vkr_context_create(UNUSED size_t debug_len, UNUSED const char *debug_name)
{
   return NULL;
}

#endif /* ENABLE_VENUS */

#endif /* VKR_RENDERER_H */
