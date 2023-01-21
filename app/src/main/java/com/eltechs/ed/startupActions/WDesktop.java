package com.eltechs.ed.startupActions;

import com.eltechs.axs.configuration.startup.actions.InteractiveStartupActionBase;
import com.eltechs.ed.activities.EDMainActivity;

public class WDesktop<StateClass> extends InteractiveStartupActionBase<StateClass, WDesktop.UserRequestedAction> {
    /* loaded from: classes.dex */
    public enum UserRequestedAction {
        GO_FURTHER,
        RESTART_ME
    }
    @Override // com.eltechs.axs.configuration.startup.StartupAction
    public void execute() {
        requestUserInput(EDMainActivity.class);
    }

    @Override // com.eltechs.axs.configuration.startup.InteractiveStartupAction
    public void userInteractionFinished(UserRequestedAction userRequestedAction) {
        switch (userRequestedAction) {
            case GO_FURTHER:
                sendDone();
                return;
            case RESTART_ME:
//                getStartupActions().addAction(new InitGuestContainersManager());
//                getStartupActions().addAction(new WDesktop());
                sendDone();
                return;
            default:
                return;
        }
    }

    @Override // com.eltechs.axs.configuration.startup.InteractiveStartupAction
    public void userInteractionCanceled() {
//        StartupActivity.shutdownAXSApplication();
    }




}
