/**************************************************************************
 *
 * Copyright (C) 2014 Red Hat Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.  IN NO EVENT SHALL
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR
 * OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 *
 **************************************************************************/
/* create our own EGL offscreen rendering context via gbm and rendernodes */


/* if we are using EGL and rendernodes then we talk via file descriptors to the remote
   node */
#ifdef HAVE_CONFIG_H
#include "config.h"
#endif

#define EGL_EGLEXT_PROTOTYPES
#include <errno.h>
#include <fcntl.h>
#include <poll.h>
#include <stdbool.h>
#include <unistd.h>
#include <xf86drm.h>

#include "util/u_memory.h"

#include "virglrenderer.h"
#include "vrend_winsys.h"
#include "vrend_winsys_egl.h"
#include "virgl_hw.h"
#include "vrend_winsys_gbm.h"
#include "virgl_util.h"

#define EGL_KHR_SURFACELESS_CONTEXT            BIT(0)
#define EGL_KHR_CREATE_CONTEXT                 BIT(1)
#define EGL_MESA_DRM_IMAGE                     BIT(2)
#define EGL_MESA_IMAGE_DMA_BUF_EXPORT          BIT(3)
#define EGL_MESA_DMA_BUF_IMAGE_IMPORT          BIT(4)
#define EGL_KHR_GL_COLORSPACE                  BIT(5)
#define EGL_EXT_IMAGE_DMA_BUF_IMPORT           BIT(6)
#define EGL_EXT_IMAGE_DMA_BUF_IMPORT_MODIFIERS BIT(7)
#define EGL_KHR_FENCE_SYNC_ANDROID             BIT(8)

static const struct {
   uint32_t bit;
   const char *string;
} extensions_list[] = {
   { EGL_KHR_SURFACELESS_CONTEXT, "EGL_KHR_surfaceless_context" },
   { EGL_KHR_CREATE_CONTEXT, "EGL_KHR_create_context" },
   { EGL_MESA_DRM_IMAGE, "EGL_MESA_drm_image" },
   { EGL_MESA_IMAGE_DMA_BUF_EXPORT, "EGL_MESA_image_dma_buf_export" },
   { EGL_KHR_GL_COLORSPACE, "EGL_KHR_gl_colorspace" },
   { EGL_EXT_IMAGE_DMA_BUF_IMPORT, "EGL_EXT_image_dma_buf_import" },
   { EGL_EXT_IMAGE_DMA_BUF_IMPORT_MODIFIERS, "EGL_EXT_image_dma_buf_import_modifiers" },
   { EGL_KHR_FENCE_SYNC_ANDROID, "EGL_ANDROID_native_fence_sync"}
};

struct virgl_egl {
   struct virgl_gbm *gbm;
   EGLDisplay egl_display;
   EGLConfig egl_conf;
   EGLContext egl_ctx;
   uint32_t extension_bits;
   EGLSyncKHR signaled_fence;
   bool different_gpu;
};

static bool virgl_egl_has_extension_in_string(const char *haystack, const char *needle)
{
   const unsigned needle_len = strlen(needle);

   if (!haystack)
      return false;

   if (needle_len == 0)
      return false;

   while (true) {
      const char *const s = strstr(haystack, needle);

      if (s == NULL)
         return false;

      if (s[needle_len] == ' ' || s[needle_len] == '\0') {
         return true;
      }

      /* strstr found an extension whose name begins with
       * needle, but whose name is not equal to needle.
       * Restart the search at s + needle_len so that we
       * don't just find the same extension again and go
       * into an infinite loop.
       */
      haystack = s + needle_len;
   }

   return false;
}

static int virgl_egl_init_extensions(struct virgl_egl *egl, const char *extensions)
{
   for (uint32_t i = 0; i < ARRAY_SIZE(extensions_list); i++) {
      if (virgl_egl_has_extension_in_string(extensions, extensions_list[i].string))
         egl->extension_bits |= extensions_list[i].bit;
   }

   if (!has_bits(egl->extension_bits, EGL_KHR_SURFACELESS_CONTEXT | EGL_KHR_CREATE_CONTEXT)) {
      vrend_printf( "Missing EGL_KHR_surfaceless_context or EGL_KHR_create_context\n");
      return -1;
   }

   return 0;
}

#ifdef ENABLE_MINIGBM_ALLOCATION

