package com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.touchAdapter;

import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.Finger;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.TouchAdapter;

import java.util.List;

public class ClickAdapter implements TouchAdapter {
    float mMaxDistance;
    Runnable mCallback;
    public ClickAdapter(float maxDistance, Runnable callback){
        mMaxDistance  =maxDistance;
        mCallback = callback;
    }
    float xDown,yDown;
    @Override
    public void notifyMoved(Finger finger, List<Finger> list) {

    }

    @Override
    public void notifyReleased(Finger finger, List<Finger> list) {
        float dx = xDown-finger.getX();
        float dy = yDown -finger.getY();
        if((dx*dx+dy*dy)<mMaxDistance*mMaxDistance)
            mCallback.run();
    }

    @Override
    public void notifyTouched(Finger finger, List<Finger> list) {
        xDown = finger.getX();
        yDown = finger.getY();
    }
}