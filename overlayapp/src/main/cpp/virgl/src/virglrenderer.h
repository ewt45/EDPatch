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

/* library interface from QEMU to virglrenderer */

#ifndef VIRGLRENDERER_H
#define VIRGLRENDERER_H

#include <stdint.h>
#include <stdbool.h>
#include <stdarg.h>

struct virgl_box;
struct iovec;

#define VIRGL_EXPORT  __attribute__((visibility("default")))

typedef void *virgl_renderer_gl_context;

struct virgl_renderer_gl_ctx_param {
   int version;
   bool shared;
   int major_ver;
   int minor_ver;
};

#ifdef VIRGL_RENDERER_UNSTABLE_APIS
#define VIRGL_RENDERER_CALLBACKS_VERSION 4
#else
#define VIRGL_RENDERER_CALLBACKS_VERSION 2
#endif

struct virgl_renderer_callbacks {
   int version;
   void (*write_fence)(void *cookie, uint32_t fence);

   /*
    * The following 3 callbacks allows virglrenderer to
    * use winsys from caller, instead of initializing it's own
    * winsys (flag VIRGL_RENDERER_USE_EGL or VIRGL_RENDERER_USE_GLX).
    */

   /* create a GL/GLES context */
   virgl_renderer_gl_context (*create_gl_context)(void *cookie, int scanout_idx, struct virgl_renderer_gl_ctx_param *param);
   /* destroy a GL/GLES context */
   void (*destroy_gl_context)(void *cookie, virgl_renderer_gl_context ctx);
   /* make a context current */
   int (*make_current)(void *cookie, int scanout_idx, virgl_renderer_gl_context ctx);

   /*
    * v2, used with flags & VIRGL_RENDERER_USE_EGL
    * Chose the drm fd, that will be used by virglrenderer
    * for winsys initialization.  Virglrenderer takes ownership of the fd
    * that is returned and is responsible to close() it.  This should not
    * return the same fd each time it is call, if called multiple times.
    */
   int (*get_drm_fd)(void *cookie);

#ifdef VIRGL_RENDERER_UNSTABLE_APIS
   void (*write_context_fence)(void *cookie, uint32_t ctx_id, uint64_t queue_id, uint64_t fence_id);

   /* version 0: a connected socket of type SOCK_SEQPACKET */
   int (*get_server_fd)(void *cookie, uint32_t version);

   /*
    * Get the EGLDisplay from caller. It requires create_gl_context,
    * destroy_gl_context, make_current to be implemented by caller.
    */
   void *(*get_egl_display)(void *cookie);
#endif
};

/* virtio-gpu compatible interface */
#define VIRGL_RENDERER_USE_EGL 1
/*
 * Wait for sync objects in thread rather than polling
 * need to use virgl_renderer_get_poll_fd to know if this feature is in effect.
 */
#define VIRGL_RENDERER_THREAD_SYNC 2
#define VIRGL_RENDERER_USE_GLX (1 << 2)
#define VIRGL_RENDERER_USE_SURFACELESS (1 << 3)
#define VIRGL_RENDERER_USE_GLES (1 << 4)

#ifdef VIRGL_RENDERER_UNSTABLE_APIS
/*
 * Blob resources used with the 3D driver must be able to be represented as file descriptors.
 * The typical use case is the virtual machine manager (or vtest) is running in a multiprocess
 * mode. In a standard Linux setup, that means the KVM process is different from the process that
 * instantiated virglrenderer. For zero-copy capability to work, file descriptors must be used.
 *
 * VMMs that advertise support for the virtio-gpu feature VIRTIO_GPU_F_RESOURCE_BLOB and run in
 * a multi-process mode *must* specify this flag.
 */
#define VIRGL_RENDERER_USE_EXTERNAL_BLOB (1 << 5)

/* Enable venus renderer.
 */
#define VIRGL_RENDERER_VENUS         (1 << 6)

/* Disable virgl renderer.
 */
#define VIRGL_RENDERER_NO_VIRGL      (1 << 7)

