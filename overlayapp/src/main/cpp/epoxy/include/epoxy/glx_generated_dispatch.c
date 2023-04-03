/* GL dispatch code.
 * This is code-generated from the GL API XML files from Khronos.
 * 
 * Copyright (c) 2013-2018 The Khronos Group Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */

#include "config.h"

#include <stdlib.h>
#include <string.h>
#include <stdio.h>

#include "dispatch_common.h"
#include "epoxy/glx.h"

#ifdef __GNUC__
#define EPOXY_NOINLINE __attribute__((noinline))
#elif defined (_MSC_VER)
#define EPOXY_NOINLINE __declspec(noinline)
#endif
struct dispatch_table {
    PFNGLXBINDCHANNELTOWINDOWSGIXPROC epoxy_glXBindChannelToWindowSGIX;
    PFNGLXBINDHYPERPIPESGIXPROC epoxy_glXBindHyperpipeSGIX;
    PFNGLXBINDSWAPBARRIERNVPROC epoxy_glXBindSwapBarrierNV;
    PFNGLXBINDSWAPBARRIERSGIXPROC epoxy_glXBindSwapBarrierSGIX;
    PFNGLXBINDTEXIMAGEEXTPROC epoxy_glXBindTexImageEXT;
    PFNGLXBINDVIDEOCAPTUREDEVICENVPROC epoxy_glXBindVideoCaptureDeviceNV;
    PFNGLXBINDVIDEODEVICENVPROC epoxy_glXBindVideoDeviceNV;
    PFNGLXBINDVIDEOIMAGENVPROC epoxy_glXBindVideoImageNV;
    PFNGLXBLITCONTEXTFRAMEBUFFERAMDPROC epoxy_glXBlitContextFramebufferAMD;
    PFNGLXCHANNELRECTSGIXPROC epoxy_glXChannelRectSGIX;
    PFNGLXCHANNELRECTSYNCSGIXPROC epoxy_glXChannelRectSyncSGIX;
    PFNGLXCHOOSEFBCONFIGPROC epoxy_glXChooseFBConfig;
    PFNGLXCHOOSEFBCONFIGSGIXPROC epoxy_glXChooseFBConfigSGIX;
    PFNGLXCHOOSEVISUALPROC epoxy_glXChooseVisual;
    PFNGLXCOPYBUFFERSUBDATANVPROC epoxy_glXCopyBufferSubDataNV;
    PFNGLXCOPYCONTEXTPROC epoxy_glXCopyContext;
    PFNGLXCOPYIMAGESUBDATANVPROC epoxy_glXCopyImageSubDataNV;
    PFNGLXCOPYSUBBUFFERMESAPROC epoxy_glXCopySubBufferMESA;
    PFNGLXCREATEASSOCIATEDCONTEXTAMDPROC epoxy_glXCreateAssociatedContextAMD;
    PFNGLXCREATEASSOCIATEDCONTEXTATTRIBSAMDPROC epoxy_glXCreateAssociatedContextAttribsAMD;
    PFNGLXCREATECONTEXTPROC epoxy_glXCreateContext;
    PFNGLXCREATECONTEXTATTRIBSARBPROC epoxy_glXCreateContextAttribsARB;
    PFNGLXCREATECONTEXTWITHCONFIGSGIXPROC epoxy_glXCreateContextWithConfigSGIX;
    PFNGLXCREATEGLXPBUFFERSGIXPROC epoxy_glXCreateGLXPbufferSGIX;
    PFNGLXCREATEGLXPIXMAPPROC epoxy_glXCreateGLXPixmap;
    PFNGLXCREATEGLXPIXMAPMESAPROC epoxy_glXCreateGLXPixmapMESA;
    PFNGLXCREATEGLXPIXMAPWITHCONFIGSGIXPROC epoxy_glXCreateGLXPixmapWithConfigSGIX;
    PFNGLXCREATENEWCONTEXTPROC epoxy_glXCreateNewContext;
    PFNGLXCREATEPBUFFERPROC epoxy_glXCreatePbuffer;
    PFNGLXCREATEPIXMAPPROC epoxy_glXCreatePixmap;
    PFNGLXCREATEWINDOWPROC epoxy_glXCreateWindow;
    PFNGLXCUSHIONSGIPROC epoxy_glXCushionSGI;
    PFNGLXDELAYBEFORESWAPNVPROC epoxy_glXDelayBeforeSwapNV;
    PFNGLXDELETEASSOCIATEDCONTEXTAMDPROC epoxy_glXDeleteAssociatedContextAMD;
    PFNGLXDESTROYCONTEXTPROC epoxy_glXDestroyContext;
    PFNGLXDESTROYGLXPBUFFERSGIXPROC epoxy_glXDestroyGLXPbufferSGIX;
    PFNGLXDESTROYGLXPIXMAPPROC epoxy_glXDestroyGLXPixmap;
    PFNGLXDESTROYGLXVIDEOSOURCESGIXPROC epoxy_glXDestroyGLXVideoSourceSGIX;
    PFNGLXDESTROYHYPERPIPECONFIGSGIXPROC epoxy_glXDestroyHyperpipeConfigSGIX;
    PFNGLXDESTROYPBUFFERPROC epoxy_glXDestroyPbuffer;
    PFNGLXDESTROYPIXMAPPROC epoxy_glXDestroyPixmap;
    PFNGLXDESTROYWINDOWPROC epoxy_glXDestroyWindow;
    PFNGLXENUMERATEVIDEOCAPTUREDEVICESNVPROC epoxy_glXEnumerateVideoCaptureDevicesNV;
    PFNGLXENUMERATEVIDEODEVICESNVPROC epoxy_glXEnumerateVideoDevicesNV;
    PFNGLXFREECONTEXTEXTPROC epoxy_glXFreeContextEXT;
    PFNGLXGETAGPOFFSETMESAPROC epoxy_glXGetAGPOffsetMESA;
    PFNGLXGETCLIENTSTRINGPROC epoxy_glXGetClientString;
    PFNGLXGETCONFIGPROC epoxy_glXGetConfig;
    PFNGLXGETCONTEXTGPUIDAMDPROC epoxy_glXGetContextGPUIDAMD;
    PFNGLXGETCONTEXTIDEXTPROC epoxy_glXGetContextIDEXT;
    PFNGLXGETCURRENTASSOCIATEDCONTEXTAMDPROC epoxy_glXGetCurrentAssociatedContextAMD;
    PFNGLXGETCURRENTCONTEXTPROC epoxy_glXGetCurrentContext;
    PFNGLXGETCURRENTDISPLAYPROC epoxy_glXGetCurrentDisplay;
    PFNGLXGETCURRENTDISPLAYEXTPROC epoxy_glXGetCurrentDisplayEXT;
    PFNGLXGETCURRENTDRAWABLEPROC epoxy_glXGetCurrentDrawable;
    PFNGLXGETCURRENTREADDRAWABLEPROC epoxy_glXGetCurrentReadDrawable;
    PFNGLXGETCURRENTREADDRAWABLESGIPROC epoxy_glXGetCurrentReadDrawableSGI;
    PFNGLXGETFBCONFIGATTRIBPROC epoxy_glXGetFBConfigAttrib;
    PFNGLXGETFBCONFIGATTRIBSGIXPROC epoxy_glXGetFBConfigAttribSGIX;
    PFNGLXGETFBCONFIGFROMVISUALSGIXPROC epoxy_glXGetFBConfigFromVisualSGIX;
    PFNGLXGETFBCONFIGSPROC epoxy_glXGetFBConfigs;
    PFNGLXGETGPUIDSAMDPROC epoxy_glXGetGPUIDsAMD;
    PFNGLXGETGPUINFOAMDPROC epoxy_glXGetGPUInfoAMD;
    PFNGLXGETMSCRATEOMLPROC epoxy_glXGetMscRateOML;
    PFNGLXGETPROCADDRESSPROC epoxy_glXGetProcAddress;
    PFNGLXGETPROCADDRESSARBPROC epoxy_glXGetProcAddressARB;
    PFNGLXGETSELECTEDEVENTPROC epoxy_glXGetSelectedEvent;
    PFNGLXGETSELECTEDEVENTSGIXPROC epoxy_glXGetSelectedEventSGIX;
    PFNGLXGETSWAPINTERVALMESAPROC epoxy_glXGetSwapIntervalMESA;
    PFNGLXGETSYNCVALUESOMLPROC epoxy_glXGetSyncValuesOML;
    PFNGLXGETTRANSPARENTINDEXSUNPROC epoxy_glXGetTransparentIndexSUN;
    PFNGLXGETVIDEODEVICENVPROC epoxy_glXGetVideoDeviceNV;
    PFNGLXGETVIDEOINFONVPROC epoxy_glXGetVideoInfoNV;
    PFNGLXGETVIDEOSYNCSGIPROC epoxy_glXGetVideoSyncSGI;
    PFNGLXGETVISUALFROMFBCONFIGPROC epoxy_glXGetVisualFromFBConfig;
    PFNGLXGETVISUALFROMFBCONFIGSGIXPROC epoxy_glXGetVisualFromFBConfigSGIX;
    PFNGLXHYPERPIPEATTRIBSGIXPROC epoxy_glXHyperpipeAttribSGIX;
    PFNGLXHYPERPIPECONFIGSGIXPROC epoxy_glXHyperpipeConfigSGIX;
    PFNGLXIMPORTCONTEXTEXTPROC epoxy_glXImportContextEXT;
    PFNGLXISDIRECTPROC epoxy_glXIsDirect;
    PFNGLXJOINSWAPGROUPNVPROC epoxy_glXJoinSwapGroupNV;
    PFNGLXJOINSWAPGROUPSGIXPROC epoxy_glXJoinSwapGroupSGIX;
    PFNGLXLOCKVIDEOCAPTUREDEVICENVPROC epoxy_glXLockVideoCaptureDeviceNV;
    PFNGLXMAKEASSOCIATEDCONTEXTCURRENTAMDPROC epoxy_glXMakeAssociatedContextCurrentAMD;
    PFNGLXMAKECONTEXTCURRENTPROC epoxy_glXMakeContextCurrent;
    PFNGLXMAKECURRENTPROC epoxy_glXMakeCurrent;
    PFNGLXMAKECURRENTREADSGIPROC epoxy_glXMakeCurrentReadSGI;
    PFNGLXNAMEDCOPYBUFFERSUBDATANVPROC epoxy_glXNamedCopyBufferSubDataNV;
    PFNGLXQUERYCHANNELDELTASSGIXPROC epoxy_glXQueryChannelDeltasSGIX;
    PFNGLXQUERYCHANNELRECTSGIXPROC epoxy_glXQueryChannelRectSGIX;
    PFNGLXQUERYCONTEXTPROC epoxy_glXQueryContext;
    PFNGLXQUERYCONTEXTINFOEXTPROC epoxy_glXQueryContextInfoEXT;
    PFNGLXQUERYCURRENTRENDERERINTEGERMESAPROC epoxy_glXQueryCurrentRendererIntegerMESA;
    PFNGLXQUERYCURRENTRENDERERSTRINGMESAPROC epoxy_glXQueryCurrentRendererStringMESA;
    PFNGLXQUERYDRAWABLEPROC epoxy_glXQueryDrawable;
    PFNGLXQUERYEXTENSIONPROC epoxy_glXQueryExtension;
    PFNGLXQUERYEXTENSIONSSTRINGPROC epoxy_glXQueryExtensionsString;
    PFNGLXQUERYFRAMECOUNTNVPROC epoxy_glXQueryFrameCountNV;
    PFNGLXQUERYGLXPBUFFERSGIXPROC epoxy_glXQueryGLXPbufferSGIX;
    PFNGLXQUERYHYPERPIPEATTRIBSGIXPROC epoxy_glXQueryHyperpipeAttribSGIX;
    PFNGLXQUERYHYPERPIPEBESTATTRIBSGIXPROC epoxy_glXQueryHyperpipeBestAttribSGIX;
    PFNGLXQUERYHYPERPIPECONFIGSGIXPROC epoxy_glXQueryHyperpipeConfigSGIX;
    PFNGLXQUERYHYPERPIPENETWORKSGIXPROC epoxy_glXQueryHyperpipeNetworkSGIX;
    PFNGLXQUERYMAXSWAPBARRIERSSGIXPROC epoxy_glXQueryMaxSwapBarriersSGIX;
    PFNGLXQUERYMAXSWAPGROUPSNVPROC epoxy_glXQueryMaxSwapGroupsNV;
    PFNGLXQUERYRENDERERINTEGERMESAPROC epoxy_glXQueryRendererIntegerMESA;
    PFNGLXQUERYRENDERERSTRINGMESAPROC epoxy_glXQueryRendererStringMESA;
    PFNGLXQUERYSERVERSTRINGPROC epoxy_glXQueryServerString;
    PFNGLXQUERYSWAPGROUPNVPROC epoxy_glXQuerySwapGroupNV;
    PFNGLXQUERYVERSIONPROC epoxy_glXQueryVersion;
    PFNGLXQUERYVIDEOCAPTUREDEVICENVPROC epoxy_glXQueryVideoCaptureDeviceNV;
    PFNGLXRELEASEBUFFERSMESAPROC epoxy_glXReleaseBuffersMESA;
    PFNGLXRELEASETEXIMAGEEXTPROC epoxy_glXReleaseTexImageEXT;
    PFNGLXRELEASEVIDEOCAPTUREDEVICENVPROC epoxy_glXReleaseVideoCaptureDeviceNV;
    PFNGLXRELEASEVIDEODEVICENVPROC epoxy_glXReleaseVideoDeviceNV;
    PFNGLXRELEASEVIDEOIMAGENVPROC epoxy_glXReleaseVideoImageNV;
    PFNGLXRESETFRAMECOUNTNVPROC epoxy_glXResetFrameCountNV;
    PFNGLXSELECTEVENTPROC epoxy_glXSelectEvent;
    PFNGLXSELECTEVENTSGIXPROC epoxy_glXSelectEventSGIX;
    PFNGLXSENDPBUFFERTOVIDEONVPROC epoxy_glXSendPbufferToVideoNV;
    PFNGLXSET3DFXMODEMESAPROC epoxy_glXSet3DfxModeMESA;
    PFNGLXSWAPBUFFERSPROC epoxy_glXSwapBuffers;
    PFNGLXSWAPBUFFERSMSCOMLPROC epoxy_glXSwapBuffersMscOML;
    PFNGLXSWAPINTERVALEXTPROC epoxy_glXSwapIntervalEXT;
    PFNGLXSWAPINTERVALMESAPROC epoxy_glXSwapIntervalMESA;
    PFNGLXSWAPINTERVALSGIPROC epoxy_glXSwapIntervalSGI;
    PFNGLXUSEXFONTPROC epoxy_glXUseXFont;
    PFNGLXWAITFORMSCOMLPROC epoxy_glXWaitForMscOML;
    PFNGLXWAITFORSBCOMLPROC epoxy_glXWaitForSbcOML;
    PFNGLXWAITGLPROC epoxy_glXWaitGL;
    PFNGLXWAITVIDEOSYNCSGIPROC epoxy_glXWaitVideoSyncSGI;
    PFNGLXWAITXPROC epoxy_glXWaitX;
};

#if USING_DISPATCH_TABLE
static inline struct dispatch_table *
get_dispatch_table(void);

#endif

enum glx_provider {
    glx_provider_terminator = 0,
    PROVIDER_GLX_10,
    PROVIDER_GLX_11,
    PROVIDER_GLX_12,
    PROVIDER_GLX_13,
    PROVIDER_GLX_AMD_gpu_association,
    PROVIDER_GLX_ARB_create_context,
    PROVIDER_GLX_ARB_get_proc_address,
    PROVIDER_GLX_EXT_import_context,
    PROVIDER_GLX_EXT_swap_control,
    PROVIDER_GLX_EXT_texture_from_pixmap,
    PROVIDER_GLX_MESA_agp_offset,
    PROVIDER_GLX_MESA_copy_sub_buffer,
    PROVIDER_GLX_MESA_pixmap_colormap,
    PROVIDER_GLX_MESA_query_renderer,
    PROVIDER_GLX_MESA_release_buffers,
    PROVIDER_GLX_MESA_set_3dfx_mode,
    PROVIDER_GLX_MESA_swap_control,
    PROVIDER_GLX_NV_copy_buffer,
    PROVIDER_GLX_NV_copy_image,
    PROVIDER_GLX_NV_delay_before_swap,
    PROVIDER_GLX_NV_present_video,
    PROVIDER_GLX_NV_swap_group,
    PROVIDER_GLX_NV_video_capture,
    PROVIDER_GLX_NV_video_out,
    PROVIDER_GLX_OML_sync_control,
    PROVIDER_GLX_SGIX_fbconfig,
    PROVIDER_GLX_SGIX_hyperpipe,
    PROVIDER_GLX_SGIX_pbuffer,
    PROVIDER_GLX_SGIX_swap_barrier,
    PROVIDER_GLX_SGIX_swap_group,
    PROVIDER_GLX_SGIX_video_resize,
    PROVIDER_GLX_SGIX_video_source,
    PROVIDER_GLX_SGI_cushion,
    PROVIDER_GLX_SGI_make_current_read,
    PROVIDER_GLX_SGI_swap_control,
    PROVIDER_GLX_SGI_video_sync,
    PROVIDER_GLX_SUN_get_transparent_index,
    PROVIDER_always_present,
} PACKED;
ENDPACKED

static const char *enum_string =
    "GLX 10\0"
    "GLX 11\0"
    "GLX 12\0"
    "GLX 13\0"
    "GLX_AMD_gpu_association\0"
    "GLX_ARB_create_context\0"
    "GLX_ARB_get_proc_address\0"
    "GLX_EXT_import_context\0"
    "GLX_EXT_swap_control\0"
    "GLX_EXT_texture_from_pixmap\0"
    "GLX_MESA_agp_offset\0"
    "GLX_MESA_copy_sub_buffer\0"
    "GLX_MESA_pixmap_colormap\0"
    "GLX_MESA_query_renderer\0"
    "GLX_MESA_release_buffers\0"
    "GLX_MESA_set_3dfx_mode\0"
    "GLX_MESA_swap_control\0"
    "GLX_NV_copy_buffer\0"
    "GLX_NV_copy_image\0"
    "GLX_NV_delay_before_swap\0"
    "GLX_NV_present_video\0"
    "GLX_NV_swap_group\0"
    "GLX_NV_video_capture\0"
    "GLX_NV_video_out\0"
    "GLX_OML_sync_control\0"
    "GLX_SGIX_fbconfig\0"
    "GLX_SGIX_hyperpipe\0"
    "GLX_SGIX_pbuffer\0"
    "GLX_SGIX_swap_barrier\0"
    "GLX_SGIX_swap_group\0"
    "GLX_SGIX_video_resize\0"
    "GLX_SGIX_video_source\0"
    "GLX_SGI_cushion\0"
    "GLX_SGI_make_current_read\0"
    "GLX_SGI_swap_control\0"
    "GLX_SGI_video_sync\0"
    "GLX_SUN_get_transparent_index\0"
    "always present\0"
     ;

static const uint16_t enum_string_offsets[] = {
    -1, /* glx_provider_terminator, unused */
    0, /* GLX 10 */
    7, /* GLX 11 */
    14, /* GLX 12 */
    21, /* GLX 13 */
    28, /* GLX_AMD_gpu_association */
    52, /* GLX_ARB_create_context */
    75, /* GLX_ARB_get_proc_address */
    100, /* GLX_EXT_import_context */
    123, /* GLX_EXT_swap_control */
    144, /* GLX_EXT_texture_from_pixmap */
    172, /* GLX_MESA_agp_offset */
    192, /* GLX_MESA_copy_sub_buffer */
    217, /* GLX_MESA_pixmap_colormap */
    242, /* GLX_MESA_query_renderer */
    266, /* GLX_MESA_release_buffers */
    291, /* GLX_MESA_set_3dfx_mode */
    314, /* GLX_MESA_swap_control */
    336, /* GLX_NV_copy_buffer */
    355, /* GLX_NV_copy_image */
    373, /* GLX_NV_delay_before_swap */
    398, /* GLX_NV_present_video */
    419, /* GLX_NV_swap_group */
    437, /* GLX_NV_video_capture */
    458, /* GLX_NV_video_out */
    475, /* GLX_OML_sync_control */
    496, /* GLX_SGIX_fbconfig */
    514, /* GLX_SGIX_hyperpipe */
    533, /* GLX_SGIX_pbuffer */
    550, /* GLX_SGIX_swap_barrier */
    572, /* GLX_SGIX_swap_group */
    592, /* GLX_SGIX_video_resize */
    614, /* GLX_SGIX_video_source */
    636, /* GLX_SGI_cushion */
    652, /* GLX_SGI_make_current_read */
    678, /* GLX_SGI_swap_control */
    699, /* GLX_SGI_video_sync */
    718, /* GLX_SUN_get_transparent_index */
    748, /* always present */
};

