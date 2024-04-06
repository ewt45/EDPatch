package com.eltechs.axs.configuration.startup.actions;

import com.eltechs.axs.Globals;
import com.eltechs.axs.ShadowApplicationConfigurationAccessor;
import com.eltechs.axs.activities.UsageActivity;
import com.eltechs.axs.applicationState.ApplicationStateBase;
import com.eltechs.axs.applicationState.SelectedExecutableFileAware;
import com.eltechs.axs.payments.PurchasableComponent;

public class ShowUsageDialog<StateClass extends SelectedExecutableFileAware<StateClass>> extends SimpleInteractiveStartupActionBase<StateClass> {
    @Override // com.eltechs.axs.configuration.startup.StartupAction
    public void execute() {
        PurchasableComponent effectivePkg =  getApplicationState().getSelectedExecutableFile().getEffectiveCustomizationPackage();
        ShadowApplicationConfigurationAccessor shadowConfig = new ShadowApplicationConfigurationAccessor(effectivePkg.getName());
        if (!shadowConfig.isUsageShown()) {
            requestUserInput(UsageActivity.class, effectivePkg.getInfoResId());
            shadowConfig.setUsageShown(true);
            return;
        }
        sendDone();
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