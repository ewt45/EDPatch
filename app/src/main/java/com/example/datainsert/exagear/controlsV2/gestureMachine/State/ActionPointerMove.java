package com.example.datainsert.exagear.controlsV2.gestureMachine.State;

import android.content.Context;
import android.view.View;

import com.example.datainsert.exagear.RR;
import com.example.datainsert.exagear.controlsV2.Const;
import com.example.datainsert.exagear.controlsV2.Finger;
import com.example.datainsert.exagear.controlsV2.gestureMachine.FSMAction2;
import com.example.datainsert.exagear.controlsV2.gestureMachine.FSMR;
import com.example.datainsert.exagear.controlsV2.gestureMachine.StateTag;
import com.example.datainsert.exagear.controlsV2.widget.LimitEditText;
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

    @Override
    public View createPropEditView(Context c) {
        LimitEditText editFingerIndex = new LimitEditText(c)
                .setCustomInputType(LimitEditText.TYPE_GIVEN_OPTIONS)
                .setSelectableOptions(FSMR.value.观测手指序号_全部可用选项)
                .setSelectedValue(mFingerIndex)
                .setUpdateListener(editText -> mFingerIndex = editText.getSelectedValue());
        editFingerIndex.setEnabled(mFingerXYType == FSMR.value.手指位置_最后移动);

        LimitEditText editFingerXYType = new LimitEditText(c)
                .setCustomInputType(LimitEditText.TYPE_GIVEN_OPTIONS)
                .setSelectableOptions(FSMR.value.手指位置_全部可用选项)
                .setSelectedValue(mFingerXYType)
                .setUpdateListener(editText -> {
                    mFingerXYType = editText.getSelectedValue();
                    editFingerIndex.setEnabled(mFingerXYType == FSMR.value.手指位置_最后移动);
                });

        return createEditViewQuickly(c,
                new String[][]{
                        /*第几根手指*/{RR.getS(RR.ctr2_stateProp_fingerIndex),null},
                        /*手指坐标类型*/{RR.getS(RR.ctr2_stateProp_fingerXYType),null},
                },
                new View[]{editFingerIndex,editFingerXYType});
    }
}
