package com.eltechs.ed.controls;

/**
 * 用于存储显示操作模式介绍的对话框内容
 */
public class ControlsInfoElem {
    public final String mDescrText;
    public final String mHeaderText;
    public final int mImageRes;

    public ControlsInfoElem(int i, String str, String str2) {
        this.mImageRes = i;
        this.mHeaderText = str;
        this.mDescrText = str2;
    }
}
