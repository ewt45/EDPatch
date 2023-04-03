package com.eltechs.axs.proto.input.parameterReaders;

import com.eltechs.axs.proto.input.XProtocolError;
import com.eltechs.axs.proto.input.annotations.impl.ParametersCollectionContext;

/* loaded from: classes.dex */
public interface ParameterReader {
    void readParameter(ParametersCollectionContext parametersCollectionContext) throws XProtocolError;
}
