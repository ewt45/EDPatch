/*
 * Copyright 2020 Google LLC
 * SPDX-License-Identifier: MIT
 */

#include "vkr_physical_device.h"

#include "venus-protocol/vn_protocol_renderer_device.h"
#include "vrend_winsys_gbm.h"

#include "vkr_context.h"
#include "vkr_device.h"
#include "vkr_instance.h"

/* TODO open render node and create gbm_device per vkr_physical_device */
static struct gbm_device *vkr_gbm_dev;

static void
vkr_gbm_device_init_once(void)
{
   struct virgl_gbm *vkr_gbm = virgl_gbm_init(-1);
   if (!vkr_gbm) {
      vkr_log("virgl_gbm_init failed");
      exit(-1);
   }

   vkr_gbm_dev = vkr_gbm->device;
}

static struct gbm_device *
vkr_physical_device_get_gbm_device(UNUSED struct vkr_physical_device *physical_dev)
{
   static once_flag gbm_once_flag = ONCE_FLAG_INIT;
   call_once(&gbm_once_flag, vkr_gbm_device_init_once);

   return vkr_gbm_dev;
}

void
vkr_physical_device_destroy(struct vkr_context *ctx,
                            struct vkr_physical_device *physical_dev)
{
   struct vkr_device *dev, *tmp;
   LIST_FOR_EACH_ENTRY_SAFE (dev, tmp, &physical_dev->devices, base.track_head)
      vkr_device_destroy(ctx, dev);

   free(physical_dev->extensions);

   vkr_context_remove_object(ctx, &physical_dev->base);
}

static VkResult
vkr_instance_enumerate_physical_devices(struct vkr_instance *instance)
{
   if (instance->physical_device_count)
      return VK_SUCCESS;

   uint32_t count;
   VkResult result =
      vkEnumeratePhysicalDevices(instance->base.handle.instance, &count, NULL);
   if (result != VK_SUCCESS)
      return result;

   VkPhysicalDevice *handles = calloc(count, sizeof(*handles));
   struct vkr_physical_device **physical_devs = calloc(count, sizeof(*physical_devs));
   if (!handles || !physical_devs) {
      free(physical_devs);
      free(handles);
      return VK_ERROR_OUT_OF_HOST_MEMORY;
   }

   result = vkEnumeratePhysicalDevices(instance->base.handle.instance, &count, handles);
   if (result != VK_SUCCESS) {
      free(physical_devs);
      free(handles);
      return result;
   }

   instance->physical_device_count = count;
   instance->physical_device_handles = handles;
   instance->physical_devices = physical_devs;

   return VK_SUCCESS;
}

static struct vkr_physical_device *
vkr_instance_lookup_physical_device(struct vkr_instance *instance,
                                    VkPhysicalDevice handle)
{
   for (uint32_t i = 0; i < instance->physical_device_count; i++) {
      /* XXX this assumes VkPhysicalDevice handles are unique */
      if (instance->physical_device_handles[i] == handle)
         return instance->physical_devices[i];
   }
   return NULL;
}

static void
vkr_physical_device_init_id_properties(struct vkr_physical_device *physical_dev)
{
   VkPhysicalDevice handle = physical_dev->base.handle.physical_device;
   physical_dev->id_properties.sType = VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_ID_PROPERTIES;
   VkPhysicalDeviceProperties2 props2 = {
      .sType = VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_PROPERTIES_2,
      .pNext = &physical_dev->id_properties
   };
   vkGetPhysicalDeviceProperties2(handle, &props2);
}

