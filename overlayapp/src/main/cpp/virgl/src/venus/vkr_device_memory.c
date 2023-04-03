/*
 * Copyright 2020 Google LLC
 * SPDX-License-Identifier: MIT
 */

#include "vkr_device_memory.h"

#include <gbm.h>

#include "venus-protocol/vn_protocol_renderer_transport.h"

#include "vkr_device_memory_gen.h"
#include "vkr_physical_device.h"

static bool
vkr_get_fd_handle_type_from_virgl_fd_type(
   struct vkr_physical_device *dev,
   enum virgl_resource_fd_type fd_type,
   VkExternalMemoryHandleTypeFlagBits *out_handle_type)
{
   assert(dev);
   assert(out_handle_type);

   switch (fd_type) {
   case VIRGL_RESOURCE_FD_DMABUF:
      if (!dev->EXT_external_memory_dma_buf)
         return false;
      *out_handle_type = VK_EXTERNAL_MEMORY_HANDLE_TYPE_DMA_BUF_BIT_EXT;
      break;
   case VIRGL_RESOURCE_FD_OPAQUE:
      if (!dev->KHR_external_memory_fd)
         return false;
      *out_handle_type = VK_EXTERNAL_MEMORY_HANDLE_TYPE_OPAQUE_FD_BIT;
      break;
   default:
      return false;
   }

   return true;
}

static bool
vkr_get_fd_info_from_resource_info(struct vkr_context *ctx,
                                   struct vkr_physical_device *physical_dev,
                                   const VkImportMemoryResourceInfoMESA *res_info,
                                   VkImportMemoryFdInfoKHR *out)
{
   struct vkr_resource_attachment *att = NULL;
   enum virgl_resource_fd_type fd_type;
   int fd = -1;
   VkExternalMemoryHandleTypeFlagBits handle_type;

   att = vkr_context_get_resource(ctx, res_info->resourceId);
   if (!att) {
      vkr_log("failed to import resource: invalid res_id %u", res_info->resourceId);
      vkr_cs_decoder_set_fatal(&ctx->decoder);
      return false;
   }

   fd_type = virgl_resource_export_fd(att->resource, &fd);
   if (fd_type == VIRGL_RESOURCE_FD_INVALID)
      return false;

   if (!vkr_get_fd_handle_type_from_virgl_fd_type(physical_dev, fd_type, &handle_type)) {
      close(fd);
      return false;
   }

   *out = (VkImportMemoryFdInfoKHR){
      .sType = VK_STRUCTURE_TYPE_IMPORT_MEMORY_FD_INFO_KHR,
      .pNext = res_info->pNext,
      .fd = fd,
      .handleType = handle_type,
   };
   return true;
}

static VkResult
vkr_get_fd_info_from_allocation_info(struct vkr_physical_device *physical_dev,
                                     const VkMemoryAllocateInfo *alloc_info,
                                     struct gbm_bo **out_gbm_bo,
                                     VkImportMemoryFdInfoKHR *out_fd_info)
{
#ifdef MINIGBM
   const uint32_t gbm_bo_use_flags =
      GBM_BO_USE_LINEAR | GBM_BO_USE_SW_READ_RARELY | GBM_BO_USE_SW_WRITE_RARELY;
#else
   const uint32_t gbm_bo_use_flags = GBM_BO_USE_LINEAR;
#endif

   struct gbm_bo *gbm_bo;
   int fd = -1;

   assert(physical_dev->gbm_device);

   /*
    * Reject here for simplicity. Letting VkPhysicalDeviceVulkan11Properties return
    * min(maxMemoryAllocationSize, UINT32_MAX) will affect unmappable scenarios.
    */
   if (alloc_info->allocationSize > UINT32_MAX)
      return VK_ERROR_OUT_OF_DEVICE_MEMORY;

   /* 4K alignment is used on all implementations we support. */
   gbm_bo =
      gbm_bo_create(physical_dev->gbm_device, align(alloc_info->allocationSize, 4096), 1,
                    GBM_FORMAT_R8, gbm_bo_use_flags);
   if (!gbm_bo)
      return VK_ERROR_OUT_OF_DEVICE_MEMORY;

   /* gbm_bo_get_fd returns negative error code on failure */
   fd = gbm_bo_get_fd(gbm_bo);
   if (fd < 0) {
      gbm_bo_destroy(gbm_bo);
      return fd == -EMFILE ? VK_ERROR_TOO_MANY_OBJECTS : VK_ERROR_OUT_OF_HOST_MEMORY;
   }

   *out_gbm_bo = gbm_bo;
   *out_fd_info = (VkImportMemoryFdInfoKHR){
      .sType = VK_STRUCTURE_TYPE_IMPORT_MEMORY_FD_INFO_KHR,
      .pNext = alloc_info->pNext,
      .fd = fd,
      .handleType = VK_EXTERNAL_MEMORY_HANDLE_TYPE_DMA_BUF_BIT_EXT,
   };
   return VK_SUCCESS;
}

