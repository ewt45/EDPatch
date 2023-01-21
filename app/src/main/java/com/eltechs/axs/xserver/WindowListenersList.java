package com.eltechs.axs.xserver;

import com.eltechs.axs.xserver.events.Event;
import com.eltechs.axs.xserver.impl.masks.Mask;
import com.example.datainsert.exagear.QH;

import java.util.ArrayList;
import java.util.Collection;

public class WindowListenersList {
    private final Window host;
    private final Collection<WindowListener> listeners = new ArrayList<>();
    public WindowListenersList(Window window) {
        this.host = window;
    }

    public void addListener(WindowListener windowListener) {
        this.listeners.add(windowListener);
    }

    public void removeListener(WindowListener windowListener) {
        this.listeners.remove(windowListener);
    }
    public void sendEvent(Event event) {
        for (WindowListener windowListener : this.listeners) {
            windowListener.onEvent(this.host, event);
        }
    }

    public void sendEventForEventName(Event event, EventName eventName) {
        for (WindowListener windowListener : this.listeners) {
            if (windowListener.isInterestedIn(eventName)) {
                windowListener.onEvent(this.host, event);
            }
        }
    }


    public void sendEventForEventMask(Event event, Mask<EventName> mask) {
        String str = "sendEventForEventMask: " +
                "event:class=" + event.getClass() + ", id=" + event.getId() +
                ". mask:class=" + mask.getClass() + ", toString=" + mask.toString();
        QH.logD(str);
        for (WindowListener windowListener : this.listeners) {
            if (windowListener.isInterestedIn(mask)) {
                QH.logD("windowListener"+windowListener+" is interested in this mask");
                windowListener.onEvent(this.host, event);
            }
        }
    }

}