struct egl_funcs {
   PFNEGLGETPLATFORMDISPLAYEXTPROC eglGetPlatformDisplay;
   PFNEGLQUERYDEVICESEXTPROC eglQueryDevices;
   PFNEGLQUERYDEVICESTRINGEXTPROC eglQueryDeviceString;
};

static bool virgl_egl_get_interface(struct egl_funcs *funcs)
{
   const char *client_extensions = eglQueryString (NULL, EGL_EXTENSIONS);

   assert(funcs);

   if (virgl_egl_has_extension_in_string(client_extensions, "EGL_EXT_platform_base")) {
      funcs->eglGetPlatformDisplay =
         (PFNEGLGETPLATFORMDISPLAYEXTPROC) eglGetProcAddress ("eglGetPlatformDisplayEXT");
   }

   if (!funcs->eglGetPlatformDisplay)
      return false;

   if (!virgl_egl_has_extension_in_string(client_extensions, "EGL_EXT_platform_device"))
      return false;

   if (!virgl_egl_has_extension_in_string(client_extensions, "EGL_EXT_device_enumeration"))
      return false;

   funcs->eglQueryDevices = (PFNEGLQUERYDEVICESEXTPROC)eglGetProcAddress ("eglQueryDevicesEXT");
   if (!funcs->eglQueryDevices)
      return false;

   if (!virgl_egl_has_extension_in_string(client_extensions, "EGL_EXT_device_query"))
      return false;

   funcs->eglQueryDeviceString = (PFNEGLQUERYDEVICESTRINGEXTPROC)eglGetProcAddress("eglQueryDeviceStringEXT");
   if (!funcs->eglQueryDeviceString)
      return false;

  return true;
}

static EGLint virgl_egl_find_3d_device(struct gbm_device_info *dev_infos, EGLint num_devices, uint32_t flags)
{
   EGLint d;

   for (d = 0; d < num_devices; d++) {
       if ((dev_infos[d].dev_type_flags & flags) == flags
           && dev_infos[d].dev_type_flags & GBM_DEV_TYPE_FLAG_3D)
          return d;
   }

   return -1;
}

static EGLint virgl_egl_find_matching_device(struct gbm_device_info *dev_infos, EGLint num_devices, int dri_node_num)
{
   EGLint d;

   for (d = 0; d < num_devices; d++) {
       if (dev_infos[d].dri_node_num == dri_node_num)
          return d;
   }

   return -1;
}

static EGLDeviceEXT virgl_egl_get_device(struct virgl_egl *egl, struct egl_funcs *funcs) {
   EGLint num_devices = 0;
   EGLint max_devices = 64;
   EGLDeviceEXT devices[64];
   struct gbm_device_info dev_infos[64];
   struct gbm_device_info gbm_dev_info;
   EGLint device_num = -1;
   EGLint d;

   if (gbm_detect_device_info(0, gbm_device_get_fd(egl->gbm->device), &gbm_dev_info) < 0)
      return EGL_NO_DEVICE_EXT;

   if (!funcs->eglQueryDevices(max_devices, devices, &num_devices))
      return EGL_NO_DEVICE_EXT;

   /* We query EGL_DRM_DEVICE_FILE_EXT without checking EGL_EXT_device_drm extension,
    * we just get NULL when it is not available. Otherwise we would have to query it
    * after initializing display for every device.
    */
   for (d = 0; d < num_devices; d++) {
       const char *dev_node = funcs->eglQueryDeviceString(devices[d], EGL_DRM_DEVICE_FILE_EXT);
       memset(&dev_infos[d], 0, sizeof(dev_infos[d]));
       if (dev_node) {
          if (gbm_detect_device_info_path(0, dev_node, dev_infos+d) < 0)
             return false;
       } else {
          dev_infos[d].dri_node_num = -1;
       }
   }

   if (getenv("VIRGL_PREFER_DGPU"))
      /* Find a discrete GPU. */
      device_num = virgl_egl_find_3d_device(dev_infos, num_devices, GBM_DEV_TYPE_FLAG_DISCRETE);

   if (device_num >= 0) {
      egl->different_gpu = dev_infos[device_num].dri_node_num != gbm_dev_info.dri_node_num;
   } else if (gbm_dev_info.dev_type_flags & GBM_DEV_TYPE_FLAG_ARMSOC) {
      /* Find 3D device on ARM SOC. */
      device_num = virgl_egl_find_3d_device(dev_infos, num_devices, GBM_DEV_TYPE_FLAG_ARMSOC);
   }

   if (device_num < 0) {
      /* Try to match GBM device. */
      device_num = virgl_egl_find_matching_device(dev_infos, num_devices, gbm_dev_info.dri_node_num);
   }
   if (device_num < 0)
      return EGL_NO_DEVICE_EXT;

  return devices[device_num];
}

