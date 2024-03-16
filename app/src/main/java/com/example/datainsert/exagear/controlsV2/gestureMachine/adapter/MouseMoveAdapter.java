package com.example.datainsert.exagear.controlsV2.gestureMachine.adapter;

import com.example.datainsert.exagear.controlsV2.axs.AndroidPointReporter;
import com.example.datainsert.exagear.controlsV2.Const;

public abstract class MouseMoveAdapter {
    public MouseMoveAdapter(){
    }
    public abstract void moveTo(float x, float y);

    public abstract void prepareMoving(float x, float y);
}
