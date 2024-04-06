package com.eltechs.axs.configuration.startup.actions;

import com.eltechs.axs.Globals;
import com.eltechs.axs.applicationState.ApplicationStateBase;
import com.eltechs.axs.helpers.EnvironmentInfoHelpers;
import com.eltechs.axs.helpers.GAHelpers;

/* loaded from: classes.dex */
public class CheckCPUFeatures extends AbstractStartupAction<ApplicationStateBase<?>> {
    private final RequiredCPUFeatures requiredCPUFeatures;

    /* loaded from: classes.dex */
    public static class RequiredCPUFeatures {
        final boolean isNeonRequired;

        public RequiredCPUFeatures(boolean neonRequired) {
            this.isNeonRequired = neonRequired;
        }
    }

    public CheckCPUFeatures(RequiredCPUFeatures requiredCPUFeatures) {
        this.requiredCPUFeatures = requiredCPUFeatures;
    }

    @Override // com.eltechs.axs.configuration.startup.StartupAction
    public void execute() {
        if (!EnvironmentInfoHelpers.canRunUbtOnCpu(false)) {
            sendError("Sorry, your CPU is not supported.");
            GAHelpers.GASendCpuNotSupported(Globals.getAppContext());
        } else if (this.requiredCPUFeatures.isNeonRequired && !EnvironmentInfoHelpers.canRunUbtOnCpu(true)) {
            sendError("No NEON support.");
            GAHelpers.GASendCpuNotSupported(Globals.getAppContext());
        } else {
            sendDone();
        }
    }
}