//
// Created by localAccount on 2023/2/17.
//

#include "ubt-helpers.h"
#include <jni.h>

extern "C"
JNIEXPORT void JNICALL
Java_com_eltechs_axs_guestApplicationsTracker_impl_ProcessHelpers_sendSignal(JNIEnv *env,
                                                                             jclass clazz, jint i,
                                                                             jint i2) {
    // TODO: implement sendSignal()
}
extern "C"
JNIEXPORT jboolean JNICALL
Java_com_eltechs_axs_helpers_EnvironmentInfoHelpers_isCpuFeaturesOk(JNIEnv *env, jclass clazz,
                                                                    jboolean z) {
    // TODO: implement isCpuFeaturesOk()
    return 0;
}
extern "C"
JNIEXPORT jboolean JNICALL
Java_com_eltechs_axs_helpers_EnvironmentInfoHelpers_runNativeProgram(JNIEnv *env, jclass clazz,
                                                                     jstring str, jstring str2) {
    // TODO: implement runNativeProgram()
    return 0;
}