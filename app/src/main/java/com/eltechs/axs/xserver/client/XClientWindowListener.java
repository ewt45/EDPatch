package com.eltechs.axs.xserver.client;

import android.util.Log;

import com.eltechs.axs.xserver.EventName;
import com.eltechs.axs.xserver.Window;
import com.eltechs.axs.xserver.WindowListener;
import com.eltechs.axs.xserver.events.Event;
import com.eltechs.axs.xserver.impl.masks.Mask;

/* loaded from: classes.dex */
public class XClientWindowListener implements WindowListener {
    private static final String TAG ="XClientWindowListener";
    private final XClient client;
    private final Mask<EventName> eventMask;

    private boolean isInterestedIn(Class<? extends Event> cls) {
        return true;
    }

    public XClientWindowListener(XClient xClient, Mask<EventName> mask) {
        this.eventMask = mask;
        this.client = xClient;
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.eltechs.axs.xserver.WindowListener
    public void onEvent(Window window, Event event) {
        if (isInterestedIn(event.getClass())) {
            Log.d(TAG, String.format("onEvent: 窗口接收到事件：id=%d, class=%s, \t content=%s",event.getId(),Event.idToClassName(event.getId()),event));
            this.client.createEventSender().sendEvent(event);
        }
    }

    @Override // com.eltechs.axs.xserver.WindowListener
    public boolean isInterestedIn(EventName eventName) {
        return this.eventMask.isSet(eventName);
    }

    @Override // com.eltechs.axs.xserver.WindowListener
    public boolean isInterestedIn(Mask<EventName> mask) {
        return this.eventMask.intersects(mask);
    }

    @Override // com.eltechs.axs.xserver.WindowListener
    public Mask<EventName> getMask() {
        return this.eventMask;
    }

    public XClient getClient() {
        return this.client;
    }
}