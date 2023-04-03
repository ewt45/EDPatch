//
// Created by localAccount on 2023/4/1.
//

#include "xconnector-fairepoll.h"
#include <jni.h>

extern "C"
JNIEXPORT jboolean JNICALL
Java_com_eltechs_axs_xconnectors_epoll_impl_EpollProcessorThread_initialiseNativeParts(JNIEnv *env,
                                                                                       jclass clazz) {
    // TODO: implement initialiseNativeParts()
    return 0;
}
extern "C"
JNIEXPORT jint JNICALL
Java_com_eltechs_axs_xconnectors_epoll_impl_EpollProcessorThread_doEpoll(JNIEnv *env, jobject thiz,
                                                                         jint i) {
    // TODO: implement doEpoll()
    return 0;
}
extern "C"
JNIEXPORT jint JNICALL
Java_com_eltechs_axs_xconnectors_epoll_impl_EpollProcessorThread_addServerSocketToEpoll(JNIEnv *env,
                                                                                        jobject thiz,
                                                                                        jint i) {
    // TODO: implement addServerSocketToEpoll()
    return 0;
}
extern "C"
JNIEXPORT jint JNICALL
Java_com_eltechs_axs_xconnectors_epoll_impl_EpollProcessorThread_addShutdownRequestFdToEpoll(
        JNIEnv *env, jobject thiz, jint i) {
    // TODO: implement addShutdownRequestFdToEpoll()
    return 0;
}
extern "C"
JNIEXPORT void JNICALL
Java_com_eltechs_axs_xconnectors_epoll_impl_EpollProcessorThread_closeEpollFd(JNIEnv *env,
                                                                              jobject thiz) {
    // TODO: implement closeEpollFd()
}
extern "C"
JNIEXPORT void JNICALL
Java_com_eltechs_axs_xconnectors_epoll_impl_EpollProcessorThread_closeShutdownRequestFd(JNIEnv *env,
                                                                                        jobject thiz) {
    // TODO: implement closeShutdownRequestFd()
}
extern "C"
JNIEXPORT jint JNICALL
Java_com_eltechs_axs_xconnectors_epoll_impl_EpollProcessorThread_createEpollFd(JNIEnv *env,
                                                                               jobject thiz) {
    // TODO: implement createEpollFd()
    return 0;
}
extern "C"
JNIEXPORT jlong JNICALL
Java_com_eltechs_axs_xconnectors_epoll_impl_EpollProcessorThread_createFdToClientMap(JNIEnv *env,
                                                                                     jobject thiz) {
    // TODO: implement createFdToClientMap()
    return 0;
}
extern "C"
JNIEXPORT jint JNICALL
Java_com_eltechs_axs_xconnectors_epoll_impl_EpollProcessorThread_createShutdownRequestFd(
        JNIEnv *env, jobject thiz) {
    // TODO: implement createShutdownRequestFd()
    return 0;
}
extern "C"
JNIEXPORT void JNICALL
Java_com_eltechs_axs_xconnectors_epoll_impl_EpollProcessorThread_destroyFdToClientMapAndKillConnections(
        JNIEnv *env, jobject thiz) {
    // TODO: implement destroyFdToClientMapAndKillConnections()
}
extern "C"
JNIEXPORT jint JNICALL
Java_com_eltechs_axs_xconnectors_epoll_impl_EpollProcessorThread_pollForRead(JNIEnv *env,
                                                                             jobject thiz, jint i,
                                                                             jobject client) {
    // TODO: implement pollForRead()
    return 0;
}
extern "C"
JNIEXPORT jint JNICALL
Java_com_eltechs_axs_xconnectors_epoll_impl_EpollProcessorThread_pollForWrite(JNIEnv *env,
                                                                              jobject thiz, jint i,
                                                                              jobject client,
                                                                              jboolean z) {
    // TODO: implement pollForWrite()
    return 0;
}
extern "C"
JNIEXPORT jint JNICALL
Java_com_eltechs_axs_xconnectors_epoll_impl_EpollProcessorThread_removeFromPoll(JNIEnv *env,
                                                                                jobject thiz,
                                                                                jint i) {
    // TODO: implement removeFromPoll()
    return 0;
}
extern "C"
JNIEXPORT void JNICALL
Java_com_eltechs_axs_xconnectors_epoll_impl_EpollProcessorThread_requestShutdown(JNIEnv *env,
                                                                                 jobject thiz) {
    // TODO: implement requestShutdown()
}
extern "C"
JNIEXPORT jboolean JNICALL
Java_com_eltechs_axs_xconnectors_epoll_impl_ConnectionListener_initialiseNativeParts(JNIEnv *env,
                                                                                     jclass clazz) {
    // TODO: implement initialiseNativeParts()
    return 0;
}
extern "C"
JNIEXPORT jint JNICALL
Java_com_eltechs_axs_xconnectors_epoll_impl_ConnectionListener_createLoopbackInetSocket(JNIEnv *env,
                                                                                        jclass clazz,
                                                                                        jint i) {
    // TODO: implement createLoopbackInetSocket()
    return 0;
}
extern "C"
JNIEXPORT jint JNICALL
Java_com_eltechs_axs_xconnectors_epoll_impl_ConnectionListener_createAfUnixSocket(JNIEnv *env,
                                                                                  jclass clazz,
                                                                                  jstring str) {
    // TODO: implement createAfUnixSocket()
    return 0;
}
extern "C"
JNIEXPORT jint JNICALL
Java_com_eltechs_axs_xconnectors_epoll_impl_ConnectionListener_createAbstractAfUnixSocket(
        JNIEnv *env, jclass clazz, jstring str) {
    // TODO: implement createAbstractAfUnixSocket()
    return 0;
}
extern "C"
JNIEXPORT void JNICALL
Java_com_eltechs_axs_xconnectors_epoll_impl_ConnectionListener_closeImpl(JNIEnv *env,
                                                                         jobject thiz) {
    // TODO: implement closeImpl()
}
extern "C"
JNIEXPORT jint JNICALL
Java_com_eltechs_axs_xconnectors_epoll_impl_ConnectionListener_acceptImpl(JNIEnv *env,
                                                                          jobject thiz) {
    // TODO: implement acceptImpl()
    return 0;
}