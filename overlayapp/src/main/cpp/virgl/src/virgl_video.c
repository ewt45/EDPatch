/**************************************************************************
 *
 * Copyright (C) 2022 Kylin Software Co., Ltd.
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

#include <stdio.h>
#include <stdint.h>
#include <stdbool.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <epoxy/gl.h>
#include <epoxy/egl.h>
#include <va/va.h>
#include <va/va_drm.h>
#include <va/va_drmcommon.h>
#include <drm/drm_fourcc.h>

#include "util/u_memory.h"
#include "virgl_hw.h"
#include "virgl_util.h"
#include "virgl_video.h"

struct virgl_video_buffer {
    enum pipe_format format;
    uint32_t width;
    uint32_t height;
    bool interlanced;
    VASurfaceID va_sfc;
    void *associated_data;
    VADRMPRIMESurfaceDescriptor desc;
    struct virgl_video_dma_buf dmabuf;
    bool exported;
};


struct virgl_video_codec {
   enum pipe_video_profile profile;
   uint32_t level;
   enum pipe_video_entrypoint entrypoint;
   enum pipe_video_chroma_format chroma_format;
   uint32_t width;
   uint32_t height;
   VAContextID va_ctx;
   VAConfigID  va_cfg;
   struct virgl_video_buffer *buffer;
};


static VADisplay va_dpy;

static virgl_video_flush_buffer flush_buffer_cb;

static enum pipe_video_profile pipe_profile_from_va(VAProfile profile)
{
   switch (profile) {
   case VAProfileMPEG2Simple:
      return PIPE_VIDEO_PROFILE_MPEG2_SIMPLE;
   case VAProfileMPEG2Main:
      return PIPE_VIDEO_PROFILE_MPEG2_MAIN;
   case VAProfileMPEG4Simple:
      return PIPE_VIDEO_PROFILE_MPEG4_SIMPLE;
   case VAProfileMPEG4AdvancedSimple:
      return PIPE_VIDEO_PROFILE_MPEG4_ADVANCED_SIMPLE;
   case VAProfileVC1Simple:
      return PIPE_VIDEO_PROFILE_VC1_SIMPLE;
   case VAProfileVC1Main:
      return PIPE_VIDEO_PROFILE_VC1_MAIN;
   case VAProfileVC1Advanced:
      return PIPE_VIDEO_PROFILE_VC1_ADVANCED;
   case VAProfileH264ConstrainedBaseline:
      return PIPE_VIDEO_PROFILE_MPEG4_AVC_BASELINE;
   case VAProfileH264Main:
      return PIPE_VIDEO_PROFILE_MPEG4_AVC_MAIN;
   case VAProfileH264High:
      return PIPE_VIDEO_PROFILE_MPEG4_AVC_HIGH;
   case VAProfileHEVCMain:
      return PIPE_VIDEO_PROFILE_HEVC_MAIN;
   case VAProfileHEVCMain10:
      return PIPE_VIDEO_PROFILE_HEVC_MAIN_10;
   case VAProfileJPEGBaseline:
      return PIPE_VIDEO_PROFILE_JPEG_BASELINE;
   case VAProfileVP9Profile0:
      return PIPE_VIDEO_PROFILE_VP9_PROFILE0;
   case VAProfileVP9Profile2:
      return PIPE_VIDEO_PROFILE_VP9_PROFILE2;
   case VAProfileAV1Profile0:
      return PIPE_VIDEO_PROFILE_AV1_MAIN;
   case VAProfileNone:
       return PIPE_VIDEO_PROFILE_UNKNOWN;
   default:
      return PIPE_VIDEO_PROFILE_UNKNOWN;
   }
}

/* NOTE: mesa va frontend only supports VLD and EncSlice */
static enum pipe_video_entrypoint pipe_entrypoint_from_va(
        VAEntrypoint entrypoint)
{
    switch (entrypoint) {
    case VAEntrypointVLD:
        return PIPE_VIDEO_ENTRYPOINT_BITSTREAM;
    case VAEntrypointIDCT:
        return PIPE_VIDEO_ENTRYPOINT_IDCT;
    case VAEntrypointMoComp:
        return PIPE_VIDEO_ENTRYPOINT_MC;
    case VAEntrypointEncSlice: /* fall through */
    case VAEntrypointEncSliceLP:
        return PIPE_VIDEO_ENTRYPOINT_ENCODE;
    default:
        return PIPE_VIDEO_ENTRYPOINT_UNKNOWN;
    }
}

static enum pipe_format pipe_format_from_va_fourcc(unsigned format)
{
   switch(format) {
   case VA_FOURCC('N','V','1','2'):
      return PIPE_FORMAT_NV12;
/* TODO: These are already defined in mesa, but not yet in virglrenderer
   case VA_FOURCC('P','0','1','0'):
      return PIPE_FORMAT_P010;
   case VA_FOURCC('P','0','1','6'):
      return PIPE_FORMAT_P016;
*/
   case VA_FOURCC('I','4','2','0'):
      return PIPE_FORMAT_IYUV;
   case VA_FOURCC('Y','V','1','2'):
      return PIPE_FORMAT_YV12;
   case VA_FOURCC('Y','U','Y','V'):
   case VA_FOURCC('Y','U','Y','2'):
      return PIPE_FORMAT_YUYV;
   case VA_FOURCC('U','Y','V','Y'):
      return PIPE_FORMAT_UYVY;
   case VA_FOURCC('B','G','R','A'):
      return PIPE_FORMAT_B8G8R8A8_UNORM;
   case VA_FOURCC('R','G','B','A'):
      return PIPE_FORMAT_R8G8B8A8_UNORM;
   case VA_FOURCC('B','G','R','X'):
      return PIPE_FORMAT_B8G8R8X8_UNORM;
   case VA_FOURCC('R','G','B','X'):
      return PIPE_FORMAT_R8G8B8X8_UNORM;
   default:
      return PIPE_FORMAT_NONE;
   }
}


