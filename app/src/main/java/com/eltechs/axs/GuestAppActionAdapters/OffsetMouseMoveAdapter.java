package com.eltechs.axs.GuestAppActionAdapters;

/* loaded from: classes.dex */
public class OffsetMouseMoveAdapter implements MouseMoveAdapter {
    private final MouseMoveAdapter moveAdapter;
    private final float offsetX;
    private final float offsetY;

    public OffsetMouseMoveAdapter(MouseMoveAdapter mouseMoveAdapter, float offX, float offY) {
        this.moveAdapter = mouseMoveAdapter;
        this.offsetX = offX;
        this.offsetY = offY;
    }

    @Override // com.eltechs.axs.GuestAppActionAdapters.MouseMoveAdapter
    public void moveTo(float x, float y) {
        this.moveAdapter.moveTo(x + this.offsetX, y + this.offsetY);
    }

    @Override // com.eltechs.axs.GuestAppActionAdapters.MouseMoveAdapter
    public void prepareMoving(float f, float f2) {
        moveTo(f, f2);
    }
}
