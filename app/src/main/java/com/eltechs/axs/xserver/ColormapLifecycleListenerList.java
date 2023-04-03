package com.eltechs.axs.xserver;

import java.util.ArrayList;
import java.util.Collection;

/* loaded from: classes.dex */
public class ColormapLifecycleListenerList {
    private final Collection<ColormapLifecycleListener> listeners = new ArrayList();

    public void addListener(ColormapLifecycleListener colormapLifecycleListener) {
        this.listeners.add(colormapLifecycleListener);
    }

    public void removeListener(ColormapLifecycleListener colormapLifecycleListener) {
        this.listeners.remove(colormapLifecycleListener);
    }

    public void sendColormapCreated(Colormap colormap) {
        for (ColormapLifecycleListener colormapLifecycleListener : this.listeners) {
            colormapLifecycleListener.colormapCreated(colormap);
        }
    }

    public void sendColormapFreed(Colormap colormap) {
        for (ColormapLifecycleListener colormapLifecycleListener : this.listeners) {
            colormapLifecycleListener.colormapFreed(colormap);
        }
    }
}
