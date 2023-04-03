package com.eltechs.axs.xserver;

import com.eltechs.axs.xserver.WindowProperty;

/* loaded from: classes.dex */
public interface WindowPropertiesManager {

    /* loaded from: classes.dex */
    public enum PropertyModification {
        REPLACE,
        PREPEND,
        APPEND
    }

    void deleteProperty(Atom atom);

    WindowProperty<?> getProperty(Atom atom);

    <T> WindowProperty<T> getProperty(Atom atom, WindowProperty.Format<T> format);

    <T> boolean modifyProperty(Atom atom, Atom atom2, WindowProperty.Format<T> format, PropertyModification propertyModification, T t);
}