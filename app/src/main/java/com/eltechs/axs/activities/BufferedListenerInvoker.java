package com.eltechs.axs.activities;

import com.eltechs.axs.helpers.Assert;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class BufferedListenerInvoker<ListenerType> {
    private final List<InvocationRequest> enqueuedInvocations = new ArrayList<>();
    private final ListenerType proxy;
    private ListenerType realListener;

    public BufferedListenerInvoker(Class<ListenerType> cls) {
        this.proxy = (ListenerType) Proxy.newProxyInstance(cls.getClassLoader(), new Class[]{cls}, new InvocationHandler() { // from class: com.eltechs.axs.activities.BufferedListenerInvoker.1
            @Override // java.lang.reflect.InvocationHandler
            public Object invoke(Object obj, Method method, Object[] objArr) throws Throwable {
                synchronized (BufferedListenerInvoker.this) {
                    if (BufferedListenerInvoker.this.realListener != null) {
                        return method.invoke(BufferedListenerInvoker.this.realListener, objArr);
                    }
                    BufferedListenerInvoker.this.enqueuedInvocations.add(new InvocationRequest(method, objArr));
                    return null;
                }
            }
        });
    }

    public ListenerType getProxy() {
        return this.proxy;
    }

    public synchronized void clearRealListener() {
        this.realListener = null;
    }

    public synchronized void setRealListener(ListenerType listenertype) {
        this.realListener = listenertype;
        if (listenertype == null) {
            return;
        }
        try {
            for (InvocationRequest invocationRequest : this.enqueuedInvocations) {
                invocationRequest.invoke(listenertype);
            }
        } catch (Exception e) {
            Assert.abort("Listener methods must throw no exceptions.", e);
        }
        this.enqueuedInvocations.clear();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class InvocationRequest {
        private final Object[] arguments;
        private final Method method;

        InvocationRequest(Method method, Object[] objArr) {
            this.method = method;
            this.arguments = objArr;
        }

        void invoke(Object obj) throws InvocationTargetException, IllegalAccessException {
            this.method.invoke(obj, this.arguments);
        }
    }
}