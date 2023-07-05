//
// Created by localAccount on 2023/4/16.
//

#include <stdio.h>
#include <stdlib.h>
#include <pthread.h>
#include <android/log.h>
#include <unistd.h>
#include <jni.h>
#include <string.h>

char *envp=NULL;
void *pulseThread(void *param)
{
    char pulseCmd[PATH_MAX * 7] = "";
    //logwrapper是安卓日志相关
    sprintf(pulseCmd, "HOME=%s TMPDIR=%s LD_LIBRARY_PATH=%s "
//                      "logwrapper "
                      "%s/pulseaudio --disable-shm -n -F %s/pulseaudio.conf "
                      "--dl-search-path=%s --daemonize=false --use-pid-file=false "
                      "--log-target=stderr --log-level=debug",
            envp, envp,envp, envp,envp, envp);
    //为什么会有不停循环(可能执行报错就退出了吧）
//    while( 1 )
//    {
        __android_log_print(ANDROID_LOG_INFO, "Pulseaudio", "Starting Pulseaudio");
        __android_log_print(ANDROID_LOG_INFO, "Pulseaudio", "%s", pulseCmd);
        //在Linux/Unix系统中，system函数会调用fork函数产生子进程，由子进程来执行command命令，命令执行完后随即返回原调用的进程
        system(pulseCmd);
        sleep(5);
//        free(envp); //释放内存？
//    }

    return NULL;
}

//static void initPulseAudioConfig()
//{
//    char cmd[PATH_MAX * 4];
//    sprintf(cmd, "%s/busybox sed -i s@/data/local/tmp@%s/pulse@g %s/pulse/pulseaudio.conf", getenv("SECURE_STORAGE_DIR"), getenv("SECURE_STORAGE_DIR"), getenv("SECURE_STORAGE_DIR"));
//    printf("Fixing up PulseAudio config file");
//    printf("%s", cmd);
//    system(cmd);
//    sprintf(cmd, "rm %s/pulse/audio-out", getenv("SECURE_STORAGE_DIR"));
//    printf("%s", cmd);
//    system(cmd);
//}
//static void launchPulseAudio()
//{
//    char cmd[PATH_MAX * 6];
//    sprintf(cmd,
//            "cd %s/pulse ; while true ; do "
//            "rm -f audio-out ; "
//            "HOME=%s/pulse "
//            "TMPDIR=%s/pulse "
//            "LD_LIBRARY_PATH=%s/pulse "
//            "./pulseaudio --disable-shm -n -F pulseaudio.conf "
//            "--dl-search-path=%s/pulse "
//            "--daemonize=false --use-pid-file=false "
//            "--log-target=stderr --log-level=notice 2>&1 ; "
//            "sleep 1 ; "
//            "done",
//            getenv("SECURE_STORAGE_DIR"), getenv("SECURE_STORAGE_DIR"), getenv("SECURE_STORAGE_DIR"), getenv("SECURE_STORAGE_DIR"), getenv("SECURE_STORAGE_DIR"));
//    printf("Launching PulseAudio daemon:");
//    printf("%s", cmd);
//    executeBackground(cmd);
//    printf("Launching PulseAudio daemon done");
//    //system(cmd);
//}

/**
 * 先调用setEnv设置路径，再调用这个函数启动pulseaudio
 * @param env
 * @param thiz
 * @return
 */
JNIEXPORT jint JNICALL
Java_com_example_datainsert_exagear_FAB_dialogfragment_AboutFab_startPulseaudio(JNIEnv *env,
                                                                                jobject thiz) {


    //重定向输出
//    freopen("/sdcard/palog.txt", "w", stdout);
    freopen("/sdcard/palog.txt", "w", stderr);

    //测试一下重定向是否成功
//    system("logcat --help");
//    sleep(5);
//    fflush(stdout);
//    fflush(stderr);

        pthread_t threadId;
    return pthread_create(&threadId, NULL, &pulseThread, NULL);
//    __android_log_print(ANDROID_LOG_INFO, "Pulseaudio", "测试logwrapper");
    return 1;
}


JNIEXPORT void JNICALL
Java_com_example_datainsert_exagear_FAB_dialogfragment_AboutFab_setEnv(JNIEnv *env, jobject thiz,
                                                                       jstring s) {

    //初始 envp打印出来是null，*envp打印直接报错
    if(envp)
        return;

    const char *str = (*env)->GetStringUTFChars(env, s, 0);
    size_t len = strlen(str)+1;
    envp = malloc(len*sizeof(char));//可变长度数组。。对应free()释放内存
    strcpy(envp,str);
    __android_log_print(ANDROID_LOG_INFO, "Pulseaudio", "设置pulseaudio寻找路径=%s", envp);

    (*env)->ReleaseStringUTFChars(env, s, str); //get完记得release
}

JNIEXPORT void JNICALL
Java_com_example_datainsert_exagear_application_MyApplication_startPulseaudio(JNIEnv *env,
                                                                              jobject thiz) {
    // TODO: implement startPulseaudio()
    Java_com_example_datainsert_exagear_FAB_dialogfragment_AboutFab_startPulseaudio(env,thiz);
}

JNIEXPORT void JNICALL
Java_com_example_datainsert_exagear_application_MyApplication_setEnv(JNIEnv *env, jobject thiz,
                                                                     jstring s) {
    // TODO: implement setEnv()
    Java_com_example_datainsert_exagear_FAB_dialogfragment_AboutFab_setEnv(env,thiz,s);
}