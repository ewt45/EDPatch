package com.example.datainsert.exagear.controlsV2.touchAdapter;

import com.example.datainsert.exagear.controlsV2.model.OneDpad;

public class ButtonDpadPressAdapter extends ButtonStickPressAdapter{
    public ButtonDpadPressAdapter(OneDpad model) {
        super(model);
    }

    @Override
    protected void updateRealOuterCenterXYAndFingerDownXY(boolean isTouching) {
        fingerFirstDownX = mModel.getLeft()+mModel.getSize()/2f;
        fingerFirstDownY = mModel.getTop() + mModel.getSize()/2f;
    }
}
