package com.eltechs.axs.xserver.events;

import com.eltechs.axs.geom.Rectangle;
import com.eltechs.axs.xserver.Window;

/* loaded from: classes.dex */
public class Expose extends Event {
    private final int height;
    private final int width;
    private final Window window;
    private final int x;
    private final int y;

    public Expose(Window window) {
        super(12);
        this.window = window;
        Rectangle boundingRectangle = window.getBoundingRectangle();
        this.y = 0;
        this.x = 0;
        this.width = boundingRectangle.width;
        this.height = boundingRectangle.height;
    }

    public Expose(Window window, int i, int i2, int i3, int i4) {
        super(12);
        this.window = window;
        this.x = i;
        this.y = i2;
        this.width = i3;
        this.height = i4;
    }

    public Window getWindow() {
        return this.window;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }
}
