package com.eltechs.axs;

import com.eltechs.axs.helpers.Assert;

/* loaded from: classes.dex */
public class MovementAccumulator {
    private float axis;
    private Direction direction;
    private float moveDeltaPixels;
    private long movementStartTimestamp;
    private float movementUnitsAccumulated;
    private final float movementUnitsInOnePixel;

    /* loaded from: classes.dex */
    public enum Direction {
        DESC,
        NEUTRAL,
        ASC
    }

    public MovementAccumulator(float movementUnitsInOnePixel, float moveDeltaPixels) {
        this.movementUnitsInOnePixel = movementUnitsInOnePixel;
        this.moveDeltaPixels = moveDeltaPixels;
    }

    public void reset(float fingerAxisPos) {
        this.direction = Direction.NEUTRAL;
        this.axis = fingerAxisPos;
        this.movementUnitsAccumulated = 0.0f;
        this.movementStartTimestamp = 0L;
    }

    private void handlePointPositionChange(float currFingerAxisPos) {
        Assert.isTrue(this.direction != Direction.NEUTRAL);
        float abs = Math.abs(currFingerAxisPos - this.axis);
        this.axis = currFingerAxisPos;
        this.movementUnitsAccumulated += abs * this.movementUnitsInOnePixel;
    }

    private boolean movementStopNeeded(float f, boolean z) {
        return (z && f < 5.0f) || f > this.moveDeltaPixels;
    }

    public void processFingerMovement(boolean z, float nowFingerAxisPos, long currTime) {
        float axis = nowFingerAxisPos - getAxis();
        float abs = Math.abs(axis);
        switch (this.direction) {
            case NEUTRAL:
                if (abs > this.moveDeltaPixels)
                    start(axis > 0.0f ? Direction.ASC : Direction.DESC, nowFingerAxisPos, currTime);
                return;
            case ASC:
                if (axis > 0.0f) {
                    handlePointPositionChange(nowFingerAxisPos);
                } else if (movementStopNeeded(abs, z)) {
                    stop(nowFingerAxisPos);
                }
                return;
            case DESC:
                if (axis < 0.0f) {
                    handlePointPositionChange(nowFingerAxisPos);
                } else if (movementStopNeeded(abs, z)) {
                    stop(nowFingerAxisPos);
                }
                return;
            default:
                Assert.unreachable();
                return;
        }
    }

    public void start(Direction direction, float currFingerAxisPos, long currTIme) {
        this.direction = direction;
        Assert.state(this.direction != Direction.NEUTRAL, "Movement in neutral direction is not a movement at all");
        this.movementStartTimestamp = currTIme;
        handlePointPositionChange(currFingerAxisPos);
    }

    public void stop(float f) {
        Assert.isTrue(this.direction != Direction.NEUTRAL);
        reset(f);
    }

    public float getMovementUnitsAccumulated() {
        return this.movementUnitsAccumulated;
    }

    public void setMovementUnitsAccumulated(float f) {
        this.movementUnitsAccumulated = f;
    }

    public float getAxis() {
        return this.axis;
    }

    public void setAxis(float f) {
        this.axis = f;
    }

    public Direction getDirection() {
        return this.direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public long getMovementStartTimestamp() {
        return this.movementStartTimestamp;
    }
}