package com.eltechs.axs.xserver.impl.windowProperties;

import com.eltechs.axs.xserver.Atom;
import com.eltechs.axs.xserver.WindowProperty;

/* loaded from: classes.dex */
public final class ArrayOfShortsWindowProperty extends MutableWindowProperty<short[]> {
    private short[] data;

    public ArrayOfShortsWindowProperty(Atom atom, short[] sArr) {
        super(atom);
        this.data = sArr;
    }

    public ArrayOfShortsWindowProperty(Atom atom) {
        this(atom, new short[0]);
    }

    @Override // com.eltechs.axs.xserver.WindowProperty
    public WindowProperty.Format<short[]> getFormat() {
        return WindowProperty.ARRAY_OF_SHORTS;
    }

    @Override // com.eltechs.axs.xserver.WindowProperty
    /* renamed from: getValues  reason: collision with other method in class */
    public short[] getValues() {
        return this.data;
    }

    @Override // com.eltechs.axs.xserver.impl.windowProperties.MutableWindowProperty
    public void replaceValues(short[] sArr) {
        this.data = sArr;
    }

    @Override // com.eltechs.axs.xserver.WindowProperty
    public int getSizeInBytes() {
        return this.data.length * 2;
    }

    @Override // com.eltechs.axs.xserver.impl.windowProperties.MutableWindowProperty
    public void appendValues(short[] sArr) {
        short[] sArr2 = new short[this.data.length + sArr.length];
        System.arraycopy(this.data, 0, sArr2, 0, this.data.length);
        System.arraycopy(sArr, 0, sArr2, this.data.length, sArr.length);
        this.data = sArr2;
    }

    @Override // com.eltechs.axs.xserver.impl.windowProperties.MutableWindowProperty
    public void prependValues(short[] sArr) {
        short[] sArr2 = new short[this.data.length + sArr.length];
        System.arraycopy(this.data, 0, sArr2, sArr.length, this.data.length);
        System.arraycopy(sArr, 0, sArr2, 0, sArr.length);
        this.data = sArr2;
    }
}
