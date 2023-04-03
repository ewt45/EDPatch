package com.eltechs.axs.guestApplicationsTracker;

import com.eltechs.axs.guestApplicationsTracker.impl.Translator;

/* loaded from: classes.dex */
public interface GuestApplicationsLifecycleListener {
    void translatorExited(Translator translator);

    void translatorStarted(Translator translator);
}