/*
 * Used in conjonction with VIRGL_RENDERER_THREAD_SYNC;
 * write_fence callback is executed directly from the polling thread. When enabled,
 * virgl_renderer_get_poll_fd should not be used to watch for retired fences.
 */
#define VIRGL_RENDERER_ASYNC_FENCE_CB (1 << 8)

/* Start a render server and move GPU rendering to the render server.
 *
 * This is respected by the venus renderer but ignored by the virgl renderer.
 */
#define VIRGL_RENDERER_RENDER_SERVER (1 << 9)

/*
 * Enable drm renderer.
 */
#define VIRGL_RENDERER_DRM           (1 << 10)

/* Video encode/decode */
#define VIRGL_RENDERER_USE_VIDEO     (1 << 11)


#endif /* VIRGL_RENDERER_UNSTABLE_APIS */

VIRGL_EXPORT int virgl_renderer_init(void *cookie, int flags, struct virgl_renderer_callbacks *cb);
VIRGL_EXPORT void virgl_renderer_poll(void); /* force fences */

/* we need to give qemu the cursor resource contents */
VIRGL_EXPORT void *virgl_renderer_get_cursor_data(uint32_t resource_id, uint32_t *width, uint32_t *height);

VIRGL_EXPORT void virgl_renderer_get_rect(int resource_id, struct iovec *iov, unsigned int num_iovs,
                                          uint32_t offset, int x, int y, int width, int height);

VIRGL_EXPORT int virgl_renderer_get_fd_for_texture(uint32_t tex_id, int *fd);
VIRGL_EXPORT int virgl_renderer_get_fd_for_texture2(uint32_t tex_id, int *fd, int *stride, int *offset);

/*
 * These are only here for compatibility-reasons. In the future, use the flags
 * from virgl_hw.h instead.
 */
#define VIRGL_RES_BIND_DEPTH_STENCIL (1 << 0)
#define VIRGL_RES_BIND_RENDER_TARGET (1 << 1)
#define VIRGL_RES_BIND_SAMPLER_VIEW  (1 << 3)
#define VIRGL_RES_BIND_VERTEX_BUFFER (1 << 4)
#define VIRGL_RES_BIND_INDEX_BUFFER  (1 << 5)
#define VIRGL_RES_BIND_CONSTANT_BUFFER (1 << 6)
#define VIRGL_RES_BIND_STREAM_OUTPUT (1 << 11)
#define VIRGL_RES_BIND_CURSOR        (1 << 16)
#define VIRGL_RES_BIND_CUSTOM        (1 << 17)
#define VIRGL_RES_BIND_SCANOUT       (1 << 18)
#define VIRGL_RES_BIND_SHARED        (1 << 20)

enum virgl_renderer_structure_type_v0 {
   VIRGL_RENDERER_STRUCTURE_TYPE_NONE = 0,
   VIRGL_RENDERER_STRUCTURE_TYPE_EXPORT_QUERY = (1 << 0),
   VIRGL_RENDERER_STRUCTURE_TYPE_SUPPORTED_STRUCTURES = (1 << 1),
};

struct virgl_renderer_resource_create_args {
   uint32_t handle;
   uint32_t target;
   uint32_t format;
   uint32_t bind;
   uint32_t width;
   uint32_t height;
   uint32_t depth;
   uint32_t array_size;
   uint32_t last_level;
   uint32_t nr_samples;
   uint32_t flags;
};

struct virgl_renderer_hdr {
   uint32_t stype;
   uint32_t stype_version;
   uint32_t size;
};

/*
 * "out_num_fds" represents the number of distinct kernel buffers backing an
 * allocation. If this number or 'out_fourcc' is zero, the resource is not
 * exportable. The "out_fds" field will be populated with "out_num_fds" file
 * descriptors if "in_export_fds" is non-zero.
 */
struct virgl_renderer_export_query {
   struct virgl_renderer_hdr hdr;
   uint32_t in_resource_id;

   uint32_t out_num_fds;
   uint32_t in_export_fds;
   uint32_t out_fourcc;
   uint32_t pad;

