package com.eltechs.axs.guestApplicationsTracker.impl;

import com.eltechs.axs.guestApplicationsTracker.impl.eventHandlers.AboutToFork;
import com.eltechs.axs.guestApplicationsTracker.impl.eventHandlers.Forked;
import com.eltechs.axs.guestApplicationsTracker.impl.eventHandlers.TranslatorStarted;
import com.eltechs.axs.helpers.ArithHelpers;
import com.eltechs.axs.proto.input.ProcessingResult;
import com.eltechs.axs.xconnectors.RequestHandler;
import com.eltechs.axs.xconnectors.XInputStream;
import com.eltechs.axs.xconnectors.XOutputStream;
import java.io.IOException;
import java.util.EnumMap;
import java.util.Map;

/* loaded from: classes.dex */
public class TranslatorRequestsDispatcher implements RequestHandler<TranslatorConnection> {
    public static final int MAGIC = 1263685446;
    public static final int MINIMUM_REQUEST_LENGTH = 8;
    public static final int MINIMUM_RESPONSE_LENGTH = 6;
    private static final int SIZE_OF_INT = 4;
    private static final int SIZE_OF_SHORT = 2;
    private final GuestApplicationsCollection guestApplicationsCollection;
    private final Map<RequestCodes, RequestHandler<TranslatorConnection>> requestHandlers = new EnumMap<>(RequestCodes.class);

    public TranslatorRequestsDispatcher(GuestApplicationsCollection guestApplicationsCollection) {
        this.guestApplicationsCollection = guestApplicationsCollection;
        this.requestHandlers.put(RequestCodes.TRANSLATOR_STARTED, new TranslatorStarted(guestApplicationsCollection));
        this.requestHandlers.put(RequestCodes.ABOUT_TO_FORK, new AboutToFork(guestApplicationsCollection));
        this.requestHandlers.put(RequestCodes.FORKED, new Forked(guestApplicationsCollection));
    }

    @Override // com.eltechs.axs.xconnectors.RequestHandler
    public ProcessingResult handleRequest(TranslatorConnection translatorConnection, XInputStream xInputStream, XOutputStream xOutputStream) throws IOException {
        if (xInputStream.getAvailableBytesCount() < MINIMUM_REQUEST_LENGTH) {
            return ProcessingResult.INCOMPLETE_BUFFER;
        }
        int rqMagic = xInputStream.getInt();
        int extendAsUnsigned = ArithHelpers.extendAsUnsigned(xInputStream.getShort()) - MINIMUM_REQUEST_LENGTH;
        int requestCode = ArithHelpers.extendAsUnsigned(xInputStream.getShort());
        if (rqMagic != MAGIC || requestCode < 0 || requestCode >= this.requestHandlers.size()) {
            return ProcessingResult.PROCESSED_KILL_CONNECTION;
        }
        if (xInputStream.getAvailableBytesCount() < extendAsUnsigned) {
            return ProcessingResult.INCOMPLETE_BUFFER;
        }
        return this.requestHandlers.get(RequestCodes.values()[requestCode]).handleRequest(translatorConnection, xInputStream, xOutputStream);
    }
}