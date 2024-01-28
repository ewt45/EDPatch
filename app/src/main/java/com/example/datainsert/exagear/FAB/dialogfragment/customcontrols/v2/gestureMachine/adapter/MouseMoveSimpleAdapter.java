package com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.gestureMachine.adapter;

import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.Const;

public class MouseMoveSimpleAdapter extends MouseMoveAdapter {
    private final AndroidPointReporter per;

    public MouseMoveSimpleAdapter(){
        super(TYPE_SIMPLE);
        per = Const.getGestureContext().getPointerReporter();
    }
    @Override
    public void moveTo(float x, float y) {
        per.pointerMove(x, y);

    }

    @Override
    public void prepareMoving(float x, float y) {
//        per.pointerMove(x, y);
    }
}