static bool virgl_egl_get_display(struct virgl_egl *egl)
{
   struct egl_funcs funcs = { 0 };
   EGLDeviceEXT device;

   if (!egl->gbm)
      return false;

   if (!virgl_egl_get_interface(&funcs))
      return false;

   device = virgl_egl_get_device(egl, &funcs);

   if (device == EGL_NO_DEVICE_EXT)
      return false;

   egl->egl_display = funcs.eglGetPlatformDisplay(EGL_PLATFORM_DEVICE_EXT, device, NULL);
   return true;
}
#endif /* ENABLE_MINIGBM_ALLOCATION */

struct virgl_egl *virgl_egl_init(struct virgl_gbm *gbm, bool surfaceless, bool gles)
{
   static EGLint conf_att[] = {
      EGL_SURFACE_TYPE, EGL_WINDOW_BIT,
      EGL_RENDERABLE_TYPE, EGL_OPENGL_BIT,
      EGL_RED_SIZE, 1,
      EGL_GREEN_SIZE, 1,
      EGL_BLUE_SIZE, 1,
      EGL_ALPHA_SIZE, 0,
      EGL_NONE,
   };
   static const EGLint ctx_att[] = {
      EGL_CONTEXT_CLIENT_VERSION, 2,
      EGL_NONE
   };
   EGLBoolean success;
   EGLenum api;
   EGLint major, minor, num_configs;
   const char *extensions;
   struct virgl_egl *egl;

   egl = calloc(1, sizeof(struct virgl_egl));
   if (!egl)
      return NULL;

   if (gles)
      conf_att[3] = EGL_OPENGL_ES2_BIT;

   if (surfaceless)
      conf_att[1] = EGL_PBUFFER_BIT;
   else if (!gbm)
      goto fail;

   egl->gbm = gbm;
   egl->different_gpu = false;
   const char *client_extensions = eglQueryString (NULL, EGL_EXTENSIONS);

#ifdef ENABLE_MINIGBM_ALLOCATION
   if (virgl_egl_get_display(egl)) {
     /* Make -Wdangling-else happy. */
   } else /* Fallback to surfaceless. */
#endif
   if (virgl_egl_has_extension_in_string(client_extensions, "EGL_EXT_platform_base")) {
      PFNEGLGETPLATFORMDISPLAYEXTPROC get_platform_display =
         (PFNEGLGETPLATFORMDISPLAYEXTPROC) eglGetProcAddress ("eglGetPlatformDisplayEXT");

      if (!get_platform_display)
        goto fail;

      if (surfaceless) {
         egl->egl_display = get_platform_display (EGL_PLATFORM_SURFACELESS_MESA,
                                                  EGL_DEFAULT_DISPLAY, NULL);
      } else
         egl->egl_display = get_platform_display (EGL_PLATFORM_GBM_KHR,
                                                 (EGLNativeDisplayType)egl->gbm->device, NULL);
   } else {
      egl->egl_display = eglGetDisplay((EGLNativeDisplayType)egl->gbm->device);
   }

   if (!egl->egl_display) {
      /*
       * Don't fallback to the default display if the fd provided by (*get_drm_fd)
       * can't be used.
       */
      if (egl->gbm && egl->gbm->fd < 0)
         goto fail;

      egl->egl_display = eglGetDisplay(EGL_DEFAULT_DISPLAY);
      if (!egl->egl_display)
         goto fail;
   }

   success = eglInitialize(egl->egl_display, &major, &minor);
   if (!success)
      goto fail;

   extensions = eglQueryString(egl->egl_display, EGL_EXTENSIONS);
#ifdef VIRGL_EGL_DEBUG
   vrend_printf( "EGL major/minor: %d.%d\n", major, minor);
   vrend_printf( "EGL version: %s\n",
           eglQueryString(egl->egl_display, EGL_VERSION));
   vrend_printf( "EGL vendor: %s\n",
           eglQueryString(egl->egl_display, EGL_VENDOR));
   vrend_printf( "EGL extensions: %s\n", extensions);
#endif

   if (virgl_egl_init_extensions(egl, extensions))
      goto fail;