static const char entrypoint_strings[] = {
   'g',
   'l',
   'X',
   'B',
   'i',
   'n',
   'd',
   'C',
   'h',
   'a',
   'n',
   'n',
   'e',
   'l',
   'T',
   'o',
   'W',
   'i',
   'n',
   'd',
   'o',
   'w',
   'S',
   'G',
   'I',
   'X',
   0, // glXBindChannelToWindowSGIX
   'g',
   'l',
   'X',
   'B',
   'i',
   'n',
   'd',
   'H',
   'y',
   'p',
   'e',
   'r',
   'p',
   'i',
   'p',
   'e',
   'S',
   'G',
   'I',
   'X',
   0, // glXBindHyperpipeSGIX
   'g',
   'l',
   'X',
   'B',
   'i',
   'n',
   'd',
   'S',
   'w',
   'a',
   'p',
   'B',
   'a',
   'r',
   'r',
   'i',
   'e',
   'r',
   'N',
   'V',
   0, // glXBindSwapBarrierNV
   'g',
   'l',
   'X',
   'B',
   'i',
   'n',
   'd',
   'S',
   'w',
   'a',
   'p',
   'B',
   'a',
   'r',
   'r',
   'i',
   'e',
   'r',
   'S',
   'G',
   'I',
   'X',
   0, // glXBindSwapBarrierSGIX
   'g',
   'l',
   'X',
   'B',
   'i',
   'n',
   'd',
   'T',
   'e',
   'x',
   'I',
   'm',
   'a',
   'g',
   'e',
   'E',
   'X',
   'T',
   0, // glXBindTexImageEXT
   'g',
   'l',
   'X',
   'B',
   'i',
   'n',
   'd',
   'V',
   'i',
   'd',
   'e',
   'o',
   'C',
   'a',
   'p',
   't',
   'u',
   'r',
   'e',
   'D',
   'e',
   'v',
   'i',
   'c',
   'e',
   'N',
   'V',
   0, // glXBindVideoCaptureDeviceNV
   'g',
   'l',
   'X',
   'B',
   'i',
   'n',
   'd',
   'V',
   'i',
   'd',
   'e',
   'o',
   'D',
   'e',
   'v',
   'i',
   'c',
   'e',
   'N',
   'V',
   0, // glXBindVideoDeviceNV
   'g',
   'l',
   'X',
   'B',
   'i',
   'n',
   'd',
   'V',
   'i',
   'd',
   'e',
   'o',
   'I',
   'm',
   'a',
   'g',
   'e',
   'N',
   'V',
   0, // glXBindVideoImageNV
   'g',
   'l',
   'X',
   'B',
   'l',
   'i',
   't',
   'C',
   'o',
   'n',
   't',
   'e',
   'x',
   't',
   'F',
   'r',
   'a',
   'm',
   'e',
   'b',
   'u',
   'f',
   'f',
   'e',
   'r',
   'A',
   'M',
   'D',
   0, // glXBlitContextFramebufferAMD
   'g',
   'l',
   'X',
   'C',
   'h',
   'a',
   'n',
   'n',
   'e',
   'l',
   'R',
   'e',
   'c',
   't',
   'S',
   'G',
   'I',
   'X',
   0, // glXChannelRectSGIX
   'g',
   'l',
   'X',
   'C',
   'h',
   'a',
   'n',
   'n',
   'e',
   'l',
   'R',
   'e',
   'c',
   't',
   'S',
   'y',
   'n',
   'c',
   'S',
   'G',
   'I',
   'X',
   0, // glXChannelRectSyncSGIX
   'g',
   'l',
   'X',
   'C',
   'h',
   'o',
   'o',
   's',
   'e',
   'F',
   'B',
   'C',
   'o',
   'n',
   'f',
   'i',
   'g',
   0, // glXChooseFBConfig
   'g',
   'l',
   'X',
   'C',
   'h',
   'o',
   'o',
   's',
   'e',
   'F',
   'B',
   'C',
   'o',
   'n',
   'f',
   'i',
   'g',
   'S',
   'G',
   'I',
   'X',
   0, // glXChooseFBConfigSGIX
   'g',
   'l',
   'X',
   'C',
   'h',
   'o',
   'o',
   's',
   'e',
   'V',
   'i',
   's',
   'u',
   'a',
   'l',
   0, // glXChooseVisual
   'g',
   'l',
   'X',
   'C',
   'o',
   'p',
   'y',
   'B',
   'u',
   'f',
   'f',
   'e',
   'r',
   'S',
   'u',
   'b',
   'D',
   'a',
   't',
   'a',
   'N',
   'V',
   0, // glXCopyBufferSubDataNV
   'g',
   'l',
   'X',
   'C',
   'o',
   'p',
   'y',
   'C',
   'o',
   'n',
   't',
   'e',
   'x',
   't',
   0, // glXCopyContext
   'g',
   'l',
   'X',
   'C',
   'o',
   'p',
   'y',
   'I',
   'm',
   'a',
   'g',
   'e',
   'S',
   'u',
   'b',
   'D',
   'a',
   't',
   'a',
   'N',
   'V',
   0, // glXCopyImageSubDataNV
   'g',
   'l',
   'X',
   'C',
   'o',
   'p',
   'y',
   'S',
   'u',
   'b',
   'B',
   'u',
   'f',
   'f',
   'e',
   'r',
   'M',
   'E',
   'S',
   'A',
   0, // glXCopySubBufferMESA
   'g',
   'l',
   'X',
   'C',
   'r',
   'e',
   'a',
   't',
   'e',
   'A',
   's',
   's',
   'o',
   'c',
   'i',
   'a',
   't',
   'e',
   'd',
   'C',
   'o',
   'n',
   't',
   'e',
   'x',
   't',
   'A',
   'M',
   'D',
   0, // glXCreateAssociatedContextAMD
   'g',
   'l',
   'X',
   'C',
   'r',
   'e',
   'a',
   't',
   'e',
   'A',
   's',
   's',
   'o',
   'c',
   'i',
   'a',
   't',
   'e',
   'd',
   'C',
   'o',
   'n',
   't',
   'e',
   'x',
   't',
   'A',
   't',
   't',
   'r',
   'i',
   'b',
   's',
   'A',
   'M',
   'D',
   0, // glXCreateAssociatedContextAttribsAMD
   'g',
   'l',
   'X',
   'C',
   'r',
   'e',
   'a',
   't',
   'e',
   'C',
   'o',
   'n',
   't',
   'e',
   'x',
   't',
   0, // glXCreateContext
   'g',
   'l',
   'X',
   'C',
   'r',
   'e',
   'a',
   't',
   'e',
   'C',
   'o',
   'n',
   't',
   'e',
   'x',
   't',
   'A',
   't',
   't',
   'r',
   'i',
   'b',
   's',
   'A',
   'R',
   'B',
   0, // glXCreateContextAttribsARB
   'g',
   'l',
   'X',
   'C',
   'r',
   'e',
   'a',
   't',
   'e',
   'C',
   'o',
   'n',
   't',
   'e',
   'x',
   't',
   'W',
   'i',
   't',
   'h',
   'C',
   'o',
   'n',
   'f',
   'i',
   'g',
   'S',
   'G',
   'I',
   'X',
   0, // glXCreateContextWithConfigSGIX
   'g',
   'l',
   'X',
   'C',
   'r',
   'e',
   'a',
   't',
   'e',
   'G',
   'L',
   'X',
   'P',
   'b',
   'u',
   'f',
   'f',
   'e',
   'r',
   'S',
   'G',
   'I',
   'X',
   0, // glXCreateGLXPbufferSGIX
   'g',
   'l',
   'X',
   'C',
   'r',
   'e',
   'a',
   't',
   'e',
   'G',
   'L',
   'X',
   'P',
   'i',
   'x',
   'm',
   'a',
   'p',
   0, // glXCreateGLXPixmap
   'g',
   'l',
   'X',
   'C',
   'r',
   'e',
   'a',
   't',
   'e',
   'G',
   'L',
   'X',
   'P',
   'i',
   'x',
   'm',
   'a',
   'p',
   'M',
   'E',
   'S',
   'A',
   0, // glXCreateGLXPixmapMESA
   'g',
   'l',
   'X',
   'C',
   'r',
   'e',
   'a',
   't',
   'e',
   'G',
   'L',
   'X',
   'P',
   'i',
   'x',
   'm',
   'a',
   'p',
   'W',
   'i',
   't',
   'h',
   'C',
   'o',
   'n',
   'f',
   'i',
   'g',
   'S',
   'G',
   'I',
   'X',
   0, // glXCreateGLXPixmapWithConfigSGIX
   'g',
   'l',
   'X',
   'C',
   'r',
   'e',
   'a',
   't',
   'e',
   'N',
   'e',
   'w',
   'C',
   'o',
   'n',
   't',
   'e',
   'x',
   't',
   0, // glXCreateNewContext
   'g',
   'l',
   'X',
   'C',
   'r',
   'e',
   'a',
   't',
   'e',
   'P',
   'b',
   'u',
   'f',
   'f',
   'e',
   'r',
   0, // glXCreatePbuffer
   'g',
   'l',
   'X',
   'C',
   'r',
   'e',
   'a',
   't',
   'e',
   'P',
   'i',
   'x',
   'm',
   'a',
   'p',
   0, // glXCreatePixmap
   'g',
   'l',
   'X',
   'C',
   'r',
   'e',
   'a',
   't',
   'e',
   'W',
   'i',
   'n',
   'd',
   'o',
   'w',
   0, // glXCreateWindow
   'g',
   'l',
   'X',
   'C',
   'u',
   's',
   'h',
   'i',
   'o',
   'n',
   'S',
   'G',
   'I',
   0, // glXCushionSGI
   'g',
   'l',
   'X',
   'D',
   'e',
   'l',
   'a',
   'y',
   'B',
   'e',
   'f',
   'o',
   'r',
   'e',
   'S',
   'w',
   'a',
   'p',
   'N',
   'V',
   0, // glXDelayBeforeSwapNV
   'g',
   'l',
   'X',
   'D',
   'e',
   'l',
   'e',
   't',
   'e',
   'A',
   's',
   's',
   'o',
   'c',
   'i',
   'a',
   't',
   'e',
   'd',
   'C',
   'o',
   'n',
   't',
   'e',
   'x',
   't',
   'A',
   'M',
   'D',
   0, // glXDeleteAssociatedContextAMD
   'g',
   'l',
   'X',
   'D',
   'e',
   's',
   't',
   'r',
   'o',
   'y',
   'C',
   'o',
   'n',
   't',
   'e',
   'x',
   't',
   0, // glXDestroyContext
   'g',
   'l',
   'X',
   'D',
   'e',
   's',
   't',
   'r',
   'o',
   'y',
   'G',
   'L',
   'X',
   'P',
   'b',
   'u',
   'f',
   'f',
   'e',
   'r',
   'S',
   'G',
   'I',
   'X',
   0, // glXDestroyGLXPbufferSGIX
   'g',
   'l',
   'X',
   'D',
   'e',
   's',
   't',
   'r',
   'o',
   'y',
   'G',
   'L',
   'X',
   'P',
   'i',
   'x',
   'm',
   'a',
   'p',
   0, // glXDestroyGLXPixmap
   'g',
   'l',
   'X',
   'D',
   'e',
   's',
   't',
   'r',
   'o',
   'y',
   'G',
   'L',
   'X',
   'V',
   'i',
   'd',
   'e',
   'o',
   'S',
   'o',
   'u',
   'r',
   'c',
   'e',
   'S',
   'G',
   'I',
   'X',
   0, // glXDestroyGLXVideoSourceSGIX
   'g',
   'l',
   'X',
   'D',
   'e',
   's',
   't',
   'r',
   'o',
   'y',
   'H',
   'y',
   'p',
   'e',
   'r',
   'p',
   'i',
   'p',
   'e',
   'C',
   'o',
   'n',
   'f',
   'i',
   'g',
   'S',
   'G',
   'I',
   'X',
   0, // glXDestroyHyperpipeConfigSGIX
   'g',
   'l',
   'X',
   'D',
   'e',
   's',
   't',
   'r',
   'o',
   'y',
   'P',
   'b',
   'u',
   'f',
   'f',
   'e',
   'r',
   0, // glXDestroyPbuffer
   'g',
   'l',
   'X',
   'D',
   'e',
   's',
   't',
   'r',
   'o',
   'y',
   'P',
   'i',
   'x',
   'm',
   'a',
   'p',
   0, // glXDestroyPixmap
   'g',
   'l',
   'X',
   'D',
   'e',
   's',
   't',
   'r',
   'o',
   'y',
   'W',
   'i',
   'n',
   'd',
   'o',
   'w',
   0, // glXDestroyWindow
   'g',
   'l',
   'X',
   'E',
   'n',
   'u',
   'm',
   'e',
   'r',
   'a',
   't',
   'e',
   'V',
   'i',
   'd',
   'e',
   'o',
   'C',
   'a',
   'p',
   't',
   'u',
   'r',
   'e',
   'D',
   'e',
   'v',
   'i',
   'c',
   'e',
   's',
   'N',
   'V',
   0, // glXEnumerateVideoCaptureDevicesNV
   'g',
   'l',
   'X',
   'E',
   'n',
   'u',
   'm',
   'e',
   'r',
   'a',
   't',
   'e',
   'V',
   'i',
   'd',
   'e',
   'o',
   'D',
   'e',
   'v',
   'i',
   'c',
   'e',
   's',
   'N',
   'V',
   0, // glXEnumerateVideoDevicesNV
   'g',
   'l',
   'X',
   'F',
   'r',
   'e',
   'e',
   'C',
   'o',
   'n',
   't',
   'e',
   'x',
   't',
   'E',
   'X',
   'T',
   0, // glXFreeContextEXT
   'g',
   'l',
   'X',
   'G',
   'e',
   't',
   'A',
   'G',
   'P',
   'O',
   'f',
   'f',
   's',
   'e',
   't',
   'M',
   'E',
   'S',
   'A',
   0, // glXGetAGPOffsetMESA
   'g',
   'l',
   'X',
   'G',
   'e',
   't',
   'C',
   'l',
   'i',
   'e',
   'n',
   't',
   'S',
   't',
   'r',
   'i',
   'n',
   'g',
   0, // glXGetClientString
   'g',
   'l',
   'X',
   'G',
   'e',
   't',
   'C',
   'o',
   'n',
   'f',
   'i',
   'g',
   0, // glXGetConfig
   'g',
   'l',
   'X',
   'G',
   'e',
   't',
   'C',
   'o',
   'n',
   't',
   'e',
   'x',
   't',
   'G',
   'P',
   'U',
   'I',
   'D',
   'A',
   'M',
   'D',
   0, // glXGetContextGPUIDAMD
   'g',
   'l',
   'X',
   'G',
   'e',
   't',
   'C',
   'o',
   'n',
   't',
   'e',
   'x',
   't',
   'I',
   'D',
   'E',
   'X',
   'T',
   0, // glXGetContextIDEXT
   'g',
   'l',
   'X',
   'G',
   'e',
   't',
   'C',
   'u',
   'r',
   'r',
   'e',
   'n',
   't',
   'A',
   's',
   's',
   'o',
   'c',
   'i',
   'a',
   't',
   'e',
   'd',
   'C',
   'o',
   'n',
   't',
   'e',
   'x',
   't',
   'A',
   'M',
   'D',
   0, // glXGetCurrentAssociatedContextAMD
   'g',
   'l',
   'X',
   'G',
   'e',
   't',
   'C',
   'u',
   'r',
   'r',
   'e',
   'n',
   't',
   'C',
   'o',
   'n',
   't',
   'e',
   'x',
   't',
   0, // glXGetCurrentContext
   'g',
   'l',
   'X',
   'G',
   'e',
   't',
   'C',
   'u',
   'r',
   'r',
   'e',
   'n',
   't',
   'D',
   'i',
   's',
   'p',
   'l',
   'a',
   'y',
   0, // glXGetCurrentDisplay
   'g',
   'l',
   'X',
   'G',
   'e',
   't',
   'C',
   'u',
   'r',
   'r',
   'e',
   'n',
   't',
   'D',
   'i',
   's',
   'p',
   'l',
   'a',
   'y',
   'E',
   'X',
   'T',
   0, // glXGetCurrentDisplayEXT
   'g',
   'l',
   'X',
   'G',
   'e',
   't',
   'C',
   'u',
   'r',
   'r',
   'e',
   'n',
   't',
   'D',
   'r',
   'a',
   'w',
   'a',
   'b',
   'l',
   'e',
   0, // glXGetCurrentDrawable
   'g',
   'l',
   'X',
   'G',
   'e',
   't',
   'C',
   'u',
   'r',
   'r',
   'e',
   'n',
   't',
   'R',
   'e',
   'a',
   'd',
   'D',
   'r',
   'a',
   'w',
   'a',
   'b',
   'l',
   'e',
   0, // glXGetCurrentReadDrawable
   'g',
   'l',
   'X',
   'G',
   'e',
   't',
   'C',
   'u',
   'r',
   'r',
   'e',
   'n',
   't',
   'R',
   'e',
   'a',
   'd',
   'D',
   'r',
   'a',
   'w',
   'a',
   'b',
   'l',
   'e',
   'S',
   'G',
   'I',
   0, // glXGetCurrentReadDrawableSGI
   'g',
   'l',
   'X',
   'G',
   'e',
   't',
   'F',
   'B',
   'C',
   'o',
   'n',
   'f',
   'i',
   'g',
   'A',
   't',
   't',
   'r',
   'i',
   'b',
   0, // glXGetFBConfigAttrib
   'g',
   'l',
   'X',
   'G',
   'e',
   't',
   'F',
   'B',
   'C',
   'o',
   'n',
   'f',
   'i',
   'g',
   'A',
   't',
   't',
   'r',
   'i',
   'b',
   'S',
   'G',
   'I',
   'X',
   0, // glXGetFBConfigAttribSGIX
   'g',
   'l',
   'X',
   'G',
   'e',
   't',
   'F',
   'B',
   'C',
   'o',
   'n',
   'f',
   'i',
   'g',
   'F',
   'r',
   'o',
   'm',
   'V',
   'i',
   's',
   'u',
   'a',
   'l',
   'S',
   'G',
   'I',
   'X',
   0, // glXGetFBConfigFromVisualSGIX
   'g',
   'l',
   'X',
   'G',
   'e',
   't',
   'F',
   'B',
   'C',
   'o',
   'n',
   'f',
   'i',
   'g',
   's',
   0, // glXGetFBConfigs
   'g',
   'l',
   'X',
   'G',
   'e',
   't',
   'G',
   'P',
   'U',
   'I',
   'D',
   's',
   'A',
   'M',
   'D',
   0, // glXGetGPUIDsAMD
   'g',
   'l',
   'X',
   'G',
   'e',
   't',
   'G',
   'P',
   'U',
   'I',
   'n',
   'f',
   'o',
   'A',
   'M',
   'D',
   0, // glXGetGPUInfoAMD
   'g',
   'l',
   'X',
   'G',
   'e',
   't',
   'M',
   's',
   'c',
   'R',
   'a',
   't',
   'e',
   'O',
   'M',
   'L',
   0, // glXGetMscRateOML
   'g',
   'l',
   'X',
   'G',
   'e',
   't',
   'P',
   'r',
   'o',
   'c',
   'A',
   'd',
   'd',
   'r',
   'e',
   's',
   's',
   0, // glXGetProcAddress
   'g',
   'l',
   'X',
   'G',
   'e',
   't',
   'P',
   'r',
   'o',
   'c',
   'A',
   'd',
   'd',
   'r',
   'e',
   's',
   's',
   'A',
   'R',
   'B',
   0, // glXGetProcAddressARB
   'g',
   'l',
   'X',
   'G',
   'e',
   't',
   'S',
   'e',
   'l',
   'e',
   'c',
   't',
   'e',
   'd',
   'E',
   'v',
   'e',
   'n',
   't',
   0, // glXGetSelectedEvent
   'g',
   'l',
   'X',
   'G',
   'e',
   't',
   'S',
   'e',
   'l',
   'e',
   'c',
   't',
   'e',
   'd',
   'E',
   'v',
   'e',
   'n',
   't',
   'S',
   'G',
   'I',
   'X',
   0, // glXGetSelectedEventSGIX
   'g',
   'l',
   'X',
   'G',
   'e',
   't',
   'S',
   'w',
   'a',
   'p',
   'I',
   'n',
   't',
   'e',
   'r',
   'v',
   'a',
   'l',
   'M',
   'E',
   'S',
   'A',
   0, // glXGetSwapIntervalMESA
   'g',
   'l',
   'X',
   'G',
   'e',
   't',
   'S',
   'y',
   'n',
   'c',
   'V',
   'a',
   'l',
   'u',
   'e',
   's',
   'O',
   'M',
   'L',
   0, // glXGetSyncValuesOML
   'g',
   'l',
   'X',
   'G',
   'e',
   't',
   'T',
   'r',
   'a',
   'n',
   's',
   'p',
   'a',
   'r',
   'e',
   'n',
   't',
   'I',
   'n',
   'd',
   'e',
   'x',
   'S',
   'U',
   'N',
   0, // glXGetTransparentIndexSUN
   'g',
   'l',
   'X',
   'G',
   'e',
   't',
   'V',
   'i',
   'd',
   'e',
   'o',
   'D',
   'e',
   'v',
   'i',
   'c',
   'e',
   'N',
   'V',
   0, // glXGetVideoDeviceNV
   'g',
   'l',
   'X',
   'G',
   'e',
   't',
   'V',
   'i',
   'd',
   'e',
   'o',
   'I',
   'n',
   'f',
   'o',
   'N',
   'V',
   0, // glXGetVideoInfoNV
   'g',
   'l',
   'X',
   'G',
   'e',
   't',
   'V',
   'i',
   'd',
   'e',
   'o',
   'S',
   'y',
   'n',
   'c',
   'S',
   'G',
   'I',
   0, // glXGetVideoSyncSGI
   'g',
   'l',
   'X',
   'G',
   'e',
   't',
   'V',
   'i',
   's',
   'u',
   'a',
   'l',
   'F',
   'r',
   'o',
   'm',
   'F',
   'B',
   'C',
   'o',
   'n',
   'f',
   'i',
   'g',
   0, // glXGetVisualFromFBConfig
   'g',
   'l',
   'X',
   'G',
   'e',
   't',
   'V',
   'i',
   's',
   'u',
   'a',
   'l',
   'F',
   'r',
   'o',
   'm',
   'F',
   'B',
   'C',
   'o',
   'n',
   'f',
   'i',
   'g',
   'S',
   'G',
   'I',
   'X',
   0, // glXGetVisualFromFBConfigSGIX
   'g',
   'l',
   'X',
   'H',
   'y',
   'p',
   'e',
   'r',
   'p',
   'i',
   'p',
   'e',
   'A',
   't',
   't',
   'r',
   'i',
   'b',
   'S',
   'G',
   'I',
   'X',
   0, // glXHyperpipeAttribSGIX
   'g',
   'l',
   'X',
   'H',
   'y',
   'p',
   'e',
   'r',
   'p',
   'i',
   'p',
   'e',
   'C',
   'o',
   'n',
   'f',
   'i',
   'g',
   'S',
   'G',
   'I',
   'X',
   0, // glXHyperpipeConfigSGIX
   'g',
   'l',
   'X',
   'I',
   'm',
   'p',
   'o',
   'r',
   't',
   'C',
   'o',
   'n',
   't',
   'e',
   'x',
   't',
   'E',
   'X',
   'T',
   0, // glXImportContextEXT
   'g',
   'l',
   'X',
   'I',
   's',
   'D',
   'i',
   'r',
   'e',
   'c',
   't',
   0, // glXIsDirect
   'g',
   'l',
   'X',
   'J',
   'o',
   'i',
   'n',
   'S',
   'w',
   'a',
   'p',
   'G',
   'r',
   'o',
   'u',
   'p',
   'N',
   'V',
   0, // glXJoinSwapGroupNV
   'g',
   'l',
   'X',
   'J',
   'o',
   'i',
   'n',
   'S',
   'w',
   'a',
   'p',
   'G',
   'r',
   'o',
   'u',
   'p',
   'S',
   'G',
   'I',
   'X',
   0, // glXJoinSwapGroupSGIX
   'g',
   'l',
   'X',
   'L',
   'o',
   'c',
   'k',
   'V',
   'i',
   'd',
   'e',
   'o',
   'C',
   'a',
   'p',
   't',
   'u',
   'r',
   'e',
   'D',
   'e',
   'v',
   'i',
   'c',
   'e',
   'N',
   'V',
   0, // glXLockVideoCaptureDeviceNV
   'g',
   'l',
   'X',
   'M',
   'a',
   'k',
   'e',
   'A',
   's',
   's',
   'o',
   'c',
   'i',
   'a',
   't',
   'e',
   'd',
   'C',
   'o',
   'n',
   't',
   'e',
   'x',
   't',
   'C',
   'u',
   'r',
   'r',
   'e',
   'n',
   't',
   'A',
   'M',
   'D',
   0, // glXMakeAssociatedContextCurrentAMD
   'g',
   'l',
   'X',
   'M',
   'a',
   'k',
   'e',
   'C',
   'o',
   'n',
   't',
   'e',
   'x',
   't',
   'C',
   'u',
   'r',
   'r',
   'e',
   'n',
   't',
   0, // glXMakeContextCurrent
   'g',
   'l',
   'X',
   'M',
   'a',
   'k',
   'e',
   'C',
   'u',
   'r',
   'r',
   'e',
   'n',
   't',
   0, // glXMakeCurrent
   'g',
   'l',
   'X',
   'M',
   'a',
   'k',
   'e',
   'C',
   'u',
   'r',
   'r',
   'e',
   'n',
   't',
   'R',
   'e',
   'a',
   'd',
   'S',
   'G',
   'I',
   0, // glXMakeCurrentReadSGI
   'g',
   'l',
   'X',
   'N',
   'a',
   'm',
   'e',
   'd',
   'C',
   'o',
   'p',
   'y',
   'B',
   'u',
   'f',
   'f',
   'e',
   'r',
   'S',
   'u',
   'b',
   'D',
   'a',
   't',
   'a',
   'N',
   'V',
   0, // glXNamedCopyBufferSubDataNV
   'g',
   'l',
   'X',
   'Q',
   'u',
   'e',
   'r',
   'y',
   'C',
   'h',
   'a',
   'n',
   'n',
   'e',
   'l',
   'D',
   'e',
   'l',
   't',
   'a',
   's',
   'S',
   'G',
   'I',
   'X',
   0, // glXQueryChannelDeltasSGIX
   'g',
   'l',
   'X',
   'Q',
   'u',
   'e',
   'r',
   'y',
   'C',
   'h',
   'a',
   'n',
   'n',
   'e',
   'l',
   'R',
   'e',
   'c',
   't',
   'S',
   'G',
   'I',
   'X',
   0, // glXQueryChannelRectSGIX
   'g',
   'l',
   'X',
   'Q',
   'u',
   'e',
   'r',
   'y',
   'C',
   'o',
   'n',
   't',
   'e',
   'x',
   't',
   0, // glXQueryContext
   'g',
   'l',
   'X',
   'Q',
   'u',
   'e',
   'r',
   'y',
   'C',
   'o',
   'n',
   't',
   'e',
   'x',
   't',
   'I',
   'n',
   'f',
   'o',
   'E',
   'X',
   'T',
   0, // glXQueryContextInfoEXT
   'g',
   'l',
   'X',
   'Q',
   'u',
   'e',
   'r',
   'y',
   'C',
   'u',
   'r',
   'r',
   'e',
   'n',
   't',
   'R',
   'e',
   'n',
   'd',
   'e',
   'r',
   'e',
   'r',
   'I',
   'n',
   't',
   'e',
   'g',
   'e',
   'r',
   'M',
   'E',
   'S',
   'A',
   0, // glXQueryCurrentRendererIntegerMESA
   'g',
   'l',
   'X',
   'Q',
   'u',
   'e',
   'r',
   'y',
   'C',
   'u',
   'r',
   'r',
   'e',
   'n',
   't',
   'R',
   'e',
   'n',
   'd',
   'e',
   'r',
   'e',
   'r',
   'S',
   't',
   'r',
   'i',
   'n',
   'g',
   'M',
   'E',
   'S',
   'A',
   0, // glXQueryCurrentRendererStringMESA
   'g',
   'l',
   'X',
   'Q',
   'u',
   'e',
   'r',
   'y',
   'D',
   'r',
   'a',
   'w',
   'a',
   'b',
   'l',
   'e',
   0, // glXQueryDrawable
   'g',
   'l',
   'X',
   'Q',
   'u',
   'e',
   'r',
   'y',
   'E',
   'x',
   't',
   'e',
   'n',
   's',
   'i',
   'o',
   'n',
   0, // glXQueryExtension
   'g',
   'l',
   'X',
   'Q',
   'u',
   'e',
   'r',
   'y',
   'E',
   'x',
   't',
   'e',
   'n',
   's',
   'i',
   'o',
   'n',
   's',
   'S',
   't',
   'r',
   'i',
   'n',
   'g',
   0, // glXQueryExtensionsString
   'g',
   'l',
   'X',
   'Q',
   'u',
   'e',
   'r',
   'y',
   'F',
   'r',
   'a',
   'm',
   'e',
   'C',
   'o',
   'u',
   'n',
   't',
   'N',
   'V',
   0, // glXQueryFrameCountNV
   'g',
   'l',
   'X',
   'Q',
   'u',
   'e',
   'r',
   'y',
   'G',
   'L',
   'X',
   'P',
   'b',
   'u',
   'f',
   'f',
   'e',
   'r',
   'S',
   'G',
   'I',
   'X',
   0, // glXQueryGLXPbufferSGIX
   'g',
   'l',
   'X',
   'Q',
   'u',
   'e',
   'r',
   'y',
   'H',
   'y',
   'p',
   'e',
   'r',
   'p',
   'i',
   'p',
   'e',
   'A',
   't',
   't',
   'r',
   'i',
   'b',
   'S',
   'G',
   'I',
   'X',
   0, // glXQueryHyperpipeAttribSGIX
   'g',
   'l',
   'X',
   'Q',
   'u',
   'e',
   'r',
   'y',
   'H',
   'y',
   'p',
   'e',
   'r',
   'p',
   'i',
   'p',
   'e',
   'B',
   'e',
   's',
   't',
   'A',
   't',
   't',
   'r',
   'i',
   'b',
   'S',
   'G',
   'I',
   'X',
   0, // glXQueryHyperpipeBestAttribSGIX
   'g',
   'l',
   'X',
   'Q',
   'u',
   'e',
   'r',
   'y',
   'H',
   'y',
   'p',
   'e',
   'r',
   'p',
   'i',
   'p',
   'e',
   'C',
   'o',
   'n',
   'f',
   'i',
   'g',
   'S',
   'G',
   'I',
   'X',
   0, // glXQueryHyperpipeConfigSGIX
   'g',
   'l',
   'X',
   'Q',
   'u',
   'e',
   'r',
   'y',
   'H',
   'y',
   'p',
   'e',
   'r',
   'p',
   'i',
   'p',
   'e',
   'N',
   'e',
   't',
   'w',
   'o',
   'r',
   'k',
   'S',
   'G',
   'I',
   'X',
   0, // glXQueryHyperpipeNetworkSGIX
   'g',
   'l',
   'X',
   'Q',
   'u',
   'e',
   'r',
   'y',
   'M',
   'a',
   'x',
   'S',
   'w',
   'a',
   'p',
   'B',
   'a',
   'r',
   'r',
   'i',
   'e',
   'r',
   's',
   'S',
   'G',
   'I',
   'X',
   0, // glXQueryMaxSwapBarriersSGIX
   'g',
   'l',
   'X',
   'Q',
   'u',
   'e',
   'r',
   'y',
   'M',
   'a',
   'x',
   'S',
   'w',
   'a',
   'p',
   'G',
   'r',
   'o',
   'u',
   'p',
   's',
   'N',
   'V',
   0, // glXQueryMaxSwapGroupsNV
   'g',
   'l',
   'X',
   'Q',
   'u',
   'e',
   'r',
   'y',
   'R',
   'e',
   'n',
   'd',
   'e',
   'r',
   'e',
   'r',
   'I',
   'n',
   't',
   'e',
   'g',
   'e',
   'r',
   'M',
   'E',
   'S',
   'A',
   0, // glXQueryRendererIntegerMESA
   'g',
   'l',
   'X',
   'Q',
   'u',
   'e',
   'r',
   'y',
   'R',
   'e',
   'n',
   'd',
   'e',
   'r',
   'e',
   'r',
   'S',
   't',
   'r',
   'i',
   'n',
   'g',
   'M',
   'E',
   'S',
   'A',
   0, // glXQueryRendererStringMESA
   'g',
   'l',
   'X',
   'Q',
   'u',
   'e',
   'r',
   'y',
   'S',
   'e',
   'r',
   'v',
   'e',
   'r',
   'S',
   't',
   'r',
   'i',
   'n',
   'g',
   0, // glXQueryServerString
   'g',
   'l',
   'X',
   'Q',
   'u',
   'e',
   'r',
   'y',
   'S',
   'w',
   'a',
   'p',
   'G',
   'r',
   'o',
   'u',
   'p',
   'N',
   'V',
   0, // glXQuerySwapGroupNV
   'g',
   'l',
   'X',
   'Q',
   'u',
   'e',
   'r',
   'y',
   'V',
   'e',
   'r',
   's',
   'i',
   'o',
   'n',
   0, // glXQueryVersion
   'g',
   'l',
   'X',
   'Q',
   'u',
   'e',
   'r',
   'y',
   'V',
   'i',
   'd',
   'e',
   'o',
   'C',
   'a',
   'p',
   't',
   'u',
   'r',
   'e',
   'D',
   'e',
   'v',
   'i',
   'c',
   'e',
   'N',
   'V',
   0, // glXQueryVideoCaptureDeviceNV
   'g',
   'l',
   'X',
   'R',
   'e',
   'l',
   'e',
   'a',
   's',
   'e',
   'B',
   'u',
   'f',
   'f',
   'e',
   'r',
   's',
   'M',
   'E',
   'S',
   'A',
   0, // glXReleaseBuffersMESA
   'g',
   'l',
   'X',
   'R',
   'e',
   'l',
   'e',
   'a',
   's',
   'e',
   'T',
   'e',
   'x',
   'I',
   'm',
   'a',
   'g',
   'e',
   'E',
   'X',
   'T',
   0, // glXReleaseTexImageEXT
   'g',
   'l',
   'X',
   'R',
   'e',
   'l',
   'e',
   'a',
   's',
   'e',
   'V',
   'i',
   'd',
   'e',
   'o',
   'C',
   'a',
   'p',
   't',
   'u',
   'r',
   'e',
   'D',
   'e',
   'v',
   'i',
   'c',
   'e',
   'N',
   'V',
   0, // glXReleaseVideoCaptureDeviceNV
   'g',
   'l',
   'X',
   'R',
   'e',
   'l',
   'e',
   'a',
   's',
   'e',
   'V',
   'i',
   'd',
   'e',
   'o',
   'D',
   'e',
   'v',
   'i',
   'c',
   'e',
   'N',
   'V',
   0, // glXReleaseVideoDeviceNV
   'g',
   'l',
   'X',
   'R',
   'e',
   'l',
   'e',
   'a',
   's',
   'e',
   'V',
   'i',
   'd',
   'e',
   'o',
   'I',
   'm',
   'a',
   'g',
   'e',
   'N',
   'V',
   0, // glXReleaseVideoImageNV
   'g',
   'l',
   'X',
   'R',
   'e',
   's',
   'e',
   't',
   'F',
   'r',
   'a',
   'm',
   'e',
   'C',
   'o',
   'u',
   'n',
   't',
   'N',
   'V',
   0, // glXResetFrameCountNV
   'g',
   'l',
   'X',
   'S',
   'e',
   'l',
   'e',
   'c',
   't',
   'E',
   'v',
   'e',
   'n',
   't',
   0, // glXSelectEvent
   'g',
   'l',
   'X',
   'S',
   'e',
   'l',
   'e',
   'c',
   't',
   'E',
   'v',
   'e',
   'n',
   't',
   'S',
   'G',
   'I',
   'X',
   0, // glXSelectEventSGIX
   'g',
   'l',
   'X',
   'S',
   'e',
   'n',
   'd',
   'P',
   'b',
   'u',
   'f',
   'f',
   'e',
   'r',
   'T',
   'o',
   'V',
   'i',
   'd',
   'e',
   'o',
   'N',
   'V',
   0, // glXSendPbufferToVideoNV
   'g',
   'l',
   'X',
   'S',
   'e',
   't',
   '3',
   'D',
   'f',
   'x',
   'M',
   'o',
   'd',
   'e',
   'M',
   'E',
   'S',
   'A',
   0, // glXSet3DfxModeMESA
   'g',
   'l',
   'X',
   'S',
   'w',
   'a',
   'p',
   'B',
   'u',
   'f',
   'f',
   'e',
   'r',
   's',
   0, // glXSwapBuffers
   'g',
   'l',
   'X',
   'S',
   'w',
   'a',
   'p',
   'B',
   'u',
   'f',
   'f',
   'e',
   'r',
   's',
   'M',
   's',
   'c',
   'O',
   'M',
   'L',
   0, // glXSwapBuffersMscOML
   'g',
   'l',
   'X',
   'S',
   'w',
   'a',
   'p',
   'I',
   'n',
   't',
   'e',
   'r',
   'v',
   'a',
   'l',
   'E',
   'X',
   'T',
   0, // glXSwapIntervalEXT
   'g',
   'l',
   'X',
   'S',
   'w',
   'a',
   'p',
   'I',
   'n',
   't',
   'e',
   'r',
   'v',
   'a',
   'l',
   'M',
   'E',
   'S',
   'A',
   0, // glXSwapIntervalMESA
   'g',
   'l',
   'X',
   'S',
   'w',
   'a',
   'p',
   'I',
   'n',
   't',
   'e',
   'r',
   'v',
   'a',
   'l',
   'S',
   'G',
   'I',
   0, // glXSwapIntervalSGI
   'g',
   'l',
   'X',
   'U',
   's',
   'e',
   'X',
   'F',
   'o',
   'n',
   't',
   0, // glXUseXFont
   'g',
   'l',
   'X',
   'W',
   'a',
   'i',
   't',
   'F',
   'o',
   'r',
   'M',
   's',
   'c',
   'O',
   'M',
   'L',
   0, // glXWaitForMscOML
   'g',
   'l',
   'X',
   'W',
   'a',
   'i',
   't',
   'F',
   'o',
   'r',
   'S',
   'b',
   'c',
   'O',
   'M',
   'L',
   0, // glXWaitForSbcOML
   'g',
   'l',
   'X',
   'W',
   'a',
   'i',
   't',
   'G',
   'L',
   0, // glXWaitGL
   'g',
   'l',
   'X',
   'W',
   'a',
   'i',
   't',
   'V',
   'i',
   'd',
   'e',
   'o',
   'S',
   'y',
   'n',
   'c',
   'S',
   'G',
   'I',
   0, // glXWaitVideoSyncSGI
   'g',
   'l',
   'X',
   'W',
   'a',
   'i',
   't',
   'X',
   0, // glXWaitX
    0 };

