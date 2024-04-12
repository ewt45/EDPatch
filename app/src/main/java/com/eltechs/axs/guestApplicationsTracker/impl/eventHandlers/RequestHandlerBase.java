package com.eltechs.axs.guestApplicationsTracker.impl.eventHandlers;

import com.eltechs.axs.guestApplicationsTracker.impl.GuestApplicationsCollection;
import com.eltechs.axs.guestApplicationsTracker.impl.TranslatorConnection;
import com.eltechs.axs.xconnectors.RequestHandler;

/* loaded from: classes.dex */
public abstract class RequestHandlerBase implements RequestHandler<TranslatorConnection> {
    protected static final int SIZE_OF_INT = 4;
    protected final GuestApplicationsCollection guestApplicationsCollection;

    protected RequestHandlerBase(GuestApplicationsCollection guestApplicationsCollection) {
        this.guestApplicationsCollection = guestApplicationsCollection;
    }
}