   if (gles)
      api = EGL_OPENGL_ES_API;
   else
      api = EGL_OPENGL_API;
   success = eglBindAPI(api);
   if (!success)
      goto fail;

   success = eglChooseConfig(egl->egl_display, conf_att, &egl->egl_conf,
                             1, &num_configs);
   if (!success || num_configs != 1)
      goto fail;

   egl->egl_ctx = eglCreateContext(egl->egl_display, egl->egl_conf, EGL_NO_CONTEXT,
                                   ctx_att);
   if (!egl->egl_ctx)
      goto fail;

   eglMakeCurrent(egl->egl_display, EGL_NO_SURFACE, EGL_NO_SURFACE,
                  egl->egl_ctx);

   if (gles && virgl_egl_supports_fences(egl)) {
      egl->signaled_fence = eglCreateSyncKHR(egl->egl_display,
                                             EGL_SYNC_NATIVE_FENCE_ANDROID, NULL);
      if (!egl->signaled_fence) {
         vrend_printf("Failed to create signaled fence");
         goto fail;
      }
   }

   return egl;

 fail:
   free(egl);
   return NULL;
}

void virgl_egl_destroy(struct virgl_egl *egl)
{
   if (egl->signaled_fence) {
      eglDestroySyncKHR(egl->egl_display, egl->signaled_fence);
   }
   eglMakeCurrent(egl->egl_display, EGL_NO_SURFACE, EGL_NO_SURFACE,
                  EGL_NO_CONTEXT);
   eglDestroyContext(egl->egl_display, egl->egl_ctx);
   eglTerminate(egl->egl_display);
   free(egl);
}

struct virgl_egl *virgl_egl_init_external(EGLDisplay egl_display)
{
   const char *extensions;
   struct virgl_egl *egl;

   egl = calloc(1, sizeof(struct virgl_egl));
   if (!egl)
      return NULL;

   egl->egl_display = egl_display;

   extensions = eglQueryString(egl->egl_display, EGL_EXTENSIONS);
#ifdef VIRGL_EGL_DEBUG
   vrend_printf( "EGL version: %s\n",
           eglQueryString(egl->egl_display, EGL_VERSION));
   vrend_printf( "EGL vendor: %s\n",
           eglQueryString(egl->egl_display, EGL_VENDOR));
   vrend_printf( "EGL extensions: %s\n", extensions);
#endif

   if (virgl_egl_init_extensions(egl, extensions)) {
      free(egl);
      return NULL;
   }

   return egl;
}

virgl_renderer_gl_context virgl_egl_create_context(struct virgl_egl *egl, struct virgl_gl_ctx_param *vparams)
{
   EGLContext egl_ctx;
   EGLint ctx_att[] = {
      EGL_CONTEXT_CLIENT_VERSION, vparams->major_ver,
      EGL_CONTEXT_MINOR_VERSION_KHR, vparams->minor_ver,
      EGL_NONE
   };
   egl_ctx = eglCreateContext(egl->egl_display,
                             egl->egl_conf,
                             vparams->shared ? eglGetCurrentContext() : EGL_NO_CONTEXT,
                             ctx_att);
   return (virgl_renderer_gl_context)egl_ctx;
}

void virgl_egl_destroy_context(struct virgl_egl *egl, virgl_renderer_gl_context virglctx)
{
   EGLContext egl_ctx = (EGLContext)virglctx;
   eglDestroyContext(egl->egl_display, egl_ctx);
}

int virgl_egl_make_context_current(struct virgl_egl *egl, virgl_renderer_gl_context virglctx)
{
   EGLContext egl_ctx = (EGLContext)virglctx;

   return eglMakeCurrent(egl->egl_display, EGL_NO_SURFACE, EGL_NO_SURFACE,
                         egl_ctx) ? 0 : -1;
}

virgl_renderer_gl_context virgl_egl_get_current_context(UNUSED struct virgl_egl *egl)
{
   EGLContext egl_ctx = eglGetCurrentContext();
   return (virgl_renderer_gl_context)egl_ctx;
}

