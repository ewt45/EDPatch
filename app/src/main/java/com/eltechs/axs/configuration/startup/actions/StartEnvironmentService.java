package com.eltechs.axs.configuration.startup.actions;

import android.util.Log;

import com.eltechs.axs.applicationState.EnvironmentAware;
import com.eltechs.axs.environmentService.AXSEnvironment;
import com.eltechs.axs.environmentService.TrayConfiguration;
import com.example.datainsert.exagear.QH;

/* loaded from: classes.dex */
public class StartEnvironmentService<StateClass extends EnvironmentAware> extends AbstractStartupAction<StateClass> {
    private final TrayConfiguration trayConfiguration;

    public StartEnvironmentService(TrayConfiguration trayConfiguration) {
        this.trayConfiguration = trayConfiguration;
    }

    @Override // com.eltechs.axs.configuration.startup.StartupAction
    public void execute() {
        getApplicationState().getEnvironment().startEnvironmentService(new AXSEnvironment.StartupCallback() { // from class: com.eltechs.axs.configuration.startup.actions.StartEnvironmentService.1


            @Override // com.eltechs.axs.environmentService.AXSEnvironment.StartupCallback
            public void serviceStarted() {
                StartEnvironmentService.this.sendDone();
            }

            @Override // com.eltechs.axs.environmentService.AXSEnvironment.StartupCallback
            public void serviceFailed(Throwable th) {
                Log.e("StartEnvironmentService", "serviceFailed: ", th);
                StartEnvironmentService.this.sendError("Failed to start the environment emulation service.", th);
            }
        }, this.trayConfiguration);
    }
}