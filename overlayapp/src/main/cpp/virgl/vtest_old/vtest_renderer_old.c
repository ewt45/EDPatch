/**************************************************************************
 *
 * Copyright (C) 2015 Red Hat Inc.
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
#include <pthread.h>
#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <unistd.h>
#include <fcntl.h>
#include <limits.h>
#include "virglrenderer.h"
#include <sys/uio.h>
#include "vtest_old.h"
#include "vtest_protocol_old.h"
#include "util/u_debug.h"

#include <epoxy/egl.h>
#include "vrend_renderer.h"
#include "vrend_object.h"

#ifdef  ANDROID_JNI
#include <jni.h>
#include <android/native_window.h>
#include <android/native_window_jni.h>
#include <android/log.h>
#define printf(...) __android_log_print(ANDROID_LOG_DEBUG, "virgl", __VA_ARGS__)
#endif

#define FL_GLX (1<<1)
#define FL_GLES (1<<2)
#define FL_OVERLAY (1<<3)
#define FL_MULTITHREAD (1<<4)

extern struct vrend_context *overlay_ctx; //

int dxtn_decompress; //DXTn (S3TC) decompress.

static char sock_path_old[128];
static int flags_old;

pthread_mutex_t mutex_old = PTHREAD_MUTEX_INITIALIZER;
EGLDisplay disp_old;

struct dt_record_old
{
    uint32_t used;
    EGLSurface egl_surf;
    int fb_id;
    int res_id;
#ifdef ANDROID_JNI
    jobject java_surf;
#endif
};

struct vtest_renderer_old {
    // pipe
    int fd;
    int flags;

    // egl
    EGLDisplay egl_display;
    EGLConfig egl_conf;
    EGLContext egl_ctx;
    EGLSurface egl_fake_surf;

    // renderer
    int ctx_id;
    int fence_id;
    int last_fence;

    struct dt_record_old dts[32];
#ifdef ANDROID_JNI
    struct jni_s_old
    {
        jclass cls;
        JNIEnv *env;
        jmethodID create;
        jmethodID get_surface;
        jmethodID set_rect;
        jmethodID destroy;
    } jni;
#endif
};

static void vtest_write_fence_old(void *cookie, uint32_t fence_id_in)
{
    struct vtest_renderer_old *r = cookie;
    r->last_fence = fence_id_in;
}

static int vtest_wait_for_fd_read_old(struct vtest_renderer_old *r)
{
    fd_set read_fds;
    int ret;

    return 0;

    FD_ZERO(&read_fds);
    FD_SET(r->fd, &read_fds);

    ret = select(r->fd + 1, &read_fds, NULL, NULL, NULL);
    if (ret < 0)
        return ret;

    if (FD_ISSET(r->fd, &read_fds)) {
        return 0;
    }
    return -1;
}

static int vtest_block_write_old(struct vtest_renderer_old *r, void *buf, int size)
{
    void *ptr = buf;
    int left;
    int ret;

    left = size;
    do {
        ret = write(r->fd, ptr, left);
        if (ret < 0)
            return -errno;

        left -= ret;
        ptr += ret;

    } while (left);
    return size;
}

static int vtest_block_read_old(struct vtest_renderer_old *r, void *buf, int size)
{
    void *ptr = buf;
    int left;
    int ret;
    static int savefd = -1;

    left = size;
    do {
        ret = read(r->fd, ptr, left);
        if (ret <= 0)
            return ret == -1 ? -errno : 0;
        left -= ret;
        ptr += ret;
    } while (left);
    if (getenv("VTEST_SAVE")) {
        if (savefd == -1) {
            savefd = open(getenv("VTEST_SAVE"),
                          O_CLOEXEC|O_CREAT|O_WRONLY|O_TRUNC|O_DSYNC, S_IRUSR|S_IWUSR);
            if (savefd == -1) {
                perror("error opening save file");
                exit(1);
            }
        }
        if (write(savefd, buf, size) != size) {
            perror("failed to save");
            exit(1);
        }
    }
    return size;
}

static int vtest_send_caps2_old(struct vtest_renderer_old *r)
{
    uint32_t hdr_buf[2];
    void *caps_buf;
    int ret;
    uint32_t max_ver, max_size;

    virgl_renderer_get_cap_set(2, &max_ver, &max_size);

    if (max_size == 0)
        return -1;
    caps_buf = malloc(max_size);
    if (!caps_buf)
        return -1;

    virgl_renderer_fill_caps(2, 1, caps_buf);

    hdr_buf[0] = max_size + 1;
    hdr_buf[1] = 2;
    ret = vtest_block_write_old(r, hdr_buf, 8);
    if (ret < 0)
        goto end;
    vtest_block_write_old(r, caps_buf, max_size);
    if (ret < 0)
        goto end;

    end:
    free(caps_buf);
    return 0;
}

static int vtest_send_caps_old(struct vtest_renderer_old *r)
{
    uint32_t  max_ver, max_size;
    void *caps_buf;
    uint32_t hdr_buf[2];
    int ret;

    virgl_renderer_get_cap_set(1, &max_ver, &max_size);

    caps_buf = malloc(max_size);
    if (!caps_buf)
        return -1;

    virgl_renderer_fill_caps(1, 1, caps_buf);

    hdr_buf[0] = max_size + 1;
    hdr_buf[1] = 1;
    ret = vtest_block_write_old(r, hdr_buf, 8);
    if (ret < 0)
        goto end;
    vtest_block_write_old(r, caps_buf, max_size);
    if (ret < 0)
        goto end;

    end:
    free(caps_buf);
    return 0;
}

static int vtest_create_resource_old(struct vtest_renderer_old *r)
{
    uint32_t res_create_buf[VCMD_RES_CREATE_SIZE];
    struct virgl_renderer_resource_create_args args;
    int ret;

    ret = vtest_block_read_old(r, &res_create_buf, sizeof(res_create_buf));
    if (ret != sizeof(res_create_buf))
        return -1;

    args.handle = res_create_buf[VCMD_RES_CREATE_RES_HANDLE];
    args.target = res_create_buf[VCMD_RES_CREATE_TARGET];
    args.format = res_create_buf[VCMD_RES_CREATE_FORMAT];
    args.bind = res_create_buf[VCMD_RES_CREATE_BIND];

    args.width = res_create_buf[VCMD_RES_CREATE_WIDTH];
    args.height = res_create_buf[VCMD_RES_CREATE_HEIGHT];
    args.depth = res_create_buf[VCMD_RES_CREATE_DEPTH];
    args.array_size = res_create_buf[VCMD_RES_CREATE_ARRAY_SIZE];
    args.last_level = res_create_buf[VCMD_RES_CREATE_LAST_LEVEL];
    args.nr_samples = res_create_buf[VCMD_RES_CREATE_NR_SAMPLES];
    args.flags = 0;

    ret = virgl_renderer_resource_create(&args, NULL, 0);

    virgl_renderer_ctx_attach_resource(r->ctx_id, args.handle);
    return ret;
}

static int vtest_resource_unref_old(struct vtest_renderer_old *r)
{
    uint32_t res_unref_buf[VCMD_RES_UNREF_SIZE];
    int ret;
    uint32_t handle;

    ret = vtest_block_read_old(r, &res_unref_buf, sizeof(res_unref_buf));
    if (ret != sizeof(res_unref_buf))
        return -1;

    handle = res_unref_buf[VCMD_RES_UNREF_RES_HANDLE];
    virgl_renderer_ctx_attach_resource(r->ctx_id, handle);
    virgl_renderer_resource_unref(handle);
    return 0;
}

static int vtest_submit_cmd_old(struct vtest_renderer_old *r, uint32_t length_dw)
{
    uint32_t *cbuf;
    int ret;

    if (length_dw > UINT_MAX / 4)
        return -1;

    cbuf = malloc(length_dw * 4);
    if (!cbuf)
        return -1;

    ret = vtest_block_read_old(r, cbuf, length_dw * 4);
    if (ret != (int)length_dw * 4) {
        free(cbuf);
        return -1;
    }

    virgl_renderer_submit_cmd(cbuf, r->ctx_id, length_dw);

    free(cbuf);
    return 0;
}

#define DECODE_TRANSFER_OLD \
  do {							\
  handle = thdr_buf[VCMD_TRANSFER_RES_HANDLE];		\
  level = thdr_buf[VCMD_TRANSFER_LEVEL];		\
  stride = thdr_buf[VCMD_TRANSFER_STRIDE];		\
  layer_stride = thdr_buf[VCMD_TRANSFER_LAYER_STRIDE];	\
  box.x = thdr_buf[VCMD_TRANSFER_X];			\
  box.y = thdr_buf[VCMD_TRANSFER_Y];			\
  box.z = thdr_buf[VCMD_TRANSFER_Z];			\
  box.w = thdr_buf[VCMD_TRANSFER_WIDTH];		\
  box.h = thdr_buf[VCMD_TRANSFER_HEIGHT];		\
  box.d = thdr_buf[VCMD_TRANSFER_DEPTH];		\
  data_size = thdr_buf[VCMD_TRANSFER_DATA_SIZE];		\
  } while(0)

static int vtest_transfer_get_old(struct vtest_renderer_old *r, UNUSED uint32_t length_dw)
{
    uint32_t thdr_buf[VCMD_TRANSFER_HDR_SIZE];
    int ret;
    int level;
    uint32_t stride, layer_stride, handle;
    struct virgl_box box;
    uint32_t data_size;
    void *ptr;
    struct iovec iovec;

    ret = vtest_block_read_old(r, thdr_buf, VCMD_TRANSFER_HDR_SIZE * 4);
    if (ret != VCMD_TRANSFER_HDR_SIZE * 4)
        return ret;

    DECODE_TRANSFER_OLD;

    ptr = malloc(data_size);
    if (!ptr)
        return -ENOMEM;

    iovec.iov_len = data_size;
    iovec.iov_base = ptr;
    ret = virgl_renderer_transfer_read_iov(handle,
                                           r->ctx_id,
                                           level,
                                           stride,
                                           layer_stride,
                                           &box,
                                           0,
                                           &iovec, 1);
    if (ret)
        fprintf(stderr," transfer read failed %d\n", ret);
    ret = vtest_block_write_old(r, ptr, data_size);

    free(ptr);
    return ret < 0 ? ret : 0;
}

static int vtest_transfer_put_old(struct vtest_renderer_old *r, UNUSED uint32_t length_dw)
{
    uint32_t thdr_buf[VCMD_TRANSFER_HDR_SIZE];
    int ret;
    int level;
    uint32_t stride, layer_stride, handle;
    struct virgl_box box;
    uint32_t data_size;
    void *ptr;
    struct iovec iovec;

    ret = vtest_block_read_old(r, thdr_buf, VCMD_TRANSFER_HDR_SIZE * 4);
    if (ret != VCMD_TRANSFER_HDR_SIZE * 4)
        return ret;

    DECODE_TRANSFER_OLD;

    ptr = malloc(data_size);
    if (!ptr)
        return -ENOMEM;

    ret = vtest_block_read_old(r, ptr, data_size);
    if (ret < 0)
        return ret;

    iovec.iov_len = data_size;
    iovec.iov_base = ptr;
    ret = virgl_renderer_transfer_write_iov(handle,
                                            r->ctx_id,
                                            level,
                                            stride,
                                            layer_stride,
                                            &box,
                                            0,
                                            &iovec, 1);
    if (ret)
        fprintf(stderr," transfer write failed %d\n", ret);
    free(ptr);
    return 0;
}

static int vtest_resource_busy_wait_old(struct vtest_renderer_old *r)
{
    uint32_t bw_buf[VCMD_BUSY_WAIT_SIZE];
    int ret, fd;
    //int flags;
    uint32_t hdr_buf[VTEST_HDR_SIZE];
    uint32_t reply_buf[1];
    bool busy = false;
    ret = vtest_block_read_old(r, &bw_buf, sizeof(bw_buf));
    if (ret != sizeof(bw_buf))
        return -1;

    /*  handle = bw_buf[VCMD_BUSY_WAIT_HANDLE]; unused as of now */
    flags_old = bw_buf[VCMD_BUSY_WAIT_FLAGS];

    if (flags_old == VCMD_BUSY_WAIT_FLAG_WAIT) {
        do {
            if (r->last_fence == (r->fence_id - 1))
                break;

            fd = virgl_renderer_get_poll_fd();
            if (fd != -1)
                vtest_wait_for_fd_read_old(r);
            virgl_renderer_poll();
        } while (1);
        busy = false;
    } else {
        busy = r->last_fence != (r->fence_id - 1);
    }

    hdr_buf[VTEST_CMD_LEN] = 1;
    hdr_buf[VTEST_CMD_ID] = VCMD_RESOURCE_BUSY_WAIT;
    reply_buf[0] = busy ? 1 : 0;

    ret = vtest_block_write_old(r, hdr_buf, sizeof(hdr_buf));
    if (ret < 0)
        return ret;

    ret = vtest_block_write_old(r, reply_buf, sizeof(reply_buf));
    if (ret < 0)
        return ret;

    return 0;
}

