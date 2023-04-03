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
    public static FSMEvent GESTURE_COMPLETED = new FSMEvent() { // from class: com.eltechs.axs.GestureStateMachine.GestureState1FingerMoveToScrollAsync.1
    };
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

    /* renamed from: com.eltechs.axs.GestureStateMachine.GestureState1FingerMoveToScrollAsync$1_fix  reason: invalid class name */
    /* loaded from: classes.dex */
    class C1_fix extends InfiniteTimer {
        C1_fix(long j) {
            super(j);
        }

        @Override // android.os.CountDownTimer
        public void onTick(long j) {
            if (GestureState1FingerMoveToScrollAsync.this.getContext().getMachine().isActiveState(GestureState1FingerMoveToScrollAsync.this)) {
                GestureState1FingerMoveToScrollAsync.this.notifyTimer();
            }
        }
    }

    public GestureState1FingerMoveToScrollAsync(
            GestureContext gestureContext, AsyncScrollAdapter asyncScrollAdapter,
            float f, float f2, float f3, boolean z, int i, boolean z2) {
        super(gestureContext);
        this.scrollAdapter = asyncScrollAdapter;
        this.movementUnitsInOnePixelX = f;
        this.movementUnitsInOnePixelY = f2;
        this.moveThresholdPixels = f3;
        this.doAdjustPointerPosition = z;
        this.pointerMargin = i;
        this.cancelIfFingerReleased = z2;
    }

    @Override // com.eltechs.axs.finiteStateMachine.FSMState
    public void notifyBecomeActive() {
        this.timer = new InfiniteTimer(timerPeriodMs) { // from class: com.eltechs.axs.GestureStateMachine.GestureState1FingerMoveToScrollAsync.2
            @Override // android.os.CountDownTimer
            public void onTick(long j) {
                if (GestureState1FingerMoveToScrollAsync.this.getContext().getMachine().isActiveState(GestureState1FingerMoveToScrollAsync.this)) {
                    GestureState1FingerMoveToScrollAsync.this.notifyTimer();
                }
            }
        };
        this.timer.start();
        boolean z = true;
        if (getContext().getFingers().size() != 1) {
            z = false;
        }
        Assert.isTrue(z);
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

    private void scrollImpl(MovementAccumulator.Direction direction, MovementAccumulator.Direction direction2, boolean z, boolean z2) {
        ScrollDirections.DirectionX directionX = ScrollDirections.DirectionX.NONE;
        ScrollDirections.DirectionY directionY = ScrollDirections.DirectionY.NONE;
        if (z) {
            switch (direction) {
                case ASC:
                    directionX = ScrollDirections.DirectionX.LEFT;
                    break;
                case DESC:
                    directionX = ScrollDirections.DirectionX.RIGHT;
                    break;
            }
        }
        if (z2) {
            switch (direction2) {
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

    /* JADX INFO: Access modifiers changed from: private */
    private void notifyTimer() {
        long currentTimeMillis = System.currentTimeMillis();
        boolean z = false;
        this.movementX.processFingerMovement(false, this.savedFinger.getX(), currentTimeMillis);
        this.movementY.processFingerMovement(false, this.savedFinger.getY(), currentTimeMillis);
        MovementAccumulator.Direction direction = this.movementX.getDirection();
        MovementAccumulator.Direction direction2 = this.movementY.getDirection();
        boolean z2 = this.movementX.getDirection() != MovementAccumulator.Direction.NEUTRAL && this.movementX.getMovementUnitsAccumulated() >= 1.0f;
        if (this.movementY.getDirection() != MovementAccumulator.Direction.NEUTRAL && this.movementY.getMovementUnitsAccumulated() >= 1.0f) {
            z = true;
        }
        scrollImpl(direction, direction2, z2, z);
        if (z2) {
            changeMovementUnits(this.movementX, this.savedFinger.getX());
        }
        if (z) {
            changeMovementUnits(this.movementY, this.savedFinger.getY());
        }
        if (getContext().getFingers().isEmpty()) {
            if (!this.cancelIfFingerReleased && (z2 || z)) {
                return;
            }
            if (z2 || z) {
                this.scrollAdapter.setScrolling(ScrollDirections.DirectionX.NONE, ScrollDirections.DirectionY.NONE);
            }
            sendEvent(GESTURE_COMPLETED);
        }
    }
}
