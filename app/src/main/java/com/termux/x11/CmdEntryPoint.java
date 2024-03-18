package com.termux.x11;


import static android.content.pm.ApplicationInfo.FLAG_TEST_ONLY;
import static com.example.datainsert.exagear.QH.MY_SHARED_PREFERENCE_SETTING;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ServiceInfo;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.ParcelFileDescriptor;
import android.os.Process;
import android.os.RemoteException;
import android.support.v4.app.NotificationCompat;
import android.system.ErrnoException;
import android.system.Os;
import android.util.Log;
import android.view.Surface;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.eltechs.axs.AppConfig;
import com.eltechs.axs.Globals;
import com.eltechs.axs.activities.SwitchToAxsFromSystemTrayActivity;
import com.eltechs.axs.applicationState.ApplicationStateBase;
import com.eltechs.axs.applicationState.ApplicationStateObject;
import com.eltechs.axs.applicationState.DroidApplicationContextAware;
import com.eltechs.axs.applicationState.ExagearImageAware;
import com.eltechs.axs.configuration.startup.StartupActionsCollection;
import com.eltechs.ed.EDApplicationState;
import com.eltechs.ed.R;
import com.example.datainsert.exagear.FAB.dialogfragment.BaseFragment;
import com.example.datainsert.exagear.QH;
import com.example.datainsert.exagear.RR;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * 改成service之后：
 * manifest声明android:process
 * axsenvironment startService
 * startupActivity stopService
 */
@SuppressLint("NewApi")
public class CmdEntryPoint extends Service {
    /**
     * 部分手机（安卓9？）会有黑屏 有箭头鼠标的问题。需要添加参数 -legacy-drawing启动、默认false
     */
    public static final String PREF_KEY_LEGACY_DRAW = "use-tx11-legacy-drawing";
    public static final String ACTION_START = "com.termux.x11.CmdEntryPoint.ACTION_START";
    public static final int PORT = 7892;
    public static final byte[] MAGIC = "0xDEADBEEF".getBytes();
    /**
     * Command to the service to display a message
     */
    public static final int MSG_SAY_HELLO = 1;
    public static final int MSG_CALL_WINDOW_CHANGED = 3;
    public static final int MSG_CALL_START = 4;
    public static final int MSG_CALL_GET_LOGCAT_OUTPUT = 5;
    public static final int MSG_CALL_GET_X_CONNECTION = 6;
    public static final int MSG_GET_PID = 7;
    public static final String INTENT_EXTRA_ARGS = "args";
    public static final String INTENT_EXTRA_ENV_DISPLAY = "ENV_DISPLAY";
    public static final String INTENT_EXTRA_ENV_TMPDIR = "ENV_TMPDIR";

    private static final String TAG = "CmdEntryPoint";
    private static final Handler handler;

    //static不仅在server端调用，而且在主进程端也调用了一次，应该不会有问题吧？（应该没问题，tx11也在主进程调用了CmdEntryPoint）
    static {
        try {
            System.loadLibrary("Xlorie");
            System.loadLibrary("some-helper");
        } catch (UnsatisfiedLinkError e) {
            e.printStackTrace();
        }

//        try {
//            redirectStdErr();
//        }catch (Exception e){}
        handler = new Handler();
//        wrapStart(null);
    }

    /**
     * Target we publish for clients to send messages to IncomingHandler.
     */
    Messenger mMessenger;

    private native static void redirectStdErr();
    private native static void testEnv();

