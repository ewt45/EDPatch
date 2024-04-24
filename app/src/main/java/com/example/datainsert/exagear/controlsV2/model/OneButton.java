package com.example.datainsert.exagear.controlsV2.model;


import com.example.datainsert.exagear.controlsV2.Const;
import com.example.datainsert.exagear.controlsV2.TouchAreaModel;

import java.util.Locale;

public class OneButton extends TouchAreaModel {
    @Const.BtnShape
    private int shape = Const.BtnShape.RECT;
    private boolean isTrigger = false;
    private String name = null; //还是允许为null吧

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

    public void setTrigger(boolean trigger) {
        isTrigger = trigger;
    }

    public boolean isTrigger() {
        return isTrigger;
    }

    public @Const.BtnShape int getShape() {
        return shape;
    }

    public void setShape(@Const.BtnShape int shape) {
        this.shape = shape;
    }

    /**
     * 返回用户友好的别名。若用户没有设置，则该别名为该按钮所有按键码的别名（getKeycodesString()）
     */
    public String getName() {
        return name != null ? name : getKeycodesString();
    }

    /**
     * 更新该按钮的别名。若别名trim后为长度为0或与getKeycodesString()值相等，则name设为null
     * <br/> 会去掉前后空格，换行换成空格
     */
    public void setName(String newName) {
        newName = newName.replace("\n"," ").trim(); //去掉前后空格，换行换成空格
        this.name = (newName.isEmpty() || newName.equals(getKeycodesString())) ? null : newName;
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
            name = ref2.name;
        }
    }
}
