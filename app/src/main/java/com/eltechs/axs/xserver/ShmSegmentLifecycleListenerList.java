package com.eltechs.axs.xserver;

import java.util.ArrayList;
import java.util.Collection;

/* loaded from: classes.dex */
public class ShmSegmentLifecycleListenerList {
    private final Collection<ShmSegmentLifecycleListener> listeners = new ArrayList();

    public void addListener(ShmSegmentLifecycleListener shmSegmentLifecycleListener) {
        this.listeners.add(shmSegmentLifecycleListener);
    }

    public void removeListener(ShmSegmentLifecycleListener shmSegmentLifecycleListener) {
        this.listeners.remove(shmSegmentLifecycleListener);
    }

    public void sendSegmentAttached(ShmSegment shmSegment) {
        for (ShmSegmentLifecycleListener shmSegmentLifecycleListener : this.listeners) {
            shmSegmentLifecycleListener.segmentAttached(shmSegment);
        }
    }

    public void sendSegmentDetached(ShmSegment shmSegment) {
        for (ShmSegmentLifecycleListener shmSegmentLifecycleListener : this.listeners) {
            shmSegmentLifecycleListener.segmentDetached(shmSegment);
        }
    }
}
