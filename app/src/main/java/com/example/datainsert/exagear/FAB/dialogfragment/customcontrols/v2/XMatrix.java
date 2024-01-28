package com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2;

import android.graphics.Matrix;

/**
 * 用于缩放xserver显示，
 * 受影响的因素：拉伸/按比例全屏，缩放倍数，xy偏移距离
 * 功能：将android view接收到的手指坐标正确转化到xserver鼠标坐标
 */
public class XMatrix extends Matrix {
    private float mXViewOffsetX;
    private float mXViewOffsetY;
    private float mAViewOffsetX;
    private float mAViewOffsetY;
    private float mScaleX;
    private float mScaleY;
    public void reInflate(){

    }

    public void updateDelta(){
//        Matrix m = new Matrix();
//        m.
//        m.setTranslate();
//        m.postTranslate()
    }
}