static int vtest_renderer_create_fence_old(struct vtest_renderer_old *r)
{
    virgl_renderer_create_fence(r->fence_id++, r->ctx_id);
    return 0;
}

static int vtest_poll_old()
{
    virgl_renderer_poll();
    return 0;
}

//TODO: EGL

static bool vtest_egl_init_old(struct vtest_renderer_old *d, bool surfaceless, bool gles)
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
    EGLBoolean b;
    EGLenum api;
    EGLint major, minor, n;
    const char *extension_list;

    if (gles)
        conf_att[3] = EGL_OPENGL_ES_BIT;

    if (surfaceless) {
        conf_att[1] = EGL_PBUFFER_BIT;
    }

    const char *client_extensions = eglQueryString (NULL, EGL_EXTENSIONS);

    d->egl_display =  eglGetDisplay(EGL_DEFAULT_DISPLAY);

    if (!d->egl_display)
        goto fail;

    b = eglInitialize(d->egl_display, &major, &minor);
    if (!b)
        goto fail;

    extension_list = eglQueryString(d->egl_display, EGL_EXTENSIONS);

    printf("EGL major/minor: %d.%d\n", major, minor);
    printf("EGL version: %s\n", eglQueryString(d->egl_display, EGL_VERSION));
    printf("EGL vendor: %s\n", eglQueryString(d->egl_display, EGL_VENDOR));
    printf("EGL extensions: %s\n", extension_list);

    if (gles)
        api = EGL_OPENGL_ES_API;
    else
        api = EGL_OPENGL_API;
    b = eglBindAPI(api);
    if (!b)
        goto fail;

    b = eglChooseConfig(d->egl_display, conf_att, &d->egl_conf,
                        1, &n);

    if (!b || n != 1)
        goto fail;

    d->egl_ctx = eglCreateContext(d->egl_display,
                                  d->egl_conf,
                                  EGL_NO_CONTEXT,
                                  ctx_att);
    if (!d->egl_ctx)
        goto fail;

    static EGLint const window_attribute_list[] = {
            EGL_RENDER_BUFFER, EGL_BACK_BUFFER,
            EGL_NONE,
    };

    struct vtest_renderer_old *r = d;
    jobject surf = (*r->jni.env)->CallStaticObjectMethod(r->jni.env, r->jni.cls, r->jni.get_surface,(*r->jni.env)->CallStaticObjectMethod(r->jni.env, r->jni.cls, r->jni.create, 0, 0, 0, 0));
    if(surf == 0)exit(0);
    ANativeWindow *window = ANativeWindow_fromSurface(r->jni.env, surf);
    int format;
    eglGetConfigAttrib(d->egl_display, d->egl_conf, EGL_NATIVE_VISUAL_ID, &format);
    ANativeWindow_setBuffersGeometry(window, 0, 0, format);
    d->egl_fake_surf = eglCreateWindowSurface(d->egl_display, d->egl_conf, window, 0);

    eglMakeCurrent(d->egl_display, d->egl_fake_surf, d->egl_fake_surf, d->egl_ctx);
    return true;
    fail:
    return false;
}

