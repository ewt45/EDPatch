package com.eltechs.axs.GuestAppActionAdapters;

import com.eltechs.axs.KeyCodesX;
import com.eltechs.axs.KeyEventReporter;
import com.eltechs.axs.PointerEventReporter;

/* loaded from: classes.dex */
public class PressAndReleaseWithModifierKeyMouseClickAdapter implements MouseClickAdapter {
    private final int buttonCode;
    private final KeyEventReporter ker;
    private final KeyCodesX keyCode;
    private final PointerEventReporter per;
    private final int sleepMs;

    @Override // com.eltechs.axs.GuestAppActionAdapters.MouseClickAdapter
    public void finalizeClick() {
    }

    public PressAndReleaseWithModifierKeyMouseClickAdapter(PointerEventReporter pointerEventReporter, int i, int i2, KeyEventReporter keyEventReporter, KeyCodesX keyCodesX) {
        this.per = pointerEventReporter;
        this.buttonCode = i;
        this.sleepMs = i2;
        this.ker = keyEventReporter;
        this.keyCode = keyCodesX;
    }

    @Override // com.eltechs.axs.GuestAppActionAdapters.MouseClickAdapter
    public void click() {
        this.ker.reportKeysPress(this.keyCode);
        this.per.click(this.buttonCode, this.sleepMs);
        this.ker.reportKeysRelease(this.keyCode);
    }
}