static void
vkr_physical_device_init_memory_properties(struct vkr_physical_device *physical_dev)
{
   VkPhysicalDevice handle = physical_dev->base.handle.physical_device;
   vkGetPhysicalDeviceMemoryProperties(handle, &physical_dev->memory_properties);

   /* XXX When a VkMemoryType has VK_MEMORY_PROPERTY_HOST_VISIBLE_BIT, we
    * assume any VkDeviceMemory with the memory type can be made external and
    * be exportable.  That is incorrect but is what we have to live with with
    * the existing external memory extensions.
    *
    * The main reason is that the external memory extensions require us to use
    * vkGetPhysicalDeviceExternalBufferProperties or
    * vkGetPhysicalDeviceImageFormatProperties2 to determine if we can
    * allocate an exportable external VkDeviceMemory.  But we normally do not
    * have the info to make the queries during vkAllocateMemory.
    *
    * We only have VkMemoryAllocateInfo during vkAllocateMemory.  The only
    * useful info in the struct is the memory type.  What we need is thus an
    * extension that tells us that, given a memory type, if all VkDeviceMemory
    * with the memory type is exportable.  If we had the extension, we could
    * filter out VK_MEMORY_PROPERTY_HOST_VISIBLE_BIT here if a memory type is
    * not always exportable.
    */

   /* XXX is_dma_buf_fd_export_supported and is_opaque_fd_export_supported
    * needs to be filled with a new extension which supports query fd export
    * against the raw memory types. Currently, we workaround by checking
    * external buffer properties before force-enabling either dma_buf or opaque
    * fd path of device memory allocation.
    */
   physical_dev->is_dma_buf_fd_export_supported = false;
   physical_dev->is_opaque_fd_export_supported = false;

   VkPhysicalDeviceExternalBufferInfo info = {
      .sType = VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_EXTERNAL_BUFFER_INFO,
      .usage = VK_BUFFER_USAGE_TRANSFER_SRC_BIT,
   };
   VkExternalBufferProperties props = {
      .sType = VK_STRUCTURE_TYPE_EXTERNAL_BUFFER_PROPERTIES,
   };

   if (physical_dev->EXT_external_memory_dma_buf) {
      info.handleType = VK_EXTERNAL_MEMORY_HANDLE_TYPE_DMA_BUF_BIT_EXT,
      vkGetPhysicalDeviceExternalBufferProperties(handle, &info, &props);
      physical_dev->is_dma_buf_fd_export_supported =
         (props.externalMemoryProperties.externalMemoryFeatures &
          VK_EXTERNAL_MEMORY_FEATURE_EXPORTABLE_BIT) &&
         (props.externalMemoryProperties.exportFromImportedHandleTypes &
          VK_EXTERNAL_MEMORY_HANDLE_TYPE_DMA_BUF_BIT_EXT);
   }

   if (physical_dev->KHR_external_memory_fd) {
      info.handleType = VK_EXTERNAL_MEMORY_HANDLE_TYPE_OPAQUE_FD_BIT,
      vkGetPhysicalDeviceExternalBufferProperties(handle, &info, &props);
      physical_dev->is_opaque_fd_export_supported =
         (props.externalMemoryProperties.externalMemoryFeatures &
          VK_EXTERNAL_MEMORY_FEATURE_EXPORTABLE_BIT) &&
         (props.externalMemoryProperties.exportFromImportedHandleTypes &
          VK_EXTERNAL_MEMORY_HANDLE_TYPE_OPAQUE_FD_BIT);
   }

   if (!physical_dev->is_dma_buf_fd_export_supported &&
       !physical_dev->is_opaque_fd_export_supported)
      physical_dev->gbm_device = vkr_physical_device_get_gbm_device(physical_dev);
}

static void
vkr_physical_device_init_extensions(struct vkr_physical_device *physical_dev,
                                    struct vkr_instance *instance)
{
   VkPhysicalDevice handle = physical_dev->base.handle.physical_device;

   VkExtensionProperties *exts;
   uint32_t count;
   VkResult result = vkEnumerateDeviceExtensionProperties(handle, NULL, &count, NULL);
   if (result != VK_SUCCESS)
      return;

   exts = malloc(sizeof(*exts) * count);
   if (!exts)
      return;

   result = vkEnumerateDeviceExtensionProperties(handle, NULL, &count, exts);
   if (result != VK_SUCCESS) {
      free(exts);
      return;
   }

