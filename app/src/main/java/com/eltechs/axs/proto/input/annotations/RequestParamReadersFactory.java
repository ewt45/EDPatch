package com.eltechs.axs.proto.input.annotations;

import com.eltechs.axs.proto.input.annotations.impl.ParameterDescriptor;
import com.eltechs.axs.proto.input.parameterReaders.ParameterReader;

/* loaded from: classes.dex */
public interface RequestParamReadersFactory {
    ParameterReader createReader(ParameterDescriptor parameterDescriptor, ConfigurationContext configurationContext);
}
