package com.example.datainsert.exagear.controls.model;

import android.support.annotation.Nullable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class OneKey implements Serializable {
    private static final long serialVersionUID = 3575276037755523286L;
    int code; //这个用原始的，不+8//https://elixir.bootlin.com/linux/v4.9/source/include/uapi/linux/input-event-codes.h#L74
    String name;

    //下面是为自定义位置准备的属性
    boolean mIsShow = true;
    int marginLeft=0;
    int marginTop=0;
    /**
     * 用于组合键
     */
    List<Integer> subCodes = new ArrayList<>();
    /**
     * 是否松手后保持按下
     */
    boolean mIsTrigger = false;

    public OneKey(int code) {
        this(code, "KEYCODE_"+ code);
    }

    public OneKey(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public boolean isShow() {
        return mIsShow;
    }

    public void setShow(boolean mIsShow) {
        this.mIsShow = mIsShow;
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

    public List<Integer> getSubCodes() {
        return subCodes;
    }

    public void setSubCodes(List<Integer> subCodes) {
        this.subCodes = subCodes;
    }

    public boolean isTrigger() {
        return mIsTrigger;
    }

    public void setTrigger(boolean mIsTrigger) {
        this.mIsTrigger = mIsTrigger;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (!(obj instanceof OneKey))
            return false;
        OneKey key = (OneKey) obj;
        return key.getCode() == this.code && key.getName().equals(this.name);
    }

    public OneKey clone() {
        //没复制margin isshow那些，不过keycode3也用不着clone
        OneKey oneKey = new OneKey(this.code, this.name );
        oneKey.mIsShow = this.mIsShow;
        oneKey.marginLeft = this.marginLeft;
        oneKey.marginTop = this.marginTop;
        oneKey.mIsTrigger = this.mIsTrigger;
        oneKey.subCodes = new ArrayList<>(this.subCodes);
        return  oneKey;
    }

    /**
     * 当按钮从隐藏变为显示时，清空之前设置的属性
     */
    public void clearPropertiesWhenShow() {
        marginLeft=0;
        marginTop=0;
        subCodes.clear();
        mIsTrigger=false;
    }

}
