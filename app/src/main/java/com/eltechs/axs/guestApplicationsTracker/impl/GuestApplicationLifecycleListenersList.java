package com.eltechs.axs.guestApplicationsTracker.impl;

import com.eltechs.axs.guestApplicationsTracker.GuestApplicationsLifecycleListener;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class GuestApplicationLifecycleListenersList {
    private final List<GuestApplicationsLifecycleListener> listeners = new ArrayList<>();

    public void addListener(GuestApplicationsLifecycleListener guestApplicationsLifecycleListener) {
        this.listeners.add(guestApplicationsLifecycleListener);
    }

    public void removeListener(GuestApplicationsLifecycleListener guestApplicationsLifecycleListener) {
        this.listeners.remove(guestApplicationsLifecycleListener);
    }

    public void sendTranslatorStarted(Translator translator) {
        for (GuestApplicationsLifecycleListener guestApplicationsLifecycleListener : this.listeners) {
            guestApplicationsLifecycleListener.translatorStarted(translator);
        }
    }

    public void sendTranslatorExited(Translator translator) {
        for (GuestApplicationsLifecycleListener guestApplicationsLifecycleListener : this.listeners) {
            guestApplicationsLifecycleListener.translatorExited(translator);
        }
    }
}