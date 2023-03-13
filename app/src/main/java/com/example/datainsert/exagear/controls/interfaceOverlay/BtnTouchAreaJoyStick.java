package com.example.datainsert.exagear.controls.interfaceOverlay;

import android.graphics.PointF;

import com.eltechs.axs.Finger;
import com.eltechs.axs.GeometryHelpers;
import com.eltechs.axs.TouchEventAdapter;

/**
 * 和BtnTouchArea类似，但是是给摇杆按钮准备的
 */
public class BtnTouchAreaJoyStick extends BtnTouchArea{

    private float radius;
    public BtnTouchAreaJoyStick(float topX, float topY, float radius, TouchEventAdapter touchEventAdapter) {
        super(topX, topY, radius*2,radius*2, touchEventAdapter);
        this.radius = radius;

    }

    @Override
    public boolean handleBtnFingerMove(Finger finger) {
        //摇杆的话不判断在不在自身范围内，而是判断是否是按下时的那根手指

        if(activeFinger == finger){
            this.adapter.notifyMoved(finger, this.immutableActiveFingers);
            return true;
        }
        return false;
    }

    @Override
    public boolean isInside(Finger finger) {
        float x = finger.getX();
        float y = finger.getY();
        return GeometryHelpers.distance(x,y,topX+radius,topY+radius)<radius;

    }
}
