package org.ewt45.customcontrols.toucharea;

public class Finger {
    private float x;
    private float xWhenFingerCountLastChanged;
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
