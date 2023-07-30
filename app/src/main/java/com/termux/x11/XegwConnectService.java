//package com.termux.x11;
//
//import android.app.Service;
//import android.content.Context;
//import android.content.Intent;
//import android.os.Handler;
//import android.os.IBinder;
//import android.os.Message;
//import android.os.Messenger;
//import android.os.Process;
//import android.util.Log;
//import android.view.Surface;
//import android.widget.Toast;
//
///**
// * manifest里声明的时候，加上android:process=":xserver" 就运行在单独的进程中了
// */
//public class XegwConnectService extends Service {
//    private static final String TAG = "TestConnectService";
//    /**
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
//
//    /**
//     * When binding to the service, we return an interface to our messenger
//     * for sending messages to the service.
//     */
//    @Override
//    public IBinder onBind(Intent intent) {
//        Toast.makeText(getApplicationContext(), "binding", Toast.LENGTH_SHORT).show();
//        mMessenger = new Messenger(new IncomingHandler(this));
//        return mMessenger.getBinder();
//    }
//}