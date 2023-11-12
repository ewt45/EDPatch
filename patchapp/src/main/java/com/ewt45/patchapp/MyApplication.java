package com.ewt45.patchapp;

import android.app.Application;
import android.content.Context;

import com.ewt45.patchapp.model.FragmentData;

public class MyApplication extends Application {
    public static final String PREFERENCE = "config";
    public static final String KEY_LAST_LAUNCH_TIME = "lastLaunchTime";

    public static MyApplication i;
    /**
     * fragment来回跳转不好存数据，数据统一放到这里吧
     */
    public static FragmentData data;

    boolean logCrash=true;
    @Override
    public void onCreate() {
        super.onCreate();
        i = this;
        data = new FragmentData();
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
