package com.eltechs.axs.proto.input.annotations.impl;

import com.eltechs.axs.helpers.Assert;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/* loaded from: classes.dex */
public class ParameterDescriptor {
    private final Annotation[] annotations;
    private final int index;
    private final Type type;

    private ParameterDescriptor(int i, Class<?> cls, Type type, Annotation[] annotationArr) {
        this.index = i;
        this.type = mapPrimitiveType(cls, type);
        this.annotations = annotationArr;
    }

    public static ParameterDescriptor[] getMethodParameters(Method method) {
        Class<?>[] parameterTypes = method.getParameterTypes();
        Type[] genericParameterTypes = method.getGenericParameterTypes();
        Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        int length = parameterTypes.length;
        ParameterDescriptor[] parameterDescriptorArr = new ParameterDescriptor[length];
        for (int i = 0; i < length; i++) {
            parameterDescriptorArr[i] = new ParameterDescriptor(i, parameterTypes[i], genericParameterTypes[i], parameterAnnotations[i]);
        }
        return parameterDescriptorArr;
    }

    public int getIndex() {
        return this.index;
    }

    public Class<?> getRawType() {
        if (this.type instanceof Class) {
            return (Class) this.type;
        }
        if (this.type instanceof ParameterizedType) {
            return (Class) ((ParameterizedType) this.type).getRawType();
        }
        Assert.notImplementedYet(String.format("Requests parser does not support handler parameters of type %s.", this.type));
        return null;
    }

    public Type getType() {
        return this.type;
    }

    public <T extends Annotation> T getAnnotation(Class<T> cls) {
        for (Annotation annotation : this.annotations) {
            T t = (T) annotation;
            if (t.annotationType() == cls) {
                return t;
            }
        }
        return null;
    }

    private Type mapPrimitiveType(Class<?> cls, Type type) {
        if (cls == Boolean.TYPE) {
            return Boolean.class;
        }
        if (cls == Byte.TYPE) {
            return Byte.class;
        }
        if (cls == Short.TYPE) {
            return Short.class;
        }
        if (cls == Integer.TYPE) {
            return Integer.class;
        }
        return cls == Long.TYPE ? Long.class : type;
    }
}
