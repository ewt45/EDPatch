/*
 * Copyright 2021 Google LLC
 * SPDX-License-Identifier: MIT
 */

#include "vkr_ring.h"

#include <stdio.h>
#include <time.h>

#include "vrend_iov.h"

#include "vkr_context.h"

enum vkr_ring_status_flag {
   VKR_RING_STATUS_IDLE = 1u << 0,
};

/* callers must make sure they do not seek to end-of-resource or beyond */
static const struct iovec *
seek_resource(const struct vkr_resource_attachment *att,
              int base_iov_index,
              size_t offset,
              int *out_iov_index,
              size_t *out_iov_offset)
{
   const struct iovec *iov = &att->iov[base_iov_index];
   assert(iov - att->iov < att->iov_count);
   while (offset >= iov->iov_len) {
      offset -= iov->iov_len;
      iov++;
      assert(iov - att->iov < att->iov_count);
   }

   *out_iov_index = iov - att->iov;
   *out_iov_offset = offset;

   return iov;
}

static void *
get_resource_pointer(const struct vkr_resource_attachment *att,
                     int base_iov_index,
                     size_t offset)
{
   const struct iovec *iov =
      seek_resource(att, base_iov_index, offset, &base_iov_index, &offset);
   return (uint8_t *)iov->iov_base + offset;
}

static void
vkr_ring_init_extra(struct vkr_ring *ring, const struct vkr_ring_layout *layout)
{
   struct vkr_ring_extra *extra = &ring->extra;

   seek_resource(layout->attachment, 0, layout->extra.begin, &extra->base_iov_index,
                 &extra->base_iov_offset);

   extra->region = vkr_region_make_relative(&layout->extra);
}

static void
vkr_ring_init_buffer(struct vkr_ring *ring, const struct vkr_ring_layout *layout)
{
   struct vkr_ring_buffer *buf = &ring->buffer;

   const struct iovec *base_iov =
      seek_resource(layout->attachment, 0, layout->buffer.begin, &buf->base_iov_index,
                    &buf->base_iov_offset);

   buf->size = vkr_region_size(&layout->buffer);
   assert(util_is_power_of_two_nonzero(buf->size));
   buf->mask = buf->size - 1;

   buf->cur = 0;
   buf->cur_iov = base_iov;
   buf->cur_iov_index = buf->base_iov_index;
   buf->cur_iov_offset = buf->base_iov_offset;
}

static bool
vkr_ring_init_control(struct vkr_ring *ring, const struct vkr_ring_layout *layout)
{
   struct vkr_ring_control *ctrl = &ring->control;

   ctrl->head = get_resource_pointer(layout->attachment, 0, layout->head.begin);
   ctrl->tail = get_resource_pointer(layout->attachment, 0, layout->tail.begin);
   ctrl->status = get_resource_pointer(layout->attachment, 0, layout->status.begin);

   /* we will manage head and status, and we expect them to be 0 initially */
   if (*ctrl->head || *ctrl->status)
      return false;

   return true;
}

static void
vkr_ring_store_head(struct vkr_ring *ring)
{
   /* the renderer is expected to load the head with memory_order_acquire,
    * forming a release-acquire ordering
    */
   atomic_store_explicit(ring->control.head, ring->buffer.cur, memory_order_release);
}

static uint32_t
vkr_ring_load_tail(const struct vkr_ring *ring)
{
   /* the driver is expected to store the tail with memory_order_release,
    * forming a release-acquire ordering
    */
   return atomic_load_explicit(ring->control.tail, memory_order_acquire);
}

static void
vkr_ring_store_status(struct vkr_ring *ring, uint32_t status)
{
   atomic_store_explicit(ring->control.status, status, memory_order_seq_cst);
}

