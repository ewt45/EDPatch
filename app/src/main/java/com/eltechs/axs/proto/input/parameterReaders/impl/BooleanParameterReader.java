package com.eltechs.axs.proto.input.parameterReaders.impl;

import com.eltechs.axs.proto.input.XProtocolError;
import com.eltechs.axs.proto.input.annotations.impl.ParameterDescriptor;
import com.eltechs.axs.proto.input.annotations.impl.ParametersCollectionContext;
import com.eltechs.axs.proto.input.annotations.impl.RequestDataReader;

/* loaded from: classes.dex */
public class BooleanParameterReader extends PrimitiveTypeParameterReader {
    public BooleanParameterReader(RequestDataReader requestDataReader, ParameterDescriptor parameterDescriptor) {
        super(requestDataReader, parameterDescriptor, 1, true);
    }

    @Override // com.eltechs.axs.proto.input.parameterReaders.impl.ParameterReaderBase
    protected Object readParameterImpl(ParametersCollectionContext parametersCollectionContext) throws XProtocolError {
        return getUnderlyingValue(parametersCollectionContext) != 0 ? Boolean.TRUE : Boolean.FALSE;
    }
}
