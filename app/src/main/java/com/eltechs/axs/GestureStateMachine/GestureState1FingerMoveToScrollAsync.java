package com.eltechs.axs.GestureStateMachine;

import com.eltechs.axs.Finger;
import com.eltechs.axs.GuestAppActionAdapters.AsyncScrollAdapter;
import com.eltechs.axs.GuestAppActionAdapters.ScrollDirections;
import com.eltechs.axs.MovementAccumulator;
import com.eltechs.axs.finiteStateMachine.FSMEvent;
import com.eltechs.axs.helpers.Assert;
import com.eltechs.axs.helpers.InfiniteTimer;

/* loaded from: classes.dex */
public class GestureState1FingerMoveToScrollAsync extends AbstractGestureFSMState {
    public static FSMEvent GESTURE_COMPLETED = new FSMEvent();
    private static final long timerPeriodMs = 40;
    private final boolean cancelIfFingerReleased;
    private final boolean doAdjustPointerPosition;
    private final float moveThresholdPixels;
    private final float movementUnitsInOnePixelX;
    private final float movementUnitsInOnePixelY;
    private MovementAccumulator movementX;
    private MovementAccumulator movementY;
    private final int pointerMargin;
    private Finger savedFinger;
    private final AsyncScrollAdapter scrollAdapter;
    private InfiniteTimer timer;

    public GestureState1FingerMoveToScrollAsync(GestureContext gestureContext, AsyncScrollAdapter scrollAdapter, float movementUnitsInOnePixelX, float movementUnitsInOnePixelY, float moveThresholdPixels, boolean doAdjustPointerPosition, int pointerMargin, boolean cancelIfFingerReleased) {
        super(gestureContext);
        this.scrollAdapter = scrollAdapter;
        this.movementUnitsInOnePixelX = movementUnitsInOnePixelX;
        this.movementUnitsInOnePixelY = movementUnitsInOnePixelY;
        this.moveThresholdPixels = moveThresholdPixels;
        this.doAdjustPointerPosition = doAdjustPointerPosition;
        this.pointerMargin = pointerMargin;
        this.cancelIfFingerReleased = cancelIfFingerReleased;
    }

    @Override // com.eltechs.axs.finiteStateMachine.FSMState
    public void notifyBecomeActive() {
        this.timer = new InfiniteTimer(timerPeriodMs) { // from class: com.eltechs.axs.GestureStateMachine.GestureState1FingerMoveToScrollAsync.2
            @Override // android.os.CountDownTimer
            public void onTick(long j) {
                if (getContext().getMachine().isActiveState(GestureState1FingerMoveToScrollAsync.this)) {
                    notifyTimer();
                }
            }
        };
        this.timer.start();
        Assert.isTrue(getContext().getFingers().size() == 1);
        this.savedFinger = getContext().getFingers().get(0);
        this.movementX = new MovementAccumulator(this.movementUnitsInOnePixelX, this.moveThresholdPixels);
        this.movementY = new MovementAccumulator(this.movementUnitsInOnePixelY, this.moveThresholdPixels);
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

    private void scrollImpl(MovementAccumulator.Direction directX, MovementAccumulator.Direction directY, boolean scrollX, boolean scrollY) {
        ScrollDirections.DirectionX directionX = ScrollDirections.DirectionX.NONE;
        ScrollDirections.DirectionY directionY = ScrollDirections.DirectionY.NONE;
        if (scrollX) {
            switch (directX) {
                case ASC:
                    directionX = ScrollDirections.DirectionX.LEFT;
                    break;
                case DESC:
                    directionX = ScrollDirections.DirectionX.RIGHT;
                    break;
            }
        }
        if (scrollY) {
            switch (directY) {
                case ASC:
                    directionY = ScrollDirections.DirectionY.UP;
                    break;
                case DESC:
                    directionY = ScrollDirections.DirectionY.DOWN;
                    break;
            }
        }
        this.scrollAdapter.setScrolling(directionX, directionY);
    }

    private void changeMovementUnits(MovementAccumulator movementAccumulator, float f) {
        if (((float) (System.currentTimeMillis() - movementAccumulator.getMovementStartTimestamp())) > movementAccumulator.getMovementUnitsAccumulated()) {
            movementAccumulator.stop(f);
        }
    }

    private void notifyTimer() {
        long currentTimeMillis = System.currentTimeMillis();
        this.movementX.processFingerMovement(false, this.savedFinger.getX(), currentTimeMillis);
        this.movementY.processFingerMovement(false, this.savedFinger.getY(), currentTimeMillis);
        MovementAccumulator.Direction directionX = this.movementX.getDirection();
        MovementAccumulator.Direction directionY = this.movementY.getDirection();
        boolean doScrollX = this.movementX.getDirection() != MovementAccumulator.Direction.NEUTRAL && this.movementX.getMovementUnitsAccumulated() >= 1.0f;
        boolean doScrollY = this.movementY.getDirection() != MovementAccumulator.Direction.NEUTRAL && this.movementY.getMovementUnitsAccumulated() >= 1.0f;
        scrollImpl(directionX, directionY, doScrollX, doScrollY);
        if (doScrollX)
            changeMovementUnits(this.movementX, this.savedFinger.getX());
        if (doScrollY)
            changeMovementUnits(this.movementY, this.savedFinger.getY());
        if (getContext().getFingers().isEmpty()) {
            if (this.cancelIfFingerReleased || !(doScrollX || doScrollY)) {
                if (doScrollX || doScrollY)
                    this.scrollAdapter.setScrolling(ScrollDirections.DirectionX.NONE, ScrollDirections.DirectionY.NONE);
                sendEvent(GESTURE_COMPLETED);
            }
        }
    }
}