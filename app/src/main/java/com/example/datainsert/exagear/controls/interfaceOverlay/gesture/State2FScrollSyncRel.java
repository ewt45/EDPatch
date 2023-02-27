package com.example.datainsert.exagear.controls.interfaceOverlay.gesture;

import com.eltechs.axs.Finger;
import com.eltechs.axs.GestureStateMachine.AbstractGestureFSMState;
import com.eltechs.axs.GestureStateMachine.GestureContext;
import com.eltechs.axs.GestureStateMachine.GestureState1FingerMoveToScrollSync;
import com.eltechs.axs.GestureStateMachine.Helpers;
import com.eltechs.axs.GuestAppActionAdapters.ScrollAdapterMouseWheel;
import com.eltechs.axs.GuestAppActionAdapters.ScrollDirections;
import com.eltechs.axs.GuestAppActionAdapters.SyncScrollAdapter;
import com.eltechs.axs.MovementAccumulator;
import com.eltechs.axs.TouchEventAdapter;
import com.eltechs.axs.finiteStateMachine.FSMEvent;
import com.eltechs.axs.helpers.Assert;
import com.eltechs.axs.helpers.InfiniteTimer;
import com.eltechs.axs.widgets.viewOfXServer.TransformationHelpers;
import com.eltechs.axs.widgets.viewOfXServer.ViewOfXServer;

import java.util.List;

/* loaded from: classes.dex */
public class State2FScrollSyncRel extends AbstractGestureFSMState  {
    public static FSMEvent GESTURE_COMPLETED = new FSMEvent("GESTURE_COMPLETED");

    private final boolean breakIfFingerReleased;
    private final boolean doAdjustPointerPosition;
    private final long fingerLocationPollIntervalMs;
    private final float moveThresholdPixels;
    private final float movementUnitsInOnePixelX;
    private final float movementUnitsInOnePixelY;
    private final int pointerMargin;
    private final SyncScrollAdapter scrollAdapter;
    private MovementAccumulator movementX;
    private MovementAccumulator movementY;
    private Finger savedFinger;
    private InfiniteTimer timer;

    public State2FScrollSyncRel(
            GestureContext gestureContext, SyncScrollAdapter syncScrollAdapter,
            float movementUnitsInOnePixelX, float movementUnitsInOnePixelY, float moveThresholdPixels,
            boolean doAdjustPointerPosition, int pointerMargin, long fingerLocationPollIntervalMs, boolean breakIfFingerReleased) {
        super(gestureContext);
        this.scrollAdapter = syncScrollAdapter;
        this.movementUnitsInOnePixelX = movementUnitsInOnePixelX;
        this.movementUnitsInOnePixelY = movementUnitsInOnePixelY;
        this.moveThresholdPixels = moveThresholdPixels;
        this.doAdjustPointerPosition = doAdjustPointerPosition;
        this.pointerMargin = pointerMargin;
        this.fingerLocationPollIntervalMs = fingerLocationPollIntervalMs;
        this.breakIfFingerReleased = breakIfFingerReleased;
    }

    @Override // com.eltechs.axs.finiteStateMachine.FSMState
    public void notifyBecomeActive() {
        this.timer = new InfiniteTimer(this.fingerLocationPollIntervalMs) {
            @Override // android.os.CountDownTimer
            public void onTick(long j) {
                if (getContext().getMachine().isActiveState(State2FScrollSyncRel.this)) {
                    notifyTimer();
                }
            }
        };
        this.timer.start();
        Assert.isTrue(getContext().getFingers().size() == 2);
        //手指只用到第二根手指？还是两根都需要
        this.savedFinger = getContext().getFingers().get(1);
        this.movementX = new MovementAccumulator(this.movementUnitsInOnePixelX, 0.0f);
        this.movementY = new MovementAccumulator(this.movementUnitsInOnePixelY, 0.0f);
        this.movementX.reset(this.savedFinger.getXWhenFirstTouched());
        this.movementY.reset(this.savedFinger.getYWhenFirstTouched());
        if (this.doAdjustPointerPosition) {
            Helpers.adjustPointerPosition(getContext().getViewFacade(), this.pointerMargin);
        }
        this.scrollAdapter.notifyStart();
    }

