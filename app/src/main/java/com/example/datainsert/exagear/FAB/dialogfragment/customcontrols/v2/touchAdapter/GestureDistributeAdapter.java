package com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.touchAdapter;

import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.Finger;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.TouchAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * 相当于TouchEventMultiplexor
 */
public class GestureDistributeAdapter implements TouchAdapter {
    float[] latestFingerXY = new float[2];

    private final ArrayList<TouchAdapter> listeners = new ArrayList<>();

    public void addListener(TouchAdapter touchEventAdapter) {
        this.listeners.add(touchEventAdapter);
    }

    public void removeListener(TouchAdapter touchEventAdapter) {
        this.listeners.remove(touchEventAdapter);
    }

    @Override
    public void notifyMoved(Finger finger, List<Finger> list) {
        for (TouchAdapter touchEventAdapter : this.listeners) {
            touchEventAdapter.notifyMoved(finger, list);
        }
        updateLatestFingerPos(finger);
    }

    @Override
    public void notifyReleased(Finger finger, List<Finger> list) {
        for (TouchAdapter touchEventAdapter : this.listeners) {
            touchEventAdapter.notifyReleased(finger, list);
        }
        updateLatestFingerPos(finger);
    }

    @Override
    public void notifyTouched(Finger finger, List<Finger> list) {
        for (TouchAdapter touchEventAdapter : this.listeners) {
            touchEventAdapter.notifyTouched(finger, list);
        }
        updateLatestFingerPos(finger);
    }

    /**
     * 手指变化时，更新手指最新位置。用于日后没有手指处于按下状态但还要获取手指坐标的时候
     */
    private void updateLatestFingerPos(Finger finger){
        latestFingerXY[0] = finger.getX();
        latestFingerXY[1] = finger.getY();
    }

    /**
     * 用于没有手指处于按下状态但还要获取手指坐标的时候
     */
    public float[] getLatestFingerXY(){
        return latestFingerXY;
    }

}
