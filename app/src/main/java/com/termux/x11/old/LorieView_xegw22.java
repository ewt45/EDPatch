//package com.termux.x11;
//
//import android.annotation.SuppressLint;
//import android.content.ClipData;
//import android.content.ClipboardManager;
//import android.content.Context;
//import android.graphics.Point;
//import android.graphics.Rect;
//import android.support.annotation.NonNull;
//import android.util.AttributeSet;
//import android.util.Log;
//import android.view.Surface;
//import android.view.SurfaceHolder;
//import android.view.SurfaceView;
//
//import com.eltechs.axs.Globals;
//import com.eltechs.axs.applicationState.EnvironmentAware;
//import com.eltechs.axs.environmentService.components.XServerComponent;
//import com.eltechs.axs.helpers.UiThread;
//
//@SuppressLint("WrongConstant")
//private class LorieView extends SurfaceView  {
//    private static final String TAG = "LorieView";
//    int xserverWidth;
//    int xserverHeight;
//    interface Callback {
//        void changed(Surface sfc, int surfaceWidth, int surfaceHeight, int screenWidth, int screenHeight);
//    }
//
//    interface PixelFormat {
//        int BGRA_8888 = 5; // Stands for HAL_PIXEL_FORMAT_BGRA_8888
//    }
//
//    /**
//     * 原来这个不是surfaceCallback。。。。应该类似于RealXServer.windowChanged？
//     */
//    private Callback mCallback;
//    private final Point p = new Point();
//    private final SurfaceHolder.Callback mSurfaceCallback = new SurfaceHolder.Callback() {
//        @Override public void surfaceCreated(@NonNull SurfaceHolder holder) {
//            holder.setFormat(PixelFormat.BGRA_8888);
//        }
//
//        @Override public void surfaceChanged(@NonNull SurfaceHolder holder, int f, int width, int height) {
//            width = getMeasuredWidth();
//            height = getMeasuredHeight();
//
//            Log.d("SurfaceChangedListener", "Surface was changed: measured " + width + "x" + height+", xserver "+xserverWidth+"x"+xserverHeight);
//            if (mCallback == null)
//                return;
//
////            getDimensionsFromSettings();
////            mCallback.changed(holder.getSurface(), width, height, p.x, p.y);
//            mCallback.changed(holder.getSurface(),width,height,xserverWidth,xserverHeight);
//        }
//
//        @Override public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
//            if (mCallback != null)
//                mCallback.changed(holder.getSurface(), 0, 0, 0, 0);
//        }
//    };
//
//    public LorieView(Context context) {super(context); init(); }
//    public LorieView(Context context, AttributeSet attrs) {super(context, attrs); init(); }
//    public LorieView(Context context, AttributeSet attrs, int defStyleAttr) {super(context, attrs, defStyleAttr); init(); }
//    @SuppressWarnings("unused")
//    public LorieView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {super(context, attrs, defStyleAttr, defStyleRes); init(); }
//
//    private void init() {
//        Log.d(TAG, "init: ");
//        EnvironmentAware environmentAware = Globals.getApplicationState();
//        assert environmentAware != null;
//        XServerComponent component = environmentAware.getEnvironment().getComponent(XServerComponent.class);
//        xserverWidth = component.getScreenInfo().widthInPixels;
//        xserverHeight = component.getScreenInfo().heightInPixels;
//
//        getHolder().addCallback(mSurfaceCallback);
//    }
//
//    public void setCallback(Callback callback) {
//        mCallback = callback;
//        triggerCallback();
//    }
//
//    /**
//     * 重新设置callback并调用triggerCallback
//     */
//    public void regenerate() {
//        Callback callback = mCallback;
//        mCallback = null;
//        getHolder().setFormat(android.graphics.PixelFormat.RGBA_8888);
//        mCallback = callback;
//
//        triggerCallback();
//    }
//
//    public void triggerCallback() {
////        setFocusable(true);
////        setFocusableInTouchMode(true);
////        requestFocus();
//        Log.d(TAG, "triggerCallback: ");
//        Rect r = getHolder().getSurfaceFrame();
//        UiThread.post(() -> mSurfaceCallback.surfaceChanged(getHolder(), PixelFormat.BGRA_8888, r.width(), r.height()));
//    }
//
//
//    /*
//    有两个设置大小的函数，不管了吧
//    getHolder().setSizeFromLayout();
//    getHolder().setFixedSize(p.x, p.y);
//     */
//
////    @Override
////    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
////        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
////        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
////        if (preferences.getBoolean("displayStretch", false)
////                || "native".equals(preferences.getString("displayResolutionMode", "native"))
////                || "scaled".equals(preferences.getString("displayResolutionMode", "native"))) {
////            getHolder().setSizeFromLayout();
////            return;
////        }
////
////        getDimensionsFromSettings();
////
////        if (p.x <= 0 || p.y <= 0)
////            return;
////
////        int width = getMeasuredWidth();
////        int height = getMeasuredHeight();
////
////        if ((width < height && p.x > p.y) || (width > height && p.x < p.y))
////            //noinspection SuspiciousNameCombination
////            p.set(p.y, p.x);
////
////        if (width > height * p.x / p.y)
////            width = height * p.x / p.y;
////        else
////            height = width * p.y / p.x;
////
////        getHolder().setFixedSize(p.x, p.y);
////        setMeasuredDimension(width, height);
////    }
//
//    // It is used in native code
//    void setClipboardText(String text) {
//        ClipboardManager clipboard = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
//        clipboard.setPrimaryClip(ClipData.newPlainText("X11 clipboard", text));
//    }
//
//    static native void connect(int fd);
//    native void handleXEvents();
//    static native void startLogcat(int fd);
//    static native void setClipboardSyncEnabled(boolean enabled);
//    static native void sendWindowChange(int width, int height, int framerate);
//    public native void sendMouseEvent(float x, float y, int whichButton, boolean buttonDown, boolean relative);
//    public native void sendTouchEvent(int action, int id, int x, int y);
//    public native boolean sendKeyEvent(int scanCode, int keyCode, boolean keyDown);
//    public native void sendTextEvent(byte[] text);
//    public native void sendUnicodeEvent(int code);
//
//    static {
//        System.loadLibrary("Xlorie");
//    }
//}
