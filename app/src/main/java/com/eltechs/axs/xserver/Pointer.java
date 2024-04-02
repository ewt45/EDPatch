package com.eltechs.axs.xserver;

import com.eltechs.axs.helpers.ArithHelpers;
import com.eltechs.axs.xserver.impl.masks.Mask;

public class Pointer {
    public static final int BUTTON_LEFT = 1;
    public static final int BUTTON_CENTER = 2;
    public static final int BUTTON_RIGHT = 3;
    public static final int BUTTON_SCROLL_UP = 4;
    public static final int BUTTON_SCROLL_DOWN = 5;
    public static final int BUTTON_SCROLL_CLICK_LEFT = 6;
    public static final int BUTTON_SCROLL_CLICK_RIGHT = 7;
    public static final int MAX_BUTTONS = 7;
    private static final String TAG = "Pointer";
    private final Mask<KeyButNames> buttons = Mask.emptyMask(KeyButNames.class);
    private final PointerListenersList listeners = new PointerListenersList();
    private final XServer xServer;
    private int xPos;
    private int yPos;
    public Pointer(XServer xServer) {
        this.xServer = xServer;
    }

    public boolean isButtonValid(byte button) {
        return button >= 1 && button <= 7;
    }

    public int getX() {
        return this.xPos;
    }

    public int getY() {
        return this.yPos;
    }

    /**
     * 这个是更新自身坐标的，会限制在视图的范围内（这个改到窗口外会闪退。。）
     */
    private void updateCoordinates(int x, int y) {
        this.xPos = ArithHelpers.unsignedSaturate(x, this.xServer.getScreenInfo().widthInPixels - 1);
        this.yPos = ArithHelpers.unsignedSaturate(y, this.xServer.getScreenInfo().heightInPixels - 1);
    }

    public void setCoordinates(int x, int y) {
        updateCoordinates(x, y);
        this.listeners.sendPointerMoved(this.xPos, this.yPos);
    }

    public void warpOnCoordinates(int x, int y) {
        updateCoordinates(x, y);
        this.listeners.sendPointerWarped(this.xPos, this.yPos);
    }

    public boolean isButtonPressed(int button) {
        return this.buttons.isSet(KeyButNames.getFlagForButtonNumber(button));
    }

    /**
     * 设置鼠标按键。
     *
     * @param keycode 哪个按键
     * @param isPress 按下还是松开
     */
    public void setButton(int keycode, boolean isPress) {
        KeyButNames keyFlag = KeyButNames.getFlagForButtonNumber(keycode);
        boolean isSet = this.buttons.isSet(keyFlag);
        this.buttons.setValue(keyFlag, isPress);
        if (isSet != isPress) {
            if (isPress) {
                this.listeners.sendPointerButtonPressed(keycode);
            } else {
                this.listeners.sendPointerButtonReleased(keycode);
            }
        }
    }

    public Mask<KeyButNames> getButtonMask() {
        return this.buttons;
    }

    public void addListener(PointerListener pointerListener) {
        this.listeners.addListener(pointerListener);
    }

    public void removeListener(PointerListener pointerListener) {
        this.listeners.removeListener(pointerListener);
    }
}
