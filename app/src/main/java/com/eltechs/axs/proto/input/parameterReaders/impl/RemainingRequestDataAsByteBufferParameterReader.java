package com.eltechs.axs.proto.input.parameterReaders.impl;

import com.eltechs.axs.proto.input.XProtocolError;
import com.eltechs.axs.proto.input.annotations.impl.ParametersCollectionContext;
import com.eltechs.axs.proto.input.annotations.impl.RequestDataRetrievalContext;

/* loaded from: classes.dex */
public class RemainingRequestDataAsByteBufferParameterReader extends ParameterReaderBase {
    public RemainingRequestDataAsByteBufferParameterReader() {
        super(null);
    }

    @Override // com.eltechs.axs.proto.input.parameterReaders.impl.ParameterReaderBase
    protected Object readParameterImpl(ParametersCollectionContext parametersCollectionContext) throws XProtocolError {
        RequestDataRetrievalContext dataRetrievalContext = parametersCollectionContext.getDataRetrievalContext();
        int i = dataRetrievalContext.remainingBytesCount;
        dataRetrievalContext.remainingBytesCount = 0;
        return dataRetrievalContext.req.readAsByteBuffer(i);
    }
}
