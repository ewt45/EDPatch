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

#include "virgl_video.h"

#include "vrend_debug.h"
#include "vrend_winsys.h"
#include "vrend_renderer.h"
#include "vrend_video.h"

struct vrend_context;

struct vrend_video_context {
    struct vrend_context *ctx;
    struct list_head codecs;
    struct list_head buffers;
};

struct vrend_video_codec {
    struct virgl_video_codec *codec;
    uint32_t handle;
    struct vrend_video_context *ctx;
    struct list_head head;
};

struct vrend_video_plane {
    uint32_t res_handle;
    GLuint texture;         /* texture for temporary use */
    GLuint framebuffer;     /* framebuffer for temporary use */
    EGLImageKHR egl_image;  /* egl image for temporary use */
};

struct vrend_video_buffer {
    struct virgl_video_buffer *buffer;

    uint32_t handle;
    struct vrend_video_context *ctx;
    struct list_head head;

    uint32_t num_planes;
    struct vrend_video_plane planes[3];
};

static struct vrend_video_buffer *vrend_video_buffer(
        struct virgl_video_buffer *buffer)
{
    return virgl_video_buffer_get_associated_data(buffer);
}

static struct vrend_video_codec *get_video_codec(
                                        struct vrend_video_context *ctx,
                                        uint32_t cdc_handle)
{
    struct vrend_video_codec *cdc;

    LIST_FOR_EACH_ENTRY(cdc, &ctx->codecs, head) {
        if (cdc->handle == cdc_handle)
            return cdc;
    }

    return NULL;
}

static struct vrend_video_buffer *get_video_buffer(
                                        struct vrend_video_context *ctx,
                                        uint32_t buf_handle)
{
    struct vrend_video_buffer *buf;

    LIST_FOR_EACH_ENTRY(buf, &ctx->buffers, head) {
        if (buf->handle == buf_handle)
            return buf;
    }

    return NULL;
}

static void flush_video_buffer(struct virgl_video_buffer *buffer,
                               const struct virgl_video_dma_buf *dmabuf)
{
    EGLint img_attrs[16];
    unsigned i, num_attrs;
    struct vrend_resource *res;
    struct vrend_video_plane *plane;
    struct vrend_video_buffer *buf = vrend_video_buffer(buffer);

    for (i = 0; i < dmabuf->num_planes && i < buf->num_planes; i++) {
        plane = &buf->planes[i];

        res = vrend_renderer_ctx_res_lookup(buf->ctx->ctx, plane->res_handle);
        if (!res) {
            vrend_printf("%s: res %d not found\n", __func__, plane->res_handle);
            continue;
        }

        /* dmabuf -> eglimage */
        if (EGL_NO_IMAGE_KHR == plane->egl_image) {
            num_attrs = 0;
            img_attrs[num_attrs++] = EGL_LINUX_DRM_FOURCC_EXT;
            img_attrs[num_attrs++] = dmabuf->planes[i].drm_format;
            img_attrs[num_attrs++] = EGL_WIDTH;
            img_attrs[num_attrs++] = dmabuf->width / (i + 1);
            img_attrs[num_attrs++] = EGL_HEIGHT;
            img_attrs[num_attrs++] = dmabuf->height / (i + 1);
            img_attrs[num_attrs++] = EGL_DMA_BUF_PLANE0_FD_EXT;
            img_attrs[num_attrs++] = dmabuf->planes[i].fd;
            img_attrs[num_attrs++] = EGL_DMA_BUF_PLANE0_OFFSET_EXT;
            img_attrs[num_attrs++] = dmabuf->planes[i].offset;
            img_attrs[num_attrs++] = EGL_DMA_BUF_PLANE0_PITCH_EXT;
            img_attrs[num_attrs++] = dmabuf->planes[i].pitch;
            img_attrs[num_attrs++] = EGL_NONE;

            plane->egl_image = eglCreateImageKHR(eglGetCurrentDisplay(),
                    EGL_NO_CONTEXT, EGL_LINUX_DMA_BUF_EXT, NULL, img_attrs);
        }

        if (EGL_NO_IMAGE_KHR == plane->egl_image) {
            vrend_printf("%s: create egl image failed\n", __func__);
            continue;
        }

        /* eglimage -> texture */
        glBindTexture(GL_TEXTURE_2D, plane->texture);
        glEGLImageTargetTexture2DOES(GL_TEXTURE_2D,
                                    (GLeglImageOES)(plane->egl_image));

        /* texture -> framebuffer */
        glBindFramebuffer(GL_READ_FRAMEBUFFER, plane->framebuffer);
        glFramebufferTexture2D(GL_READ_FRAMEBUFFER, GL_COLOR_ATTACHMENT0,
                               GL_TEXTURE_2D, plane->texture, 0);

        /* framebuffer -> surface */
        glBindTexture(GL_TEXTURE_2D, res->id);
        glCopyTexSubImage2D(GL_TEXTURE_2D, 0, 0, 0, 0, 0,
                            res->base.width0, res->base.height0);
    }

    glBindTexture(GL_TEXTURE_2D, 0);
    glBindFramebuffer(GL_FRAMEBUFFER, 0);
}

