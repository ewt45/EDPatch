//package com.eltechs.axs.GestureStateMachine;
//
//import com.eltechs.axs.Finger;
//import com.eltechs.axs.GuestAppActionAdapters.AsyncScrollAdapter;
//import com.eltechs.axs.GuestAppActionAdapters.ScrollDirections;
//import com.eltechs.axs.MovementAccumulator;
//import com.eltechs.axs.TouchEventAdapter;
//import com.eltechs.axs.finiteStateMachine.FSMEvent;
//import com.eltechs.axs.helpers.Assert;
//import com.eltechs.axs.helpers.InfiniteTimer;
//import java.util.List;
//
///* loaded from: classes.dex */
//public class GestureState2FingerMoveToScrollAsync extends AbstractGestureFSMState implements TouchEventAdapter {
//    private static final long timerPeriodMs = 40;
//    private final boolean cancelIfFingerReleased;
//    private final boolean doAdjustPointerPosition;
//    private final float moveThresholdPixels;
//    private final float movementUnitsInOnePixelX;
//    private final float movementUnitsInOnePixelY;
//    private MovementAccumulator movementX;
//    private MovementAccumulator movementY;
//    private final int pointerMargin;
//    private List<Finger> savedFingers;
//    private final AsyncScrollAdapter scrollAdapter;
//    private InfiniteTimer timer;
//    public static FSMEvent FINGER_TOUCHED = new FSMEvent() { // from class: com.eltechs.axs.GestureStateMachine.GestureState2FingerMoveToScrollAsync.1_fix
//    };
//    public static FSMEvent FINGER_RELEASED = new FSMEvent() { // from class: com.eltechs.axs.GestureStateMachine.GestureState2FingerMoveToScrollAsync.2_fix
//    };
//    public static FSMEvent FINGER_MOVED_IN = new FSMEvent() { // from class: com.eltechs.axs.GestureStateMachine.GestureState2FingerMoveToScrollAsync.3_fix
//    };
//    public static FSMEvent FINGER_MOVED_OUT = new FSMEvent() { // from class: com.eltechs.axs.GestureStateMachine.GestureState2FingerMoveToScrollAsync.4_fix
//    };
//
//    @Override // com.eltechs.axs.TouchEventAdapter
//    public void notifyMoved(Finger finger, List list) {
//    }
//
//    public GestureState2FingerMoveToScrollAsync(GestureContext gestureContext, AsyncScrollAdapter asyncScrollAdapter, float f, float f2, float f3, boolean z, int i, boolean z2) {
//        super(gestureContext);
//        this.scrollAdapter = asyncScrollAdapter;
//        this.movementUnitsInOnePixelX = f;
//        this.movementUnitsInOnePixelY = f2;
//        this.moveThresholdPixels = f3;
//        this.doAdjustPointerPosition = z;
//        this.pointerMargin = i;
//        this.cancelIfFingerReleased = z2;
//    }
//
//    @Override // com.eltechs.axs.finiteStateMachine.FSMState
//    public void notifyBecomeActive() {
//        this.timer = new InfiniteTimer(timerPeriodMs) { // from class: com.eltechs.axs.GestureStateMachine.GestureState2FingerMoveToScrollAsync.5_fix
//            @Override // android.os.CountDownTimer
//            public void onTick(long j) {
//                if (GestureState2FingerMoveToScrollAsync.this.getContext().getMachine().isActiveState(GestureState2FingerMoveToScrollAsync.this)) {
//                    GestureState2FingerMoveToScrollAsync.this.notifyTimer();
//                }
//            }
//        };
//        this.timer.start();
//        Assert.isTrue(getContext().getFingers().size() == 2);
//        this.savedFingers = getContext().getFingers();
//        getContext().getFingerEventsSource().addListener(this);
//        this.movementX = new MovementAccumulator(this.movementUnitsInOnePixelX, this.moveThresholdPixels);
//        this.movementY = new MovementAccumulator(this.movementUnitsInOnePixelY, this.moveThresholdPixels);
//        this.movementX.reset((this.savedFingers.get(0).getX() + this.savedFingers.get(1).getX()) / 2.0f);
//        this.movementY.reset((this.savedFingers.get(0).getY() + this.savedFingers.get(1).getY()) / 2.0f);
//        if (this.doAdjustPointerPosition) {
//            Helpers.adjustPointerPosition(getContext().getViewFacade(), this.pointerMargin);
//        }
//        this.scrollAdapter.notifyStart();
//    }
//
//    @Override // com.eltechs.axs.finiteStateMachine.FSMState
//    public void notifyBecomeInactive() {
//        this.scrollAdapter.setScrolling(ScrollDirections.DirectionX.NONE, ScrollDirections.DirectionY.NONE);
//        getContext().getFingerEventsSource().removeListener(this);
//        this.scrollAdapter.notifyStop();
//        this.timer.cancel();
//    }
//
//    private void scrollImpl(MovementAccumulator.Direction direction, MovementAccumulator.Direction direction2, boolean z, boolean z2) {
//        ScrollDirections.DirectionX directionX = ScrollDirections.DirectionX.NONE;
//        ScrollDirections.DirectionY directionY = ScrollDirections.DirectionY.NONE;
//        if (z) {
//            int i = C6_fix.$SwitchMap$com$eltechs$axs$MovementAccumulator$Direction[direction.ordinal()];
//            if (i == 1) {
//                directionX = ScrollDirections.DirectionX.LEFT;
//            } else if (i == 2) {
//                directionX = ScrollDirections.DirectionX.RIGHT;
//            }
//        }
//        if (z2) {
//            int i2 = C6_fix.$SwitchMap$com$eltechs$axs$MovementAccumulator$Direction[direction2.ordinal()];
//            if (i2 == 1) {
//                directionY = ScrollDirections.DirectionY.UP;
//            } else if (i2 == 2) {
//                directionY = ScrollDirections.DirectionY.DOWN;
//            }
//        }
//        this.scrollAdapter.setScrolling(directionX, directionY);
//    }
//
//    /* JADX INFO: Access modifiers changed from: package-private */
//    /* renamed from: com.eltechs.axs.GestureStateMachine.GestureState2FingerMoveToScrollAsync$6_fix  reason: invalid class name */
//    /* loaded from: classes.dex */
//    public static /* synthetic */ class C6_fix {
//        static final /* synthetic */ int[] $SwitchMap$com$eltechs$axs$MovementAccumulator$Direction = new int[MovementAccumulator.Direction.values().length];
//
//        static {
//            try {
//                $SwitchMap$com$eltechs$axs$MovementAccumulator$Direction[MovementAccumulator.Direction.ASC.ordinal()] = 1;
//            } catch (NoSuchFieldError unused) {
//            }
//            try {
//                $SwitchMap$com$eltechs$axs$MovementAccumulator$Direction[MovementAccumulator.Direction.DESC.ordinal()] = 2;
//            } catch (NoSuchFieldError unused2) {
//            }
//        }
//    }
//
//    private void changeMovementUnits(MovementAccumulator movementAccumulator, float f) {
//        if (((float) (System.currentTimeMillis() - movementAccumulator.getMovementStartTimestamp())) > movementAccumulator.getMovementUnitsAccumulated()) {
//            movementAccumulator.stop(f);
//        }
//    }
//
//    public void notifyTimer() {
//        long currentTimeMillis = System.currentTimeMillis();
//        if (this.savedFingers.size() != 2) {
//            return;
//        }
//        this.movementX.processFingerMovement(false, (this.savedFingers.get(0).getX() + this.savedFingers.get(1).getX()) / 2.0f, currentTimeMillis);
//        this.movementY.processFingerMovement(false, (this.savedFingers.get(0).getY() + this.savedFingers.get(1).getY()) / 2.0f, currentTimeMillis);
//        MovementAccumulator.Direction direction = this.movementX.getDirection();
//        MovementAccumulator.Direction direction2 = this.movementY.getDirection();
//        boolean z = this.movementX.getDirection() != MovementAccumulator.Direction.NEUTRAL && this.movementX.getMovementUnitsAccumulated() >= 1.0f;
//        boolean z2 = this.movementY.getDirection() != MovementAccumulator.Direction.NEUTRAL && this.movementY.getMovementUnitsAccumulated() >= 1.0f;
//        scrollImpl(direction, direction2, z, z2);
//        if (z) {
//            changeMovementUnits(this.movementX, (this.savedFingers.get(0).getX() + this.savedFingers.get(1).getX()) / 2.0f);
//        }
//        if (!z2) {
//            return;
//        }
//        changeMovementUnits(this.movementY, (this.savedFingers.get(0).getY() + this.savedFingers.get(1).getY()) / 2.0f);
//    }
//
//    @Override // com.eltechs.axs.TouchEventAdapter
//    public void notifyReleased(Finger finger, List list) {
//        sendEvent(FINGER_RELEASED);
//    }
//
//    @Override // com.eltechs.axs.TouchEventAdapter
//    public void notifyTouched(Finger finger, List<Finger> list) {
//        sendEvent(FINGER_TOUCHED);
//    }
//
//    @Override // com.eltechs.axs.TouchEventAdapter
//    public void notifyMovedIn(Finger finger, List list) {
//        sendEvent(FINGER_MOVED_IN);
//    }
//
//    @Override // com.eltechs.axs.TouchEventAdapter
//    public void notifyMovedOut(Finger finger, List list) {
//        sendEvent(FINGER_MOVED_OUT);
//    }
//}
