package com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.gestureMachine.State;

import com.eltechs.axs.xserver.ViewFacade;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.Const;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.XKeyInjector;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.annotation.StateTag;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.gestureMachine.AbstractFSMAction2;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.gestureMachine.FSMR;
import com.google.gson.annotations.SerializedName;

@StateTag(tag = FSMR.state.操作_点击, isAction = true)
public class ActionButtonClick extends AbstractFSMAction2 {
    @SerializedName(value = Const.GsonField.st_keycode)
    public int mKeycode = 0;
    @SerializedName(value = Const.GsonField.st_doPress)
    public boolean mDoPress = true;
    @SerializedName(value = Const.GsonField.st_doRelease)
    public boolean mDoRelease = true;
    transient private ViewFacade facade;

    @Override
    protected void onAttach() {
        facade = Const.getViewFacade();
    }

    @Override
    public void run() {
        if (facade != null) {
            if (mDoPress)
                XKeyInjector.press(facade, mKeycode);
            if (mDoRelease)
                XKeyInjector.release(facade, mKeycode);
        }
    }
}
