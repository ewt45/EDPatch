package com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.model;

import static com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.Const.minTouchSize;

import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.Const;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class TouchAreaModel {

    //TODO 保证编辑完成时改值不为空
    public String name = "key";
    public List<Integer> keycodes = new ArrayList<>(Collections.singletonList(0));
    public int mainColor = Const.defaultBgColor;
    @Const.BtnColorStyle
    public int colorStyle = Const.BtnColorStyle.STROKE;
    protected int left = 0;
    protected int top = 0;
    protected int width = minTouchSize;
    protected int height = minTouchSize;

    protected TouchAreaModel() {
    }



    public int getLeft() {
        return left;
    }

    public void setLeft(int left) {
        this.left = Math.max(0, left);
    }

    public int getTop() {
        return top;
    }

    public void setTop(int top) {
        this.top = Math.max(0, top);
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = Math.max(minTouchSize, width);
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = Math.max(minTouchSize, height);
    }

    public String getKeycodesString() {
        //TODO keycode转为对应的文字名称
        StringBuilder builder = new StringBuilder();
        for (Integer key : keycodes)
            builder.append(key).append(", ");
        if (builder.length() > 2)
            builder.delete(builder.length() - 2, builder.length());
        return builder.toString();
    }
}