static VAProfile va_profile_from_pipe(enum pipe_video_profile profile)
{
   switch (profile) {
   case PIPE_VIDEO_PROFILE_MPEG2_SIMPLE:
      return VAProfileMPEG2Simple;
   case PIPE_VIDEO_PROFILE_MPEG2_MAIN:
      return VAProfileMPEG2Main;
   case PIPE_VIDEO_PROFILE_MPEG4_SIMPLE:
      return VAProfileMPEG4Simple;
   case PIPE_VIDEO_PROFILE_MPEG4_ADVANCED_SIMPLE:
      return VAProfileMPEG4AdvancedSimple;
   case PIPE_VIDEO_PROFILE_VC1_SIMPLE:
      return VAProfileVC1Simple;
   case PIPE_VIDEO_PROFILE_VC1_MAIN:
      return VAProfileVC1Main;
   case PIPE_VIDEO_PROFILE_VC1_ADVANCED:
      return VAProfileVC1Advanced;
   case PIPE_VIDEO_PROFILE_MPEG4_AVC_BASELINE:
      return VAProfileH264ConstrainedBaseline;
   case PIPE_VIDEO_PROFILE_MPEG4_AVC_MAIN:
      return VAProfileH264Main;
   case PIPE_VIDEO_PROFILE_MPEG4_AVC_HIGH:
      return VAProfileH264High;
   case PIPE_VIDEO_PROFILE_HEVC_MAIN:
      return VAProfileHEVCMain;
   case PIPE_VIDEO_PROFILE_HEVC_MAIN_10:
      return VAProfileHEVCMain10;
   case PIPE_VIDEO_PROFILE_JPEG_BASELINE:
      return VAProfileJPEGBaseline;
   case PIPE_VIDEO_PROFILE_VP9_PROFILE0:
      return VAProfileVP9Profile0;
   case PIPE_VIDEO_PROFILE_VP9_PROFILE2:
      return VAProfileVP9Profile2;
   case PIPE_VIDEO_PROFILE_AV1_MAIN:
      return VAProfileAV1Profile0;
   case PIPE_VIDEO_PROFILE_MPEG4_AVC_EXTENDED:
   case PIPE_VIDEO_PROFILE_MPEG4_AVC_HIGH10:
   case PIPE_VIDEO_PROFILE_MPEG4_AVC_HIGH422:
   case PIPE_VIDEO_PROFILE_MPEG4_AVC_HIGH444:
   case PIPE_VIDEO_PROFILE_MPEG4_AVC_CONSTRAINED_BASELINE:
   case PIPE_VIDEO_PROFILE_HEVC_MAIN_12:
   case PIPE_VIDEO_PROFILE_HEVC_MAIN_STILL:
   case PIPE_VIDEO_PROFILE_HEVC_MAIN_444:
   case PIPE_VIDEO_PROFILE_UNKNOWN:
      return VAProfileNone;
   default:
      return -1;
   }
}

/*
 * There is no invalid entrypoint defined in libva,
 * so add this definition to make the code clear
 */
#define VAEntrypointNone 0
static int va_entrypoint_from_pipe(enum pipe_video_entrypoint entrypoint)
{
    switch (entrypoint) {
    case PIPE_VIDEO_ENTRYPOINT_BITSTREAM:
        return VAEntrypointVLD;
    case PIPE_VIDEO_ENTRYPOINT_IDCT:
        return VAEntrypointIDCT;
    case PIPE_VIDEO_ENTRYPOINT_MC:
        return VAEntrypointMoComp;
    case PIPE_VIDEO_ENTRYPOINT_ENCODE:
        return VAEntrypointEncSlice;
    default:
        return VAEntrypointNone;
    }
}

static uint32_t va_format_from_pipe_chroma(
        enum pipe_video_chroma_format chroma_format)
{
    switch (chroma_format) {
    case PIPE_VIDEO_CHROMA_FORMAT_400:
        return VA_RT_FORMAT_YUV400;
    case PIPE_VIDEO_CHROMA_FORMAT_420:
        return VA_RT_FORMAT_YUV420;
    case PIPE_VIDEO_CHROMA_FORMAT_422:
        return VA_RT_FORMAT_YUV422;
    case PIPE_VIDEO_CHROMA_FORMAT_444:
        return VA_RT_FORMAT_YUV444;
    case PIPE_VIDEO_CHROMA_FORMAT_NONE:
    default:
        return 0;
    }
}

static uint32_t drm_format_from_va_fourcc(uint32_t va_fourcc)
{
    switch (va_fourcc) {
    case VA_FOURCC_NV12:
        return DRM_FORMAT_NV12;
    case VA_FOURCC_NV21:
        return DRM_FORMAT_NV21;
    default:
        return DRM_FORMAT_INVALID;
    }
}


static void fill_dma_buf(const VADRMPRIMESurfaceDescriptor *desc,
                         struct virgl_video_dma_buf *dmabuf)
{
    unsigned i, j, obj_idx;
    struct virgl_video_dma_buf_plane *plane;

/*
    virgl_log("surface: fourcc=0x%08x, size=%ux%u, num_objects=%u,
              num_layers=%u\n", desc->fourcc, desc->width, desc->height,
              desc->num_objects, desc->num_layers);

    for (i = 0; i < desc->num_objects; i++)
        virgl_log("  objects[%u]: fd=%d, size=%u, modifier=0x%lx\n",
                  i, desc->objects[i].fd, desc->objects[i].size,
                  desc->objects[i].drm_format_modifier);

    for (i = 0; i < desc->num_layers; i++)
        virgl_log("  layers[%u] : format=0x%08x, num_planes=%u, "
                  "obj=%u,%u,%u,%u, offset=%u,%u,%u,%u, pitch=%u,%u,%u,%u\n",
                  i, desc->layers[i].drm_format, desc->layers[i].num_planes,
                  desc->layers[i].object_index[0],
                  desc->layers[i].object_index[1],
                  desc->layers[i].object_index[2],
                  desc->layers[i].object_index[3],
                  desc->layers[i].offset[0],
                  desc->layers[i].offset[1],
                  desc->layers[i].offset[2],
                  desc->layers[i].offset[3],
                  desc->layers[i].pitch[0],
                  desc->layers[i].pitch[1],
                  desc->layers[i].pitch[2],
                  desc->layers[i].pitch[3]);
*/

    dmabuf->drm_format = drm_format_from_va_fourcc(desc->fourcc);
    dmabuf->width = desc->width;
    dmabuf->height = desc->height;

    for (i = 0, dmabuf->num_planes = 0; i < desc->num_layers; i++) {
        for (j = 0; j < desc->layers[i].num_planes &&
                    dmabuf->num_planes < ARRAY_SIZE(dmabuf->planes); j++) {

            obj_idx = desc->layers[i].object_index[j];
            plane = &dmabuf->planes[dmabuf->num_planes++];
            plane->drm_format = desc->layers[i].drm_format;
            plane->offset     = desc->layers[i].offset[j];
            plane->pitch      = desc->layers[i].pitch[j];
            plane->fd         = desc->objects[obj_idx].fd;
            plane->size       = desc->objects[obj_idx].size;
            plane->modifier   = desc->objects[obj_idx].drm_format_modifier;
        }
    }
}

