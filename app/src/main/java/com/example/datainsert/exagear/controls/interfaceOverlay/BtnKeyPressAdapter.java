package com.example.datainsert.exagear.controls.interfaceOverlay;

import android.util.Log;
import android.widget.Button;

import com.eltechs.axs.Finger;
import com.eltechs.axs.TouchEventAdapter;
import com.example.datainsert.exagear.controls.interfaceOverlay.widget.BaseMoveBtn;
import com.example.datainsert.exagear.controls.interfaceOverlay.widget.RegularKeyBtn;

import java.util.List;

public class BtnKeyPressAdapter implements TouchEventAdapter {

    private final BaseMoveBtn mBtn;
    String TAG="BtnKeyPressAdapter";
    public BtnKeyPressAdapter( BaseMoveBtn btn){
        mBtn=btn;
    }
    @Override
    public void notifyTouched(Finger finger, List<Finger> list) {
        Log.d(TAG, "notifyTouched: ");
        //如果已经按下了，就不再按下
        if(mBtn.isPressed())
            return;
        mBtn.setPressed(true);
        mBtn.injectPress(finger);
    }
    @Override
    public void notifyMoved(Finger finger, List<Finger> list) {
//        Log.d(TAG, "notifyMoved: ");
        if(!mBtn.isPressed())
            return;
        mBtn.injectMove(finger);
    }

    @Override
    public void notifyMovedIn(Finger finger, List<Finger> list) {
        notifyTouched(finger,list);
    }

    @Override
    public void notifyMovedOut(Finger finger, List<Finger> list) {
        notifyReleased(finger,list);
    }

    @Override
    public void notifyReleased(Finger finger, List<Finger> list) {
        Log.d(TAG, "notifyReleased: ");
        //如果没按下就不松开
        if(!mBtn.isPressed())
            return;
        //按钮取消press，注入按键松开
        mBtn.setPressed(false);
        mBtn.injectRelease(finger);

    }


}
