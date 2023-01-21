/*
 * Copyright 2020 Google LLC
 * SPDX-License-Identifier: MIT
 */

#include "vkr_common.h"

#include "util/u_debug.h"
#include "venus-protocol/vn_protocol_renderer_info.h"
#include "virglrenderer_hw.h"

static const struct debug_named_value vkr_debug_options[] = {
   { "validate", VKR_DEBUG_VALIDATE, "Force enabling the validation layer" },
   DEBUG_NAMED_VALUE_END
};

uint32_t vkr_renderer_flags;
uint32_t vkr_debug_flags;

size_t
vkr_get_capset(void *capset)
{
   struct virgl_renderer_capset_venus *c = capset;
   if (c) {
      memset(c, 0, sizeof(*c));
      c->wire_format_version = vn_info_wire_format_version();
      c->vk_xml_version = vn_info_vk_xml_version();
      c->vk_ext_command_serialization_spec_version =
         vkr_extension_get_spec_version("VK_EXT_command_serialization");
      c->vk_mesa_venus_protocol_spec_version =
         vkr_extension_get_spec_version("VK_MESA_venus_protocol");
      /* After https://gitlab.freedesktop.org/virgl/virglrenderer/-/merge_requests/688,
       * this flag is used to indicate render server config, and will be needed until drm
       * virtio-gpu blob mem gets fixed to attach_resource before resource_map.
       */
      c->supports_blob_id_0 = (bool)(vkr_renderer_flags & VKR_RENDERER_RENDER_SERVER);

      uint32_t ext_mask[VN_INFO_EXTENSION_MAX_NUMBER / 32 + 1] = { 0 };
      vn_info_extension_mask_init(ext_mask);

      static_assert(sizeof(ext_mask) <= sizeof(c->vk_extension_mask1),
                    "Time to extend venus capset with vk_extension_mask2");
      memcpy(c->vk_extension_mask1, ext_mask, sizeof(ext_mask));

      /* set bit 0 to enable the extension mask(s) */
      assert(!(c->vk_extension_mask1[0] & 0x1u));
      c->vk_extension_mask1[0] |= 0x1u;

      c->allow_vk_wait_syncs = 1;
   }

   return sizeof(*c);
}

int
vkr_renderer_init(uint32_t flags)
{
   if ((vkr_renderer_flags & VKR_RENDERER_ASYNC_FENCE_CB) &&
       !(vkr_renderer_flags & VKR_RENDERER_THREAD_SYNC))
      return -EINVAL;

   vkr_renderer_flags = flags;
   vkr_debug_flags = debug_get_flags_option("VKR_DEBUG", vkr_debug_options, 0);

   return 0;
}

void
vkr_renderer_fini(void)
{
   vkr_renderer_flags = 0;
   vkr_debug_flags = 0;
}

void
vkr_renderer_reset(void)
{
}