static void *glx_provider_resolver(const char *name,
                                   const enum glx_provider *providers,
                                   const uint32_t *entrypoints)
{
    int i;
    for (i = 0; providers[i] != glx_provider_terminator; i++) {
        const char *provider_name = enum_string + enum_string_offsets[providers[i]];
        switch (providers[i]) {

        case PROVIDER_GLX_10:
            if (true)
                return epoxy_glx_dlsym(entrypoint_strings + entrypoints[i]);
            break;
        case PROVIDER_GLX_11:
            if (true)
                return epoxy_glx_dlsym(entrypoint_strings + entrypoints[i]);
            break;
        case PROVIDER_GLX_12:
            if (true)
                return epoxy_glx_dlsym(entrypoint_strings + entrypoints[i]);
            break;
        case PROVIDER_GLX_13:
            if (true)
                return epoxy_glx_dlsym(entrypoint_strings + entrypoints[i]);
            break;
        case PROVIDER_GLX_AMD_gpu_association:
            if (epoxy_conservative_has_glx_extension(provider_name))
                return glXGetProcAddress((const GLubyte *)entrypoint_strings + entrypoints[i]);
            break;
        case PROVIDER_GLX_ARB_create_context:
            if (epoxy_conservative_has_glx_extension(provider_name))
                return glXGetProcAddress((const GLubyte *)entrypoint_strings + entrypoints[i]);
            break;
        case PROVIDER_GLX_ARB_get_proc_address:
            if (epoxy_conservative_has_glx_extension(provider_name))
                return glXGetProcAddress((const GLubyte *)entrypoint_strings + entrypoints[i]);
            break;
        case PROVIDER_GLX_EXT_import_context:
            if (epoxy_conservative_has_glx_extension(provider_name))
                return glXGetProcAddress((const GLubyte *)entrypoint_strings + entrypoints[i]);
            break;
        case PROVIDER_GLX_EXT_swap_control:
            if (epoxy_conservative_has_glx_extension(provider_name))
                return glXGetProcAddress((const GLubyte *)entrypoint_strings + entrypoints[i]);
            break;
        case PROVIDER_GLX_EXT_texture_from_pixmap:
            if (epoxy_conservative_has_glx_extension(provider_name))
                return glXGetProcAddress((const GLubyte *)entrypoint_strings + entrypoints[i]);
            break;
        case PROVIDER_GLX_MESA_agp_offset:
            if (epoxy_conservative_has_glx_extension(provider_name))
                return glXGetProcAddress((const GLubyte *)entrypoint_strings + entrypoints[i]);
            break;
        case PROVIDER_GLX_MESA_copy_sub_buffer:
            if (epoxy_conservative_has_glx_extension(provider_name))
                return glXGetProcAddress((const GLubyte *)entrypoint_strings + entrypoints[i]);
            break;
        case PROVIDER_GLX_MESA_pixmap_colormap:
            if (epoxy_conservative_has_glx_extension(provider_name))
                return glXGetProcAddress((const GLubyte *)entrypoint_strings + entrypoints[i]);
            break;
        case PROVIDER_GLX_MESA_query_renderer:
            if (epoxy_conservative_has_glx_extension(provider_name))
                return glXGetProcAddress((const GLubyte *)entrypoint_strings + entrypoints[i]);
            break;
        case PROVIDER_GLX_MESA_release_buffers:
            if (epoxy_conservative_has_glx_extension(provider_name))
                return glXGetProcAddress((const GLubyte *)entrypoint_strings + entrypoints[i]);
            break;
        case PROVIDER_GLX_MESA_set_3dfx_mode:
            if (epoxy_conservative_has_glx_extension(provider_name))
                return glXGetProcAddress((const GLubyte *)entrypoint_strings + entrypoints[i]);
            break;
        case PROVIDER_GLX_MESA_swap_control:
            if (epoxy_conservative_has_glx_extension(provider_name))
                return glXGetProcAddress((const GLubyte *)entrypoint_strings + entrypoints[i]);
            break;
        case PROVIDER_GLX_NV_copy_buffer:
            if (epoxy_conservative_has_glx_extension(provider_name))
                return glXGetProcAddress((const GLubyte *)entrypoint_strings + entrypoints[i]);
            break;
        case PROVIDER_GLX_NV_copy_image:
            if (epoxy_conservative_has_glx_extension(provider_name))
                return glXGetProcAddress((const GLubyte *)entrypoint_strings + entrypoints[i]);
            break;
        case PROVIDER_GLX_NV_delay_before_swap:
            if (epoxy_conservative_has_glx_extension(provider_name))
                return glXGetProcAddress((const GLubyte *)entrypoint_strings + entrypoints[i]);
            break;
        case PROVIDER_GLX_NV_present_video:
            if (epoxy_conservative_has_glx_extension(provider_name))
                return glXGetProcAddress((const GLubyte *)entrypoint_strings + entrypoints[i]);
            break;
        case PROVIDER_GLX_NV_swap_group:
            if (epoxy_conservative_has_glx_extension(provider_name))
                return glXGetProcAddress((const GLubyte *)entrypoint_strings + entrypoints[i]);
            break;
        case PROVIDER_GLX_NV_video_capture:
            if (epoxy_conservative_has_glx_extension(provider_name))
                return glXGetProcAddress((const GLubyte *)entrypoint_strings + entrypoints[i]);
            break;
        case PROVIDER_GLX_NV_video_out:
            if (epoxy_conservative_has_glx_extension(provider_name))
                return glXGetProcAddress((const GLubyte *)entrypoint_strings + entrypoints[i]);
            break;
        case PROVIDER_GLX_OML_sync_control:
            if (epoxy_conservative_has_glx_extension(provider_name))
                return glXGetProcAddress((const GLubyte *)entrypoint_strings + entrypoints[i]);
            break;
        case PROVIDER_GLX_SGIX_fbconfig:
            if (epoxy_conservative_has_glx_extension(provider_name))
                return glXGetProcAddress((const GLubyte *)entrypoint_strings + entrypoints[i]);
            break;
        case PROVIDER_GLX_SGIX_hyperpipe:
            if (epoxy_conservative_has_glx_extension(provider_name))
                return glXGetProcAddress((const GLubyte *)entrypoint_strings + entrypoints[i]);
            break;
        case PROVIDER_GLX_SGIX_pbuffer:
            if (epoxy_conservative_has_glx_extension(provider_name))
                return glXGetProcAddress((const GLubyte *)entrypoint_strings + entrypoints[i]);
            break;
        case PROVIDER_GLX_SGIX_swap_barrier:
            if (epoxy_conservative_has_glx_extension(provider_name))
                return glXGetProcAddress((const GLubyte *)entrypoint_strings + entrypoints[i]);
            break;
        case PROVIDER_GLX_SGIX_swap_group:
            if (epoxy_conservative_has_glx_extension(provider_name))
                return glXGetProcAddress((const GLubyte *)entrypoint_strings + entrypoints[i]);
            break;
        case PROVIDER_GLX_SGIX_video_resize:
            if (epoxy_conservative_has_glx_extension(provider_name))
                return glXGetProcAddress((const GLubyte *)entrypoint_strings + entrypoints[i]);
            break;
        case PROVIDER_GLX_SGIX_video_source:
            if (epoxy_conservative_has_glx_extension(provider_name))
                return glXGetProcAddress((const GLubyte *)entrypoint_strings + entrypoints[i]);
            break;
        case PROVIDER_GLX_SGI_cushion:
            if (epoxy_conservative_has_glx_extension(provider_name))
                return glXGetProcAddress((const GLubyte *)entrypoint_strings + entrypoints[i]);
            break;
        case PROVIDER_GLX_SGI_make_current_read:
            if (epoxy_conservative_has_glx_extension(provider_name))
                return glXGetProcAddress((const GLubyte *)entrypoint_strings + entrypoints[i]);
            break;
        case PROVIDER_GLX_SGI_swap_control:
            if (epoxy_conservative_has_glx_extension(provider_name))
                return glXGetProcAddress((const GLubyte *)entrypoint_strings + entrypoints[i]);
            break;
        case PROVIDER_GLX_SGI_video_sync:
            if (epoxy_conservative_has_glx_extension(provider_name))
                return glXGetProcAddress((const GLubyte *)entrypoint_strings + entrypoints[i]);
            break;
        case PROVIDER_GLX_SUN_get_transparent_index:
            if (epoxy_conservative_has_glx_extension(provider_name))
                return glXGetProcAddress((const GLubyte *)entrypoint_strings + entrypoints[i]);
            break;
        case PROVIDER_always_present:
            if (true)
                return epoxy_glx_dlsym(entrypoint_strings + entrypoints[i]);
            break;
        case glx_provider_terminator:
            abort(); /* Not reached */
        }
    }

    if (epoxy_resolver_failure_handler)
        return epoxy_resolver_failure_handler(name);

    fprintf(stderr, "No provider of %s found.  Requires one of:\n", name);
    for (i = 0; providers[i] != glx_provider_terminator; i++) {
        fprintf(stderr, "    %s\n", enum_string + enum_string_offsets[providers[i]]);
    }
    if (providers[0] == glx_provider_terminator) {
        fprintf(stderr, "    No known providers.  This is likely a bug "
                        "in libepoxy code generation\n");
    }
    abort();
}

