package com.termux.x11;

import android.content.Context;
import android.util.Log;

import com.eltechs.axs.KeyCodesX;

public class ViewForRendering extends LorieView {
    private static final String TAG = "ViewForRendering";
    public ViewForRendering(Context context) {
        super(context);
        mInstance = this;
    }

    private static ViewForRendering mInstance;
    public static void keyEvent(int keycode, int keySym, boolean down){
        if(mInstance==null)
            return;
        //终于知道问题所在了。exa里android keycode在java里转为x keycode（用KeyCodeX），之后传到这里的keycode直接是x keycode + 8。
        // 而tx11往native传的参数keycode仍然是android keycode。然后native中转为x keycode. 传的参数scancode才是x keycode

        //keyEvent只能处理keycode。所以keSym优先，避免需要shift的字符等无法输入（但是这样就不支持长按了？）
       if(keySym!=0){
           textEvent(keySym,down);
            return;
        }

        //应该让它用unicode。也就是scancode不为0. 也就是把scancode设为unicode，也就是把scancode设为keysym
        if(keycode==0|| keycode==8)
            Log.e(TAG, "keyEvent: keycode为0 不应出现这种情况");

        int scancode = keycode-8; //exa不仅转换了android keycode，而且提前加好8了
        mInstance.sendKeyEvent(scancode,keycode,down);
        //native里送的是scancode+8.。。
    }

    public static void textEvent(int keySym ,boolean down){
        if(mInstance==null)
            return;

        //textevent不支持长按，所以需要跳过一个事件
        if(!down)
            return;

        //不知道为什么括号没法输入。只能shift + 9/0了
        if(keySym==0x28 || keySym == 0x29){
            KeyCodesX parenKeyCode = keySym==0x28?KeyCodesX.KEY_9:KeyCodesX.KEY_0;
            keyEvent(KeyCodesX.KEY_SHIFT_LEFT.getValue(), 0,true);
            keyEvent(parenKeyCode.getValue(), 0,true);
            keyEvent(parenKeyCode.getValue(), 0,false);
            keyEvent(KeyCodesX.KEY_SHIFT_LEFT.getValue(), 0,false);
            return;
        }

        //回车 应该是return 0xff0d .但是从keyevent获取到的unicode是0x0a linefeed，手动改一下
        if(keySym==0x0a)
            keySym=0x0d;

        mInstance.sendTextEvent(String.valueOf((char)keySym));

    }

    public static void textEvent(String text){
        if(mInstance==null)
            return;

        mInstance.sendTextEvent(text);
    }

    private static final int XI_TouchBegin = 18; //MotionEvent.ACTION_DOWN MotionEvent.ACTION_POINTER_DOWN
    private static final int XI_TouchUpdate = 19; //MotionEvent.ACTION_MOVE
    private static final int XI_TouchEnd = 20; //MotionEvent.ACTION_UP MotionEvent.ACTION_POINTER_UP MotionEvent.ACTION_CANCEL

    public static void mouseEvent(int xPos, int yPos, int keycode, boolean isPress, boolean relative) {
        if(mInstance==null)
            return;
        //？？？滚轮上下都是BUTTON_SCROLL==4，然后负数是上正数是下
        if(keycode==5 || keycode == 4){
            xPos= 0; //横向滚动 exa好像不支持
            yPos=keycode==4?-30:30;
            keycode=4;
        }

        mInstance.sendMouseEvent(xPos,yPos,keycode,isPress,relative);
    }

    public static void touchEvent(int x,int y){
        if(mInstance==null)
            return;
        //先一律设置成XI_TouchUpdate吧。id是手指编号先设置成0
        mInstance.sendTouchEvent(XI_TouchUpdate,0,x,y);
    }

    /**
     * 退出容器时，自身的静态实例清空
     */
    public static void clearStaticInstance() {
        mInstance=null;
    }
}



/*
第一版xegw。单独拉出来的surfaceview
 */

