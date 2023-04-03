package com.eltechs.axs.xserver;

import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class GraphicsContextLifecycleListenersList {
    private final List<GraphicsContextLifecycleListener> listeners = new ArrayList();

    public void addListener(GraphicsContextLifecycleListener graphicsContextLifecycleListener) {
        this.listeners.add(graphicsContextLifecycleListener);
    }

    public void removeListener(GraphicsContextLifecycleListener graphicsContextLifecycleListener) {
        this.listeners.remove(graphicsContextLifecycleListener);
    }

    public void sendGraphicsContextCreated(GraphicsContext graphicsContext) {
        for (GraphicsContextLifecycleListener graphicsContextLifecycleListener : this.listeners) {
            graphicsContextLifecycleListener.graphicsContextCreated(graphicsContext);
        }
    }

    public void sendGraphicsContextFreed(GraphicsContext graphicsContext) {
        for (GraphicsContextLifecycleListener graphicsContextLifecycleListener : this.listeners) {
            graphicsContextLifecycleListener.graphicsContextFreed(graphicsContext);
        }
    }
}
