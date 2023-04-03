package com.eltechs.axs.container;

import com.eltechs.axs.container.impl.AutowiredPropertiesScanner;
import com.eltechs.axs.container.impl.AutowiringRequest;
import com.eltechs.axs.container.impl.LifecycleHandlerMethod;
import com.eltechs.axs.container.impl.LifecycleHandlersScanner;
import com.eltechs.axs.helpers.Assert;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/* loaded from: classes.dex */
public class Container {
    private boolean iterating;
    private final Thread mainThread = Thread.currentThread();
    private final Map<String, Object> components = Collections.synchronizedMap(new LinkedHashMap());
    private final Map<Class, List<LifecycleHandlerMethod>> postAddActions = new HashMap();
    private final Map<Class, List<LifecycleHandlerMethod>> preRemoveActions = new HashMap();
    private final Map<Class, List<AutowiringRequest>> autowiringInformation = new HashMap();

    public void addComponent(String str, Object obj) {
        Assert.state(Thread.currentThread() == this.mainThread);
        Assert.isFalse(this.components.containsKey(str), String.format("The component '%s' is already present in the container.", str));
        Assert.state(!this.iterating);
        this.components.put(str, obj);
        updateNewComponentConfiguration(obj);
        callPostAdd(obj);
        updateExistingComponentsConfigurationUponAddition(obj);
    }

    public void removeComponent(String str) {
        Assert.state(Thread.currentThread() == this.mainThread);
        Object remove = this.components.remove(str);
        Assert.notNull(remove, String.format("The component '%s' is not present in the container.", str));
        Assert.state(!this.iterating);
        callPreRemove(remove);
        updateExistingComponentsConfigurationUponRemoval(remove);
    }

    public void setComponent(String str, Object obj) {
        Assert.state(Thread.currentThread() == this.mainThread);
        Assert.state(!this.iterating);
        if (this.components.containsKey(str)) {
            removeComponent(str);
        }
        if (obj != null) {
            addComponent(str, obj);
        }
    }

    public void removeAllComponents() {
        Assert.state(Thread.currentThread() == this.mainThread);
        LinkedList linkedList = new LinkedList();
        for (Map.Entry<String, Object> entry : this.components.entrySet()) {
            linkedList.addFirst(entry.getKey());
        }
        Iterator it = linkedList.iterator();
        while (it.hasNext()) {
            removeComponent((String) it.next());
        }
    }

    public Object getComponent(String str) {
        return this.components.get(str);
    }

    private void updateNewComponentConfiguration(Object obj) {
        Assert.state(!this.iterating);
        this.iterating = true;
        for (Map.Entry<String, Object> entry : this.components.entrySet()) {
            Object value = entry.getValue();
            callSetter(obj, value.getClass(), value);
        }
        Assert.state(this.iterating);
        this.iterating = false;
    }

    private void updateExistingComponentsConfigurationUponAddition(Object obj) {
        Assert.state(!this.iterating);
        this.iterating = true;
        for (Map.Entry<String, Object> entry : this.components.entrySet()) {
            callSetter(entry.getValue(), obj.getClass(), obj);
        }
        Assert.state(this.iterating);
        this.iterating = false;
    }

    private void updateExistingComponentsConfigurationUponRemoval(Object obj) {
        Assert.state(!this.iterating);
        this.iterating = true;
        for (Map.Entry<String, Object> entry : this.components.entrySet()) {
            callSetter(entry.getValue(), obj.getClass(), null);
        }
        Assert.state(this.iterating);
        this.iterating = false;
    }

    private void callPostAdd(Object obj) {
        for (LifecycleHandlerMethod lifecycleHandlerMethod : getPostAddActions(obj)) {
            lifecycleHandlerMethod.apply(obj);
        }
    }

    private void callPreRemove(Object obj) {
        for (LifecycleHandlerMethod lifecycleHandlerMethod : getPreRemoveActions(obj)) {
            lifecycleHandlerMethod.apply(obj);
        }
    }

    private List<LifecycleHandlerMethod> getPostAddActions(Object obj) {
        Class<?> cls = obj.getClass();
        List<LifecycleHandlerMethod> list = this.postAddActions.get(cls);
        if (list == null) {
            List<LifecycleHandlerMethod> listPostAddActions = LifecycleHandlersScanner.listPostAddActions(cls);
            this.postAddActions.put(cls, listPostAddActions);
            return listPostAddActions;
        }
        return list;
    }

    private List<LifecycleHandlerMethod> getPreRemoveActions(Object obj) {
        Class<?> cls = obj.getClass();
        List<LifecycleHandlerMethod> list = this.preRemoveActions.get(cls);
        if (list == null) {
            List<LifecycleHandlerMethod> listPreRemoveActions = LifecycleHandlersScanner.listPreRemoveActions(cls);
            this.preRemoveActions.put(cls, listPreRemoveActions);
            return listPreRemoveActions;
        }
        return list;
    }

    private void callSetter(Object obj, Class<?> cls, Object obj2) {
        for (AutowiringRequest autowiringRequest : getAutowiringRequestsForComponent(obj)) {
            if (autowiringRequest.isInterestedIn(cls)) {
                autowiringRequest.inject(obj, obj2);
                return;
            }
        }
    }

    private List<AutowiringRequest> getAutowiringRequestsForComponent(Object obj) {
        Class<?> cls = obj.getClass();
        List<AutowiringRequest> list = this.autowiringInformation.get(cls);
        if (list == null) {
            List<AutowiringRequest> listAutowiringRequests = AutowiredPropertiesScanner.listAutowiringRequests(cls);
            this.autowiringInformation.put(cls, listAutowiringRequests);
            return listAutowiringRequests;
        }
        return list;
    }
}