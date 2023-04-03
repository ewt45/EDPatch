package com.eltechs.axs.xserver.events;

/* loaded from: classes.dex */
public abstract class Event {
    private final int id;

    /* JADX INFO: Access modifiers changed from: protected */
    protected Event(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }
}