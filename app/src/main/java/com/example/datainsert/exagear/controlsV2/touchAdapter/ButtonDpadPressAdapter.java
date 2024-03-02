package com.example.datainsert.exagear.controlsV2.touchAdapter;

import com.example.datainsert.exagear.controlsV2.model.OneDpad;

public class ButtonDpadPressAdapter extends ButtonStickPressAdapter{
    public ButtonDpadPressAdapter(OneDpad model) {
        super(model);
    }

    @Override
    protected void updateRealCenterXY(boolean isTouching) {
        startCenterX = mModel.getLeft()+mModel.getSize()/2f;
        startCenterY = mModel.getTop() + mModel.getSize()/2f;
    }
}
