package com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.gestureMachine.State;

import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.Const;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.Finger;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.gestureMachine.FSMAction2;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.gestureMachine.FSMR;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.annotation.StateTag;
import com.google.gson.annotations.SerializedName;

import java.util.List;
/**
 * 若执行此操作时没有手指按下，则忽略fingerIndex，从历史记录中获取手指位置
 */
@StateTag(tag = FSMR.state.操作_鼠标移动, isAction = true)
public class ActionPointerMove extends FSMAction2 {
    /**
     * 若执行此操作时没有手指按下，则忽略该值，从历史记录中获取手指位置
     */
    @SerializedName(value = Const.GsonField.st_fingerIndex)
    public int mFingerIndex = 0;

    @SerializedName(value = Const.GsonField.st_fingerXYType)
    public int mFingerXYType = FSMR.value.手指位置_最后移动;

    @Override
    protected void onAttach() {

    }

    @Override
    public void run() {

        List<Finger> fingerList = getContext().getFingers();
        if (mFingerIndex >= fingerList.size())
            mFingerXYType = FSMR.value.手指位置_最后移动;

        float[] fingerXY = getContext().getFingerXYByType(mFingerXYType, mFingerIndex);
        getContext().getPointerReporter().pointerMove(fingerXY[0], fingerXY[1]);
    }
}