static virgl_renderer_gl_context vtest_egl_create_context_old(void *cookie, int scanout_idx, struct virgl_renderer_gl_ctx_param *param)
{
    struct vtest_renderer_old *ve = cookie;
    EGLContext eglctx;
    EGLint ctx_att[] = {
            EGL_CONTEXT_CLIENT_VERSION, param->major_ver,
            EGL_CONTEXT_MINOR_VERSION_KHR, param->minor_ver,
            EGL_NONE
    };


    eglctx = eglCreateContext(ve->egl_display,
                              ve->egl_conf,
                              param->shared ? eglGetCurrentContext() :
                              ve->egl_ctx,
                              ctx_att);

    //printf("create_context %d %d %d %d %x\n", scanout_idx, param->shared, param->major_ver, param->minor_ver, eglctx);

    return (virgl_renderer_gl_context)eglctx;
}

static void vtest_egl_destroy_context_old(void *cookie, virgl_renderer_gl_context ctx)
{
    struct vtest_renderer_old *ve = cookie;
    EGLContext eglctx = (EGLContext)ctx;

    //printf("destroy_context %x\n", ctx);

    eglDestroyContext(ve->egl_display, eglctx);
}

static int vtest_egl_make_context_current_old(void *cookie, int scanout_idx, virgl_renderer_gl_context ctx)
{
    struct vtest_renderer_old *ve = cookie;
    EGLContext eglctx = (EGLContext)ctx;
    if( ctx == ve->egl_ctx )
        return eglMakeCurrent(ve->egl_display, ve->egl_fake_surf ,ve->egl_fake_surf, eglctx);
    else
        return eglMakeCurrent(ve->egl_display, EGL_NO_SURFACE, EGL_NO_SURFACE, eglctx);

}