   uint32_t advertised_count = 0;
   for (uint32_t i = 0; i < count; i++) {
      VkExtensionProperties *props = &exts[i];

      if (!strcmp(props->extensionName, "VK_KHR_external_memory_fd"))
         physical_dev->KHR_external_memory_fd = true;
      else if (!strcmp(props->extensionName, "VK_EXT_external_memory_dma_buf"))
         physical_dev->EXT_external_memory_dma_buf = true;
      else if (!strcmp(props->extensionName, "VK_KHR_external_fence_fd"))
         physical_dev->KHR_external_fence_fd = true;

      const uint32_t spec_ver = vkr_extension_get_spec_version(props->extensionName);
      if (spec_ver) {
         if (props->specVersion > spec_ver)
            props->specVersion = spec_ver;
         exts[advertised_count++] = exts[i];
      }
   }

   if (physical_dev->KHR_external_fence_fd) {
      const VkPhysicalDeviceExternalFenceInfo fence_info = {
         .sType = VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_EXTERNAL_FENCE_INFO,
         .handleType = VK_EXTERNAL_FENCE_HANDLE_TYPE_SYNC_FD_BIT,
      };
      VkExternalFenceProperties fence_props = {
         .sType = VK_STRUCTURE_TYPE_EXTERNAL_FENCE_PROPERTIES,
      };
      PFN_vkGetPhysicalDeviceExternalFenceProperties get_fence_props =
         (PFN_vkGetPhysicalDeviceExternalFenceProperties)vkGetInstanceProcAddr(
            instance->base.handle.instance, "vkGetPhysicalDeviceExternalFenceProperties");
      get_fence_props(handle, &fence_info, &fence_props);

      if (!(fence_props.externalFenceFeatures & VK_EXTERNAL_FENCE_FEATURE_EXPORTABLE_BIT))
         physical_dev->KHR_external_fence_fd = false;
   }

   physical_dev->extensions = exts;
   physical_dev->extension_count = advertised_count;
}

static void
vkr_physical_device_init_properties(struct vkr_physical_device *physical_dev)
{
   VkPhysicalDevice handle = physical_dev->base.handle.physical_device;
   vkGetPhysicalDeviceProperties(handle, &physical_dev->properties);

   VkPhysicalDeviceProperties *props = &physical_dev->properties;
   props->apiVersion = vkr_api_version_cap_minor(props->apiVersion, VKR_MAX_API_VERSION);
   props->driverVersion = 0;

   /* TODO lie about props->pipelineCacheUUID and patch cache header */
}

static void
vkr_physical_device_init_proc_table(struct vkr_physical_device *physical_dev,
                                    struct vkr_instance *instance)
{
   vn_util_init_physical_device_proc_table(instance->base.handle.instance,
                                           &physical_dev->proc_table);
}

static void
vkr_dispatch_vkEnumeratePhysicalDevices(struct vn_dispatch_context *dispatch,
                                        struct vn_command_vkEnumeratePhysicalDevices *args)
{
   struct vkr_context *ctx = dispatch->data;

   struct vkr_instance *instance = vkr_instance_from_handle(args->instance);
   if (instance != ctx->instance) {
      vkr_cs_decoder_set_fatal(&ctx->decoder);
      return;
   }

   args->ret = vkr_instance_enumerate_physical_devices(instance);
   if (args->ret != VK_SUCCESS)
      return;

   uint32_t count = instance->physical_device_count;
   if (!args->pPhysicalDevices) {
      *args->pPhysicalDeviceCount = count;
      args->ret = VK_SUCCESS;
      return;
   }

   if (count > *args->pPhysicalDeviceCount) {
      count = *args->pPhysicalDeviceCount;
      args->ret = VK_INCOMPLETE;
   } else {
      *args->pPhysicalDeviceCount = count;
      args->ret = VK_SUCCESS;
   }