static int flush_video_buffer(struct virgl_video_buffer *buffer)
{
    VAStatus va_stat;

    if (!flush_buffer_cb)
        return 0;

    if (!buffer->exported) {
        va_stat = vaExportSurfaceHandle(va_dpy, buffer->va_sfc,
                                        VA_SURFACE_ATTRIB_MEM_TYPE_DRM_PRIME_2,
                                        VA_EXPORT_SURFACE_READ_ONLY |
                                        VA_EXPORT_SURFACE_SEPARATE_LAYERS,
                                        &buffer->desc);
        if (VA_STATUS_SUCCESS != va_stat) {
            virgl_log("export surface failed, err = 0x%X\n", va_stat);
            return -1;
        }

        fill_dma_buf(&buffer->desc, &buffer->dmabuf);
        buffer->exported = true;
    }

    va_stat = vaSyncSurface(va_dpy, buffer->va_sfc);

    if (VA_STATUS_SUCCESS != va_stat) {
        virgl_log("sync surface failed, err = 0x%x\n", va_stat);
        return -1;
    }

    flush_buffer_cb(buffer, &buffer->dmabuf);

    return 0;
}


int virgl_video_init(int drm_fd,
                     virgl_video_flush_buffer cb, unsigned int flags)
{
    VAStatus va_stat;
    int major_ver, minor_ver;
    const char *driver;

    (void)flags;

    if (drm_fd < 0) {
        virgl_log("invalid drm fd: %d\n", drm_fd);
        return -1;
    }

    va_dpy = vaGetDisplayDRM(drm_fd);
    if (!va_dpy) {
        virgl_log("get va display failed\n");
        return -1;
    }

    va_stat = vaInitialize(va_dpy, &major_ver, &minor_ver);
    if (VA_STATUS_SUCCESS != va_stat) {
        virgl_log("init va library failed\n");
        virgl_video_destroy();
        return -1;
    }

    virgl_log("VA-API version: %d.%d\n", major_ver, minor_ver);

    driver = vaQueryVendorString(va_dpy);
    virgl_log("Driver version: %s\n", driver ? driver : "<unknown>");

    if (!driver || !strstr(driver, "Mesa Gallium")) {
        virgl_log("only supports mesa va drivers now\n");
        virgl_video_destroy();
        return -1;
    }

    flush_buffer_cb = cb;

    return 0;
}

void virgl_video_destroy(void)
{
    if (va_dpy) {
        vaTerminate(va_dpy);
        va_dpy = NULL;
    }

    flush_buffer_cb = NULL;
}

static int fill_vcaps_entry(VAProfile profile, VAEntrypoint entrypoint,
                            struct virgl_video_caps *vcaps)
{
    VAConfigID cfg;
    VASurfaceAttrib *attrs;
    unsigned i, num_attrs;

    /* FIXME: default values */
    vcaps->profile = pipe_profile_from_va(profile);
    vcaps->entrypoint = pipe_entrypoint_from_va(entrypoint);
    vcaps->max_level = 0;
    vcaps->stacked_frames = 0;
    vcaps->max_width = 0;
    vcaps->max_height = 0;
    vcaps->prefered_format = PIPE_FORMAT_NONE;
    vcaps->max_macroblocks = 1;
    vcaps->npot_texture = 1;
    vcaps->supports_progressive = 1;
    vcaps->supports_interlaced = 0;
    vcaps->prefers_interlaced = 0;
    vcaps->max_temporal_layers = 0;

    vaCreateConfig(va_dpy, profile, entrypoint, NULL, 0, &cfg);

    vaQuerySurfaceAttributes(va_dpy, cfg, NULL, &num_attrs);
    attrs = calloc(num_attrs, sizeof(VASurfaceAttrib));
    if (!attrs)
        return -1;

    vaQuerySurfaceAttributes(va_dpy, cfg, attrs, &num_attrs);
    for (i = 0; i < num_attrs; i++) {
        switch (attrs[i].type) {
        case VASurfaceAttribMaxHeight:
            vcaps->max_height = attrs[i].value.value.i;
            break;
        case VASurfaceAttribMaxWidth:
            vcaps->max_width = attrs[i].value.value.i;
            break;
        case VASurfaceAttribPixelFormat:
            if (PIPE_FORMAT_NONE == vcaps->prefered_format)
                vcaps->prefered_format = \
                    pipe_format_from_va_fourcc(attrs[i].value.value.i);
            break;
        default:
            break;
        }
    }

    free(attrs);

    vaDestroyConfig(va_dpy, cfg);

    return 0;
}

int virgl_video_fill_caps(union virgl_caps *caps)
{
    int i, j;
    int num_profiles, num_entrypoints;
    VAProfile *profiles = NULL;
    VAEntrypoint *entrypoints = NULL;

    if (!va_dpy || !caps)
        return -1;

    num_entrypoints = vaMaxNumEntrypoints(va_dpy);
    entrypoints = calloc(num_entrypoints, sizeof(VAEntrypoint));
    if (!entrypoints)
        return -1;

    num_profiles = vaMaxNumProfiles(va_dpy);
    profiles = calloc(num_profiles, sizeof(VAProfile));
    if (!profiles) {
        free(entrypoints);
        return -1;
    }

    vaQueryConfigProfiles(va_dpy, profiles, &num_profiles);
    for (i = 0, caps->v2.num_video_caps = 0; i < num_profiles; i++) {
        /* only support H.264 and H.265 now */
        if (profiles[i] != VAProfileH264Main &&
            profiles[i] != VAProfileH264High &&
            profiles[i] != VAProfileH264ConstrainedBaseline &&
            profiles[i] != VAProfileHEVCMain)
            continue;

        vaQueryConfigEntrypoints(va_dpy, profiles[i],
                                 entrypoints, &num_entrypoints);
        for (j = 0; j < num_entrypoints &&
             caps->v2.num_video_caps < ARRAY_SIZE(caps->v2.video_caps); j++) {
            /* only support decode now */
            if (VAEntrypointVLD != entrypoints[j])
                continue;

            fill_vcaps_entry(profiles[i], entrypoints[j],
                    &caps->v2.video_caps[caps->v2.num_video_caps++]);
        }
    }

    free(profiles);
    free(entrypoints);

    return 0;
}

