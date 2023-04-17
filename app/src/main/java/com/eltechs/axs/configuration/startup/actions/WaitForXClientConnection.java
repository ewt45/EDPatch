package com.eltechs.axs.configuration.startup.actions;

import com.eltechs.axs.AppConfig;
import com.eltechs.axs.applicationState.EnvironmentAware;
import com.eltechs.axs.configuration.startup.StartupActionInfo;
import com.eltechs.axs.environmentService.AXSEnvironment;
import com.eltechs.axs.environmentService.components.GuestApplicationsTrackerComponent;
import com.eltechs.axs.environmentService.components.XServerComponent;
import com.eltechs.axs.firebase.FAHelper;
import com.eltechs.axs.guestApplicationsTracker.GuestApplicationsLifecycleAdapter;
import com.eltechs.axs.guestApplicationsTracker.GuestApplicationsLifecycleListener;
import com.eltechs.axs.guestApplicationsTracker.impl.Translator;
import com.eltechs.axs.xserver.LocksManager;
import com.eltechs.axs.xserver.Window;
import com.eltechs.axs.xserver.WindowContentModificationListener;

/* loaded from: classes.dex */
public class WaitForXClientConnection<StateClass extends EnvironmentAware> extends AbstractStartupAction<StateClass> {
    private GuestApplicationsLifecycleListener guestApplicationTerminationListener;
    private final boolean hideXServerImage;
    private final String progressFileName;
    private WindowContentModificationListener putImageListener;
    private boolean receivedEvent;

    public WaitForXClientConnection(String str, boolean z) {
        this.progressFileName = str;
        this.hideXServerImage = z;
    }

    public WaitForXClientConnection(String str) {
        this.progressFileName = str;
        this.hideXServerImage = false;
    }

    public WaitForXClientConnection() {
        this.progressFileName = null;
        this.hideXServerImage = false;
    }

    @Override // com.eltechs.axs.configuration.startup.actions.AbstractStartupAction, com.eltechs.axs.configuration.startup.StartupAction
    public StartupActionInfo getInfo() {
        return new StartupActionInfo("", this.progressFileName);
    }

    @Override // com.eltechs.axs.configuration.startup.StartupAction
    public void execute() {
        AXSEnvironment environment = ((EnvironmentAware) getApplicationState()).getEnvironment();
        XServerComponent xServerComponent = (XServerComponent) environment.getComponent(XServerComponent.class);
        final GuestApplicationsTrackerComponent guestApplicationsTrackerComponent = (GuestApplicationsTrackerComponent) environment.getComponent(GuestApplicationsTrackerComponent.class);
        this.putImageListener = new WindowContentModificationListener() { // from class: com.eltechs.axs.configuration.startup.actions.WaitForXClientConnection.1
            @Override // com.eltechs.axs.xserver.WindowContentModificationListener
            public void frontBufferReplaced(Window window) {
            }

            @Override // com.eltechs.axs.xserver.WindowContentModificationListener
            public void contentChanged(Window window, int i, int i2, int i3, int i4) {
                startedDrawing();
            }
        };
        this.guestApplicationTerminationListener = new GuestApplicationsLifecycleAdapter() { // from class: com.eltechs.axs.configuration.startup.actions.WaitForXClientConnection.2
            @Override // com.eltechs.axs.guestApplicationsTracker.GuestApplicationsLifecycleAdapter, com.eltechs.axs.guestApplicationsTracker.GuestApplicationsLifecycleListener
            public void translatorExited(Translator translator) {
                if (guestApplicationsTrackerComponent.haveGuestApplications()) {
                    return;
                }
                guestApplicationsTerminated();
            }
        };
        LocksManager.XLock lock = xServerComponent.getXServer().getLocksManager().lock(LocksManager.Subsystem.WINDOWS_MANAGER);
        try {
            if (!this.hideXServerImage) {
                xServerComponent.getXServer().getWindowsManager().addWindowContentModificationListner(this.putImageListener);
            }
            guestApplicationsTrackerComponent.addListener(this.guestApplicationTerminationListener);
            lock.close();
            if (guestApplicationsTrackerComponent.haveGuestApplications()) {
                return;
            }
            guestApplicationsTerminated();
        } catch (Throwable th) {
            lock.close();
            throw th;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    private synchronized void startedDrawing() {
        if (this.receivedEvent) {
            return;
        }
        AppConfig appConfig = AppConfig.getInstance(getAppContext());
        if (!appConfig.isXServerFirstConnectDone()) {
            appConfig.setXServerFirstConnectDone(true);
            FAHelper.logXServerFirstConnectEvent(getAppContext());
        }
        appConfig.setGuestLaunchesCount(appConfig.getGuestLaunchesCount() + 1);
        this.receivedEvent = true;
        ((GuestApplicationsTrackerComponent) ((EnvironmentAware) getApplicationState()).getEnvironment().getComponent(GuestApplicationsTrackerComponent.class)).freezeGuestApplications();
        sendDone();
        removeListeners();
    }

    /* JADX INFO: Access modifiers changed from: private */
    private synchronized void guestApplicationsTerminated() {
        if (this.receivedEvent) {
            return;
        }
        this.receivedEvent = true;
        sendError("Guest applications died before showing anything.");
        removeListeners();
    }

    private void removeListeners() {
        AXSEnvironment environment = ((EnvironmentAware) getApplicationState()).getEnvironment();
        XServerComponent xServerComponent = (XServerComponent) environment.getComponent(XServerComponent.class);
        GuestApplicationsTrackerComponent guestApplicationsTrackerComponent = (GuestApplicationsTrackerComponent) environment.getComponent(GuestApplicationsTrackerComponent.class);
        LocksManager.XLock lock = xServerComponent.getXServer().getLocksManager().lock(LocksManager.Subsystem.WINDOWS_MANAGER);
        try {
            xServerComponent.getXServer().getWindowsManager().removeWindowContentModificationListner(this.putImageListener);
            guestApplicationsTrackerComponent.removeListener(this.guestApplicationTerminationListener);
            lock.close();
            this.putImageListener = null;
            this.guestApplicationTerminationListener = null;
        } catch (Throwable th) {
            lock.close();
            throw th;
        }
    }
}