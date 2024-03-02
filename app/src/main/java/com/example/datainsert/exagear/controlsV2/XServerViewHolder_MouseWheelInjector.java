package com.example.datainsert.exagear.controlsV2;

import android.os.Handler;
import android.os.Looper;

import java.lang.ref.WeakReference;

class XServerViewHolder_MouseWheelInjector implements Runnable {
    static WeakReference<XServerViewHolder_MouseWheelInjector> UP;
    static WeakReference<XServerViewHolder_MouseWheelInjector> DOWN;
    static int intervalMs = 30;
    final int pointerButton;
    Handler handler = new Handler(Looper.getMainLooper()); //不知道用handler的话延迟会不会太大
    boolean isPressing = false;

    public XServerViewHolder_MouseWheelInjector(int buttonCode) {
        pointerButton = buttonCode;
    }

    public static XServerViewHolder_MouseWheelInjector getByCode(int buttonCode) {
        if (buttonCode == 4) {
            if (UP == null || UP.get() == null)
                UP = new WeakReference<>(new XServerViewHolder_MouseWheelInjector(4));
            return UP.get();
        } else if (buttonCode == 5) {
            if (DOWN == null || DOWN.get() == null)
                DOWN = new WeakReference<>(new XServerViewHolder_MouseWheelInjector(5));
            return DOWN.get();
        }
        throw new RuntimeException("buttonCode必须为4或5");
    }

    public void start() {
        isPressing = true;
        handler.post(this);

    }

    public void stop() {
        isPressing = false;
    }

    @Override
    public void run() {
        if (!isPressing)
            return;

        XServerViewHolder holder = Const.getXServerHolder();
        holder.injectPointerButtonPress(pointerButton);
        holder.injectPointerButtonRelease(pointerButton);
        handler.postDelayed(this, intervalMs);
    }
}
