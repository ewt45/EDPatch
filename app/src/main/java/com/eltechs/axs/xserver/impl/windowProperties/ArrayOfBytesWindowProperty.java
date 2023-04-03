package com.eltechs.axs.xserver.impl.windowProperties;

import com.eltechs.axs.xserver.Atom;
import com.eltechs.axs.xserver.WindowProperty;
import java.nio.charset.Charset;

/* loaded from: classes.dex */
public final class ArrayOfBytesWindowProperty extends MutableWindowProperty<byte[]> {
    private byte[] data;

    public ArrayOfBytesWindowProperty(Atom atom, byte[] bArr) {
        super(atom);
        this.data = bArr;
    }

    public ArrayOfBytesWindowProperty(Atom atom) {
        this(atom, new byte[0]);
    }

    @Override // com.eltechs.axs.xserver.WindowProperty
    public WindowProperty.Format<byte[]> getFormat() {
        return WindowProperty.ARRAY_OF_BYTES;
    }

    @Override // com.eltechs.axs.xserver.WindowProperty
    /* renamed from: getValues  reason: collision with other method in class */
    public byte[] getValues() {
        return this.data;
    }

    @Override // com.eltechs.axs.xserver.impl.windowProperties.MutableWindowProperty
    public void replaceValues(byte[] bArr) {
        this.data = bArr;
    }

    @Override // com.eltechs.axs.xserver.WindowProperty
    public int getSizeInBytes() {
        return this.data.length;
    }

    @Override // com.eltechs.axs.xserver.impl.windowProperties.MutableWindowProperty
    public void appendValues(byte[] bArr) {
        byte[] bArr2 = new byte[this.data.length + bArr.length];
        System.arraycopy(this.data, 0, bArr2, 0, this.data.length);
        System.arraycopy(bArr, 0, bArr2, this.data.length, bArr.length);
        this.data = bArr2;
    }

    @Override // com.eltechs.axs.xserver.impl.windowProperties.MutableWindowProperty
    public void prependValues(byte[] bArr) {
        byte[] bArr2 = new byte[this.data.length + bArr.length];
        System.arraycopy(this.data, 0, bArr2, bArr.length, this.data.length);
        System.arraycopy(bArr, 0, bArr2, 0, bArr.length);
        this.data = bArr2;
    }

    public String toString() {
        if ("STRING".equals(getType().getName())) {
            return new String(this.data, Charset.forName("latin1"));
        }
        return super.toString();
    }
}
