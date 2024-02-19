package com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.gestureMachine.State;

import android.content.Context;
import android.view.View;

import com.eltechs.axs.GeometryHelpers;
import com.eltechs.axs.helpers.Assert;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.Const;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.Finger;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.TouchAdapter;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.annotation.StateTag;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.gestureMachine.FSMR;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.gestureMachine.FSMState2;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.gestureMachine.adapter.MouseMoveAdapter;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.gestureMachine.adapter.MouseMoveSimpleAdapter;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.widget.LimitEditText;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * 进入时，应该保证只有一根手指在按下状态
 * <br/> 当手指移动时，通知鼠标adapter 手指位置,让其处理鼠标位置
 * <br/> 当手指松开时，退出该状态
 * <br/> 当新手指按下时，无视
 */
@StateTag(tag = FSMR.state.一指移动带动鼠标移动, events = {FSMR.event.新手指按下, FSMR.event.某手指松开})
public class State1FingerMoveToMouseMove extends FSMState2 implements TouchAdapter {
    @SerializedName(value = Const.GsonField.st_ignorePixels)
    public float mNoMoveThreshold = 0;
    transient private float[] firstXY;
    transient private boolean startMove;
    transient private Finger finger;
    transient private MouseMoveAdapter moveAdapter;

    protected void onAttach() {
        moveAdapter = new MouseMoveSimpleAdapter();
    }

    @Override
    public void notifyBecomeActive() {
        addTouchListener(this);
        Assert.state(getContext().getFingers().size() == 1);
        this.finger = getContext().getFingers().get(0);
        this.moveAdapter.prepareMoving(this.finger.getXWhenFirstTouched(), this.finger.getYWhenFirstTouched());
        firstXY = new float[]{finger.getX(), finger.getY()};
        startMove = false;
    }

    @Override
    public void notifyBecomeInactive() {
        this.finger = null;
        removeTouchListener(this);

    }

    @Override
    public void notifyMoved(Finger finger, List<Finger> list) {
        if (finger != this.finger)
            return;

        if (!startMove)
            startMove = GeometryHelpers.distance(firstXY[0], firstXY[1], finger.getX(), finger.getY()) >= mNoMoveThreshold;

        if (startMove)
            this.moveAdapter.moveTo(finger.getX(), finger.getY());
    }

    @Override
    public void notifyReleased(Finger finger, List<Finger> list) {
        sendEvent(FSMR.event.某手指松开);
    }

    @Override
    public void notifyTouched(Finger finger, List<Finger> list) {
        sendEvent(FSMR.event.新手指按下);
    }

    @Override
    public View createPropEditView(Context c) {
        //TODO 应该给一个手指序号的选择吧，不然拖拽移动那个咋实现的
        return createEditViewQuickly(c,
                new String[][]{{"手指移动小于此距离时不移动鼠标"}},
                new View[]{new LimitEditText(c)
                        .setCustomInputType(LimitEditText.TYPE_NUMBER_FLOAT)
                        .setFloatValue(mNoMoveThreshold)
                        .setUpdateListener(editText -> mNoMoveThreshold = editText.getFloatValue())});
    }
}
