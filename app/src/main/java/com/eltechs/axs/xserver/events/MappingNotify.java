package com.eltechs.axs.xserver.events;

/* loaded from: classes.dex */
public class MappingNotify extends Event {
    private final int count;
    private final int first_keycode;
    private final Request request;

    /* loaded from: classes.dex */
    public enum Request {
        MODIFIER,
        KEYBOARD,
        POINTER
    }

    public MappingNotify(Request request, int i, int i2) {
        super(34);
        this.request = request;
        this.first_keycode = i;
        this.count = i2;
    }

    public Request getRequest() {
        return this.request;
    }

    public int getFirstKeycode() {
        return this.first_keycode;
    }

    public int getCount() {
        return this.count;
    }
}
