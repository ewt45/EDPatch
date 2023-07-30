package com.termux.x11;


import static com.termux.x11.CmdEntryPoint.MSG_CALL_GET_LOGCAT_OUTPUT;
import static com.termux.x11.CmdEntryPoint.MSG_CALL_GET_X_CONNECTION;
import static com.termux.x11.CmdEntryPoint.MSG_GET_PID;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.ParcelFileDescriptor;
import android.os.Process;
import android.os.RemoteException;
import android.util.Log;

import com.eltechs.axs.activities.XServerDisplayActivity;
import com.eltechs.axs.helpers.UiThread;

import java.lang.ref.WeakReference;


/**
 * 原MainActivity
 */
public class MainActivity extends XServerDisplayActivity {
    static final String ACTION_STOP = "com.termux.x11.ACTION_STOP";
    private static final String TAG = "MainActivity";
    public static Handler handler = new Handler();

//    @Deprecated
//    private ICmdEntryInterface service = null;

//    static {
//        System.loadLibrary("some-helper");
//    }

    private LorieView mLorieView;

    private boolean mClientConnected = false;

//    /**
//     * 用以代替 ICmdEntryInterface service (改成messenger了）
//     * 整个生命周期 cmd应该只在命令行termux :1 & 时实例化一次。请求连接只是发送一次广播
//     */
//    private CmdEntryPoint mCmd = null;
    /**
     * Messenger for communicating with the service.
     */
    Messenger mService = null;

    /**
     * 用于在StartupActivity shutdown的时候 关闭service进程
     */
    public static int servicePid = -1;

    /**
     * Flag indicating whether we have called bind on the service.
     */
    boolean bound;

    /**
     * Class for interacting with the main interface of the service.
     */
    private final ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onNullBinding(ComponentName name) {
            ServiceConnection.super.onNullBinding(name);
            Log.d(TAG, "ServiceConnection onNullBinding: ");
        }

        public void onServiceConnected(ComponentName className, IBinder service) {
            Log.d(TAG, "ServiceConnection onServiceConnected: ");
            // This is called when the connection with the service has been
            // established, giving us the object we can use to
            // interact with the service.  We are communicating with the
            // service using a Messenger, so here we get a client-side
            // representation of that from the raw IBinder object.
            mService = new Messenger(service);
            bound = true;

            try {

                service.linkToDeath(() -> {
                    Log.e(TAG, "断开连接，: linkToDeath" );
                    mService = null;
                    bound=false;
                    //这里bind的话，可能导致没有unbind，而下次bind会报错android.app.ServiceConnectionLeaked ？
                    bound = bindService(new Intent(MainActivity.this, CmdEntryPoint.class), mConnection, Context.BIND_AUTO_CREATE);
                    Log.v("Lorie", "Disconnected");
                }, 0);

                Message msg = Message.obtain(null, MSG_GET_PID, 0, 0);
                msg.replyTo = mMessenger;
                mService.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }

            //viewofxserver是oncreate创建的，onstart才开始连接，连接上的时候肯定初始化过了
            Log.d(TAG, "onConnectToService: pid=" + Process.myPid());

            //主进程设置surfaceview的callback。服务进程只接收surface对象
            updateLorieView();


            //onCreate(); 原广播接收器 onReceive内容
            //每次重新连接的时候重置为false？
            mClientConnected=false;
            retrieveLogcatOutput();
            retrieveXConnection();

        }

