package com.example.datainsert.exagear.controlsV2.model;


import com.example.datainsert.exagear.controlsV2.Const;
import com.example.datainsert.exagear.controlsV2.TouchAreaModel;

import java.util.Locale;

public class OneButton extends TouchAreaModel {
    @Const.BtnShape
    public int shape = Const.BtnShape.RECT;
    public boolean isTrigger = false;
    transient private int triggerFlag=0;

    //用于反射，请勿删除
    public OneButton() {
        super(TYPE_BUTTON);
        mMinAreaSize = Const.minBtnAreaSize;
        width = mMinAreaSize;
        height = mMinAreaSize;
    }

    /**
     * 处理连发的逻辑，传入的值不一定是设置到成员变量isPressed上的值
     */
    @Override
    public void setPressed(boolean pressed) {
        if(!isTrigger){
            isPressed = pressed;
        }else{
            triggerFlag = (triggerFlag+1)%2;
            if(triggerFlag==1)
                isPressed = !isPressed;
        }
    }

    @Override
    public boolean isPressed() {
        return super.isPressed();
    }

    public String getCoordinateString() {
        return String.format(Locale.ROOT, "%d,%d", left, top);
    }

    @Override
    protected void cloneSelfFields(TouchAreaModel ref) {
        if(ref.getClass().equals(OneButton.class)){
            OneButton ref2 = (OneButton) ref;
            shape = ref2.shape;
            isTrigger = ref2.isTrigger;
        }
    }
}
