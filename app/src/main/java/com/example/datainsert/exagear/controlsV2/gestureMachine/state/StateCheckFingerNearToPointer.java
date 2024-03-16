package com.example.datainsert.exagear.controlsV2.gestureMachine.state;

import static com.example.datainsert.exagear.RR.getS;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;

import com.eltechs.axs.GeometryHelpers;
import com.eltechs.axs.geom.Point;
import com.eltechs.axs.helpers.Assert;
import com.eltechs.axs.widgets.viewOfXServer.TransformationHelpers;
import com.example.datainsert.exagear.RR;
import com.example.datainsert.exagear.controlsV2.Const;
import com.example.datainsert.exagear.controlsV2.gestureMachine.FSMState2;
import com.example.datainsert.exagear.controlsV2.gestureMachine.FSMR;
import com.example.datainsert.exagear.controlsV2.gestureMachine.StateTag;
import com.example.datainsert.exagear.controlsV2.widget.LimitEditText;
import com.google.gson.annotations.SerializedName;

/**
 * 虽然是瞬间完成但是由于需要发送事件，所以没法写成action，还是state了
 */
@StateTag(tag = FSMR.state.判断_手指与鼠标位置距离, events = {FSMR.event.手指距离指针_近, FSMR.event.手指距离指针_远})
public class StateCheckFingerNearToPointer extends FSMState2 {
    private static final String TAG = "StateCheckFingerNearToPointer";
    @SerializedName(value = Const.GsonField.st_fingerIndex)
    public int mFingerIndex = 0;
    @SerializedName(value = Const.GsonField.st_fingerXYType)
    public int mFingerXYType = FSMR.value.手指位置_当前;
    /**
     * 单位是安卓像素
     */
    @SerializedName(value = Const.GsonField.st_nearFarThreshold)
    public float mDistThreshold = 12f;

    @Override
    protected void onAttach() {

    }

    @SuppressLint("LongLogTag")
    @Override
    public void notifyBecomeActive() {

        Assert.state(getContext().getFingers().size() > mFingerIndex);
        Point pointerLocation = getContext().getXServerHolder().getPointerLocation();
        float[] posInAMatrix = {pointerLocation.x, pointerLocation.y};
        TransformationHelpers.mapPoints(getContext().getXServerHolder().getXServerToViewTransformationMatrix(), posInAMatrix);

        //TODO 这里原代码用的getXWhenFirstTouched，是有什么理由吗？）
        float[] fingerXY = getContext().getFingerXYByType(mFingerXYType,mFingerIndex);
        float shortestDist = GeometryHelpers.distance(posInAMatrix[0], posInAMatrix[1], fingerXY[0], fingerXY[1]);

        sendEvent( shortestDist<= this.mDistThreshold
                ? FSMR.event.手指距离指针_近
                : FSMR.event.手指距离指针_远);
    }

    @Override
    public void notifyBecomeInactive() {
    }

    @Override
    public View createPropEditView(Context c) {
        LimitEditText editFingerIndex = new LimitEditText(c)
                .setCustomInputType(LimitEditText.TYPE_GIVEN_OPTIONS)
                .setSelectableOptions(FSMR.value.观测手指序号_全部可用选项)
                .setSelectedValue(mFingerIndex)
                .setUpdateListener(editText -> mFingerIndex = editText.getSelectedValue());
        editFingerIndex.setEnabled(mFingerXYType != FSMR.value.手指位置_最后移动);

        LimitEditText editFingerXYType = new LimitEditText(c)
                .setCustomInputType(LimitEditText.TYPE_GIVEN_OPTIONS)
                .setSelectableOptions(FSMR.value.手指位置_全部可用选项)
                .setSelectedValue(mFingerXYType)
                .setUpdateListener(editText -> {
                    mFingerXYType = editText.getSelectedValue();
                    editFingerIndex.setEnabled(mFingerXYType != FSMR.value.手指位置_最后移动);
                });

        LimitEditText editThreshold = new LimitEditText(c)
                .setCustomInputType(LimitEditText.TYPE_NUMBER_FLOAT)
                .setFloatValue(mDistThreshold)
                .setUpdateListener(editText -> mDistThreshold = editText.getFloatValue());

        return createEditViewQuickly(c,
                new String[][]{
                        {getS(RR.ctr2_stateProp_fingerIndex),null},//"观测第几根手指"
                        {getS(RR.ctr2_stateProp_fingerXYType),null},//观测手指的哪个坐标
                        {getS(RR.ctr2_stateProp_farDistThres),null}//超过此大小则属于远距离
                },
                new View[]{editFingerIndex,editFingerXYType,editThreshold});
    }
}