struct virgl_renderer_callbacks vtest_cbs_old = {
        .version = 1,
        .write_fence = vtest_write_fence_old,
        .create_gl_context = vtest_egl_create_context_old,
        .destroy_gl_context = vtest_egl_destroy_context_old,
        .make_current = vtest_egl_make_context_current_old,
};

//TODO: DT

static void vtest_dt_destroy_old(struct vtest_renderer_old *r, struct dt_record_old *dt)
{
#ifdef ANDROID_JNI
    if( dt->java_surf )
        (*r->jni.env)->CallStaticVoidMethod(r->jni.env, r->jni.cls, r->jni.destroy, dt->java_surf);
    dt->java_surf = 0;
#endif

    if( dt->egl_surf )
    {
        eglMakeCurrent( r->egl_display, r->egl_fake_surf, r->egl_fake_surf, r->egl_ctx);
        eglDestroySurface(r->egl_display, dt->egl_surf);
        dt->egl_surf = 0;
    }
}

static void vtest_dt_flush_old(struct vtest_renderer_old *r, struct dt_record_old *dt, int handle, int x, int y, int w, int h)
{
    eglMakeCurrent(r->egl_display, dt->egl_surf, dt->egl_surf, r->egl_ctx);

    if(!dt->fb_id)
        glGenFramebuffersEXT(1,&dt->fb_id);

    glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, dt->fb_id );

    // use internal API here to get texture id
    if( handle != dt->res_id)
    {
        struct vrend_resource *res;

        res = vrend_renderer_ctx_res_lookup(overlay_ctx, handle); //

        glFramebufferTexture2DEXT(GL_FRAMEBUFFER_EXT, GL_COLOR_ATTACHMENT0_EXT, GL_TEXTURE_2D, res->id, 0);

        dt->res_id = handle;
    }

    glReadBuffer(GL_COLOR_ATTACHMENT0_EXT);

    glBindFramebufferEXT(GL_DRAW_FRAMEBUFFER_EXT, 0);
    glBlitFramebuffer(x,y+h,w+x,y,x,y,w+x,h+y,GL_COLOR_BUFFER_BIT,GL_NEAREST);

    eglSwapBuffers(r->egl_display, dt->egl_surf);
}

