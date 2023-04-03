package com.eltechs.axs.proto.input.impl;

import com.eltechs.axs.helpers.ArithHelpers;
import com.eltechs.axs.proto.input.ExtensionRequestHandler;
import com.eltechs.axs.proto.input.XProtocolError;
import com.eltechs.axs.proto.input.errors.BadRequest;
import com.eltechs.axs.xconnectors.XRequest;
import com.eltechs.axs.xconnectors.XResponse;
import com.eltechs.axs.xserver.client.XClient;
import java.io.IOException;

/* loaded from: classes.dex */
public class BigReqExtensionHandler implements ExtensionRequestHandler {
    private static final int REQUEST_SIZE_LIMIT_HINT = 16777212;

    @Override // com.eltechs.axs.proto.input.ExtensionRequestHandler
    public byte getAssignedMajorOpcode() {
        return (byte) -113;
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
        return "BIG-REQUESTS";
    }

    @Override // com.eltechs.axs.proto.input.ExtensionRequestHandler
    public void handleRequest(XClient xClient, byte b, byte b2, int i, XRequest xRequest, XResponse xResponse) throws XProtocolError, IOException {
        xRequest.setMinorOpcode((short) ArithHelpers.extendAsUnsigned(b2));
        if (b2 != 0) {
            throw new BadRequest();
        }
        xResponse.sendSimpleSuccessReply((byte) 0, 4194303, (short) 0);
    }
}
