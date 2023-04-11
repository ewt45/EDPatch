#include <jni.h>
#include <android/log.h>
//
// Created by localAccount on 2023/4/4.
//

extern "C"
JNIEXPORT void JNICALL
Java_com_eltechs_axs_xserver_RealXServer_start(JNIEnv *env, jclass clazz) {
    // TODO: implement start()
}
extern "C"
JNIEXPORT void JNICALL
Java_com_eltechs_axs_xserver_RealXServer_windowChanged(JNIEnv *env, jclass clazz, jobject surface,
                                                       jint width, jint height) {
    // TODO: implement windowChanged()
    __android_log_print(ANDROID_LOG_DEBUG, "MD_DEBUG", "windowChanged");

}
extern "C"
JNIEXPORT void JNICALL
Java_com_eltechs_axs_xserver_RealXServer_keySym(JNIEnv *env, jclass clazz,jint keycode,jint keysym, jint state) {
    // TODO: implement keySym()
}
extern "C"
JNIEXPORT void JNICALL
Java_com_eltechs_axs_xserver_RealXServer_key(JNIEnv *env, jclass clazz, jint key, jint state) {
    // TODO: implement key()
}
extern "C"
JNIEXPORT void JNICALL
Java_com_eltechs_axs_xserver_RealXServer_click(JNIEnv *env, jclass clazz, jint button, jint state) {
    // TODO: implement click()
}
extern "C"
JNIEXPORT void JNICALL
Java_com_eltechs_axs_xserver_RealXServer_motion(JNIEnv *env, jclass clazz, jint x, jint y) {
    // TODO: implement motion()
}
extern "C"
JNIEXPORT void JNICALL
Java_com_eltechs_axs_xserver_RealXServer_scroll(JNIEnv *env, jclass clazz, jint axis, jint value) {
    // TODO: implement scroll()
}