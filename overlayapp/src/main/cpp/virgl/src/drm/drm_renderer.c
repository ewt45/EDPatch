/*
 * Copyright 2022 Google LLC
 * SPDX-License-Identifier: MIT
 */

#include "config.h"

#include <errno.h>
#include <inttypes.h>
#include <stddef.h>
#include <stdint.h>
#include <unistd.h>

#include <xf86drm.h>

#include "drm_hw.h"
#include "drm_renderer.h"

#ifdef ENABLE_DRM_MSM
#  include "msm/msm_renderer.h"
#endif

static struct virgl_renderer_capset_drm capset;

static const struct backend {
   uint32_t context_type;
   const char *name;
   int (*probe)(int fd, struct virgl_renderer_capset_drm *capset);
   struct virgl_context *(*create)(int fd);
} backends[] = {
#ifdef ENABLE_DRM_MSM
   {
      .context_type = VIRTGPU_DRM_CONTEXT_MSM,
      .name = "msm",
      .probe = msm_renderer_probe,
      .create = msm_renderer_create,
   },
#endif
};

int
drm_renderer_init(int drm_fd)
{
   for (unsigned i = 0; i < ARRAY_SIZE(backends); i++) {
      const struct backend *b = &backends[i];
      int fd;

      if (drm_fd != -1) {
         fd = drm_fd;
      } else {
         fd = drmOpenWithType(b->name, NULL, DRM_NODE_RENDER);
         if (fd < 0)
            continue;
      }

      drmVersionPtr ver = drmGetVersion(fd);
      if (!ver) {
         close(fd);
         return -ENOMEM;
      }

      if (strcmp(ver->name, b->name)) {
         /* In the drmOpenWithType() path, we will only get back an fd
          * for the device with matching name.  But when we are using
          * an externally provided fd, we need to go thru the backends
          * table to see which one has the matching name.
          */
         assert(drm_fd != -1);
         drmFreeVersion(ver);
         continue;
      }

      capset.version_major = ver->version_major;
      capset.version_minor = ver->version_minor;
      capset.version_patchlevel = ver->version_patchlevel;
      capset.context_type = b->context_type;

      int ret = b->probe(fd, &capset);
      if (ret)
         memset(&capset, 0, sizeof(capset));

      drmFreeVersion(ver);
      close(fd);
      return ret;
   }

   if (drm_fd != -1)
      close(drm_fd);

   return -ENODEV;
}

void
drm_renderer_fini(void)
{
   drm_log("");
}

void
drm_renderer_reset(void)
{
   drm_log("");
}

size_t
drm_renderer_capset(void *_c)
{
   struct virgl_renderer_capset_drm *c = _c;
   drm_log("c=%p", c);

   if (!capset.context_type)
      return 0;

   if (c)
      *c = capset;

   return sizeof(*c);
}

struct virgl_context *
drm_renderer_create(UNUSED size_t debug_len, UNUSED const char *debug_name)
{
   for (unsigned i = 0; i < ARRAY_SIZE(backends); i++) {
      const struct backend *b = &backends[i];

      if (b->context_type != capset.context_type)
         continue;

      int fd = drmOpenWithType(b->name, NULL, DRM_NODE_RENDER);
      if (fd < 0)
         return NULL;

      return b->create(fd);
   }

   return NULL;
}
