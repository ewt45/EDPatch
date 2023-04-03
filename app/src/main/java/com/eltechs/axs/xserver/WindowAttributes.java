package com.eltechs.axs.xserver;

import com.eltechs.axs.xserver.impl.masks.Mask;

/* loaded from: classes.dex */
public class WindowAttributes {
    private int borderWidth;
    private Cursor cursor;
    private final Window window;
    private final WindowChangeListenersList windowChangeListenersList;
    private final WindowClass windowClass;
    private BackingStore backingStore = BackingStore.NOT_USEFUL;
    private BitGravity bitGravity = BitGravity.CENTER;
    private WinGravity winGravity = WinGravity.CENTER;
    private int backingPlanes = 1;
    private int backingPixel = 0;
    private boolean saveUnder = false;
    private boolean isMapped = false;
    private boolean overrideRedirect = false;
    private Mask<EventName> doNotPropagateMask = Mask.emptyMask(EventName.class);

    /* loaded from: classes.dex */
    public enum BackingStore {
        NOT_USEFUL,
        WHEN_MAPPED,
        ALWAYS
    }

    /* loaded from: classes.dex */
    public enum WindowClass {
        INPUT_OUTPUT,
        INPUT_ONLY
    }

    public WindowAttributes(WindowClass windowClass, WindowChangeListenersList windowChangeListenersList, Window window) {
        this.windowClass = windowClass;
        this.windowChangeListenersList = windowChangeListenersList;
        this.window = window;
    }

    public BackingStore getBackingStore() {
        return this.backingStore;
    }

    public WindowClass getWindowClass() {
        return this.windowClass;
    }

    public BitGravity getBitGravity() {
        return this.bitGravity;
    }

    public WinGravity getWinGravity() {
        return this.winGravity;
    }

    public int getBackingPlanes() {
        return this.backingPlanes;
    }

    public int getBackingPixel() {
        return this.backingPixel;
    }

    public boolean isSaveUnder() {
        return this.saveUnder;
    }

    public boolean isMapped() {
        return this.isMapped;
    }

    public void setMapped(boolean z) {
        this.isMapped = z;
    }

    public boolean isOverrideRedirect() {
        return this.overrideRedirect;
    }

    public Mask<EventName> getDoNotPropagateMask() {
        return this.doNotPropagateMask;
    }

    public int getBorderWidth() {
        return this.borderWidth;
    }

    public void setBorderWidth(int i) {
        this.borderWidth = i;
    }

    public Cursor getCursor() {
        return this.cursor;
    }

    public void setCursor(Cursor cursor) {
        this.cursor = cursor;
    }

    public void update(Mask<WindowAttributeNames> mask, Integer num, Integer num2, BitGravity bitGravity, WinGravity winGravity, BackingStore backingStore, Integer num3, Integer num4, Boolean bool, Boolean bool2, Mask<EventName> mask2, Integer num5, Cursor cursor) {
        if (mask.isSet(WindowAttributeNames.BACKING_PIXEL)) {
            this.backingPixel = num4.intValue();
        }
        if (mask.isSet(WindowAttributeNames.BACKING_PLANES)) {
            this.backingPlanes = num3.intValue();
        }
        if (mask.isSet(WindowAttributeNames.BIT_GRAVITY)) {
            this.bitGravity = bitGravity;
        }
        if (mask.isSet(WindowAttributeNames.WIN_GRAVITY)) {
            this.winGravity = winGravity;
        }
        if (mask.isSet(WindowAttributeNames.BACKING_STORE)) {
            this.backingStore = backingStore;
        }
        if (mask.isSet(WindowAttributeNames.SAVE_UNDER)) {
            this.saveUnder = bool2.booleanValue();
        }
        if (mask.isSet(WindowAttributeNames.OVERRIDE_REDIRECT)) {
            this.overrideRedirect = bool.booleanValue();
        }
        if (mask.isSet(WindowAttributeNames.DO_NOT_PROPAGATE_MASK)) {
            this.doNotPropagateMask = mask2;
        }
        if (mask.isSet(WindowAttributeNames.CURSOR)) {
            this.cursor = cursor;
        }
        this.windowChangeListenersList.sendWindowAttributeChanged(this.window, mask);
    }
}