EPOXY_NOINLINE static void *
glx_single_resolver(enum glx_provider provider, uint32_t entrypoint_offset);

static void *
glx_single_resolver(enum glx_provider provider, uint32_t entrypoint_offset)
{
    enum glx_provider providers[] = {
        provider,
        glx_provider_terminator
    };
    return glx_provider_resolver(entrypoint_strings + entrypoint_offset,
                                providers, &entrypoint_offset);
}

static PFNGLXBINDCHANNELTOWINDOWSGIXPROC
epoxy_glXBindChannelToWindowSGIX_resolver(void)
{
    return glx_single_resolver(PROVIDER_GLX_SGIX_video_resize, 0 /* glXBindChannelToWindowSGIX */);
}

static PFNGLXBINDHYPERPIPESGIXPROC
epoxy_glXBindHyperpipeSGIX_resolver(void)
{
    return glx_single_resolver(PROVIDER_GLX_SGIX_hyperpipe, 27 /* glXBindHyperpipeSGIX */);
}

static PFNGLXBINDSWAPBARRIERNVPROC
epoxy_glXBindSwapBarrierNV_resolver(void)
{
    return glx_single_resolver(PROVIDER_GLX_NV_swap_group, 48 /* glXBindSwapBarrierNV */);
}

static PFNGLXBINDSWAPBARRIERSGIXPROC
epoxy_glXBindSwapBarrierSGIX_resolver(void)
{
    return glx_single_resolver(PROVIDER_GLX_SGIX_swap_barrier, 69 /* glXBindSwapBarrierSGIX */);
}

static PFNGLXBINDTEXIMAGEEXTPROC
epoxy_glXBindTexImageEXT_resolver(void)
{
    return glx_single_resolver(PROVIDER_GLX_EXT_texture_from_pixmap, 92 /* glXBindTexImageEXT */);
}

static PFNGLXBINDVIDEOCAPTUREDEVICENVPROC
epoxy_glXBindVideoCaptureDeviceNV_resolver(void)
{
    return glx_single_resolver(PROVIDER_GLX_NV_video_capture, 111 /* glXBindVideoCaptureDeviceNV */);
}

static PFNGLXBINDVIDEODEVICENVPROC
epoxy_glXBindVideoDeviceNV_resolver(void)
{
    return glx_single_resolver(PROVIDER_GLX_NV_present_video, 139 /* glXBindVideoDeviceNV */);
}

static PFNGLXBINDVIDEOIMAGENVPROC
epoxy_glXBindVideoImageNV_resolver(void)
{
    return glx_single_resolver(PROVIDER_GLX_NV_video_out, 160 /* glXBindVideoImageNV */);
}

static PFNGLXBLITCONTEXTFRAMEBUFFERAMDPROC
epoxy_glXBlitContextFramebufferAMD_resolver(void)
{
    return glx_single_resolver(PROVIDER_GLX_AMD_gpu_association, 180 /* glXBlitContextFramebufferAMD */);
}

static PFNGLXCHANNELRECTSGIXPROC
epoxy_glXChannelRectSGIX_resolver(void)
{
    return glx_single_resolver(PROVIDER_GLX_SGIX_video_resize, 209 /* glXChannelRectSGIX */);
}

static PFNGLXCHANNELRECTSYNCSGIXPROC
epoxy_glXChannelRectSyncSGIX_resolver(void)
{
    return glx_single_resolver(PROVIDER_GLX_SGIX_video_resize, 228 /* glXChannelRectSyncSGIX */);
}

static PFNGLXCHOOSEFBCONFIGPROC
epoxy_glXChooseFBConfig_resolver(void)
{
    return glx_single_resolver(PROVIDER_GLX_13, 251 /* glXChooseFBConfig */);
}

static PFNGLXCHOOSEFBCONFIGSGIXPROC
epoxy_glXChooseFBConfigSGIX_resolver(void)
{
    return glx_single_resolver(PROVIDER_GLX_SGIX_fbconfig, 269 /* glXChooseFBConfigSGIX */);
}

static PFNGLXCHOOSEVISUALPROC
epoxy_glXChooseVisual_resolver(void)
{
    return glx_single_resolver(PROVIDER_GLX_10, 291 /* glXChooseVisual */);
}

static PFNGLXCOPYBUFFERSUBDATANVPROC
epoxy_glXCopyBufferSubDataNV_resolver(void)
{
    return glx_single_resolver(PROVIDER_GLX_NV_copy_buffer, 307 /* glXCopyBufferSubDataNV */);
}

static PFNGLXCOPYCONTEXTPROC
epoxy_glXCopyContext_resolver(void)
{
    return glx_single_resolver(PROVIDER_GLX_10, 330 /* glXCopyContext */);
}

static PFNGLXCOPYIMAGESUBDATANVPROC
epoxy_glXCopyImageSubDataNV_resolver(void)
{
    return glx_single_resolver(PROVIDER_GLX_NV_copy_image, 345 /* glXCopyImageSubDataNV */);
}

static PFNGLXCOPYSUBBUFFERMESAPROC
epoxy_glXCopySubBufferMESA_resolver(void)
{
    return glx_single_resolver(PROVIDER_GLX_MESA_copy_sub_buffer, 367 /* glXCopySubBufferMESA */);
}

static PFNGLXCREATEASSOCIATEDCONTEXTAMDPROC
epoxy_glXCreateAssociatedContextAMD_resolver(void)
{
    return glx_single_resolver(PROVIDER_GLX_AMD_gpu_association, 388 /* glXCreateAssociatedContextAMD */);
}

static PFNGLXCREATEASSOCIATEDCONTEXTATTRIBSAMDPROC
epoxy_glXCreateAssociatedContextAttribsAMD_resolver(void)
{
    return glx_single_resolver(PROVIDER_GLX_AMD_gpu_association, 418 /* glXCreateAssociatedContextAttribsAMD */);
}

static PFNGLXCREATECONTEXTPROC
epoxy_glXCreateContext_resolver(void)
{
    return glx_single_resolver(PROVIDER_GLX_10, 455 /* glXCreateContext */);
}

static PFNGLXCREATECONTEXTATTRIBSARBPROC
epoxy_glXCreateContextAttribsARB_resolver(void)
{
    return glx_single_resolver(PROVIDER_GLX_ARB_create_context, 472 /* glXCreateContextAttribsARB */);
}

static PFNGLXCREATECONTEXTWITHCONFIGSGIXPROC
epoxy_glXCreateContextWithConfigSGIX_resolver(void)
{
    return glx_single_resolver(PROVIDER_GLX_SGIX_fbconfig, 499 /* glXCreateContextWithConfigSGIX */);
}

static PFNGLXCREATEGLXPBUFFERSGIXPROC
epoxy_glXCreateGLXPbufferSGIX_resolver(void)
{
    return glx_single_resolver(PROVIDER_GLX_SGIX_pbuffer, 530 /* glXCreateGLXPbufferSGIX */);
}

static PFNGLXCREATEGLXPIXMAPPROC
epoxy_glXCreateGLXPixmap_resolver(void)
{
    return glx_single_resolver(PROVIDER_GLX_10, 554 /* glXCreateGLXPixmap */);
}

static PFNGLXCREATEGLXPIXMAPMESAPROC
epoxy_glXCreateGLXPixmapMESA_resolver(void)
{
    return glx_single_resolver(PROVIDER_GLX_MESA_pixmap_colormap, 573 /* glXCreateGLXPixmapMESA */);
}

static PFNGLXCREATEGLXPIXMAPWITHCONFIGSGIXPROC
epoxy_glXCreateGLXPixmapWithConfigSGIX_resolver(void)
{
    return glx_single_resolver(PROVIDER_GLX_SGIX_fbconfig, 596 /* glXCreateGLXPixmapWithConfigSGIX */);
}

static PFNGLXCREATENEWCONTEXTPROC
epoxy_glXCreateNewContext_resolver(void)
{
    return glx_single_resolver(PROVIDER_GLX_13, 629 /* glXCreateNewContext */);
}

static PFNGLXCREATEPBUFFERPROC
epoxy_glXCreatePbuffer_resolver(void)
{
    return glx_single_resolver(PROVIDER_GLX_13, 649 /* glXCreatePbuffer */);
}

static PFNGLXCREATEPIXMAPPROC
epoxy_glXCreatePixmap_resolver(void)
{
    return glx_single_resolver(PROVIDER_GLX_13, 666 /* glXCreatePixmap */);
}

static PFNGLXCREATEWINDOWPROC
epoxy_glXCreateWindow_resolver(void)
{
    return glx_single_resolver(PROVIDER_GLX_13, 682 /* glXCreateWindow */);
}

static PFNGLXCUSHIONSGIPROC
epoxy_glXCushionSGI_resolver(void)
{
    return glx_single_resolver(PROVIDER_GLX_SGI_cushion, 698 /* glXCushionSGI */);
}

static PFNGLXDELAYBEFORESWAPNVPROC
epoxy_glXDelayBeforeSwapNV_resolver(void)
{
    return glx_single_resolver(PROVIDER_GLX_NV_delay_before_swap, 712 /* glXDelayBeforeSwapNV */);
}

static PFNGLXDELETEASSOCIATEDCONTEXTAMDPROC
epoxy_glXDeleteAssociatedContextAMD_resolver(void)
{
    return glx_single_resolver(PROVIDER_GLX_AMD_gpu_association, 733 /* glXDeleteAssociatedContextAMD */);
}

static PFNGLXDESTROYCONTEXTPROC
epoxy_glXDestroyContext_resolver(void)
{
    return glx_single_resolver(PROVIDER_GLX_10, 763 /* glXDestroyContext */);
}

static PFNGLXDESTROYGLXPBUFFERSGIXPROC
epoxy_glXDestroyGLXPbufferSGIX_resolver(void)
{
    return glx_single_resolver(PROVIDER_GLX_SGIX_pbuffer, 781 /* glXDestroyGLXPbufferSGIX */);
}

static PFNGLXDESTROYGLXPIXMAPPROC
epoxy_glXDestroyGLXPixmap_resolver(void)
{
    return glx_single_resolver(PROVIDER_GLX_10, 806 /* glXDestroyGLXPixmap */);
}

static PFNGLXDESTROYGLXVIDEOSOURCESGIXPROC
epoxy_glXDestroyGLXVideoSourceSGIX_resolver(void)
{
    return glx_single_resolver(PROVIDER_GLX_SGIX_video_source, 826 /* glXDestroyGLXVideoSourceSGIX */);
}

static PFNGLXDESTROYHYPERPIPECONFIGSGIXPROC
epoxy_glXDestroyHyperpipeConfigSGIX_resolver(void)
{
    return glx_single_resolver(PROVIDER_GLX_SGIX_hyperpipe, 855 /* glXDestroyHyperpipeConfigSGIX */);
}

static PFNGLXDESTROYPBUFFERPROC
epoxy_glXDestroyPbuffer_resolver(void)
{
    return glx_single_resolver(PROVIDER_GLX_13, 885 /* glXDestroyPbuffer */);
}

static PFNGLXDESTROYPIXMAPPROC
epoxy_glXDestroyPixmap_resolver(void)
{
    return glx_single_resolver(PROVIDER_GLX_13, 903 /* glXDestroyPixmap */);
}

static PFNGLXDESTROYWINDOWPROC
epoxy_glXDestroyWindow_resolver(void)
{
    return glx_single_resolver(PROVIDER_GLX_13, 920 /* glXDestroyWindow */);
}

static PFNGLXENUMERATEVIDEOCAPTUREDEVICESNVPROC
epoxy_glXEnumerateVideoCaptureDevicesNV_resolver(void)
{
    return glx_single_resolver(PROVIDER_GLX_NV_video_capture, 937 /* glXEnumerateVideoCaptureDevicesNV */);
}

static PFNGLXENUMERATEVIDEODEVICESNVPROC
epoxy_glXEnumerateVideoDevicesNV_resolver(void)
{
    return glx_single_resolver(PROVIDER_GLX_NV_present_video, 971 /* glXEnumerateVideoDevicesNV */);
}

static PFNGLXFREECONTEXTEXTPROC
epoxy_glXFreeContextEXT_resolver(void)
{
    return glx_single_resolver(PROVIDER_GLX_EXT_import_context, 998 /* glXFreeContextEXT */);
}

static PFNGLXGETAGPOFFSETMESAPROC
epoxy_glXGetAGPOffsetMESA_resolver(void)
{
    return glx_single_resolver(PROVIDER_GLX_MESA_agp_offset, 1016 /* glXGetAGPOffsetMESA */);
}

static PFNGLXGETCLIENTSTRINGPROC
epoxy_glXGetClientString_resolver(void)
{
    return glx_single_resolver(PROVIDER_GLX_11, 1036 /* glXGetClientString */);
}

static PFNGLXGETCONFIGPROC
epoxy_glXGetConfig_resolver(void)
{
    return glx_single_resolver(PROVIDER_GLX_10, 1055 /* glXGetConfig */);
}

static PFNGLXGETCONTEXTGPUIDAMDPROC
epoxy_glXGetContextGPUIDAMD_resolver(void)
{
    return glx_single_resolver(PROVIDER_GLX_AMD_gpu_association, 1068 /* glXGetContextGPUIDAMD */);
}

static PFNGLXGETCONTEXTIDEXTPROC
epoxy_glXGetContextIDEXT_resolver(void)
{
    return glx_single_resolver(PROVIDER_GLX_EXT_import_context, 1090 /* glXGetContextIDEXT */);
}

static PFNGLXGETCURRENTASSOCIATEDCONTEXTAMDPROC
epoxy_glXGetCurrentAssociatedContextAMD_resolver(void)
{
    return glx_single_resolver(PROVIDER_GLX_AMD_gpu_association, 1109 /* glXGetCurrentAssociatedContextAMD */);
}

static PFNGLXGETCURRENTCONTEXTPROC
epoxy_glXGetCurrentContext_resolver(void)
{
    return glx_single_resolver(PROVIDER_GLX_10, 1143 /* glXGetCurrentContext */);
}

static PFNGLXGETCURRENTDISPLAYPROC
epoxy_glXGetCurrentDisplay_resolver(void)
{
    return glx_single_resolver(PROVIDER_GLX_12, 1164 /* glXGetCurrentDisplay */);
}

static PFNGLXGETCURRENTDISPLAYEXTPROC
epoxy_glXGetCurrentDisplayEXT_resolver(void)
{
    return glx_single_resolver(PROVIDER_GLX_EXT_import_context, 1185 /* glXGetCurrentDisplayEXT */);
}

static PFNGLXGETCURRENTDRAWABLEPROC
epoxy_glXGetCurrentDrawable_resolver(void)
{
    return glx_single_resolver(PROVIDER_GLX_10, 1209 /* glXGetCurrentDrawable */);
}

static PFNGLXGETCURRENTREADDRAWABLEPROC
epoxy_glXGetCurrentReadDrawable_resolver(void)
{
    return glx_single_resolver(PROVIDER_GLX_13, 1231 /* glXGetCurrentReadDrawable */);
}

static PFNGLXGETCURRENTREADDRAWABLESGIPROC
epoxy_glXGetCurrentReadDrawableSGI_resolver(void)
{
    return glx_single_resolver(PROVIDER_GLX_SGI_make_current_read, 1257 /* glXGetCurrentReadDrawableSGI */);
}

static PFNGLXGETFBCONFIGATTRIBPROC
epoxy_glXGetFBConfigAttrib_resolver(void)
{
    return glx_single_resolver(PROVIDER_GLX_13, 1286 /* glXGetFBConfigAttrib */);
}

static PFNGLXGETFBCONFIGATTRIBSGIXPROC
epoxy_glXGetFBConfigAttribSGIX_resolver(void)
{
    return glx_single_resolver(PROVIDER_GLX_SGIX_fbconfig, 1307 /* glXGetFBConfigAttribSGIX */);
}

static PFNGLXGETFBCONFIGFROMVISUALSGIXPROC
epoxy_glXGetFBConfigFromVisualSGIX_resolver(void)
{
    return glx_single_resolver(PROVIDER_GLX_SGIX_fbconfig, 1332 /* glXGetFBConfigFromVisualSGIX */);
}

static PFNGLXGETFBCONFIGSPROC
epoxy_glXGetFBConfigs_resolver(void)
{
    return glx_single_resolver(PROVIDER_GLX_13, 1361 /* glXGetFBConfigs */);
}

static PFNGLXGETGPUIDSAMDPROC
epoxy_glXGetGPUIDsAMD_resolver(void)
{
    return glx_single_resolver(PROVIDER_GLX_AMD_gpu_association, 1377 /* glXGetGPUIDsAMD */);
}

static PFNGLXGETGPUINFOAMDPROC
epoxy_glXGetGPUInfoAMD_resolver(void)
{
    return glx_single_resolver(PROVIDER_GLX_AMD_gpu_association, 1393 /* glXGetGPUInfoAMD */);
}

static PFNGLXGETMSCRATEOMLPROC
epoxy_glXGetMscRateOML_resolver(void)
{
    return glx_single_resolver(PROVIDER_GLX_OML_sync_control, 1410 /* glXGetMscRateOML */);
}

static PFNGLXGETPROCADDRESSPROC
epoxy_glXGetProcAddress_resolver(void)
{
    return glx_single_resolver(PROVIDER_always_present, 1427 /* glXGetProcAddress */);
}

static PFNGLXGETPROCADDRESSARBPROC
epoxy_glXGetProcAddressARB_resolver(void)
{
    return glx_single_resolver(PROVIDER_GLX_ARB_get_proc_address, 1445 /* glXGetProcAddressARB */);
}

static PFNGLXGETSELECTEDEVENTPROC
epoxy_glXGetSelectedEvent_resolver(void)
{
    return glx_single_resolver(PROVIDER_GLX_13, 1466 /* glXGetSelectedEvent */);
}

static PFNGLXGETSELECTEDEVENTSGIXPROC
epoxy_glXGetSelectedEventSGIX_resolver(void)
{
    return glx_single_resolver(PROVIDER_GLX_SGIX_pbuffer, 1486 /* glXGetSelectedEventSGIX */);
}

static PFNGLXGETSWAPINTERVALMESAPROC
epoxy_glXGetSwapIntervalMESA_resolver(void)
{
    return glx_single_resolver(PROVIDER_GLX_MESA_swap_control, 1510 /* glXGetSwapIntervalMESA */);
}

static PFNGLXGETSYNCVALUESOMLPROC
epoxy_glXGetSyncValuesOML_resolver(void)
{
    return glx_single_resolver(PROVIDER_GLX_OML_sync_control, 1533 /* glXGetSyncValuesOML */);
}

static PFNGLXGETTRANSPARENTINDEXSUNPROC
epoxy_glXGetTransparentIndexSUN_resolver(void)
{
    return glx_single_resolver(PROVIDER_GLX_SUN_get_transparent_index, 1553 /* glXGetTransparentIndexSUN */);
}

static PFNGLXGETVIDEODEVICENVPROC
epoxy_glXGetVideoDeviceNV_resolver(void)
{
    return glx_single_resolver(PROVIDER_GLX_NV_video_out, 1579 /* glXGetVideoDeviceNV */);
}

static PFNGLXGETVIDEOINFONVPROC
epoxy_glXGetVideoInfoNV_resolver(void)
{
    return glx_single_resolver(PROVIDER_GLX_NV_video_out, 1599 /* glXGetVideoInfoNV */);
}

static PFNGLXGETVIDEOSYNCSGIPROC
epoxy_glXGetVideoSyncSGI_resolver(void)
{
    return glx_single_resolver(PROVIDER_GLX_SGI_video_sync, 1617 /* glXGetVideoSyncSGI */);
}

static PFNGLXGETVISUALFROMFBCONFIGPROC
epoxy_glXGetVisualFromFBConfig_resolver(void)
{
    return glx_single_resolver(PROVIDER_GLX_13, 1636 /* glXGetVisualFromFBConfig */);
}

static PFNGLXGETVISUALFROMFBCONFIGSGIXPROC
epoxy_glXGetVisualFromFBConfigSGIX_resolver(void)
{
    return glx_single_resolver(PROVIDER_GLX_SGIX_fbconfig, 1661 /* glXGetVisualFromFBConfigSGIX */);
}

static PFNGLXHYPERPIPEATTRIBSGIXPROC
epoxy_glXHyperpipeAttribSGIX_resolver(void)
{
    return glx_single_resolver(PROVIDER_GLX_SGIX_hyperpipe, 1690 /* glXHyperpipeAttribSGIX */);
}

static PFNGLXHYPERPIPECONFIGSGIXPROC
epoxy_glXHyperpipeConfigSGIX_resolver(void)
{
    return glx_single_resolver(PROVIDER_GLX_SGIX_hyperpipe, 1713 /* glXHyperpipeConfigSGIX */);
}

static PFNGLXIMPORTCONTEXTEXTPROC
epoxy_glXImportContextEXT_resolver(void)
{
    return glx_single_resolver(PROVIDER_GLX_EXT_import_context, 1736 /* glXImportContextEXT */);
}

static PFNGLXISDIRECTPROC
epoxy_glXIsDirect_resolver(void)
{
    return glx_single_resolver(PROVIDER_GLX_10, 1756 /* glXIsDirect */);
}

static PFNGLXJOINSWAPGROUPNVPROC
epoxy_glXJoinSwapGroupNV_resolver(void)
{
    return glx_single_resolver(PROVIDER_GLX_NV_swap_group, 1768 /* glXJoinSwapGroupNV */);
}

static PFNGLXJOINSWAPGROUPSGIXPROC
epoxy_glXJoinSwapGroupSGIX_resolver(void)
{
    return glx_single_resolver(PROVIDER_GLX_SGIX_swap_group, 1787 /* glXJoinSwapGroupSGIX */);
}

static PFNGLXLOCKVIDEOCAPTUREDEVICENVPROC
epoxy_glXLockVideoCaptureDeviceNV_resolver(void)
{
    return glx_single_resolver(PROVIDER_GLX_NV_video_capture, 1808 /* glXLockVideoCaptureDeviceNV */);
}

static PFNGLXMAKEASSOCIATEDCONTEXTCURRENTAMDPROC
epoxy_glXMakeAssociatedContextCurrentAMD_resolver(void)
{
    return glx_single_resolver(PROVIDER_GLX_AMD_gpu_association, 1836 /* glXMakeAssociatedContextCurrentAMD */);
}

static PFNGLXMAKECONTEXTCURRENTPROC
epoxy_glXMakeContextCurrent_resolver(void)
{
    return glx_single_resolver(PROVIDER_GLX_13, 1871 /* glXMakeContextCurrent */);
}

static PFNGLXMAKECURRENTPROC
epoxy_glXMakeCurrent_resolver(void)
{
    return glx_single_resolver(PROVIDER_GLX_10, 1893 /* glXMakeCurrent */);
}

static PFNGLXMAKECURRENTREADSGIPROC
epoxy_glXMakeCurrentReadSGI_resolver(void)
{
    return glx_single_resolver(PROVIDER_GLX_SGI_make_current_read, 1908 /* glXMakeCurrentReadSGI */);
}

