package com.example.datainsert.exagear.controls.axs;

import com.eltechs.axs.Finger;
import com.eltechs.axs.TouchEventAdapter;

import java.util.List;

/**
 * 用于普通按钮的TouchArea。摇杆按钮请用BtnTouchAreaJoyStick
 */
public class BtnTouchArea {
    protected final TouchEventAdapter adapter;
    //    private final List<Finger> activeFingers = new ArrayList<>();
    protected final List<Finger> immutableActiveFingers = null;//Collections.unmodifiableList(this.activeFingers);
    protected float topX;
    protected float topY;
    //按钮区域只能有一个活跃的手指吧. 然后那个immutableActiveFingers直接传null
    protected Finger activeFinger;
    String TAG = "BtnTouchArea";
    private float bottomX;
    private float bottomY;
    private float radius;

    /**
     * 矩形边界用的构造函数
     */
    public BtnTouchArea(float topX, float topY, float width, float height, TouchEventAdapter touchEventAdapter) {
        this.topX = topX;
        this.topY = topY;
        this.bottomX = topX + width;
        this.bottomY = topY + height;
        this.adapter = touchEventAdapter;
    }



    public void updateArea(int left, int top, int right, int bottom) {
        topX = left;
        topY = top;
        bottomX = right;
        bottomY = bottom;
    }

    public boolean handleBtnFingerDown(Finger finger) {

        if (!isInside(finger))
            return false;
//        Log.d(TAG, String.format("handleBtnFingerDown: 在toucharea范围内吗%f,%f,%f,%f", topX, topY, bottomX, bottomY));

        //如果finger已经失效，就清空数组。（这样如果按键卡住了，再次按下的时候就可以恢复）
        if (activeFinger == null) {
            addFinger(finger);
            this.adapter.notifyTouched(finger, this.immutableActiveFingers);
        }
        return true;
    }

    public boolean handleBtnFingerUp(Finger finger) {
        if (this.activeFinger == finger) {
            removeFinger(finger);
            this.adapter.notifyReleased(finger, this.immutableActiveFingers);
        }
        return isInside(finger);

    }

    /**
     * 该区域是否处理了该手指的操作。
     * 普通按钮返回true代表消耗该事件，摇杆按钮若该手指在down时在范围内则move时始终返回true
     *
     * @param finger
     * @return
     */
    public boolean handleBtnFingerMove(Finger finger) {
//        Log.d(TAG, "handleBtnFingerMove: ");
        //摇杆的话不判断在不在自身范围内，而是判断是否是按下时的那根手指.  按钮的话正常判断
        if (isInside(finger)) {
            if (activeFinger == finger) {
                this.adapter.notifyMoved(finger, this.immutableActiveFingers);
            } else if (activeFinger == null) {
                addFinger(finger);
                this.adapter.notifyMovedIn(finger, this.immutableActiveFingers);
            }
        } else if (activeFinger == finger) {
            removeFinger(finger);
            this.adapter.notifyMovedOut(finger, this.immutableActiveFingers);
        } else
            return false; //完全跟自己没关系，返回false
        return true; //如果进了其他几个if，说明消耗了，返回true
    }

    /**
     * 判断是否
     *
     * @param finger
     * @return
     */
    public boolean isInside(Finger finger) {
        float x = finger.getX();
        float y = finger.getY();

        return x > this.topX && x < this.bottomX && y > this.topY && y < this.bottomY;

    }

    private void removeFinger(Finger finger) {
        this.activeFinger = null;
//
//        for (Finger finger2 : this.activeFingers) {
//            finger2.notifyFingersCountChanged();
//        }
//        this.activeFingers.remove(finger);
    }

    private void addFinger(Finger finger) {
        this.activeFinger = finger;
//        this.activeFingers.add(finger);
//        for (Finger finger2 : this.activeFingers) {
//            finger2.notifyFingersCountChanged();
//        }
    }
}
