package com.eltechs.axs.xserver.events;

import com.eltechs.axs.xserver.Window;

/* loaded from: classes.dex */
public class CreateNotify extends Event {
    private final Window parent;
    private final Window window;

    public CreateNotify(Window window, Window window2) {
        super(16);
        this.parent = window;
        this.window = window2;
    }

    public Window getParent() {
        return this.parent;
    }

    public Window getWindow() {
        return this.window;
    }
}
