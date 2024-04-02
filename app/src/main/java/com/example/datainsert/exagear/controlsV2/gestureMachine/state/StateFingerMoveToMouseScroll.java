package com.example.datainsert.exagear.controlsV2.gestureMachine.state;

import static com.example.datainsert.exagear.controlsV2.gestureMachine.FSMR.event.新手指按下;
import static com.example.datainsert.exagear.controlsV2.gestureMachine.FSMR.event.某手指松开;

import android.content.Context;
import android.view.View;

import com.example.datainsert.exagear.controlsV2.Const;
import com.example.datainsert.exagear.controlsV2.Finger;
import com.example.datainsert.exagear.controlsV2.TouchAdapter;
import com.example.datainsert.exagear.controlsV2.gestureMachine.StateTag;
import com.example.datainsert.exagear.controlsV2.gestureMachine.FSMR;
import com.example.datainsert.exagear.controlsV2.gestureMachine.FSMState2;
import com.example.datainsert.exagear.controlsV2.gestureMachine.adapter.MouseScrollAdapter;
import com.example.datainsert.exagear.controlsV2.widget.LimitEditText;
import com.google.gson.annotations.SerializedName;

import java.util.List;

@StateTag(tag = FSMR.state.手指移动_鼠标滚轮, events = {某手指松开, 新手指按下})
public class StateFingerMoveToMouseScroll extends FSMState2 implements TouchAdapter {
    @SerializedName(value = Const.GsonField.st_fingerIndex)
    public int mFingerIndex = 0;
    transient private MouseScrollAdapter scrollAdapter;
    transient private Finger savedFinger;


    public StateFingerMoveToMouseScroll() {

    }

    @Override
    protected void onAttach() {
        this.scrollAdapter = new MouseScrollAdapter();
        if(mFingerIndex < 0)
            mFingerIndex = 0;
    }

    @Override
    public void notifyBecomeActive() {
        addTouchListener(this);
        this.savedFinger = getContext().getFingers().get(mFingerIndex);
        this.scrollAdapter.start(savedFinger.getX(),savedFinger.getY());
    }

    @Override
    public void notifyBecomeInactive() {
        this.scrollAdapter.stop();
        removeTouchListener(this);
        savedFinger = null;
    }

    @Override
    public void notifyMoved(Finger finger, List<Finger> list) {
        //如果手指移动多远，视图就滚多远，那么可以在这里写。但是如果要手指移动然后定住，但是视图一直滚动，就要用计时器了
        scrollAdapter.scroll(finger.getX(), finger.getY());
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
        return FSMState2.createEditViewQuickly(this, c,
                new String[][]{FSMR.getFieldS(Const.GsonField.st_fingerIndex)}, //观测第几根手指
                new View[]{new LimitEditText(c)
                .setCustomInputType(LimitEditText.TYPE_GIVEN_OPTIONS)
                .setSelectableOptions(FSMR.value.观测手指序号_全部可用选项)
                .setSelectedValue(mFingerIndex)
                .setUpdateListener(editText -> mFingerIndex = editText.getSelectedValue())});
    }
}
