package com.eltechs.axs.GuestAppActionAdapters;

import android.util.Log;

public class OffsetMouseMoveAdapter implements MouseMoveAdapter{
    private final MouseMoveAdapter moveAdapter;
    private final float offsetX;
    private final float offsetY;

    public OffsetMouseMoveAdapter(MouseMoveAdapter mouseMoveAdapter, float f, float f2) {
        Log.d("OffsetMouseMoveAdapter", String.format("新建了一个adapter，类型：%s, f1:%f, f2:%f",mouseMoveAdapter,f,f2));
        this.moveAdapter = mouseMoveAdapter;
        this.offsetX = f;
        this.offsetY = f2;
    }

    @Override // com.eltechs.axs.GuestAppActionAdapters.MouseMoveAdapter
    public void moveTo(float f, float f2) {
        this.moveAdapter.moveTo(f*1.0f + this.offsetX, f2*1.0f + this.offsetY);
//        this.moveAdapter.moveTo(f + this.offsetX, f2*1.7f + this.offsetY);
    }

    @Override // com.eltechs.axs.GuestAppActionAdapters.MouseMoveAdapter
    public void prepareMoving(float f, float f2) {
        this.moveAdapter.moveTo(f + this.offsetX, f2 + this.offsetY);
    }

}
