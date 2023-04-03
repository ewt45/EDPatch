package com.eltechs.axs.xserver.impl.windowProperties;

import com.eltechs.axs.xserver.Atom;
import com.eltechs.axs.xserver.WindowProperty;

/* loaded from: classes.dex */
public final class ArrayOfIntsWindowProperty extends MutableWindowProperty<int[]> {
    private int[] data;

    public ArrayOfIntsWindowProperty(Atom atom, int[] iArr) {
        super(atom);
        this.data = iArr;
    }

    public ArrayOfIntsWindowProperty(Atom atom) {
        this(atom, new int[0]);
    }

    @Override // com.eltechs.axs.xserver.WindowProperty
    public WindowProperty.Format<int[]> getFormat() {
        return WindowProperty.ARRAY_OF_INTS;
    }

    @Override // com.eltechs.axs.xserver.WindowProperty
    /* renamed from: getValues  reason: collision with other method in class */
    public int[] getValues() {
        return this.data;
    }

    @Override // com.eltechs.axs.xserver.impl.windowProperties.MutableWindowProperty
    public void replaceValues(int[] iArr) {
        this.data = iArr;
    }

    @Override // com.eltechs.axs.xserver.WindowProperty
    public int getSizeInBytes() {
        return this.data.length * 4;
    }

    @Override // com.eltechs.axs.xserver.impl.windowProperties.MutableWindowProperty
    public void appendValues(int[] iArr) {
        int[] iArr2 = new int[this.data.length + iArr.length];
        System.arraycopy(this.data, 0, iArr2, 0, this.data.length);
        System.arraycopy(iArr, 0, iArr2, this.data.length, iArr.length);
        this.data = iArr2;
    }

    @Override // com.eltechs.axs.xserver.impl.windowProperties.MutableWindowProperty
    public void prependValues(int[] iArr) {
        int[] iArr2 = new int[this.data.length + iArr.length];
        System.arraycopy(this.data, 0, iArr2, iArr.length, this.data.length);
        System.arraycopy(iArr, 0, iArr2, 0, iArr.length);
        this.data = iArr2;
    }
}