static PFNGLXNAMEDCOPYBUFFERSUBDATANVPROC
epoxy_glXNamedCopyBufferSubDataNV_resolver(void)
{
    return glx_single_resolver(PROVIDER_GLX_NV_copy_buffer, 1930 /* glXNamedCopyBufferSubDataNV */);
}

static PFNGLXQUERYCHANNELDELTASSGIXPROC
epoxy_glXQueryChannelDeltasSGIX_resolver(void)
{
    return glx_single_resolver(PROVIDER_GLX_SGIX_video_resize, 1958 /* glXQueryChannelDeltasSGIX */);
}

static PFNGLXQUERYCHANNELRECTSGIXPROC
epoxy_glXQueryChannelRectSGIX_resolver(void)
{
    return glx_single_resolver(PROVIDER_GLX_SGIX_video_resize, 1984 /* glXQueryChannelRectSGIX */);
}

static PFNGLXQUERYCONTEXTPROC
epoxy_glXQueryContext_resolver(void)
{
    return glx_single_resolver(PROVIDER_GLX_13, 2008 /* glXQueryContext */);
}

static PFNGLXQUERYCONTEXTINFOEXTPROC
epoxy_glXQueryContextInfoEXT_resolver(void)
{
    return glx_single_resolver(PROVIDER_GLX_EXT_import_context, 2024 /* glXQueryContextInfoEXT */);
}

static PFNGLXQUERYCURRENTRENDERERINTEGERMESAPROC
epoxy_glXQueryCurrentRendererIntegerMESA_resolver(void)
{
    return glx_single_resolver(PROVIDER_GLX_MESA_query_renderer, 2047 /* glXQueryCurrentRendererIntegerMESA */);
}

static PFNGLXQUERYCURRENTRENDERERSTRINGMESAPROC
epoxy_glXQueryCurrentRendererStringMESA_resolver(void)
{
    return glx_single_resolver(PROVIDER_GLX_MESA_query_renderer, 2082 /* glXQueryCurrentRendererStringMESA */);
}

static PFNGLXQUERYDRAWABLEPROC
epoxy_glXQueryDrawable_resolver(void)
{
    return glx_single_resolver(PROVIDER_GLX_13, 2116 /* glXQueryDrawable */);
}

static PFNGLXQUERYEXTENSIONPROC
epoxy_glXQueryExtension_resolver(void)
{
    return glx_single_resolver(PROVIDER_GLX_10, 2133 /* glXQueryExtension */);
}

static PFNGLXQUERYEXTENSIONSSTRINGPROC
epoxy_glXQueryExtensionsString_resolver(void)
{
    return glx_single_resolver(PROVIDER_GLX_11, 2151 /* glXQueryExtensionsString */);
}

static PFNGLXQUERYFRAMECOUNTNVPROC
epoxy_glXQueryFrameCountNV_resolver(void)
{
    return glx_single_resolver(PROVIDER_GLX_NV_swap_group, 2176 /* glXQueryFrameCountNV */);
}

static PFNGLXQUERYGLXPBUFFERSGIXPROC
epoxy_glXQueryGLXPbufferSGIX_resolver(void)
{
    return glx_single_resolver(PROVIDER_GLX_SGIX_pbuffer, 2197 /* glXQueryGLXPbufferSGIX */);
}

static PFNGLXQUERYHYPERPIPEATTRIBSGIXPROC
epoxy_glXQueryHyperpipeAttribSGIX_resolver(void)
{
    return glx_single_resolver(PROVIDER_GLX_SGIX_hyperpipe, 2220 /* glXQueryHyperpipeAttribSGIX */);
}

static PFNGLXQUERYHYPERPIPEBESTATTRIBSGIXPROC
epoxy_glXQueryHyperpipeBestAttribSGIX_resolver(void)
{
    return glx_single_resolver(PROVIDER_GLX_SGIX_hyperpipe, 2248 /* glXQueryHyperpipeBestAttribSGIX */);
}

static PFNGLXQUERYHYPERPIPECONFIGSGIXPROC
epoxy_glXQueryHyperpipeConfigSGIX_resolver(void)
{
    return glx_single_resolver(PROVIDER_GLX_SGIX_hyperpipe, 2280 /* glXQueryHyperpipeConfigSGIX */);
}

static PFNGLXQUERYHYPERPIPENETWORKSGIXPROC
epoxy_glXQueryHyperpipeNetworkSGIX_resolver(void)
{
    return glx_single_resolver(PROVIDER_GLX_SGIX_hyperpipe, 2308 /* glXQueryHyperpipeNetworkSGIX */);
}

static PFNGLXQUERYMAXSWAPBARRIERSSGIXPROC
epoxy_glXQueryMaxSwapBarriersSGIX_resolver(void)
{
    return glx_single_resolver(PROVIDER_GLX_SGIX_swap_barrier, 2337 /* glXQueryMaxSwapBarriersSGIX */);
}

static PFNGLXQUERYMAXSWAPGROUPSNVPROC
epoxy_glXQueryMaxSwapGroupsNV_resolver(void)
{
    return glx_single_resolver(PROVIDER_GLX_NV_swap_group, 2365 /* glXQueryMaxSwapGroupsNV */);
}

static PFNGLXQUERYRENDERERINTEGERMESAPROC
epoxy_glXQueryRendererIntegerMESA_resolver(void)
{
    return glx_single_resolver(PROVIDER_GLX_MESA_query_renderer, 2389 /* glXQueryRendererIntegerMESA */);
}

static PFNGLXQUERYRENDERERSTRINGMESAPROC
epoxy_glXQueryRendererStringMESA_resolver(void)
{
    return glx_single_resolver(PROVIDER_GLX_MESA_query_renderer, 2417 /* glXQueryRendererStringMESA */);
}

static PFNGLXQUERYSERVERSTRINGPROC
epoxy_glXQueryServerString_resolver(void)
{
    return glx_single_resolver(PROVIDER_GLX_11, 2444 /* glXQueryServerString */);
}

static PFNGLXQUERYSWAPGROUPNVPROC
epoxy_glXQuerySwapGroupNV_resolver(void)
{
    return glx_single_resolver(PROVIDER_GLX_NV_swap_group, 2465 /* glXQuerySwapGroupNV */);
}

static PFNGLXQUERYVERSIONPROC
epoxy_glXQueryVersion_resolver(void)
{
    return glx_single_resolver(PROVIDER_GLX_10, 2485 /* glXQueryVersion */);
}

static PFNGLXQUERYVIDEOCAPTUREDEVICENVPROC
epoxy_glXQueryVideoCaptureDeviceNV_resolver(void)
{
    return glx_single_resolver(PROVIDER_GLX_NV_video_capture, 2501 /* glXQueryVideoCaptureDeviceNV */);
}

static PFNGLXRELEASEBUFFERSMESAPROC
epoxy_glXReleaseBuffersMESA_resolver(void)
{
    return glx_single_resolver(PROVIDER_GLX_MESA_release_buffers, 2530 /* glXReleaseBuffersMESA */);
}

static PFNGLXRELEASETEXIMAGEEXTPROC
epoxy_glXReleaseTexImageEXT_resolver(void)
{
    return glx_single_resolver(PROVIDER_GLX_EXT_texture_from_pixmap, 2552 /* glXReleaseTexImageEXT */);
}

static PFNGLXRELEASEVIDEOCAPTUREDEVICENVPROC
epoxy_glXReleaseVideoCaptureDeviceNV_resolver(void)
{
    return glx_single_resolver(PROVIDER_GLX_NV_video_capture, 2574 /* glXReleaseVideoCaptureDeviceNV */);
}

static PFNGLXRELEASEVIDEODEVICENVPROC
epoxy_glXReleaseVideoDeviceNV_resolver(void)
{
    return glx_single_resolver(PROVIDER_GLX_NV_video_out, 2605 /* glXReleaseVideoDeviceNV */);
}

static PFNGLXRELEASEVIDEOIMAGENVPROC
epoxy_glXReleaseVideoImageNV_resolver(void)
{
    return glx_single_resolver(PROVIDER_GLX_NV_video_out, 2629 /* glXReleaseVideoImageNV */);
}

static PFNGLXRESETFRAMECOUNTNVPROC
epoxy_glXResetFrameCountNV_resolver(void)
{
    return glx_single_resolver(PROVIDER_GLX_NV_swap_group, 2652 /* glXResetFrameCountNV */);
}

static PFNGLXSELECTEVENTPROC
epoxy_glXSelectEvent_resolver(void)
{
    return glx_single_resolver(PROVIDER_GLX_13, 2673 /* glXSelectEvent */);
}

static PFNGLXSELECTEVENTSGIXPROC
epoxy_glXSelectEventSGIX_resolver(void)
{
    return glx_single_resolver(PROVIDER_GLX_SGIX_pbuffer, 2688 /* glXSelectEventSGIX */);
}

static PFNGLXSENDPBUFFERTOVIDEONVPROC
epoxy_glXSendPbufferToVideoNV_resolver(void)
{
    return glx_single_resolver(PROVIDER_GLX_NV_video_out, 2707 /* glXSendPbufferToVideoNV */);
}

static PFNGLXSET3DFXMODEMESAPROC
epoxy_glXSet3DfxModeMESA_resolver(void)
{
    return glx_single_resolver(PROVIDER_GLX_MESA_set_3dfx_mode, 2731 /* glXSet3DfxModeMESA */);
}

static PFNGLXSWAPBUFFERSPROC
epoxy_glXSwapBuffers_resolver(void)
{
    return glx_single_resolver(PROVIDER_GLX_10, 2750 /* glXSwapBuffers */);
}

static PFNGLXSWAPBUFFERSMSCOMLPROC
epoxy_glXSwapBuffersMscOML_resolver(void)
{
    return glx_single_resolver(PROVIDER_GLX_OML_sync_control, 2765 /* glXSwapBuffersMscOML */);
}

static PFNGLXSWAPINTERVALEXTPROC
epoxy_glXSwapIntervalEXT_resolver(void)
{
    return glx_single_resolver(PROVIDER_GLX_EXT_swap_control, 2786 /* glXSwapIntervalEXT */);
}

static PFNGLXSWAPINTERVALMESAPROC
epoxy_glXSwapIntervalMESA_resolver(void)
{
    return glx_single_resolver(PROVIDER_GLX_MESA_swap_control, 2805 /* glXSwapIntervalMESA */);
}

static PFNGLXSWAPINTERVALSGIPROC
epoxy_glXSwapIntervalSGI_resolver(void)
{
    return glx_single_resolver(PROVIDER_GLX_SGI_swap_control, 2825 /* glXSwapIntervalSGI */);
}

static PFNGLXUSEXFONTPROC
epoxy_glXUseXFont_resolver(void)
{
    return glx_single_resolver(PROVIDER_GLX_10, 2844 /* glXUseXFont */);
}

static PFNGLXWAITFORMSCOMLPROC
epoxy_glXWaitForMscOML_resolver(void)
{
    return glx_single_resolver(PROVIDER_GLX_OML_sync_control, 2856 /* glXWaitForMscOML */);
}

static PFNGLXWAITFORSBCOMLPROC
epoxy_glXWaitForSbcOML_resolver(void)
{
    return glx_single_resolver(PROVIDER_GLX_OML_sync_control, 2873 /* glXWaitForSbcOML */);
}

static PFNGLXWAITGLPROC
epoxy_glXWaitGL_resolver(void)
{
    return glx_single_resolver(PROVIDER_GLX_10, 2890 /* glXWaitGL */);
}

static PFNGLXWAITVIDEOSYNCSGIPROC
epoxy_glXWaitVideoSyncSGI_resolver(void)
{
    return glx_single_resolver(PROVIDER_GLX_SGI_video_sync, 2900 /* glXWaitVideoSyncSGI */);
}

static PFNGLXWAITXPROC
epoxy_glXWaitX_resolver(void)
{
    return glx_single_resolver(PROVIDER_GLX_10, 2920 /* glXWaitX */);
}

