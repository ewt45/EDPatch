package com.eltechs.axs.xserver;

import android.util.Log;

import com.eltechs.axs.helpers.ArithHelpers;
import com.termux.x11.ViewForRendering;
import com.eltechs.axs.xserver.impl.masks.Mask;
import com.termux.x11.input.InputStub;

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
    //未经调整到视图内的坐标
    int lastXUnmodify;
    int lastYUnmodify;
    private int xPos;
    private int yPos;
    public Pointer(XServer xServer) {
        this.xServer = xServer;
        //初始别放左上角了 看不见
        xPos=100;
        yPos=100;
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


    public void setButton(int keycode, boolean isPress) {
        Log.d(TAG, String.format("setButton: 鼠标按键：keycode=%d, isPress=%s",keycode,isPress));
        KeyButNames keyFlag = KeyButNames.getFlagForButtonNumber(keycode);
        boolean isSet = this.buttons.isSet(keyFlag);
        this.buttons.setValue(keyFlag, isPress);
        if (isSet != isPress) {
            //第一版xegw的用法
//            if (isPress) {
//                RealXServer.click(keycode, 1);
//                this.listeners.sendPointerButtonPressed(keycode);
//            } else {
//                RealXServer.click(keycode, 0);
////                this.listeners.sendPointerButtonReleased(keycode);//没有release的通知吗
//            }

            ViewForRendering.mouseEvent(xPos, yPos, keycode, isPress, false);
        }
    }

    public void setCoordinates(int x, int y) {
        int deltaX = x-xPos;
        int deltaY = y-yPos;
        updateCoordinates(x, y);
        //第一版xegw的写法
//        RealXServer.motion(x, y);

        ViewForRendering.mouseEvent(x,y, InputStub.BUTTON_UNDEFINED,false,false);
//        ViewForRendering.mouseEvent(deltaX,deltaY,0,false,true); //tx11还没实现
//        Log.d(TAG, String.format("鼠标移动到( %d , %d )",x,y));
    }

    public void warpOnCoordinates(int x, int y) {
        updateCoordinates(x, y);
        this.listeners.sendPointerWarped(this.xPos, this.yPos);
    }

    public boolean isButtonPressed(int button) {
        return this.buttons.isSet(KeyButNames.getFlagForButtonNumber(button));
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
