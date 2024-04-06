package com.example.datainsert.exagear.controlsV2.model;

import static com.example.datainsert.exagear.controlsV2.Const.dp8;
import static com.example.datainsert.exagear.controlsV2.Const.minStickAreaSize;

import android.support.annotation.IntDef;

import com.example.datainsert.exagear.controlsV2.Const;
import com.example.datainsert.exagear.controlsV2.TestHelper;
import com.example.datainsert.exagear.controlsV2.TouchAreaModel;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

public class OneStick extends TouchAreaModel {
    public static final int WAY_4 = 0;
    public static final int WAY_8 = 1;
    public static final int WAY_MOUSE = 2;//鼠标移动
    public static final int KEY_LEFT = 0;
    public static final int KEY_TOP = 1;
    public static final int KEY_RIGHT = 2;
    public static final int KEY_BOTTOM = 3;
    public @HowManyDirections int direction = WAY_8;
    /**
     * 宽高相等的大小
     */
    protected int size;

    //用于反射，请勿删除
    public OneStick() {
        this(TYPE_STICK);
    }

    //keycodes 分别为上下左右
    OneStick(int type) {
        super(type);
        mMinAreaSize = minStickAreaSize;
        width = mMinAreaSize;
        height = mMinAreaSize;
        size = width;
        //确保初始化完了有且只有4个keycode
        mendToOnly4Keycodes();
    }

    @Override
    public void setWidth(int width) {
        setSize(width);
    }

    @Override
    public void setHeight(int height) {
        setSize(height);
    }

    public void setSize(int newSize) {
        this.size = Math.max(TestHelper.makeMultipleOf(dp8/2,newSize), minStickAreaSize);
        this.width = this.size;
        this.height  = this.size;
    }

    /**
     * 获取此区域的正方形边长（外圆的半径）
     */
    public int getSize() {
        return size;
    }

    /**
     * 获取外圆半径，为区域边长的一半
     */
    public float getOuterRadius() {
        return size / 2f;
    }

    /**
     * 获取内圆半径,为外圆半径*Const.stickInnerOuterRatio
     */
    public float getInnerRadius() {
        return getOuterRadius() * Const.stickInnerOuterRatio;
    }

    /**
     * 获取内圆圆心距离外圆圆心的最远距离。为外圆半径*Const.stickInnerMaxOffOuterRadiusRatio
     */
    public float getInnerMaxOffsetFromOuterCenter() {
        return getOuterRadius() * Const.stickInnerMaxOffOuterRadiusRatio;
    }

    public void setKeycodeAt(int keycode, @KeyPos int pos) {
        List<Integer> keycodes = getKeycodes();
        keycodes.remove(pos);
        keycodes.add(pos, keycode);
    }

    public int getKeycodeAt(@KeyPos int pos) {
        return getKeycodes().get(pos);
    }

    @Override
    public void setKeycodes(List<Integer> newKeys) {
        super.setKeycodes(newKeys);
        mendToOnly4Keycodes();
    }

    private void mendToOnly4Keycodes() {
        while (keycodes.size() < 4)
            keycodes.add(0);
        while (keycodes.size() > 4)
            keycodes.remove(keycodes.size() - 1);
    }

    public @HowManyDirections int getDirection(){
        return direction;
    }

    @Override
    protected void cloneSelfFields(TouchAreaModel ref) {
        if (ref.getClass().equals(OneStick.class)) {
            OneStick ref2 = (OneStick) ref;
            direction = ref2.direction;
            size = ref2.size;
        }
    }


    /**
     * 移动时，是只有四个方向还是有八个方向（允许斜向）
     */
    @IntDef({WAY_4, WAY_8, WAY_MOUSE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface HowManyDirections {
    }

    /**
     * model的按键列表中，对应方向的按键的索引。
     * 获取对应方向的按键：model.getKeycodeAt(KeyPos)
     */
    @IntDef({KEY_LEFT, KEY_TOP, KEY_RIGHT, KEY_BOTTOM})
    @Retention(RetentionPolicy.SOURCE)
    public @interface KeyPos {
    }
}
