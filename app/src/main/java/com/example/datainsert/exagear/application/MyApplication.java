package com.example.datainsert.exagear.application;

import android.app.Application;

public class MyApplication extends Application {
    boolean logCrash=false;
    @Override
    public void onCreate() {
        super.onCreate();

       if(logCrash){
           // 注册全局异常处理类
           CrashExceptionHandler crashExceptionHandler = CrashExceptionHandler.newInstance();
           crashExceptionHandler.init(getApplicationContext());
       }

    }

}
