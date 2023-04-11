package com.eltechs.axs.xserver;

import android.support.annotation.NonNull;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;


public class RealXServer {
    public static native void start();
    public static native void windowChanged(Surface surface, int width, int height);
    public static native void keySym(int keysym, int state, int i);
    public static native void key(int key, int state);
    public static native void click(int button, int state);
    public static native void motion(int x, int y);
    public static native void scroll(int axis, int value);

    static {
        System.loadLibrary("inputstub");
    }

    public void addCallback(SurfaceView v) {
        v.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(@NonNull SurfaceHolder holder) {
                windowChanged(holder.getSurface(), 0, 0);
            }

            @Override
            public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
                windowChanged(holder.getSurface(), width, height);
            }

            @Override
            public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
                windowChanged(null, 0, 0);
            }
        });
    }
}