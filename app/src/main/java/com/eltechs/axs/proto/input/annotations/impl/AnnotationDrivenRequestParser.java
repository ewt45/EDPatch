package com.eltechs.axs.proto.input.annotations.impl;

import com.eltechs.axs.proto.input.XProtocolError;
import com.eltechs.axs.proto.input.parameterReaders.ParameterReader;
import com.eltechs.axs.xconnectors.XRequest;
import com.eltechs.axs.xconnectors.XResponse;
import com.eltechs.axs.xserver.XServer;
import com.eltechs.axs.xserver.client.XClient;

/* loaded from: classes.dex */
public class AnnotationDrivenRequestParser {
    private final RequestStreamParser impl;

    public AnnotationDrivenRequestParser(ParameterReader[] parameterReaderArr) {
        this.impl = new RequestStreamParser(parameterReaderArr);
    }

    public Object[] getRequestHandlerParameters(XClient xClient, XServer xServer, XRequest xRequest, XResponse xResponse, int length, byte minorOpCode) throws XProtocolError {
        return this.impl.parse(xServer, xClient, new RequestDataRetrievalContext(xRequest, xResponse, minorOpCode, length));
    }
}
