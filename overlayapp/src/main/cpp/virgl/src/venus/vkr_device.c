/*
 * Copyright 2020 Google LLC
 * SPDX-License-Identifier: MIT
 */

#include "vkr_device.h"

#include "venus-protocol/vn_protocol_renderer_device.h"

#include "vkr_command_buffer.h"
#include "vkr_context.h"
#include "vkr_descriptor_set.h"
#include "vkr_device_memory.h"
#include "vkr_physical_device.h"
#include "vkr_queue.h"

static VkResult
vkr_device_create_queues(struct vkr_context *ctx,
                         struct vkr_device *dev,
                         uint32_t create_info_count,
                         const VkDeviceQueueCreateInfo *create_infos)
{
   struct vn_device_proc_table *vk = &dev->proc_table;
   list_inithead(&dev->queues);

   for (uint32_t i = 0; i < create_info_count; i++) {
      for (uint32_t j = 0; j < create_infos[i].queueCount; j++) {
         const VkDeviceQueueInfo2 info = {
            .sType = VK_STRUCTURE_TYPE_DEVICE_QUEUE_INFO_2,
            .pNext = NULL,
            .flags = create_infos[i].flags,
            .queueFamilyIndex = create_infos[i].queueFamilyIndex,
            .queueIndex = j,
         };
         VkQueue handle = VK_NULL_HANDLE;
         /* There was a bug in spec which forbids usage of vkGetDeviceQueue2
          * with flags set to zero. It was fixed in spec version 1.1.130.
          * Work around drivers that are implementing this buggy behavior
          */
         if (info.flags) {
            vk->GetDeviceQueue2(dev->base.handle.device, &info, &handle);
         } else {
            vk->GetDeviceQueue(dev->base.handle.device, info.queueFamilyIndex,
                               info.queueIndex, &handle);
         }

         struct vkr_queue *queue = vkr_queue_create(
            ctx, dev, info.flags, info.queueFamilyIndex, info.queueIndex, handle);
         if (!queue) {
            struct vkr_queue *entry, *tmp;
            LIST_FOR_EACH_ENTRY_SAFE (entry, tmp, &dev->queues, base.track_head)
               vkr_queue_destroy(ctx, entry);

            return VK_ERROR_OUT_OF_HOST_MEMORY;
         }

         /* queues are not tracked as device objects */
         list_add(&queue->base.track_head, &dev->queues);
      }
   }

   return VK_SUCCESS;
}

static void
vkr_device_init_proc_table(struct vkr_device *dev,
                           uint32_t api_version,
                           const char *const *exts,
                           uint32_t count)
{
   struct vn_info_extension_table ext_table;
   vkr_extension_table_init(&ext_table, exts, count);

   vn_util_init_device_proc_table(dev->base.handle.device, api_version, &ext_table,
                                  &dev->proc_table);
}

static void
vkr_dispatch_vkCreateDevice(struct vn_dispatch_context *dispatch,
                            struct vn_command_vkCreateDevice *args)
{
   struct vkr_context *ctx = dispatch->data;

   struct vkr_physical_device *physical_dev =
      vkr_physical_device_from_handle(args->physicalDevice);

   /* append extensions for our own use */
   const char **exts = NULL;
   uint32_t ext_count = args->pCreateInfo->enabledExtensionCount;
   ext_count += physical_dev->KHR_external_memory_fd;
   ext_count += physical_dev->EXT_external_memory_dma_buf;
   ext_count += physical_dev->KHR_external_fence_fd;
   if (ext_count > args->pCreateInfo->enabledExtensionCount) {
      exts = malloc(sizeof(*exts) * ext_count);
      if (!exts) {
         args->ret = VK_ERROR_OUT_OF_HOST_MEMORY;
         return;
      }
      for (uint32_t i = 0; i < args->pCreateInfo->enabledExtensionCount; i++)
         exts[i] = args->pCreateInfo->ppEnabledExtensionNames[i];

      ext_count = args->pCreateInfo->enabledExtensionCount;
      if (physical_dev->KHR_external_memory_fd)
         exts[ext_count++] = "VK_KHR_external_memory_fd";
      if (physical_dev->EXT_external_memory_dma_buf)
         exts[ext_count++] = "VK_EXT_external_memory_dma_buf";
      if (physical_dev->KHR_external_fence_fd)
         exts[ext_count++] = "VK_KHR_external_fence_fd";

      ((VkDeviceCreateInfo *)args->pCreateInfo)->ppEnabledExtensionNames = exts;
      ((VkDeviceCreateInfo *)args->pCreateInfo)->enabledExtensionCount = ext_count;
   }

   struct vkr_device *dev =
      vkr_context_alloc_object(ctx, sizeof(*dev), VK_OBJECT_TYPE_DEVICE, args->pDevice);
   if (!dev) {
      args->ret = VK_ERROR_OUT_OF_HOST_MEMORY;
      free(exts);
      return;
   }

