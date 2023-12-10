package org.ewt45.customcontrols.toucharea.impl;

import android.graphics.Canvas;
import android.graphics.drawable.Drawable;

import org.ewt45.customcontrols.model.OneButton;
import org.ewt45.customcontrols.toucharea.Finger;
import org.ewt45.customcontrols.toucharea.TouchAdapter;
import org.ewt45.customcontrols.toucharea.TouchArea;
import org.ewt45.customcontrols.toucharea.TouchAreaView;

import java.util.List;

public class TouchAreaButton extends TouchArea<OneButton> {

    private void showEditDialog() {

    }

    public TouchAreaButton(TouchAreaView host,OneButton model){
        super(host,model,TouchArea.DEFAULT_ADAPTER);
        //TODO 设置mAdapter
    }


    @Override
    public void onDraw(Canvas canvas) {

    }

    @Override
    public int handleFingerDown(Finger finger) {
        return 0;
    }

    @Override
    public int handleFingerMove(Finger finger) {
        return 0;
    }

    @Override
    public int handleFingerUp(Finger finger) {
        return 0;
    }
}
