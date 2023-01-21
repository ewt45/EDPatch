package com.eltechs.axs.applicationState;

public class ApplicationStateObject<T> {
//    private final Container container;
    private final T state;

    public ApplicationStateObject(Class<T> cls) {
//        AutowiringEnhancedObject addAutowiring = AutowiringEnhancedObject.addAutowiring(cls);
//        this.state = (T) addAutowiring.getProxy();
//        this.container = addAutowiring.getContainer();
        this.state=null;
    }

    public T getState() {
        return this.state;
    }

//    public void clear() {
//        this.container.removeAllComponents();
//    }
}
