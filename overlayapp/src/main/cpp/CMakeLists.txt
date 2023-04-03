# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.

cmake_minimum_required(VERSION 3.10.2)

# Declares and names the project.

project("virgloverlay")

# Creates and names a library, sets it as either STATIC
# or SHARED, and provides the relative paths to its source code.
# You can define multiple libraries, and CMake builds them for you.
# Gradle automatically packages shared libraries with your APK.

add_definitions(
        #-DENABLE_TRACING=TRACE_WITH_STDERR
        -DUTIL_ARCH_LITTLE_ENDIAN=0
        -DUTIL_ARCH_BIG_ENDIAN=1
        -DNO_VKR
        -DNO_GBM
        -DNO_DRM
        -DHAVE_PTHREAD
        -DPIPE_OS_ANDROID
        -DVIRGL_RENDERER_UNSTABLE_APIS
        -DHAVE_CONFIG_H
        -DHAVE_SYS_UIO_H
        -DANDROID_JNI
        -DANDROID_ABI=armeabi-v7a
        -DANDROID_ARM_NEON=ON
        -D_LINUX_IPC_H
        -D__aeabi_idiv
        -Dipc_perm=debian_ipc_perm
        -lm -Wl,--no-undefined -landroid -llog
        )

add_library( # Sets the name of the library.
             virgl-lib

             # Sets the library as a shared library.
             SHARED

        # Provides a relative path to your source file(s).

        virgl

        virgl/src

        virgl/src/iov.c
        virgl/src/virgl_context.c
        virgl/src/virgl_resource.c
        virgl/src/virgl_util.c
        #virgl/src/virgl_video.c
        virgl/src/virglrenderer.c
        virgl/src/vrend_blitter.c
        virgl/src/vrend_debug.c
        virgl/src/vrend_decode.c
        virgl/src/vrend_formats.c
        virgl/src/vrend_object.c
        virgl/src/vrend_renderer.c
        virgl/src/vrend_shader.c
        virgl/src/vrend_tweaks.c
        #virgl/src/vrend_video.c
        virgl/src/vrend_winsys.c

        virgl/src/gallium/auxiliary/cso_cache/cso_cache.c
        virgl/src/gallium/auxiliary/cso_cache/cso_hash.c

        virgl/src/gallium/auxiliary/tgsi/tgsi_build.c
        virgl/src/gallium/auxiliary/tgsi/tgsi_dump.c
        virgl/src/gallium/auxiliary/tgsi/tgsi_info.c
        virgl/src/gallium/auxiliary/tgsi/tgsi_iterate.c
        virgl/src/gallium/auxiliary/tgsi/tgsi_parse.c
        virgl/src/gallium/auxiliary/tgsi/tgsi_sanity.c
        virgl/src/gallium/auxiliary/tgsi/tgsi_scan.c
        virgl/src/gallium/auxiliary/tgsi/tgsi_strings.c
        virgl/src/gallium/auxiliary/tgsi/tgsi_text.c
        virgl/src/gallium/auxiliary/tgsi/tgsi_util.c

        virgl/src/gallium/auxiliary/util/u_debug_describe.c
        virgl/src/gallium/auxiliary/util/u_format.c
        virgl/src/gallium/auxiliary/util/u_format_table.c
        virgl/src/gallium/auxiliary/util/u_hash_table.c
        virgl/src/gallium/auxiliary/util/u_texture.c

        virgl/src/mesa/util/anon_file.c
        virgl/src/mesa/util/bitscan.c
        virgl/src/mesa/util/hash_table.c
        virgl/src/mesa/util/os_file.c
        virgl/src/mesa/util/os_misc.c
        virgl/src/mesa/util/ralloc.c
        virgl/src/mesa/util/u_cpu_detect.c
        virgl/src/mesa/util/u_debug.c
        virgl/src/mesa/util/u_math.c

        virgl/vtest/util.c
        virgl/vtest/vtest_renderer.c
        virgl/vtest/vtest_server.c
        virgl/vtest/vtest_shm.c

        virgl/vtest_old/vtest_renderer_old.c
        virgl/vtest_old/vtest_server_old.c

        virgl/gl4es/decompress.c

        )

target_include_directories(
        virgl-lib

        PUBLIC epoxy/include

        PUBLIC virgl/gl4es

        PRIVATE virgl/src
        PRIVATE virgl/src/gallium/auxiliary
        PRIVATE virgl/src/gallium/auxiliary/cso_cache
        PRIVATE virgl/src/gallium/auxiliary/tgsi
        PRIVATE virgl/src/gallium/auxiliary/util
        PRIVATE virgl/src/gallium/include
        PRIVATE virgl/src/gallium/include/pipe
        PRIVATE virgl/src/mesa
        PRIVATE virgl/src/mesa/compat
        PRIVATE virgl/src/mesa/compat/c11
        PRIVATE virgl/src/mesa/pipe
        PRIVATE virgl/src/mesa/util
        PRIVATE virgl/vtest
        PRIVATE virgl/vtest_old
        PRIVATE virgl/src/gl4es/
)

# Searches for a specified prebuilt library and stores the path as a
# variable. Because CMake includes system libraries in the search path by
# default, you only need to specify the name of the public NDK library
# you want to add. CMake verifies that the library exists before
# completing its build.

find_library( # Sets the name of the path variable.
              log-lib

              # Specifies the name of the NDK library that
              # you want CMake to locate.
              log
              lgcc
            )

add_subdirectory(
        epoxy
)

# Specifies libraries CMake should link to your target library. You
# can link multiple libraries, such as libraries you define in this
# build script, prebuilt third-party libraries, or system libraries.

target_link_libraries( # Specifies the target library.
                       virgl-lib
                       epoxy-lib
                       -ljnigraphics
                       -landroid
                       -lgcc

                       # Links the target library to the log library
                       # included in the NDK.
                       ${log-lib}
        )