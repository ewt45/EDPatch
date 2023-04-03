package com.eltechs.axs.desktopExperience;

import com.eltechs.axs.desktopExperience.ICCCM.WMStateProperty;
import com.eltechs.axs.helpers.Assert;
import com.eltechs.axs.xserver.Atom;
import com.eltechs.axs.xserver.Window;
import com.eltechs.axs.xserver.WindowPropertiesManager;
import com.eltechs.axs.xserver.WindowProperty;
import com.eltechs.axs.xserver.XServer;

/* loaded from: classes.dex */
public class ICCCMHelpers {
    public static void setWMState(XServer xServer, Window window, WMStateProperty wMStateProperty) {
        Atom atom = xServer.getAtomsManager().getAtom("WM_STATE");
        Assert.state(atom != null, "Atom WM_STATE must be predefined");
        int[] iArr = new int[2];
        iArr[0] = wMStateProperty.state.value();
        iArr[1] = wMStateProperty.iconWindow != null ? wMStateProperty.iconWindow.getId() : 0;
        window.getPropertiesManager().modifyProperty(atom, atom, WindowProperty.ARRAY_OF_INTS, WindowPropertiesManager.PropertyModification.REPLACE, iArr);
    }
}