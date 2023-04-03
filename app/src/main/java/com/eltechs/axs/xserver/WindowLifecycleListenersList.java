package com.eltechs.axs.xserver;

import java.util.ArrayList;
import java.util.Collection;

/* loaded from: classes.dex */
public class WindowLifecycleListenersList {
    private final Collection<WindowLifecycleListener> listeners = new ArrayList();

    public void addListener(WindowLifecycleListener windowLifecycleListener) {
        this.listeners.add(windowLifecycleListener);
    }

    public void removeListener(WindowLifecycleListener windowLifecycleListener) {
        this.listeners.remove(windowLifecycleListener);
    }

    public void sendWindowCreated(Window window) {
        for (WindowLifecycleListener windowLifecycleListener : this.listeners) {
            windowLifecycleListener.windowCreated(window);
        }
    }

    public void sendWindowMapped(Window window) {
        for (WindowLifecycleListener windowLifecycleListener : this.listeners) {
            windowLifecycleListener.windowMapped(window);
        }
    }

    public void sendWindowUnmapped(Window window) {
        for (WindowLifecycleListener windowLifecycleListener : this.listeners) {
            windowLifecycleListener.windowUnmapped(window);
        }
    }

    public void sendWindowReparented(Window window, Window window2) {
        for (WindowLifecycleListener windowLifecycleListener : this.listeners) {
            windowLifecycleListener.windowReparented(window, window2);
        }
    }

    public void sendWindowZOrderChange(Window window) {
        for (WindowLifecycleListener windowLifecycleListener : this.listeners) {
            windowLifecycleListener.windowZOrderChange(window);
        }
    }

    public void sendWindowDestroyed(Window window) {
        for (WindowLifecycleListener windowLifecycleListener : this.listeners) {
            windowLifecycleListener.windowDestroyed(window);
        }
    }
}