struct virgl_video_codec *virgl_video_create_codec(
        const struct virgl_video_create_codec_args *args)
{
    VAStatus va_stat;
    VAConfigID cfg;
    VAContextID ctx;
    VAConfigAttrib attr;
    VAProfile profile;
    VAEntrypoint entrypoint;
    uint32_t format;
    struct virgl_video_codec *codec;

    if (!va_dpy || !args)
        return NULL;

    profile = va_profile_from_pipe(args->profile);
    entrypoint = va_entrypoint_from_pipe(args->entrypoint);
    format = va_format_from_pipe_chroma(args->chroma_format);
    if (VAProfileNone == profile || VAEntrypointNone == entrypoint)
        return NULL;

    codec = (struct virgl_video_codec *)calloc(1, sizeof(*codec));
    if (!codec)
        return NULL;

    attr.type = VAConfigAttribRTFormat;
    vaGetConfigAttributes(va_dpy, profile, entrypoint, &attr, 1);
    if (!(attr.value & format)) {
        virgl_log("format 0x%x not supported, supported formats: 0x%x\n",
                  format, attr.value);
        goto err;
    }

    va_stat = vaCreateConfig(va_dpy, profile, entrypoint, &attr, 1, &cfg);
    if (VA_STATUS_SUCCESS != va_stat) {
        virgl_log("create config failed, err = 0x%x\n", va_stat);
        goto err;
    }
    codec->va_cfg = cfg;

    va_stat = vaCreateContext(va_dpy, cfg, args->width, args->height,
                                VA_PROGRESSIVE, NULL, 0, &ctx);
    if (VA_STATUS_SUCCESS != va_stat) {
        virgl_log("create context failed, err = 0x%x\n", va_stat);
        goto err;
    }
    codec->va_ctx = ctx;

    codec->profile = args->profile;
    codec->entrypoint = args->entrypoint;
    codec->chroma_format = args->chroma_format;
    codec->width = args->width;
    codec->height = args->height;

    return codec;

err:
    virgl_video_destroy_codec(codec);

    return NULL;
}

void virgl_video_destroy_codec(struct virgl_video_codec *codec)
{
    if (!va_dpy || !codec)
        return;

    if (codec->va_ctx)
        vaDestroyContext(va_dpy, codec->va_ctx);

    if (codec->va_cfg)
        vaDestroyConfig(va_dpy, codec->va_cfg);

    free(codec);
}

struct virgl_video_buffer *virgl_video_create_buffer(
        const struct virgl_video_create_buffer_args *args)
{
    VAStatus va_stat;
    VASurfaceID sfc;
    uint32_t format;
    struct virgl_video_buffer *buffer;

    if (!va_dpy || !args)
        return NULL;

    /*
     * FIXME: always use YUV420 now,
     * may be use va_format_from_pipe(args->format)
     */
    format = VA_RT_FORMAT_YUV420;
    if (!format) {
        virgl_log("pipe format %d not supported\n", args->format);
        return NULL;
    }

    buffer = (struct virgl_video_buffer *)calloc(1, sizeof(*buffer));
    if (!buffer)
        return NULL;

    va_stat = vaCreateSurfaces(va_dpy, format,
                               args->width, args->height, &sfc, 1, NULL, 0);
    if (VA_STATUS_SUCCESS != va_stat) {
        free(buffer);
        return NULL;
    }

    buffer->va_sfc = sfc;
    buffer->format = args->format;
    buffer->width  = args->width;
    buffer->height = args->height;
    buffer->exported = false;

    return buffer;
}

void virgl_video_destroy_buffer(struct virgl_video_buffer *buffer)
{
    unsigned i;

    if (!va_dpy || !buffer)
        return;

    if (buffer->exported) {
        for (i = 0; i < buffer->desc.num_objects; i++)
            close(buffer->desc.objects[i].fd);
    }

    if (buffer->va_sfc)
        vaDestroySurfaces(va_dpy, &buffer->va_sfc, 1);

    free(buffer);
}

enum pipe_video_profile virgl_video_codec_profile(
        const struct virgl_video_codec *codec)
{
    return codec ? codec->profile : PIPE_VIDEO_PROFILE_UNKNOWN;
}

uint32_t virgl_video_buffer_id(const struct virgl_video_buffer *buffer)
{
    return (uint32_t)(buffer ? buffer->va_sfc : VA_INVALID_SURFACE);
}

void virgl_video_buffer_set_associated_data(
        struct virgl_video_buffer *buffer, void *data)
{
    if (buffer)
        buffer->associated_data = data;
}

void *virgl_video_buffer_get_associated_data(struct virgl_video_buffer *buffer)
{
    return buffer ? buffer->associated_data : NULL;
}

int virgl_video_begin_frame(struct virgl_video_codec *codec,
                            struct virgl_video_buffer *target)
{
    VAStatus va_stat;

    if (!va_dpy || !codec || !target)
        return -1;

    codec->buffer = target;
    va_stat = vaBeginPicture(va_dpy, codec->va_ctx, target->va_sfc);
    if (VA_STATUS_SUCCESS != va_stat) {
        virgl_log("begin picture failed, err = 0x%x\n", va_stat);
        return -1;
    }

    return 0;
}


#define ITEM_SET(dest, src, member) \
        (dest)->member = (src)->member

#define ITEM_CPY(dest, src, member) \
        memcpy(&(dest)->member, &(src)->member, sizeof((dest)->member))


static void h264_init_picture(VAPictureH264 *pic)
{
    pic->picture_id           = VA_INVALID_SURFACE;
    pic->frame_idx            = 0;
    pic->flags                = VA_PICTURE_H264_INVALID;
    pic->TopFieldOrderCnt     = 0;
    pic->BottomFieldOrderCnt  = 0;
}

/*
 * Refer to vlVaHandlePictureParameterBufferH264() in mesa,
 * and comment out some unused parameters.
 */
