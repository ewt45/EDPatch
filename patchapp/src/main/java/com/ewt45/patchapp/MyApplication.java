package com.ewt45.patchapp;

import static android.os.Environment.DIRECTORY_DOWNLOADS;
import static android.os.Environment.getExternalStoragePublicDirectory;

import android.app.Application;
import android.content.Context;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.ewt45.patchapp.model.FragmentData;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

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
            String logFilePath = getExternalFilesDir(null) + "/crash-logInfo/crash-%s.log";
            Thread.setDefaultUncaughtExceptionHandler(new CrashExceptionHandler(logFilePath));
        }

    }

    /**
     * 系统处理异常类，处理整个APP的异常
     */
    public static class CrashExceptionHandler implements Thread.UncaughtExceptionHandler{
        private final String mLogFilePath;

        public CrashExceptionHandler(String logFilePath){
            this.mLogFilePath = logFilePath;
        }

        @Override
        public void uncaughtException(@NonNull Thread t, @NonNull Throwable e) {
            //显示toast
            new Thread(()->{
                Looper.prepare();
                // 不能使用这个ToastUtils.show()，不能即时的提示，会因为异常出现问题
                //            ToastUtils.show("很抱歉，程序出现异常，即将退出！");
                //            MyToast.toast(mContext, "很抱歉,程序出现异常,即将退出.", Toast.LENGTH_LONG);
                Toast.makeText(MyApplication.i, "很抱歉，程序出现异常，即将退出", Toast.LENGTH_SHORT).show();
                Looper.loop();
            }).start();

            //写入文件
            File fl = new File(String.format(mLogFilePath, System.currentTimeMillis()));
            if(!fl.getParentFile().exists()) fl.mkdirs();

            try (StringWriter strWriter = new StringWriter();
                 PrintWriter writer = new PrintWriter(strWriter);
                 FileOutputStream fos = new FileOutputStream(fl)) {
                e.printStackTrace(writer);
                strWriter.flush();
                fos.write(strWriter.toString().getBytes());
            } catch (IOException e2) {
                e2.printStackTrace();
            }

            // 睡眠3s主要是为了下面的Toast能够显示出来，否则，Toast是没有机会显示的
            try {
                Thread.sleep(3000);
            } catch (Exception exception) {
                exception.printStackTrace();
            }

            //退出程序
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);

        }
    }
}
