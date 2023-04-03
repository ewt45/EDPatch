package com.eltechs.axs.GuestAppActionAdapters;

import com.eltechs.axs.GestureStateMachine.PointerContext;

/* loaded from: classes.dex */
public class SimpleMousePointAndClickAdapter implements MousePointAndClickAdapter {
    private final MouseClickAdapter clicker;
    private final MouseMoveAdapter mover;
    private final PointerContext pointerContext;

    public SimpleMousePointAndClickAdapter(MouseMoveAdapter mouseMoveAdapter, MouseClickAdapter mouseClickAdapter, PointerContext pointerContext) {
        this.clicker = mouseClickAdapter;
        this.pointerContext = pointerContext;
        this.mover = mouseMoveAdapter;
    }

    @Override // com.eltechs.axs.GuestAppActionAdapters.MousePointAndClickAdapter
    public void click(float f, float f2) {
        this.mover.moveTo(f, f2);
        try {
            Thread.sleep(20L, 0);
        } catch (InterruptedException unused) {
        }
        this.clicker.click();
    }

    @Override // com.eltechs.axs.GuestAppActionAdapters.MousePointAndClickAdapter
    public void finalizeClick(float f, float f2) {
        this.clicker.finalizeClick();
        this.pointerContext.setLastMoveMethod(PointerContext.MoveMethod.TAP);
    }
}
