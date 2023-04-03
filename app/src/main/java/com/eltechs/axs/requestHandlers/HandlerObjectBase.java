package com.eltechs.axs.requestHandlers;

import com.eltechs.axs.proto.input.annotations.BoundToXServer;
import com.eltechs.axs.xserver.XServer;

/* loaded from: classes.dex */
public abstract class HandlerObjectBase implements BoundToXServer {
    protected final XServer xServer;

    /* JADX INFO: Access modifiers changed from: protected */
    public HandlerObjectBase(XServer xServer) {
        this.xServer = xServer;
    }

    @Override // com.eltechs.axs.proto.input.annotations.BoundToXServer
    public final XServer getXServer() {
        return this.xServer;
    }
}