int vrend_video_init(int drm_fd)
{
    if (drm_fd < 0)
        return -1;

    return virgl_video_init(drm_fd, flush_video_buffer, 0);
}

void vrend_video_fini(void)
{
    virgl_video_destroy();
}

int vrend_video_fill_caps(union virgl_caps *caps)
{
    return virgl_video_fill_caps(caps);
}

int vrend_video_create_codec(struct vrend_video_context *ctx,
                             uint32_t handle,
                             uint32_t profile,
                             uint32_t entrypoint,
                             uint32_t chroma_format,
                             uint32_t level,
                             uint32_t width,
                             uint32_t height,
                             uint32_t flags)
{
    struct vrend_video_codec *cdc = get_video_codec(ctx, handle);
    struct virgl_video_create_codec_args args;

    if (cdc)
        return 0;

    if (profile <= PIPE_VIDEO_PROFILE_UNKNOWN ||
        profile >= PIPE_VIDEO_PROFILE_MAX)
        return -1;

    if (entrypoint <= PIPE_VIDEO_ENTRYPOINT_UNKNOWN ||
        entrypoint > PIPE_VIDEO_ENTRYPOINT_ENCODE)
        return -1;

    if (chroma_format >= PIPE_VIDEO_CHROMA_FORMAT_NONE)
        return -1;

    if (!width || !height)
        return -1;

    cdc = (struct vrend_video_codec *)calloc(1, sizeof(*cdc));
    if (!cdc)
        return -1;

    args.profile = profile;
    args.entrypoint = entrypoint;
    args.chroma_format = chroma_format;
    args.level = level;
    args.width = width;
    args.height = height;
    args.flags = flags;
    cdc->codec = virgl_video_create_codec(&args);
    if (!cdc->codec) {
        free(cdc);
        return -1;
    }

    cdc->handle = handle;
    cdc->ctx = ctx;
    list_add(&cdc->head, &ctx->codecs);

    return 0;
}

static void destroy_video_codec(struct vrend_video_codec *cdc)
{
    if (cdc) {
        list_del(&cdc->head);
        virgl_video_destroy_codec(cdc->codec);
        free(cdc);
    }
}

void vrend_video_destroy_codec(struct vrend_video_context *ctx,
                               uint32_t handle)
{
    struct vrend_video_codec *cdc = get_video_codec(ctx, handle);

    destroy_video_codec(cdc);
}

