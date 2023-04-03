package com.eltechs.axs.xserver.events;

import com.eltechs.axs.xserver.ConfigureWindowParts;
import com.eltechs.axs.xserver.StackMode;
import com.eltechs.axs.xserver.Window;
import com.eltechs.axs.xserver.impl.masks.Mask;

/* loaded from: classes.dex */
public class ConfigureRequest extends Event {
    private final int borderWidth;
    private final int height;
    private final Window parent;
    private final Mask<ConfigureWindowParts> parts;
    private final Window sibling;
    private final StackMode stackMode;
    private final int width;
    private final Window window;
    private final int x;
    private final int y;

    public ConfigureRequest(Window window, Window window2, Window window3, int i, int i2, int i3, int i4, int i5, StackMode stackMode, Mask<ConfigureWindowParts> mask) {
        super(23);
        this.parent = window;
        this.window = window2;
        this.sibling = window3;
        this.x = i;
        this.y = i2;
        this.width = i3;
        this.height = i4;
        this.borderWidth = i5;
        if (stackMode != null) {
            this.stackMode = stackMode;
        } else {
            this.stackMode = StackMode.ABOVE;
        }
        this.parts = mask;
    }

    public Window getParent() {
        return this.parent;
    }

    public Window getWindow() {
        return this.window;
    }

    public Window getSibling() {
        return this.sibling;
    }

    public StackMode getStackMode() {
        return this.stackMode;
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

    public int getBorderWidth() {
        return this.borderWidth;
    }

    public Mask<ConfigureWindowParts> getParts() {
        return this.parts;
    }
}
