package com.example.datainsert.exagear.controls.interfaceOverlay.GuestAppActionAdapters;

import com.eltechs.axs.GestureStateMachine.PointerContext;
import com.eltechs.axs.GuestAppActionAdapters.MouseClickAdapter;
import com.eltechs.axs.GuestAppActionAdapters.MouseMoveAdapter;
import com.eltechs.axs.GuestAppActionAdapters.MousePointAndClickAdapter;

/* loaded from: classes.dex */
public class RelativeMouseClickAdapter implements MousePointAndClickAdapter {
    private final MouseClickAdapter clicker;
    private final PointerContext pointerContext;

    public RelativeMouseClickAdapter( MouseClickAdapter mouseClickAdapter, PointerContext pointerContext) {
        this.clicker = mouseClickAdapter;
        this.pointerContext = pointerContext;

    }

    @Override // com.eltechs.axs.GuestAppActionAdapters.MousePointAndClickAdapter
    public void click(float f, float f2) {
        this.clicker.click();
    }

    @Override // com.eltechs.axs.GuestAppActionAdapters.MousePointAndClickAdapter
    public void finalizeClick(float f, float f2) {
        this.clicker.finalizeClick();
        this.pointerContext.setLastMoveMethod(PointerContext.MoveMethod.TAP);
    }
}
