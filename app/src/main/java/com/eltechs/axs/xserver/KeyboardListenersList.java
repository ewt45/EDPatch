package com.eltechs.axs.xserver;

import android.util.Log;

import com.eltechs.axs.xserver.impl.masks.Mask;

import org.apache.commons.compress.utils.Lists;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class KeyboardListenersList {
    private static final String TAG= "KeyboardListenersList";
    private final Collection<KeyboardListener> listeners = new ArrayList<>();

    public void addListener(KeyboardListener keyboardListener) {
        this.listeners.add(keyboardListener);
    }

    public void removeListener(KeyboardListener keyboardListener) {
        this.listeners.remove(keyboardListener);
    }

    public void sendKeyPressed(byte b, int i, Mask<KeyButNames> mask) {
        for (KeyboardListener keyboardListener : this.listeners) {
            Log.d(TAG, "sendKeyPressed: 给监听器发送按下按键事件"+ keyboardListener);

            keyboardListener.keyPressed(b, i, mask);
        }
    }

    public void sendKeyReleased(byte b, int i, Mask<KeyButNames> mask) {
        for (KeyboardListener keyboardListener : this.listeners) {
            Log.d(TAG, "sendKeyPressed: 给监听器发送松开按键事件"+ keyboardListener);
            keyboardListener.keyReleased(b, i, mask);
        }
    }
}