   int32_t out_fds[4];
   uint32_t out_strides[4];
   uint32_t out_offsets[4];
   uint64_t out_modifier;
};

/*
 * "out_supported_structures_mask" is a bitmask representing the structures that
 * virglrenderer knows how to handle for a given "in_stype_version".
 */

struct virgl_renderer_supported_structures {
   struct virgl_renderer_hdr hdr;
   uint32_t in_stype_version;
   uint32_t out_supported_structures_mask;
};

/* new API */
/* This typedef must be kept in sync with vrend_debug.h */
typedef void (*virgl_debug_callback_type)(const char *fmt, va_list ap);

VIRGL_EXPORT int virgl_renderer_resource_create(struct virgl_renderer_resource_create_args *args, struct iovec *iov, uint32_t num_iovs);
VIRGL_EXPORT int virgl_renderer_resource_import_eglimage(struct virgl_renderer_resource_create_args *args, void *image);
VIRGL_EXPORT void virgl_renderer_resource_unref(uint32_t res_handle);

VIRGL_EXPORT void virgl_renderer_resource_set_priv(uint32_t res_handle, void *priv);
VIRGL_EXPORT void *virgl_renderer_resource_get_priv(uint32_t res_handle);

VIRGL_EXPORT int virgl_renderer_context_create(uint32_t handle, uint32_t nlen, const char *name);
VIRGL_EXPORT void virgl_renderer_context_destroy(uint32_t handle);

VIRGL_EXPORT int virgl_renderer_submit_cmd(void *buffer,
                                           int ctx_id,
                                           int ndw);

VIRGL_EXPORT int virgl_renderer_transfer_read_iov(uint32_t handle, uint32_t ctx_id,
                                                  uint32_t level, uint32_t stride,
                                                  uint32_t layer_stride,
                                                  struct virgl_box *box,
                                                  uint64_t offset, struct iovec *iov,
                                                  int iovec_cnt);

VIRGL_EXPORT int virgl_renderer_transfer_write_iov(uint32_t handle,
                                                   uint32_t ctx_id,
                                                   int level,
                                                   uint32_t stride,
                                                   uint32_t layer_stride,
                                                   struct virgl_box *box,
                                                   uint64_t offset,
                                                   struct iovec *iovec,
                                                   unsigned int iovec_cnt);

VIRGL_EXPORT void virgl_renderer_get_cap_set(uint32_t set, uint32_t *max_ver,
                                             uint32_t *max_size);

VIRGL_EXPORT void virgl_renderer_fill_caps(uint32_t set, uint32_t version,
                                           void *caps);

VIRGL_EXPORT int virgl_renderer_resource_attach_iov(int res_handle, struct iovec *iov,
                                                    int num_iovs);
VIRGL_EXPORT void virgl_renderer_resource_detach_iov(int res_handle, struct iovec **iov, int *num_iovs);

VIRGL_EXPORT int virgl_renderer_create_fence(int client_fence_id, uint32_t ctx_id);

VIRGL_EXPORT void virgl_renderer_force_ctx_0(void);

VIRGL_EXPORT void virgl_renderer_ctx_attach_resource(int ctx_id, int res_handle);
VIRGL_EXPORT void virgl_renderer_ctx_detach_resource(int ctx_id, int res_handle);

VIRGL_EXPORT virgl_debug_callback_type virgl_set_debug_callback(virgl_debug_callback_type cb);

/* return information about a resource */

struct virgl_renderer_resource_info {
   uint32_t handle;
   uint32_t virgl_format;
   uint32_t width;
   uint32_t height;
   uint32_t depth;
   uint32_t flags;
   uint32_t tex_id;
   uint32_t stride;
   int drm_fourcc;
};

VIRGL_EXPORT int virgl_renderer_resource_get_info(int res_handle,
                                                  struct virgl_renderer_resource_info *info);

VIRGL_EXPORT void virgl_renderer_cleanup(void *cookie);

/* reset the rendererer - destroy all contexts and resource */
VIRGL_EXPORT void virgl_renderer_reset(void);

