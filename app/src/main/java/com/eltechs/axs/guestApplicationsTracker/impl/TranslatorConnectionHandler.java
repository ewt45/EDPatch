package com.eltechs.axs.guestApplicationsTracker.impl;

import com.eltechs.axs.proto.input.ConnectionHandler;
import com.eltechs.axs.xconnectors.XInputStream;
import com.eltechs.axs.xconnectors.XOutputStream;
import java.nio.ByteOrder;

/* loaded from: classes.dex */
public class TranslatorConnectionHandler implements ConnectionHandler<TranslatorConnection> {
    private final GuestApplicationsCollection guestApplicationsCollection;

    public TranslatorConnectionHandler(GuestApplicationsCollection guestApplicationsCollection) {
        this.guestApplicationsCollection = guestApplicationsCollection;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // com.eltechs.axs.proto.input.ConnectionHandler
    public TranslatorConnection handleNewConnection(XInputStream xInputStream, XOutputStream xOutputStream) {
        xInputStream.setByteOrder(ByteOrder.LITTLE_ENDIAN);
        xOutputStream.setByteOrder(ByteOrder.LITTLE_ENDIAN);
        return new TranslatorConnection(xOutputStream);
    }

    @Override // com.eltechs.axs.proto.input.ConnectionHandler
    public void handleConnectionShutdown(TranslatorConnection translatorConnection) {
        translatorConnection.processShutdown();
    }
}