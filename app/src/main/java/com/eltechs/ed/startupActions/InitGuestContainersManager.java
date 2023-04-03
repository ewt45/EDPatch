package com.eltechs.ed.startupActions;

import com.eltechs.axs.configuration.startup.AsyncStartupAction;
import com.eltechs.axs.configuration.startup.StartupActionInfo;
import com.eltechs.axs.configuration.startup.actions.AbstractStartupAction;
import com.eltechs.axs.helpers.UiThread;
import com.eltechs.ed.guestContainers.GuestContainersManager;

/* loaded from: classes.dex */
public class InitGuestContainersManager<StateClass> extends AbstractStartupAction<StateClass> implements AsyncStartupAction<StateClass> {
    @Override // com.eltechs.axs.configuration.startup.actions.AbstractStartupAction, com.eltechs.axs.configuration.startup.StartupAction
    public StartupActionInfo getInfo() {
        return new StartupActionInfo("Preparing containers...", null);
    }

    @Override // com.eltechs.axs.configuration.startup.StartupAction
    public void execute() {
        GuestContainersManager.getInstance(getAppContext());
        UiThread.post(new Runnable() { // from class: com.eltechs.ed.startupActions.InitGuestContainersManager.1
            @Override // java.lang.Runnable
            public void run() {
                InitGuestContainersManager.this.sendDone();
            }
        });
    }
}