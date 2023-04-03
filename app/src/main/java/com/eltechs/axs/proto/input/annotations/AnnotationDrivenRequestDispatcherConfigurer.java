package com.eltechs.axs.proto.input.annotations;

import com.eltechs.axs.helpers.Assert;
import com.eltechs.axs.proto.input.ConfigurableRequestsDispatcher;
import com.eltechs.axs.proto.input.annotations.impl.AnnotationDrivenOpcodeHandler;
import com.eltechs.axs.proto.input.annotations.impl.AnnotationDrivenRequestParser;
import com.eltechs.axs.proto.input.annotations.impl.ParameterDescriptor;
import com.eltechs.axs.proto.input.parameterReaders.ParameterReader;
import com.eltechs.axs.xserver.LocksManager;
import java.lang.reflect.Method;

/* loaded from: classes.dex */
public class AnnotationDrivenRequestDispatcherConfigurer {
    private final RequestContextParamReadersFactory reqCtxParamReadersFactory;
    private final RequestParamReadersFactory reqParamReadersFactory;
    private final ConfigurableRequestsDispatcher target;

    public AnnotationDrivenRequestDispatcherConfigurer(ConfigurableRequestsDispatcher configurableRequestsDispatcher, RequestContextParamReadersFactory requestContextParamReadersFactory, RequestParamReadersFactory requestParamReadersFactory) {
        this.target = configurableRequestsDispatcher;
        this.reqCtxParamReadersFactory = requestContextParamReadersFactory;
        this.reqParamReadersFactory = requestParamReadersFactory;
    }

    public void configureDispatcher(Object... objArr) {
        Method[] methods;
        for (Object obj : objArr) {
            for (Method method : obj.getClass().getMethods()) {
                RequestHandler requestHandler = method.getAnnotation(RequestHandler.class);
                if (requestHandler != null) {
                    processOneHandler(requestHandler.opcode(), obj, method);
                }
            }
        }
    }

    private void processOneHandler(int i, Object obj, Method method) {
        this.target.installRequestHandler(i, new AnnotationDrivenOpcodeHandler(obj, method, getNeededLocks(method), buildRequestParser(method)));
    }

    private AnnotationDrivenRequestParser buildRequestParser(Method method) {
        ParameterDescriptor[] methodParameters = ParameterDescriptor.getMethodParameters(method);
        int length = methodParameters.length;
        ParameterReader[] parameterReaderArr = new ParameterReader[length];
        for (int i = 0; i < length; i++) {
            parameterReaderArr[i] = configureParameterReader(method, methodParameters, i);
        }
        return new AnnotationDrivenRequestParser(parameterReaderArr);
    }

    private ParameterReader configureParameterReader(final Method method, final ParameterDescriptor[] parameterDescriptorArr, int i) {
        ParameterReader createReader;
        ParameterDescriptor parameterDescriptor = parameterDescriptorArr[i];
        ConfigurationContext configurationContext = new ConfigurationContext() { // from class: com.eltechs.axs.proto.input.annotations.AnnotationDrivenRequestDispatcherConfigurer.1
            @Override // com.eltechs.axs.proto.input.annotations.ConfigurationContext
            public String getHandlerMethodName() {
                return String.format("%s::%s()", method.getDeclaringClass().getSimpleName(), method.getName());
            }

            @Override // com.eltechs.axs.proto.input.annotations.ConfigurationContext
            public int getParametersCount() {
                return parameterDescriptorArr.length;
            }

            @Override // com.eltechs.axs.proto.input.annotations.ConfigurationContext
            public ParameterDescriptor getParameter(int i2) {
                return parameterDescriptorArr[i2];
            }

            @Override // com.eltechs.axs.proto.input.annotations.ConfigurationContext
            public ParameterDescriptor findNamedParameter(String str) {
                return AnnotationDrivenRequestDispatcherConfigurer.this.findNamedParameter(parameterDescriptorArr, str);
            }
        };
        if (((RequestParam) parameterDescriptor.getAnnotation(RequestParam.class)) == null) {
            createReader = this.reqCtxParamReadersFactory.createReader(parameterDescriptor, configurationContext);
        } else {
            createReader = this.reqParamReadersFactory.createReader(parameterDescriptor, configurationContext);
        }
        Assert.state(createReader != null, String.format("Resolved no parameter reader for the context parameter %d of the request handler method %s.", Integer.valueOf(parameterDescriptor.getIndex()), configurationContext.getHandlerMethodName()));
        return createReader;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public ParameterDescriptor findNamedParameter(ParameterDescriptor[] parameterDescriptorArr, String str) {
        for (ParameterDescriptor parameterDescriptor : parameterDescriptorArr) {
            ParamName paramName = (ParamName) parameterDescriptor.getAnnotation(ParamName.class);
            if (paramName != null && str.equals(paramName.value())) {
                return parameterDescriptor;
            }
        }
        return null;
    }

    private LocksManager.Subsystem[] getNeededLocks(Method method) {
        if (method.getAnnotation(GiantLocked.class) != null) {
            return LocksManager.Subsystem.values();
        }
        Locks locks = (Locks) method.getAnnotation(Locks.class);
        if (locks == null) {
            return new LocksManager.Subsystem[0];
        }
        String[] value = locks.value();
        int length = value.length;
        LocksManager.Subsystem[] subsystemArr = new LocksManager.Subsystem[length];
        for (int i = 0; i < length; i++) {
            subsystemArr[i] = LocksManager.Subsystem.valueOf(value[i]);
        }
        return subsystemArr;
    }
}