static void
vkr_dispatch_vkAllocateMemory(struct vn_dispatch_context *dispatch,
                              struct vn_command_vkAllocateMemory *args)
{
   struct vkr_context *ctx = dispatch->data;
   struct vkr_device *dev = vkr_device_from_handle(args->device);
   struct vkr_physical_device *physical_dev = dev->physical_device;
   VkBaseInStructure *prev_of_res_info = NULL;
   VkImportMemoryResourceInfoMESA *res_info = NULL;
   VkImportMemoryFdInfoKHR local_import_info = { .fd = -1 };
   VkExportMemoryAllocateInfo *export_info = vkr_find_struct(
      args->pAllocateInfo->pNext, VK_STRUCTURE_TYPE_EXPORT_MEMORY_ALLOCATE_INFO);
   const bool no_dma_buf_export =
      !export_info ||
      !(export_info->handleTypes & VK_EXTERNAL_MEMORY_HANDLE_TYPE_DMA_BUF_BIT_EXT);
   struct vkr_device_memory *mem = NULL;
   const uint32_t mem_type_index = args->pAllocateInfo->memoryTypeIndex;
   const uint32_t property_flags =
      physical_dev->memory_properties.memoryTypes[mem_type_index].propertyFlags;
   uint32_t valid_fd_types = 0;
   struct gbm_bo *gbm_bo = NULL;

   /* translate VkImportMemoryResourceInfoMESA into VkImportMemoryFdInfoKHR in place */
   prev_of_res_info = vkr_find_prev_struct(
      args->pAllocateInfo, VK_STRUCTURE_TYPE_IMPORT_MEMORY_RESOURCE_INFO_MESA);
   if (prev_of_res_info) {
      res_info = (VkImportMemoryResourceInfoMESA *)prev_of_res_info->pNext;
      if (!vkr_get_fd_info_from_resource_info(ctx, physical_dev, res_info,
                                              &local_import_info)) {
         args->ret = VK_ERROR_INVALID_EXTERNAL_HANDLE;
         return;
      }

      prev_of_res_info->pNext = (const struct VkBaseInStructure *)&local_import_info;
   }

   /* XXX Force dma_buf/opaque fd export or gbm bo import until a new extension that
    * supports direct export from host visible memory
    *
    * Most VkImage and VkBuffer are non-external while most VkDeviceMemory are external
    * if allocated with a host visible memory type. We still violate the spec by binding
    * external memory to non-external image or buffer, which needs spec changes with a
    * new extension.
    *
    * Skip forcing external if a valid VkImportMemoryResourceInfoMESA is provided, since
    * the mapping will be directly set up from the existing virgl resource.
    */
   VkExportMemoryAllocateInfo local_export_info;
   if ((property_flags & VK_MEMORY_PROPERTY_HOST_VISIBLE_BIT) && !res_info) {
      /* An implementation can support dma_buf import along with opaque fd export/import.
       * If the client driver is using external memory and requesting dma_buf, without
       * dma_buf fd export support, we must use gbm bo import path instead of forcing
       * opaque fd export. e.g. the client driver uses external memory for wsi image.
       */
      if (dev->physical_device->is_dma_buf_fd_export_supported ||
          (dev->physical_device->is_opaque_fd_export_supported && no_dma_buf_export)) {
         VkExternalMemoryHandleTypeFlagBits handle_type =
            dev->physical_device->is_dma_buf_fd_export_supported
               ? VK_EXTERNAL_MEMORY_HANDLE_TYPE_DMA_BUF_BIT_EXT
               : VK_EXTERNAL_MEMORY_HANDLE_TYPE_OPAQUE_FD_BIT;
         if (export_info) {
            export_info->handleTypes |= handle_type;
         } else {
            local_export_info = (const VkExportMemoryAllocateInfo){
               .sType = VK_STRUCTURE_TYPE_EXPORT_MEMORY_ALLOCATE_INFO,
               .pNext = args->pAllocateInfo->pNext,
               .handleTypes = handle_type,
            };
            export_info = &local_export_info;
            ((VkMemoryAllocateInfo *)args->pAllocateInfo)->pNext = &local_export_info;
         }
      } else if (dev->physical_device->EXT_external_memory_dma_buf) {
         /* Allocate gbm bo to force dma_buf fd import. */
         VkResult result;

         if (export_info) {
            /* Strip export info since valid_fd_types can only be dma_buf here. */
            VkBaseInStructure *prev_of_export_info = vkr_find_prev_struct(
               args->pAllocateInfo, VK_STRUCTURE_TYPE_EXPORT_MEMORY_ALLOCATE_INFO);

            prev_of_export_info->pNext = export_info->pNext;
            export_info = NULL;
         }

         result = vkr_get_fd_info_from_allocation_info(physical_dev, args->pAllocateInfo,
                                                       &gbm_bo, &local_import_info);
         if (result != VK_SUCCESS) {
            args->ret = result;
            return;
         }

         ((VkMemoryAllocateInfo *)args->pAllocateInfo)->pNext = &local_import_info;

         valid_fd_types = 1 << VIRGL_RESOURCE_FD_DMABUF;
      }
   }