GEN_THUNKS_RET(int, glXBindChannelToWindowSGIX, (Display * display, int screen, int channel, Window window), (display, screen, channel, window))
GEN_THUNKS_RET(int, glXBindHyperpipeSGIX, (Display * dpy, int hpId), (dpy, hpId))
GEN_THUNKS_RET(Bool, glXBindSwapBarrierNV, (Display * dpy, GLuint group, GLuint barrier), (dpy, group, barrier))
GEN_THUNKS(glXBindSwapBarrierSGIX, (Display * dpy, GLXDrawable drawable, int barrier), (dpy, drawable, barrier))
GEN_THUNKS(glXBindTexImageEXT, (Display * dpy, GLXDrawable drawable, int buffer, const int * attrib_list), (dpy, drawable, buffer, attrib_list))
GEN_THUNKS_RET(int, glXBindVideoCaptureDeviceNV, (Display * dpy, unsigned int video_capture_slot, GLXVideoCaptureDeviceNV device), (dpy, video_capture_slot, device))
GEN_THUNKS_RET(int, glXBindVideoDeviceNV, (Display * dpy, unsigned int video_slot, unsigned int video_device, const int * attrib_list), (dpy, video_slot, video_device, attrib_list))
GEN_THUNKS_RET(int, glXBindVideoImageNV, (Display * dpy, GLXVideoDeviceNV VideoDevice, GLXPbuffer pbuf, int iVideoBuffer), (dpy, VideoDevice, pbuf, iVideoBuffer))
GEN_THUNKS(glXBlitContextFramebufferAMD, (GLXContext dstCtx, GLint srcX0, GLint srcY0, GLint srcX1, GLint srcY1, GLint dstX0, GLint dstY0, GLint dstX1, GLint dstY1, GLbitfield mask, GLenum filter), (dstCtx, srcX0, srcY0, srcX1, srcY1, dstX0, dstY0, dstX1, dstY1, mask, filter))
GEN_THUNKS_RET(int, glXChannelRectSGIX, (Display * display, int screen, int channel, int x, int y, int w, int h), (display, screen, channel, x, y, w, h))
GEN_THUNKS_RET(int, glXChannelRectSyncSGIX, (Display * display, int screen, int channel, GLenum synctype), (display, screen, channel, synctype))
GEN_THUNKS_RET(GLXFBConfig *, glXChooseFBConfig, (Display * dpy, int screen, const int * attrib_list, int * nelements), (dpy, screen, attrib_list, nelements))
GEN_THUNKS_RET(GLXFBConfigSGIX *, glXChooseFBConfigSGIX, (Display * dpy, int screen, int * attrib_list, int * nelements), (dpy, screen, attrib_list, nelements))
GEN_THUNKS_RET(XVisualInfo *, glXChooseVisual, (Display * dpy, int screen, int * attribList), (dpy, screen, attribList))
GEN_THUNKS(glXCopyBufferSubDataNV, (Display * dpy, GLXContext readCtx, GLXContext writeCtx, GLenum readTarget, GLenum writeTarget, GLintptr readOffset, GLintptr writeOffset, GLsizeiptr size), (dpy, readCtx, writeCtx, readTarget, writeTarget, readOffset, writeOffset, size))
GEN_THUNKS(glXCopyContext, (Display * dpy, GLXContext src, GLXContext dst, unsigned long mask), (dpy, src, dst, mask))
GEN_THUNKS(glXCopyImageSubDataNV, (Display * dpy, GLXContext srcCtx, GLuint srcName, GLenum srcTarget, GLint srcLevel, GLint srcX, GLint srcY, GLint srcZ, GLXContext dstCtx, GLuint dstName, GLenum dstTarget, GLint dstLevel, GLint dstX, GLint dstY, GLint dstZ, GLsizei width, GLsizei height, GLsizei depth), (dpy, srcCtx, srcName, srcTarget, srcLevel, srcX, srcY, srcZ, dstCtx, dstName, dstTarget, dstLevel, dstX, dstY, dstZ, width, height, depth))
GEN_THUNKS(glXCopySubBufferMESA, (Display * dpy, GLXDrawable drawable, int x, int y, int width, int height), (dpy, drawable, x, y, width, height))
GEN_THUNKS_RET(GLXContext, glXCreateAssociatedContextAMD, (unsigned int id, GLXContext share_list), (id, share_list))
GEN_THUNKS_RET(GLXContext, glXCreateAssociatedContextAttribsAMD, (unsigned int id, GLXContext share_context, const int * attribList), (id, share_context, attribList))
GEN_THUNKS_RET(GLXContext, glXCreateContext, (Display * dpy, XVisualInfo * vis, GLXContext shareList, Bool direct), (dpy, vis, shareList, direct))
GEN_THUNKS_RET(GLXContext, glXCreateContextAttribsARB, (Display * dpy, GLXFBConfig config, GLXContext share_context, Bool direct, const int * attrib_list), (dpy, config, share_context, direct, attrib_list))
GEN_THUNKS_RET(GLXContext, glXCreateContextWithConfigSGIX, (Display * dpy, GLXFBConfigSGIX config, int render_type, GLXContext share_list, Bool direct), (dpy, config, render_type, share_list, direct))
GEN_THUNKS_RET(GLXPbufferSGIX, glXCreateGLXPbufferSGIX, (Display * dpy, GLXFBConfigSGIX config, unsigned int width, unsigned int height, int * attrib_list), (dpy, config, width, height, attrib_list))
GEN_THUNKS_RET(GLXPixmap, glXCreateGLXPixmap, (Display * dpy, XVisualInfo * visual, Pixmap pixmap), (dpy, visual, pixmap))
GEN_THUNKS_RET(GLXPixmap, glXCreateGLXPixmapMESA, (Display * dpy, XVisualInfo * visual, Pixmap pixmap, Colormap cmap), (dpy, visual, pixmap, cmap))
GEN_THUNKS_RET(GLXPixmap, glXCreateGLXPixmapWithConfigSGIX, (Display * dpy, GLXFBConfigSGIX config, Pixmap pixmap), (dpy, config, pixmap))
GEN_THUNKS_RET(GLXContext, glXCreateNewContext, (Display * dpy, GLXFBConfig config, int render_type, GLXContext share_list, Bool direct), (dpy, config, render_type, share_list, direct))
GEN_THUNKS_RET(GLXPbuffer, glXCreatePbuffer, (Display * dpy, GLXFBConfig config, const int * attrib_list), (dpy, config, attrib_list))
GEN_THUNKS_RET(GLXPixmap, glXCreatePixmap, (Display * dpy, GLXFBConfig config, Pixmap pixmap, const int * attrib_list), (dpy, config, pixmap, attrib_list))
GEN_THUNKS_RET(GLXWindow, glXCreateWindow, (Display * dpy, GLXFBConfig config, Window win, const int * attrib_list), (dpy, config, win, attrib_list))
GEN_THUNKS(glXCushionSGI, (Display * dpy, Window window, float cushion), (dpy, window, cushion))
GEN_THUNKS_RET(Bool, glXDelayBeforeSwapNV, (Display * dpy, GLXDrawable drawable, GLfloat seconds), (dpy, drawable, seconds))
GEN_THUNKS_RET(Bool, glXDeleteAssociatedContextAMD, (GLXContext ctx), (ctx))
GEN_THUNKS(glXDestroyContext, (Display * dpy, GLXContext ctx), (dpy, ctx))
GEN_THUNKS(glXDestroyGLXPbufferSGIX, (Display * dpy, GLXPbufferSGIX pbuf), (dpy, pbuf))
GEN_THUNKS(glXDestroyGLXPixmap, (Display * dpy, GLXPixmap pixmap), (dpy, pixmap))
GEN_THUNKS(glXDestroyGLXVideoSourceSGIX, (Display * dpy, GLXVideoSourceSGIX glxvideosource), (dpy, glxvideosource))
GEN_THUNKS_RET(int, glXDestroyHyperpipeConfigSGIX, (Display * dpy, int hpId), (dpy, hpId))
GEN_THUNKS(glXDestroyPbuffer, (Display * dpy, GLXPbuffer pbuf), (dpy, pbuf))
GEN_THUNKS(glXDestroyPixmap, (Display * dpy, GLXPixmap pixmap), (dpy, pixmap))
GEN_THUNKS(glXDestroyWindow, (Display * dpy, GLXWindow win), (dpy, win))
GEN_THUNKS_RET(GLXVideoCaptureDeviceNV *, glXEnumerateVideoCaptureDevicesNV, (Display * dpy, int screen, int * nelements), (dpy, screen, nelements))
GEN_THUNKS_RET(unsigned int *, glXEnumerateVideoDevicesNV, (Display * dpy, int screen, int * nelements), (dpy, screen, nelements))
GEN_THUNKS(glXFreeContextEXT, (Display * dpy, GLXContext context), (dpy, context))
GEN_THUNKS_RET(unsigned int, glXGetAGPOffsetMESA, (const void * pointer), (pointer))
GEN_THUNKS_RET(const char *, glXGetClientString, (Display * dpy, int name), (dpy, name))
GEN_THUNKS_RET(int, glXGetConfig, (Display * dpy, XVisualInfo * visual, int attrib, int * value), (dpy, visual, attrib, value))
GEN_THUNKS_RET(unsigned int, glXGetContextGPUIDAMD, (GLXContext ctx), (ctx))
GEN_THUNKS_RET(GLXContextID, glXGetContextIDEXT, (const GLXContext context), (context))
GEN_THUNKS_RET(GLXContext, glXGetCurrentAssociatedContextAMD, (void), ())
GEN_THUNKS_RET(GLXContext, glXGetCurrentContext, (void), ())
GEN_THUNKS_RET(Display *, glXGetCurrentDisplay, (void), ())
GEN_THUNKS_RET(Display *, glXGetCurrentDisplayEXT, (void), ())
GEN_THUNKS_RET(GLXDrawable, glXGetCurrentDrawable, (void), ())
GEN_THUNKS_RET(GLXDrawable, glXGetCurrentReadDrawable, (void), ())
GEN_THUNKS_RET(GLXDrawable, glXGetCurrentReadDrawableSGI, (void), ())
GEN_THUNKS_RET(int, glXGetFBConfigAttrib, (Display * dpy, GLXFBConfig config, int attribute, int * value), (dpy, config, attribute, value))
GEN_THUNKS_RET(int, glXGetFBConfigAttribSGIX, (Display * dpy, GLXFBConfigSGIX config, int attribute, int * value), (dpy, config, attribute, value))
GEN_THUNKS_RET(GLXFBConfigSGIX, glXGetFBConfigFromVisualSGIX, (Display * dpy, XVisualInfo * vis), (dpy, vis))
GEN_THUNKS_RET(GLXFBConfig *, glXGetFBConfigs, (Display * dpy, int screen, int * nelements), (dpy, screen, nelements))
GEN_THUNKS_RET(unsigned int, glXGetGPUIDsAMD, (unsigned int maxCount, unsigned int * ids), (maxCount, ids))
GEN_THUNKS_RET(int, glXGetGPUInfoAMD, (unsigned int id, int property, GLenum dataType, unsigned int size, void * data), (id, property, dataType, size, data))
GEN_THUNKS_RET(Bool, glXGetMscRateOML, (Display * dpy, GLXDrawable drawable, int32_t * numerator, int32_t * denominator), (dpy, drawable, numerator, denominator))
GEN_THUNKS_RET(__GLXextFuncPtr, glXGetProcAddress, (const GLubyte * procName), (procName))
GEN_THUNKS_RET(__GLXextFuncPtr, glXGetProcAddressARB, (const GLubyte * procName), (procName))
GEN_THUNKS(glXGetSelectedEvent, (Display * dpy, GLXDrawable draw, unsigned long * event_mask), (dpy, draw, event_mask))
GEN_THUNKS(glXGetSelectedEventSGIX, (Display * dpy, GLXDrawable drawable, unsigned long * mask), (dpy, drawable, mask))
GEN_THUNKS_RET(int, glXGetSwapIntervalMESA, (void), ())
GEN_THUNKS_RET(Bool, glXGetSyncValuesOML, (Display * dpy, GLXDrawable drawable, int64_t * ust, int64_t * msc, int64_t * sbc), (dpy, drawable, ust, msc, sbc))
GEN_THUNKS_RET(Status, glXGetTransparentIndexSUN, (Display * dpy, Window overlay, Window underlay, unsigned long * pTransparentIndex), (dpy, overlay, underlay, pTransparentIndex))
GEN_THUNKS_RET(int, glXGetVideoDeviceNV, (Display * dpy, int screen, int numVideoDevices, GLXVideoDeviceNV * pVideoDevice), (dpy, screen, numVideoDevices, pVideoDevice))
GEN_THUNKS_RET(int, glXGetVideoInfoNV, (Display * dpy, int screen, GLXVideoDeviceNV VideoDevice, unsigned long * pulCounterOutputPbuffer, unsigned long * pulCounterOutputVideo), (dpy, screen, VideoDevice, pulCounterOutputPbuffer, pulCounterOutputVideo))
GEN_THUNKS_RET(int, glXGetVideoSyncSGI, (unsigned int * count), (count))
GEN_THUNKS_RET(XVisualInfo *, glXGetVisualFromFBConfig, (Display * dpy, GLXFBConfig config), (dpy, config))
GEN_THUNKS_RET(XVisualInfo *, glXGetVisualFromFBConfigSGIX, (Display * dpy, GLXFBConfigSGIX config), (dpy, config))
GEN_THUNKS_RET(int, glXHyperpipeAttribSGIX, (Display * dpy, int timeSlice, int attrib, int size, void * attribList), (dpy, timeSlice, attrib, size, attribList))
GEN_THUNKS_RET(int, glXHyperpipeConfigSGIX, (Display * dpy, int networkId, int npipes, GLXHyperpipeConfigSGIX * cfg, int * hpId), (dpy, networkId, npipes, cfg, hpId))
GEN_THUNKS_RET(GLXContext, glXImportContextEXT, (Display * dpy, GLXContextID contextID), (dpy, contextID))
GEN_THUNKS_RET(Bool, glXIsDirect, (Display * dpy, GLXContext ctx), (dpy, ctx))
GEN_THUNKS_RET(Bool, glXJoinSwapGroupNV, (Display * dpy, GLXDrawable drawable, GLuint group), (dpy, drawable, group))
GEN_THUNKS(glXJoinSwapGroupSGIX, (Display * dpy, GLXDrawable drawable, GLXDrawable member), (dpy, drawable, member))
GEN_THUNKS(glXLockVideoCaptureDeviceNV, (Display * dpy, GLXVideoCaptureDeviceNV device), (dpy, device))
GEN_THUNKS_RET(Bool, glXMakeAssociatedContextCurrentAMD, (GLXContext ctx), (ctx))
GEN_THUNKS_RET(Bool, glXMakeContextCurrent, (Display * dpy, GLXDrawable draw, GLXDrawable read, GLXContext ctx), (dpy, draw, read, ctx))
GEN_THUNKS_RET(Bool, glXMakeCurrent, (Display * dpy, GLXDrawable drawable, GLXContext ctx), (dpy, drawable, ctx))
GEN_THUNKS_RET(Bool, glXMakeCurrentReadSGI, (Display * dpy, GLXDrawable draw, GLXDrawable read, GLXContext ctx), (dpy, draw, read, ctx))
GEN_THUNKS(glXNamedCopyBufferSubDataNV, (Display * dpy, GLXContext readCtx, GLXContext writeCtx, GLuint readBuffer, GLuint writeBuffer, GLintptr readOffset, GLintptr writeOffset, GLsizeiptr size), (dpy, readCtx, writeCtx, readBuffer, writeBuffer, readOffset, writeOffset, size))
GEN_THUNKS_RET(int, glXQueryChannelDeltasSGIX, (Display * display, int screen, int channel, int * x, int * y, int * w, int * h), (display, screen, channel, x, y, w, h))
GEN_THUNKS_RET(int, glXQueryChannelRectSGIX, (Display * display, int screen, int channel, int * dx, int * dy, int * dw, int * dh), (display, screen, channel, dx, dy, dw, dh))
GEN_THUNKS_RET(int, glXQueryContext, (Display * dpy, GLXContext ctx, int attribute, int * value), (dpy, ctx, attribute, value))
GEN_THUNKS_RET(int, glXQueryContextInfoEXT, (Display * dpy, GLXContext context, int attribute, int * value), (dpy, context, attribute, value))
GEN_THUNKS_RET(Bool, glXQueryCurrentRendererIntegerMESA, (int attribute, unsigned int * value), (attribute, value))
GEN_THUNKS_RET(const char *, glXQueryCurrentRendererStringMESA, (int attribute), (attribute))
GEN_THUNKS(glXQueryDrawable, (Display * dpy, GLXDrawable draw, int attribute, unsigned int * value), (dpy, draw, attribute, value))
GEN_THUNKS_RET(Bool, glXQueryExtension, (Display * dpy, int * errorb, int * event), (dpy, errorb, event))
GEN_THUNKS_RET(const char *, glXQueryExtensionsString, (Display * dpy, int screen), (dpy, screen))
GEN_THUNKS_RET(Bool, glXQueryFrameCountNV, (Display * dpy, int screen, GLuint * count), (dpy, screen, count))
GEN_THUNKS(glXQueryGLXPbufferSGIX, (Display * dpy, GLXPbufferSGIX pbuf, int attribute, unsigned int * value), (dpy, pbuf, attribute, value))
GEN_THUNKS_RET(int, glXQueryHyperpipeAttribSGIX, (Display * dpy, int timeSlice, int attrib, int size, void * returnAttribList), (dpy, timeSlice, attrib, size, returnAttribList))
GEN_THUNKS_RET(int, glXQueryHyperpipeBestAttribSGIX, (Display * dpy, int timeSlice, int attrib, int size, void * attribList, void * returnAttribList), (dpy, timeSlice, attrib, size, attribList, returnAttribList))
GEN_THUNKS_RET(GLXHyperpipeConfigSGIX *, glXQueryHyperpipeConfigSGIX, (Display * dpy, int hpId, int * npipes), (dpy, hpId, npipes))
GEN_THUNKS_RET(GLXHyperpipeNetworkSGIX *, glXQueryHyperpipeNetworkSGIX, (Display * dpy, int * npipes), (dpy, npipes))
GEN_THUNKS_RET(Bool, glXQueryMaxSwapBarriersSGIX, (Display * dpy, int screen, int * max), (dpy, screen, max))
GEN_THUNKS_RET(Bool, glXQueryMaxSwapGroupsNV, (Display * dpy, int screen, GLuint * maxGroups, GLuint * maxBarriers), (dpy, screen, maxGroups, maxBarriers))
GEN_THUNKS_RET(Bool, glXQueryRendererIntegerMESA, (Display * dpy, int screen, int renderer, int attribute, unsigned int * value), (dpy, screen, renderer, attribute, value))
GEN_THUNKS_RET(const char *, glXQueryRendererStringMESA, (Display * dpy, int screen, int renderer, int attribute), (dpy, screen, renderer, attribute))
GEN_THUNKS_RET(const char *, glXQueryServerString, (Display * dpy, int screen, int name), (dpy, screen, name))
GEN_THUNKS_RET(Bool, glXQuerySwapGroupNV, (Display * dpy, GLXDrawable drawable, GLuint * group, GLuint * barrier), (dpy, drawable, group, barrier))
GEN_THUNKS_RET(Bool, glXQueryVersion, (Display * dpy, int * maj, int * min), (dpy, maj, min))
GEN_THUNKS_RET(int, glXQueryVideoCaptureDeviceNV, (Display * dpy, GLXVideoCaptureDeviceNV device, int attribute, int * value), (dpy, device, attribute, value))
GEN_THUNKS_RET(Bool, glXReleaseBuffersMESA, (Display * dpy, GLXDrawable drawable), (dpy, drawable))
GEN_THUNKS(glXReleaseTexImageEXT, (Display * dpy, GLXDrawable drawable, int buffer), (dpy, drawable, buffer))
GEN_THUNKS(glXReleaseVideoCaptureDeviceNV, (Display * dpy, GLXVideoCaptureDeviceNV device), (dpy, device))
GEN_THUNKS_RET(int, glXReleaseVideoDeviceNV, (Display * dpy, int screen, GLXVideoDeviceNV VideoDevice), (dpy, screen, VideoDevice))
GEN_THUNKS_RET(int, glXReleaseVideoImageNV, (Display * dpy, GLXPbuffer pbuf), (dpy, pbuf))
GEN_THUNKS_RET(Bool, glXResetFrameCountNV, (Display * dpy, int screen), (dpy, screen))
GEN_THUNKS(glXSelectEvent, (Display * dpy, GLXDrawable draw, unsigned long event_mask), (dpy, draw, event_mask))
GEN_THUNKS(glXSelectEventSGIX, (Display * dpy, GLXDrawable drawable, unsigned long mask), (dpy, drawable, mask))
GEN_THUNKS_RET(int, glXSendPbufferToVideoNV, (Display * dpy, GLXPbuffer pbuf, int iBufferType, unsigned long * pulCounterPbuffer, GLboolean bBlock), (dpy, pbuf, iBufferType, pulCounterPbuffer, bBlock))
GEN_THUNKS_RET(GLboolean, glXSet3DfxModeMESA, (GLint mode), (mode))
GEN_THUNKS(glXSwapBuffers, (Display * dpy, GLXDrawable drawable), (dpy, drawable))
GEN_THUNKS_RET(int64_t, glXSwapBuffersMscOML, (Display * dpy, GLXDrawable drawable, int64_t target_msc, int64_t divisor, int64_t remainder), (dpy, drawable, target_msc, divisor, remainder))
GEN_THUNKS(glXSwapIntervalEXT, (Display * dpy, GLXDrawable drawable, int interval), (dpy, drawable, interval))
GEN_THUNKS_RET(int, glXSwapIntervalMESA, (unsigned int interval), (interval))
GEN_THUNKS_RET(int, glXSwapIntervalSGI, (int interval), (interval))
GEN_THUNKS(glXUseXFont, (Font font, int first, int count, int list), (font, first, count, list))
GEN_THUNKS_RET(Bool, glXWaitForMscOML, (Display * dpy, GLXDrawable drawable, int64_t target_msc, int64_t divisor, int64_t remainder, int64_t * ust, int64_t * msc, int64_t * sbc), (dpy, drawable, target_msc, divisor, remainder, ust, msc, sbc))
GEN_THUNKS_RET(Bool, glXWaitForSbcOML, (Display * dpy, GLXDrawable drawable, int64_t target_sbc, int64_t * ust, int64_t * msc, int64_t * sbc), (dpy, drawable, target_sbc, ust, msc, sbc))
GEN_THUNKS(glXWaitGL, (void), ())
GEN_THUNKS_RET(int, glXWaitVideoSyncSGI, (int divisor, int remainder, unsigned int * count), (divisor, remainder, count))
GEN_THUNKS(glXWaitX, (void), ())

#if USING_DISPATCH_TABLE
static struct dispatch_table resolver_table = {
    epoxy_glXBindChannelToWindowSGIX_dispatch_table_rewrite_ptr, /* glXBindChannelToWindowSGIX */
    epoxy_glXBindHyperpipeSGIX_dispatch_table_rewrite_ptr, /* glXBindHyperpipeSGIX */
    epoxy_glXBindSwapBarrierNV_dispatch_table_rewrite_ptr, /* glXBindSwapBarrierNV */
    epoxy_glXBindSwapBarrierSGIX_dispatch_table_rewrite_ptr, /* glXBindSwapBarrierSGIX */
    epoxy_glXBindTexImageEXT_dispatch_table_rewrite_ptr, /* glXBindTexImageEXT */
    epoxy_glXBindVideoCaptureDeviceNV_dispatch_table_rewrite_ptr, /* glXBindVideoCaptureDeviceNV */
    epoxy_glXBindVideoDeviceNV_dispatch_table_rewrite_ptr, /* glXBindVideoDeviceNV */
    epoxy_glXBindVideoImageNV_dispatch_table_rewrite_ptr, /* glXBindVideoImageNV */
    epoxy_glXBlitContextFramebufferAMD_dispatch_table_rewrite_ptr, /* glXBlitContextFramebufferAMD */
    epoxy_glXChannelRectSGIX_dispatch_table_rewrite_ptr, /* glXChannelRectSGIX */
    epoxy_glXChannelRectSyncSGIX_dispatch_table_rewrite_ptr, /* glXChannelRectSyncSGIX */
    epoxy_glXChooseFBConfig_dispatch_table_rewrite_ptr, /* glXChooseFBConfig */
    epoxy_glXChooseFBConfigSGIX_dispatch_table_rewrite_ptr, /* glXChooseFBConfigSGIX */
    epoxy_glXChooseVisual_dispatch_table_rewrite_ptr, /* glXChooseVisual */
    epoxy_glXCopyBufferSubDataNV_dispatch_table_rewrite_ptr, /* glXCopyBufferSubDataNV */
    epoxy_glXCopyContext_dispatch_table_rewrite_ptr, /* glXCopyContext */
    epoxy_glXCopyImageSubDataNV_dispatch_table_rewrite_ptr, /* glXCopyImageSubDataNV */
    epoxy_glXCopySubBufferMESA_dispatch_table_rewrite_ptr, /* glXCopySubBufferMESA */
    epoxy_glXCreateAssociatedContextAMD_dispatch_table_rewrite_ptr, /* glXCreateAssociatedContextAMD */
    epoxy_glXCreateAssociatedContextAttribsAMD_dispatch_table_rewrite_ptr, /* glXCreateAssociatedContextAttribsAMD */
    epoxy_glXCreateContext_dispatch_table_rewrite_ptr, /* glXCreateContext */
    epoxy_glXCreateContextAttribsARB_dispatch_table_rewrite_ptr, /* glXCreateContextAttribsARB */
    epoxy_glXCreateContextWithConfigSGIX_dispatch_table_rewrite_ptr, /* glXCreateContextWithConfigSGIX */
    epoxy_glXCreateGLXPbufferSGIX_dispatch_table_rewrite_ptr, /* glXCreateGLXPbufferSGIX */
    epoxy_glXCreateGLXPixmap_dispatch_table_rewrite_ptr, /* glXCreateGLXPixmap */
    epoxy_glXCreateGLXPixmapMESA_dispatch_table_rewrite_ptr, /* glXCreateGLXPixmapMESA */
    epoxy_glXCreateGLXPixmapWithConfigSGIX_dispatch_table_rewrite_ptr, /* glXCreateGLXPixmapWithConfigSGIX */
    epoxy_glXCreateNewContext_dispatch_table_rewrite_ptr, /* glXCreateNewContext */
    epoxy_glXCreatePbuffer_dispatch_table_rewrite_ptr, /* glXCreatePbuffer */
    epoxy_glXCreatePixmap_dispatch_table_rewrite_ptr, /* glXCreatePixmap */
    epoxy_glXCreateWindow_dispatch_table_rewrite_ptr, /* glXCreateWindow */
    epoxy_glXCushionSGI_dispatch_table_rewrite_ptr, /* glXCushionSGI */
    epoxy_glXDelayBeforeSwapNV_dispatch_table_rewrite_ptr, /* glXDelayBeforeSwapNV */
    epoxy_glXDeleteAssociatedContextAMD_dispatch_table_rewrite_ptr, /* glXDeleteAssociatedContextAMD */
    epoxy_glXDestroyContext_dispatch_table_rewrite_ptr, /* glXDestroyContext */
    epoxy_glXDestroyGLXPbufferSGIX_dispatch_table_rewrite_ptr, /* glXDestroyGLXPbufferSGIX */
    epoxy_glXDestroyGLXPixmap_dispatch_table_rewrite_ptr, /* glXDestroyGLXPixmap */
    epoxy_glXDestroyGLXVideoSourceSGIX_dispatch_table_rewrite_ptr, /* glXDestroyGLXVideoSourceSGIX */
    epoxy_glXDestroyHyperpipeConfigSGIX_dispatch_table_rewrite_ptr, /* glXDestroyHyperpipeConfigSGIX */
    epoxy_glXDestroyPbuffer_dispatch_table_rewrite_ptr, /* glXDestroyPbuffer */
    epoxy_glXDestroyPixmap_dispatch_table_rewrite_ptr, /* glXDestroyPixmap */
    epoxy_glXDestroyWindow_dispatch_table_rewrite_ptr, /* glXDestroyWindow */
    epoxy_glXEnumerateVideoCaptureDevicesNV_dispatch_table_rewrite_ptr, /* glXEnumerateVideoCaptureDevicesNV */
    epoxy_glXEnumerateVideoDevicesNV_dispatch_table_rewrite_ptr, /* glXEnumerateVideoDevicesNV */
    epoxy_glXFreeContextEXT_dispatch_table_rewrite_ptr, /* glXFreeContextEXT */
    epoxy_glXGetAGPOffsetMESA_dispatch_table_rewrite_ptr, /* glXGetAGPOffsetMESA */
    epoxy_glXGetClientString_dispatch_table_rewrite_ptr, /* glXGetClientString */
    epoxy_glXGetConfig_dispatch_table_rewrite_ptr, /* glXGetConfig */
    epoxy_glXGetContextGPUIDAMD_dispatch_table_rewrite_ptr, /* glXGetContextGPUIDAMD */
    epoxy_glXGetContextIDEXT_dispatch_table_rewrite_ptr, /* glXGetContextIDEXT */
    epoxy_glXGetCurrentAssociatedContextAMD_dispatch_table_rewrite_ptr, /* glXGetCurrentAssociatedContextAMD */
    epoxy_glXGetCurrentContext_dispatch_table_rewrite_ptr, /* glXGetCurrentContext */
    epoxy_glXGetCurrentDisplay_dispatch_table_rewrite_ptr, /* glXGetCurrentDisplay */
    epoxy_glXGetCurrentDisplayEXT_dispatch_table_rewrite_ptr, /* glXGetCurrentDisplayEXT */
    epoxy_glXGetCurrentDrawable_dispatch_table_rewrite_ptr, /* glXGetCurrentDrawable */
    epoxy_glXGetCurrentReadDrawable_dispatch_table_rewrite_ptr, /* glXGetCurrentReadDrawable */
    epoxy_glXGetCurrentReadDrawableSGI_dispatch_table_rewrite_ptr, /* glXGetCurrentReadDrawableSGI */
    epoxy_glXGetFBConfigAttrib_dispatch_table_rewrite_ptr, /* glXGetFBConfigAttrib */
    epoxy_glXGetFBConfigAttribSGIX_dispatch_table_rewrite_ptr, /* glXGetFBConfigAttribSGIX */
    epoxy_glXGetFBConfigFromVisualSGIX_dispatch_table_rewrite_ptr, /* glXGetFBConfigFromVisualSGIX */
    epoxy_glXGetFBConfigs_dispatch_table_rewrite_ptr, /* glXGetFBConfigs */
    epoxy_glXGetGPUIDsAMD_dispatch_table_rewrite_ptr, /* glXGetGPUIDsAMD */
    epoxy_glXGetGPUInfoAMD_dispatch_table_rewrite_ptr, /* glXGetGPUInfoAMD */
    epoxy_glXGetMscRateOML_dispatch_table_rewrite_ptr, /* glXGetMscRateOML */
    epoxy_glXGetProcAddress_dispatch_table_rewrite_ptr, /* glXGetProcAddress */
    epoxy_glXGetProcAddressARB_dispatch_table_rewrite_ptr, /* glXGetProcAddressARB */
    epoxy_glXGetSelectedEvent_dispatch_table_rewrite_ptr, /* glXGetSelectedEvent */
    epoxy_glXGetSelectedEventSGIX_dispatch_table_rewrite_ptr, /* glXGetSelectedEventSGIX */
    epoxy_glXGetSwapIntervalMESA_dispatch_table_rewrite_ptr, /* glXGetSwapIntervalMESA */
    epoxy_glXGetSyncValuesOML_dispatch_table_rewrite_ptr, /* glXGetSyncValuesOML */
    epoxy_glXGetTransparentIndexSUN_dispatch_table_rewrite_ptr, /* glXGetTransparentIndexSUN */
    epoxy_glXGetVideoDeviceNV_dispatch_table_rewrite_ptr, /* glXGetVideoDeviceNV */
    epoxy_glXGetVideoInfoNV_dispatch_table_rewrite_ptr, /* glXGetVideoInfoNV */
    epoxy_glXGetVideoSyncSGI_dispatch_table_rewrite_ptr, /* glXGetVideoSyncSGI */
    epoxy_glXGetVisualFromFBConfig_dispatch_table_rewrite_ptr, /* glXGetVisualFromFBConfig */
    epoxy_glXGetVisualFromFBConfigSGIX_dispatch_table_rewrite_ptr, /* glXGetVisualFromFBConfigSGIX */
    epoxy_glXHyperpipeAttribSGIX_dispatch_table_rewrite_ptr, /* glXHyperpipeAttribSGIX */
    epoxy_glXHyperpipeConfigSGIX_dispatch_table_rewrite_ptr, /* glXHyperpipeConfigSGIX */
    epoxy_glXImportContextEXT_dispatch_table_rewrite_ptr, /* glXImportContextEXT */
    epoxy_glXIsDirect_dispatch_table_rewrite_ptr, /* glXIsDirect */
    epoxy_glXJoinSwapGroupNV_dispatch_table_rewrite_ptr, /* glXJoinSwapGroupNV */
    epoxy_glXJoinSwapGroupSGIX_dispatch_table_rewrite_ptr, /* glXJoinSwapGroupSGIX */
    epoxy_glXLockVideoCaptureDeviceNV_dispatch_table_rewrite_ptr, /* glXLockVideoCaptureDeviceNV */
    epoxy_glXMakeAssociatedContextCurrentAMD_dispatch_table_rewrite_ptr, /* glXMakeAssociatedContextCurrentAMD */
    epoxy_glXMakeContextCurrent_dispatch_table_rewrite_ptr, /* glXMakeContextCurrent */
    epoxy_glXMakeCurrent_dispatch_table_rewrite_ptr, /* glXMakeCurrent */
    epoxy_glXMakeCurrentReadSGI_dispatch_table_rewrite_ptr, /* glXMakeCurrentReadSGI */
    epoxy_glXNamedCopyBufferSubDataNV_dispatch_table_rewrite_ptr, /* glXNamedCopyBufferSubDataNV */
    epoxy_glXQueryChannelDeltasSGIX_dispatch_table_rewrite_ptr, /* glXQueryChannelDeltasSGIX */
    epoxy_glXQueryChannelRectSGIX_dispatch_table_rewrite_ptr, /* glXQueryChannelRectSGIX */
    epoxy_glXQueryContext_dispatch_table_rewrite_ptr, /* glXQueryContext */
    epoxy_glXQueryContextInfoEXT_dispatch_table_rewrite_ptr, /* glXQueryContextInfoEXT */
    epoxy_glXQueryCurrentRendererIntegerMESA_dispatch_table_rewrite_ptr, /* glXQueryCurrentRendererIntegerMESA */
    epoxy_glXQueryCurrentRendererStringMESA_dispatch_table_rewrite_ptr, /* glXQueryCurrentRendererStringMESA */
    epoxy_glXQueryDrawable_dispatch_table_rewrite_ptr, /* glXQueryDrawable */
    epoxy_glXQueryExtension_dispatch_table_rewrite_ptr, /* glXQueryExtension */
    epoxy_glXQueryExtensionsString_dispatch_table_rewrite_ptr, /* glXQueryExtensionsString */
    epoxy_glXQueryFrameCountNV_dispatch_table_rewrite_ptr, /* glXQueryFrameCountNV */
    epoxy_glXQueryGLXPbufferSGIX_dispatch_table_rewrite_ptr, /* glXQueryGLXPbufferSGIX */
    epoxy_glXQueryHyperpipeAttribSGIX_dispatch_table_rewrite_ptr, /* glXQueryHyperpipeAttribSGIX */
    epoxy_glXQueryHyperpipeBestAttribSGIX_dispatch_table_rewrite_ptr, /* glXQueryHyperpipeBestAttribSGIX */
    epoxy_glXQueryHyperpipeConfigSGIX_dispatch_table_rewrite_ptr, /* glXQueryHyperpipeConfigSGIX */
    epoxy_glXQueryHyperpipeNetworkSGIX_dispatch_table_rewrite_ptr, /* glXQueryHyperpipeNetworkSGIX */
    epoxy_glXQueryMaxSwapBarriersSGIX_dispatch_table_rewrite_ptr, /* glXQueryMaxSwapBarriersSGIX */
    epoxy_glXQueryMaxSwapGroupsNV_dispatch_table_rewrite_ptr, /* glXQueryMaxSwapGroupsNV */
    epoxy_glXQueryRendererIntegerMESA_dispatch_table_rewrite_ptr, /* glXQueryRendererIntegerMESA */
    epoxy_glXQueryRendererStringMESA_dispatch_table_rewrite_ptr, /* glXQueryRendererStringMESA */
    epoxy_glXQueryServerString_dispatch_table_rewrite_ptr, /* glXQueryServerString */
    epoxy_glXQuerySwapGroupNV_dispatch_table_rewrite_ptr, /* glXQuerySwapGroupNV */
    epoxy_glXQueryVersion_dispatch_table_rewrite_ptr, /* glXQueryVersion */
    epoxy_glXQueryVideoCaptureDeviceNV_dispatch_table_rewrite_ptr, /* glXQueryVideoCaptureDeviceNV */
    epoxy_glXReleaseBuffersMESA_dispatch_table_rewrite_ptr, /* glXReleaseBuffersMESA */
    epoxy_glXReleaseTexImageEXT_dispatch_table_rewrite_ptr, /* glXReleaseTexImageEXT */
    epoxy_glXReleaseVideoCaptureDeviceNV_dispatch_table_rewrite_ptr, /* glXReleaseVideoCaptureDeviceNV */
    epoxy_glXReleaseVideoDeviceNV_dispatch_table_rewrite_ptr, /* glXReleaseVideoDeviceNV */
    epoxy_glXReleaseVideoImageNV_dispatch_table_rewrite_ptr, /* glXReleaseVideoImageNV */
    epoxy_glXResetFrameCountNV_dispatch_table_rewrite_ptr, /* glXResetFrameCountNV */
    epoxy_glXSelectEvent_dispatch_table_rewrite_ptr, /* glXSelectEvent */
    epoxy_glXSelectEventSGIX_dispatch_table_rewrite_ptr, /* glXSelectEventSGIX */
    epoxy_glXSendPbufferToVideoNV_dispatch_table_rewrite_ptr, /* glXSendPbufferToVideoNV */
    epoxy_glXSet3DfxModeMESA_dispatch_table_rewrite_ptr, /* glXSet3DfxModeMESA */
    epoxy_glXSwapBuffers_dispatch_table_rewrite_ptr, /* glXSwapBuffers */
    epoxy_glXSwapBuffersMscOML_dispatch_table_rewrite_ptr, /* glXSwapBuffersMscOML */
    epoxy_glXSwapIntervalEXT_dispatch_table_rewrite_ptr, /* glXSwapIntervalEXT */
    epoxy_glXSwapIntervalMESA_dispatch_table_rewrite_ptr, /* glXSwapIntervalMESA */
    epoxy_glXSwapIntervalSGI_dispatch_table_rewrite_ptr, /* glXSwapIntervalSGI */
    epoxy_glXUseXFont_dispatch_table_rewrite_ptr, /* glXUseXFont */
    epoxy_glXWaitForMscOML_dispatch_table_rewrite_ptr, /* glXWaitForMscOML */
    epoxy_glXWaitForSbcOML_dispatch_table_rewrite_ptr, /* glXWaitForSbcOML */
    epoxy_glXWaitGL_dispatch_table_rewrite_ptr, /* glXWaitGL */
    epoxy_glXWaitVideoSyncSGI_dispatch_table_rewrite_ptr, /* glXWaitVideoSyncSGI */
    epoxy_glXWaitX_dispatch_table_rewrite_ptr, /* glXWaitX */
};

