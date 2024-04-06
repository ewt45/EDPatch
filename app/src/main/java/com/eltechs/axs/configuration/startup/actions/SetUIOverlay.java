package com.eltechs.axs.configuration.startup.actions;

import com.eltechs.axs.applicationState.SelectedExecutableFileAware;
import com.eltechs.axs.applicationState.XServerDisplayActivityConfigurationAware;
import com.eltechs.axs.configuration.startup.DetectedExecutableFile;
import com.eltechs.axs.payments.PurchasableComponent;

public class SetUIOverlay<StateClass extends XServerDisplayActivityConfigurationAware & SelectedExecutableFileAware<StateClass>> extends AbstractStartupAction<StateClass> {
    @Override // com.eltechs.axs.configuration.startup.StartupAction
    public void execute() {
        DetectedExecutableFile<StateClass> exe = getApplicationState().getSelectedExecutableFile();
        PurchasableComponent effectivePkg = exe.getEffectiveCustomizationPackage();
        getApplicationState().setXServerDisplayActivityInterfaceOverlay(
                effectivePkg != exe.getRecommendedCustomizationPackage()
                ? effectivePkg.getUiOverlay()
                : exe.getDefaultUiOverlay());
        sendDone();
    }
}