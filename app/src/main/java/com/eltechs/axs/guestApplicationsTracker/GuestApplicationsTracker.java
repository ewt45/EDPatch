package com.eltechs.axs.guestApplicationsTracker;

import android.util.Log;

import com.eltechs.axs.configuration.UBTLaunchConfiguration;
import com.eltechs.axs.environmentService.AXSEnvironment;
import com.eltechs.axs.guestApplicationsTracker.impl.GuestApplicationsCollection;
import com.eltechs.axs.guestApplicationsTracker.impl.Killswitch;
import com.eltechs.axs.guestApplicationsTracker.impl.TranslatorConnection;
import com.eltechs.axs.guestApplicationsTracker.impl.TranslatorConnectionHandler;
import com.eltechs.axs.guestApplicationsTracker.impl.TranslatorRequestsDispatcher;
import com.eltechs.axs.xconnectors.epoll.FairEpollConnector;
import com.eltechs.axs.xconnectors.epoll.UnixSocketConfiguration;
import java.io.IOException;

/* loaded from: classes.dex */
public class GuestApplicationsTracker {
    private final FairEpollConnector<TranslatorConnection> connector;
    private final GuestApplicationsCollection guestApplicationsCollection = new GuestApplicationsCollection();
    private final Killswitch killswitch;
    private final String libubtPath;

    public GuestApplicationsTracker(UnixSocketConfiguration unixSocketConfiguration, String elfLoaderPath, String libUbtPath, String killSwitchPath) throws IOException {
        this.connector = FairEpollConnector.listenOnSpecifiedUnixSocket(
                unixSocketConfiguration,
                new TranslatorConnectionHandler(this.guestApplicationsCollection),
                new TranslatorRequestsDispatcher(this.guestApplicationsCollection));
        this.connector.start();
        this.killswitch = new Killswitch(elfLoaderPath, killSwitchPath, libUbtPath);
        this.libubtPath = libUbtPath;
    }

    public void stop() throws IOException {
        freezeGuestApplications();
        killGuestApplications();
        this.connector.stop();
        this.killswitch.stop();
    }

    public void addListener(GuestApplicationsLifecycleListener guestApplicationsLifecycleListener) {
        this.guestApplicationsCollection.addListener(guestApplicationsLifecycleListener);
    }

    public void removeListener(GuestApplicationsLifecycleListener guestApplicationsLifecycleListener) {
        this.guestApplicationsCollection.removeListener(guestApplicationsLifecycleListener);
    }

    public boolean startGuestApplication(UBTLaunchConfiguration uBTLaunchConfiguration, AXSEnvironment aXSEnvironment) {
        int pid = UBT.runUbt(uBTLaunchConfiguration, aXSEnvironment, this.libubtPath);
        if (pid < 0) {
            return false;
        }
        Log.d("TAG", "startGuestApplication: fork的进程id="+pid);
        this.guestApplicationsCollection.registerTranslator(pid);
        return true;
    }

    public void freezeGuestApplications() {
        this.guestApplicationsCollection.freezeGuestApplications();
    }

    public void resumeGuestApplications() {
        this.guestApplicationsCollection.resumeGuestApplications();
    }

    public void killGuestApplications() {
        this.guestApplicationsCollection.killGuestApplications();
    }

    public boolean haveGuestApplications() {
        return this.guestApplicationsCollection.haveGuestApplications();
    }

    public String getLibubtPath() {
        return this.libubtPath;
    }
}