uint32_t glx_tls_index;
uint32_t glx_tls_size = sizeof(struct dispatch_table);

static inline struct dispatch_table *
get_dispatch_table(void)
{
	return TlsGetValue(glx_tls_index);
}

void
glx_init_dispatch_table(void)
{
    struct dispatch_table *dispatch_table = get_dispatch_table();
    memcpy(dispatch_table, &resolver_table, sizeof(resolver_table));
}

void
glx_switch_to_dispatch_table(void)
{
    epoxy_glXBindChannelToWindowSGIX = epoxy_glXBindChannelToWindowSGIX_dispatch_table_thunk;
    epoxy_glXBindHyperpipeSGIX = epoxy_glXBindHyperpipeSGIX_dispatch_table_thunk;
    epoxy_glXBindSwapBarrierNV = epoxy_glXBindSwapBarrierNV_dispatch_table_thunk;
    epoxy_glXBindSwapBarrierSGIX = epoxy_glXBindSwapBarrierSGIX_dispatch_table_thunk;
    epoxy_glXBindTexImageEXT = epoxy_glXBindTexImageEXT_dispatch_table_thunk;
    epoxy_glXBindVideoCaptureDeviceNV = epoxy_glXBindVideoCaptureDeviceNV_dispatch_table_thunk;
    epoxy_glXBindVideoDeviceNV = epoxy_glXBindVideoDeviceNV_dispatch_table_thunk;
    epoxy_glXBindVideoImageNV = epoxy_glXBindVideoImageNV_dispatch_table_thunk;
    epoxy_glXBlitContextFramebufferAMD = epoxy_glXBlitContextFramebufferAMD_dispatch_table_thunk;
    epoxy_glXChannelRectSGIX = epoxy_glXChannelRectSGIX_dispatch_table_thunk;
    epoxy_glXChannelRectSyncSGIX = epoxy_glXChannelRectSyncSGIX_dispatch_table_thunk;
    epoxy_glXChooseFBConfig = epoxy_glXChooseFBConfig_dispatch_table_thunk;
    epoxy_glXChooseFBConfigSGIX = epoxy_glXChooseFBConfigSGIX_dispatch_table_thunk;
    epoxy_glXChooseVisual = epoxy_glXChooseVisual_dispatch_table_thunk;
    epoxy_glXCopyBufferSubDataNV = epoxy_glXCopyBufferSubDataNV_dispatch_table_thunk;
    epoxy_glXCopyContext = epoxy_glXCopyContext_dispatch_table_thunk;
    epoxy_glXCopyImageSubDataNV = epoxy_glXCopyImageSubDataNV_dispatch_table_thunk;
    epoxy_glXCopySubBufferMESA = epoxy_glXCopySubBufferMESA_dispatch_table_thunk;
    epoxy_glXCreateAssociatedContextAMD = epoxy_glXCreateAssociatedContextAMD_dispatch_table_thunk;
    epoxy_glXCreateAssociatedContextAttribsAMD = epoxy_glXCreateAssociatedContextAttribsAMD_dispatch_table_thunk;
    epoxy_glXCreateContext = epoxy_glXCreateContext_dispatch_table_thunk;
    epoxy_glXCreateContextAttribsARB = epoxy_glXCreateContextAttribsARB_dispatch_table_thunk;
    epoxy_glXCreateContextWithConfigSGIX = epoxy_glXCreateContextWithConfigSGIX_dispatch_table_thunk;
    epoxy_glXCreateGLXPbufferSGIX = epoxy_glXCreateGLXPbufferSGIX_dispatch_table_thunk;
    epoxy_glXCreateGLXPixmap = epoxy_glXCreateGLXPixmap_dispatch_table_thunk;
    epoxy_glXCreateGLXPixmapMESA = epoxy_glXCreateGLXPixmapMESA_dispatch_table_thunk;
    epoxy_glXCreateGLXPixmapWithConfigSGIX = epoxy_glXCreateGLXPixmapWithConfigSGIX_dispatch_table_thunk;
    epoxy_glXCreateNewContext = epoxy_glXCreateNewContext_dispatch_table_thunk;
    epoxy_glXCreatePbuffer = epoxy_glXCreatePbuffer_dispatch_table_thunk;
    epoxy_glXCreatePixmap = epoxy_glXCreatePixmap_dispatch_table_thunk;
    epoxy_glXCreateWindow = epoxy_glXCreateWindow_dispatch_table_thunk;
    epoxy_glXCushionSGI = epoxy_glXCushionSGI_dispatch_table_thunk;
    epoxy_glXDelayBeforeSwapNV = epoxy_glXDelayBeforeSwapNV_dispatch_table_thunk;
    epoxy_glXDeleteAssociatedContextAMD = epoxy_glXDeleteAssociatedContextAMD_dispatch_table_thunk;
    epoxy_glXDestroyContext = epoxy_glXDestroyContext_dispatch_table_thunk;
    epoxy_glXDestroyGLXPbufferSGIX = epoxy_glXDestroyGLXPbufferSGIX_dispatch_table_thunk;
    epoxy_glXDestroyGLXPixmap = epoxy_glXDestroyGLXPixmap_dispatch_table_thunk;
    epoxy_glXDestroyGLXVideoSourceSGIX = epoxy_glXDestroyGLXVideoSourceSGIX_dispatch_table_thunk;
    epoxy_glXDestroyHyperpipeConfigSGIX = epoxy_glXDestroyHyperpipeConfigSGIX_dispatch_table_thunk;
    epoxy_glXDestroyPbuffer = epoxy_glXDestroyPbuffer_dispatch_table_thunk;
    epoxy_glXDestroyPixmap = epoxy_glXDestroyPixmap_dispatch_table_thunk;
    epoxy_glXDestroyWindow = epoxy_glXDestroyWindow_dispatch_table_thunk;
    epoxy_glXEnumerateVideoCaptureDevicesNV = epoxy_glXEnumerateVideoCaptureDevicesNV_dispatch_table_thunk;
    epoxy_glXEnumerateVideoDevicesNV = epoxy_glXEnumerateVideoDevicesNV_dispatch_table_thunk;
    epoxy_glXFreeContextEXT = epoxy_glXFreeContextEXT_dispatch_table_thunk;
    epoxy_glXGetAGPOffsetMESA = epoxy_glXGetAGPOffsetMESA_dispatch_table_thunk;
    epoxy_glXGetClientString = epoxy_glXGetClientString_dispatch_table_thunk;
    epoxy_glXGetConfig = epoxy_glXGetConfig_dispatch_table_thunk;
    epoxy_glXGetContextGPUIDAMD = epoxy_glXGetContextGPUIDAMD_dispatch_table_thunk;
    epoxy_glXGetContextIDEXT = epoxy_glXGetContextIDEXT_dispatch_table_thunk;
    epoxy_glXGetCurrentAssociatedContextAMD = epoxy_glXGetCurrentAssociatedContextAMD_dispatch_table_thunk;
    epoxy_glXGetCurrentContext = epoxy_glXGetCurrentContext_dispatch_table_thunk;
    epoxy_glXGetCurrentDisplay = epoxy_glXGetCurrentDisplay_dispatch_table_thunk;
    epoxy_glXGetCurrentDisplayEXT = epoxy_glXGetCurrentDisplayEXT_dispatch_table_thunk;
    epoxy_glXGetCurrentDrawable = epoxy_glXGetCurrentDrawable_dispatch_table_thunk;
    epoxy_glXGetCurrentReadDrawable = epoxy_glXGetCurrentReadDrawable_dispatch_table_thunk;
    epoxy_glXGetCurrentReadDrawableSGI = epoxy_glXGetCurrentReadDrawableSGI_dispatch_table_thunk;
    epoxy_glXGetFBConfigAttrib = epoxy_glXGetFBConfigAttrib_dispatch_table_thunk;
    epoxy_glXGetFBConfigAttribSGIX = epoxy_glXGetFBConfigAttribSGIX_dispatch_table_thunk;
    epoxy_glXGetFBConfigFromVisualSGIX = epoxy_glXGetFBConfigFromVisualSGIX_dispatch_table_thunk;
    epoxy_glXGetFBConfigs = epoxy_glXGetFBConfigs_dispatch_table_thunk;
    epoxy_glXGetGPUIDsAMD = epoxy_glXGetGPUIDsAMD_dispatch_table_thunk;
    epoxy_glXGetGPUInfoAMD = epoxy_glXGetGPUInfoAMD_dispatch_table_thunk;
    epoxy_glXGetMscRateOML = epoxy_glXGetMscRateOML_dispatch_table_thunk;
    epoxy_glXGetProcAddress = epoxy_glXGetProcAddress_dispatch_table_thunk;
    epoxy_glXGetProcAddressARB = epoxy_glXGetProcAddressARB_dispatch_table_thunk;
    epoxy_glXGetSelectedEvent = epoxy_glXGetSelectedEvent_dispatch_table_thunk;
    epoxy_glXGetSelectedEventSGIX = epoxy_glXGetSelectedEventSGIX_dispatch_table_thunk;
    epoxy_glXGetSwapIntervalMESA = epoxy_glXGetSwapIntervalMESA_dispatch_table_thunk;
    epoxy_glXGetSyncValuesOML = epoxy_glXGetSyncValuesOML_dispatch_table_thunk;
    epoxy_glXGetTransparentIndexSUN = epoxy_glXGetTransparentIndexSUN_dispatch_table_thunk;
    epoxy_glXGetVideoDeviceNV = epoxy_glXGetVideoDeviceNV_dispatch_table_thunk;
    epoxy_glXGetVideoInfoNV = epoxy_glXGetVideoInfoNV_dispatch_table_thunk;
    epoxy_glXGetVideoSyncSGI = epoxy_glXGetVideoSyncSGI_dispatch_table_thunk;
    epoxy_glXGetVisualFromFBConfig = epoxy_glXGetVisualFromFBConfig_dispatch_table_thunk;
    epoxy_glXGetVisualFromFBConfigSGIX = epoxy_glXGetVisualFromFBConfigSGIX_dispatch_table_thunk;
    epoxy_glXHyperpipeAttribSGIX = epoxy_glXHyperpipeAttribSGIX_dispatch_table_thunk;
    epoxy_glXHyperpipeConfigSGIX = epoxy_glXHyperpipeConfigSGIX_dispatch_table_thunk;
    epoxy_glXImportContextEXT = epoxy_glXImportContextEXT_dispatch_table_thunk;
    epoxy_glXIsDirect = epoxy_glXIsDirect_dispatch_table_thunk;
    epoxy_glXJoinSwapGroupNV = epoxy_glXJoinSwapGroupNV_dispatch_table_thunk;
    epoxy_glXJoinSwapGroupSGIX = epoxy_glXJoinSwapGroupSGIX_dispatch_table_thunk;
    epoxy_glXLockVideoCaptureDeviceNV = epoxy_glXLockVideoCaptureDeviceNV_dispatch_table_thunk;
    epoxy_glXMakeAssociatedContextCurrentAMD = epoxy_glXMakeAssociatedContextCurrentAMD_dispatch_table_thunk;
    epoxy_glXMakeContextCurrent = epoxy_glXMakeContextCurrent_dispatch_table_thunk;
    epoxy_glXMakeCurrent = epoxy_glXMakeCurrent_dispatch_table_thunk;
    epoxy_glXMakeCurrentReadSGI = epoxy_glXMakeCurrentReadSGI_dispatch_table_thunk;
    epoxy_glXNamedCopyBufferSubDataNV = epoxy_glXNamedCopyBufferSubDataNV_dispatch_table_thunk;
    epoxy_glXQueryChannelDeltasSGIX = epoxy_glXQueryChannelDeltasSGIX_dispatch_table_thunk;
    epoxy_glXQueryChannelRectSGIX = epoxy_glXQueryChannelRectSGIX_dispatch_table_thunk;
    epoxy_glXQueryContext = epoxy_glXQueryContext_dispatch_table_thunk;
    epoxy_glXQueryContextInfoEXT = epoxy_glXQueryContextInfoEXT_dispatch_table_thunk;
    epoxy_glXQueryCurrentRendererIntegerMESA = epoxy_glXQueryCurrentRendererIntegerMESA_dispatch_table_thunk;
    epoxy_glXQueryCurrentRendererStringMESA = epoxy_glXQueryCurrentRendererStringMESA_dispatch_table_thunk;
    epoxy_glXQueryDrawable = epoxy_glXQueryDrawable_dispatch_table_thunk;
    epoxy_glXQueryExtension = epoxy_glXQueryExtension_dispatch_table_thunk;
    epoxy_glXQueryExtensionsString = epoxy_glXQueryExtensionsString_dispatch_table_thunk;
    epoxy_glXQueryFrameCountNV = epoxy_glXQueryFrameCountNV_dispatch_table_thunk;
    epoxy_glXQueryGLXPbufferSGIX = epoxy_glXQueryGLXPbufferSGIX_dispatch_table_thunk;
    epoxy_glXQueryHyperpipeAttribSGIX = epoxy_glXQueryHyperpipeAttribSGIX_dispatch_table_thunk;
    epoxy_glXQueryHyperpipeBestAttribSGIX = epoxy_glXQueryHyperpipeBestAttribSGIX_dispatch_table_thunk;
    epoxy_glXQueryHyperpipeConfigSGIX = epoxy_glXQueryHyperpipeConfigSGIX_dispatch_table_thunk;
    epoxy_glXQueryHyperpipeNetworkSGIX = epoxy_glXQueryHyperpipeNetworkSGIX_dispatch_table_thunk;
    epoxy_glXQueryMaxSwapBarriersSGIX = epoxy_glXQueryMaxSwapBarriersSGIX_dispatch_table_thunk;
    epoxy_glXQueryMaxSwapGroupsNV = epoxy_glXQueryMaxSwapGroupsNV_dispatch_table_thunk;
    epoxy_glXQueryRendererIntegerMESA = epoxy_glXQueryRendererIntegerMESA_dispatch_table_thunk;
    epoxy_glXQueryRendererStringMESA = epoxy_glXQueryRendererStringMESA_dispatch_table_thunk;
    epoxy_glXQueryServerString = epoxy_glXQueryServerString_dispatch_table_thunk;
    epoxy_glXQuerySwapGroupNV = epoxy_glXQuerySwapGroupNV_dispatch_table_thunk;
    epoxy_glXQueryVersion = epoxy_glXQueryVersion_dispatch_table_thunk;
    epoxy_glXQueryVideoCaptureDeviceNV = epoxy_glXQueryVideoCaptureDeviceNV_dispatch_table_thunk;
    epoxy_glXReleaseBuffersMESA = epoxy_glXReleaseBuffersMESA_dispatch_table_thunk;
    epoxy_glXReleaseTexImageEXT = epoxy_glXReleaseTexImageEXT_dispatch_table_thunk;
    epoxy_glXReleaseVideoCaptureDeviceNV = epoxy_glXReleaseVideoCaptureDeviceNV_dispatch_table_thunk;
    epoxy_glXReleaseVideoDeviceNV = epoxy_glXReleaseVideoDeviceNV_dispatch_table_thunk;
    epoxy_glXReleaseVideoImageNV = epoxy_glXReleaseVideoImageNV_dispatch_table_thunk;
    epoxy_glXResetFrameCountNV = epoxy_glXResetFrameCountNV_dispatch_table_thunk;
    epoxy_glXSelectEvent = epoxy_glXSelectEvent_dispatch_table_thunk;
    epoxy_glXSelectEventSGIX = epoxy_glXSelectEventSGIX_dispatch_table_thunk;
    epoxy_glXSendPbufferToVideoNV = epoxy_glXSendPbufferToVideoNV_dispatch_table_thunk;
    epoxy_glXSet3DfxModeMESA = epoxy_glXSet3DfxModeMESA_dispatch_table_thunk;
    epoxy_glXSwapBuffers = epoxy_glXSwapBuffers_dispatch_table_thunk;
    epoxy_glXSwapBuffersMscOML = epoxy_glXSwapBuffersMscOML_dispatch_table_thunk;
    epoxy_glXSwapIntervalEXT = epoxy_glXSwapIntervalEXT_dispatch_table_thunk;
    epoxy_glXSwapIntervalMESA = epoxy_glXSwapIntervalMESA_dispatch_table_thunk;
    epoxy_glXSwapIntervalSGI = epoxy_glXSwapIntervalSGI_dispatch_table_thunk;
    epoxy_glXUseXFont = epoxy_glXUseXFont_dispatch_table_thunk;
    epoxy_glXWaitForMscOML = epoxy_glXWaitForMscOML_dispatch_table_thunk;
    epoxy_glXWaitForSbcOML = epoxy_glXWaitForSbcOML_dispatch_table_thunk;
    epoxy_glXWaitGL = epoxy_glXWaitGL_dispatch_table_thunk;
    epoxy_glXWaitVideoSyncSGI = epoxy_glXWaitVideoSyncSGI_dispatch_table_thunk;
    epoxy_glXWaitX = epoxy_glXWaitX_dispatch_table_thunk;
}

#endif /* !USING_DISPATCH_TABLE */
PFNGLXBINDCHANNELTOWINDOWSGIXPROC epoxy_glXBindChannelToWindowSGIX = epoxy_glXBindChannelToWindowSGIX_global_rewrite_ptr;

PFNGLXBINDHYPERPIPESGIXPROC epoxy_glXBindHyperpipeSGIX = epoxy_glXBindHyperpipeSGIX_global_rewrite_ptr;

PFNGLXBINDSWAPBARRIERNVPROC epoxy_glXBindSwapBarrierNV = epoxy_glXBindSwapBarrierNV_global_rewrite_ptr;

