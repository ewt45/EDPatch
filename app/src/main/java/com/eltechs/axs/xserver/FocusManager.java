package com.eltechs.axs.xserver;

/* loaded from: classes.dex */
public interface FocusManager {

    /* loaded from: classes.dex */
    public enum FocusReversionPolicy {
        NONE,
        POINTER_ROOT,
        PARENT
    }

    void addFocusListner(FocusListener focusListener);

    FocusReversionPolicy getFocusReversionPolicy();

    Window getFocusedWindow();

    void removeFocusListener(FocusListener focusListener);

    void setFocus(Window window, FocusReversionPolicy focusReversionPolicy);
}
