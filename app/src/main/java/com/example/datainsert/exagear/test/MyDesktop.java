package com.example.datainsert.exagear.test;

import android.util.Log;

import com.eltechs.axs.Globals;
import com.eltechs.axs.activities.StartupActivity;
import com.eltechs.axs.applicationState.ExagearImageAware;
import com.eltechs.axs.configuration.startup.actions.AbstractStartupAction;
import com.eltechs.axs.configuration.startup.actions.InteractiveStartupActionBase;
import com.eltechs.ed.activities.EDMainActivity;
import com.eltechs.ed.startupActions.InitGuestContainersManager;
import com.ewt45.exagearsupportv7.MainActivity;


public class MyDesktop<StateClass> extends InteractiveStartupActionBase<StateClass, com.eltechs.ed.startupActions.WDesktop.UserRequestedAction> {
    @Override // com.eltechs.axs.configuration.startup.StartupAction
    public void execute() {
        requestUserInput(
                Globals.getAppContext().getPackageName().equals("com.ewt45.exagearsupportv7")
                        ? MainActivity.class
                        : EDMainActivity.class);
    }

    @Override // com.eltechs.axs.configuration.startup.InteractiveStartupAction
    public void userInteractionFinished(com.eltechs.ed.startupActions.WDesktop.UserRequestedAction userRequestedAction) {
        switch (userRequestedAction) {
            case GO_FURTHER:
                sendDone();
                return;
            case RESTART_ME:
//                getStartupActions().addAction(new InitGuestContainersManager());
                getStartupActions().addAction(new com.eltechs.ed.startupActions.WDesktop());
                sendDone();
                return;
            default:
                return;
        }
    }

    @Override // com.eltechs.axs.configuration.startup.InteractiveStartupAction
    public void userInteractionCanceled() {
        StartupActivity.shutdownAXSApplication();
    }




}

