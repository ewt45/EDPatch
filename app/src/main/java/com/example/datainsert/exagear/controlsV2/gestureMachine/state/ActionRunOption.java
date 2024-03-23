package com.example.datainsert.exagear.controlsV2.gestureMachine.state;

import android.content.Context;
import android.view.View;

import com.example.datainsert.exagear.RR;
import com.example.datainsert.exagear.controlsV2.Const;
import com.example.datainsert.exagear.controlsV2.gestureMachine.FSMAction2;
import com.example.datainsert.exagear.controlsV2.gestureMachine.FSMR;
import com.example.datainsert.exagear.controlsV2.gestureMachine.StateTag;
import com.example.datainsert.exagear.controlsV2.options.OptionsProvider;
import com.example.datainsert.exagear.controlsV2.widget.LimitEditText;
import com.google.gson.annotations.SerializedName;

/**
 * 执行一个选项。
 * <br/> 可编辑：
 * <br/> 1. 选项类型
 * <br/> 2. 是否在单独线程运行并等待运行结束后再发送结束事件（比如长时间操作）
 */
@StateTag(tag = FSMR.state.操作_直接执行选项, isAction = true)
public class ActionRunOption extends FSMAction2 {
    @SerializedName(value = Const.GsonField.st_optionType)
    @OptionsProvider.OptionType
    public int mOptionType = OptionsProvider.OPTION_SHOW_SOFT_INPUT;
    @SerializedName(value = Const.GsonField.st_waitUntilFinish)
    public boolean mWaitUntilFinish = false;

    @Override
    protected void onAttach() {
    }

    private void doOptionAndLeave() {
        OptionsProvider.getOption(mOptionType).run();

    }

    @Override
    public void run() {
        doOptionAndLeave();
//        if (!mWaitUntilFinish) {
//            doOptionAndLeave();
//        } else {
//            new Handler(Looper.getMainLooper()).post(() -> doOptionAndLeave());
//        }
    }

    @Override
    public View createPropEditView(Context c) {
        LimitEditText editOption = new LimitEditText(c)
                .setCustomInputType(LimitEditText.TYPE_GIVEN_OPTIONS)
                .setSelectableOptions(OptionsProvider.optionsInt,OptionsProvider.optionsName)
                .setSelectedValue(mOptionType)
                .setUpdateListener(editText -> mOptionType = editText.getSelectedValue());
        return createEditViewQuickly(c,new String[][]{{/*操作*/RR.getS(RR.ctr2_stateProp_option),null}},new View[]{editOption});
    }
}
