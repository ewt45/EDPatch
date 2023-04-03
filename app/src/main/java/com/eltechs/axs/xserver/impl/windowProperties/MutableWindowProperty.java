package com.eltechs.axs.xserver.impl.windowProperties;

import com.eltechs.axs.xserver.Atom;
import com.eltechs.axs.xserver.WindowProperty;

/* loaded from: classes.dex */
public abstract class MutableWindowProperty<T> implements WindowProperty<T> {
    private final Atom type;

    public abstract void appendValues(T t);

    public abstract void prependValues(T t);

    public abstract void replaceValues(T t);

    /* JADX INFO: Access modifiers changed from: protected */
    public MutableWindowProperty(Atom atom) {
        this.type = atom;
    }

    @Override // com.eltechs.axs.xserver.WindowProperty
    public Atom getType() {
        return this.type;
    }
}
