package com.eltechs.axs.xconnectors;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/* loaded from: classes.dex */
public interface XInputStream {
    void get(byte[] bArr);

    ByteBuffer getAsByteBuffer(int i);

    int getAvailableBytesCount();

    byte getByte();

    int getInt();

    short getShort();

    void setByteOrder(ByteOrder byteOrder);

    void skip(int i);
}