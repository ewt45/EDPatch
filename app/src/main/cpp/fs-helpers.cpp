//
// Created by localAccount on 2023/3/4.
//

#include "fs-helpers.h"
#include <jni.h>

extern "C"
JNIEXPORT jboolean JNICALL
Java_com_eltechs_axs_helpers_SafeFileHelpers_exists(JNIEnv *env, jclass clazz, jstring str) {
    // TODO: implement exists()
    return 0;
}
extern "C"
JNIEXPORT jboolean JNICALL
Java_com_eltechs_axs_helpers_SafeFileHelpers_initialiseNativeParts(JNIEnv *env, jclass clazz) {
    // TODO: implement initialiseNativeParts()
    return 0;
}
extern "C"
JNIEXPORT jboolean JNICALL
Java_com_eltechs_axs_helpers_SafeFileHelpers_isDirectory(JNIEnv *env, jclass clazz, jstring str) {
    // TODO: implement isDirectory()
    return 0;
}
extern "C"
JNIEXPORT jboolean JNICALL
Java_com_eltechs_axs_helpers_SafeFileHelpers_isSymlink(JNIEnv *env, jclass clazz, jstring str) {
    // TODO: implement isSymlink()
    return 0;
}
extern "C"
JNIEXPORT jobjectArray JNICALL
Java_com_eltechs_axs_helpers_SafeFileHelpers_listWellNamedFilesImpl(JNIEnv *env, jclass clazz,
                                                                    jstring str) {
    // TODO: implement listWellNamedFilesImpl()
    return nullptr;
}
extern "C"
JNIEXPORT jint JNICALL
Java_com_eltechs_axs_helpers_SafeFileHelpers_removeDirectoryImpl(JNIEnv *env, jclass clazz,
                                                                 jstring str, jboolean z) {
    // TODO: implement removeDirectoryImpl()
    return 0;
}
extern "C"
JNIEXPORT jint JNICALL
Java_com_eltechs_axs_helpers_SafeFileHelpers_symlink(JNIEnv *env, jclass clazz, jstring str,
                                                     jstring str2) {
    // TODO: implement symlink()
    return 0;
}