    @Override // com.eltechs.axs.finiteStateMachine.FSMState
    public void notifyBecomeInactive() {
        this.scrollAdapter.notifyStop();
        this.timer.cancel();
    }

    private void scrollImpl(
            MovementAccumulator.Direction direction, MovementAccumulator.Direction direction2,
            boolean isXAccumulate, boolean isYAccumulate) {
        ScrollDirections.DirectionX directionX = ScrollDirections.DirectionX.NONE;
        ScrollDirections.DirectionY directionY = ScrollDirections.DirectionY.NONE;
        if (isXAccumulate) {
            switch (direction) {
                case ASC:
                    directionX = ScrollDirections.DirectionX.LEFT;
                    break;
                case DESC:
                    directionX = ScrollDirections.DirectionX.RIGHT;
                    break;
            }
        }
        if (isYAccumulate) {
            switch (direction2) {
                case ASC:
                    directionY = ScrollDirections.DirectionY.UP;
                    break;
                case DESC:
                    directionY = ScrollDirections.DirectionY.DOWN;
                    break;
            }
        }
        this.scrollAdapter.scroll(directionX, directionY, 1);
    }

    private void changeMovementUnits(MovementAccumulator movementAccumulator, float f) {
        float movementUnitsAccumulated = movementAccumulator.getMovementUnitsAccumulated() - 1.0f;
        if (movementUnitsAccumulated > 0.0f) {
            movementAccumulator.setMovementUnitsAccumulated(movementUnitsAccumulated);
        } else {
            movementAccumulator.stop(f);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    private void notifyTimer() {
        long currentTimeMillis = System.currentTimeMillis();

        this.movementX.processFingerMovement(false, this.savedFinger.getX(), currentTimeMillis);
        this.movementY.processFingerMovement(false, this.savedFinger.getY(), currentTimeMillis);
        MovementAccumulator.Direction directionX = this.movementX.getDirection();
        MovementAccumulator.Direction directionY = this.movementY.getDirection();

        boolean isXAccumulate = this.movementX.getDirection() != MovementAccumulator.Direction.NEUTRAL && this.movementX.getMovementUnitsAccumulated() >= 1.0f;
        boolean isYAccumulate = this.movementY.getDirection() != MovementAccumulator.Direction.NEUTRAL && this.movementY.getMovementUnitsAccumulated() >= 1.0f;

        scrollImpl(directionX, directionY, isXAccumulate, isYAccumulate);
        if (isXAccumulate) {
            changeMovementUnits(this.movementX, this.savedFinger.getX());
        }
        if (isYAccumulate) {
            changeMovementUnits(this.movementY, this.savedFinger.getY());
        }

        //返回是送一个手指还是两个都松？
        if (getContext().getFingers().isEmpty()) {
            if (!this.breakIfFingerReleased && (isXAccumulate || isYAccumulate)) {
                return;
            }
            sendEvent(GESTURE_COMPLETED);
        }
    }


    public static class SimpleBuilder{
        public State2FScrollSyncRel create(GestureContext gestureContext, ViewOfXServer viewOfXServer, long scrollPeriodMs ){
            return new State2FScrollSyncRel(
                    gestureContext,
                    new ScrollAdapterMouseWheel(gestureContext.getPointerReporter()),
                    0.05f * TransformationHelpers.getScaleX(viewOfXServer.getViewToXServerTransformationMatrix()),
                    0.05f * TransformationHelpers.getScaleY(viewOfXServer.getViewToXServerTransformationMatrix()),
                    0.0f, false, 0, scrollPeriodMs, true);
        }
    }
}
