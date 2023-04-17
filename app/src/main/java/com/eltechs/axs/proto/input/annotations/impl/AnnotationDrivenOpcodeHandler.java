package com.eltechs.axs.proto.input.annotations.impl;

import com.eltechs.axs.helpers.Assert;
import com.eltechs.axs.proto.input.OpcodeHandler;
import com.eltechs.axs.proto.input.XProtocolError;
import com.eltechs.axs.proto.input.annotations.BoundToXServer;
import com.eltechs.axs.xconnectors.XRequest;
import com.eltechs.axs.xconnectors.XResponse;
import com.eltechs.axs.xserver.LocksManager;
import com.eltechs.axs.xserver.XServer;
import com.eltechs.axs.xserver.client.XClient;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/* loaded from: classes.dex */
public class AnnotationDrivenOpcodeHandler implements OpcodeHandler {
    private final Method handlerMethod;
    private final Object handlerObject;
    private final LocksManager.Subsystem[] locks;
    private final AnnotationDrivenRequestParser requestParser;
    private final XServer xServer;// = getXServer();

    public AnnotationDrivenOpcodeHandler(Object obj, Method method, LocksManager.Subsystem[] subsystemArr, AnnotationDrivenRequestParser annotationDrivenRequestParser) {
        this.handlerObject = obj;
        this.handlerMethod = method;
        this.locks = subsystemArr;
        this.requestParser = annotationDrivenRequestParser;
        xServer = getXServer();
    }

    private XServer getXServer() {
        Assert.isTrue(this.handlerObject instanceof BoundToXServer, String.format("Request handler objects must be bound to a X-Server, but %s is not.", this.handlerObject.getClass().getSimpleName()));
        return ((BoundToXServer) this.handlerObject).getXServer();
    }

    @Override // com.eltechs.axs.proto.input.OpcodeHandler
    public void handleRequest(XClient xClient, int i, byte b, XRequest xRequest, XResponse xResponse) throws XProtocolError, IOException {
        try {
            LocksManager.XLock lock = this.xServer.getLocksManager().lock(this.locks);
            try {
                this.handlerMethod.invoke(this.handlerObject, this.requestParser.getRequestHandlerParameters(xClient, this.xServer, xRequest, xResponse, i, b));
                if (lock == null) {
                    return;
                }
                lock.close();
            } catch (Throwable th) {
                if (lock != null) {
                    try {
                        lock.close();
                    } catch (Throwable th3) {
                        th.addSuppressed(th3);
                    }
                }
                throw th;
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            Throwable targetException = e.getTargetException();
            if (targetException instanceof IOException) {
                throw ((IOException) targetException);
            }
            if (targetException instanceof XProtocolError) {
                throw ((XProtocolError) targetException);
            }
            targetException.printStackTrace();
        }
    }
}