   uint32_t i;
   for (i = 0; i < count; i++) {
      struct vkr_physical_device *physical_dev = instance->physical_devices[i];
      const vkr_object_id id = vkr_cs_handle_load_id(
         (const void **)&args->pPhysicalDevices[i], VK_OBJECT_TYPE_PHYSICAL_DEVICE);

      if (physical_dev) {
         if (physical_dev->base.id != id) {
            vkr_cs_decoder_set_fatal(&ctx->decoder);
            break;
         }
         continue;
      }

      if (!vkr_context_validate_object_id(ctx, id))
         break;

      physical_dev =
         vkr_object_alloc(sizeof(*physical_dev), VK_OBJECT_TYPE_PHYSICAL_DEVICE, id);
      if (!physical_dev) {
         args->ret = VK_ERROR_OUT_OF_HOST_MEMORY;
         break;
      }

      physical_dev->base.handle.physical_device = instance->physical_device_handles[i];

      vkr_physical_device_init_proc_table(physical_dev, instance);
      vkr_physical_device_init_properties(physical_dev);
      physical_dev->api_version =
         MIN2(physical_dev->properties.apiVersion, instance->api_version);
      vkr_physical_device_init_extensions(physical_dev, instance);
      vkr_physical_device_init_memory_properties(physical_dev);
      vkr_physical_device_init_id_properties(physical_dev);

      list_inithead(&physical_dev->devices);

      instance->physical_devices[i] = physical_dev;

      vkr_context_add_object(ctx, &physical_dev->base);
   }
   /* remove all physical devices on errors */
   if (i < count) {
      for (i = 0; i < instance->physical_device_count; i++) {
         struct vkr_physical_device *physical_dev = instance->physical_devices[i];
         if (!physical_dev)
            break;
         free(physical_dev->extensions);
         vkr_context_remove_object(ctx, &physical_dev->base);
         instance->physical_devices[i] = NULL;
      }
   }
}

static void
vkr_dispatch_vkEnumeratePhysicalDeviceGroups(
   struct vn_dispatch_context *dispatch,
   struct vn_command_vkEnumeratePhysicalDeviceGroups *args)
{
   struct vkr_context *ctx = dispatch->data;

   struct vkr_instance *instance = vkr_instance_from_handle(args->instance);
   if (instance != ctx->instance) {
      vkr_cs_decoder_set_fatal(&ctx->decoder);
      return;
   }

   args->ret = vkr_instance_enumerate_physical_devices(instance);
   if (args->ret != VK_SUCCESS)
      return;

   VkPhysicalDeviceGroupProperties *orig_props = args->pPhysicalDeviceGroupProperties;
   if (orig_props) {
      args->pPhysicalDeviceGroupProperties =
         calloc(*args->pPhysicalDeviceGroupCount, sizeof(*orig_props));
      if (!args->pPhysicalDeviceGroupProperties) {
         args->ret = VK_ERROR_OUT_OF_HOST_MEMORY;
         return;
      }

      for (uint32_t i = 0; i < *args->pPhysicalDeviceGroupCount; i++) {
         args->pPhysicalDeviceGroupProperties[i].sType =
            VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_GROUP_PROPERTIES;
      }
   }

   vn_replace_vkEnumeratePhysicalDeviceGroups_args_handle(args);
   args->ret =
      vkEnumeratePhysicalDeviceGroups(args->instance, args->pPhysicalDeviceGroupCount,
                                      args->pPhysicalDeviceGroupProperties);
   if (args->ret != VK_SUCCESS)
      return;

   if (!orig_props)
      return;

   /* XXX this assumes vkEnumeratePhysicalDevices is called first */
   /* replace VkPhysicalDevice handles by object ids */
   for (uint32_t i = 0; i < *args->pPhysicalDeviceGroupCount; i++) {
      const VkPhysicalDeviceGroupProperties *props =
         &args->pPhysicalDeviceGroupProperties[i];
      VkPhysicalDeviceGroupProperties *out = &orig_props[i];

      out->physicalDeviceCount = props->physicalDeviceCount;
      out->subsetAllocation = props->subsetAllocation;
      for (uint32_t j = 0; j < props->physicalDeviceCount; j++) {
         const struct vkr_physical_device *physical_dev =
            vkr_instance_lookup_physical_device(instance, props->physicalDevices[j]);
         vkr_cs_handle_store_id((void **)&out->physicalDevices[j], physical_dev->base.id,
                                VK_OBJECT_TYPE_PHYSICAL_DEVICE);
      }
   }

