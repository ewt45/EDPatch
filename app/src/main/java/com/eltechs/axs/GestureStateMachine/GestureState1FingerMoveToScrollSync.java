package com.eltechs.axs.GestureStateMachine;

import com.eltechs.axs.Finger;
import com.eltechs.axs.GuestAppActionAdapters.ScrollDirections;
import com.eltechs.axs.GuestAppActionAdapters.SyncScrollAdapter;
import com.eltechs.axs.MovementAccumulator;
import com.eltechs.axs.finiteStateMachine.FSMEvent;
import com.eltechs.axs.helpers.Assert;
import com.eltechs.axs.helpers.InfiniteTimer;

/* loaded from: classes.dex */
public class GestureState1FingerMoveToScrollSync extends AbstractGestureFSMState {
    public static FSMEvent GESTURE_COMPLETED = new FSMEvent();
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

    public GestureState1FingerMoveToScrollSync(
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
        this.timer = new InfiniteTimer(this.fingerLocationPollIntervalMs) { // from class: com.eltechs.axs.GestureStateMachine.GestureState1FingerMoveToScrollSync.2
            @Override // android.os.CountDownTimer
            public void onTick(long j) {
                if (getContext().getMachine().isActiveState(GestureState1FingerMoveToScrollSync.this)) {
                    notifyTimer();
                }
            }
        };
        this.timer.start();
        Assert.isTrue(getContext().getFingers().size() == 1);
        this.savedFinger = getContext().getFingers().get(0);
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

    private void notifyTimer() {
        long currentTimeMillis = System.currentTimeMillis();
        this.movementX.processFingerMovement(false, this.savedFinger.getX(), currentTimeMillis);
        this.movementY.processFingerMovement(false, this.savedFinger.getY(), currentTimeMillis);
        MovementAccumulator.Direction direction = this.movementX.getDirection();
        MovementAccumulator.Direction direction2 = this.movementY.getDirection();
        boolean doScrollX = this.movementX.getDirection() != MovementAccumulator.Direction.NEUTRAL && this.movementX.getMovementUnitsAccumulated() >= 1.0f;
        boolean doScrollY = this.movementY.getDirection() != MovementAccumulator.Direction.NEUTRAL && this.movementY.getMovementUnitsAccumulated() >= 1.0f;
        scrollImpl(direction, direction2, doScrollX, doScrollY);
        if (doScrollX)
            changeMovementUnits(this.movementX, this.savedFinger.getX());
        if (doScrollY)
            changeMovementUnits(this.movementY, this.savedFinger.getY());

        if (getContext().getFingers().isEmpty()) {
            if (!this.breakIfFingerReleased && (doScrollX || doScrollY))
                return;

            sendEvent(GESTURE_COMPLETED);
        }
    }
}
