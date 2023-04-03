package com.eltechs.axs.xserver.impl;

import com.eltechs.axs.proto.input.errors.BadAccess;
import com.eltechs.axs.sysvipc.SHMEngine;
import com.eltechs.axs.xserver.ShmSegment;
import com.eltechs.axs.xserver.ShmSegmentLifecycleListener;
import com.eltechs.axs.xserver.ShmSegmentLifecycleListenerList;
import com.eltechs.axs.xserver.ShmSegmentsManager;
import java.util.HashMap;
import java.util.Map;

/* loaded from: classes.dex */
public class ShmSegmentsManagerImpl implements ShmSegmentsManager {
    private final SHMEngine shmEngine;
    private final Map<Integer, ShmSegment> segments = new HashMap();
    private final ShmSegmentLifecycleListenerList shmSegmentLifecycleListenerList = new ShmSegmentLifecycleListenerList();

    public ShmSegmentsManagerImpl(SHMEngine sHMEngine) {
        this.shmEngine = sHMEngine;
    }

    public SHMEngine getShmEngine() {
        return this.shmEngine;
    }

    @Override // com.eltechs.axs.xserver.ShmSegmentsManager
    public ShmSegment attachSegment(int i, int i2, boolean z) throws BadAccess {
        if (this.segments.containsKey(Integer.valueOf(i))) {
            detachSegment(this.segments.get(Integer.valueOf(i)));
        }
        ShmSegmentImpl shmSegmentImpl = new ShmSegmentImpl(this.shmEngine, i, i2, z);
        this.segments.put(Integer.valueOf(i), shmSegmentImpl);
        this.shmSegmentLifecycleListenerList.sendSegmentAttached(shmSegmentImpl);
        return shmSegmentImpl;
    }

    @Override // com.eltechs.axs.xserver.ShmSegmentsManager
    public void detachSegment(ShmSegment shmSegment) {
        ((ShmSegmentImpl) shmSegment).detach();
        this.segments.remove(Integer.valueOf(shmSegment.getXid()));
        this.shmSegmentLifecycleListenerList.sendSegmentDetached(shmSegment);
    }

    @Override // com.eltechs.axs.xserver.ShmSegmentsManager
    public ShmSegment getSegment(int i) {
        return this.segments.get(Integer.valueOf(i));
    }

    @Override // com.eltechs.axs.xserver.ShmSegmentsManager
    public void addShmSegmentLifecycleListener(ShmSegmentLifecycleListener shmSegmentLifecycleListener) {
        this.shmSegmentLifecycleListenerList.addListener(shmSegmentLifecycleListener);
    }

    @Override // com.eltechs.axs.xserver.ShmSegmentsManager
    public void removeShmSegmentLifecycleListener(ShmSegmentLifecycleListener shmSegmentLifecycleListener) {
        this.shmSegmentLifecycleListenerList.removeListener(shmSegmentLifecycleListener);
    }
}
