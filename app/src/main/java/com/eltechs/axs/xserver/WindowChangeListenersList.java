package com.eltechs.axs.xserver;

import com.eltechs.axs.xserver.impl.masks.Mask;
import java.util.ArrayList;
import java.util.Collection;

/* loaded from: classes.dex */
public class WindowChangeListenersList {
    private final Collection<WindowChangeListener> listeners = new ArrayList();

    public void addListener(WindowChangeListener windowChangeListener) {
        this.listeners.add(windowChangeListener);
    }

    public void removeListener(WindowChangeListener windowChangeListener) {
        this.listeners.remove(windowChangeListener);
    }

    public void sendWindowGeometryChanged(Window window) {
        for (WindowChangeListener windowChangeListener : this.listeners) {
            windowChangeListener.geometryChanged(window);
        }
    }

    public void sendWindowAttributeChanged(Window window, Mask<WindowAttributeNames> mask) {
        for (WindowChangeListener windowChangeListener : this.listeners) {
            windowChangeListener.attributesChanged(window, mask);
        }
    }
}
