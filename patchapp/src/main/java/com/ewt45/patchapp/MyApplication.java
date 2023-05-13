package com.ewt45.patchapp;

import android.app.Application;
import android.content.Context;

import java.io.File;
import java.util.Objects;

public class MyApplication extends Application {
    public static final String PREFERENCE = "config";
    public static final String KEY_LAST_LAUNCH_TIME = "lastLaunchTime";

    public static MyApplication instance;

    boolean logCrash=true;
    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
//        getApplicationInfo().nativeLibraryDir = Objects.requireNonNull(getApplicationContext().getExternalFilesDir(null)).getAbsolutePath();

        //记录本次应用启动时间。如果立刻闪退，就清除数据
        getApplicationContext().getSharedPreferences(PREFERENCE, Context.MODE_PRIVATE)
                .edit().putLong(KEY_LAST_LAUNCH_TIME,System.currentTimeMillis()).commit();


        if(logCrash){
            // 注册全局异常处理类
            CrashExceptionHandler crashExceptionHandler = CrashExceptionHandler.newInstance();
            crashExceptionHandler.init(getApplicationContext());
        }

    }

}
