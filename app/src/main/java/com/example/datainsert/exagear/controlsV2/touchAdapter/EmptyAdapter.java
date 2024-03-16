package com.example.datainsert.exagear.controlsV2.touchAdapter;

import com.example.datainsert.exagear.controlsV2.Finger;
import com.example.datainsert.exagear.controlsV2.TouchAdapter;

import java.util.List;

public class EmptyAdapter implements TouchAdapter {
    @Override
    public void notifyMoved(Finger finger, List<Finger> list) {

    }

    @Override
    public void notifyReleased(Finger finger, List<Finger> list) {

    }

    @Override
    public void notifyTouched(Finger finger, List<Finger> list) {

    }
}
