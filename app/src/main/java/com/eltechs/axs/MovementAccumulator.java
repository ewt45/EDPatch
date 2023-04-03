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

    public MovementAccumulator(float f, float f2) {
        this.moveDeltaPixels = f2;
        this.movementUnitsInOnePixel = f;
    }

    public void reset(float f) {
        this.direction = Direction.NEUTRAL;
        this.axis = f;
        this.movementUnitsAccumulated = 0.0f;
        this.movementStartTimestamp = 0L;
    }

    private void handlePointPositionChange(float f) {
        Assert.isTrue(this.direction != Direction.NEUTRAL);
        float abs = Math.abs(f - this.axis);
        this.axis = f;
        this.movementUnitsAccumulated += abs * this.movementUnitsInOnePixel;
    }

    private boolean movementStopNeeded(float f, boolean z) {
        return (z && f < 5.0f) || f > this.moveDeltaPixels;
    }

    public void processFingerMovement(boolean z, float f, long j) {
        float axis = f - getAxis();
        float abs = Math.abs(axis);
        switch (this.direction) {
            case NEUTRAL:
                if (abs > this.moveDeltaPixels) {
                    if (axis > 0.0f) {
                        start(Direction.ASC, f, j);
                        return;
                    } else {
                        start(Direction.DESC, f, j);
                        return;
                    }
                }
                return;
            case ASC:
                if (axis > 0.0f) {
                    handlePointPositionChange(f);
                    return;
                } else if (movementStopNeeded(abs, z)) {
                    stop(f);
                    return;
                } else {
                    return;
                }
            case DESC:
                if (axis < 0.0f) {
                    handlePointPositionChange(f);
                    return;
                } else if (movementStopNeeded(abs, z)) {
                    stop(f);
                    return;
                } else {
                    return;
                }
            default:
                Assert.unreachable();
                return;
        }
    }

    public void start(Direction direction, float f, long j) {
        this.direction = direction;
        Assert.state(this.direction != Direction.NEUTRAL, "Movement in neutral direction is not a movement at all");
        this.movementStartTimestamp = j;
        handlePointPositionChange(f);
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