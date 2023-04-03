package com.eltechs.axs.xserver;

import java.util.ArrayList;
import java.util.Collection;

/* loaded from: classes.dex */
public class CursorLifecycleListenerList {
    private final Collection<CursorLifecycleListener> listeners = new ArrayList();

    public void addListener(CursorLifecycleListener cursorLifecycleListener) {
        this.listeners.add(cursorLifecycleListener);
    }

    public void removeListener(CursorLifecycleListener cursorLifecycleListener) {
        this.listeners.remove(cursorLifecycleListener);
    }

    public void sendCursorCreated(Cursor cursor) {
        for (CursorLifecycleListener cursorLifecycleListener : this.listeners) {
            cursorLifecycleListener.cursorCreated(cursor);
        }
    }

    public void sendCursorFreed(Cursor cursor) {
        for (CursorLifecycleListener cursorLifecycleListener : this.listeners) {
            cursorLifecycleListener.cursorFreed(cursor);
        }
    }
}
