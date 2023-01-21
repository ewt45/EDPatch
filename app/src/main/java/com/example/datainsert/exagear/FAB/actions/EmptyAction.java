package com.example.datainsert.exagear.FAB.actions;

import com.eltechs.axs.configuration.startup.actions.AbstractStartupAction;

public class EmptyAction extends AbstractStartupAction {
    @Override
    public void execute() {
        sendDone();
    }
}