static void vtest_dt_set_rect_old(struct vtest_renderer_old *r, struct dt_record_old *dt, int visible, int x, int y, int w, int h)
{
#ifdef ANDROID_JNI
    (*r->jni.env)->CallStaticVoidMethod(r->jni.env, r->jni.cls, r->jni.set_rect, dt->java_surf,x,y,w,h,visible);
#endif
}

static void vtest_dt_create_old(struct vtest_renderer_old *r, struct dt_record_old *dt, int drawable, int x, int y, int w, int h)
{
#ifdef ANDROID_JNI
    if(!dt->egl_surf)
    {
        dt->java_surf = (*r->jni.env)->CallStaticObjectMethod(r->jni.env, r->jni.cls, r->jni.create, x, y, w, h);
        jobject surf = (*r->jni.env)->CallStaticObjectMethod(r->jni.env, r->jni.cls, r->jni.get_surface, dt->java_surf);
        ANativeWindow *window = ANativeWindow_fromSurface(r->jni.env, surf);
        int format;
        eglGetConfigAttrib(r->egl_display, r->egl_conf, EGL_NATIVE_VISUAL_ID, &format);
        ANativeWindow_setBuffersGeometry(window, 0, 0, format);
        dt->egl_surf = eglCreateWindowSurface(r->egl_display, r->egl_conf, window, 0);
        //r->egl_drawable_surf = r->egl_fake_surf;
    }
#endif
}

