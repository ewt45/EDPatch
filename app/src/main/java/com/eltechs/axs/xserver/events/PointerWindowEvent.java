package com.eltechs.axs.xserver.events;

import com.eltechs.axs.xserver.KeyButNames;
import com.eltechs.axs.xserver.Window;
import com.eltechs.axs.xserver.impl.masks.Mask;

/* loaded from: classes.dex */
public abstract class PointerWindowEvent extends Event {
    private final Window child;
    private final Detail detail;
    private final Window event;
    private final short eventX;
    private final short eventY;
    private final Mode mode;
    private final Window root;
    private final short rootX;
    private final short rootY;
    private final byte sameScreenAndFocus;
    private final Mask<KeyButNames> state;
    private final int timestamp;

    /* loaded from: classes.dex */
    public enum Detail {
        ANCESTOR,
        VIRTUAL,
        INFERIOR,
        NONLINEAR,
        NONLINEAR_VIRTUAL
    }

    /* loaded from: classes.dex */
    public enum Mode {
        NORMAL,
        GRAB,
        UNGRAB
    }

    /* JADX INFO: Access modifiers changed from: protected */
    protected PointerWindowEvent(int i, Detail detail, Mode mode, int timestamp, Window root, Window event, Window child, short rootX, short rootY, short eventX, short eventY, Mask<KeyButNames> state, boolean focus) {
        super(i);
        this.detail = detail;
        this.mode = mode;
        this.timestamp = timestamp;
        this.root = root;
        this.event = event;
        this.child = child;
        this.rootX = rootX;
        this.rootY = rootY;
        this.eventX = eventX;
        this.eventY = eventY;
        this.state = state;
        //意思是sameScreen一直是1.占用第二位。第一位是focus
        this.sameScreenAndFocus = (byte) ((focus ? 1 : 0) | 2);
    }

    public int getTimestamp() {
        return this.timestamp;
    }

    public Window getRoot() {
        return this.root;
    }

    public Window getEvent() {
        return this.event;
    }

    public Window getChild() {
        return this.child;
    }

    public short getRootX() {
        return this.rootX;
    }

    public short getRootY() {
        return this.rootY;
    }

    public short getEventX() {
        return this.eventX;
    }

    public short getEventY() {
        return this.eventY;
    }

    public Mask<KeyButNames> getState() {
        return this.state;
    }

    public byte getSameScreenAndFocus() {
        return this.sameScreenAndFocus;
    }

    public Detail getDetail() {
        return this.detail;
    }

    public Mode getMode() {
        return this.mode;
    }
}
