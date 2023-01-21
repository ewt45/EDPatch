# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.

cmake_minimum_required(VERSION 3.10.2)

# Declares and names the project.

#project("virgloverlay")

# Creates and names a library, sets it as either STATIC
# or SHARED, and provides the relative paths to its source code.
# You can define multiple libraries, and CMake builds them for you.
# Gradle automatically packages shared libraries with your APK.

add_definitions(
        -D__aeabi_idiv
        -Dipc_perm=debian_ipc_perm
        -lm -Wl,--no-undefined -landroid -llog
        )

add_library( # Sets the name of the library.
        epoxy-lib

        # Sets the library as a shared library.
        SHARED

        # Provides a relative path to your source file(s).

        src/dispatch_common.c
        src/dispatch_egl.c
        include/epoxy/egl_generated_dispatch.c
        include/epoxy/gl_generated_dispatch.c

        )

target_include_directories(
        epoxy-lib

        PUBLIC include
        PUBLIC include/epoxy

        PRIVATE src
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

# Specifies libraries CMake should link to your target library. You
# can link multiple libraries, such as libraries you define in this
# build script, prebuilt third-party libraries, or system libraries.

target_link_libraries( # Specifies the target library.
                       epoxy-lib
                       -landroid
                       -lgcc

                       # Links the target library to the log library
                       # included in the NDK.
                       ${log-lib} )