   free(args->pPhysicalDeviceGroupProperties);
   args->pPhysicalDeviceGroupProperties = orig_props;
}

static void
vkr_dispatch_vkEnumerateDeviceExtensionProperties(
   struct vn_dispatch_context *dispatch,
   struct vn_command_vkEnumerateDeviceExtensionProperties *args)
{
   struct vkr_context *ctx = dispatch->data;

   struct vkr_physical_device *physical_dev =
      vkr_physical_device_from_handle(args->physicalDevice);
   if (args->pLayerName) {
      vkr_cs_decoder_set_fatal(&ctx->decoder);
      return;
   }

   if (!args->pProperties) {
      *args->pPropertyCount = physical_dev->extension_count;
      args->ret = VK_SUCCESS;
      return;
   }

   uint32_t count = physical_dev->extension_count;
   if (count > *args->pPropertyCount) {
      count = *args->pPropertyCount;
      args->ret = VK_INCOMPLETE;
   } else {
      *args->pPropertyCount = count;
      args->ret = VK_SUCCESS;
   }

   memcpy(args->pProperties, physical_dev->extensions,
          sizeof(*args->pProperties) * count);
}

static void
vkr_dispatch_vkGetPhysicalDeviceFeatures(
   UNUSED struct vn_dispatch_context *dispatch,
   struct vn_command_vkGetPhysicalDeviceFeatures *args)
{
   vn_replace_vkGetPhysicalDeviceFeatures_args_handle(args);
   vkGetPhysicalDeviceFeatures(args->physicalDevice, args->pFeatures);
}

static void
vkr_dispatch_vkGetPhysicalDeviceProperties(
   UNUSED struct vn_dispatch_context *dispatch,
   struct vn_command_vkGetPhysicalDeviceProperties *args)
{
   struct vkr_physical_device *physical_dev =
      vkr_physical_device_from_handle(args->physicalDevice);

   *args->pProperties = physical_dev->properties;
}

static void
vkr_dispatch_vkGetPhysicalDeviceQueueFamilyProperties(
   UNUSED struct vn_dispatch_context *dispatch,
   struct vn_command_vkGetPhysicalDeviceQueueFamilyProperties *args)
{
   vn_replace_vkGetPhysicalDeviceQueueFamilyProperties_args_handle(args);
   vkGetPhysicalDeviceQueueFamilyProperties(args->physicalDevice,
                                            args->pQueueFamilyPropertyCount,
                                            args->pQueueFamilyProperties);
}

static void
vkr_dispatch_vkGetPhysicalDeviceMemoryProperties(
   UNUSED struct vn_dispatch_context *dispatch,
   struct vn_command_vkGetPhysicalDeviceMemoryProperties *args)
{
   struct vkr_physical_device *physical_dev =
      vkr_physical_device_from_handle(args->physicalDevice);
   *args->pMemoryProperties = physical_dev->memory_properties;
}

static void
vkr_dispatch_vkGetPhysicalDeviceFormatProperties(
   UNUSED struct vn_dispatch_context *dispatch,
   struct vn_command_vkGetPhysicalDeviceFormatProperties *args)
{
   vn_replace_vkGetPhysicalDeviceFormatProperties_args_handle(args);
   vkGetPhysicalDeviceFormatProperties(args->physicalDevice, args->format,
                                       args->pFormatProperties);
}

static void
vkr_dispatch_vkGetPhysicalDeviceImageFormatProperties(
   UNUSED struct vn_dispatch_context *dispatch,
   struct vn_command_vkGetPhysicalDeviceImageFormatProperties *args)
{
   vn_replace_vkGetPhysicalDeviceImageFormatProperties_args_handle(args);
   args->ret = vkGetPhysicalDeviceImageFormatProperties(
      args->physicalDevice, args->format, args->type, args->tiling, args->usage,
      args->flags, args->pImageFormatProperties);
}