/* TODO consider requiring virgl_resource to be logically contiguous */
static void
vkr_ring_read_buffer(struct vkr_ring *ring, void *data, uint32_t size)
{
   struct vkr_ring_buffer *buf = &ring->buffer;
   const struct vkr_resource_attachment *att = ring->attachment;

   assert(size <= buf->size);
   const uint32_t buf_offset = buf->cur & buf->mask;
   const uint32_t buf_avail = buf->size - buf_offset;
   const bool wrap = size >= buf_avail;

   uint32_t read_size;
   uint32_t wrap_size;
   if (!wrap) {
      read_size = size;
      wrap_size = 0;
   } else {
      read_size = buf_avail;
      /* When size == buf_avail, wrap is true but wrap_size is 0.  We want to
       * wrap because it seems slightly faster on the next call.  Besides,
       * seek_resource does not support seeking to end-of-resource which could
       * happen if we don't wrap and the buffer region end coincides with the
       * resource end.
       */
      wrap_size = size - buf_avail;
   }

   /* do the reads */
   if (read_size <= buf->cur_iov->iov_len - buf->cur_iov_offset) {
      const void *src = (const uint8_t *)buf->cur_iov->iov_base + buf->cur_iov_offset;
      memcpy(data, src, read_size);

      /* fast path */
      if (!wrap) {
         assert(!wrap_size);
         buf->cur += read_size;
         buf->cur_iov_offset += read_size;
         return;
      }
   } else {
      vrend_read_from_iovec(buf->cur_iov, att->iov_count - buf->cur_iov_index,
                            buf->cur_iov_offset, data, read_size);
   }

   if (wrap_size) {
      vrend_read_from_iovec(att->iov + buf->base_iov_index,
                            att->iov_count - buf->base_iov_index, buf->base_iov_offset,
                            (char *)data + read_size, wrap_size);
   }

   /* advance cur */
   buf->cur += size;
   if (!wrap) {
      buf->cur_iov = seek_resource(att, buf->cur_iov_index, buf->cur_iov_offset + size,
                                   &buf->cur_iov_index, &buf->cur_iov_offset);
   } else {
      buf->cur_iov =
         seek_resource(att, buf->base_iov_index, buf->base_iov_offset + wrap_size,
                       &buf->cur_iov_index, &buf->cur_iov_offset);
   }
}

struct vkr_ring *
vkr_ring_create(const struct vkr_ring_layout *layout,
                struct virgl_context *ctx,
                uint64_t idle_timeout)
{
   struct vkr_ring *ring;
   int ret;

   ring = calloc(1, sizeof(*ring));
   if (!ring)
      return NULL;

   ring->attachment = layout->attachment;

   if (!vkr_ring_init_control(ring, layout)) {
      free(ring);
      return NULL;
   }

   vkr_ring_init_buffer(ring, layout);
   vkr_ring_init_extra(ring, layout);

   ring->cmd = malloc(ring->buffer.size);
   if (!ring->cmd) {
      free(ring);
      return NULL;
   }

   ring->context = ctx;
   ring->idle_timeout = idle_timeout;

   ret = mtx_init(&ring->mutex, mtx_plain);
   if (ret != thrd_success) {
      free(ring->cmd);
      free(ring);
      return NULL;
   }
   ret = cnd_init(&ring->cond);
   if (ret != thrd_success) {
      mtx_destroy(&ring->mutex);
      free(ring->cmd);
      free(ring);
      return NULL;
   }

   return ring;
}

void
vkr_ring_destroy(struct vkr_ring *ring)
{
   list_del(&ring->head);

   assert(!ring->started);
   mtx_destroy(&ring->mutex);
   cnd_destroy(&ring->cond);
   free(ring->cmd);
   free(ring);
}

static uint64_t
vkr_ring_now(void)
{
   const uint64_t ns_per_sec = 1000000000llu;
   struct timespec now;
   if (clock_gettime(CLOCK_MONOTONIC, &now))
      return 0;
   return ns_per_sec * now.tv_sec + now.tv_nsec;
}

static void
vkr_ring_relax(uint32_t *iter)
{
   /* TODO do better */
   const uint32_t busy_wait_order = 4;
   const uint32_t base_sleep_us = 10;

   (*iter)++;
   if (*iter < (1u << busy_wait_order)) {
      thrd_yield();
      return;
   }

   const uint32_t shift = util_last_bit(*iter) - busy_wait_order - 1;
   const uint32_t us = base_sleep_us << shift;
   const struct timespec ts = {
      .tv_sec = us / 1000000,
      .tv_nsec = (us % 1000000) * 1000,
   };
   clock_nanosleep(CLOCK_MONOTONIC, 0, &ts, NULL);
}

