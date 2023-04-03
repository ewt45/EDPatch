package com.eltechs.axs.xserver;

/* loaded from: classes.dex */
public interface GraphicsContextLifecycleListener {
    void graphicsContextCreated(GraphicsContext graphicsContext);

    void graphicsContextFreed(GraphicsContext graphicsContext);
}