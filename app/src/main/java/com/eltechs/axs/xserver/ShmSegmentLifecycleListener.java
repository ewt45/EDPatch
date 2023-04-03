package com.eltechs.axs.xserver;

/* loaded from: classes.dex */
public interface ShmSegmentLifecycleListener {
    void segmentAttached(ShmSegment shmSegment);

    void segmentDetached(ShmSegment shmSegment);
}