   vn_replace_vkCreateDevice_args_handle(args);
   args->ret = vkCreateDevice(args->physicalDevice, args->pCreateInfo, NULL,
                              &dev->base.handle.device);
   if (args->ret != VK_SUCCESS) {
      free(exts);
      free(dev);
      return;
   }

   dev->physical_device = physical_dev;

   vkr_device_init_proc_table(dev, physical_dev->api_version,
                              args->pCreateInfo->ppEnabledExtensionNames,
                              args->pCreateInfo->enabledExtensionCount);

   free(exts);

   args->ret = vkr_device_create_queues(ctx, dev, args->pCreateInfo->queueCreateInfoCount,
                                        args->pCreateInfo->pQueueCreateInfos);
   if (args->ret != VK_SUCCESS) {
      vkDestroyDevice(dev->base.handle.device, NULL);
      free(dev);
      return;
   }

   mtx_init(&dev->free_sync_mutex, mtx_plain);
   list_inithead(&dev->free_syncs);

   list_inithead(&dev->objects);

   list_add(&dev->base.track_head, &physical_dev->devices);

   vkr_context_add_object(ctx, &dev->base);
}

static void
vkr_device_object_destroy(struct vkr_context *ctx,
                          struct vkr_device *dev,
                          struct vkr_object *obj)
{
   struct vn_device_proc_table *vk = &dev->proc_table;
   VkDevice device = dev->base.handle.device;

   assert(vkr_device_should_track_object(obj));

   switch (obj->type) {
   case VK_OBJECT_TYPE_SEMAPHORE:
      vk->DestroySemaphore(device, obj->handle.semaphore, NULL);
      break;
   case VK_OBJECT_TYPE_FENCE:
      vk->DestroyFence(device, obj->handle.fence, NULL);
      break;
   case VK_OBJECT_TYPE_DEVICE_MEMORY:
      vk->FreeMemory(device, obj->handle.device_memory, NULL);
      vkr_device_memory_release((struct vkr_device_memory *)obj);
      break;
   case VK_OBJECT_TYPE_BUFFER:
      vk->DestroyBuffer(device, obj->handle.buffer, NULL);
      break;
   case VK_OBJECT_TYPE_IMAGE:
      vk->DestroyImage(device, obj->handle.image, NULL);
      break;
   case VK_OBJECT_TYPE_EVENT:
      vk->DestroyEvent(device, obj->handle.event, NULL);
      break;
   case VK_OBJECT_TYPE_QUERY_POOL:
      vk->DestroyQueryPool(device, obj->handle.query_pool, NULL);
      break;
   case VK_OBJECT_TYPE_BUFFER_VIEW:
      vk->DestroyBufferView(device, obj->handle.buffer_view, NULL);
      break;
   case VK_OBJECT_TYPE_IMAGE_VIEW:
      vk->DestroyImageView(device, obj->handle.image_view, NULL);
      break;
   case VK_OBJECT_TYPE_SHADER_MODULE:
      vk->DestroyShaderModule(device, obj->handle.shader_module, NULL);
      break;
   case VK_OBJECT_TYPE_PIPELINE_CACHE:
      vk->DestroyPipelineCache(device, obj->handle.pipeline_cache, NULL);
      break;
   case VK_OBJECT_TYPE_PIPELINE_LAYOUT:
      vk->DestroyPipelineLayout(device, obj->handle.pipeline_layout, NULL);
      break;
   case VK_OBJECT_TYPE_RENDER_PASS:
      vk->DestroyRenderPass(device, obj->handle.render_pass, NULL);
      break;
   case VK_OBJECT_TYPE_PIPELINE:
      vk->DestroyPipeline(device, obj->handle.pipeline, NULL);
      break;
   case VK_OBJECT_TYPE_DESCRIPTOR_SET_LAYOUT:
      vk->DestroyDescriptorSetLayout(device, obj->handle.descriptor_set_layout, NULL);
      break;
   case VK_OBJECT_TYPE_SAMPLER:
      vk->DestroySampler(device, obj->handle.sampler, NULL);
      break;
   case VK_OBJECT_TYPE_DESCRIPTOR_POOL: {
      /* Destroying VkDescriptorPool frees all VkDescriptorSet allocated inside. */
      vk->DestroyDescriptorPool(device, obj->handle.descriptor_pool, NULL);
      vkr_descriptor_pool_release(ctx, (struct vkr_descriptor_pool *)obj);
      break;
   }
   case VK_OBJECT_TYPE_FRAMEBUFFER:
      vk->DestroyFramebuffer(device, obj->handle.framebuffer, NULL);
      break;
   case VK_OBJECT_TYPE_COMMAND_POOL: {
      /* Destroying VkCommandPool frees all VkCommandBuffer allocated inside. */
      vk->DestroyCommandPool(device, obj->handle.command_pool, NULL);
      vkr_command_pool_release(ctx, (struct vkr_command_pool *)obj);
      break;
   }
   case VK_OBJECT_TYPE_SAMPLER_YCBCR_CONVERSION:
      vk->DestroySamplerYcbcrConversion(device, obj->handle.sampler_ycbcr_conversion,
                                        NULL);
      break;
   case VK_OBJECT_TYPE_DESCRIPTOR_UPDATE_TEMPLATE:
      vk->DestroyDescriptorUpdateTemplate(device, obj->handle.descriptor_update_template,
                                          NULL);
      break;
   default:
      vkr_log("Unhandled vkr_object(%p) with VkObjectType(%u)", obj, (uint32_t)obj->type);
      assert(false);
      break;
   };

