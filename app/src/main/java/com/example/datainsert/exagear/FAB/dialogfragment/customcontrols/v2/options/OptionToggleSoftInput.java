package com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.options;

import android.content.Context;
import android.view.inputmethod.InputMethodManager;

import com.eltechs.axs.Globals;
import com.eltechs.axs.applicationState.ApplicationStateBase;

public class OptionToggleSoftInput extends AbstractOption {
    @Override
    public void run() {
        ((ApplicationStateBase) Globals.getApplicationState()).getCurrentActivity().getWindow().getDecorView().postDelayed(
                () -> ((InputMethodManager) Globals.getAppContext().getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE)).toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
                , 200);
    }

    @Override
    public String getName() {
        return "显示/隐藏安卓输入法";
    }
}
