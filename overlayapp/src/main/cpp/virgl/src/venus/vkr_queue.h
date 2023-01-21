/*
 * Copyright 2020 Google LLC
 * SPDX-License-Identifier: MIT
 */

#ifndef VKR_QUEUE_H
#define VKR_QUEUE_H

#include "vkr_common.h"

struct vkr_queue_sync {
   VkFence fence;
   bool device_lost;

   uint32_t flags;
   uint64_t queue_id;
   uint64_t fence_id;

   struct list_head head;
};

struct vkr_queue {
   struct vkr_object base;

   struct vkr_context *context;
   struct vkr_device *device;

   VkDeviceQueueCreateFlags flags;
   uint32_t family;
   uint32_t index;

   /* Submitted fences are added to pending_syncs first.  How submitted fences
    * are retired depends on VKR_RENDERER_THREAD_SYNC and
    * VKR_RENDERER_ASYNC_FENCE_CB.
    *
    * When VKR_RENDERER_THREAD_SYNC is not set, the main thread calls
    * vkGetFenceStatus and retires signaled fences in pending_syncs in order.
    *
    * When VKR_RENDERER_THREAD_SYNC is set but VKR_RENDERER_ASYNC_FENCE_CB is
    * not set, the sync thread calls vkWaitForFences and moves signaled fences
    * from pending_syncs to signaled_syncs in order.  The main thread simply
    * retires all fences in signaled_syncs.
    *
    * When VKR_RENDERER_THREAD_SYNC and VKR_RENDERER_ASYNC_FENCE_CB are both
    * set, the sync thread calls vkWaitForFences and retires signaled fences
    * in pending_syncs in order.
    */
   int eventfd;
   thrd_t thread;
   mtx_t mutex;
   cnd_t cond;
   bool join;
   struct list_head pending_syncs;
   struct list_head signaled_syncs;

   struct list_head busy_head;
};
VKR_DEFINE_OBJECT_CAST(queue, VK_OBJECT_TYPE_QUEUE, VkQueue)

struct vkr_fence {
   struct vkr_object base;
};
VKR_DEFINE_OBJECT_CAST(fence, VK_OBJECT_TYPE_FENCE, VkFence)

struct vkr_semaphore {
   struct vkr_object base;
};
VKR_DEFINE_OBJECT_CAST(semaphore, VK_OBJECT_TYPE_SEMAPHORE, VkSemaphore)

struct vkr_event {
   struct vkr_object base;
};
VKR_DEFINE_OBJECT_CAST(event, VK_OBJECT_TYPE_EVENT, VkEvent)

void
vkr_context_init_queue_dispatch(struct vkr_context *ctx);

void
vkr_context_init_fence_dispatch(struct vkr_context *ctx);

void
vkr_context_init_semaphore_dispatch(struct vkr_context *ctx);

void
vkr_context_init_event_dispatch(struct vkr_context *ctx);

struct vkr_queue_sync *
vkr_device_alloc_queue_sync(struct vkr_device *dev,
                            uint32_t fence_flags,
                            uint64_t queue_id,
                            uint64_t fence_id);

void
vkr_device_free_queue_sync(struct vkr_device *dev, struct vkr_queue_sync *sync);

void
vkr_queue_get_signaled_syncs(struct vkr_queue *queue,
                             struct list_head *retired_syncs,
                             bool *queue_empty);

struct vkr_queue *
vkr_queue_create(struct vkr_context *ctx,
                 struct vkr_device *dev,
                 VkDeviceQueueCreateFlags flags,
                 uint32_t family,
                 uint32_t index,
                 VkQueue handle);

void
vkr_queue_destroy(struct vkr_context *ctx, struct vkr_queue *queue);

#endif /* VKR_QUEUE_H */