   vkr_device_remove_object(ctx, dev, obj);
}

void
vkr_device_destroy(struct vkr_context *ctx, struct vkr_device *dev)
{
   struct vn_device_proc_table *vk = &dev->proc_table;
   VkDevice device = dev->base.handle.device;

   if (!LIST_IS_EMPTY(&dev->objects))
      vkr_log("destroying device with valid objects");

   VkResult result = vk->DeviceWaitIdle(device);
   if (result != VK_SUCCESS)
      vkr_log("vkDeviceWaitIdle(%p) failed(%d)", dev, (int32_t)result);

   if (!LIST_IS_EMPTY(&dev->objects)) {
      struct vkr_object *obj, *obj_tmp;
      LIST_FOR_EACH_ENTRY_SAFE (obj, obj_tmp, &dev->objects, track_head)
         vkr_device_object_destroy(ctx, dev, obj);
   }

   struct vkr_queue *queue, *queue_tmp;
   LIST_FOR_EACH_ENTRY_SAFE (queue, queue_tmp, &dev->queues, base.track_head)
      vkr_queue_destroy(ctx, queue);

   struct vkr_queue_sync *sync, *sync_tmp;
   LIST_FOR_EACH_ENTRY_SAFE (sync, sync_tmp, &dev->free_syncs, head) {
      vk->DestroyFence(dev->base.handle.device, sync->fence, NULL);
      free(sync);
   }

   mtx_destroy(&dev->free_sync_mutex);

   vk->DestroyDevice(device, NULL);

   list_del(&dev->base.track_head);

   vkr_context_remove_object(ctx, &dev->base);
}

static void
vkr_dispatch_vkDestroyDevice(struct vn_dispatch_context *dispatch,
                             struct vn_command_vkDestroyDevice *args)
{
   struct vkr_context *ctx = dispatch->data;

   struct vkr_device *dev = vkr_device_from_handle(args->device);
   /* this never happens */
   if (!dev)
      return;

   vkr_device_destroy(ctx, dev);
}

static void
vkr_dispatch_vkGetDeviceGroupPeerMemoryFeatures(
   UNUSED struct vn_dispatch_context *dispatch,
   struct vn_command_vkGetDeviceGroupPeerMemoryFeatures *args)
{
   struct vkr_device *dev = vkr_device_from_handle(args->device);
   struct vn_device_proc_table *vk = &dev->proc_table;

   vn_replace_vkGetDeviceGroupPeerMemoryFeatures_args_handle(args);
   vk->GetDeviceGroupPeerMemoryFeatures(args->device, args->heapIndex,
                                        args->localDeviceIndex, args->remoteDeviceIndex,
                                        args->pPeerMemoryFeatures);
}

static void
vkr_dispatch_vkDeviceWaitIdle(struct vn_dispatch_context *dispatch,
                              UNUSED struct vn_command_vkDeviceWaitIdle *args)
{
   struct vkr_context *ctx = dispatch->data;
   /* no blocking call */
   vkr_cs_decoder_set_fatal(&ctx->decoder);
}

static void
vkr_dispatch_vkGetCalibratedTimestampsEXT(
   UNUSED struct vn_dispatch_context *ctx,
   struct vn_command_vkGetCalibratedTimestampsEXT *args)
{
   struct vkr_device *dev = vkr_device_from_handle(args->device);
   struct vn_device_proc_table *vk = &dev->proc_table;

   vn_replace_vkGetCalibratedTimestampsEXT_args_handle(args);
   args->ret = vk->GetCalibratedTimestampsEXT(args->device, args->timestampCount,
                                              args->pTimestampInfos, args->pTimestamps,
                                              args->pMaxDeviation);
}

void
vkr_context_init_device_dispatch(struct vkr_context *ctx)
{
   struct vn_dispatch_context *dispatch = &ctx->dispatch;

   dispatch->dispatch_vkCreateDevice = vkr_dispatch_vkCreateDevice;
   dispatch->dispatch_vkDestroyDevice = vkr_dispatch_vkDestroyDevice;
   dispatch->dispatch_vkGetDeviceProcAddr = NULL;
   dispatch->dispatch_vkGetDeviceGroupPeerMemoryFeatures =
      vkr_dispatch_vkGetDeviceGroupPeerMemoryFeatures;
   dispatch->dispatch_vkDeviceWaitIdle = vkr_dispatch_vkDeviceWaitIdle;
   dispatch->dispatch_vkGetCalibratedTimestampsEXT =
      vkr_dispatch_vkGetCalibratedTimestampsEXT;
}