VIRGL_EXPORT int virgl_renderer_get_poll_fd(void);

VIRGL_EXPORT int virgl_renderer_execute(void *execute_args, uint32_t execute_size);

#define VIRGL_RENDERER_CONTEXT_FLAG_CAPSET_ID_MASK 0xff

VIRGL_EXPORT int virgl_renderer_context_create_with_flags(uint32_t ctx_id,
                                                          uint32_t ctx_flags,
                                                          uint32_t nlen,
                                                          const char *name);

#define VIRGL_RENDERER_BLOB_MEM_GUEST             0x0001
#define VIRGL_RENDERER_BLOB_MEM_HOST3D            0x0002
#define VIRGL_RENDERER_BLOB_MEM_HOST3D_GUEST      0x0003
#define VIRGL_RENDERER_BLOB_MEM_GUEST_VRAM        0x0004

#define VIRGL_RENDERER_BLOB_FLAG_USE_MAPPABLE     0x0001
#define VIRGL_RENDERER_BLOB_FLAG_USE_SHAREABLE    0x0002
#define VIRGL_RENDERER_BLOB_FLAG_USE_CROSS_DEVICE 0x0004

struct virgl_renderer_resource_create_blob_args
{
   uint32_t res_handle;
   uint32_t ctx_id;
   uint32_t blob_mem;
   uint32_t blob_flags;
   uint64_t blob_id;
   uint64_t size;
   const struct iovec *iovecs;
   uint32_t num_iovs;
};

VIRGL_EXPORT int
virgl_renderer_resource_create_blob(const struct virgl_renderer_resource_create_blob_args *args);

VIRGL_EXPORT int virgl_renderer_resource_map(uint32_t res_handle, void **map, uint64_t *out_size);

VIRGL_EXPORT int virgl_renderer_resource_unmap(uint32_t res_handle);

#define VIRGL_RENDERER_MAP_CACHE_MASK      0x0f
#define VIRGL_RENDERER_MAP_CACHE_NONE      0x00
#define VIRGL_RENDERER_MAP_CACHE_CACHED    0x01
#define VIRGL_RENDERER_MAP_CACHE_UNCACHED  0x02
#define VIRGL_RENDERER_MAP_CACHE_WC        0x03

VIRGL_EXPORT int virgl_renderer_resource_get_map_info(uint32_t res_handle, uint32_t *map_info);

#define VIRGL_RENDERER_BLOB_FD_TYPE_DMABUF        0x0001
#define VIRGL_RENDERER_BLOB_FD_TYPE_OPAQUE        0x0002
#define VIRGL_RENDERER_BLOB_FD_TYPE_SHM           0x0003

VIRGL_EXPORT int
virgl_renderer_resource_export_blob(uint32_t res_id, uint32_t *fd_type, int *fd);

/*
 * These are unstable APIs for development only. Use these for development/testing purposes
 * only, not in production
 */
#ifdef VIRGL_RENDERER_UNSTABLE_APIS

struct virgl_renderer_resource_import_blob_args
{
   uint32_t res_handle;
   uint32_t blob_mem;
   uint32_t fd_type;
   int fd;
   uint64_t size;
};

VIRGL_EXPORT int
virgl_renderer_resource_import_blob(const struct virgl_renderer_resource_import_blob_args *args);

VIRGL_EXPORT int
virgl_renderer_export_fence(uint32_t client_fence_id, int *fd);

#define VIRGL_RENDERER_FENCE_FLAG_MERGEABLE      (1 << 0)
VIRGL_EXPORT int virgl_renderer_context_create_fence(uint32_t ctx_id,
                                                     uint32_t flags,
                                                     uint64_t queue_id,
                                                     uint64_t fence_id);
VIRGL_EXPORT void virgl_renderer_context_poll(uint32_t ctx_id); /* force fences */
VIRGL_EXPORT int virgl_renderer_context_get_poll_fd(uint32_t ctx_id);

#endif /* VIRGL_RENDERER_UNSTABLE_APIS */

#endif
