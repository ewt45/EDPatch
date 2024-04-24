package com.eltechs.axs.configuration.startup.actions;

import com.eltechs.axs.activities.RateMeActivity;

/* loaded from: classes.dex */
public class ShowRateMeDialog<StateClass> extends SimpleInteractiveStartupActionBase<StateClass> {
    @Override // com.eltechs.axs.configuration.startup.StartupAction
    public void execute() {
        requestUserInput(RateMeActivity.class);
    }

    @Override // com.eltechs.axs.configuration.startup.actions.SimpleInteractiveStartupActionBase
    public void userInteractionFinished() {
        sendDone();
    }

    @Override // com.eltechs.axs.configuration.startup.actions.SimpleInteractiveStartupActionBase, com.eltechs.axs.configuration.startup.InteractiveStartupAction
    public void userInteractionCanceled() {
        sendDone();
    }
}