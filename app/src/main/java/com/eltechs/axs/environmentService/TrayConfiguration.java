package com.eltechs.axs.environmentService;

/* loaded from: classes.dex */
public class TrayConfiguration {
    private final int returnToDescription;
    private final int trayIcon;
    private final int trayIconName;

    public TrayConfiguration(int icon, int iconName, int returnToDescription) {
        this.trayIcon = icon;
        this.trayIconName = iconName;
        this.returnToDescription = returnToDescription;
    }

    public int getTrayIcon() {
        return this.trayIcon;
    }

    public int getTrayIconName() {
        return this.trayIconName;
    }

    public int getReturnToDescription() {
        return this.returnToDescription;
    }
}