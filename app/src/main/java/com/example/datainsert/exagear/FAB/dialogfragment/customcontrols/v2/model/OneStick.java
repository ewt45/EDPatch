package com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.model;

import static com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.Const.minStickAreaSize;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

public class OneStick extends TouchAreaModel {
    public static final int WAY_8 = 1;
    public static final int WAY_4 = 0;
    public static final int KEY_LEFT = 0;
    public static final int KEY_TOP = 1;
    public static final int KEY_RIGHT = 2;
    public static final int KEY_BOTTOM = 3;
    //TODO adapter里实现这个
    public @HowManyDirections int direction = WAY_8;
    /**
     * 宽高相等的大小
     */
    protected int size;
    OneStick(){
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
        super.setWidth(width);
        this.size = Math.max(this.width, height);
    }

    @Override
    public void setHeight(int height) {
        super.setHeight(height);
        this.size = Math.max(this.width, height);
    }

    public int getSize() {
        return size;
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

    @Override
    protected void cloneSelfFields(TouchAreaModel ref) {
        if(ref.getClass().equals(OneStick.class)){
            OneStick ref2 = (OneStick) ref;
            direction = ref2.direction;
            size = ref2.size;
        }
    }

    @IntDef({WAY_4, WAY_8})
    @Retention(RetentionPolicy.SOURCE)
    public @interface HowManyDirections {
    }

    @IntDef({KEY_LEFT, KEY_TOP, KEY_RIGHT, KEY_BOTTOM})
    @Retention(RetentionPolicy.SOURCE)
    public @interface KeyPos {
    }
}
