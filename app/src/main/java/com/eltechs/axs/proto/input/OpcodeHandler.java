package com.eltechs.axs.proto.input;

import com.eltechs.axs.xconnectors.XRequest;
import com.eltechs.axs.xconnectors.XResponse;
import com.eltechs.axs.xserver.client.XClient;
import java.io.IOException;

/* loaded from: classes.dex */
public interface OpcodeHandler {
    void handleRequest(XClient xClient, int length, byte minorOpCode, XRequest xRequest, XResponse xResponse) throws XProtocolError, IOException;
}
