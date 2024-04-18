package com.example.datainsert.exagear.controlsV2.touchAdapter;

import com.example.datainsert.exagear.controlsV2.Const;
import com.example.datainsert.exagear.controlsV2.Finger;
import com.example.datainsert.exagear.controlsV2.TouchAdapter;

import java.util.List;

public class ButtonPressAdapter implements TouchAdapter {
    private final List<Integer> mKeys;
    boolean isTouched=false;
    public ButtonPressAdapter(List<Integer> keys){
        mKeys = keys;
    }

    @Override
    public void notifyMoved(Finger finger, List<Finger> list) {

    }

    @Override
    public void notifyReleased(Finger finger, List<Finger> list) {
        Const.getXServerHolder().releaseKeyOrPointer(mKeys);
        isTouched=false;
    }

    @Override
    public void notifyTouched(Finger finger, List<Finger> list) {
        if (isTouched)
            return;

        isTouched=true;
        Const.getXServerHolder().pressKeyOrPointer(mKeys);
    }
}