int virgl_egl_get_fourcc_for_texture(struct virgl_egl *egl, uint32_t tex_id, uint32_t format, int *fourcc)
{
   int ret = EINVAL;
   uint32_t gbm_format = 0;

   EGLImageKHR image;
   EGLBoolean success;

   if (!has_bit(egl->extension_bits, EGL_MESA_IMAGE_DMA_BUF_EXPORT)) {
      ret = 0;
      goto fallback;
   }

   image = eglCreateImageKHR(egl->egl_display, eglGetCurrentContext(), EGL_GL_TEXTURE_2D_KHR,
                            (EGLClientBuffer)(uintptr_t)tex_id, NULL);

   if (!image)
      return EINVAL;

   success = eglExportDMABUFImageQueryMESA(egl->egl_display, image, fourcc, NULL, NULL);
   if (!success)
      goto out_destroy;
   ret = 0;
 out_destroy:
   eglDestroyImageKHR(egl->egl_display, image);
   return ret;

 fallback:
   ret = virgl_gbm_convert_format(&format, &gbm_format);
   *fourcc = (int)gbm_format;
   return ret;
}

int virgl_egl_get_fd_for_texture2(struct virgl_egl *egl, uint32_t tex_id, int *fd,
                                  int *stride, int *offset)
{
   int ret = EINVAL;
   EGLImageKHR image = eglCreateImageKHR(egl->egl_display, eglGetCurrentContext(),
                                         EGL_GL_TEXTURE_2D_KHR,
                                         (EGLClientBuffer)(uintptr_t)tex_id, NULL);
   if (!image)
      return EINVAL;
   if (!has_bit(egl->extension_bits, EGL_MESA_IMAGE_DMA_BUF_EXPORT))
      goto out_destroy;

   if (!eglExportDMABUFImageMESA(egl->egl_display, image, fd,
                                 stride, offset))
      goto out_destroy;

   ret = 0;

out_destroy:
   eglDestroyImageKHR(egl->egl_display, image);
   return ret;
}

int virgl_egl_get_fd_for_texture(struct virgl_egl *egl, uint32_t tex_id, int *fd)
{
   EGLImageKHR image;
   EGLint stride;
   EGLint offset;
   EGLBoolean success;
   int ret;
   image = eglCreateImageKHR(egl->egl_display, eglGetCurrentContext(), EGL_GL_TEXTURE_2D_KHR,
                            (EGLClientBuffer)(uintptr_t)tex_id, NULL);

   if (!image)
      return EINVAL;

   ret = EINVAL;
   if (has_bit(egl->extension_bits, EGL_MESA_IMAGE_DMA_BUF_EXPORT)) {
      success = eglExportDMABUFImageMESA(egl->egl_display, image, fd, &stride,
                                         &offset);
      if (!success)
         goto out_destroy;
   } else if (has_bit(egl->extension_bits, EGL_MESA_DRM_IMAGE)) {
      EGLint handle;
      success = eglExportDRMImageMESA(egl->egl_display, image, NULL, &handle,
                                      &stride);

      if (!success)
         goto out_destroy;

      if (!egl->gbm)
         goto out_destroy;

      ret = virgl_gbm_export_fd(egl->gbm->device, handle, fd);
      if (ret < 0)
         goto out_destroy;
   } else {
      goto out_destroy;
   }

   ret = 0;
 out_destroy:
   eglDestroyImageKHR(egl->egl_display, image);
   return ret;
}

bool virgl_has_egl_khr_gl_colorspace(struct virgl_egl *egl)
{
   return has_bit(egl->extension_bits, EGL_KHR_GL_COLORSPACE);
}

