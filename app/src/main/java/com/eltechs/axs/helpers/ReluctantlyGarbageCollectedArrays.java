package com.eltechs.axs.helpers;

import java.lang.ref.SoftReference;

/* loaded from: classes.dex */
public class ReluctantlyGarbageCollectedArrays {
    private SoftReference<byte[]> byteArray = null;
    private SoftReference<short[]> shortArray = null;
    private SoftReference<int[]> intArray = null;

    public byte[] getByteArray(int i) {
        byte[] bArr = this.byteArray != null ? this.byteArray.get() : null;
        if (bArr == null || bArr.length < i) {
            byte[] bArr2 = new byte[i];
            this.byteArray = new SoftReference<>(bArr2);
            return bArr2;
        }
        return bArr;
    }

    public short[] getShortArray(int i) {
        short[] sArr = this.shortArray != null ? this.shortArray.get() : null;
        if (sArr == null || sArr.length < i) {
            short[] sArr2 = new short[i];
            this.shortArray = new SoftReference<>(sArr2);
            return sArr2;
        }
        return sArr;
    }

    public int[] getIntArray(int i) {
        int[] iArr = this.intArray != null ? this.intArray.get() : null;
        if (iArr == null || iArr.length < i) {
            int[] iArr2 = new int[i];
            this.intArray = new SoftReference<>(iArr2);
            return iArr2;
        }
        return iArr;
    }
}