static int
vkr_ring_thread(void *arg)
{
   struct vkr_ring *ring = arg;
   struct virgl_context *ctx = ring->context;
   char thread_name[16];

   snprintf(thread_name, ARRAY_SIZE(thread_name), "vkr-ring-%d", ctx->ctx_id);
   u_thread_setname(thread_name);

   uint64_t last_submit = vkr_ring_now();
   uint32_t relax_iter = 0;
   int ret = 0;
   while (ring->started) {
      bool wait = false;
      uint32_t cmd_size;

      if (vkr_ring_now() >= last_submit + ring->idle_timeout) {
         ring->pending_notify = false;
         vkr_ring_store_status(ring, VKR_RING_STATUS_IDLE);
         wait = ring->buffer.cur == vkr_ring_load_tail(ring);
         if (!wait)
            vkr_ring_store_status(ring, 0);
      }

      if (wait) {
         TRACE_SCOPE("ring idle");

         mtx_lock(&ring->mutex);
         if (ring->started && !ring->pending_notify)
            cnd_wait(&ring->cond, &ring->mutex);
         vkr_ring_store_status(ring, 0);
         mtx_unlock(&ring->mutex);

         if (!ring->started)
            break;

         last_submit = vkr_ring_now();
         relax_iter = 0;
      }

      cmd_size = vkr_ring_load_tail(ring) - ring->buffer.cur;
      if (cmd_size) {
         if (cmd_size > ring->buffer.size) {
            ret = -EINVAL;
            break;
         }

         vkr_ring_read_buffer(ring, ring->cmd, cmd_size);
         ctx->submit_cmd(ctx, ring->cmd, cmd_size);
         vkr_ring_store_head(ring);

         last_submit = vkr_ring_now();
         relax_iter = 0;
      } else {
         vkr_ring_relax(&relax_iter);
      }
   }

   return ret;
}

void
vkr_ring_start(struct vkr_ring *ring)
{
   int ret;

   assert(!ring->started);
   ring->started = true;
   ret = thrd_create(&ring->thread, vkr_ring_thread, ring);
   if (ret != thrd_success)
      ring->started = false;
}

bool
vkr_ring_stop(struct vkr_ring *ring)
{
   mtx_lock(&ring->mutex);
   if (thrd_equal(ring->thread, thrd_current())) {
      mtx_unlock(&ring->mutex);
      return false;
   }
   assert(ring->started);
   ring->started = false;
   cnd_signal(&ring->cond);
   mtx_unlock(&ring->mutex);

   thrd_join(ring->thread, NULL);

   return true;
}

void
vkr_ring_notify(struct vkr_ring *ring)
{
   mtx_lock(&ring->mutex);
   ring->pending_notify = true;
   cnd_signal(&ring->cond);
   mtx_unlock(&ring->mutex);

   {
      TRACE_SCOPE("ring notify done");
   }
}

bool
vkr_ring_write_extra(struct vkr_ring *ring, size_t offset, uint32_t val)
{
   struct vkr_ring_extra *extra = &ring->extra;

   if (unlikely(extra->cached_offset != offset || !extra->cached_data)) {
      const struct vkr_region access = VKR_REGION_INIT(offset, sizeof(val));
      if (!vkr_region_is_valid(&access) || !vkr_region_is_within(&access, &extra->region))
         return false;

      /* Mesa always sets offset to 0 and the cache hit rate will be 100% */
      extra->cached_offset = offset;
      extra->cached_data = get_resource_pointer(ring->attachment, extra->base_iov_index,
                                                extra->base_iov_offset + offset);
   }

   atomic_store_explicit(extra->cached_data, val, memory_order_release);

   {
      TRACE_SCOPE("ring extra done");
   }

   return true;
}
