package com.eltechs.axs.configuration.startup.actions;

import com.eltechs.axs.ExagearImageConfiguration.ExagearImageConfigurationHelpers;
import com.eltechs.axs.applicationState.ExagearImageAware;
import com.eltechs.axs.environmentService.components.NativeLibsConfiguration;
import java.io.IOException;

/* loaded from: classes.dex */
public class PrepareGuestImageForPackagedWineApplication<StateClass extends ExagearImageAware> extends AbstractStartupAction<StateClass> {
    @Override // com.eltechs.axs.configuration.startup.StartupAction
    public void execute() {
        try {
            new ExagearImageConfigurationHelpers(getApplicationState().getExagearImage()).prepareWineForCurrentMemoryConfiguration(new NativeLibsConfiguration(getAppContext()));
            sendDone();
        } catch (IOException e) {
            sendError("Failed to prepare the unpacked exagear image for the game being started.", e);
        }
    }
}