static void h264_fill_picture_param(struct virgl_video_codec *codec,
                            struct virgl_video_buffer *target,
                            const struct virgl_h264_picture_desc *desc,
                            VAPictureParameterBufferH264 *vapp)
{
    unsigned i;
    VAPictureH264 *pic;

    (void)codec;

    /* CurrPic */
    pic = &vapp->CurrPic;
    pic->picture_id = target->va_sfc;
    pic->frame_idx  = desc->frame_num;
    pic->flags = desc->is_reference ? VA_PICTURE_H264_SHORT_TERM_REFERENCE : 0;
    if (desc->field_pic_flag)
        pic->flags |= (desc->bottom_field_flag ? VA_PICTURE_H264_BOTTOM_FIELD
                                               : VA_PICTURE_H264_TOP_FIELD);
    pic->TopFieldOrderCnt = desc->field_order_cnt[0];
    pic->BottomFieldOrderCnt = desc->field_order_cnt[1];


    /* ReferenceFrames */
    for (i = 0; i < ARRAY_SIZE(vapp->ReferenceFrames); i++)
        h264_init_picture(&vapp->ReferenceFrames[i]);

    for (i = 0; i < desc->num_ref_frames; i++) {
        pic = &vapp->ReferenceFrames[i];

        pic->picture_id = desc->buffer_id[i];
        pic->frame_idx  = desc->frame_num_list[i];
        pic->flags = (desc->is_long_term[i]
                      ? VA_PICTURE_H264_LONG_TERM_REFERENCE
                      : VA_PICTURE_H264_SHORT_TERM_REFERENCE);
        if (desc->top_is_reference[i] && desc->bottom_is_reference[i]) {
            // Full frame. This block intentionally left blank. No flags set.
        } else {
            if (desc->top_is_reference[i])
                pic->flags |= VA_PICTURE_H264_TOP_FIELD;
            else
                pic->flags |= VA_PICTURE_H264_BOTTOM_FIELD;
        }
        pic->TopFieldOrderCnt = desc->field_order_cnt_list[i][0];
        pic->BottomFieldOrderCnt = desc->field_order_cnt_list[i][1];
    }

    //vapp->picture_width_in_mbs_minus1  = (codec->width - 1) / 16;
    //vapp->picture_height_in_mbs_minus1 = (codec->height - 1) / 16;
    ITEM_SET(vapp, &desc->pps.sps, bit_depth_luma_minus8);
    ITEM_SET(vapp, &desc->pps.sps, bit_depth_chroma_minus8);
    ITEM_SET(vapp, desc, num_ref_frames);

    ITEM_SET(&vapp->seq_fields.bits, &desc->pps.sps, chroma_format_idc);
    //vapp->seq_fields.bits.residual_colour_transform_flag       = 0;
    //vapp->seq_fields.bits.gaps_in_frame_num_value_allowed_flag = 0;
    ITEM_SET(&vapp->seq_fields.bits, &desc->pps.sps, frame_mbs_only_flag);
    ITEM_SET(&vapp->seq_fields.bits,
             &desc->pps.sps, mb_adaptive_frame_field_flag);
    ITEM_SET(&vapp->seq_fields.bits, &desc->pps.sps, direct_8x8_inference_flag);
    ITEM_SET(&vapp->seq_fields.bits, &desc->pps.sps, MinLumaBiPredSize8x8);
    ITEM_SET(&vapp->seq_fields.bits, &desc->pps.sps, log2_max_frame_num_minus4);
    ITEM_SET(&vapp->seq_fields.bits, &desc->pps.sps, pic_order_cnt_type);
    ITEM_SET(&vapp->seq_fields.bits,
             &desc->pps.sps, log2_max_pic_order_cnt_lsb_minus4);
    ITEM_SET(&vapp->seq_fields.bits,
             &desc->pps.sps, delta_pic_order_always_zero_flag);

    //ITEM_SET(vapp, &desc->pps, num_slice_groups_minus1);
    //ITEM_SET(vapp, &desc->pps, slice_group_map_type);
    //ITEM_SET(vapp, &desc->pps, slice_group_change_rate_minus1);
    ITEM_SET(vapp, &desc->pps, pic_init_qp_minus26);
    ITEM_SET(vapp, &desc->pps, pic_init_qs_minus26);
    ITEM_SET(vapp, &desc->pps, chroma_qp_index_offset);
    ITEM_SET(vapp, &desc->pps, second_chroma_qp_index_offset);

    ITEM_SET(&vapp->pic_fields.bits, &desc->pps, entropy_coding_mode_flag);
    ITEM_SET(&vapp->pic_fields.bits, &desc->pps, weighted_pred_flag);
    ITEM_SET(&vapp->pic_fields.bits, &desc->pps, weighted_bipred_idc);
    ITEM_SET(&vapp->pic_fields.bits, &desc->pps, transform_8x8_mode_flag);
    ITEM_SET(&vapp->pic_fields.bits, desc,       field_pic_flag);
    ITEM_SET(&vapp->pic_fields.bits, &desc->pps, constrained_intra_pred_flag);
    vapp->pic_fields.bits.pic_order_present_flag =
             desc->pps.bottom_field_pic_order_in_frame_present_flag;
    ITEM_SET(&vapp->pic_fields.bits,
             &desc->pps, deblocking_filter_control_present_flag);
    ITEM_SET(&vapp->pic_fields.bits,
             &desc->pps, redundant_pic_cnt_present_flag);
    vapp->pic_fields.bits.reference_pic_flag = desc->is_reference;

    ITEM_SET(vapp, desc, frame_num);
}


 /* Refer to vlVaHandleIQMatrixBufferH264() in mesa */
static void h264_fill_iq_matrix(const struct virgl_h264_picture_desc *desc,
                                VAIQMatrixBufferH264 *vaiqm)
{
    ITEM_CPY(vaiqm, &desc->pps, ScalingList4x4);
    ITEM_CPY(vaiqm, &desc->pps, ScalingList8x8);
}

/*
 * Refer to vlVaHandleSliceParameterBufferH264() in mesa,
 * and comment out some unused parameters.
 */
static void h264_fill_slice_param(const struct virgl_h264_picture_desc *desc,
                                  VASliceParameterBufferH264 *vasp)
{
    //vasp->slice_data_size;
    //vasp->slice_data_offset;
    //vasp->slice_data_flag;
    //vasp->slice_data_bit_offset;
    //vasp->first_mb_in_slice;
    //vasp->slice_type;
    //vasp->direct_spatial_mv_pred_flag;
    ITEM_SET(vasp, desc, num_ref_idx_l0_active_minus1);
    ITEM_SET(vasp, desc, num_ref_idx_l1_active_minus1);
    //vasp->cabac_init_idc;
    //vasp->slice_qp_delta;
    //vasp->disable_deblocking_filter_idc;
    //vasp->slice_alpha_c0_offset_div2;
    //vasp->slice_beta_offset_div2;
    //vasp->RefPicList0[32];
    //vasp->RefPicList1[32];

    /* see pred_weight_table */
    //vasp->luma_log2_weight_denom;
    //vasp->chroma_log2_weight_denom;
    //vasp->luma_weight_l0_flag;
    //vasp->luma_weight_l0[32];
    //vasp->luma_offset_l0[32];
    //vasp->chroma_weight_l0_flag;
    //vasp->chroma_weight_l0[32][2];
    //vasp->chroma_offset_l0[32][2];
    //vasp->luma_weight_l1_flag;
    //vasp->luma_weight_l1[32];
    //vasp->luma_offset_l1[32];
    //vasp->chroma_weight_l1_flag;
    //vasp->chroma_weight_l1[32][2];
    //vasp->chroma_offset_l1[32][2];
}

