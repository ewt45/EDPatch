package com.termux.x11;

import static android.content.pm.ApplicationInfo.FLAG_TEST_ONLY;
import static com.example.datainsert.exagear.QH.MY_SHARED_PREFERENCE_SETTING;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.Process;
import android.support.v4.app.NotificationCompat;
import android.system.Os;
import android.util.Log;
import android.view.Surface;
import android.widget.Toast;

import com.eltechs.axs.Globals;
import com.eltechs.axs.activities.SwitchToAxsFromSystemTrayActivity;
import com.eltechs.ed.R;
import com.example.datainsert.exagear.QH;
import com.example.datainsert.exagear.RR;

import java.util.ArrayList;
import java.util.List;

/**
 * manifest里声明的时候，加上android:process=":xserver" 就运行在单独的进程中了
 */
public class XegwConnectService extends Service {
    /**
     * 部分手机（安卓9？）会有黑屏 有箭头鼠标的问题。需要添加参数 -legacy-drawing启动、默认false
     */
    public static final String PREF_KEY_LEGACY_DRAW = "use-tx11-legacy-drawing";
    private static final String TAG = "TestConnectService";
    /**
//     * Command to the service to display a message
//     */
//    public static final int MSG_SAY_HELLO = 1;
//    public static final int MSG_CONNECT=2;
//    public static final int MSG_WINDOW_CHANGED= 3;
//
//    /**
//     * Handler of incoming messages from clients.
//     */
//    static class IncomingHandler extends Handler {
//        private Context applicationContext;
//        private MainActivity xegwConnection = new MainActivity();
//
//        IncomingHandler(Context context) {
//            applicationContext = context.getApplicationContext();
//        }
//
//        @Override
//        public void handleMessage(Message msg) {
//            Log.d("ServiceConnection", "handleMessage: 接收message，pid="+ Process.myPid());
//            switch (msg.what) {
//                case MSG_SAY_HELLO:
//                    Toast.makeText(applicationContext, "hello!", Toast.LENGTH_SHORT).show();
//                    break;
//                case MSG_CONNECT:
//                    Log.d(TAG, "handleMessage: 与xserver连接");
//                    assert msg.obj instanceof LorieView;
//                    xegwConnection.updateLorieView((LorieView) msg.obj);
//                    xegwConnection.onCreate();
//                    break;
//
//                case MSG_WINDOW_CHANGED:
//                    Log.d(TAG, "handleMessage: MSG_WINDOW_CHANGED");
//                    assert  msg.obj instanceof Surface;
//                    xegwConnection
//                default:
//                    super.handleMessage(msg);
//            }
//        }
//    }
//
//    /**
//     * Target we publish for clients to send messages to IncomingHandler.
//     */
//    Messenger mMessenger;

    CmdEntryPoint iBinder;

    /**
     * 启动容器的准备过程 运行一次cmd
     */
    @Override
    public void onCreate() {
        //TODO 目前是发送intent后立即启动模拟器了。考虑通过写入文件的方式，来判断x11是否创建成功？
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

        //环境变量要在运行server的进程里设置
        try {
            String env_tmpdir = String.format("%s/image/tmp", this.getFilesDir().getAbsolutePath());
            Os.setenv("TMPDIR", env_tmpdir, true);
            Os.setenv("TERMUX_X11_OVERRIDE_PACKAGE",getPackageName(),true);
        } catch (Exception e) {
            e.printStackTrace();
        }

        List<String> argsList = new ArrayList<>();
        argsList.add(":12");
        if (getSharedPreferences(MY_SHARED_PREFERENCE_SETTING, Context.MODE_PRIVATE).getBoolean(PREF_KEY_LEGACY_DRAW, false))
            argsList.add("-legacy-drawing");
        iBinder = new CmdEntryPoint(argsList.toArray(new String[0]),this);

        //显示前台通知，防止切后台后，service被杀
        configureAsForegroundService();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return iBinder;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy: 在这里直接结束进程吧");
        super.onDestroy();
        this.iBinder=null;
        //关闭前台通知
        stopForeground(true);
        Globals.clearState();
        Process.killProcess(Process.myPid()); //TODO 现在不杀进程行了吗
    }

    /**
     * 启动前台服务通知
     */
    private void configureAsForegroundService() {
//        TrayConfiguration trayConfiguration = ((EnvironmentAware) Globals.getApplicationState()).getEnvironment().trayConfiguration;
        Intent intent = new Intent(this, SwitchToAxsFromSystemTrayActivity.class);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= 26) {
            notificationManager.createNotificationChannel(new NotificationChannel("notification_channel_id", "ExaGear", NotificationManager.IMPORTANCE_DEFAULT));
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
}