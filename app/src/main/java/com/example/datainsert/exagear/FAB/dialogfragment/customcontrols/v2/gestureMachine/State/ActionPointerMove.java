package com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.gestureMachine.State;

import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.Const;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.Finger;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.gestureMachine.AbstractFSMAction2;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.gestureMachine.FSMR;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.annotation.StateTag;
import com.google.gson.annotations.SerializedName;

import java.util.List;

@StateTag(tag = FSMR.state.操作_鼠标移动, isAction = true)
public class ActionPointerMove extends AbstractFSMAction2 {
    @SerializedName(value = Const.GsonField.st_fingerIndex)
    public int mFingerIndex = 0;
    /**
     * 当设置为 {@link FSMR.value#观测手指序号_无} 时，说明当前没有手指按下，忽略fingerIndex，应从历史记录中获取手指位置
     */
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
