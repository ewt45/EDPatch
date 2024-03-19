package com.example.datainsert.exagear.controlsV2;

import static com.example.datainsert.exagear.controlsV2.axs.XKeyButton.POINTER_SCROLL_DOWN;
import static com.example.datainsert.exagear.controlsV2.axs.XKeyButton.POINTER_SCROLL_UP;

import android.os.Handler;
import android.os.Looper;

import java.lang.ref.WeakReference;

class XServerViewHolder_MouseWheelInjector implements Runnable {
    static WeakReference<XServerViewHolder_MouseWheelInjector> UP;
    static WeakReference<XServerViewHolder_MouseWheelInjector> DOWN;
    final static int intervalMs = 50;
    final static int firstIntervalMs = 300; //判断为长按的时间
    final int pointerButton;
    Handler handler = new Handler(Looper.getMainLooper()); //不知道用handler的话延迟会不会太大
    volatile boolean isFirstPressing = true; //如果为true，则说明此时为按下后第一次发送事件，应等待较长时间后确定为长按再不断发送事件
    volatile boolean isPressing = false; //如果为true，则再post一次，否则停止运行。

    public XServerViewHolder_MouseWheelInjector(int buttonCode) {
        pointerButton = buttonCode;
    }

    public static XServerViewHolder_MouseWheelInjector getByCode(int buttonCode) {
        if (buttonCode == POINTER_SCROLL_UP) {
            if (UP == null || UP.get() == null)
                UP = new WeakReference<>(new XServerViewHolder_MouseWheelInjector(POINTER_SCROLL_UP));
            return UP.get();
        } else if (buttonCode == POINTER_SCROLL_DOWN) {
            if (DOWN == null || DOWN.get() == null)
                DOWN = new WeakReference<>(new XServerViewHolder_MouseWheelInjector(POINTER_SCROLL_DOWN));
            return DOWN.get();
        }
        throw new RuntimeException("buttonCode必须为4或5");
    }

    public void start() {
        isPressing = true;
        isFirstPressing=true;
        handler.post(this);

    }

    public void stop() {
        isPressing = false;
        handler.removeCallbacks(this); //应该立刻移除回调。否则在时间间隔内，如果又置为true的话可能导致问题
    }

    @Override
    public void run() {
        if (!isPressing){
            return;
        }

        XServerViewHolder holder = Const.getXServerHolder();
        holder.injectPointerButtonPress(pointerButton);
        holder.injectPointerButtonRelease(pointerButton);
        handler.postDelayed(this, isFirstPressing?firstIntervalMs:intervalMs);
        isFirstPressing=false;
    }
}
