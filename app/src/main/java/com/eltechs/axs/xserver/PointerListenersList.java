package com.eltechs.axs.xserver;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collection;

public class PointerListenersList {
    private final Collection<PointerListener> listeners = new ArrayList<>();

    public void addListener(PointerListener pointerListener) {
        this.listeners.add(pointerListener);
    }

    public void removeListener(PointerListener pointerListener) {
        this.listeners.remove(pointerListener);
    }

    public void sendPointerMoved(int i, int i2) {
        for (PointerListener pointerListener : this.listeners) {
            pointerListener.pointerMoved(i, i2);
        }
    }

    public void sendPointerButtonPressed(int i) {
        StringBuilder builder = new StringBuilder("sendPointerButtonPressed: 按下鼠标按键"+i+". 发送给的监听器有");
        for (PointerListener pointerListener : this.listeners) {
            builder.append(pointerListener.getClass().getSimpleName());
            pointerListener.pointerButtonPressed(i);
        }
//        Log.d("TAG", builder.toString());
    }

    public void sendPointerButtonReleased(int i) {
        for (PointerListener pointerListener : this.listeners) {
            pointerListener.pointerButtonReleased(i);
        }
    }

    public void sendPointerWarped(int i, int i2) {
        for (PointerListener pointerListener : this.listeners) {
            pointerListener.pointerWarped(i, i2);
        }
    }
}
