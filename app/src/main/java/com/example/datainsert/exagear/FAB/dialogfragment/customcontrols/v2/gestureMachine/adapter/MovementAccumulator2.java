package com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.gestureMachine.adapter;

import com.eltechs.axs.helpers.Assert;

public class MovementAccumulator2 {
    private final float movementUnitsInOnePixel; //一个像素对应几个单位距离
    private float posInAxis;
    private Direction direction;
    private final float movedMinPixels; //小于此距离的移动认为是不动
    private long movementStartTimestamp;
    private float movementUnitsAccumulated; //本次时间间隔内移动了几个单位距离

    public MovementAccumulator2(float movementUnitsInOnePixel, float movedMinPixels) {
        this.movedMinPixels = movedMinPixels;
        this.movementUnitsInOnePixel = movementUnitsInOnePixel;
    }

    public void reset(float f) {
        this.direction = Direction.NEUTRAL;
        this.posInAxis = f;
        this.movementUnitsAccumulated = 0.0f;
        this.movementStartTimestamp = 0L;
    }

    private void handlePointPositionChange(float newPos) {
        Assert.isTrue(this.direction != Direction.NEUTRAL);
        float abs = Math.abs(newPos - this.posInAxis);
        this.posInAxis = newPos;
        this.movementUnitsAccumulated += abs * this.movementUnitsInOnePixel;
    }

    private boolean movementStopNeeded(float absDiff, boolean z) {
        return (z && absDiff < 5.0f) || absDiff > this.movedMinPixels;
    }

    /**
     * 计算本次时间间隔内手指的移动情况
     *
     * @param z
     * @param newPos
     * @param currentTime
     */
    public void processFingerMovement(boolean z, float newPos, long currentTime) {
        float diff = newPos - posInAxis;
        float absDiff = Math.abs(diff);
        switch (this.direction) {
            case NEUTRAL:
                if (absDiff > this.movedMinPixels)
                    start(diff > 0.0f ? Direction.ASC : Direction.DESC, newPos, currentTime);
                return;
            case ASC:
                if (diff > 0.0f)
                    handlePointPositionChange(newPos);
                else if (movementStopNeeded(absDiff, z))
                    stop(newPos);

                return;
            case DESC:
                if (diff < 0.0f)
                    handlePointPositionChange(newPos);
                else if (movementStopNeeded(absDiff, z))
                    stop(newPos);
                return;
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

    public void setPosInAxis(float f) {
        this.posInAxis = f;
    }

    public Direction getDirection() {
        return this.direction;
    }

    public long getMovementStartTimestamp() {
        return this.movementStartTimestamp;
    }

    /* loaded from: classes.dex */
    public enum Direction {
        DESC,
        NEUTRAL,
        ASC
    }
}
