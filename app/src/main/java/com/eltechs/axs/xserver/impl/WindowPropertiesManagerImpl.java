package com.eltechs.axs.xserver.impl;

import com.eltechs.axs.helpers.Assert;
import com.eltechs.axs.xserver.Atom;
import com.eltechs.axs.xserver.EventName;
import com.eltechs.axs.xserver.Window;
import com.eltechs.axs.xserver.WindowPropertiesManager;
import com.eltechs.axs.xserver.WindowProperty;
import com.eltechs.axs.xserver.events.PropertyNotify;
import com.eltechs.axs.xserver.impl.windowProperties.ArrayOfBytesWindowProperty;
import com.eltechs.axs.xserver.impl.windowProperties.ArrayOfIntsWindowProperty;
import com.eltechs.axs.xserver.impl.windowProperties.ArrayOfShortsWindowProperty;
import com.eltechs.axs.xserver.impl.windowProperties.MutableWindowProperty;
import java.util.HashMap;
import java.util.Map;

/* loaded from: classes.dex */
public class WindowPropertiesManagerImpl implements WindowPropertiesManager {
    private final Window host;
    private final Map<Atom, MutableWindowProperty<?>> properties = new HashMap<>();

    public WindowPropertiesManagerImpl(Window window) {
        this.host = window;
    }

    @Override // com.eltechs.axs.xserver.WindowPropertiesManager
    public WindowProperty<?> getProperty(Atom atom) {
        return this.properties.get(atom);
    }

    @Override // com.eltechs.axs.xserver.WindowPropertiesManager
    public <T> WindowProperty<T> getProperty(Atom atom, WindowProperty.Format<T> format) {
        MutableWindowProperty<T> mutableWindowProperty = (MutableWindowProperty<T>) this.properties.get(atom);
        if (mutableWindowProperty != null && mutableWindowProperty.getFormat() == format) {
            return  mutableWindowProperty;
        }
        return null;
    }

    @Override // com.eltechs.axs.xserver.WindowPropertiesManager
    public <T> boolean modifyProperty(Atom atom, Atom atom2, WindowProperty.Format<T> format, WindowPropertiesManager.PropertyModification propertyModification, T t) {
        if (doModifyProperty(atom, atom2, format, propertyModification, t)) {
            this.host.getEventListenersList().sendEventForEventName(new PropertyNotify(this.host, atom, (int) System.currentTimeMillis(), false), EventName.PROPERTY_CHANGE);
            return true;
        }
        return false;
    }

    public <T> boolean doModifyProperty(Atom atom, Atom atom2, WindowProperty.Format<T> format, WindowPropertiesManager.PropertyModification propertyModification, T t) {
        MutableWindowProperty<T> mutableWindowProperty = (MutableWindowProperty<T>) this.properties.get(atom);
        if (mutableWindowProperty == null) {
            this.properties.put(atom, createProperty(format, atom2, t));
            return true;
        } else if (propertyModification == WindowPropertiesManager.PropertyModification.REPLACE) {
            if (mutableWindowProperty.getFormat() == format) {
                mutableWindowProperty.replaceValues(t);
            } else {
                this.properties.put(atom, createProperty(format, atom2, t));
            }
            return true;
        } else if (mutableWindowProperty.getFormat() != format) {
            return false;
        } else {
            if (propertyModification == WindowPropertiesManager.PropertyModification.PREPEND) {
                mutableWindowProperty.prependValues(t);
            } else if (propertyModification == WindowPropertiesManager.PropertyModification.APPEND) {
                mutableWindowProperty.appendValues(t);
            } else {
                Assert.state(false, String.format("Unsupported PropertyModification %s.", propertyModification));
            }
            return true;
        }
    }

    @Override // com.eltechs.axs.xserver.WindowPropertiesManager
    public void deleteProperty(Atom atom) {
        if (this.properties.remove(atom) != null) {
            this.host.getEventListenersList().sendEventForEventName(new PropertyNotify(this.host, atom, (int) System.currentTimeMillis(), true), EventName.PROPERTY_CHANGE);
        }
    }

    private <T> MutableWindowProperty<T> createProperty(WindowProperty.Format<T> format, Atom atom, T t) {
        if (format == WindowProperty.ARRAY_OF_BYTES) {
            return (MutableWindowProperty<T>) new ArrayOfBytesWindowProperty(atom, (byte[]) t);
        }
        if (format == WindowProperty.ARRAY_OF_SHORTS) {
            return (MutableWindowProperty<T>) new ArrayOfShortsWindowProperty(atom, (short[]) t);
        }
        if (format == WindowProperty.ARRAY_OF_INTS) {
            return (MutableWindowProperty<T>) new ArrayOfIntsWindowProperty(atom, (int[]) t);
        }
        Assert.state(false, String.format("Invalid property format marker %s.", format));
        return null;
    }
}
