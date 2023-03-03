package com.example.datainsert.exagear.controls.menus;

import com.eltechs.axs.activities.menus.ShowKeyboard;
import com.eltechs.axs.helpers.AndroidHelpers;
import com.eltechs.axs.widgets.actions.AbstractAction;

public class ToggleKeyBoardA11 extends ShowKeyboard {
    private boolean isShowing = false;

    public ToggleKeyBoardA11() {
        super();
    }

    @Override
    public String getName() {
        isShowing = ! isShowing;
        return isShowing?"显示输入法":"隐藏输入法";
    }
    @Override // com.eltechs.axs.widgets.actions.Action
    public void run() {
        AndroidHelpers.toggleSoftInput();
    }
}
