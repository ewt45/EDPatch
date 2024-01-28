package com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.gestureMachine.State;

import android.annotation.SuppressLint;
import android.util.Log;

import com.eltechs.axs.GeometryHelpers;
import com.eltechs.axs.geom.Point;
import com.eltechs.axs.helpers.Assert;
import com.eltechs.axs.widgets.viewOfXServer.TransformationHelpers;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.Const;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.Finger;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.gestureMachine.AbstractFSMState2;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.gestureMachine.FSMR;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.annotation.StateTag;
import com.google.gson.annotations.SerializedName;

/**
 * 虽然是瞬间完成但是由于需要发送事件，所以没法写成action，还是state了
 */
@StateTag(tag = FSMR.state.判断_手指与鼠标位置距离, events = {FSMR.event.手指距离指针_近, FSMR.event.手指距离指针_远})
public class StateCheckFingerNearToPointer extends AbstractFSMState2 {
    private static final String TAG = "StateCheckFingerNearToPointer";
    @SerializedName(value = Const.GsonField.st_fingerIndex)
    public int mFingerIndex = 0;
    @SerializedName(value = Const.GsonField.st_fingerXYType)
    public int mFingerXYType = FSMR.value.手指位置_当前;
    /**
     * 单位是安卓像素
     */
    public double distThreshold = 9f;

    @Override
    protected void onAttach() {

    }

    @SuppressLint("LongLogTag")
    @Override
    public void notifyBecomeActive() {
        Assert.state(getContext().getFingers().size() > mFingerIndex);
        Point pointerLocation = getContext().getViewFacade().getPointerLocation();
        float[] posInAMatrix = {pointerLocation.x, pointerLocation.y};
        TransformationHelpers.mapPoints(getContext().getHostView().getXServerToViewTransformationMatrix(), posInAMatrix);

        Finger finger = getContext().getFingers().get(mFingerIndex);
        //TODO 这里原代码用的getXWhenFirstTouched，是有什么理由吗？）
        float[] fingerXY = getContext().getFingerXYByType(mFingerXYType,mFingerIndex);
        float shortestDist = GeometryHelpers.distance(posInAMatrix[0], posInAMatrix[1], fingerXY[0], fingerXY[1]);
        Log.d(TAG, "第二次左键单击 手指距离光标位置 = "+shortestDist);

        sendEvent( shortestDist< this.distThreshold
                ? FSMR.event.手指距离指针_近
                : FSMR.event.手指距离指针_远);
    }

    @Override
    public void notifyBecomeInactive() {

    }
}
