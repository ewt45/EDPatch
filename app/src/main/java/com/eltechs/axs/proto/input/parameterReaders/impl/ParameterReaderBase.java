package com.eltechs.axs.proto.input.parameterReaders.impl;

import com.eltechs.axs.proto.input.XProtocolError;
import com.eltechs.axs.proto.input.annotations.impl.ParametersCollectionContext;
import com.eltechs.axs.proto.input.annotations.impl.RequestDataReader;
import com.eltechs.axs.proto.input.parameterReaders.ParameterReader;

/* loaded from: classes.dex */
public abstract class ParameterReaderBase implements ParameterReader {
    protected final RequestDataReader dataReader;

    protected abstract Object readParameterImpl(ParametersCollectionContext parametersCollectionContext) throws XProtocolError;

    /* JADX INFO: Access modifiers changed from: protected */
    public ParameterReaderBase(RequestDataReader requestDataReader) {
        this.dataReader = requestDataReader;
    }

    @Override // com.eltechs.axs.proto.input.parameterReaders.ParameterReader
    public final void readParameter(ParametersCollectionContext parametersCollectionContext) throws XProtocolError {
        parametersCollectionContext.parameterCollected(readParameterImpl(parametersCollectionContext));
    }
}
