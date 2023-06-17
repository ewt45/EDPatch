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
    private int offWindowLimit = 0;

    //未经调整到视图内的坐标
    int lastXUnmodify;
    int lastYUnmodify;
    public Pointer(XServer xServer) {
        this.xServer = xServer;
    }

    public boolean isButtonValid(byte b) {
        return b >= 1 && b <= 7;
    }

    public int getX() {
        return this.xPos;
    }

    public int getY() {
        return this.yPos;
    }

    public void setOffWindowLimit(int offWindowLimit) {
        this.offWindowLimit = offWindowLimit;
    }

    /**
     * 这个是更新自身坐标的，会限制在视图的范围内（这个改到窗口外会闪退。。）
     */
    private void updateCoordinates(int x, int y) {
        this.xPos = ArithHelpers.unsignedSaturate(x, this.xServer.getScreenInfo().widthInPixels - 1);
        this.yPos = ArithHelpers.unsignedSaturate(y, this.xServer.getScreenInfo().heightInPixels - 1);
        lastXUnmodify = x;
        lastYUnmodify = y;

    }


    public void setCoordinates(int x, int y) {
        int lastX = lastXUnmodify;
        int lastY = lastYUnmodify;
//        Log.d(TAG, String.format("setCoordinates: 原始输入坐标(%d,%d)",x,y));
        //这样貌似也不行。得想办法让linux内部一直移动才行。应该在这些listener里有一个是给linux传输入的。就改哪一个，其他的不变? 图标样式的也要固定位置才行
        updateCoordinates(x, y);
        if (offWindowLimit!=0) {

//            xPos = this.xServer.getScreenInfo().widthInPixels/2;
//            yPos = xServer.getScreenInfo().heightInPixels/2;

//            //试试move改warp呢（不行啊光标也会跟着移动到中心）
//            if(x>xServer.getScreenInfo().widthInPixels || x<0
//            || y > xServer.getScreenInfo().heightInPixels || y<0){
//                updateCoordinates(xServer.getScreenInfo().widthInPixels/2,xServer.getScreenInfo().heightInPixels/2);
//                this.listeners.sendPointerWarped(xPos,yPos);
//            }else{
//                this.listeners.sendPointerMoved(this.xPos, this.yPos);
//            }

            //如果超出视图，固定超出1/32（x) 1(放到adapter里试试？）
            if (x > xServer.getScreenInfo().widthInPixels)
                x = xServer.getScreenInfo().widthInPixels +offWindowLimit;
            else if (x < 0)
                x = -offWindowLimit;

            if (y > xServer.getScreenInfo().heightInPixels)
                y = xServer.getScreenInfo().heightInPixels +offWindowLimit;
            else if (y < 0)
                y = -offWindowLimit;

//            //始终偏移1？(也不行，乱飞）
//            if((x-lastX)<0)
//                x =-1;
//            else if((x-lastX)>0)
//                x = xServer.getScreenInfo().widthInPixels +offWindowLimit;
//            else x= 0;
//            if((y-lastY)<0)
//                y =-1;
//            else if((y-lastY)>0)
//                y = xServer.getScreenInfo().heightInPixels +offWindowLimit;
//            else y= 0;
            //难道说允许移出画面尺寸就可以 鼠标到边界，游戏内仍然可以移动视角了吗（好像是哎）
            this.listeners.sendPointerMoved(x, y);
        } else {
            this.listeners.sendPointerMoved(this.xPos, this.yPos);
        }


        //updateCoordinates(x, y);
        //this.listeners.sendPointerMoved(this.xPos, this.yPos);
    }

    public void warpOnCoordinates(int i, int i2) {
        updateCoordinates(i, i2);
        this.listeners.sendPointerWarped(this.xPos, this.yPos);
    }

    public boolean isButtonPressed(int i) {
        return this.buttons.isSet(KeyButNames.getFlagForButtonNumber(i));
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
//                Log.d(TAG, "setButton: 按下鼠标键" + keyFlag);

                this.listeners.sendPointerButtonPressed(keycode);
            } else {
//                Log.d(TAG, "setButton: 松开鼠标键" + keyFlag);
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
