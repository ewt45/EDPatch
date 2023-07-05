package com.example.datainsert.exagear.application;

import android.app.Application;

import com.eltechs.axs.Globals;

import java.io.File;

public class MyApplication extends Application {
    boolean logCrash=true;
    @Override
    public void onCreate() {
        super.onCreate();

       if(logCrash){
           // 注册全局异常处理类
           CrashExceptionHandler crashExceptionHandler = CrashExceptionHandler.newInstance();
           crashExceptionHandler.init(getApplicationContext());
       }
//
//        File paDir = new File(getApplicationContext().getFilesDir(), "pulseaudio-xsdl");
//        setEnv(paDir.getAbsolutePath());
//        startPulseaudio();

    }

    private native void startPulseaudio();
    private native  void setEnv(String s);
    static {
        System.loadLibrary("some-helper");
    }

}
