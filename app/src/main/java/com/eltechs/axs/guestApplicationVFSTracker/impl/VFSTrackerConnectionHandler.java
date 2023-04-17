package com.eltechs.axs.guestApplicationVFSTracker.impl;

import com.eltechs.axs.proto.input.ConnectionHandler;
import com.eltechs.axs.xconnectors.XInputStream;
import com.eltechs.axs.xconnectors.XOutputStream;
import java.nio.ByteOrder;

/* loaded from: classes.dex */
public class VFSTrackerConnectionHandler implements ConnectionHandler<VFSTrackerConnection> {
    @Override // com.eltechs.axs.proto.input.ConnectionHandler
    public void handleConnectionShutdown(VFSTrackerConnection vFSTrackerConnection) {
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // com.eltechs.axs.proto.input.ConnectionHandler
    public VFSTrackerConnection handleNewConnection(XInputStream xInputStream, XOutputStream xOutputStream) {
        xInputStream.setByteOrder(ByteOrder.LITTLE_ENDIAN);
        xOutputStream.setByteOrder(ByteOrder.LITTLE_ENDIAN);
        return new VFSTrackerConnection(xOutputStream);
    }
}