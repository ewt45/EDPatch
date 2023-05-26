package com.eltechs.ed.startupActions;

import android.util.Log;

import com.eltechs.axs.Globals;
import com.eltechs.axs.activities.StartupActivity;
import com.eltechs.axs.configuration.startup.actions.InteractiveStartupActionBase;
import com.eltechs.ed.activities.EDMainActivity;
import com.ewt45.exagearsupportv7.MainActivity;
import com.example.datainsert.exagear.QH;

public class WDesktop<StateClass> extends InteractiveStartupActionBase<StateClass, WDesktop.UserRequestedAction> {
    public WDesktop(){
        Log.d("TAG", "WDesktop: 初始化");
    }
    @Override // com.eltechs.axs.configuration.startup.StartupAction
    public void execute() {
        requestUserInput(MainActivity.class);
//        requestUserInput(
//                QH.isTesting()
//                        ? MainActivity.class
//                        : EDMainActivity.class);
    }

    @Override // com.eltechs.axs.configuration.startup.InteractiveStartupAction
    public void userInteractionFinished(UserRequestedAction userRequestedAction) {
        switch (userRequestedAction) {
            case GO_FURTHER:
                sendDone();
                return;
            case RESTART_ME:
                getStartupActions().addAction(new InitGuestContainersManager<>());
                getStartupActions().addAction(new WDesktop<>());
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


    /* loaded from: classes.dex */
    public enum UserRequestedAction {
        GO_FURTHER,
        RESTART_ME
    }


}
