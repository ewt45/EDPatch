package com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.model;


import static com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.Const.minTouchSize;

import android.os.Bundle;

import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.Const;

import java.util.Locale;

public class OneButton extends TouchAreaModel{
    protected OneButton() {
    }
    @Const.BtnShape
    public int shape = Const.BtnShape.RECT;
    public boolean isTrigger = false;

    /**
     * 新建一个model的实例。
     * @param reference 若该参数不为null，则尽可能的将该model的数据拷贝到新实例中。
     */
    public static OneButton newInstance(TouchAreaModel reference) {
        //TODO 这个移到TouchAreaModel层？但是返回值就只能是抽象类TouchAreaModel，不能具体哪个了
        OneButton one = new OneButton();

        if(reference!=null){
            one.left = reference.getLeft();
            one.top=reference.getTop();
            one.width=reference.getWidth();
            one.height=reference.getHeight();
            one.colorStyle =reference.colorStyle;
            one.mainColor = reference.mainColor;
            //TODO 这样会导致联动吗
            one.name = reference.name;
            one.keycodes.clear();
            one.keycodes.addAll(reference.keycodes);
        }

        return one;
    }

    public String getCoordinateString() {
        return String.format(Locale.ROOT,"%d,%d",left,top);
    }
}
