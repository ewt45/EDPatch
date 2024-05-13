package com.example.datainsert.exagear.controlsV2.gestureMachine.state;

import android.content.Context;
import android.view.View;

import com.example.datainsert.exagear.QH;
import com.example.datainsert.exagear.controlsV2.Const;
import com.example.datainsert.exagear.controlsV2.axs.XKeyButton;
import com.example.datainsert.exagear.controlsV2.gestureMachine.FSMAction2;
import com.example.datainsert.exagear.controlsV2.gestureMachine.FSMR;
import com.example.datainsert.exagear.controlsV2.gestureMachine.GestureContext2;
import com.example.datainsert.exagear.controlsV2.gestureMachine.StateTag;

@StateTag(tag = FSMR.state.操作_根据模式点击鼠标按键, isAction = true)

public class ActionMouseClickByMode extends FSMAction2 {
    @Override
    public void run() {
        int button = getContext().getMouseClickMode() == GestureContext2.MOUSE_CLICK_MODE_LEFT
                ? XKeyButton.POINTER_LEFT : XKeyButton.POINTER_RIGHT;

        Const.getXServerHolder().injectPointerButtonPress(button);
        QH.sleep(50);
        Const.getXServerHolder().injectPointerButtonRelease(button);
    }

    @Override
    protected void onAttach() {

    }
}
