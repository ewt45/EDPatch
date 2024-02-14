package com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.gestureMachine.State;

import static com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.gestureMachine.FSMR.event.手指数量不变;
import static com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.gestureMachine.FSMR.event.新手指按下;
import static com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.gestureMachine.FSMR.event.某手指松开;

import com.eltechs.axs.helpers.OneShotTimer;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.Const;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.Finger;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.TouchAdapter;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.gestureMachine.FSMState2;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.gestureMachine.FSMR;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.annotation.StateTag;
import com.google.gson.annotations.SerializedName;

import java.util.List;

@StateTag(tag = FSMR.state.监测手指数量变化, events = {新手指按下,某手指松开, 手指数量不变})
public class StateCountDownWaitFingerNumChange extends FSMState2 implements TouchAdapter {
    @SerializedName(value = Const.GsonField.st_countDownMs)
    public int mCountDownMs = 250;
    transient private OneShotTimer timer;
    @Override
    protected void onAttach() {

    }

    @Override
    public void notifyBecomeActive() {
        addTouchListener(this);
        timer = new OneShotTimer(mCountDownMs) {
            @Override
            public void onFinish() {
                sendEvent(手指数量不变);
            }
        };
        timer.start();
    }

    @Override
    public void notifyBecomeInactive() {
        removeTouchListener(this);
        timer.cancel();
    }

    @Override
    public void notifyMoved(Finger finger, List<Finger> list) {

    }

    @Override
    public void notifyReleased(Finger finger, List<Finger> list) {
        sendEvent(某手指松开);
    }

    @Override
    public void notifyTouched(Finger finger, List<Finger> list) {
        sendEvent(新手指按下);
    }
}
