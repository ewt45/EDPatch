/*
 * Copyright 2020 Google LLC
 * SPDX-License-Identifier: MIT
 */

#ifndef VKR_CONTEXT_H
#define VKR_CONTEXT_H

#include "vkr_common.h"

#include "venus-protocol/vn_protocol_renderer_defines.h"
#include "virgl_context.h"
#include "vrend_iov.h"

#include "vkr_cs.h"

struct virgl_resource;

/*
 * When a virgl_resource is attached in vkr_context_attach_resource, a
 * vkr_resource_attachment is created.  A vkr_resource_attachment is valid
 * until the resource it tracks is detached.
 */
struct vkr_resource_attachment {
   struct virgl_resource *resource;

   /* if VIRGL_RESOURCE_FD_SHM, this is the mapping of the shm and iov below
    * points to this
    */
   struct iovec shm_iov;

   const struct iovec *iov;
   int iov_count;
};

enum vkr_context_validate_level {
   /* no validation */
   VKR_CONTEXT_VALIDATE_NONE,
   /* force enabling a subset of the validation layer */
   VKR_CONTEXT_VALIDATE_ON,
   /* force enabling the validation layer */
   VKR_CONTEXT_VALIDATE_FULL,
};

struct vkr_context {
   struct virgl_context base;

   char *debug_name;
   enum vkr_context_validate_level validate_level;
   bool validate_fatal;

   mtx_t mutex;

   struct list_head rings;
   struct hash_table *object_table;
   struct hash_table *resource_table;

   struct vkr_cs_encoder encoder;
   struct vkr_cs_decoder decoder;
   struct vn_dispatch_context dispatch;

   int fence_eventfd;
   struct list_head busy_queues;
   struct list_head signaled_syncs;

   struct vkr_instance *instance;
   char *instance_name;
};

void
vkr_context_free_resource(struct hash_entry *entry);

static inline void
vkr_context_add_resource(struct vkr_context *ctx, struct vkr_resource_attachment *att)
{
   assert(!_mesa_hash_table_search(ctx->resource_table, &att->resource->res_id));
   _mesa_hash_table_insert(ctx->resource_table, &att->resource->res_id, att);
}

static inline void
vkr_context_remove_resource(struct vkr_context *ctx, uint32_t res_id)
{
   struct hash_entry *entry = _mesa_hash_table_search(ctx->resource_table, &res_id);
   if (likely(entry)) {
      vkr_context_free_resource(entry);
      _mesa_hash_table_remove(ctx->resource_table, entry);
   }
}

static inline struct vkr_resource_attachment *
vkr_context_get_resource(struct vkr_context *ctx, uint32_t res_id)
{
   const struct hash_entry *entry = _mesa_hash_table_search(ctx->resource_table, &res_id);
   return likely(entry) ? entry->data : NULL;
}

static inline bool
vkr_context_validate_object_id(struct vkr_context *ctx, vkr_object_id id)
{
   if (unlikely(!id || _mesa_hash_table_search(ctx->object_table, &id))) {
      vkr_log("invalid object id %" PRIu64, id);
      vkr_cs_decoder_set_fatal(&ctx->decoder);
      return false;
   }

   return true;
}

static inline void *
vkr_context_alloc_object(UNUSED struct vkr_context *ctx,
                         size_t size,
                         VkObjectType type,
                         const void *id_handle)
{
   const vkr_object_id id = vkr_cs_handle_load_id((const void **)id_handle, type);
   if (!vkr_context_validate_object_id(ctx, id))
      return NULL;

   return vkr_object_alloc(size, type, id);
}

void
vkr_context_free_object(struct hash_entry *entry);

static inline void
vkr_context_add_object(struct vkr_context *ctx, struct vkr_object *obj)
{
   assert(vkr_is_recognized_object_type(obj->type));
   assert(obj->id);
   assert(!_mesa_hash_table_search(ctx->object_table, &obj->id));

   _mesa_hash_table_insert(ctx->object_table, &obj->id, obj);
}

static inline void
vkr_context_remove_object(struct vkr_context *ctx, struct vkr_object *obj)
{
   assert(_mesa_hash_table_search(ctx->object_table, &obj->id));

   struct hash_entry *entry = _mesa_hash_table_search(ctx->object_table, &obj->id);
   if (likely(entry)) {
      vkr_context_free_object(entry);
      _mesa_hash_table_remove(ctx->object_table, entry);
   }
}

static inline void
vkr_context_remove_objects(struct vkr_context *ctx, struct list_head *objects)
{
   struct vkr_object *obj, *tmp;
   LIST_FOR_EACH_ENTRY_SAFE (obj, tmp, objects, track_head)
      vkr_context_remove_object(ctx, obj);
   /* objects should be reinitialized if to be reused */
}

static inline void *
vkr_context_get_object(struct vkr_context *ctx, vkr_object_id obj_id)
{
   const struct hash_entry *entry = _mesa_hash_table_search(ctx->object_table, &obj_id);
   return likely(entry) ? entry->data : NULL;
}

static inline const char *
vkr_context_get_name(const struct vkr_context *ctx)
{
   /* ctx->instance_name is the application name while ctx->debug_name is
    * usually the guest process name or the hypervisor name.  This never
    * returns NULL because ctx->debug_name is never NULL.
    */
   return ctx->instance_name ? ctx->instance_name : ctx->debug_name;
}

void
vkr_context_add_instance(struct vkr_context *ctx,
                         struct vkr_instance *instance,
                         const char *name);

void
vkr_context_remove_instance(struct vkr_context *ctx, struct vkr_instance *instance);

#endif /* VKR_CONTEXT_H */
