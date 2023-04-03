package com.eltechs.axs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/* loaded from: classes.dex */
public class TouchEventMultiplexor implements TouchEventAdapter {
    private final Collection<TouchEventAdapter> listeners = new ArrayList<>();

    public void addListener(TouchEventAdapter touchEventAdapter) {
        this.listeners.add(touchEventAdapter);
    }

    public void removeListener(TouchEventAdapter touchEventAdapter) {
        this.listeners.remove(touchEventAdapter);
    }

    @Override // com.eltechs.axs.TouchEventAdapter
    public void notifyTouched(Finger finger, List<Finger> list) {
        for (TouchEventAdapter touchEventAdapter : this.listeners) {
            touchEventAdapter.notifyTouched(finger, list);
        }
    }

    @Override // com.eltechs.axs.TouchEventAdapter
    public void notifyReleased(Finger finger, List<Finger> list) {
        for (TouchEventAdapter touchEventAdapter : this.listeners) {
            touchEventAdapter.notifyReleased(finger, list);
        }
    }

    @Override // com.eltechs.axs.TouchEventAdapter
    public void notifyMoved(Finger finger, List<Finger> list) {
        for (TouchEventAdapter touchEventAdapter : this.listeners) {
            touchEventAdapter.notifyMoved(finger, list);
        }
    }

    @Override // com.eltechs.axs.TouchEventAdapter
    public void notifyMovedIn(Finger finger, List<Finger> list) {
        for (TouchEventAdapter touchEventAdapter : this.listeners) {
            touchEventAdapter.notifyMovedIn(finger, list);
        }
    }

    @Override // com.eltechs.axs.TouchEventAdapter
    public void notifyMovedOut(Finger finger, List<Finger> list) {
        for (TouchEventAdapter touchEventAdapter : this.listeners) {
            touchEventAdapter.notifyMovedOut(finger, list);
        }
    }
}