package com.eltechs.axs.xserver.events;

import com.eltechs.axs.xserver.KeyButNames;
import com.eltechs.axs.xserver.Window;
import com.eltechs.axs.xserver.events.PointerWindowEvent;
import com.eltechs.axs.xserver.impl.masks.Mask;

/* loaded from: classes.dex */
public class EnterNotify extends PointerWindowEvent {
    public EnterNotify(PointerWindowEvent.Detail detail, PointerWindowEvent.Mode mode, int timestamp, Window root, Window event, Window child, short rootX, short rootY, short eventX, short eventY, Mask<KeyButNames> state, boolean z) {
        super(7, detail, mode, timestamp, root, event, child, rootX, rootY, eventX, eventY, state, z);
    }
}
