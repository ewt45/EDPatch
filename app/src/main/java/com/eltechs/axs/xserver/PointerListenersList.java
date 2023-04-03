package com.eltechs.axs.xserver;

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
        for (PointerListener pointerListener : this.listeners) {
            pointerListener.pointerButtonPressed(i);
        }
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
