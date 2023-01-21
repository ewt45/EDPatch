package com.eltechs.axs.configuration;

public class TouchScreenControlsInputConfiguration {
    public static TouchScreenControlsInputConfiguration DEFAULT = new TouchScreenControlsInputConfiguration(BackKeyAction.XKEYCODE);
    public final BackKeyAction backKeyAction;

    /* loaded from: classes.dex */
    public enum BackKeyAction {
        XKEYCODE,
        SHOW_POPUP_MENU
    }

    public TouchScreenControlsInputConfiguration(BackKeyAction backKeyAction) {
        this.backKeyAction = backKeyAction;
    }
}
