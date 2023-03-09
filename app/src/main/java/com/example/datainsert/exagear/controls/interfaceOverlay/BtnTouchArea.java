package com.example.datainsert.exagear.controls.interfaceOverlay;

import android.graphics.PointF;
import android.util.Log;

import com.eltechs.axs.Finger;
import com.eltechs.axs.GeometryHelpers;
import com.eltechs.axs.TouchArea;
import com.eltechs.axs.TouchEventAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BtnTouchArea {
    String TAG="BtnTouchArea";
    private  float bottomX;
    private  float bottomY;
    private  float topX;
    private  float topY;
    private  float radius;
    private final TouchEventAdapter adapter;

    private final boolean isRound;
    private final List<Finger> activeFingers = new ArrayList<>();
    private final List<Finger> immutableActiveFingers = Collections.unmodifiableList(this.activeFingers);

    /**
     * 矩形边界用的构造函数
     */
    public BtnTouchArea(float topX, float topY, float width, float height, TouchEventAdapter touchEventAdapter) {
        this.topX = topX;
        this.topY = topY;
        this.bottomX = topX+width;
        this.bottomY = topY+height;
        this.adapter = touchEventAdapter;
        radius=0;
       isRound=false;
    }
    /**
     * 圆形边界用的构造函数
     */
    public BtnTouchArea(float topX, float topY, float radius,TouchEventAdapter touchEventAdapter){
        this.topX = topX;
        this.topY = topY;
        this.bottomX = topX+radius;
        this.bottomY = topY+radius;
        this.radius=radius;
        this.adapter = touchEventAdapter;
        isRound=true;
    }

    public void updateArea(int left,int top,int right, int bottom){
        topX=left;
        topY=top;
        bottomX=right;
        bottomY=bottom;
    }

    public boolean handleBtnFingerDown(Finger finger) {

        if(!isInside(finger))
            return false;
        Log.d(TAG, String.format("handleBtnFingerDown: 在toucharea范围内吗%f,%f,%f,%f",topX,topY,bottomX,bottomY));

        //如果finger已经失效，就清空数组。（这样如果按键卡住了，再次按下的时候就可以恢复）
        if(activeFingers.size()==0){
            addFinger(finger);
            this.adapter.notifyTouched(finger, this.immutableActiveFingers);
        }
        return true;
    }

    public boolean handleBtnFingerUp(Finger finger) {
        if ( this.activeFingers.contains(finger)) {
            removeFinger(finger);
            this.adapter.notifyReleased(finger, this.immutableActiveFingers);
        }
        return isInside(finger);

    }

    /**
     * 该区域是否处理了该手指的操作。
     * 普通按钮返回isInside，摇杆按钮若该手指在down时在范围内则move时始终返回true
     * @param finger
     * @return
     */
    public boolean handleBtnFingerMove(Finger finger) {
//        Log.d(TAG, "handleBtnFingerMove: ");
        //摇杆的话不判断在不在自身范围内，而是判断是否是按下时的那根手指
        if(isRound ){
            if(activeFingers.contains(finger)){
                this.adapter.notifyMoved(finger, this.immutableActiveFingers);
            }
            return isInside(finger);
        }
        //按钮的话正常判断
        if (isInside(finger)) {
            if (this.activeFingers.contains(finger)) {
                this.adapter.notifyMoved(finger, this.immutableActiveFingers);
            }else if(activeFingers.size()==0){
                addFinger(finger);
                this.adapter.notifyMovedIn(finger, this.immutableActiveFingers);
            }
        } else if (this.activeFingers.contains(finger)) {
            removeFinger(finger);
            this.adapter.notifyMovedOut(finger, this.immutableActiveFingers);
        }
        return isInside(finger);
    }

    /**
     * 判断是否
     * @param finger
     * @return
     */
    public boolean isInside(Finger finger) {
        float x = finger.getX();
        float y = finger.getY();
        if(!isRound)
            return x > this.topX && x < this.bottomX && y > this.topY && y < this.bottomY;
        else
            return GeometryHelpers.distance(new PointF(x,y),new PointF(topX+radius,topY+radius))<radius;
    }

    private void removeFinger(Finger finger) {
        for (Finger finger2 : this.activeFingers) {
            finger2.notifyFingersCountChanged();
        }
        this.activeFingers.remove(finger);
    }

    private void addFinger(Finger finger) {
        this.activeFingers.add(finger);
        for (Finger finger2 : this.activeFingers) {
            finger2.notifyFingersCountChanged();
        }
    }
}
