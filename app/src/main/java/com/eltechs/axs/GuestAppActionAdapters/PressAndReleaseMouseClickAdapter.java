package com.eltechs.axs.GuestAppActionAdapters;

import com.eltechs.axs.PointerEventReporter;

/* loaded from: classes.dex */
public class PressAndReleaseMouseClickAdapter implements MouseClickAdapter {
    private final int buttonCode;
    private final PointerEventReporter per;
    private final int sleepMs;

    @Override // com.eltechs.axs.GuestAppActionAdapters.MouseClickAdapter
    public void finalizeClick() {
    }

    public PressAndReleaseMouseClickAdapter(PointerEventReporter pointerEventReporter, int code, int sleepMs) {
        this.per = pointerEventReporter;
        this.buttonCode = code;
        this.sleepMs = sleepMs;
    }

    @Override // com.eltechs.axs.GuestAppActionAdapters.MouseClickAdapter
    public void click() {
        this.per.click(this.buttonCode, this.sleepMs);
    }
}