void *virgl_egl_image_from_dmabuf(struct virgl_egl *egl,
                                  uint32_t width,
                                  uint32_t height,
                                  uint32_t drm_format,
                                  uint64_t drm_modifier,
                                  uint32_t plane_count,
                                  const int *plane_fds,
                                  const uint32_t *plane_strides,
                                  const uint32_t *plane_offsets)
{
   EGLint attrs[6 + VIRGL_GBM_MAX_PLANES * 10 + 1];
   uint32_t count;

   assert(VIRGL_GBM_MAX_PLANES <= 4);
   assert(plane_count && plane_count <= VIRGL_GBM_MAX_PLANES);

   count = 0;
   attrs[count++] = EGL_WIDTH;
   attrs[count++] = width;
   attrs[count++] = EGL_HEIGHT;
   attrs[count++] = height;
   attrs[count++] = EGL_LINUX_DRM_FOURCC_EXT;
   attrs[count++] = drm_format;
   for (uint32_t i = 0; i < plane_count; i++) {
      if (i < 3) {
         attrs[count++] = EGL_DMA_BUF_PLANE0_FD_EXT + i * 3;
         attrs[count++] = plane_fds[i];
         attrs[count++] = EGL_DMA_BUF_PLANE0_PITCH_EXT + i * 3;
         attrs[count++] = plane_strides[i];
         attrs[count++] = EGL_DMA_BUF_PLANE0_OFFSET_EXT + i * 3;
         attrs[count++] = plane_offsets[i];
      }

      if (has_bit(egl->extension_bits, EGL_EXT_IMAGE_DMA_BUF_IMPORT_MODIFIERS)) {
         if (i == 3) {
            attrs[count++] = EGL_DMA_BUF_PLANE3_FD_EXT;
            attrs[count++] = plane_fds[i];
            attrs[count++] = EGL_DMA_BUF_PLANE3_PITCH_EXT;
            attrs[count++] = plane_strides[i];
            attrs[count++] = EGL_DMA_BUF_PLANE3_OFFSET_EXT;
            attrs[count++] = plane_offsets[i];
         }

	 if (drm_modifier != DRM_FORMAT_MOD_INVALID) {
            attrs[count++] = EGL_DMA_BUF_PLANE0_MODIFIER_LO_EXT + i * 2;
            attrs[count++] = (uint32_t)drm_modifier;
            attrs[count++] = EGL_DMA_BUF_PLANE0_MODIFIER_HI_EXT + i * 2;
            attrs[count++] = (uint32_t)(drm_modifier >> 32);
	 }
      }
   }
   attrs[count++] = EGL_NONE;
   assert(count <= ARRAY_SIZE(attrs));

   return (void *)eglCreateImageKHR(egl->egl_display,
                                    EGL_NO_CONTEXT,
                                    EGL_LINUX_DMA_BUF_EXT,
                                    (EGLClientBuffer)NULL,
                                    attrs);
}

void virgl_egl_image_destroy(struct virgl_egl *egl, void *image)
{
   eglDestroyImageKHR(egl->egl_display, image);
}

#ifdef ENABLE_MINIGBM_ALLOCATION
void *virgl_egl_image_from_gbm_bo(struct virgl_egl *egl, struct gbm_bo *bo)
{
   int ret;
   void *image = NULL;
   int fds[VIRGL_GBM_MAX_PLANES] = {-1, -1, -1, -1};
   uint32_t strides[VIRGL_GBM_MAX_PLANES];
   uint32_t offsets[VIRGL_GBM_MAX_PLANES];
   int num_planes = gbm_bo_get_plane_count(bo);

   if (num_planes < 0 || num_planes > VIRGL_GBM_MAX_PLANES)
      return NULL;

   for (int plane = 0; plane < num_planes; plane++) {
      uint32_t handle = gbm_bo_get_handle_for_plane(bo, plane).u32;
      ret = virgl_gbm_export_fd(egl->gbm->device, handle, &fds[plane]);
      if (ret < 0) {
         vrend_printf( "failed to export plane handle\n");
         goto out_close;
      }

      strides[plane] = gbm_bo_get_stride_for_plane(bo, plane);
      offsets[plane] = gbm_bo_get_offset(bo, plane);
   }

   image = virgl_egl_image_from_dmabuf(egl,
                                       gbm_bo_get_width(bo),
                                       gbm_bo_get_height(bo),
                                       gbm_bo_get_format(bo),
                                       gbm_bo_get_modifier(bo),
                                       num_planes,
                                       fds,
                                       strides,
                                       offsets);

out_close:
   for (int plane = 0; plane < num_planes; plane++)
      close(fds[plane]);

   return image;
}

void *virgl_egl_aux_plane_image_from_gbm_bo(struct virgl_egl *egl, struct gbm_bo *bo, int plane)
{
   int ret;
   void *image = NULL;
   int fd = -1;

   int bytes_per_pixel = virgl_gbm_get_plane_bytes_per_pixel(bo, plane);
   if (bytes_per_pixel != 1 && bytes_per_pixel != 2)
      return NULL;

   uint32_t handle = gbm_bo_get_handle_for_plane(bo, plane).u32;
   ret = drmPrimeHandleToFD(gbm_device_get_fd(egl->gbm->device), handle, DRM_CLOEXEC, &fd);
   if (ret < 0) {
      vrend_printf("failed to export plane handle %d\n", errno);
      return NULL;
   }

   const uint32_t format = bytes_per_pixel == 1 ? GBM_FORMAT_R8 : GBM_FORMAT_GR88;
   const uint32_t stride = gbm_bo_get_stride_for_plane(bo, plane);
   const uint32_t offset = gbm_bo_get_offset(bo, plane);
   image = virgl_egl_image_from_dmabuf(egl,
                                       virgl_gbm_get_plane_width(bo, plane),
                                       virgl_gbm_get_plane_height(bo, plane),
                                       format,
                                       gbm_bo_get_modifier(bo),
                                       1,
                                       &fd,
                                       &stride,
                                       &offset);
   close(fd);

   return image;
}
#endif /* ENABLE_MINIGBM_ALLOCATION */

