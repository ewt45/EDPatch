package com.example.datainsert.exagear.controlsV2.touchAdapter;

import com.example.datainsert.exagear.controlsV2.model.OneDpad;

public class ButtonDpadPressAdapter extends ButtonStickPressAdapter{
    public ButtonDpadPressAdapter(OneDpad model) {
        super(model);
    }

    @Override
    protected void updateRealOuterCenterXYAndFingerDownXY(boolean isTouching) {
        float centerX = mModel.getLeft() + mModel.getSize() / 2f;
        float centerY = mModel.getTop() + mModel.getSize() / 2f;

        fingerFirstDownX = isTouching ? mFinger.getXWhenFirstTouched() : centerX;
        fingerFirstDownY = isTouching ? mFinger.getYWhenFirstTouched() : centerY;

        outerCenterX = centerX;
        outerCenterY = centerY;
    }
}
