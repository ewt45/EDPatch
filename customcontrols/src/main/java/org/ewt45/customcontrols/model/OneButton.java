package org.ewt45.customcontrols.model;

import org.ewt45.customcontrols.Const;

public class OneButton extends TouchAreaModel{
    public  int type = Const.BTN_TYPE_NORMAL;
    public String text = "";
    public int keycode=0;
    public int bgColor;
    public int left=0;
    public int top=0;
    public boolean isTrigger = false;
}
