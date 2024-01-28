package com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.gestureMachine.State;

import android.os.Handler;
import android.os.Looper;

import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.Const;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.gestureMachine.AbstractFSMAction2;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.gestureMachine.FSMR;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.annotation.StateTag;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.options.OptionsProvider;
import com.google.gson.annotations.SerializedName;

/**
 * 执行一个选项。
 * <br/> 可编辑：
 * <br/> 1. 选项类型
 * <br/> 2. 是否在单独线程运行并等待运行结束后再发送结束事件（比如长时间操作）
 */
@StateTag(tag = FSMR.state.操作_直接执行选项, isAction = true)
public class ActionRunOption extends AbstractFSMAction2 {
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
        if (!mWaitUntilFinish) {
            doOptionAndLeave();
        } else {
            new Handler(Looper.getMainLooper()).post(() -> doOptionAndLeave());
        }
    }
}
