package com.example.datainsert.exagear.controlsV2.gestureMachine.state;

import static com.example.datainsert.exagear.controlsV2.gestureMachine.GestureContext2.MOUSE_CLICK_MODE_LEFT;
import static com.example.datainsert.exagear.controlsV2.gestureMachine.GestureContext2.MOUSE_CLICK_MODE_RIGHT;

import android.content.Context;
import android.view.View;

import com.example.datainsert.exagear.controlsV2.gestureMachine.FSMAction2;
import com.example.datainsert.exagear.controlsV2.gestureMachine.FSMR;
import com.example.datainsert.exagear.controlsV2.gestureMachine.GestureContext2;
import com.example.datainsert.exagear.controlsV2.gestureMachine.StateTag;

@StateTag(tag = FSMR.state.操作_切换鼠标左右键, isAction = true)

public class ActionSwitchMouseMode extends FSMAction2 {
    @Override
    public void run() {
        int currMode = getContext().getMouseClickMode();
        getContext().setMouseClickMode(currMode == MOUSE_CLICK_MODE_LEFT ? MOUSE_CLICK_MODE_RIGHT : MOUSE_CLICK_MODE_LEFT);
    }

    @Override
    protected void onAttach() {

    }

}