static int h264_decode_bitstream(struct virgl_video_codec *codec,
                                 struct virgl_video_buffer *target,
                                 const struct virgl_h264_picture_desc *desc,
                                 unsigned num_buffers,
                                 const void * const *buffers,
                                 const unsigned *sizes)
{
    unsigned i;
    int err = 0;
    VAStatus va_stat;
    VABufferID *slice_data_buf, pic_param_buf, iq_matrix_buf, slice_param_buf;
    VAPictureParameterBufferH264 pic_param;
    VAIQMatrixBufferH264 iq_matrix;
    VASliceParameterBufferH264 slice_param;

    slice_data_buf = calloc(num_buffers, sizeof(VABufferID));
    if (!slice_data_buf) {
        virgl_log("alloc slice data buffer id failed\n");
        return -1;
    }

    h264_fill_picture_param(codec, target, desc, &pic_param);
    vaCreateBuffer(va_dpy, codec->va_ctx, VAPictureParameterBufferType,
                   sizeof(pic_param), 1, &pic_param, &pic_param_buf);

    h264_fill_iq_matrix(desc, &iq_matrix);
    vaCreateBuffer(va_dpy, codec->va_ctx, VAIQMatrixBufferType,
                   sizeof(iq_matrix), 1, &iq_matrix, &iq_matrix_buf);

    h264_fill_slice_param(desc, &slice_param);
    vaCreateBuffer(va_dpy, codec->va_ctx, VASliceParameterBufferType,
                   sizeof(slice_param), 1, &slice_param, &slice_param_buf);

    for (i = 0; i < num_buffers; i++) {
        vaCreateBuffer(va_dpy, codec->va_ctx, VASliceDataBufferType,
                      sizes[i], 1, (void *)(buffers[i]), &slice_data_buf[i]);
    }

    va_stat = vaRenderPicture(va_dpy, codec->va_ctx, &pic_param_buf, 1);
    if (VA_STATUS_SUCCESS != va_stat) {
        virgl_log("render picture param failed, err = 0x%x\n", va_stat);
        err = -1;
        goto err;
    }

    va_stat = vaRenderPicture(va_dpy, codec->va_ctx, &iq_matrix_buf, 1);
    if (VA_STATUS_SUCCESS != va_stat) {
        virgl_log("render iq matrix failed, err = 0x%x\n", va_stat);
        err = -1;
        goto err;
    }

    va_stat = vaRenderPicture(va_dpy, codec->va_ctx, &slice_param_buf, 1);
    if (VA_STATUS_SUCCESS != va_stat) {
        virgl_log("render slice param failed, err = 0x%x\n", va_stat);
        err = -1;
        goto err;
    }

    for (i = 0; i < num_buffers; i++) {
        va_stat = vaRenderPicture(va_dpy, codec->va_ctx, &slice_data_buf[i], 1);

        if (VA_STATUS_SUCCESS != va_stat) {
            virgl_log("render slice data failed, err = 0x%x\n", va_stat);
            err = -1;
        }
    }

err:
    vaDestroyBuffer(va_dpy, pic_param_buf);
    vaDestroyBuffer(va_dpy, iq_matrix_buf);
    vaDestroyBuffer(va_dpy, slice_param_buf);
    for (i = 0; i < num_buffers; i++)
        vaDestroyBuffer(va_dpy, slice_data_buf[i]);
    free(slice_data_buf);

    return err;
}

/*
 * Refer to vlVaHandlePictureParameterBufferHEVC() in mesa,
 * and comment out some unused parameters.
 */
