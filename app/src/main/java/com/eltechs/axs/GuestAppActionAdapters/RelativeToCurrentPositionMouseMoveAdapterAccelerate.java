package com.eltechs.axs.GuestAppActionAdapters;

import com.example.datainsert.exagear.controls.SensitivitySeekBar;

public class RelativeToCurrentPositionMouseMoveAdapterAccelerate implements MouseMoveAdapter{
    @Override
    public void moveTo(float f, float f2) {
        float f4 = SensitivitySeekBar.MOUSE_SENSITIVITY;
    }

    @Override
    public void prepareMoving(float f, float f2) {

    }
}
