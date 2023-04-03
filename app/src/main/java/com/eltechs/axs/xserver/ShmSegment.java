package com.eltechs.axs.xserver;

import java.nio.MappedByteBuffer;

/* loaded from: classes.dex */
public interface ShmSegment {
    MappedByteBuffer getContent();

    int getXid();

    boolean isWritable();
}