    public static void wrapStart(Context context) {
        //启动容器的准备过程 运行一次cmd
        boolean useLegacyDraw = context.getSharedPreferences(MY_SHARED_PREFERENCE_SETTING, Context.MODE_PRIVATE).getBoolean(PREF_KEY_LEGACY_DRAW, false);
        List<String> argsList = new ArrayList<>();
        argsList.add(":12");
        if (useLegacyDraw)
            argsList.add("-legacy-drawing");

        //环境变量要在运行server的进程里设置
        try {
            String env_tmpdir = String.format("%s/image/tmp", context.getFilesDir().getAbsolutePath());
            Os.setenv("TMPDIR", env_tmpdir, true);
            //设置宽高？
//            String xdroidNum =  new File(context.getFilesDir(),"image/home/xdroid").getCanonicalFile().getName().replace("xdroid_","");
//            Log.d(TAG, "wrapStart: 当前启动的容器序号："+xdroidNum);
//            String scrSize = context.getSharedPreferences(String.format("%s.CONTAINER_CONFIG_%s", context.getPackageName(),xdroidNum ),Context.MODE_PRIVATE).getString("SCREEN_SIZE","1280,1024");//.split(",");
//            if(scrSize==null || "default".equals(scrSize))
//                scrSize="1280,1024";
//            String[] resSplit  = scrSize.split(",");
//            Os.setenv("INIT_WIDTH",resSplit[0],true);
//            Os.setenv("INIT_HEIGHT",resSplit[1],true);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //在这里传入命令行参数吧
        Log.d(TAG, "onBind: 传入参数 调用start方法. 重新bind static会重走吗（pid会变吗？） pid=" + Process.myPid());
        start(argsList.toArray(new String[0]));
    }


//    CmdEntryPoint(String[] args) {
//        Log.d(TAG, "CmdEntryPoint: 新建cmd实例");
//        if (!start(args)) {
////            System.exit(1);
//        }
//
////        sendBroadcast();
//    }

    //native code
    public static void requestConnection() {
        Log.e(TAG, "requestConnection: 被native代码调用了，但是无法执行原本的内容");
    }

    /**
     * 在启动容器时，AXSEnvironment里调用。用于启动service进程
     * <p>
     * 不应在service进程里调用
     */
    public static void sendStartSignalInAppProcess() {
        Context context = Globals.getAppContext();
        if (context != null) {
//            setGPUTurbo();
            context.startService(new Intent(context, CmdEntryPoint.class));
        }
    }

    public static void setGPUTurbo(){
        File workDir = new File(((ExagearImageAware) Globals.getApplicationState()).getExagearImage().getPath(),"opt/gpuclock");
        File logFile = new File(workDir,"log.txt");

        List<String> cmdList = new ArrayList<>();
        cmdList.add("./gpulock-lock-exe");
        if(QH.getPreference().getBoolean(DialogOptions.PREF_KEY_GPU_CLOCK, true))
            cmdList.add("1");
        try {
            ProcessBuilder builder = new ProcessBuilder(cmdList);
            builder.directory(workDir);
            if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                builder.redirectErrorStream(true);
                boolean b = logFile.getParentFile().mkdirs();
                builder.redirectOutput(logFile);
            }
            builder.start();
        }catch (Exception e){
            try (PrintWriter printWriter = new PrintWriter(logFile);) {
                e.printStackTrace(printWriter);
            } catch (FileNotFoundException ignored) {

            }
            e.printStackTrace();
        }
    }

    /**
     * 在退出容器时，StartupActivity里调用，用于结束service进程 和 清空LorieView静态实例
     * <p>
     * 不应在service进程里调用
     */
    public static void sendStopSignalInAppProcess() {
        Context context = Globals.getAppContext();
        if (context != null) {
            context.stopService(new Intent(context, CmdEntryPoint.class));
            ViewForRendering.clearStaticInstance();
        }

    }
    public static LinearLayout getViewForFabDialog(BaseFragment baseFragment){
        return DialogOptions.getView(baseFragment);
    }


    @Override
    public void onCreate() {
        super.onCreate();
        //如果service是在activity bindService时创建的，那么activity小窗（destroy）的时候也会重走onCreate
        //如果先startService，再bind，activity重建时不会带着service一起重建
        Log.e(TAG, "onCreate: pid=" + Process.myPid());
        //到底还是需要用到全局context，设置一个吧 也不好，需要用到包名。先只设置RR的locale吧
        RR.locale = getResources().getConfiguration().locale.getLanguage();
//        if (Globals.getApplicationState() == null) {
//            Globals.setApplicationState(new ApplicationStateObject<>(EDApplicationState.class));
//            ((DroidApplicationContextAware) Globals.getApplicationState()).setAndroidApplicationContext(getApplicationContext());
//        }

        wrapStart(this);

        //显示前台通知，防止切后台后，service被杀
        configureAsForegroundService();
        //尝试锁定gpu最高频率

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand: 仅startService能走到这里来. flag返回sticky？算了还是用super的吧");
        return   super.onStartCommand(intent, flags, startId);

    }

