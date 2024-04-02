package com.eltechs.axs.container.impl;

import com.eltechs.axs.container.annotations.PostAdd;
import com.eltechs.axs.container.annotations.PreRemove;
import com.eltechs.axs.helpers.Assert;
import com.eltechs.axs.helpers.ReflectionHelpers;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public abstract class LifecycleHandlersScanner {
    private LifecycleHandlersScanner() {
    }

    public static List<LifecycleHandlerMethod> listPostAddActions(Class<?> cls) {
        final ArrayList<LifecycleHandlerMethod> arrayList = new ArrayList<>();
        try {
            ReflectionHelpers.doWithMethods(cls, method -> {
                boolean z = method.getParameterTypes().length != 0;
                Assert.isFalse(ReflectionHelpers.isStatic(method), String.format("The method %s is marked with @PostAdd annotation and must be a member method.", method));
                Assert.isFalse(z, String.format("The method %s is marked with @PostAdd annotation and must have no parameters.", method));
                method.setAccessible(true);
                arrayList.add(new LifecycleHandlerMethod(method));
            }, ReflectionHelpers.Filters.instanceMethodsBearingAnnotation(PostAdd.class));
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            Assert.unreachable();
        }
        arrayList.trimToSize();
        return arrayList;
    }

    public static List<LifecycleHandlerMethod> listPreRemoveActions(Class<?> cls) {
        final ArrayList<LifecycleHandlerMethod> arrayList = new ArrayList<>();
        try {
            ReflectionHelpers.doWithMethods(cls, method -> {
                boolean z = method.getParameterTypes().length != 0;
                Assert.isFalse(ReflectionHelpers.isStatic(method), String.format("The method %s is marked with @PreRemove annotation and must be a member method.", method));
                Assert.isFalse(z, String.format("The method %s is marked with @PreRemove annotation and must have no parameters.", method));
                method.setAccessible(true);
                arrayList.add(new LifecycleHandlerMethod(method));
            }, ReflectionHelpers.Filters.instanceMethodsBearingAnnotation(PreRemove.class));
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            Assert.unreachable();
        }
        arrayList.trimToSize();
        return arrayList;
    }
}