static void h265_fill_picture_param(struct virgl_video_codec *codec,
                            struct virgl_video_buffer *target,
                            const struct virgl_h265_picture_desc *desc,
                            VAPictureParameterBufferHEVC *vapp)
{
    unsigned i;

    (void)codec;
    (void)target;

    //vapp->CurrPic.picture_id
    vapp->CurrPic.pic_order_cnt = desc->CurrPicOrderCntVal;
    //vapp->CurrPic.flags

    for (i = 0; i < 15; i++) {
        vapp->ReferenceFrames[i].pic_order_cnt = desc->PicOrderCntVal[i];
        vapp->ReferenceFrames[i].picture_id = desc->ref[i];
        vapp->ReferenceFrames[i].flags = VA_INVALID_SURFACE == desc->ref[i]
                                       ? VA_PICTURE_HEVC_INVALID : 0;
    }
    for (i = 0; i < desc->NumPocStCurrBefore; i++)
        vapp->ReferenceFrames[desc->RefPicSetStCurrBefore[i]].flags |= \
                VA_PICTURE_HEVC_RPS_ST_CURR_BEFORE;
    for (i = 0; i < desc->NumPocStCurrAfter; i++)
        vapp->ReferenceFrames[desc->RefPicSetStCurrAfter[i]].flags |= \
                VA_PICTURE_HEVC_RPS_ST_CURR_AFTER;
    for (i = 0; i < desc->NumPocLtCurr; i++)
        vapp->ReferenceFrames[desc->RefPicSetLtCurr[i]].flags |= \
                VA_PICTURE_HEVC_RPS_LT_CURR;

    ITEM_SET(vapp, &desc->pps.sps, pic_width_in_luma_samples);
    ITEM_SET(vapp, &desc->pps.sps, pic_height_in_luma_samples);

    ITEM_SET(&vapp->pic_fields.bits, &desc->pps.sps, chroma_format_idc);
    ITEM_SET(&vapp->pic_fields.bits,
             &desc->pps.sps, separate_colour_plane_flag);
    ITEM_SET(&vapp->pic_fields.bits, &desc->pps.sps, pcm_enabled_flag);
    ITEM_SET(&vapp->pic_fields.bits,
             &desc->pps.sps, scaling_list_enabled_flag);
    ITEM_SET(&vapp->pic_fields.bits,
             &desc->pps, transform_skip_enabled_flag);
    ITEM_SET(&vapp->pic_fields.bits, &desc->pps.sps, amp_enabled_flag);
    ITEM_SET(&vapp->pic_fields.bits,
             &desc->pps.sps, strong_intra_smoothing_enabled_flag);
    ITEM_SET(&vapp->pic_fields.bits, &desc->pps, sign_data_hiding_enabled_flag);
    ITEM_SET(&vapp->pic_fields.bits, &desc->pps, constrained_intra_pred_flag);
    ITEM_SET(&vapp->pic_fields.bits, &desc->pps, cu_qp_delta_enabled_flag);
    ITEM_SET(&vapp->pic_fields.bits, &desc->pps, weighted_pred_flag);
    ITEM_SET(&vapp->pic_fields.bits, &desc->pps, weighted_bipred_flag);
    ITEM_SET(&vapp->pic_fields.bits,
             &desc->pps, transquant_bypass_enabled_flag);
    ITEM_SET(&vapp->pic_fields.bits, &desc->pps, tiles_enabled_flag);
    ITEM_SET(&vapp->pic_fields.bits,
             &desc->pps, entropy_coding_sync_enabled_flag);
    ITEM_SET(&vapp->pic_fields.bits, &desc->pps,
             pps_loop_filter_across_slices_enabled_flag);
    if (desc->pps.tiles_enabled_flag)
        ITEM_SET(&vapp->pic_fields.bits,
                 &desc->pps, loop_filter_across_tiles_enabled_flag);
    if (desc->pps.sps.pcm_enabled_flag)
        ITEM_SET(&vapp->pic_fields.bits,
                 &desc->pps.sps, pcm_loop_filter_disabled_flag);
    //ITEM_SET(vapp->pic_fields.bits, desc->pps.sps, NoPicReorderingFlag);
    //ITEM_SET(vapp->pic_fields.bits, desc->pps.sps, NoBiPredFlag);

    ITEM_SET(vapp, &desc->pps.sps, sps_max_dec_pic_buffering_minus1);
    ITEM_SET(vapp, &desc->pps.sps, bit_depth_luma_minus8);
    ITEM_SET(vapp, &desc->pps.sps, bit_depth_chroma_minus8);
    if (desc->pps.sps.pcm_enabled_flag) {
        ITEM_SET(vapp, &desc->pps.sps, pcm_sample_bit_depth_luma_minus1);
        ITEM_SET(vapp, &desc->pps.sps, pcm_sample_bit_depth_chroma_minus1);
    }
    ITEM_SET(vapp, &desc->pps.sps, log2_min_luma_coding_block_size_minus3);
    ITEM_SET(vapp, &desc->pps.sps, log2_diff_max_min_luma_coding_block_size);
    ITEM_SET(vapp, &desc->pps.sps, log2_min_transform_block_size_minus2);
    ITEM_SET(vapp, &desc->pps.sps, log2_diff_max_min_transform_block_size);
    if (desc->pps.sps.pcm_enabled_flag) {
        ITEM_SET(vapp, &desc->pps.sps,
                 log2_min_pcm_luma_coding_block_size_minus3);
        ITEM_SET(vapp, &desc->pps.sps,
                 log2_diff_max_min_pcm_luma_coding_block_size);
    }
    ITEM_SET(vapp, &desc->pps.sps, max_transform_hierarchy_depth_intra);
    ITEM_SET(vapp, &desc->pps.sps, max_transform_hierarchy_depth_inter);
    ITEM_SET(vapp, &desc->pps, init_qp_minus26);
    ITEM_SET(vapp, &desc->pps, diff_cu_qp_delta_depth);
    ITEM_SET(vapp, &desc->pps, pps_cb_qp_offset);
    ITEM_SET(vapp, &desc->pps, pps_cr_qp_offset);
    ITEM_SET(vapp, &desc->pps, log2_parallel_merge_level_minus2);
    if (desc->pps.tiles_enabled_flag) {
        ITEM_SET(vapp, &desc->pps, num_tile_columns_minus1);
        ITEM_SET(vapp, &desc->pps, num_tile_rows_minus1);
        ITEM_CPY(vapp, &desc->pps, column_width_minus1);
        ITEM_CPY(vapp, &desc->pps, row_height_minus1);
    }

    ITEM_SET(&vapp->slice_parsing_fields.bits,
             &desc->pps, lists_modification_present_flag);
    ITEM_SET(&vapp->slice_parsing_fields.bits,
             &desc->pps.sps, long_term_ref_pics_present_flag);
    ITEM_SET(&vapp->slice_parsing_fields.bits,
             &desc->pps.sps, sps_temporal_mvp_enabled_flag);
    ITEM_SET(&vapp->slice_parsing_fields.bits,
             &desc->pps, cabac_init_present_flag);
    ITEM_SET(&vapp->slice_parsing_fields.bits,
             &desc->pps, output_flag_present_flag);
    ITEM_SET(&vapp->slice_parsing_fields.bits,
             &desc->pps, dependent_slice_segments_enabled_flag);
    ITEM_SET(&vapp->slice_parsing_fields.bits,
             &desc->pps, pps_slice_chroma_qp_offsets_present_flag);
    ITEM_SET(&vapp->slice_parsing_fields.bits,
             &desc->pps.sps, sample_adaptive_offset_enabled_flag);
    ITEM_SET(&vapp->slice_parsing_fields.bits,
             &desc->pps, deblocking_filter_override_enabled_flag);
    vapp->slice_parsing_fields.bits.pps_disable_deblocking_filter_flag = \
             desc->pps.pps_deblocking_filter_disabled_flag;
    ITEM_SET(&vapp->slice_parsing_fields.bits,
             &desc->pps, slice_segment_header_extension_present_flag);
    vapp->slice_parsing_fields.bits.RapPicFlag = desc->RAPPicFlag;
    vapp->slice_parsing_fields.bits.IdrPicFlag = desc->IDRPicFlag;
    //vapp->slice_parsing_fields.bits.IntraPicFlag

    ITEM_SET(vapp, &desc->pps.sps, log2_max_pic_order_cnt_lsb_minus4);
    ITEM_SET(vapp, &desc->pps.sps, num_short_term_ref_pic_sets);
    vapp->num_long_term_ref_pic_sps = desc->pps.sps.num_long_term_ref_pics_sps;
    ITEM_SET(vapp, &desc->pps, num_ref_idx_l0_default_active_minus1);
    ITEM_SET(vapp, &desc->pps, num_ref_idx_l1_default_active_minus1);
    ITEM_SET(vapp, &desc->pps, pps_beta_offset_div2);
    ITEM_SET(vapp, &desc->pps, pps_tc_offset_div2);
    ITEM_SET(vapp, &desc->pps, num_extra_slice_header_bits);

    ITEM_SET(vapp, &desc->pps, st_rps_bits);
}

/*
 * Refer to vlVaHandleSliceParameterBufferHEVC() in mesa,
 * and comment out some unused parameters.
 */
