# Sets the minimum version of CMake required to build your native library.
# This ensures that a certain set of CMake features is available to
# your build.

cmake_minimum_required(VERSION 3.4.1)

# Specifies a library name, specifies whether the library is STATIC or
# SHARED, and provides relative paths to the source code. You can
# define multiple libraries by adding multiple add_library() commands,
# and CMake builds them for you. When you build your app, Gradle
# automatically packages shared libraries with your APK.

#add_library( # Specifies the name of the library.
#        ubt-helpers
#
#        # Sets the library as a shared library.
#        SHARED
#
#        # Provides a relative path to your source file(s).
#        src/main/cpp/ubt-helpers.cpp )
#
#add_library(fs-helpers SHARED src/main/cpp/fs-helpers.cpp)
#
#add_library(axs-helpers SHARED src/main/cpp/axs-helpers.cpp)
#add_library(ipc-helpers SHARED src/main/cpp/ipc-helpers.cpp)
#add_library(xconnector-fairepoll SHARED src/main/cpp/xconnector-fairepoll.cpp)
add_library(Xegw SHARED src/main/cpp/Xegw.cpp)
# Specifies a path to native header files.
include_directories(src/main/cpp/)

target_link_libraries(Xegw "-llog")




# 如果重命名或移除了 CMake build 脚本中的库，
# 您需要在 Gradle 应用相关更改或从 APK 中移除旧版库之前清理您的项目。
# 如需清理项目，请在菜单栏中依次选择 Build > Clean Project。