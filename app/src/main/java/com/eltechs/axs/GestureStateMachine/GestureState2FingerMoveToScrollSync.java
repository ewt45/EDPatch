//package com.eltechs.axs.GestureStateMachine;
//
//import com.eltechs.axs.Finger;
//import com.eltechs.axs.GuestAppActionAdapters.ScrollDirections;
//import com.eltechs.axs.GuestAppActionAdapters.SyncScrollAdapter;
//import com.eltechs.axs.MovementAccumulator;
//import com.eltechs.axs.TouchEventAdapter;
//import com.eltechs.axs.finiteStateMachine.FSMEvent;
//import com.eltechs.axs.helpers.Assert;
//import com.eltechs.axs.helpers.InfiniteTimer;
//import java.util.List;
//
///* loaded from: classes.dex */
//public class GestureState2FingerMoveToScrollSync extends AbstractGestureFSMState implements TouchEventAdapter {
//    private final boolean doAdjustPointerPosition;
//    private final long fingerLocationPollIntervalMs;
//    private final float moveThresholdPixels;
//    private final float movementUnitsInOnePixelX;
//    private final float movementUnitsInOnePixelY;
//    private MovementAccumulator movementX;
//    private MovementAccumulator movementY;
//    private final int pointerMargin;
//    private List<Finger> savedFingers;
//    private final SyncScrollAdapter scrollAdapter;
//    private InfiniteTimer timer;
//    public static FSMEvent FINGER_MOVED_IN = new FSMEvent() { // from class: com.eltechs.axs.GestureStateMachine.GestureState2FingerMoveToScrollSync.1_fix
//    };
//    public static FSMEvent FINGER_MOVED_OUT = new FSMEvent() { // from class: com.eltechs.axs.GestureStateMachine.GestureState2FingerMoveToScrollSync.2_fix
//    };
//    public static FSMEvent FINGER_TOUCHED = new FSMEvent() { // from class: com.eltechs.axs.GestureStateMachine.GestureState2FingerMoveToScrollSync.3_fix
//    };
//    public static FSMEvent FINGER_RELEASED = new FSMEvent() { // from class: com.eltechs.axs.GestureStateMachine.GestureState2FingerMoveToScrollSync.4_fix
//    };
//
//    @Override // com.eltechs.axs.TouchEventAdapter
//    public void notifyMoved(Finger finger, List list) {
//    }
//
//    public GestureState2FingerMoveToScrollSync(GestureContext gestureContext, SyncScrollAdapter syncScrollAdapter, float f, float f2, float f3, boolean z, int i, long j, boolean z2) {
//        super(gestureContext);
//        this.scrollAdapter = syncScrollAdapter;
//        this.movementUnitsInOnePixelX = f;
//        this.movementUnitsInOnePixelY = f2;
//        this.moveThresholdPixels = f3;
//        this.doAdjustPointerPosition = z;
//        this.pointerMargin = i;
//        this.fingerLocationPollIntervalMs = j;
//    }
//
//    @Override // com.eltechs.axs.finiteStateMachine.FSMState
//    public void notifyBecomeActive() {
//        this.timer = new InfiniteTimer(this.fingerLocationPollIntervalMs) { // from class: com.eltechs.axs.GestureStateMachine.GestureState2FingerMoveToScrollSync.5_fix
//            @Override // android.os.CountDownTimer
//            public void onTick(long j) {
//                if (GestureState2FingerMoveToScrollSync.this.getContext().getMachine().isActiveState(GestureState2FingerMoveToScrollSync.this)) {
//                    GestureState2FingerMoveToScrollSync.this.notifyTimer();
//                }
//            }
//        };
//        this.timer.start();
//        Assert.isTrue(getContext().getFingers().size() == 2);
//        getContext().getFingerEventsSource().addListener(this);
//        this.savedFingers = getContext().getFingers();
//        this.movementX = new MovementAccumulator(this.movementUnitsInOnePixelX, 0.0f);
//        this.movementY = new MovementAccumulator(this.movementUnitsInOnePixelY, 0.0f);
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
//        this.scrollAdapter.scroll(directionX, directionY, 1);
//    }
//
//    /* JADX INFO: Access modifiers changed from: package-private */
//    /* renamed from: com.eltechs.axs.GestureStateMachine.GestureState2FingerMoveToScrollSync$6_fix  reason: invalid class name */
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
//        float movementUnitsAccumulated = movementAccumulator.getMovementUnitsAccumulated() - 1.0f;
//        if (movementUnitsAccumulated > 0.0f) {
//            movementAccumulator.setMovementUnitsAccumulated(movementUnitsAccumulated);
//        } else {
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
//    public void notifyTouched(Finger finger, List<Finger> list) {
//        sendEvent(FINGER_TOUCHED);
//    }
//
//    @Override // com.eltechs.axs.TouchEventAdapter
//    public void notifyReleased(Finger finger, List list) {
//        sendEvent(FINGER_RELEASED);
//    }
//
//    @Override // com.eltechs.axs.TouchEventAdapter
//    public void notifyMovedOut(Finger finger, List list) {
//        sendEvent(FINGER_MOVED_OUT);
//    }
//
//    @Override // com.eltechs.axs.TouchEventAdapter
//    public void notifyMovedIn(Finger finger, List list) {
//        sendEvent(FINGER_MOVED_IN);
//    }
//}