static void
vkr_dispatch_vkGetPhysicalDeviceSparseImageFormatProperties(
   UNUSED struct vn_dispatch_context *dispatch,
   struct vn_command_vkGetPhysicalDeviceSparseImageFormatProperties *args)
{
   vn_replace_vkGetPhysicalDeviceSparseImageFormatProperties_args_handle(args);
   vkGetPhysicalDeviceSparseImageFormatProperties(
      args->physicalDevice, args->format, args->type, args->samples, args->usage,
      args->tiling, args->pPropertyCount, args->pProperties);
}

static void
vkr_dispatch_vkGetPhysicalDeviceFeatures2(
   UNUSED struct vn_dispatch_context *dispatch,
   struct vn_command_vkGetPhysicalDeviceFeatures2 *args)
{
   vn_replace_vkGetPhysicalDeviceFeatures2_args_handle(args);
   vkGetPhysicalDeviceFeatures2(args->physicalDevice, args->pFeatures);
}

static void
vkr_dispatch_vkGetPhysicalDeviceProperties2(
   UNUSED struct vn_dispatch_context *dispatch,
   struct vn_command_vkGetPhysicalDeviceProperties2 *args)
{
   struct vkr_physical_device *physical_dev =
      vkr_physical_device_from_handle(args->physicalDevice);

   vn_replace_vkGetPhysicalDeviceProperties2_args_handle(args);
   vkGetPhysicalDeviceProperties2(args->physicalDevice, args->pProperties);

   union {
      VkBaseOutStructure *pnext;
      VkPhysicalDeviceProperties2 *props;
      VkPhysicalDeviceVulkan11Properties *vk11;
      VkPhysicalDeviceVulkan12Properties *vk12;
      VkPhysicalDeviceIDProperties *id;
      VkPhysicalDeviceDriverProperties *driver;
   } u;

   u.pnext = (VkBaseOutStructure *)args->pProperties;
   while (u.pnext) {
      switch (u.pnext->sType) {
      case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_PROPERTIES_2:
         u.props->properties = physical_dev->properties;
         break;
      case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_VULKAN_1_1_PROPERTIES:
         memset(u.vk11->deviceUUID, 0, sizeof(u.vk11->deviceUUID));
         memset(u.vk11->driverUUID, 0, sizeof(u.vk11->driverUUID));
         memset(u.vk11->deviceLUID, 0, sizeof(u.vk11->deviceLUID));
         u.vk11->deviceNodeMask = 0;
         u.vk11->deviceLUIDValid = false;
         break;
      case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_VULKAN_1_2_PROPERTIES:
         u.vk12->driverID = 0;
         memset(u.vk12->driverName, 0, sizeof(u.vk12->driverName));
         memset(u.vk12->driverInfo, 0, sizeof(u.vk12->driverInfo));
         memset(&u.vk12->conformanceVersion, 0, sizeof(u.vk12->conformanceVersion));
         break;
      case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_ID_PROPERTIES:
         memset(u.id->deviceUUID, 0, sizeof(u.id->deviceUUID));
         memset(u.id->driverUUID, 0, sizeof(u.id->driverUUID));
         memset(u.id->deviceLUID, 0, sizeof(u.id->deviceLUID));
         u.id->deviceNodeMask = 0;
         u.id->deviceLUIDValid = false;
         break;
      case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_DRIVER_PROPERTIES:
         u.driver->driverID = 0;
         memset(u.driver->driverName, 0, sizeof(u.driver->driverName));
         memset(u.driver->driverInfo, 0, sizeof(u.driver->driverInfo));
         memset(&u.driver->conformanceVersion, 0, sizeof(u.driver->conformanceVersion));
         break;
      default:
         break;
      }

      u.pnext = u.pnext->pNext;
   }
}

static void
vkr_dispatch_vkGetPhysicalDeviceQueueFamilyProperties2(
   UNUSED struct vn_dispatch_context *dispatch,
   struct vn_command_vkGetPhysicalDeviceQueueFamilyProperties2 *args)
{
   vn_replace_vkGetPhysicalDeviceQueueFamilyProperties2_args_handle(args);
   vkGetPhysicalDeviceQueueFamilyProperties2(args->physicalDevice,
                                             args->pQueueFamilyPropertyCount,
                                             args->pQueueFamilyProperties);
}

