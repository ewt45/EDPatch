package com.eltechs.axs.environmentService;

import android.content.Context;
import android.content.Intent;
import com.eltechs.axs.Globals;
import com.eltechs.axs.applicationState.EnvironmentAware;
import com.eltechs.axs.environmentService.components.DirectSoundServerComponent;
import com.eltechs.axs.environmentService.components.GuestApplicationsTrackerComponent;
import com.eltechs.axs.environmentService.components.NativeLibsConfiguration;
import com.eltechs.axs.helpers.Assert;
import com.termux.x11.CmdEntryPoint;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/* loaded from: classes.dex */
public class AXSEnvironment implements Iterable<EnvironmentComponent> {
    private final Context applicationContext;
    private final List<EnvironmentComponent> components = new ArrayList<>();
    StartupCallback startupCallback = null;
    TrayConfiguration trayConfiguration = null;

    /* loaded from: classes.dex */
    public interface StartupCallback {
        void serviceFailed(Throwable th);

        void serviceStarted();
    }

    public AXSEnvironment(Context context) {
        this.applicationContext = context;
        addComponent(new NativeLibsConfiguration(context));
    }

    public void addComponent(EnvironmentComponent environmentComponent) {
        Assert.state(getComponent(environmentComponent.getClass()) == null, String.format("A component of type '%s' is already registered within the guest environment.", environmentComponent.getClass().getName()));
        Assert.state(getService() == null, "It seems useless to add component after service has been already started.");
        this.components.add(environmentComponent);
        environmentComponent.addedToEnvironment(this);
    }

    public <T extends EnvironmentComponent> T getComponent(Class<T> cls) {
        for (EnvironmentComponent component : this.components) {
            if (component.getClass() == cls) {
                return (T) component;
            }
        }
        return null;
    }

    public NativeLibsConfiguration getNativeLibsConfiguration() {
        return getComponent(NativeLibsConfiguration.class);
    }

    @Override // java.lang.Iterable
    public Iterator<EnvironmentComponent> iterator() {
        return this.components.iterator();
    }

    public void startEnvironmentService(StartupCallback startupCallback, TrayConfiguration trayConfiguration) {
        this.startupCallback = startupCallback;
        this.trayConfiguration = trayConfiguration;
        this.applicationContext.startService(new Intent(this.applicationContext, AXSEnvironmentService.class));
        CmdEntryPoint.sendStartSignalInAppProcess();
    }

    private AXSEnvironmentService getService() {
        return ((EnvironmentAware) Globals.getApplicationState()).getEnvironmentServiceInstance();
    }

    public void freezeEnvironment() {
        Assert.state(getService() != null);
        GuestApplicationsTrackerComponent guestApplicationsTrackerComponent = getComponent(GuestApplicationsTrackerComponent.class);
        DirectSoundServerComponent directSoundServerComponent = getComponent(DirectSoundServerComponent.class);
        if (directSoundServerComponent != null) {
            directSoundServerComponent.suspendPlayback();
        }
        if (guestApplicationsTrackerComponent != null) {
            guestApplicationsTrackerComponent.freezeGuestApplications();
        }
    }

    public void resumeEnvironment() {
        Assert.state(getService() != null);
        GuestApplicationsTrackerComponent guestApplicationsTrackerComponent = getComponent(GuestApplicationsTrackerComponent.class);
        DirectSoundServerComponent directSoundServerComponent = getComponent(DirectSoundServerComponent.class);
        if (guestApplicationsTrackerComponent != null) {
            guestApplicationsTrackerComponent.resumeGuestApplications();
        }
        if (directSoundServerComponent != null) {
            directSoundServerComponent.resumePlayback();
        }
    }
}