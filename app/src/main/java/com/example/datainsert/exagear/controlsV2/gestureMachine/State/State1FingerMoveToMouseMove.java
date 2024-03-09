package com.example.datainsert.exagear.controlsV2.gestureMachine.State;

import android.content.Context;
import android.view.View;

import com.eltechs.axs.GeometryHelpers;
import com.example.datainsert.exagear.RR;
import com.example.datainsert.exagear.controlsV2.Const;
import com.example.datainsert.exagear.controlsV2.Finger;
import com.example.datainsert.exagear.controlsV2.TestHelper;
import com.example.datainsert.exagear.controlsV2.TouchAdapter;
import com.example.datainsert.exagear.controlsV2.gestureMachine.StateTag;
import com.example.datainsert.exagear.controlsV2.gestureMachine.FSMR;
import com.example.datainsert.exagear.controlsV2.gestureMachine.FSMState2;
import com.example.datainsert.exagear.controlsV2.gestureMachine.adapter.MouseMoveAdapter;
import com.example.datainsert.exagear.controlsV2.gestureMachine.adapter.MouseMoveSimpleAdapter;
import com.example.datainsert.exagear.controlsV2.widget.LimitEditText;
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
    //TODO 可调节移动速度，鼠标移动逻辑
    @SerializedName(value = Const.GsonField.st_ignorePixels)
    public float mNoMoveThreshold = 0;
    @SerializedName(value = Const.GsonField.st_pointMoveType)
    public int mMouseMoveType = FSMR.value.鼠标移动逻辑_普通; //可以设置鼠标视角移动
    @SerializedName(value = Const.GsonField.st_fingerIndex)
    public int mFingerIndex = 0; //不能为-2
    transient private float[] firstXY;
    transient private boolean startMove;
    transient private Finger finger;
    transient private MouseMoveAdapter moveAdapter;

    protected void onAttach() {
        if(mMouseMoveType == FSMR.value.鼠标移动逻辑_普通)
            moveAdapter = new MouseMoveSimpleAdapter();
        else  if(mMouseMoveType == FSMR.value.鼠标移动逻辑_视角转动)
            moveAdapter = Const.Extension.getImpl(Const.Extension.MOUSE_MOVE_CAMERA_RELATIVE);
        else
            throw new RuntimeException("未识别的鼠标移动逻辑："+mMouseMoveType);
    }

    @Override
    public void notifyBecomeActive() {
        addTouchListener(this);
        TestHelper.assertTrue(getContext().getFingers().size() >mFingerIndex && mFingerIndex!=FSMR.value.观测手指序号_全部);
        this.finger = getContext().getFingers().get(mFingerIndex);
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
        //TODO 松手和和新手指按下时，也应该先同步一下当前观测的手指的位置，再退出。尝试用于解决点击底部没反应的问题？但是是否会带来副作用（比如现在是鼠标和手指位置同步，如果要用相对偏移的话呢？）
        notifyMoved(finger,list);
        sendEvent(FSMR.event.某手指松开);
    }

    @Override
    public void notifyTouched(Finger finger, List<Finger> list) {
        //发送事件前，也应该先移动一下鼠标
        notifyMoved(this.finger,list);
        sendEvent(FSMR.event.新手指按下);
    }

    @Override
    public View createPropEditView(Context c) {
        LimitEditText editFingerIndex = new LimitEditText(c)
                .setCustomInputType(LimitEditText.TYPE_GIVEN_OPTIONS)
                .setSelectableOptions(FSMR.value.观测手指序号_全部可用选项)
                .setSelectedValue(mFingerIndex)
                .setUpdateListener(editText -> {
                    int selectedValue = editText.getSelectedValue();
                    if(selectedValue == FSMR.value.观测手指序号_全部) //不允许选择观测全部，必须指定一个手指
                        editText.setSelectedValue(0);
                    else
                        mFingerIndex = selectedValue;
                });

        LimitEditText editNoMoveThreshold = new LimitEditText(c)
                .setCustomInputType(LimitEditText.TYPE_NUMBER_FLOAT)
                .setFloatValue(mNoMoveThreshold)
                .setUpdateListener(editText -> mNoMoveThreshold = editText.getFloatValue());
        return createEditViewQuickly(c,
                new String[][]{{/*第几根手指*/RR.getS(RR.ctr2_stateProp_fingerIndex),null},{RR.getS(RR.ctr2_stateProp_noMoveThreshold),null}},
                new View[]{editFingerIndex,editNoMoveThreshold});
    }
}