   if (export_info) {
      if (export_info->handleTypes & VK_EXTERNAL_MEMORY_HANDLE_TYPE_OPAQUE_FD_BIT)
         valid_fd_types |= 1 << VIRGL_RESOURCE_FD_OPAQUE;
      if (export_info->handleTypes & VK_EXTERNAL_MEMORY_HANDLE_TYPE_DMA_BUF_BIT_EXT)
         valid_fd_types |= 1 << VIRGL_RESOURCE_FD_DMABUF;
   }

   mem = vkr_device_memory_create_and_add(ctx, args);
   if (!mem) {
      if (local_import_info.fd >= 0)
         close(local_import_info.fd);
      if (gbm_bo)
         gbm_bo_destroy(gbm_bo);
      return;
   }

   mem->device = dev;
   mem->property_flags = property_flags;
   mem->valid_fd_types = valid_fd_types;
   mem->gbm_bo = gbm_bo;
   mem->allocation_size = args->pAllocateInfo->allocationSize;
   mem->memory_type_index = mem_type_index;
}

static void
vkr_dispatch_vkFreeMemory(struct vn_dispatch_context *dispatch,
                          struct vn_command_vkFreeMemory *args)
{
   struct vkr_device_memory *mem = vkr_device_memory_from_handle(args->memory);
   if (!mem)
      return;

   vkr_device_memory_release(mem);
   vkr_device_memory_destroy_and_remove(dispatch->data, args);
}

static void
vkr_dispatch_vkGetDeviceMemoryCommitment(
   UNUSED struct vn_dispatch_context *dispatch,
   struct vn_command_vkGetDeviceMemoryCommitment *args)
{
   struct vkr_device *dev = vkr_device_from_handle(args->device);
   struct vn_device_proc_table *vk = &dev->proc_table;

   vn_replace_vkGetDeviceMemoryCommitment_args_handle(args);
   vk->GetDeviceMemoryCommitment(args->device, args->memory,
                                 args->pCommittedMemoryInBytes);
}

static void
vkr_dispatch_vkGetDeviceMemoryOpaqueCaptureAddress(
   UNUSED struct vn_dispatch_context *dispatch,
   struct vn_command_vkGetDeviceMemoryOpaqueCaptureAddress *args)
{
   struct vkr_device *dev = vkr_device_from_handle(args->device);
   struct vn_device_proc_table *vk = &dev->proc_table;

   vn_replace_vkGetDeviceMemoryOpaqueCaptureAddress_args_handle(args);
   args->ret = vk->GetDeviceMemoryOpaqueCaptureAddress(args->device, args->pInfo);
}

static void
vkr_dispatch_vkGetMemoryResourcePropertiesMESA(
   struct vn_dispatch_context *dispatch,
   struct vn_command_vkGetMemoryResourcePropertiesMESA *args)
{
   struct vkr_context *ctx = dispatch->data;
   struct vkr_device *dev = vkr_device_from_handle(args->device);
   struct vn_device_proc_table *vk = &dev->proc_table;

   struct vkr_resource_attachment *att = vkr_context_get_resource(ctx, args->resourceId);
   if (!att) {
      vkr_log("failed to query resource props: invalid res_id %u", args->resourceId);
      vkr_cs_decoder_set_fatal(&ctx->decoder);
      return;
   }

