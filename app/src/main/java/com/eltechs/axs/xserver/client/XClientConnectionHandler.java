package com.eltechs.axs.xserver.client;

import com.eltechs.axs.proto.input.ConnectionHandler;
import com.eltechs.axs.xconnectors.XInputStream;
import com.eltechs.axs.xconnectors.XOutputStream;
import com.eltechs.axs.xserver.XServer;

/* loaded from: classes.dex */
public class XClientConnectionHandler implements ConnectionHandler<XClient> {
    private XServer xServer;

    public XClientConnectionHandler(XServer xServer) {
        this.xServer = xServer;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // com.eltechs.axs.proto.input.ConnectionHandler
    /* renamed from: handleNewConnection */
    public XClient handleNewConnection(XInputStream xInputStream, XOutputStream xOutputStream) {
        return new XClient(this.xServer, xOutputStream);
    }

    @Override // com.eltechs.axs.proto.input.ConnectionHandler
    public void handleConnectionShutdown(XClient xClient) {
        xClient.freeAssociatedResources();
    }
}
