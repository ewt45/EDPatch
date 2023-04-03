package com.eltechs.axs.xserver;

import java.util.ArrayList;
import java.util.Collection;

/* loaded from: classes.dex */
public class PixmapLifecycleListenerList {
    private final Collection<PixmapLifecycleListener> listeners = new ArrayList();

    public void addListener(PixmapLifecycleListener pixmapLifecycleListener) {
        this.listeners.add(pixmapLifecycleListener);
    }

    public void removeListener(PixmapLifecycleListener pixmapLifecycleListener) {
        this.listeners.remove(pixmapLifecycleListener);
    }

    public void sendPixmapCreated(Pixmap pixmap) {
        for (PixmapLifecycleListener pixmapLifecycleListener : this.listeners) {
            pixmapLifecycleListener.pixmapCreated(pixmap);
        }
    }

    public void sendPixmapFreed(Pixmap pixmap) {
        for (PixmapLifecycleListener pixmapLifecycleListener : this.listeners) {
            pixmapLifecycleListener.pixmapFreed(pixmap);
        }
    }
}
