package com.eltechs.axs.sysvipc;

import java.nio.MappedByteBuffer;

/* loaded from: classes.dex */
public class AttachedSHMSegmentImpl implements AttachedSHMSegment {
    MappedByteBuffer content;
    long size;

    /* JADX INFO: Access modifiers changed from: package-private */
    public AttachedSHMSegmentImpl(MappedByteBuffer mappedByteBuffer, long j) {
        this.content = mappedByteBuffer;
        this.size = j;
    }

    @Override // com.eltechs.axs.sysvipc.AttachedSHMSegment
    public MappedByteBuffer getContent() {
        return this.content;
    }
}