    /**
     * When binding to the service, we return an interface to our messenger
     * for sending messages to the service.
     */
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "service端onBind被调用,pid=" + Process.myPid());
//        Toast.makeText(getApplicationContext(), "binding", Toast.LENGTH_SHORT).show();
        mMessenger = new Messenger(new IncomingHandler(this));
        return mMessenger.getBinder();
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy: 在这里直接结束进程吧");
        super.onDestroy();
        this.mMessenger = null;
        //关闭前台通知
        stopForeground(true);
        Globals.clearState();
        Process.killProcess(Process.myPid());
    }

    /**
     * 启动前台服务通知
     */
    private void configureAsForegroundService() {
//        TrayConfiguration trayConfiguration = ((EnvironmentAware) Globals.getApplicationState()).getEnvironment().trayConfiguration;
        Intent intent = new Intent(this, SwitchToAxsFromSystemTrayActivity.class);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= 26) {
            notificationManager.createNotificationChannel(new NotificationChannel("notification_channel_id", "ExaGear", NotificationManager.IMPORTANCE_MIN));
        }

        Notification build = new NotificationCompat.Builder(this, "notification_channel_id")
                .setSmallIcon((getApplicationInfo().flags &  FLAG_TEST_ONLY) !=0? R.drawable.tray:0x7f0800cc)
                .setContentText(RR.getS(RR.xegw_notification))
                .setContentTitle("Exagear")
                .setContentIntent(PendingIntent.getActivity(this, 0, intent, 0))
                .build();
        notificationManager.notify(2, build);
        startForeground(2, build);
    }

    public static native boolean start(String[] args);

    public native void windowChanged(Surface surface);

    public native ParcelFileDescriptor getXConnection();

    public native ParcelFileDescriptor getLogcatOutput();

    /**
     * Handler of incoming messages from clients.
     */
    static class IncomingHandler extends Handler {
        private final Context applicationContext;
        WeakReference<CmdEntryPoint> mHost;

        IncomingHandler(CmdEntryPoint context) {
            applicationContext = context.getApplicationContext();
            mHost = new WeakReference<>(context);
        }

        @Override
        public void handleMessage(Message msg) {
            Log.d("ServiceConnection", "handleMessage: 接收message，pid=" + Process.myPid());
            switch (msg.what) {
                case MSG_SAY_HELLO:
                    Toast.makeText(applicationContext, "hello!", Toast.LENGTH_SHORT).show();
                    break;

                case MSG_CALL_WINDOW_CHANGED: {
                    Log.d(TAG, "handleMessage: MSG_WINDOW_CHANGED 传入surface");
                    assert msg.obj instanceof Surface;
                    if (mHost.get() != null)
                        mHost.get().windowChanged((Surface) msg.obj);
                    break;
                }

                case MSG_CALL_GET_LOGCAT_OUTPUT: {
                    Log.d(TAG, "handleMessage: 获取logcat的fd");
                    assert msg.replyTo != null;
                    try {
                        if (mHost.get() != null)
                            msg.replyTo.send(Message.obtain(null, MSG_CALL_GET_LOGCAT_OUTPUT, mHost.get().getLogcatOutput()));
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    break;
                }

                case MSG_CALL_GET_X_CONNECTION: {
                    Log.d(TAG, "handleMessage: 获取连接xserver的fd");
                    assert msg.replyTo != null;
                    try {
                        if (mHost.get() != null)
                            msg.replyTo.send(Message.obtain(null, MSG_CALL_GET_X_CONNECTION, mHost.get().getXConnection()));
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    break;
                }

                case MSG_GET_PID: {
                    Log.d(TAG, "handleMessage: 获取service所在进程的pid");
                    assert msg.replyTo != null;
                    try {
                        msg.replyTo.send(Message.obtain(null, MSG_GET_PID, Process.myPid(), 0));
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    break;
                }

                default:
                    super.handleMessage(msg);
            }
        }
    }
}