static int vtest_dt_cmd_old(struct vtest_renderer_old *r)
{
    uint32_t flush_buf[VCMD_DT_SIZE];
    int ret;
    uint32_t cmd, x, y, w, h, handle;
    uint32_t drawable, id;

    ret = vtest_block_read_old(r, &flush_buf, sizeof(flush_buf));
    if (ret != sizeof(flush_buf))
        return -1;

    //EGLContext ctx = eglGetCurrentContext();

    drawable = flush_buf[VCMD_DT_DRAWABLE];
    x = flush_buf[VCMD_DT_X];
    y = flush_buf[VCMD_DT_Y];
    w = flush_buf[VCMD_DT_WIDTH];
    h = flush_buf[VCMD_DT_HEIGHT];
    id = flush_buf[VCMD_DT_ID];
    cmd = flush_buf[VCMD_DT_CMD];
    handle = flush_buf[VCMD_DT_HANDLE];

    //printf("dt_cmd %d %d %d %d %d %d %d %d\n", cmd, x, y, w, h, id, handle, drawable);

    struct dt_record_old *dt = &r->dts[id];

    if( cmd == VCMD_DT_CMD_CREATE )
        vtest_dt_create_old(r, dt, drawable, x, y, w, h);
    else if(cmd == VCMD_DT_CMD_DESTROY)
        vtest_dt_destroy_old(r, dt);
    else if(cmd == VCMD_DT_CMD_SET_RECT)
        vtest_dt_set_rect_old(r, dt, drawable, x, y, w, h);
    if( cmd == VCMD_DT_CMD_FLUSH )
        vtest_dt_flush_old(r, dt, handle, x, y, w, h);
    return 0;
}

//TODO: RENDERER

static int vtest_create_renderer_old(struct vtest_renderer_old *r, uint32_t length)
{
    char *vtestname;
    int ret;
    int ctx = 0;

    if (getenv("VTEST_USE_EGL_SURFACELESS")) {
        if (r->flags & FL_GLX) {
            fprintf(stderr, "Cannot use surfaceless with GLX.\n");
            return -1;
        }
        ctx |= VIRGL_RENDERER_USE_SURFACELESS;
    }

    if (r->flags & FL_GLES) {
        if (r->flags & FL_GLX) {
            fprintf(stderr, "Cannot use GLES with GLX.\n");
            return -1;
        }
        ctx |= VIRGL_RENDERER_USE_GLES;
    }

    if( !(r->flags & FL_GLX) )
        vtest_egl_init_old(r, false, (ctx & VIRGL_RENDERER_USE_GLES) != 0);

    ret = virgl_renderer_init(r, ctx | VIRGL_RENDERER_THREAD_SYNC, &vtest_cbs_old);
    if (ret) {
        fprintf(stderr, "failed to initialise renderer.\n");
        return -1;
    }

    vtestname = calloc(1, length + 1);
    if (!vtestname)
        return -1;

    ret = vtest_block_read_old(r, vtestname, length);
    if (ret != (int)length) {
        ret = -1;
        goto end;
    }

    ret = virgl_renderer_context_create(r->ctx_id, strlen(vtestname), vtestname);

    end:
    free(vtestname);
    return ret;
}

static void vtest_destroy_renderer_old(struct vtest_renderer_old *r)
{
    int i;

    for( i = 0; i < 32; i++)
        vtest_dt_destroy_old(r, &r->dts[i]);

    virgl_renderer_context_destroy(r->ctx_id);
    virgl_renderer_cleanup(r);
}

void *create_renderer_old(int in_fd, int ctx_id)
{
    struct vtest_renderer_old *r = calloc(1, sizeof(struct vtest_renderer_old));

    r->ctx_id = ctx_id;
    r->fence_id = 1;
    r->fd = in_fd;

    return r;
}

