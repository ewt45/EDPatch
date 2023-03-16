package com.example.datainsert.exagear.controls.menus;

import android.content.Context;
import android.view.inputmethod.InputMethodManager;

import com.eltechs.axs.Globals;
import com.eltechs.axs.activities.menus.ShowKeyboard;
import com.eltechs.axs.applicationState.ApplicationStateBase;

/**
 * 还是在自己的控制模式里再写一个输入法显示吧。
 */
public class ShowKeyboardA11 extends ShowKeyboard {
    //    private boolean isShowing = false;
    private static final String TAG = "ToggleKeyBoardA11";

    public ShowKeyboardA11() {
        super();
    }
    @Override // com.eltechs.axs.widgets.actions.Action
    public void run() {
        ((ApplicationStateBase) Globals.getApplicationState()).getCurrentActivity().getWindow().getDecorView().postDelayed(
                () -> ((InputMethodManager) Globals.getAppContext().getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE)).toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
                , 200);
    }
}
