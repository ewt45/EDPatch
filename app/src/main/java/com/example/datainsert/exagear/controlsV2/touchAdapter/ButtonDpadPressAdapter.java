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

        fingerFirstDown.x = isTouching ? mFinger.getXWhenFirstTouched() : centerX;
        fingerFirstDown.y = isTouching ? mFinger.getYWhenFirstTouched() : centerY;

        outerCenter.x = centerX;
        outerCenter.y = centerY;
    }
}
