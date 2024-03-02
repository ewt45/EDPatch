package com.example.datainsert.exagear.controlsV2.gestureMachine.adapter;

import com.example.datainsert.exagear.controlsV2.Const;

public class MouseMoveSimpleAdapter extends MouseMoveAdapter {

    float startX, startY;

    public MouseMoveSimpleAdapter() {
    }

    @Override
    public void moveTo(float x, float y) {
        //应用移动速度
        float speed = Const.getActiveProfile().getMouseMoveSpeed();
        float nowXApplySpeed = startX + (x - startX) * speed;
        float nowYApplySpeed = startY + (y - startY) * speed;
        getPointReporter().pointerMove(nowXApplySpeed, nowYApplySpeed);
    }

    @Override
    public void prepareMoving(float x, float y) {
//        per.pointerMove(x, y);
        startX = x;
        startY = y;
    }
}
