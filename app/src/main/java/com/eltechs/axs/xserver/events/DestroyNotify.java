package com.eltechs.axs.xserver.events;

import android.support.annotation.NonNull;

import com.eltechs.axs.xserver.Window;

/* loaded from: classes.dex */
public class DestroyNotify extends Event {
    private final Window deletedWindow;
    private final Window originatedAt;

    public DestroyNotify(Window window, Window window2) {
        super(17);
        this.originatedAt = window;
        this.deletedWindow = window2;
    }

    public Window getOriginatedAt() {
        return this.originatedAt;
    }

    public Window getDeletedWindow() {
        return this.deletedWindow;
    }

    @NonNull
    @Override
    public String toString() {
        return "DestroyNotify{" +
                "deletedWindow=" + deletedWindow +
                ", originatedAt=" + originatedAt +
                '}';
    }
}
