package com.eltechs.axs.environmentService.components;

import android.util.Log;

import com.eltechs.axs.Globals;
import com.eltechs.axs.applicationState.MemsplitConfigurationAware;
import com.eltechs.axs.configuration.UBTLaunchConfiguration;
import com.eltechs.axs.environmentService.EnvironmentComponent;
import com.eltechs.axs.guestApplicationsTracker.GuestApplicationsLifecycleListener;
import com.eltechs.axs.guestApplicationsTracker.GuestApplicationsTracker;
import com.eltechs.axs.helpers.Assert;
import com.eltechs.axs.xconnectors.epoll.UnixSocketConfiguration;
import java.io.IOException;

/* loaded from: classes.dex */
public class GuestApplicationsTrackerComponent extends EnvironmentComponent {
    private static final String TAG ="GuestApplicationsTrackerComponent";
    private final UnixSocketConfiguration socketConf;
    private GuestApplicationsTracker tracker;

    public GuestApplicationsTrackerComponent(UnixSocketConfiguration unixSocketConfiguration) {
        this.socketConf = unixSocketConfiguration;
    }

    @Override // com.eltechs.axs.environmentService.EnvironmentComponent
    public void start() throws IOException {
        String libubt2GPath;
        Assert.state(this.tracker == null, "Guest applications tracker is already started.");
        NativeLibsConfiguration nativeLibsConfiguration = getEnvironment().getNativeLibsConfiguration();
        if (((MemsplitConfigurationAware) Globals.getApplicationState()).getMemsplitConfiguration().isMemsplit3g()) {
            libubt2GPath = nativeLibsConfiguration.getLibubtPath();
        } else {
            libubt2GPath = nativeLibsConfiguration.getLibubt2GPath();
        }
        this.tracker = new GuestApplicationsTracker(this.socketConf, nativeLibsConfiguration.getElfLoaderPath(), libubt2GPath, nativeLibsConfiguration.getKillswitchPath());
    }

    @Override // com.eltechs.axs.environmentService.EnvironmentComponent
    public void stop() {
        Assert.state(this.tracker != null, "Guest applications tracker is not yet started.");
        this.tracker.killGuestApplications();
        try {
            this.tracker.stop();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.tracker = null;
    }

    public String getSocket() {
        return this.socketConf.getGuestPath();
    }

    public void startGuestApplication(UBTLaunchConfiguration uBTLaunchConfiguration) {
        //如果后台同时启动了两个ex的xserver，这里会报null错误(不对，是因为我在service里上一个component报错了还强制启动这个component）
        this.tracker.startGuestApplication(uBTLaunchConfiguration, getEnvironment());
    }

    public void addListener(GuestApplicationsLifecycleListener guestApplicationsLifecycleListener) {
        Assert.state(this.tracker != null, "Guest applications tracker is not yet started.");
        this.tracker.addListener(guestApplicationsLifecycleListener);
    }

    public void removeListener(GuestApplicationsLifecycleListener guestApplicationsLifecycleListener) {
        Assert.state(this.tracker != null, "Guest applications tracker is not yet started.");
        this.tracker.removeListener(guestApplicationsLifecycleListener);
    }

    public void freezeGuestApplications() {
        Assert.state(this.tracker != null, "Guest applications tracker is not yet started.");
        this.tracker.freezeGuestApplications();
    }

    public void resumeGuestApplications() {
        Assert.state(this.tracker != null, "Guest applications tracker is not yet started.");
        this.tracker.resumeGuestApplications();
    }

    public boolean haveGuestApplications() {
        Assert.state(this.tracker != null, "Guest applications tracker is not yet started.");
        return this.tracker.haveGuestApplications();
    }
}