   int fd = -1;
   enum virgl_resource_fd_type fd_type = virgl_resource_export_fd(att->resource, &fd);
   VkExternalMemoryHandleTypeFlagBits handle_type;
   if (!vkr_get_fd_handle_type_from_virgl_fd_type(dev->physical_device, fd_type,
                                                  &handle_type) ||
       handle_type != VK_EXTERNAL_MEMORY_HANDLE_TYPE_DMA_BUF_BIT_EXT) {
      close(fd);
      args->ret = VK_ERROR_INVALID_EXTERNAL_HANDLE;
      return;
   }

   VkMemoryFdPropertiesKHR mem_fd_props = {
      .sType = VK_STRUCTURE_TYPE_MEMORY_FD_PROPERTIES_KHR,
      .pNext = NULL,
      .memoryTypeBits = 0,
   };
   vn_replace_vkGetMemoryResourcePropertiesMESA_args_handle(args);
   args->ret = vk->GetMemoryFdPropertiesKHR(args->device, handle_type, fd, &mem_fd_props);
   if (args->ret != VK_SUCCESS) {
      close(fd);
      return;
   }

   args->pMemoryResourceProperties->memoryTypeBits = mem_fd_props.memoryTypeBits;

   VkMemoryResourceAllocationSizeProperties100000MESA *alloc_size_props = vkr_find_struct(
      args->pMemoryResourceProperties->pNext,
      VK_STRUCTURE_TYPE_MEMORY_RESOURCE_ALLOCATION_SIZE_PROPERTIES_100000_MESA);
   if (alloc_size_props)
      alloc_size_props->allocationSize = lseek(fd, 0, SEEK_END);

   close(fd);
}

void
vkr_context_init_device_memory_dispatch(struct vkr_context *ctx)
{
   struct vn_dispatch_context *dispatch = &ctx->dispatch;

   dispatch->dispatch_vkAllocateMemory = vkr_dispatch_vkAllocateMemory;
   dispatch->dispatch_vkFreeMemory = vkr_dispatch_vkFreeMemory;
   dispatch->dispatch_vkMapMemory = NULL;
   dispatch->dispatch_vkUnmapMemory = NULL;
   dispatch->dispatch_vkFlushMappedMemoryRanges = NULL;
   dispatch->dispatch_vkInvalidateMappedMemoryRanges = NULL;
   dispatch->dispatch_vkGetDeviceMemoryCommitment =
      vkr_dispatch_vkGetDeviceMemoryCommitment;
   dispatch->dispatch_vkGetDeviceMemoryOpaqueCaptureAddress =
      vkr_dispatch_vkGetDeviceMemoryOpaqueCaptureAddress;

   dispatch->dispatch_vkGetMemoryResourcePropertiesMESA =
      vkr_dispatch_vkGetMemoryResourcePropertiesMESA;
}

void
vkr_device_memory_release(struct vkr_device_memory *mem)
{
   if (mem->gbm_bo)
      gbm_bo_destroy(mem->gbm_bo);
}

int
vkr_device_memory_export_fd(struct vkr_device_memory *mem,
                            VkExternalMemoryHandleTypeFlagBits handle_type,
                            int *out_fd)
{
   struct vn_device_proc_table *vk = &mem->device->proc_table;
   int fd = -1;

   if (mem->gbm_bo) {
      /* mem->gbm_bo is a gbm bo backing non-external mappable memory */
      assert((handle_type == VK_EXTERNAL_MEMORY_HANDLE_TYPE_DMA_BUF_BIT_EXT) &&
             (mem->valid_fd_types == 1 << VIRGL_RESOURCE_FD_DMABUF));

      /* gbm_bo_get_fd returns negative error code on failure */
      fd = gbm_bo_get_fd(mem->gbm_bo);
      if (fd < 0)
         return fd;
   } else {
      VkDevice dev_handle = mem->device->base.handle.device;
      VkDeviceMemory mem_handle = mem->base.handle.device_memory;
      const VkMemoryGetFdInfoKHR fd_info = {
         .sType = VK_STRUCTURE_TYPE_MEMORY_GET_FD_INFO_KHR,
         .memory = mem_handle,
         .handleType = handle_type,
      };
      VkResult result = vk->GetMemoryFdKHR(dev_handle, &fd_info, &fd);
      if (result != VK_SUCCESS)
         return result == VK_ERROR_TOO_MANY_OBJECTS ? -EMFILE : -ENOMEM;
   }

   *out_fd = fd;
   return 0;
}