static void
vkr_dispatch_vkGetPhysicalDeviceMemoryProperties2(
   UNUSED struct vn_dispatch_context *dispatch,
   struct vn_command_vkGetPhysicalDeviceMemoryProperties2 *args)
{
   struct vkr_physical_device *physical_dev =
      vkr_physical_device_from_handle(args->physicalDevice);
   args->pMemoryProperties->memoryProperties = physical_dev->memory_properties;
}

static void
vkr_dispatch_vkGetPhysicalDeviceFormatProperties2(
   UNUSED struct vn_dispatch_context *dispatch,
   struct vn_command_vkGetPhysicalDeviceFormatProperties2 *args)
{
   vn_replace_vkGetPhysicalDeviceFormatProperties2_args_handle(args);
   vkGetPhysicalDeviceFormatProperties2(args->physicalDevice, args->format,
                                        args->pFormatProperties);
}

static void
vkr_dispatch_vkGetPhysicalDeviceImageFormatProperties2(
   UNUSED struct vn_dispatch_context *dispatch,
   struct vn_command_vkGetPhysicalDeviceImageFormatProperties2 *args)
{
   vn_replace_vkGetPhysicalDeviceImageFormatProperties2_args_handle(args);
   args->ret = vkGetPhysicalDeviceImageFormatProperties2(
      args->physicalDevice, args->pImageFormatInfo, args->pImageFormatProperties);
}

static void
vkr_dispatch_vkGetPhysicalDeviceSparseImageFormatProperties2(
   UNUSED struct vn_dispatch_context *dispatch,
   struct vn_command_vkGetPhysicalDeviceSparseImageFormatProperties2 *args)
{
   vn_replace_vkGetPhysicalDeviceSparseImageFormatProperties2_args_handle(args);
   vkGetPhysicalDeviceSparseImageFormatProperties2(
      args->physicalDevice, args->pFormatInfo, args->pPropertyCount, args->pProperties);
}

static void
vkr_dispatch_vkGetPhysicalDeviceExternalBufferProperties(
   UNUSED struct vn_dispatch_context *dispatch,
   struct vn_command_vkGetPhysicalDeviceExternalBufferProperties *args)
{
   vn_replace_vkGetPhysicalDeviceExternalBufferProperties_args_handle(args);
   vkGetPhysicalDeviceExternalBufferProperties(
      args->physicalDevice, args->pExternalBufferInfo, args->pExternalBufferProperties);
}

static void
vkr_dispatch_vkGetPhysicalDeviceExternalSemaphoreProperties(
   UNUSED struct vn_dispatch_context *dispatch,
   struct vn_command_vkGetPhysicalDeviceExternalSemaphoreProperties *args)
{
   vn_replace_vkGetPhysicalDeviceExternalSemaphoreProperties_args_handle(args);
   vkGetPhysicalDeviceExternalSemaphoreProperties(args->physicalDevice,
                                                  args->pExternalSemaphoreInfo,
                                                  args->pExternalSemaphoreProperties);
}

static void
vkr_dispatch_vkGetPhysicalDeviceExternalFenceProperties(
   UNUSED struct vn_dispatch_context *dispatch,
   struct vn_command_vkGetPhysicalDeviceExternalFenceProperties *args)
{
   vn_replace_vkGetPhysicalDeviceExternalFenceProperties_args_handle(args);
   vkGetPhysicalDeviceExternalFenceProperties(
      args->physicalDevice, args->pExternalFenceInfo, args->pExternalFenceProperties);
}

static void
vkr_dispatch_vkGetPhysicalDeviceCalibrateableTimeDomainsEXT(
   UNUSED struct vn_dispatch_context *ctx,
   struct vn_command_vkGetPhysicalDeviceCalibrateableTimeDomainsEXT *args)
{
   struct vkr_physical_device *physical_dev =
      vkr_physical_device_from_handle(args->physicalDevice);
   struct vn_physical_device_proc_table *vk = &physical_dev->proc_table;

   vn_replace_vkGetPhysicalDeviceCalibrateableTimeDomainsEXT_args_handle(args);
   args->ret = vk->GetPhysicalDeviceCalibrateableTimeDomainsEXT(
      args->physicalDevice, args->pTimeDomainCount, args->pTimeDomains);
}

