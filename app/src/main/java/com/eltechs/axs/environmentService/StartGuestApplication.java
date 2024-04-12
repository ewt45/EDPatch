package com.eltechs.axs.environmentService;

import com.eltechs.axs.activities.StartupActivity;
import com.eltechs.axs.applicationState.EnvironmentAware;
import com.eltechs.axs.applicationState.UBTLaunchConfigurationAware;
import com.eltechs.axs.configuration.startup.actions.AbstractStartupAction;
import com.eltechs.axs.environmentService.components.GuestApplicationsTrackerComponent;
import com.eltechs.axs.guestApplicationsTracker.GuestApplicationsLifecycleAdapter;
import com.eltechs.axs.guestApplicationsTracker.impl.Translator;

/* loaded from: classes.dex */
public class StartGuestApplication<StateClass extends UBTLaunchConfigurationAware & EnvironmentAware> extends AbstractStartupAction<StateClass> {
    private final boolean restartAXSAfterShutdown;
    private final boolean terminateAXSOnGuestExit;

    public StartGuestApplication(boolean terminateAXSOnGuestExit) {
        this(terminateAXSOnGuestExit, false);
    }

    public StartGuestApplication(boolean terminateAXSOnGuestExit, boolean restartAXSAfterShutdown) {
        this.terminateAXSOnGuestExit = terminateAXSOnGuestExit;
        this.restartAXSAfterShutdown = restartAXSAfterShutdown;
    }

    @Override // com.eltechs.axs.configuration.startup.StartupAction
    public void execute() {
        UBTLaunchConfigurationAware uBTLaunchConfigurationAware = getApplicationState();
        final GuestApplicationsTrackerComponent guestApplicationsTrackerComponent = ((EnvironmentAware) getApplicationState()).getEnvironment().getComponent(GuestApplicationsTrackerComponent.class);
        guestApplicationsTrackerComponent.startGuestApplication(uBTLaunchConfigurationAware.getUBTLaunchConfiguration());
        if (this.terminateAXSOnGuestExit) {
            guestApplicationsTrackerComponent.addListener(new GuestApplicationsLifecycleAdapter() { // from class: com.eltechs.axs.environmentService.StartGuestApplication.1
                @Override // com.eltechs.axs.guestApplicationsTracker.GuestApplicationsLifecycleAdapter, com.eltechs.axs.guestApplicationsTracker.GuestApplicationsLifecycleListener
                public void translatorExited(Translator translator) {
                    if (!guestApplicationsTrackerComponent.haveGuestApplications()) {
                        StartupActivity.shutdownAXSApplication(restartAXSAfterShutdown);
                    }
                }
            });
        }
        sendDone();
    }
}