//public class ViewForRendering extends SurfaceView {
//    private static final String TAG = "ViewForRendering";
//    int xserverWidth;
//    int xserverHeight;
//
//    public static boolean isServerStart;
//    public ViewForRendering(Context context) {
//        super(context);
//        Log.d(TAG, "ViewForRendering: 构造函数");
//        EnvironmentAware environmentAware = Globals.getApplicationState();
//        XServerComponent component = environmentAware.getEnvironment().getComponent(XServerComponent.class);
//        xserverWidth = component.getScreenInfo().widthInPixels;
//        xserverHeight = component.getScreenInfo().heightInPixels;
//
////        getHolder().setFormat(PixelFormat.RGBA_8888);
//        getHolder().addCallback(new SurfaceHolder.Callback() {
//            @Override
//            public void surfaceCreated(SurfaceHolder holder) {
//                Log.d(TAG, "surfaceCreated: ");
//                if(!isServerStart){
//                    Log.e(TAG, "surfaceCreated: 开始执行RealXServer.start();");
//                    RealXServer.start();
//                    isServerStart = true;
//                }
//                windowChanged(holder.getSurface(), 0, 0);
//            }
//
//            @Override
//            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height){
//                if(width!=0 && height!=0){
//                    Log.e(TAG, "surfaceChanged: ");
////                windowChanged(null,xserverWidth,xserverHeight);
//                    windowChanged(holder.getSurface(),xserverWidth,xserverHeight);
////                setWillNotDraw(false);
//                }
//
//            }
//
//            @Override
//            public void surfaceDestroyed(SurfaceHolder holder) {
//                Log.d(TAG, "surfaceDestroyed: ");
//
//                windowChanged(null,0,0);
////                if(isServerStart){
////                    Log.e(TAG, "surfaceDestroyed: 开始执行RealXServer.stop();");
////                    RealXServer.stop();
////                    isServerStart = false;
////                }
//            }
//        });
//
////            isFirstStart = false;//试试只调用一次？目前第二次调用会闪退
//    }
//
//    @Override
//    protected void onAttachedToWindow() {
//        super.onAttachedToWindow();
////        postDelayed(()->{
////            if(!isServerStart){
////                Log.e(TAG, "onAttachedToWindow: 开始执行RealXServer.start();");
////                RealXServer.start();
////                isServerStart = true;
////            }
////        },3000);
//    }
//
//    @Override
//    protected void onDetachedFromWindow() {
//        super.onDetachedFromWindow();
////        if(isServerStart){
////            Log.e(TAG, "onDetachedFromWindow: 开始执行RealXServer.stop();");
////            RealXServer.stop();
////            isServerStart=false;
////        }
//    }
//
//    long lastOperate=0;
//    private void postStartStopXServer(boolean start){
//
//    }
//
//}

/*
学习surfaceview的时候。非正式用途
 */

//public class ViewForRendering extends TextureView implements TextureView.SurfaceTextureListener{
//    private static final String TAG = "ViewForRendering";
//    int xserverWidth;
//    int xserverHeight;
//    Surface mSurface;
//    public ViewForRendering(Context context) {
//        super(context);
////        getHolder().setFormat(PixelFormat.RGBA_8888);
//
////        getHolder().setFormat(PixelFormat.TRANSPARENT);
////        setWillNotDraw(true);
//
//
//        EnvironmentAware environmentAware = Globals.getApplicationState();
//        XServerComponent component = environmentAware.getEnvironment().getComponent(XServerComponent.class);
//        xserverWidth = component.getScreenInfo().widthInPixels;
//        xserverHeight = component.getScreenInfo().heightInPixels;
//        this.setSurfaceTextureListener(this);
//
//    }
//
//    @Override
//    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
//        Log.d(TAG, "onSurfaceTextureAvailable: ");
//        if(mSurface!=null)
//            mSurface.release();
//        surface.attachToGLContext(234);
//        mSurface = new Surface(surface);
//        RealXServer.windowChanged(mSurface,xserverWidth,xserverHeight);
//    }
//
//    @Override
//    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
//        Log.d(TAG, "onSurfaceTextureSizeChanged: ");
//        RealXServer.windowChanged(mSurface,xserverWidth,xserverHeight);
//    }
//
//    @Override
//    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
//        return true;
//    }
//
//    @Override
//    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
//        Log.d(TAG, "onSurfaceTextureUpdated: ");
//    }
//}
