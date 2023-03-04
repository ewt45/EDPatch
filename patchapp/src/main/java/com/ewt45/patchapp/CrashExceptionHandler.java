package com.ewt45.patchapp;


import static com.ewt45.patchapp.MyApplication.KEY_LAST_LAUNCH_TIME;
import static com.ewt45.patchapp.MyApplication.PREFERENCE;

import android.content.Context;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

/**
 * 系统处理异常类，处理整个APP的异常
 */
public class CrashExceptionHandler implements Thread.UncaughtExceptionHandler{

    private Context mContext;

    // 本类实例
    private static CrashExceptionHandler myCrashHandler;

    // 系统默认的UncaughtExceptionHandler
    private Thread.UncaughtExceptionHandler mDefaultException;

    // 保证只有一个实例
    public CrashExceptionHandler() {
    }

    // 单例模式
    public synchronized static CrashExceptionHandler newInstance(){
        if (myCrashHandler == null){
            myCrashHandler = new CrashExceptionHandler();
        }
        return myCrashHandler;
    }


    /**
     * 初始化
     * @param context
     */
    public void init(Context context){
        this.mContext = context;
        // 系统默认处理类
        this.mDefaultException = Thread.getDefaultUncaughtExceptionHandler();
        // 将该类设置为系统默认处理类
        Thread.setDefaultUncaughtExceptionHandler(this);
    }


    @Override
    public void uncaughtException(@NonNull Thread t, @NonNull Throwable e) {
        if(!handleExample(e) && mDefaultException != null) { //判断异常是否已经被处理
            mDefaultException.uncaughtException(t, e);
        }else {
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

    /**
     * 提示用户出现异常，将异常信息保存/上传
     * @param ex
     * @return
     */
    private boolean handleExample(Throwable ex){
        if (ex == null){
            return false;
        }

        new Thread(()->{
            Looper.prepare();
            // 不能使用这个ToastUtils.show()，不能即时的提示，会因为异常出现问题
//            ToastUtils.show("很抱歉，程序出现异常，即将退出！");
//            MyToast.toast(mContext, "很抱歉,程序出现异常,即将退出.", Toast.LENGTH_LONG);
            Toast.makeText(mContext, "很抱歉，程序出现异常，即将退出", Toast.LENGTH_SHORT).show();

            Looper.loop();
        }).start();

        saveCrashInfoToFile(ex);

        //如果反复启动时就闪退，清除全部数据
//        long lastStartTime = mContext.getSharedPreferences(PREFERENCE, Context.MODE_PRIVATE).getLong(KEY_LAST_LAUNCH_TIME,0);
//        if((System.currentTimeMillis()-lastStartTime)<6){
//            File patcher = new File(mContext.getExternalFilesDir(null).getAbsolutePath()+"/patchtmp/patcher.apk");
//            String msg;
//            if(patcher.exists()){
//                boolean b=patcher.delete();
//            }
//        }
        return true;
    }

    /**
     * 保存异常信息到本地
     * @param ex
     */
    private void saveCrashInfoToFile(Throwable ex) {
        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        ex.printStackTrace(printWriter);
        Throwable exCause = ex.getCause();
        while (exCause != null) {
            exCause.printStackTrace(printWriter);
            exCause = exCause.getCause();
        }
        printWriter.close();

        long timeMillis = System.currentTimeMillis();
        //错误日志文件名称
        String fileName = "crash-" + timeMillis + ".log";
        //文件存储位置
        String path = mContext.getExternalFilesDir(null) + "/crash_logInfo/";
        File fl = new File(path);
        //创建文件夹
        if(!fl.exists()) {
            fl.mkdirs();
        }
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(path + fileName);
            fileOutputStream.write(writer.toString().getBytes());
            fileOutputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


