package com.eltechs.axs.proto.input.parameterReaders.impl;

import com.eltechs.axs.proto.input.XProtocolError;
import com.eltechs.axs.proto.input.annotations.impl.ParametersCollectionContext;

/* loaded from: classes.dex */
public class ConnectionContextParameterReader extends ParameterReaderBase {
    public ConnectionContextParameterReader() {
        super(null);
    }

    @Override // com.eltechs.axs.proto.input.parameterReaders.impl.ParameterReaderBase
    protected Object readParameterImpl(ParametersCollectionContext parametersCollectionContext) throws XProtocolError {
        return parametersCollectionContext.getConnectionContext();
    }
}
