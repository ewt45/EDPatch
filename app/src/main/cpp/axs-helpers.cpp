//
// Created by localAccount on 2023/3/7.
//

#include "axs-helpers.h"
#include <jni.h>

extern "C"
JNIEXPORT void JNICALL
Java_com_eltechs_axs_xserver_impl_drawables_bitmapBacked_PainterOnBitmap_readBitmap(JNIEnv *env,
                                                                                    jobject thiz,
                                                                                    jobject byte_buffer,
                                                                                    jint i, jint i2,
                                                                                    jint i3,
                                                                                    jint i4,
                                                                                    jint i5,
                                                                                    jintArray i_arr) {
    // TODO: implement readBitmap()
}
extern "C"
JNIEXPORT void JNICALL
Java_com_eltechs_axs_xserver_impl_drawables_bitmapBacked_PainterOnBitmap_readZPixmap24(JNIEnv *env,
                                                                                       jobject thiz,
                                                                                       jobject byte_buffer,
                                                                                       jint i,
                                                                                       jint i2,
                                                                                       jint i3,
                                                                                       jint i4,
                                                                                       jint i5,
                                                                                       jintArray i_arr) {
    // TODO: implement readZPixmap24()
}
extern "C"
JNIEXPORT void JNICALL
Java_com_eltechs_axs_graphicsScene_SceneOfRectangles_moveRectangleImpl(JNIEnv *env, jobject thiz,
                                                                       jint i, jfloat f, jfloat f2,
                                                                       jfloat f3, jfloat f4,
                                                                       jfloat f5) {
    // TODO: implement moveRectangleImpl()
}
extern "C"
JNIEXPORT void JNICALL
Java_com_eltechs_axs_graphicsScene_SceneOfRectangles_placeRectangleImpl(JNIEnv *env, jobject thiz,
                                                                        jint i, jfloat f, jfloat f2,
                                                                        jfloat f3, jfloat f4,
                                                                        jfloat f5, jint i2,
                                                                        jfloat f6, jfloat f7,
                                                                        jfloat f8, jboolean z) {
    // TODO: implement placeRectangleImpl()
}
extern "C"
JNIEXPORT jboolean JNICALL
Java_com_eltechs_axs_graphicsScene_SceneOfRectangles_initialiseNativeParts(JNIEnv *env,
                                                                           jclass clazz) {
    // TODO: implement initialiseNativeParts()
    return 0;
}
extern "C"
JNIEXPORT void JNICALL
Java_com_eltechs_axs_graphicsScene_SceneOfRectangles_draw(JNIEnv *env, jobject thiz) {
    // TODO: implement draw()
}
extern "C"
JNIEXPORT void JNICALL
Java_com_eltechs_axs_graphicsScene_SceneOfRectangles_setMVPMatrix(JNIEnv *env, jobject thiz,
                                                                  jfloatArray f_arr) {
    // TODO: implement setMVPMatrix()
}
extern "C"
JNIEXPORT void JNICALL
Java_com_eltechs_axs_graphicsScene_SceneOfRectangles_freeNativeSceneData(JNIEnv *env,
                                                                         jobject thiz) {
    // TODO: implement freeNativeSceneData()
}
extern "C"
JNIEXPORT void JNICALL
Java_com_eltechs_axs_graphicsScene_SceneOfRectangles_allocateNativeSceneData(JNIEnv *env,
                                                                             jobject thiz, jint i) {
    // TODO: implement allocateNativeSceneData()
}