bool virgl_egl_supports_fences(struct virgl_egl *egl)
{
   return (egl && has_bit(egl->extension_bits, EGL_KHR_FENCE_SYNC_ANDROID));
}

EGLSyncKHR virgl_egl_fence_create(struct virgl_egl *egl)
{
   if (!egl || !has_bit(egl->extension_bits, EGL_KHR_FENCE_SYNC_ANDROID)) {
      return EGL_NO_SYNC_KHR;
   }

   return eglCreateSyncKHR(egl->egl_display, EGL_SYNC_NATIVE_FENCE_ANDROID, NULL);
}

void virgl_egl_fence_destroy(struct virgl_egl *egl, EGLSyncKHR fence) {
   eglDestroySyncKHR(egl->egl_display, fence);
}

bool virgl_egl_client_wait_fence(struct virgl_egl *egl, EGLSyncKHR fence, bool blocking)
{
   /* attempt to poll the native fence fd instead of eglClientWaitSyncKHR() to
    * avoid Mesa's eglapi global-display-lock synchronizing vrend's sync_thread.
    */
   int fd = -1;
   if (!virgl_egl_export_fence(egl, fence, &fd)) {
      EGLint egl_result = eglClientWaitSyncKHR(egl->egl_display, fence, 0,
                                               blocking ? EGL_FOREVER_KHR : 0);
      if (egl_result == EGL_FALSE)
         vrend_printf("wait sync failed\n");
      return egl_result != EGL_TIMEOUT_EXPIRED_KHR;
   }
   assert(fd >= 0);

   int ret;
   struct pollfd pfd = {
      .fd = fd,
      .events = POLLIN,
   };
   do {
      ret = poll(&pfd, 1, blocking ? -1 : 0);
      if (ret > 0 && (pfd.revents & (POLLERR | POLLNVAL))) {
         ret = -1;
         break;
      }
   } while (ret == -1 && (errno == EINTR || errno == EAGAIN));
   close(fd);

   if (ret < 0)
      vrend_printf("wait sync failed\n");
   return ret != 0;
}

bool virgl_egl_export_signaled_fence(struct virgl_egl *egl, int *out_fd) {
   return virgl_egl_export_fence(egl, egl->signaled_fence, out_fd);
}

bool virgl_egl_export_fence(struct virgl_egl *egl, EGLSyncKHR fence, int *out_fd) {
   *out_fd = eglDupNativeFenceFDANDROID(egl->egl_display, fence);
   return *out_fd != EGL_NO_NATIVE_FENCE_FD_ANDROID;
}

bool virgl_egl_different_gpu(struct virgl_egl *egl)
{
   return egl->different_gpu;
}

const char *virgl_egl_error_string(EGLint error)
{
    switch (error) {
#define CASE_STR( value ) case value: return #value;
    CASE_STR( EGL_SUCCESS             )
    CASE_STR( EGL_NOT_INITIALIZED     )
    CASE_STR( EGL_BAD_ACCESS          )
    CASE_STR( EGL_BAD_ALLOC           )
    CASE_STR( EGL_BAD_ATTRIBUTE       )
    CASE_STR( EGL_BAD_CONTEXT         )
    CASE_STR( EGL_BAD_CONFIG          )
    CASE_STR( EGL_BAD_CURRENT_SURFACE )
    CASE_STR( EGL_BAD_DISPLAY         )
    CASE_STR( EGL_BAD_SURFACE         )
    CASE_STR( EGL_BAD_MATCH           )
    CASE_STR( EGL_BAD_PARAMETER       )
    CASE_STR( EGL_BAD_NATIVE_PIXMAP   )
    CASE_STR( EGL_BAD_NATIVE_WINDOW   )
    CASE_STR( EGL_CONTEXT_LOST        )
#undef CASE_STR
    default: return "Unknown error";
    }
}