static void h265_fill_slice_param(const struct virgl_h265_picture_desc *desc,
                                  VASliceParameterBufferHEVC *vapp)
{
    unsigned i, j;

    //slice_data_size;
    //slice_data_offset;
    //slice_data_flag;
    //slice_data_byte_offset;
    //slice_segment_address;
    for (i = 0; i < 2; i++) {
        for (j = 0; j < 15; j++)
            vapp->RefPicList[i][j] = desc->RefPicList[i][j];
    }
    //LongSliceFlags;
    //collocated_ref_idx;
    //num_ref_idx_l0_active_minus1;
    //num_ref_idx_l1_active_minus1;
    //slice_qp_delta;
    //slice_cb_qp_offset;
    //slice_cr_qp_offset;
    //slice_beta_offset_div2;
    //slice_tc_offset_div2;
    //luma_log2_weight_denom;
    //delta_chroma_log2_weight_denom;
    //delta_luma_weight_l0[15];
    //luma_offset_l0[15];
    //delta_chroma_weight_l0[15][2];
    //ChromaOffsetL0[15][2];
    //delta_luma_weight_l1[15];
    //luma_offset_l1[15];
    //delta_chroma_weight_l1[15][2];
    //ChromaOffsetL1[15][2];
    //five_minus_max_num_merge_cand;
    //num_entry_point_offsets;
    //entry_offset_to_subset_array;
    //slice_data_num_emu_prevn_bytes;
    //va_reserved[VA_PADDING_LOW - 2];
}

static int h265_decode_bitstream(struct virgl_video_codec *codec,
                                 struct virgl_video_buffer *target,
                                 const struct virgl_h265_picture_desc *desc,
                                 unsigned num_buffers,
                                 const void * const *buffers,
                                 const unsigned *sizes)
{
    unsigned i;
    int err = 0;
    VAStatus va_stat;
    VABufferID *slice_data_buf, pic_param_buf, slice_param_buf;
    VAPictureParameterBufferHEVC pic_param = {0};
    VASliceParameterBufferHEVC slice_param = {0};

    slice_data_buf = calloc(num_buffers, sizeof(VABufferID));
    if (!slice_data_buf) {
        virgl_log("alloc slice data buffer id failed\n");
        return -1;
    }

    h265_fill_picture_param(codec, target, desc, &pic_param);
    vaCreateBuffer(va_dpy, codec->va_ctx, VAPictureParameterBufferType,
                   sizeof(pic_param), 1, &pic_param, &pic_param_buf);

    h265_fill_slice_param(desc, &slice_param);
    vaCreateBuffer(va_dpy, codec->va_ctx, VASliceParameterBufferType,
                   sizeof(slice_param), 1, &slice_param, &slice_param_buf);

    for (i = 0; i < num_buffers; i++) {
        vaCreateBuffer(va_dpy, codec->va_ctx, VASliceDataBufferType,
                      sizes[i], 1, (void *)(buffers[i]), &slice_data_buf[i]);
    }

    va_stat = vaRenderPicture(va_dpy, codec->va_ctx, &pic_param_buf, 1);
    if (VA_STATUS_SUCCESS != va_stat) {
        virgl_log("render picture param failed, err = 0x%x\n", va_stat);
        err = -1;
        goto err;
    }

    va_stat = vaRenderPicture(va_dpy, codec->va_ctx, &slice_param_buf, 1);
    if (VA_STATUS_SUCCESS != va_stat) {
        virgl_log("render slice param failed, err = 0x%x\n", va_stat);
        err = -1;
        goto err;
    }

    for (i = 0; i < num_buffers; i++) {
        va_stat = vaRenderPicture(va_dpy, codec->va_ctx, &slice_data_buf[i], 1);

        if (VA_STATUS_SUCCESS != va_stat) {
            virgl_log("render slice data failed, err = 0x%x\n", va_stat);
            err = -1;
        }
    }

err:
    vaDestroyBuffer(va_dpy, pic_param_buf);
    vaDestroyBuffer(va_dpy, slice_param_buf);
    for (i = 0; i < num_buffers; i++)
        vaDestroyBuffer(va_dpy, slice_data_buf[i]);
    free(slice_data_buf);

    return err;
}

int virgl_video_decode_bitstream(struct virgl_video_codec *codec,
                                 struct virgl_video_buffer *target,
                                 const union virgl_picture_desc *desc,
                                 unsigned num_buffers,
                                 const void * const *buffers,
                                 const unsigned *sizes)
{

    if (!va_dpy || !codec || !target || !desc
        || !num_buffers || !buffers || !sizes)
        return -1;

    if (desc->base.profile != codec->profile) {
        virgl_log("profiles not matched, picture: %d, codec: %d\n",
                desc->base.profile, codec->profile);
        return -1;
    }

    switch (codec->profile) {
    case PIPE_VIDEO_PROFILE_MPEG4_AVC_BASELINE:
    case PIPE_VIDEO_PROFILE_MPEG4_AVC_CONSTRAINED_BASELINE:
    case PIPE_VIDEO_PROFILE_MPEG4_AVC_MAIN:
    case PIPE_VIDEO_PROFILE_MPEG4_AVC_EXTENDED:
    case PIPE_VIDEO_PROFILE_MPEG4_AVC_HIGH:
    case PIPE_VIDEO_PROFILE_MPEG4_AVC_HIGH10:
    case PIPE_VIDEO_PROFILE_MPEG4_AVC_HIGH422:
    case PIPE_VIDEO_PROFILE_MPEG4_AVC_HIGH444:
        return h264_decode_bitstream(codec, target, &desc->h264,
                                     num_buffers, buffers, sizes);
    case PIPE_VIDEO_PROFILE_HEVC_MAIN:
    case PIPE_VIDEO_PROFILE_HEVC_MAIN_10:
    case PIPE_VIDEO_PROFILE_HEVC_MAIN_STILL:
    case PIPE_VIDEO_PROFILE_HEVC_MAIN_12:
    case PIPE_VIDEO_PROFILE_HEVC_MAIN_444:
        return h265_decode_bitstream(codec, target, &desc->h265,
                                     num_buffers, buffers, sizes);
    default:
        break;
    }

    return -1;
}

int virgl_video_end_frame(struct virgl_video_codec *codec,
                          struct virgl_video_buffer *target)
{
    VAStatus va_stat;

    if (!va_dpy || !codec || !target)
        return -1;

    va_stat = vaEndPicture(va_dpy, codec->va_ctx);

    if (VA_STATUS_SUCCESS != va_stat) {
        virgl_log("end picture failed, err = 0x%x\n", va_stat);
        return -1;
    }

    flush_video_buffer(target);

    return 0;
}

