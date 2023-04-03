package com.eltechs.axs.GuestAppActionAdapters;

import com.eltechs.axs.PointerEventReporter;

/* loaded from: classes.dex */
public class PressAndHoldMouseClickAdapter implements MouseClickAdapter {
    private final int buttonCode;
    private final PointerEventReporter per;

    public PressAndHoldMouseClickAdapter(PointerEventReporter pointerEventReporter, int i) {
        this.per = pointerEventReporter;
        this.buttonCode = i;
    }

    @Override // com.eltechs.axs.GuestAppActionAdapters.MouseClickAdapter
    public void click() {
        this.per.buttonPressed(this.buttonCode);
    }

    @Override // com.eltechs.axs.GuestAppActionAdapters.MouseClickAdapter
    public void finalizeClick() {
        this.per.buttonReleased(this.buttonCode);
    }
}
