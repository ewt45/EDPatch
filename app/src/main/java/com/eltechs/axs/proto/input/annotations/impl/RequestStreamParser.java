package com.eltechs.axs.proto.input.annotations.impl;

import android.util.Log;

import com.eltechs.axs.proto.input.XProtocolError;
import com.eltechs.axs.proto.input.parameterReaders.ParameterReader;
import com.eltechs.axs.xserver.XServer;

import java.util.Arrays;

/* loaded from: classes.dex */
public class RequestStreamParser {
    private final ParameterReader[] parameterReaders;

    /* JADX INFO: Access modifiers changed from: package-private */
     RequestStreamParser(ParameterReader[] parameterReaderArr) {
        this.parameterReaders = parameterReaderArr;
    }

    public Object[] parse(XServer xServer, Object obj, RequestDataRetrievalContext requestDataRetrievalContext) throws XProtocolError {
        ParametersCollectionContext paramsContext = new ParametersCollectionContext(obj, xServer, requestDataRetrievalContext, this.parameterReaders.length);
        for (ParameterReader parameterReader : this.parameterReaders) {
            parameterReader.readParameter(paramsContext);
        }

        return paramsContext.getCollectedParameters();
    }
}
