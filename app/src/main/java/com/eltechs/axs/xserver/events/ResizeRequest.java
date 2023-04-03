package com.eltechs.axs.xserver.events;

import com.eltechs.axs.xserver.Window;

/* loaded from: classes.dex */
public class ResizeRequest extends Event {
    private final int height;
    private final int width;
    private final Window window;

    public ResizeRequest(Window window, int i, int i2) {
        super(25);
        this.window = window;
        this.width = i;
        this.height = i2;
    }

    public Window getWindow() {
        return this.window;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }
}
