package com.eltechs.axs.activities.menus;

import com.eltechs.axs.helpers.AndroidHelpers;
import com.eltechs.axs.widgets.actions.AbstractAction;
import com.eltechs.ed.R;

public class ShowKeyboard extends AbstractAction {
    public ShowKeyboard() {
        super(AndroidHelpers.getString(R.string.show_keyboard));
    }

    @Override // com.eltechs.axs.widgets.actions.Action
    public void run() {
        AndroidHelpers.toggleSoftInput();
    }
}