PFNGLXBINDSWAPBARRIERSGIXPROC epoxy_glXBindSwapBarrierSGIX = epoxy_glXBindSwapBarrierSGIX_global_rewrite_ptr;

PFNGLXBINDTEXIMAGEEXTPROC epoxy_glXBindTexImageEXT = epoxy_glXBindTexImageEXT_global_rewrite_ptr;

PFNGLXBINDVIDEOCAPTUREDEVICENVPROC epoxy_glXBindVideoCaptureDeviceNV = epoxy_glXBindVideoCaptureDeviceNV_global_rewrite_ptr;

PFNGLXBINDVIDEODEVICENVPROC epoxy_glXBindVideoDeviceNV = epoxy_glXBindVideoDeviceNV_global_rewrite_ptr;

PFNGLXBINDVIDEOIMAGENVPROC epoxy_glXBindVideoImageNV = epoxy_glXBindVideoImageNV_global_rewrite_ptr;

PFNGLXBLITCONTEXTFRAMEBUFFERAMDPROC epoxy_glXBlitContextFramebufferAMD = epoxy_glXBlitContextFramebufferAMD_global_rewrite_ptr;

PFNGLXCHANNELRECTSGIXPROC epoxy_glXChannelRectSGIX = epoxy_glXChannelRectSGIX_global_rewrite_ptr;

PFNGLXCHANNELRECTSYNCSGIXPROC epoxy_glXChannelRectSyncSGIX = epoxy_glXChannelRectSyncSGIX_global_rewrite_ptr;

PFNGLXCHOOSEFBCONFIGPROC epoxy_glXChooseFBConfig = epoxy_glXChooseFBConfig_global_rewrite_ptr;

PFNGLXCHOOSEFBCONFIGSGIXPROC epoxy_glXChooseFBConfigSGIX = epoxy_glXChooseFBConfigSGIX_global_rewrite_ptr;

PFNGLXCHOOSEVISUALPROC epoxy_glXChooseVisual = epoxy_glXChooseVisual_global_rewrite_ptr;

PFNGLXCOPYBUFFERSUBDATANVPROC epoxy_glXCopyBufferSubDataNV = epoxy_glXCopyBufferSubDataNV_global_rewrite_ptr;

PFNGLXCOPYCONTEXTPROC epoxy_glXCopyContext = epoxy_glXCopyContext_global_rewrite_ptr;

PFNGLXCOPYIMAGESUBDATANVPROC epoxy_glXCopyImageSubDataNV = epoxy_glXCopyImageSubDataNV_global_rewrite_ptr;

PFNGLXCOPYSUBBUFFERMESAPROC epoxy_glXCopySubBufferMESA = epoxy_glXCopySubBufferMESA_global_rewrite_ptr;

PFNGLXCREATEASSOCIATEDCONTEXTAMDPROC epoxy_glXCreateAssociatedContextAMD = epoxy_glXCreateAssociatedContextAMD_global_rewrite_ptr;

PFNGLXCREATEASSOCIATEDCONTEXTATTRIBSAMDPROC epoxy_glXCreateAssociatedContextAttribsAMD = epoxy_glXCreateAssociatedContextAttribsAMD_global_rewrite_ptr;

PFNGLXCREATECONTEXTPROC epoxy_glXCreateContext = epoxy_glXCreateContext_global_rewrite_ptr;

PFNGLXCREATECONTEXTATTRIBSARBPROC epoxy_glXCreateContextAttribsARB = epoxy_glXCreateContextAttribsARB_global_rewrite_ptr;

PFNGLXCREATECONTEXTWITHCONFIGSGIXPROC epoxy_glXCreateContextWithConfigSGIX = epoxy_glXCreateContextWithConfigSGIX_global_rewrite_ptr;

PFNGLXCREATEGLXPBUFFERSGIXPROC epoxy_glXCreateGLXPbufferSGIX = epoxy_glXCreateGLXPbufferSGIX_global_rewrite_ptr;

PFNGLXCREATEGLXPIXMAPPROC epoxy_glXCreateGLXPixmap = epoxy_glXCreateGLXPixmap_global_rewrite_ptr;

PFNGLXCREATEGLXPIXMAPMESAPROC epoxy_glXCreateGLXPixmapMESA = epoxy_glXCreateGLXPixmapMESA_global_rewrite_ptr;

PFNGLXCREATEGLXPIXMAPWITHCONFIGSGIXPROC epoxy_glXCreateGLXPixmapWithConfigSGIX = epoxy_glXCreateGLXPixmapWithConfigSGIX_global_rewrite_ptr;

PFNGLXCREATENEWCONTEXTPROC epoxy_glXCreateNewContext = epoxy_glXCreateNewContext_global_rewrite_ptr;

PFNGLXCREATEPBUFFERPROC epoxy_glXCreatePbuffer = epoxy_glXCreatePbuffer_global_rewrite_ptr;

PFNGLXCREATEPIXMAPPROC epoxy_glXCreatePixmap = epoxy_glXCreatePixmap_global_rewrite_ptr;

PFNGLXCREATEWINDOWPROC epoxy_glXCreateWindow = epoxy_glXCreateWindow_global_rewrite_ptr;

PFNGLXCUSHIONSGIPROC epoxy_glXCushionSGI = epoxy_glXCushionSGI_global_rewrite_ptr;

PFNGLXDELAYBEFORESWAPNVPROC epoxy_glXDelayBeforeSwapNV = epoxy_glXDelayBeforeSwapNV_global_rewrite_ptr;

PFNGLXDELETEASSOCIATEDCONTEXTAMDPROC epoxy_glXDeleteAssociatedContextAMD = epoxy_glXDeleteAssociatedContextAMD_global_rewrite_ptr;

PFNGLXDESTROYCONTEXTPROC epoxy_glXDestroyContext = epoxy_glXDestroyContext_global_rewrite_ptr;

PFNGLXDESTROYGLXPBUFFERSGIXPROC epoxy_glXDestroyGLXPbufferSGIX = epoxy_glXDestroyGLXPbufferSGIX_global_rewrite_ptr;

PFNGLXDESTROYGLXPIXMAPPROC epoxy_glXDestroyGLXPixmap = epoxy_glXDestroyGLXPixmap_global_rewrite_ptr;

PFNGLXDESTROYGLXVIDEOSOURCESGIXPROC epoxy_glXDestroyGLXVideoSourceSGIX = epoxy_glXDestroyGLXVideoSourceSGIX_global_rewrite_ptr;

PFNGLXDESTROYHYPERPIPECONFIGSGIXPROC epoxy_glXDestroyHyperpipeConfigSGIX = epoxy_glXDestroyHyperpipeConfigSGIX_global_rewrite_ptr;

PFNGLXDESTROYPBUFFERPROC epoxy_glXDestroyPbuffer = epoxy_glXDestroyPbuffer_global_rewrite_ptr;

PFNGLXDESTROYPIXMAPPROC epoxy_glXDestroyPixmap = epoxy_glXDestroyPixmap_global_rewrite_ptr;

PFNGLXDESTROYWINDOWPROC epoxy_glXDestroyWindow = epoxy_glXDestroyWindow_global_rewrite_ptr;

PFNGLXENUMERATEVIDEOCAPTUREDEVICESNVPROC epoxy_glXEnumerateVideoCaptureDevicesNV = epoxy_glXEnumerateVideoCaptureDevicesNV_global_rewrite_ptr;

PFNGLXENUMERATEVIDEODEVICESNVPROC epoxy_glXEnumerateVideoDevicesNV = epoxy_glXEnumerateVideoDevicesNV_global_rewrite_ptr;

PFNGLXFREECONTEXTEXTPROC epoxy_glXFreeContextEXT = epoxy_glXFreeContextEXT_global_rewrite_ptr;

PFNGLXGETAGPOFFSETMESAPROC epoxy_glXGetAGPOffsetMESA = epoxy_glXGetAGPOffsetMESA_global_rewrite_ptr;

PFNGLXGETCLIENTSTRINGPROC epoxy_glXGetClientString = epoxy_glXGetClientString_global_rewrite_ptr;

PFNGLXGETCONFIGPROC epoxy_glXGetConfig = epoxy_glXGetConfig_global_rewrite_ptr;

PFNGLXGETCONTEXTGPUIDAMDPROC epoxy_glXGetContextGPUIDAMD = epoxy_glXGetContextGPUIDAMD_global_rewrite_ptr;

PFNGLXGETCONTEXTIDEXTPROC epoxy_glXGetContextIDEXT = epoxy_glXGetContextIDEXT_global_rewrite_ptr;

PFNGLXGETCURRENTASSOCIATEDCONTEXTAMDPROC epoxy_glXGetCurrentAssociatedContextAMD = epoxy_glXGetCurrentAssociatedContextAMD_global_rewrite_ptr;

PFNGLXGETCURRENTCONTEXTPROC epoxy_glXGetCurrentContext = epoxy_glXGetCurrentContext_global_rewrite_ptr;

PFNGLXGETCURRENTDISPLAYPROC epoxy_glXGetCurrentDisplay = epoxy_glXGetCurrentDisplay_global_rewrite_ptr;

PFNGLXGETCURRENTDISPLAYEXTPROC epoxy_glXGetCurrentDisplayEXT = epoxy_glXGetCurrentDisplayEXT_global_rewrite_ptr;

PFNGLXGETCURRENTDRAWABLEPROC epoxy_glXGetCurrentDrawable = epoxy_glXGetCurrentDrawable_global_rewrite_ptr;

PFNGLXGETCURRENTREADDRAWABLEPROC epoxy_glXGetCurrentReadDrawable = epoxy_glXGetCurrentReadDrawable_global_rewrite_ptr;

PFNGLXGETCURRENTREADDRAWABLESGIPROC epoxy_glXGetCurrentReadDrawableSGI = epoxy_glXGetCurrentReadDrawableSGI_global_rewrite_ptr;

PFNGLXGETFBCONFIGATTRIBPROC epoxy_glXGetFBConfigAttrib = epoxy_glXGetFBConfigAttrib_global_rewrite_ptr;

PFNGLXGETFBCONFIGATTRIBSGIXPROC epoxy_glXGetFBConfigAttribSGIX = epoxy_glXGetFBConfigAttribSGIX_global_rewrite_ptr;

PFNGLXGETFBCONFIGFROMVISUALSGIXPROC epoxy_glXGetFBConfigFromVisualSGIX = epoxy_glXGetFBConfigFromVisualSGIX_global_rewrite_ptr;

PFNGLXGETFBCONFIGSPROC epoxy_glXGetFBConfigs = epoxy_glXGetFBConfigs_global_rewrite_ptr;

PFNGLXGETGPUIDSAMDPROC epoxy_glXGetGPUIDsAMD = epoxy_glXGetGPUIDsAMD_global_rewrite_ptr;

PFNGLXGETGPUINFOAMDPROC epoxy_glXGetGPUInfoAMD = epoxy_glXGetGPUInfoAMD_global_rewrite_ptr;

PFNGLXGETMSCRATEOMLPROC epoxy_glXGetMscRateOML = epoxy_glXGetMscRateOML_global_rewrite_ptr;

PFNGLXGETPROCADDRESSPROC epoxy_glXGetProcAddress = epoxy_glXGetProcAddress_global_rewrite_ptr;

PFNGLXGETPROCADDRESSARBPROC epoxy_glXGetProcAddressARB = epoxy_glXGetProcAddressARB_global_rewrite_ptr;

PFNGLXGETSELECTEDEVENTPROC epoxy_glXGetSelectedEvent = epoxy_glXGetSelectedEvent_global_rewrite_ptr;

PFNGLXGETSELECTEDEVENTSGIXPROC epoxy_glXGetSelectedEventSGIX = epoxy_glXGetSelectedEventSGIX_global_rewrite_ptr;

PFNGLXGETSWAPINTERVALMESAPROC epoxy_glXGetSwapIntervalMESA = epoxy_glXGetSwapIntervalMESA_global_rewrite_ptr;

PFNGLXGETSYNCVALUESOMLPROC epoxy_glXGetSyncValuesOML = epoxy_glXGetSyncValuesOML_global_rewrite_ptr;

PFNGLXGETTRANSPARENTINDEXSUNPROC epoxy_glXGetTransparentIndexSUN = epoxy_glXGetTransparentIndexSUN_global_rewrite_ptr;

PFNGLXGETVIDEODEVICENVPROC epoxy_glXGetVideoDeviceNV = epoxy_glXGetVideoDeviceNV_global_rewrite_ptr;

PFNGLXGETVIDEOINFONVPROC epoxy_glXGetVideoInfoNV = epoxy_glXGetVideoInfoNV_global_rewrite_ptr;

PFNGLXGETVIDEOSYNCSGIPROC epoxy_glXGetVideoSyncSGI = epoxy_glXGetVideoSyncSGI_global_rewrite_ptr;

PFNGLXGETVISUALFROMFBCONFIGPROC epoxy_glXGetVisualFromFBConfig = epoxy_glXGetVisualFromFBConfig_global_rewrite_ptr;

PFNGLXGETVISUALFROMFBCONFIGSGIXPROC epoxy_glXGetVisualFromFBConfigSGIX = epoxy_glXGetVisualFromFBConfigSGIX_global_rewrite_ptr;

PFNGLXHYPERPIPEATTRIBSGIXPROC epoxy_glXHyperpipeAttribSGIX = epoxy_glXHyperpipeAttribSGIX_global_rewrite_ptr;

PFNGLXHYPERPIPECONFIGSGIXPROC epoxy_glXHyperpipeConfigSGIX = epoxy_glXHyperpipeConfigSGIX_global_rewrite_ptr;

PFNGLXIMPORTCONTEXTEXTPROC epoxy_glXImportContextEXT = epoxy_glXImportContextEXT_global_rewrite_ptr;

PFNGLXISDIRECTPROC epoxy_glXIsDirect = epoxy_glXIsDirect_global_rewrite_ptr;

PFNGLXJOINSWAPGROUPNVPROC epoxy_glXJoinSwapGroupNV = epoxy_glXJoinSwapGroupNV_global_rewrite_ptr;

PFNGLXJOINSWAPGROUPSGIXPROC epoxy_glXJoinSwapGroupSGIX = epoxy_glXJoinSwapGroupSGIX_global_rewrite_ptr;

PFNGLXLOCKVIDEOCAPTUREDEVICENVPROC epoxy_glXLockVideoCaptureDeviceNV = epoxy_glXLockVideoCaptureDeviceNV_global_rewrite_ptr;

PFNGLXMAKEASSOCIATEDCONTEXTCURRENTAMDPROC epoxy_glXMakeAssociatedContextCurrentAMD = epoxy_glXMakeAssociatedContextCurrentAMD_global_rewrite_ptr;

PFNGLXMAKECONTEXTCURRENTPROC epoxy_glXMakeContextCurrent = epoxy_glXMakeContextCurrent_global_rewrite_ptr;

PFNGLXMAKECURRENTPROC epoxy_glXMakeCurrent = epoxy_glXMakeCurrent_global_rewrite_ptr;

PFNGLXMAKECURRENTREADSGIPROC epoxy_glXMakeCurrentReadSGI = epoxy_glXMakeCurrentReadSGI_global_rewrite_ptr;

PFNGLXNAMEDCOPYBUFFERSUBDATANVPROC epoxy_glXNamedCopyBufferSubDataNV = epoxy_glXNamedCopyBufferSubDataNV_global_rewrite_ptr;

PFNGLXQUERYCHANNELDELTASSGIXPROC epoxy_glXQueryChannelDeltasSGIX = epoxy_glXQueryChannelDeltasSGIX_global_rewrite_ptr;

PFNGLXQUERYCHANNELRECTSGIXPROC epoxy_glXQueryChannelRectSGIX = epoxy_glXQueryChannelRectSGIX_global_rewrite_ptr;

PFNGLXQUERYCONTEXTPROC epoxy_glXQueryContext = epoxy_glXQueryContext_global_rewrite_ptr;

PFNGLXQUERYCONTEXTINFOEXTPROC epoxy_glXQueryContextInfoEXT = epoxy_glXQueryContextInfoEXT_global_rewrite_ptr;

PFNGLXQUERYCURRENTRENDERERINTEGERMESAPROC epoxy_glXQueryCurrentRendererIntegerMESA = epoxy_glXQueryCurrentRendererIntegerMESA_global_rewrite_ptr;

PFNGLXQUERYCURRENTRENDERERSTRINGMESAPROC epoxy_glXQueryCurrentRendererStringMESA = epoxy_glXQueryCurrentRendererStringMESA_global_rewrite_ptr;

PFNGLXQUERYDRAWABLEPROC epoxy_glXQueryDrawable = epoxy_glXQueryDrawable_global_rewrite_ptr;

PFNGLXQUERYEXTENSIONPROC epoxy_glXQueryExtension = epoxy_glXQueryExtension_global_rewrite_ptr;

PFNGLXQUERYEXTENSIONSSTRINGPROC epoxy_glXQueryExtensionsString = epoxy_glXQueryExtensionsString_global_rewrite_ptr;

PFNGLXQUERYFRAMECOUNTNVPROC epoxy_glXQueryFrameCountNV = epoxy_glXQueryFrameCountNV_global_rewrite_ptr;

PFNGLXQUERYGLXPBUFFERSGIXPROC epoxy_glXQueryGLXPbufferSGIX = epoxy_glXQueryGLXPbufferSGIX_global_rewrite_ptr;

PFNGLXQUERYHYPERPIPEATTRIBSGIXPROC epoxy_glXQueryHyperpipeAttribSGIX = epoxy_glXQueryHyperpipeAttribSGIX_global_rewrite_ptr;

PFNGLXQUERYHYPERPIPEBESTATTRIBSGIXPROC epoxy_glXQueryHyperpipeBestAttribSGIX = epoxy_glXQueryHyperpipeBestAttribSGIX_global_rewrite_ptr;

PFNGLXQUERYHYPERPIPECONFIGSGIXPROC epoxy_glXQueryHyperpipeConfigSGIX = epoxy_glXQueryHyperpipeConfigSGIX_global_rewrite_ptr;

PFNGLXQUERYHYPERPIPENETWORKSGIXPROC epoxy_glXQueryHyperpipeNetworkSGIX = epoxy_glXQueryHyperpipeNetworkSGIX_global_rewrite_ptr;

PFNGLXQUERYMAXSWAPBARRIERSSGIXPROC epoxy_glXQueryMaxSwapBarriersSGIX = epoxy_glXQueryMaxSwapBarriersSGIX_global_rewrite_ptr;

PFNGLXQUERYMAXSWAPGROUPSNVPROC epoxy_glXQueryMaxSwapGroupsNV = epoxy_glXQueryMaxSwapGroupsNV_global_rewrite_ptr;

PFNGLXQUERYRENDERERINTEGERMESAPROC epoxy_glXQueryRendererIntegerMESA = epoxy_glXQueryRendererIntegerMESA_global_rewrite_ptr;

PFNGLXQUERYRENDERERSTRINGMESAPROC epoxy_glXQueryRendererStringMESA = epoxy_glXQueryRendererStringMESA_global_rewrite_ptr;

PFNGLXQUERYSERVERSTRINGPROC epoxy_glXQueryServerString = epoxy_glXQueryServerString_global_rewrite_ptr;

PFNGLXQUERYSWAPGROUPNVPROC epoxy_glXQuerySwapGroupNV = epoxy_glXQuerySwapGroupNV_global_rewrite_ptr;

PFNGLXQUERYVERSIONPROC epoxy_glXQueryVersion = epoxy_glXQueryVersion_global_rewrite_ptr;

PFNGLXQUERYVIDEOCAPTUREDEVICENVPROC epoxy_glXQueryVideoCaptureDeviceNV = epoxy_glXQueryVideoCaptureDeviceNV_global_rewrite_ptr;

PFNGLXRELEASEBUFFERSMESAPROC epoxy_glXReleaseBuffersMESA = epoxy_glXReleaseBuffersMESA_global_rewrite_ptr;

PFNGLXRELEASETEXIMAGEEXTPROC epoxy_glXReleaseTexImageEXT = epoxy_glXReleaseTexImageEXT_global_rewrite_ptr;

PFNGLXRELEASEVIDEOCAPTUREDEVICENVPROC epoxy_glXReleaseVideoCaptureDeviceNV = epoxy_glXReleaseVideoCaptureDeviceNV_global_rewrite_ptr;

PFNGLXRELEASEVIDEODEVICENVPROC epoxy_glXReleaseVideoDeviceNV = epoxy_glXReleaseVideoDeviceNV_global_rewrite_ptr;

PFNGLXRELEASEVIDEOIMAGENVPROC epoxy_glXReleaseVideoImageNV = epoxy_glXReleaseVideoImageNV_global_rewrite_ptr;

PFNGLXRESETFRAMECOUNTNVPROC epoxy_glXResetFrameCountNV = epoxy_glXResetFrameCountNV_global_rewrite_ptr;

PFNGLXSELECTEVENTPROC epoxy_glXSelectEvent = epoxy_glXSelectEvent_global_rewrite_ptr;

PFNGLXSELECTEVENTSGIXPROC epoxy_glXSelectEventSGIX = epoxy_glXSelectEventSGIX_global_rewrite_ptr;

PFNGLXSENDPBUFFERTOVIDEONVPROC epoxy_glXSendPbufferToVideoNV = epoxy_glXSendPbufferToVideoNV_global_rewrite_ptr;

PFNGLXSET3DFXMODEMESAPROC epoxy_glXSet3DfxModeMESA = epoxy_glXSet3DfxModeMESA_global_rewrite_ptr;

PFNGLXSWAPBUFFERSPROC epoxy_glXSwapBuffers = epoxy_glXSwapBuffers_global_rewrite_ptr;

PFNGLXSWAPBUFFERSMSCOMLPROC epoxy_glXSwapBuffersMscOML = epoxy_glXSwapBuffersMscOML_global_rewrite_ptr;

PFNGLXSWAPINTERVALEXTPROC epoxy_glXSwapIntervalEXT = epoxy_glXSwapIntervalEXT_global_rewrite_ptr;

PFNGLXSWAPINTERVALMESAPROC epoxy_glXSwapIntervalMESA = epoxy_glXSwapIntervalMESA_global_rewrite_ptr;

PFNGLXSWAPINTERVALSGIPROC epoxy_glXSwapIntervalSGI = epoxy_glXSwapIntervalSGI_global_rewrite_ptr;

PFNGLXUSEXFONTPROC epoxy_glXUseXFont = epoxy_glXUseXFont_global_rewrite_ptr;

PFNGLXWAITFORMSCOMLPROC epoxy_glXWaitForMscOML = epoxy_glXWaitForMscOML_global_rewrite_ptr;

PFNGLXWAITFORSBCOMLPROC epoxy_glXWaitForSbcOML = epoxy_glXWaitForSbcOML_global_rewrite_ptr;

PFNGLXWAITGLPROC epoxy_glXWaitGL = epoxy_glXWaitGL_global_rewrite_ptr;

PFNGLXWAITVIDEOSYNCSGIPROC epoxy_glXWaitVideoSyncSGI = epoxy_glXWaitVideoSyncSGI_global_rewrite_ptr;

PFNGLXWAITXPROC epoxy_glXWaitX = epoxy_glXWaitX_global_rewrite_ptr;

