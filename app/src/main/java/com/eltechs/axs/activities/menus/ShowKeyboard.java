package com.eltechs.axs.activities.menus;

import com.eltechs.axs.widgets.actions.AbstractAction;

public class ShowKeyboard extends AbstractAction {
    public ShowKeyboard() {
//        super(AndroidHelpers.getString(R.string.show_keyboard));
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public boolean isCheckable() {
        return false;
    }

    @Override
    public boolean isChecked() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }

    @Override // com.eltechs.axs.widgets.actions.Action
    public void run() {
//        AndroidHelpers.toggleSoftInput();
    }
}
