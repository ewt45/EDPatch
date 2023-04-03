package com.eltechs.axs.xserver.impl;

import com.eltechs.axs.proto.input.errors.BadAccess;
import com.eltechs.axs.sysvipc.AttachedSHMSegment;
import com.eltechs.axs.sysvipc.SHMEngine;
import com.eltechs.axs.xserver.ShmSegment;
import java.nio.MappedByteBuffer;

/* loaded from: classes.dex */
public class ShmSegmentImpl implements ShmSegment {
    private final boolean isWritable;
    private final AttachedSHMSegment segment;
    private final SHMEngine shmEngine;
    private final int xid;

    public ShmSegmentImpl(SHMEngine sHMEngine, int i, int i2, boolean z) throws BadAccess {
        this.shmEngine = sHMEngine;
        this.xid = i;
        this.isWritable = z;
        this.segment = sHMEngine.attachSegment(i2, false);
        if (this.segment == null) {
            throw new BadAccess();
        }
    }

    @Override // com.eltechs.axs.xserver.ShmSegment
    public int getXid() {
        return this.xid;
    }

    @Override // com.eltechs.axs.xserver.ShmSegment
    public MappedByteBuffer getContent() {
        return this.segment.getContent();
    }

    @Override // com.eltechs.axs.xserver.ShmSegment
    public boolean isWritable() {
        return this.isWritable;
    }

    public void detach() {
        this.shmEngine.detachSHMSegment(this.segment);
    }
}