void
vkr_context_init_physical_device_dispatch(struct vkr_context *ctx)
{
   struct vn_dispatch_context *dispatch = &ctx->dispatch;

   dispatch->dispatch_vkEnumeratePhysicalDevices =
      vkr_dispatch_vkEnumeratePhysicalDevices;
   dispatch->dispatch_vkEnumeratePhysicalDeviceGroups =
      vkr_dispatch_vkEnumeratePhysicalDeviceGroups;
   dispatch->dispatch_vkEnumerateDeviceExtensionProperties =
      vkr_dispatch_vkEnumerateDeviceExtensionProperties;
   dispatch->dispatch_vkEnumerateDeviceLayerProperties = NULL;

   dispatch->dispatch_vkGetPhysicalDeviceFeatures =
      vkr_dispatch_vkGetPhysicalDeviceFeatures;
   dispatch->dispatch_vkGetPhysicalDeviceProperties =
      vkr_dispatch_vkGetPhysicalDeviceProperties;
   dispatch->dispatch_vkGetPhysicalDeviceQueueFamilyProperties =
      vkr_dispatch_vkGetPhysicalDeviceQueueFamilyProperties;
   dispatch->dispatch_vkGetPhysicalDeviceMemoryProperties =
      vkr_dispatch_vkGetPhysicalDeviceMemoryProperties;
   dispatch->dispatch_vkGetPhysicalDeviceFormatProperties =
      vkr_dispatch_vkGetPhysicalDeviceFormatProperties;
   dispatch->dispatch_vkGetPhysicalDeviceImageFormatProperties =
      vkr_dispatch_vkGetPhysicalDeviceImageFormatProperties;
   dispatch->dispatch_vkGetPhysicalDeviceSparseImageFormatProperties =
      vkr_dispatch_vkGetPhysicalDeviceSparseImageFormatProperties;
   dispatch->dispatch_vkGetPhysicalDeviceFeatures2 =
      vkr_dispatch_vkGetPhysicalDeviceFeatures2;
   dispatch->dispatch_vkGetPhysicalDeviceProperties2 =
      vkr_dispatch_vkGetPhysicalDeviceProperties2;
   dispatch->dispatch_vkGetPhysicalDeviceQueueFamilyProperties2 =
      vkr_dispatch_vkGetPhysicalDeviceQueueFamilyProperties2;
   dispatch->dispatch_vkGetPhysicalDeviceMemoryProperties2 =
      vkr_dispatch_vkGetPhysicalDeviceMemoryProperties2;
   dispatch->dispatch_vkGetPhysicalDeviceFormatProperties2 =
      vkr_dispatch_vkGetPhysicalDeviceFormatProperties2;
   dispatch->dispatch_vkGetPhysicalDeviceImageFormatProperties2 =
      vkr_dispatch_vkGetPhysicalDeviceImageFormatProperties2;
   dispatch->dispatch_vkGetPhysicalDeviceSparseImageFormatProperties2 =
      vkr_dispatch_vkGetPhysicalDeviceSparseImageFormatProperties2;
   dispatch->dispatch_vkGetPhysicalDeviceExternalBufferProperties =
      vkr_dispatch_vkGetPhysicalDeviceExternalBufferProperties;
   dispatch->dispatch_vkGetMemoryFdKHR = NULL;
   dispatch->dispatch_vkGetMemoryFdPropertiesKHR = NULL;
   dispatch->dispatch_vkGetPhysicalDeviceExternalSemaphoreProperties =
      vkr_dispatch_vkGetPhysicalDeviceExternalSemaphoreProperties;
   dispatch->dispatch_vkGetPhysicalDeviceExternalFenceProperties =
      vkr_dispatch_vkGetPhysicalDeviceExternalFenceProperties;
   dispatch->dispatch_vkGetPhysicalDeviceCalibrateableTimeDomainsEXT =
      vkr_dispatch_vkGetPhysicalDeviceCalibrateableTimeDomainsEXT;
}
