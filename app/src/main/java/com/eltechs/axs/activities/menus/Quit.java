package com.eltechs.axs.activities.menus;

import com.eltechs.axs.activities.StartupActivity;
import com.eltechs.axs.helpers.AndroidHelpers;
import com.eltechs.axs.widgets.actions.AbstractAction;

/* loaded from: classes.dex */
public class Quit extends AbstractAction {
    public Quit() {
//        super(AndroidHelpers.getString(com.eltechs.axs.R.string.stop_Xserver));
        super("退出");
    }

    @Override // com.eltechs.axs.widgets.actions.Action
    public void run() {
        StartupActivity.shutdownAXSApplication(true);
    }
}