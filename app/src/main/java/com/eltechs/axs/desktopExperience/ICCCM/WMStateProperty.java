package com.eltechs.axs.desktopExperience.ICCCM;

import com.eltechs.axs.xserver.Window;

/* loaded from: classes.dex */
public class WMStateProperty {
    public final Window iconWindow;
    public final WMStateValues state;

    public WMStateProperty(WMStateValues wMStateValues, Window window) {
        this.state = wMStateValues;
        this.iconWindow = window;
    }
}