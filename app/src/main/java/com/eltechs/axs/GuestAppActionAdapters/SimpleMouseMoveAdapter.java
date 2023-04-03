package com.eltechs.axs.GuestAppActionAdapters;

import com.eltechs.axs.PointerEventReporter;

/* loaded from: classes.dex */
public class SimpleMouseMoveAdapter implements MouseMoveAdapter {
    private final PointerEventReporter per;

    @Override // com.eltechs.axs.GuestAppActionAdapters.MouseMoveAdapter
    public void prepareMoving(float f, float f2) {
    }

    public SimpleMouseMoveAdapter(PointerEventReporter pointerEventReporter) {
        this.per = pointerEventReporter;
    }

    @Override // com.eltechs.axs.GuestAppActionAdapters.MouseMoveAdapter
    public void moveTo(float f, float f2) {
        this.per.pointerMove(f, f2);
    }
}
