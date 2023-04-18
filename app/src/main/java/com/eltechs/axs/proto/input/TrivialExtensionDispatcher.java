package com.eltechs.axs.proto.input;

import com.eltechs.axs.helpers.ArithHelpers;
import com.eltechs.axs.proto.input.errors.BadRequest;
import com.eltechs.axs.proto.input.impl.OpcodeHandlersRegistry;
import com.eltechs.axs.xconnectors.XRequest;
import com.eltechs.axs.xconnectors.XResponse;
import com.eltechs.axs.xserver.client.XClient;
import java.io.IOException;

/* loaded from: classes.dex */
public abstract class TrivialExtensionDispatcher implements ConfigurableRequestsDispatcher {
    private final byte firstAssignedErrorId;
    private final byte firstAssignedEventId;
    private final OpcodeHandlersRegistry handlersRegistry = new OpcodeHandlersRegistry();
    private final byte majorOpcode;
    private final String name;

    /* JADX INFO: Access modifiers changed from: protected */
    public TrivialExtensionDispatcher(byte b, String str, byte b2, byte b3) {
        this.majorOpcode = b;
        this.name = str;
        this.firstAssignedEventId = b2;
        this.firstAssignedErrorId = b3;
    }

    @Override // com.eltechs.axs.proto.input.ExtensionRequestHandler
    public final byte getAssignedMajorOpcode() {
        return this.majorOpcode;
    }

    @Override // com.eltechs.axs.proto.input.ExtensionRequestHandler
    public final String getName() {
        return this.name;
    }

    @Override // com.eltechs.axs.proto.input.ExtensionRequestHandler
    public final byte getFirstAssignedEventId() {
        return this.firstAssignedEventId;
    }

    @Override // com.eltechs.axs.proto.input.ExtensionRequestHandler
    public final byte getFirstAssignedErrorId() {
        return this.firstAssignedErrorId;
    }

    @Override // com.eltechs.axs.proto.input.ExtensionRequestHandler
    public final void handleRequest(XClient xClient, byte majorOpCode, byte minorOpCode, int length, XRequest xRequest, XResponse xResponse) throws XProtocolError, IOException {
        short extendAsUnsigned = (short) ArithHelpers.extendAsUnsigned(minorOpCode);
        OpcodeHandler handler = this.handlersRegistry.getHandler(extendAsUnsigned);
        xRequest.setMinorOpcode(extendAsUnsigned);
        if (handler == null) {
            throw new BadRequest();
        }
        handler.handleRequest(xClient, length, minorOpCode, xRequest, xResponse);
    }

    @Override // com.eltechs.axs.proto.input.ConfigurableRequestsDispatcher
    public final void installRequestHandler(int i, OpcodeHandler opcodeHandler) {
        this.handlersRegistry.installRequestHandler(i, opcodeHandler);
    }
}
