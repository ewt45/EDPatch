package com.example.datainsert.exagear.controlsV2.gestureMachine.adapter;

import com.example.datainsert.exagear.controlsV2.Const;

public abstract class MouseMoveAdapter {
    private final AndroidPointReporter per;
    public MouseMoveAdapter(){
        per = Const.getGestureContext().getPointerReporter();
    }
    protected AndroidPointReporter getPointReporter(){
        return per;
    }
    public abstract void moveTo(float x, float y);

    public abstract void prepareMoving(float x, float y);
}
