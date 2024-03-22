package com.example.datainsert.exagear.controlsV2.gestureMachine.adapter;

import android.util.Log;

import com.example.datainsert.exagear.controlsV2.Const;

/**
 * 相对移动，最终调用到ViewFacade.injectPointerDelta，xegw重写ViewFacade即可
 */
public class MouseMoveRelativeAdapter extends MouseMoveAdapter {
    int[] pStart = new int[2];
    int[] pLast = new int[2];

    @Override
    public void moveTo(float x, float y) {
        //因为exa发送坐标只有int而不是float，所以会丢失精度。
        //所以pLast记录的并非上一次手指位置，而是上上一次位置再偏移上一次float强转int后的位置
        //比如移动过程是0, 0.2, 0.5, 0.7, 1.2， 那么0.2 0.5 0.7的时候pLast都是0，鼠标移动距离也是0，直到1.2的时候，pLast改为1，鼠标也移动1
        //emmm也不行，因为从view转到x11单位时精度还会变，干脆不用AndroidPointerReporter了吧，直接存x11单位的坐标
        int[] pNow = mapToXUnit(x, y);
        Const.getXServerHolder().injectPointerDelta(pNow[0] - pLast[0], pNow[1] - pLast[1]);
        Log.d("相对移动", "moveTo: 移动xy="+(pNow[0] - pLast[0])+","+(pNow[1] - pLast[1]));
        pLast = pNow;
    }

    @Override
    public void prepareMoving(float x, float y) {
        pStart = mapToXUnit(x, y);
        pLast = mapToXUnit(x, y);
    }

    /**
     * 将view单位坐标转为x单位坐标并放入int数组中返回
     */
    private int[] mapToXUnit(float x, float y) {
        float[] fp = {x, y};
        Const.getXServerHolder().getViewToXServerTransformationMatrix().mapPoints(fp);
        return new int[]{(int) fp[0], (int) fp[1]};
    }
};