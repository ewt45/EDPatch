package com.example.datainsert.exagear.controls.model;

import static com.example.datainsert.exagear.RR.getS;

import com.example.datainsert.exagear.RR;

import java.io.Serializable;

public class JoyParams implements Serializable {
    private static final long serialVersionUID = 5962021171151927361L;
    static final String TAG = "JoyStickParams";
    //四个方向的按键(0123对应上下左右）
    int[] key4Directions = new int[]{-1, -1, -1, -1};
    /**
     * presetKey为CUSTOM时使用自定义的key4Directions
     */
    PresetKey presetKey;
    //布局位置
    private int marginLeft;
    private int marginTop;
    //是否用4个方向。false的话就是8个方向
    private boolean isFourDirections;

    /**
     * 默认上下左右是wasd
     */
    public JoyParams() {
        presetKey = PresetKey.WASD;
        key4Directions = presetKey.getKeys();
    }

    public JoyParams(int[] keys) {
        key4Directions = keys;
    }

    public void setKey4Directions(int[] key4Directions) {
        this.key4Directions = key4Directions;
    }

    public int getMarginLeft() {
        return marginLeft;
    }

    public void setMarginLeft(int marginLeft) {
        this.marginLeft = marginLeft;
    }

    public int getMarginTop() {
        return marginTop;
    }

    public void setMarginTop(int marginTop) {
        this.marginTop = marginTop;
    }

    public boolean isFourDirections() {
        return isFourDirections;
    }

    public void setFourDirections(boolean fourDirections) {
        isFourDirections = fourDirections;
    }

    public void setPresetKey(PresetKey presetKey) {
        this.presetKey = presetKey;
        this.key4Directions = presetKey.getKeys();
    }

    public PresetKey getPresetKey() {
        return presetKey;
    }

    public int[] getKey4Directions() {
        return key4Directions;
    }

    public enum PresetKey {
        WASD(new int[]{17, 31, 30, 32}, "W A S D"),
        ARROWS(new int[]{103, 108, 105, 106}, "↑ ↓ ← →"),
        MOUSE_MOVE(new int[]{0,0,0,0}, "\uD83D\uDDB1️️"),//🖱️
        MOUSE_LEFT_CLICK(new int[]{1, 1, 1, 1}, "鼠标左键点击"),
        MOUSE_RIGHT_CLICK(new int[]{3, 3, 3, 3}, "鼠标右键点击"),
        CUSTOM(new int[]{17, 31, 30, 32}, getS(RR.cmCtrl_JoyEditKeyCstm)),
        ;

        private final int[] keys;
        private final String name;

        PresetKey(int[] ints, String name) {
            keys = ints;
            this.name = name;
        }
        public String getName(){return name;}
        public int[] getKeys() {
            return keys;
        }
    }
}
