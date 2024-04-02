package com.eltechs.axs.container;

import com.eltechs.axs.helpers.Assert;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/* loaded from: classes.dex */
public class AutowiringEnhancedObject<T> {
    private final Container container;
    private final T proxy;

    private AutowiringEnhancedObject(T t, Container container) {
        this.proxy = t;
        this.container = container;
    }

    public T getProxy() {
        return this.proxy;
    }

    public Container getContainer() {
        return this.container;
    }

    public static <T> AutowiringEnhancedObject<T> addAutowiring(Class<T> cls) {
        final Container container = new Container();
        return new AutowiringEnhancedObject<>((T) Proxy.newProxyInstance(cls.getClassLoader(), new Class[]{cls}, new InvocationHandler() { // from class: com.eltechs.axs.container.AutowiringEnhancedObject.1
            @Override // java.lang.reflect.InvocationHandler
            public Object invoke(Object obj, Method method, Object[] objArr) throws Throwable {
                String propertyNameOfSetter = getPropertyNameOfSetter(method.getName());
                String propertyNameOfGetter = getPropertyNameOfGetter(method.getName());
                if (propertyNameOfSetter != null) {
                    container.setComponent(propertyNameOfSetter, objArr[0]);
                    return null;
                } else if (propertyNameOfGetter != null) {
                    return container.getComponent(propertyNameOfGetter);
                } else {
                    Assert.unreachable();
                    return null;
                }
            }
        }), container);
    }

    /* JADX INFO: Access modifiers changed from: private */
    private static String getPropertyNameOfSetter(String str) {
        if (str.startsWith("set")) {
            return str.substring(3);
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    private static String getPropertyNameOfGetter(String str) {
        if (str.startsWith("get")) {
            return str.substring(3);
        }
        if (str.startsWith("is")) {
            return str.substring(2);
        }
        return null;
    }
}