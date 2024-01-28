package com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.touchAdapter;

import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.TouchAdapter;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.model.OneDpad;

public class ButtonDpadPressAdapter extends ButtonStickPressAdapter{
    public ButtonDpadPressAdapter(OneDpad model, TouchAdapter adapter) {
        super(model,adapter);
    }

    @Override
    protected void updateRealCenterXY(boolean isTouching) {
        startCenterX = mModel.getLeft()+mModel.getSize()/2f;
        startCenterY = mModel.getTop() + mModel.getSize()/2f;
    }
}
