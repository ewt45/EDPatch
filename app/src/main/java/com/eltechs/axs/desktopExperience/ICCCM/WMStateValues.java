package com.eltechs.axs.desktopExperience.ICCCM;

/* loaded from: classes.dex */
public enum WMStateValues {
    WITHDRAWN(0),
    NORMAL(1),
    ICONIC(3);

    private final int id;

    WMStateValues(int id) {
        this.id = id;
    }

    public int value() {
        return this.id;
    }
}