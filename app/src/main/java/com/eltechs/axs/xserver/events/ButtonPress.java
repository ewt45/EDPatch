package com.eltechs.axs.xserver.events;

import com.eltechs.axs.xserver.KeyButNames;
import com.eltechs.axs.xserver.Window;
import com.eltechs.axs.xserver.impl.masks.Mask;

/* loaded from: classes.dex */
public class ButtonPress extends InputDeviceEvent {
    public ButtonPress(byte btnCode, int timestamp, Window root, Window event, Window child, short rootX, short rootY, short eventX, short eventY, Mask<KeyButNames> mask) {
        super(4, btnCode, timestamp, root, event, child, rootX, rootY, eventX, eventY, mask);
    }
}
