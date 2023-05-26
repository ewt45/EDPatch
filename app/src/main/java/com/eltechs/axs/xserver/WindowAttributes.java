package com.eltechs.axs.xserver;

import com.eltechs.axs.xserver.impl.masks.Mask;

/* loaded from: classes.dex */
public class WindowAttributes {
    private final Window window;
    private final WindowChangeListenersList windowChangeListenersList;
    private final WindowClass windowClass;
    private int borderWidth;
    private Cursor cursor;
    private BackingStore backingStore = BackingStore.NOT_USEFUL;
    private BitGravity bitGravity = BitGravity.CENTER;
    private WinGravity winGravity = WinGravity.CENTER;
    private int backingPlanes = 1;
    private int backingPixel = 0;
    private boolean saveUnder = false;
    private boolean isMapped = false;
    private boolean overrideRedirect = false;
    /**
     * 禁止向父窗口传递的事件类型。如果要找禁止在本窗口或子窗口传递的事件类型，应该看WindowListener（XClientWindowListener）内的eventMask
     */
    private Mask<EventName> doNotPropagateMask = Mask.emptyMask(EventName.class);

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

    /**
     * 获取该窗口对应的光标
     *
     * @return 自己的光标。当自己的光标为null时，尝试获取父窗口的光标并返回
     */
    public Cursor getCursor() {
        //如果自己光标为null，但需要用光标了，则使用父窗口的光标
        if (cursor == null && window.getParent()!=null)
            return window.getParent().getWindowAttributes().getCursor();
        return this.cursor;
    }

    public void setCursor(Cursor cursor) {
        this.cursor = cursor;
    }

    public void update(Mask<WindowAttributeNames> attrMask, Integer borderPixmap, Integer borderPixel, BitGravity bitGravity, WinGravity winGravity, BackingStore backingStore, Integer backingPlanes, Integer backingPixel, Boolean overrideRedirect, Boolean saveUnder, Mask<EventName> doNotPropagateMask, Integer colormap, Cursor cursor) {
        if (attrMask.isSet(WindowAttributeNames.BACKING_PIXEL)) {
            this.backingPixel = backingPixel;
        }
        if (attrMask.isSet(WindowAttributeNames.BACKING_PLANES)) {
            this.backingPlanes = backingPlanes;
        }
        if (attrMask.isSet(WindowAttributeNames.BIT_GRAVITY)) {
            this.bitGravity = bitGravity;
        }
        if (attrMask.isSet(WindowAttributeNames.WIN_GRAVITY)) {
            this.winGravity = winGravity;
        }
        if (attrMask.isSet(WindowAttributeNames.BACKING_STORE)) {
            this.backingStore = backingStore;
        }
        if (attrMask.isSet(WindowAttributeNames.SAVE_UNDER)) {
            this.saveUnder = saveUnder;
        }
        if (attrMask.isSet(WindowAttributeNames.OVERRIDE_REDIRECT)) {
            this.overrideRedirect = overrideRedirect;
        }
        if (attrMask.isSet(WindowAttributeNames.DO_NOT_PROPAGATE_MASK)) {
            this.doNotPropagateMask = doNotPropagateMask;
        }
        if (attrMask.isSet(WindowAttributeNames.CURSOR)) {
            this.cursor = cursor;
        }
        this.windowChangeListenersList.sendWindowAttributeChanged(this.window, attrMask);
    }

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
}