package com.eltechs.axs.proto.input.annotations.impl;

import com.eltechs.axs.helpers.Assert;
import com.eltechs.axs.proto.input.XProtocolError;
import com.eltechs.axs.proto.input.parameterReaders.ParameterReader;
import com.eltechs.axs.xconnectors.XRequest;
import com.eltechs.axs.xconnectors.XResponse;
import com.eltechs.axs.xserver.XServer;
import com.eltechs.axs.xserver.events.Event;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/* loaded from: classes.dex */
public class EventParser {
    private final RequestStreamParser impl;
    private final Method parseMethod;

    public EventParser(ParameterReader[] parameterReaderArr, Method method) {
        this.impl = new RequestStreamParser(parameterReaderArr);
        this.parseMethod = method;
    }

    public Event parse(ParametersCollectionContext parametersCollectionContext) throws XProtocolError {
        try {
            return (Event) this.parseMethod.invoke(null, getEventParameters(parametersCollectionContext.getConnectionContext(), parametersCollectionContext.getXServer(), parametersCollectionContext.getDataRetrievalContext().req, parametersCollectionContext.getDataRetrievalContext().resp));
        } catch (IllegalAccessException unused) {
            Assert.state(false, String.format("Event parser %s must be public", this.parseMethod.toString()));
            Assert.unreachable();
            return null;
        } catch (InvocationTargetException unused2) {
            Assert.state(false, String.format("Event parser %s must not throw exceptions", this.parseMethod.toString()));
            Assert.unreachable();
            return null;
        }
    }

    private Object[] getEventParameters(Object obj, XServer xServer, XRequest xRequest, XResponse xResponse) throws XProtocolError {
        return this.impl.parse(xServer, obj, new RequestDataRetrievalContext(xRequest, xResponse, (byte) 0, 31));
    }
}
