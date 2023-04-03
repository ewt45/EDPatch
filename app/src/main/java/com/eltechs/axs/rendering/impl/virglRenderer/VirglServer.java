package com.eltechs.axs.rendering.impl.virglRenderer;

/* loaded from: classes.dex */
public class VirglServer {
    public native void startServer(String str);

    public native void stopServer();

    static {
        System.loadLibrary("virgl-server");
    }
}
