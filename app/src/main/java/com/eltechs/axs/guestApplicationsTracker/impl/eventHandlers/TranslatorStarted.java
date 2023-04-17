package com.eltechs.axs.guestApplicationsTracker.impl.eventHandlers;

import com.eltechs.axs.guestApplicationsTracker.impl.GuestApplicationsCollection;
import com.eltechs.axs.guestApplicationsTracker.impl.TranslatorConnection;
import com.eltechs.axs.proto.input.ProcessingResult;
import com.eltechs.axs.xconnectors.XInputStream;
import com.eltechs.axs.xconnectors.XOutputStream;
import java.io.IOException;

/* loaded from: classes.dex */
public class TranslatorStarted extends RequestHandlerBase {
    public TranslatorStarted(GuestApplicationsCollection guestApplicationsCollection) {
        super(guestApplicationsCollection);
    }

    @Override // com.eltechs.axs.xconnectors.RequestHandler
    public ProcessingResult handleRequest(TranslatorConnection translatorConnection, XInputStream xInputStream, XOutputStream xOutputStream) throws IOException {
        if (xInputStream.getAvailableBytesCount() < 4) {
            return ProcessingResult.INCOMPLETE_BUFFER;
        }
        this.guestApplicationsCollection.translatorStarted(xInputStream.getInt(), translatorConnection);
        return ProcessingResult.PROCESSED;
    }
}