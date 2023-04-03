package com.eltechs.axs.xserver.events;

import com.eltechs.axs.xserver.KeyButNames;
import com.eltechs.axs.xserver.Window;
import com.eltechs.axs.xserver.impl.masks.Mask;

/* loaded from: classes.dex */
public class MotionNotify extends InputDeviceEvent {
    public MotionNotify(boolean z, int timestamp, Window rootWindow, Window eventWindow, Window childWindow, short rootX, short rootY, short eventX, short eventY, Mask<KeyButNames> mask) {
        super(6, z ? (byte) 1 : (byte) 0, timestamp, rootWindow, eventWindow, childWindow, rootX, rootY, eventX, eventY, mask);
    }
}
