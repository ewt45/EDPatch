package com.example.datainsert.exagear.controlsV2.gestureMachine.State;

import static com.example.datainsert.exagear.controlsV2.gestureMachine.FSMR.event.手指数量不变;
import static com.example.datainsert.exagear.controlsV2.gestureMachine.FSMR.event.新手指按下;
import static com.example.datainsert.exagear.controlsV2.gestureMachine.FSMR.event.某手指松开;

import android.content.Context;
import android.view.View;

import com.eltechs.axs.helpers.OneShotTimer;
import com.example.datainsert.exagear.controlsV2.Const;
import com.example.datainsert.exagear.controlsV2.Finger;
import com.example.datainsert.exagear.controlsV2.TouchAdapter;
import com.example.datainsert.exagear.controlsV2.gestureMachine.StateTag;
import com.example.datainsert.exagear.controlsV2.gestureMachine.FSMR;
import com.example.datainsert.exagear.controlsV2.gestureMachine.FSMState2;
import com.example.datainsert.exagear.controlsV2.widget.LimitEditText;
import com.google.gson.annotations.SerializedName;

import java.util.List;

@StateTag(tag = FSMR.state.监测手指数量变化, events = {新手指按下, 某手指松开, 手指数量不变})
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

    @Override
    public View createPropEditView(Context c) {
        return createEditViewQuickly(c, new String[][]{{"倒计时限时 (毫秒)", null}}, new View[]{new LimitEditText(c)
                .setCustomInputType(LimitEditText.TYPE_NUMBER_INT)
                .setRange(0, Integer.MAX_VALUE)
                .setIntValue(mCountDownMs)
                .setUpdateListener(editText -> mCountDownMs = editText.getIntValue())});


    }
}