        public void onServiceDisconnected(ComponentName className) {
            // This is called when the connection with the service has been
            // unexpectedly disconnected -- that is, its process crashed.
            Log.d(TAG, "onServiceDisconnected: 断开连接，cmd报错了吗");
            mService = null;
            bound = false;
        }
    };


    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: 绑定服务");

        // Bind to the service
        bound = bindService(new Intent(this, CmdEntryPoint.class), mConnection, Context.BIND_AUTO_CREATE);

    }

    @Override
    protected void onStop() {
        super.onStop();
        // Unbind from the service
        if(bound){
            unbindService(mConnection);
            bound = false;
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * activity重建时lorieview会变化，需要重新设置
     */
    public void updateLorieView(){

    }


//    public native static void setEnv(String name, String value);

    /**
     * 原onCreate 改为外部调用吧。初始化 并连接xserver
     */
    public void onCreate() {

    }
    final Messenger mMessenger = new Messenger(new ReceiveFdHandler(this));
    /**
     * 向service发送消息，请求fd
     */
    private void retrieveLogcatOutput(){
        try {
            Message msg = Message.obtain(null, MSG_CALL_GET_LOGCAT_OUTPUT, 0, 0);
            msg.replyTo = mMessenger;
            mService.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * 向service发送消息，请求fd
     */
    private void retrieveXConnection(){
        try {
            Message msg = Message.obtain(null, MSG_CALL_GET_X_CONNECTION, 0, 0);
            msg.replyTo = mMessenger;
            mService.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    boolean sendDone = false;
    /**
     * 发送的消息得到回应，拿到fd之后的操作
     */
    private void onReceiveLogcatOutput(ParcelFileDescriptor fd){
        try {
            Log.v("LorieBroadcastReceiver", "Extracting logcat fd.");
            if (fd != null)
                LorieView.startLogcat(fd.detachFd());
        } catch (Exception e) {
            Log.e("MainActivity", "Something went wrong while we were establishing connection", e);
        }
    }
    /**
     * 发送的消息得到回应，拿到fd之后的操作
     */
    private void onReceiveXConnection(ParcelFileDescriptor fd){
        if (mClientConnected)
            return;
        try {
            Log.v("LorieBroadcastReceiver", "Extracting X connection socket.");
            if (fd != null) {
                LorieView.connect(fd.detachFd());
                getLorieView().triggerCallback();
                clientConnectedStateChanged(true);
            } else {
                handler.postDelayed(this::retrieveXConnection, 500);
                Log.d(TAG, "tryConnect: 未获取fd无法连接，半秒钟后重试");
            }
        } catch (Exception e) {
            Log.e("MainActivity", "Something went wrong while we were establishing connection", e);
//            service = null;

            // We should reset the View for the case if we have sent it's surface to the client.
            getLorieView().regenerate();
        }

//        if(sendDone)
//            return;
//        sendDone=true;
//        Log.d(TAG, "onReceiveXConnection: 连接到xserver了，发送sendDone。然后运行wine");
//        try {
//            Method getCurrentAction = StartupActionsCollection.class.getDeclaredMethod("getCurrentAction");
//            getCurrentAction.setAccessible(true);
//            StartupActionsCollectionAware<?> aware = Globals.getApplicationState();
//            assert aware != null;
//            StartupAction<?> currentAction = (StartupAction<?>) getCurrentAction.invoke(aware.getStartupActionsCollection()); //第一个参数是所属类的实例
//            if(currentAction instanceof StartXegwService)
//                aware.getStartupActionsCollection().actionDone(currentAction);
//
//        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
//            e.printStackTrace();
//        }

    }

    private void onReceiveServicePid(int pid){
        servicePid = pid;
    }

    /**
     * 原先调用tryConnect的应该改为调用retrieveXConnection
     */
    @Deprecated
    void tryConnect() {

    }

    public LorieView getLorieView() {
        return mLorieView;
    }


    @SuppressWarnings("SameParameterValue")
    void clientConnectedStateChanged(boolean connected) {
        UiThread.post(() -> {
            mClientConnected = connected;

            // We should recover connection in the case if file descriptor for some reason was broken...
            if (!connected)
                retrieveXConnection();

//            if (connected)
//                getLorieView().setPointerIcon(PointerIcon.getSystemIcon(this, PointerIcon.TYPE_NULL));
        });
    }

    /**
     * 接收从service从传来的 fd
     */
    static class ReceiveFdHandler extends Handler{
        WeakReference<MainActivity> mHost ;
        public ReceiveFdHandler(MainActivity mainActivity){
            mHost = new WeakReference<>(mainActivity);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_CALL_GET_LOGCAT_OUTPUT:{
                    assert  msg.obj == null || msg.obj instanceof  ParcelFileDescriptor;
                    if(mHost.get()!=null)
                        mHost.get().onReceiveLogcatOutput((ParcelFileDescriptor) msg.obj);
                    break;
                }

                case MSG_CALL_GET_X_CONNECTION:{
                    assert  msg.obj == null || msg.obj instanceof  ParcelFileDescriptor;
                    if(mHost.get()!=null)
                        mHost.get().onReceiveXConnection((ParcelFileDescriptor) msg.obj);
                    break;
                }
                case MSG_GET_PID:{
                    if(mHost.get()!=null)
                        mHost.get().onReceiveServicePid(msg.arg1);
                    break;
                }
                default:
                    super.handleMessage(msg);
            }
        }
    }

//    @Override
//    public void onWindowFocusChanged(boolean hasFocus) {
//        super.onWindowFocusChanged(hasFocus);
//        if (hasFocus)
//            getLorieView().regenerate();
//    }
//
//
//    @SuppressLint("WrongConstant")
//    @Override
//    public WindowInsets onApplyWindowInsets(View v, WindowInsets insets) {
//        handler.postDelayed(() -> getLorieView().triggerCallback(), 100);
//        return insets;
//    }


//    private void checkXEvents() {
//        getLorieView().handleXEvents();
//        handler.postDelayed(this::checkXEvents, 300);
//    }
}
