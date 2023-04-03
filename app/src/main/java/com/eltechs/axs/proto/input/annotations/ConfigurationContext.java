package com.eltechs.axs.proto.input.annotations;

import com.eltechs.axs.proto.input.annotations.impl.ParameterDescriptor;

/* loaded from: classes.dex */
public interface ConfigurationContext {
    ParameterDescriptor findNamedParameter(String str);

    String getHandlerMethodName();

    ParameterDescriptor getParameter(int i);

    int getParametersCount();
}