int vrend_video_create_buffer(struct vrend_video_context *ctx,
                              uint32_t handle,
                              uint32_t format,
                              uint32_t width,
                              uint32_t height,
                              uint32_t *res_handles,
                              unsigned int num_res)
{
    unsigned i;
    struct vrend_video_plane *plane;
    struct vrend_video_buffer *buf = get_video_buffer(ctx, handle);
    struct virgl_video_create_buffer_args args;

    if (buf)
        return 0;

    if (format <= PIPE_FORMAT_NONE || format >= PIPE_FORMAT_COUNT)
        return -1;

    if (!width || !height || !res_handles || !num_res)
        return -1;

    buf = (struct vrend_video_buffer *)calloc(1, sizeof(*buf));
    if (!buf)
        return -1;

    args.format = format;
    args.width = width;
    args.height = height;
    args.interlaced = 0;
    buf->buffer = virgl_video_create_buffer(&args);
    if (!buf->buffer) {
        free(buf);
        return -1;
    }
    virgl_video_buffer_set_associated_data(buf->buffer, buf);

    for (i = 0; i < ARRAY_SIZE(buf->planes); i++)
        buf->planes[i].egl_image = EGL_NO_IMAGE_KHR;

    for (i = 0, buf->num_planes = 0;
         i < num_res && buf->num_planes < ARRAY_SIZE(buf->planes); i++) {

        if (!res_handles[i])
            continue;

        plane = &buf->planes[buf->num_planes++];
        plane->res_handle = res_handles[i];
        glGenFramebuffers(1, &plane->framebuffer);
        glGenTextures(1, &plane->texture);
        glBindTexture(GL_TEXTURE_2D, plane->texture);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    buf->handle = handle;
    buf->ctx = ctx;
    list_add(&buf->head, &ctx->buffers);

    return 0;
}

static void destroy_video_buffer(struct vrend_video_buffer *buf)
{
    unsigned i;
    struct vrend_video_plane *plane;

    if (!buf)
        return;

    list_del(&buf->head);

    for (i = 0; i < buf->num_planes; i++) {
        plane = &buf->planes[i];

        glDeleteTextures(1, &plane->texture);
        glDeleteFramebuffers(1, &plane->framebuffer);
        if (plane->egl_image == EGL_NO_IMAGE_KHR)
            eglDestroyImageKHR(eglGetCurrentDisplay(), plane->egl_image);
    }

    virgl_video_destroy_buffer(buf->buffer);

    free(buf);
}

void vrend_video_destroy_buffer(struct vrend_video_context *ctx,
                                uint32_t handle)
{
    struct vrend_video_buffer *buf = get_video_buffer(ctx, handle);

    destroy_video_buffer(buf);
}

struct vrend_video_context *vrend_video_create_context(struct vrend_context *ctx)
{
    struct vrend_video_context *vctx;

    vctx = (struct vrend_video_context *)calloc(1, sizeof(*vctx));
    if (vctx) {
        vctx->ctx = ctx;
        list_inithead(&vctx->codecs);
        list_inithead(&vctx->buffers);
    }

    return vctx;
}

void vrend_video_destroy_context(struct vrend_video_context *ctx)
{
   struct vrend_video_codec *vcdc, *vcdc_tmp;
   struct vrend_video_buffer *vbuf, *vbuf_tmp;

   LIST_FOR_EACH_ENTRY_SAFE(vcdc, vcdc_tmp, &ctx->codecs, head)
      destroy_video_codec(vcdc);

   LIST_FOR_EACH_ENTRY_SAFE(vbuf, vbuf_tmp, &ctx->buffers, head)
      destroy_video_buffer(vbuf);

   free(ctx);
}

int vrend_video_begin_frame(struct vrend_video_context *ctx,
                            uint32_t cdc_handle,
                            uint32_t tgt_handle)
{
    struct vrend_video_codec *cdc = get_video_codec(ctx, cdc_handle);
    struct vrend_video_buffer *tgt = get_video_buffer(ctx, tgt_handle);

    if (!cdc || !tgt)
        return -1;

    return virgl_video_begin_frame(cdc->codec, tgt->buffer);
}

static void modify_h264_picture_desc(struct vrend_video_codec *cdc,
                                     struct vrend_video_buffer *tgt,
                                     struct virgl_h264_picture_desc *desc)
{
    unsigned i;
    struct vrend_video_buffer *vbuf;

    (void)tgt;

    for (i = 0; i < ARRAY_SIZE(desc->buffer_id); i++) {
        vbuf = get_video_buffer(cdc->ctx, desc->buffer_id[i]);
        desc->buffer_id[i] = virgl_video_buffer_id(vbuf ? vbuf->buffer : NULL);
    }
}

static void modify_h265_picture_desc(struct vrend_video_codec *cdc,
                                     struct vrend_video_buffer *tgt,
                                     struct virgl_h265_picture_desc *desc)
{
    unsigned i;
    struct vrend_video_buffer *vbuf;

    (void)tgt;

    for (i = 0; i < ARRAY_SIZE(desc->ref); i++) {
        vbuf = get_video_buffer(cdc->ctx, desc->ref[i]);
        desc->ref[i] = virgl_video_buffer_id(vbuf ? vbuf->buffer : NULL);
    }
}

static void modify_picture_desc(struct vrend_video_codec *cdc,
                                struct vrend_video_buffer *tgt,
                                union virgl_picture_desc *desc)
{
    switch(virgl_video_codec_profile(cdc->codec)) {
    case PIPE_VIDEO_PROFILE_MPEG4_AVC_BASELINE:
    case PIPE_VIDEO_PROFILE_MPEG4_AVC_CONSTRAINED_BASELINE:
    case PIPE_VIDEO_PROFILE_MPEG4_AVC_MAIN:
    case PIPE_VIDEO_PROFILE_MPEG4_AVC_EXTENDED:
    case PIPE_VIDEO_PROFILE_MPEG4_AVC_HIGH:
    case PIPE_VIDEO_PROFILE_MPEG4_AVC_HIGH10:
    case PIPE_VIDEO_PROFILE_MPEG4_AVC_HIGH422:
    case PIPE_VIDEO_PROFILE_MPEG4_AVC_HIGH444:
        modify_h264_picture_desc(cdc, tgt, &desc->h264);
        break;
    case PIPE_VIDEO_PROFILE_HEVC_MAIN:
    case PIPE_VIDEO_PROFILE_HEVC_MAIN_10:
    case PIPE_VIDEO_PROFILE_HEVC_MAIN_STILL:
    case PIPE_VIDEO_PROFILE_HEVC_MAIN_12:
    case PIPE_VIDEO_PROFILE_HEVC_MAIN_444:
        modify_h265_picture_desc(cdc, tgt, &desc->h265);
        break;
    default:
        break;
    }
}

int vrend_video_decode_bitstream(struct vrend_video_context *ctx,
                                 uint32_t cdc_handle,
                                 uint32_t tgt_handle,
                                 uint32_t desc_handle,
                                 unsigned num_buffers,
                                 const uint32_t *buffer_handles,
                                 const uint32_t *buffer_sizes)
{
    int err = -1;
    unsigned i, num_bs, *bs_sizes = NULL;
    void **bs_buffers = NULL;
    struct vrend_resource *res;
    struct vrend_video_codec  *cdc = get_video_codec(ctx, cdc_handle);
    struct vrend_video_buffer *tgt = get_video_buffer(ctx, tgt_handle);
    union virgl_picture_desc desc;

    if (!cdc || !tgt)
        return -1;

    bs_buffers = calloc(num_buffers, sizeof(void *));
    if (!bs_buffers) {
        vrend_printf("%s: alloc bs_buffers failed\n", __func__);
        return -1;
    }

    bs_sizes = calloc(num_buffers, sizeof(unsigned));
    if (!bs_sizes) {
        vrend_printf("%s: alloc bs_sizes failed\n", __func__);
        goto err;
    }

    for (i = 0, num_bs = 0; i < num_buffers; i++) {
        res = vrend_renderer_ctx_res_lookup(ctx->ctx, buffer_handles[i]);
        if (!res || !res->ptr) {
            vrend_printf("%s: bs res %d invalid or not found",
                         __func__, buffer_handles[i]);
            continue;
        }

        vrend_read_from_iovec(res->iov, res->num_iovs, 0,
                              res->ptr, buffer_sizes[i]);
        bs_buffers[num_bs] = res->ptr;
        bs_sizes[num_bs] = buffer_sizes[i];
        num_bs++;
    }

    res = vrend_renderer_ctx_res_lookup(ctx->ctx, desc_handle);
    if (!res) {
        vrend_printf("%s: desc res %d not found\n", __func__, desc_handle);
        goto err;
    }
    vrend_read_from_iovec(res->iov, res->num_iovs,
                          0, (char *)(&desc), sizeof(desc));
    modify_picture_desc(cdc, tgt, &desc);

    err = virgl_video_decode_bitstream(cdc->codec, tgt->buffer, &desc,
                           num_bs, (const void * const *)bs_buffers, bs_sizes);

err:
    free(bs_buffers);
    free(bs_sizes);

    return err;
}


int vrend_video_end_frame(struct vrend_video_context *ctx,
                          uint32_t cdc_handle,
                          uint32_t tgt_handle)
{
    struct vrend_video_codec *cdc = get_video_codec(ctx, cdc_handle);
    struct vrend_video_buffer *tgt = get_video_buffer(ctx, tgt_handle);

    if (!cdc || !tgt)
        return -1;

    return virgl_video_end_frame(cdc->codec, tgt->buffer);
}

