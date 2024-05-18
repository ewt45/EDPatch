package com.example.datainsert.exagear.controlsV2.gestureMachine.state;

import static com.example.datainsert.exagear.controlsV2.gestureMachine.FSMR.event.新手指按下;
import static com.example.datainsert.exagear.controlsV2.gestureMachine.FSMR.event.某手指松开;

import android.content.Context;
import android.view.View;

import com.eltechs.axs.helpers.Assert;
import com.example.datainsert.exagear.controlsV2.Const;
import com.example.datainsert.exagear.controlsV2.Finger;
import com.example.datainsert.exagear.controlsV2.TouchAdapter;
import com.example.datainsert.exagear.controlsV2.XZoomHandler;
import com.example.datainsert.exagear.controlsV2.gestureMachine.StateTag;
import com.example.datainsert.exagear.controlsV2.gestureMachine.FSMR;
import com.example.datainsert.exagear.controlsV2.gestureMachine.FSMState2;
import com.example.datainsert.exagear.controlsV2.widget.LimitEditText;
import com.google.gson.annotations.SerializedName;

import java.util.List;

@StateTag(tag = FSMR.state.两根手指缩放, events = {某手指松开, FSMR.event.新手指按下})
public class State2FingersZoom extends FSMState2 implements TouchAdapter {
    transient private final float[] tmpMatrixValues = new float[3];
    @SerializedName(value = Const.GsonField.st_zoomFingerIndex1)
    public int mFingerIndex1 = 0;
    @SerializedName(value = Const.GsonField.st_zoomFingerIndex2)
    public int mFingerIndex2 = 1;
    transient private Finger finger1;
    transient private Finger finger2;
    transient private float[] lastCenterXY;
    transient private float lastDistance;
    transient private XZoomHandler mZoomController;
    @Override
    protected void onAttach() {
        if(mFingerIndex1 < 0)
            mFingerIndex1 = 0;
        if(mFingerIndex2 < 0)
            mFingerIndex2 = (mFingerIndex1 + 1) % 10;
    }

    @Override
    public void notifyBecomeActive() {
        addTouchListener(this);
        int totalFingerCount = getContext().getFingers().size();
        Assert.state(totalFingerCount > mFingerIndex1 && totalFingerCount > mFingerIndex2);
        finger1 = getContext().getFingers().get(mFingerIndex1);
        finger2 = getContext().getFingers().get(mFingerIndex2);
//        lastCenterXY = new float[]{(finger1.getX() + finger2.getX()) / 2, (finger1.getY() + finger2.getY()) / 2};
//        lastDistance = GeometryHelpers.distance(finger1.getX(), finger1.getY(), finger2.getX(), finger2.getY());
        mZoomController = getContext().getZoomController();
//        mZoomController.setAnchorBoth(finger1.getX(), finger1.getY());
        //为什么原代码用的getXWhenFingerCountLastChanged？
        mZoomController.start(finger1.getX(),finger1.getY(),finger2.getX(),finger2.getY());
    }

    @Override
    public void notifyBecomeInactive() {
        removeTouchListener(this);
        mZoomController.stop();
        mZoomController=null;
        finger1 = null;
        finger2 = null;
    }

    @Override
    public void notifyMoved(Finger finger, List<Finger> list) {
        mZoomController.update(finger1.getX(),finger1.getY(),finger2.getX(),finger2.getY());
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
        LimitEditText edit1 = new LimitEditText(c)
                .setCustomInputType(LimitEditText.TYPE_GIVEN_OPTIONS)
                .setSelectableOptions(FSMR.value.观测手指序号_全部可用选项)
                .setSelectedValue(mFingerIndex1)
                .setUpdateListener(editText -> mFingerIndex1 = editText.getSelectedValue());

        LimitEditText edit2 = new LimitEditText(c)
                .setCustomInputType(LimitEditText.TYPE_GIVEN_OPTIONS)
                .setSelectableOptions(FSMR.value.观测手指序号_全部可用选项)
                .setSelectedValue(mFingerIndex2)
                .setUpdateListener(editText -> mFingerIndex2 = editText.getSelectedValue());

        //第几根手指 (其一)$第几根手指 (其二)
        return FSMState2.createEditViewQuickly(this, c,
                new String[][]{
                        FSMR.getFieldS(Const.GsonField.st_zoomFingerIndex1),
                        FSMR.getFieldS(Const.GsonField.st_zoomFingerIndex2)},
                new View[]{edit1, edit2});
    }
}