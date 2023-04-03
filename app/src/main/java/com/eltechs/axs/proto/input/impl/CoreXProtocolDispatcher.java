package com.eltechs.axs.proto.input.impl;

import com.eltechs.axs.proto.input.ConfigurableRequestsDispatcher;
import com.eltechs.axs.proto.input.OpcodeHandler;
import com.eltechs.axs.proto.input.XProtocolError;
import com.eltechs.axs.proto.input.errors.BadRequest;
import com.eltechs.axs.xconnectors.XRequest;
import com.eltechs.axs.xconnectors.XResponse;
import com.eltechs.axs.xserver.client.XClient;
import java.io.IOException;

/* loaded from: classes.dex */
public class CoreXProtocolDispatcher implements ConfigurableRequestsDispatcher {
    private final OpcodeHandlersRegistry handlersRegistry = new OpcodeHandlersRegistry();

    @Override // com.eltechs.axs.proto.input.ExtensionRequestHandler
    public byte getAssignedMajorOpcode() {
        return (byte) 0;
    }

    @Override // com.eltechs.axs.proto.input.ExtensionRequestHandler
    public byte getFirstAssignedErrorId() {
        return (byte) 0;
    }

    @Override // com.eltechs.axs.proto.input.ExtensionRequestHandler
    public byte getFirstAssignedEventId() {
        return (byte) 0;
    }

    @Override // com.eltechs.axs.proto.input.ExtensionRequestHandler
    public String getName() {
        return "CORE";
    }

    @Override // com.eltechs.axs.proto.input.ExtensionRequestHandler
    public void handleRequest(XClient xClient, byte b, byte b2, int i, XRequest xRequest, XResponse xResponse) throws XProtocolError, IOException {
        OpcodeHandler handler = this.handlersRegistry.getHandler(b);
        xRequest.setMinorOpcode((short) 0);
        if (handler == null) {
            throw new BadRequest();
        }
        handler.handleRequest(xClient, i, b2, xRequest, xResponse);
    }

    @Override // com.eltechs.axs.proto.input.ConfigurableRequestsDispatcher
    public void installRequestHandler(int i, OpcodeHandler opcodeHandler) {
        this.handlersRegistry.installRequestHandler(i, opcodeHandler);
    }
}
