package com.eltechs.axs.proto.input.parameterReaders.impl;

import com.eltechs.axs.proto.input.XProtocolError;
import com.eltechs.axs.proto.input.annotations.NewXId;
import com.eltechs.axs.proto.input.annotations.impl.ParameterDescriptor;
import com.eltechs.axs.proto.input.annotations.impl.ParametersCollectionContext;
import com.eltechs.axs.proto.input.annotations.impl.RequestDataReader;
import com.eltechs.axs.proto.input.errors.BadIdChoice;
import com.eltechs.axs.xserver.client.XClient;

/* loaded from: classes.dex */
public class IntegerParameterReader extends PrimitiveTypeParameterReader {
    private final boolean newXId;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public IntegerParameterReader(RequestDataReader requestDataReader, ParameterDescriptor parameterDescriptor) {
        super(requestDataReader, parameterDescriptor, 4, false);
        boolean z = false;
        this.newXId = ((NewXId) parameterDescriptor.getAnnotation(NewXId.class)) != null ? true : z;
    }

    @Override // com.eltechs.axs.proto.input.parameterReaders.impl.ParameterReaderBase
    protected Object readParameterImpl(ParametersCollectionContext parametersCollectionContext) throws XProtocolError {
        int underlyingValue = getUnderlyingValue(parametersCollectionContext);
        if (this.newXId && !((XClient) parametersCollectionContext.getConnectionContext()).getIdInterval().isInInterval(underlyingValue)) {
            throw new BadIdChoice(underlyingValue);
        }
        return Integer.valueOf(underlyingValue);
    }
}
