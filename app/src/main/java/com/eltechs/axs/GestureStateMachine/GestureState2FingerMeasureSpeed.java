//package com.eltechs.axs.GestureStateMachine;
//
//import com.eltechs.axs.Finger;
//import com.eltechs.axs.FirebaseRemoteConfig;
//import com.eltechs.axs.GeometryHelpers;
//import com.eltechs.axs.TouchEventAdapter;
//import com.eltechs.axs.finiteStateMachine.FSMEvent;
//import com.eltechs.axs.helpers.Assert;
//import com.eltechs.axs.helpers.OneShotTimer;
////import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
//import java.util.List;
//
///* loaded from: classes.dex */
//public class GestureState2FingerMeasureSpeed extends AbstractGestureFSMState implements TouchEventAdapter {
//    public static FSMEvent FINGER_FLASHED = new FSMEvent() { // from class: com.eltechs.axs.GestureStateMachine.GestureState2FingerMeasureSpeed.1_fix
//    };
//    public static FSMEvent FINGER_MOVED_IN = new FSMEvent() { // from class: com.eltechs.axs.GestureStateMachine.GestureState2FingerMeasureSpeed.2_fix
//    };
//    public static FSMEvent FINGER_MOVED_OUT = new FSMEvent() { // from class: com.eltechs.axs.GestureStateMachine.GestureState2FingerMeasureSpeed.3_fix
//    };
//    public static FSMEvent FINGER_SCROLL = new FSMEvent() { // from class: com.eltechs.axs.GestureStateMachine.GestureState2FingerMeasureSpeed.4_fix
//    };
//    public static FSMEvent FINGER_TAPPED = new FSMEvent() { // from class: com.eltechs.axs.GestureStateMachine.GestureState2FingerMeasureSpeed.5_fix
//    };
//    public static FSMEvent FINGER_TOUCHED = new FSMEvent() { // from class: com.eltechs.axs.GestureStateMachine.GestureState2FingerMeasureSpeed.6_fix
//    };
//    private double[] distances;
//    private final float fingerFlashTime;
//    private final int measureTime;
//    private boolean[] moveFlag;
//    private long moveTime;
//    private final float standingFingerMaxMove;
//    private OneShotTimer timer;
//
//    public GestureState2FingerMeasureSpeed(GestureContext gestureContext, int i, float f, float f2) {
//        super(gestureContext);
//        this.distances = new double[]{FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE, FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE};
//        this.moveFlag = new boolean[]{false, false};
//        this.moveTime = 0L;
//        this.measureTime = i;
//        this.standingFingerMaxMove = f;
//        this.fingerFlashTime = f2;
//    }
//
//    @Override // com.eltechs.axs.finiteStateMachine.FSMState
//    public void notifyBecomeActive() {
//        Assert.state(getContext().getFingers().size() == 2);
//        getContext().getFingerEventsSource().addListener(this);
//        this.timer = new OneShotTimer(this.measureTime) { // from class: com.eltechs.axs.GestureStateMachine.GestureState2FingerMeasureSpeed.7_fix
//            @Override // android.os.CountDownTimer
//            public void onFinish() {
//                if (GestureState2FingerMeasureSpeed.this.getContext().getMachine().isActiveState(GestureState2FingerMeasureSpeed.this)) {
//                    GestureState2FingerMeasureSpeed.this.notifyTimeout();
//                }
//            }
//        };
//        boolean[] zArr = this.moveFlag;
//        zArr[0] = false;
//        zArr[1] = false;
//        double[] dArr = this.distances;
//        dArr[0] = 0.0d;
//        dArr[1] = 0.0d;
//        this.moveTime = 0L;
//        this.timer.start();
//    }
//
//    @Override // com.eltechs.axs.finiteStateMachine.FSMState
//    public void notifyBecomeInactive() {
//        getContext().getFingerEventsSource().removeListener(this);
//        this.timer.cancel();
//    }
//
//    private void recalcDistance(List<Finger> list) {
//        for (int i = 0; i < list.size(); i++) {
//            double distance = GeometryHelpers.distance(list.get(i).getX(), list.get(i).getY(), list.get(i).getXWhenFingerCountLastChanged(), list.get(i).getYWhenFingerCountLastChanged());
//            double[] dArr = this.distances;
//            if (dArr[i] < distance) {
//                dArr[i] = distance;
//            }
//            if (this.distances[i] < this.standingFingerMaxMove) {
//                this.moveFlag[i] = false;
//            } else {
//                this.moveFlag[i] = true;
//            }
//        }
//    }
//
//    public void notifyTimeout() {
//        Assert.isTrue(getContext().getFingers().size() == 2);
//        recalcDistance(getContext().getFingers());
//        boolean[] zArr = this.moveFlag;
//        if (zArr[1] ^ zArr[0]) {
//            sendEvent(FINGER_FLASHED);
//        } else {
//            sendEvent(FINGER_SCROLL);
//        }
//    }
//
//    @Override // com.eltechs.axs.TouchEventAdapter
//    public void notifyTouched(Finger finger, List<Finger> list) {
//        sendEvent(FINGER_TOUCHED);
//    }
//
//    @Override // com.eltechs.axs.TouchEventAdapter
//    public void notifyReleased(Finger finger, List list) {
//        sendEvent(FINGER_TAPPED);
//    }
//
//    @Override // com.eltechs.axs.TouchEventAdapter
//    public void notifyMoved(Finger finger, List list) {
//        recalcDistance(list);
//        boolean[] zArr = this.moveFlag;
//        if (zArr[0] && zArr[1]) {
//            sendEvent(FINGER_SCROLL);
//            return;
//        }
//        boolean[] zArr2 = this.moveFlag;
//        if (!zArr2[0] && !zArr2[1]) {
//            return;
//        }
//        if (this.moveTime == 0) {
//            this.moveTime = System.currentTimeMillis();
//        }
//        if (((float) (System.currentTimeMillis() - this.moveTime)) <= this.fingerFlashTime) {
//            return;
//        }
//        sendEvent(FINGER_FLASHED);
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