int renderer_loop_old(void *d)
{
    int ret;
    uint32_t header[VTEST_HDR_SIZE];
    bool inited = false;
    struct vtest_renderer_old *r = d;
    EGLContext ctx = 0;
    EGLSurface surf = 0;

    again:
    ret = vtest_wait_for_fd_read_old(r);
    if (ret < 0)
        goto fail;

    ret = vtest_block_read_old(r, &header, sizeof(header));

    if (ret == 8) {
        if (!inited) {
            if (header[1] != VCMD_CREATE_RENDERER)
                goto fail;
            ret = vtest_create_renderer_old(r, header[0]);
            inited = true;
        }
        vtest_poll_old();
        switch (header[1]) {
            case VCMD_GET_CAPS:
                ret = vtest_send_caps_old(r);
                break;
            case VCMD_RESOURCE_CREATE:
                ret = vtest_create_resource_old(r);
                break;
            case VCMD_RESOURCE_UNREF:
                ret = vtest_resource_unref_old(r);
                break;
            case VCMD_SUBMIT_CMD:
                ret = vtest_submit_cmd_old(r, header[0]);
                break;
            case VCMD_TRANSFER_GET:
                ret = vtest_transfer_get_old(r, header[0]);
                break;
            case VCMD_TRANSFER_PUT:
                ret = vtest_transfer_put_old(r, header[0]);
                break;
            case VCMD_RESOURCE_BUSY_WAIT:
                vtest_renderer_create_fence_old(r);
                ret = vtest_resource_busy_wait_old(r);
                break;
            case VCMD_GET_CAPS2:
                ret = vtest_send_caps2_old(r);
                break;
            case VCMD_DT_COMMAND:
                ret = vtest_dt_cmd_old(r);
                break;
            default:
                break;
        }

        if (ret < 0) {
            goto fail;
        }
        goto again;
    }
    if (ret <= 0) {
        goto fail;
    }
    fail:
    //fprintf(stderr, "socket failed - closing renderer\n");
    printf("socket failed - closing renderer\n");

    vtest_destroy_renderer_old(r);
    close(r->fd);
    free(r);

    return 0;
}

//TODO: JNI

#ifdef ANDROID_JNI
JNIEXPORT jint JNICALL Java_com_mittorn_virgloverlay_common_overlay_nativeOpenOld(JNIEnv *env, jclass cls)
{
    disp_old = eglGetDisplay(EGL_DEFAULT_DISPLAY);
    return vtest_open_socket_old(sock_path_old);
}

JNIEXPORT void JNICALL Java_com_mittorn_virgloverlay_common_overlay_nativeUnlinkOld(JNIEnv *env, jclass cls)
{
    unlink(sock_path_old);
}
JNIEXPORT jint JNICALL Java_com_mittorn_virgloverlay_common_overlay_nativeAcceptOld(JNIEnv *env, jclass cls, jint fd)
{
    return wait_for_socket_accept_old(fd);
}

JNIEXPORT void JNICALL Java_com_mittorn_virgloverlay_common_overlay_nativeSettingsOld(JNIEnv *env, jclass cls, jstring settings)
{
    char *utf = (*env)->GetStringUTFChars(env, settings, 0);
    int var1, var2, var3;
    if (utf) {
        FILE *f = fopen(utf, "r");
        if(!f)exit(1);
        fscanf(f, "%d %d %d %d", &var1, &var2, &var3, &dxtn_decompress); //
        (*env)->ReleaseStringUTFChars(env, settings, utf);
    }
}

JNIEXPORT jint JNICALL Java_com_mittorn_virgloverlay_common_overlay_nativeInitOld(JNIEnv *env, jclass cls, jstring settings)
{
    char *utf = (*env)->GetStringUTFChars(env, settings, 0);
    if (utf) {
        FILE *f = fopen(utf, "r");
        if(!f)exit(1);
        fscanf(f, "%d %[^ ]", &flags_old, sock_path_old);
        printf("'%d', '%s'", flags_old, sock_path_old);
        (*env)->ReleaseStringUTFChars(env, settings, utf);
    }
    return flags_old & FL_MULTITHREAD;
}

JNIEXPORT void JNICALL Java_com_mittorn_virgloverlay_common_overlay_nativeRunOld(JNIEnv *env, jclass cls, jint fd)
{
    static int ctx_id;
    ctx_id++;
    struct vtest_renderer_old *r = create_renderer_old(fd, ctx_id);
    r->jni.env = env;
    r->jni.cls = cls;
    r->jni.create = (*env)->GetStaticMethodID(env,cls, "create", "(IIII)Landroid/view/SurfaceView;");
    r->jni.get_surface = (*env)->GetStaticMethodID(env,cls, "get_surface", "(Landroid/view/SurfaceView;)Landroid/view/Surface;");
    r->jni.set_rect = (*env)->GetStaticMethodID(env,cls, "set_rect", "(Landroid/view/SurfaceView;IIIII)V");
    r->jni.destroy = (*env)->GetStaticMethodID(env,cls, "destroy", "(Landroid/view/SurfaceView;)V");
    r->flags = flags_old;

    renderer_loop_old(r);
}
#endif
