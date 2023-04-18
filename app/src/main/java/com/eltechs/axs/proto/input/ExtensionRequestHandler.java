package com.eltechs.axs.proto.input;

import com.eltechs.axs.xconnectors.XRequest;
import com.eltechs.axs.xconnectors.XResponse;
import com.eltechs.axs.xserver.client.XClient;
import java.io.IOException;

/* loaded from: classes.dex */
public interface ExtensionRequestHandler {
    byte getAssignedMajorOpcode();

    byte getFirstAssignedErrorId();

    byte getFirstAssignedEventId();

    String getName();

    void handleRequest(XClient xClient, byte majorOpCode, byte minorOpCode, int length, XRequest xRequest, XResponse xResponse) throws XProtocolError, IOException;
}
