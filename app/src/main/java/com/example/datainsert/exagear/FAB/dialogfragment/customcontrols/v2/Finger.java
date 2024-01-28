package com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2;

public class Finger {
    private float x;
    private float xWhenFingerCountLastChanged; //好像理解了，比如新手指按下后，需要观测旧手指的移动，但是想忽略旧手指之前移动的距离
    private final float xWhenFirstTouched;
    private float y;
    private float yWhenFingerCountLastChanged;
    private final float yWhenFirstTouched;

    public Finger(float x, float y) {
        this.x = x;
        this.xWhenFirstTouched = x;
        this.y = y;
        this.yWhenFirstTouched = y;
    }

    public void update(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void release(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void notifyFingersCountChanged() {
        this.xWhenFingerCountLastChanged = this.x;
        this.yWhenFingerCountLastChanged = this.y;
    }

    public float getX() {
        return this.x;
    }

    public float getY() {
        return this.y;
    }

    public float getXWhenFirstTouched() {
        return this.xWhenFirstTouched;
    }

    public float getYWhenFirstTouched() {
        return this.yWhenFirstTouched;
    }

    public float getXWhenFingerCountLastChanged() {
        return this.xWhenFingerCountLastChanged;
    }

    public float getYWhenFingerCountLastChanged() {
        return this.yWhenFingerCountLastChanged;
    }
}
