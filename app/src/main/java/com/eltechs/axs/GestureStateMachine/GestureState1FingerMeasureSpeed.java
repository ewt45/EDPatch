package com.eltechs.axs.GestureStateMachine;

import com.eltechs.axs.Finger;
import com.eltechs.axs.GeometryHelpers;
import com.eltechs.axs.TouchEventAdapter;
import com.eltechs.axs.finiteStateMachine.FSMEvent;
import com.eltechs.axs.helpers.Assert;
import com.eltechs.axs.helpers.OneShotTimer;
//import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import java.util.List;

/**
 * 设定一个时间段。
 * 如果在时间段内，
 *   手指出现move且移动距离大于aimingFingerMaxMove，则发送FINGER_FLASHED
 *   某个手指松开，且移动距离小于tappingFingerMaxMove，则发送Tap事件，否则发送WalkAndGone事件
 *   新的手指按下，发送Touch事件
 * 如果在时间段内没发送任何事件，那么在倒计时结束后
 *   这段期间内手指移动距离比standingFingerMaxMove还小，发送Stand事件，
 *   比standingFingerMaxMove大，但小于aimingFingerMaxMove，发送Walk事件
 *   比aimingFingerMaxMove还大，发送Flash事件
 *
 */
public class GestureState1FingerMeasureSpeed extends AbstractGestureFSMState implements TouchEventAdapter {
    private final float aimingFingerMaxMove;
    private double distance;
    private final int measureTime;
    private final float standingFingerMaxMove;
    private final float tappingFingerMaxMove;
    private OneShotTimer timer;
    private long touchTime;
    public static FSMEvent FINGER_STANDING = new FSMEvent() { // from class: com.eltechs.axs.GestureStateMachine.GestureState1FingerMeasureSpeed.1
    };
    public static FSMEvent FINGER_TAPPED = new FSMEvent() { // from class: com.eltechs.axs.GestureStateMachine.GestureState1FingerMeasureSpeed.2
    };
    public static FSMEvent FINGER_WALKED = new FSMEvent() { // from class: com.eltechs.axs.GestureStateMachine.GestureState1FingerMeasureSpeed.3
    };
    public static FSMEvent FINGER_WALKED_AND_GONE = new FSMEvent() { // from class: com.eltechs.axs.GestureStateMachine.GestureState1FingerMeasureSpeed.4
    };
    public static FSMEvent FINGER_FLASHED = new FSMEvent() { // from class: com.eltechs.axs.GestureStateMachine.GestureState1FingerMeasureSpeed.5
    };
    public static FSMEvent FINGER_TOUCHED = new FSMEvent() { // from class: com.eltechs.axs.GestureStateMachine.GestureState1FingerMeasureSpeed.6
    };
    public static FSMEvent FINGER_MOVED_IN = new FSMEvent() { // from class: com.eltechs.axs.GestureStateMachine.GestureState1FingerMeasureSpeed.7
    };
    public static FSMEvent FINGER_MOVED_OUT = new FSMEvent() { // from class: com.eltechs.axs.GestureStateMachine.GestureState1FingerMeasureSpeed.8
    };

    public GestureState1FingerMeasureSpeed(GestureContext gestureContext, int measureTime, float standingFingerMaxMove, float aimingFingerMaxMove, float tappingFingerMaxMove, float f4) {
        super(gestureContext);
        this.measureTime = measureTime;
        this.standingFingerMaxMove = standingFingerMaxMove;
        this.tappingFingerMaxMove = tappingFingerMaxMove;
        this.aimingFingerMaxMove = aimingFingerMaxMove;
        this.distance =0.0d;
    }

    @Override // com.eltechs.axs.finiteStateMachine.FSMState
    public void notifyBecomeActive() {
        Assert.state(getContext().getFingers().size() == 1);
        getContext().getFingerEventsSource().addListener(this);
        this.timer = new OneShotTimer(this.measureTime) { // from class: com.eltechs.axs.GestureStateMachine.GestureState1FingerMeasureSpeed.9
            @Override // android.os.CountDownTimer
            public void onFinish() {
                if (GestureState1FingerMeasureSpeed.this.getContext().getMachine().isActiveState(GestureState1FingerMeasureSpeed.this)) {
                    GestureState1FingerMeasureSpeed.this.notifyTimeout();
                }
            }
        };
        this.timer.start();
        this.touchTime = System.currentTimeMillis();
        this.distance = 0.0d;
    }

    @Override // com.eltechs.axs.finiteStateMachine.FSMState
    public void notifyBecomeInactive() {
        getContext().getFingerEventsSource().removeListener(this);
        this.timer.cancel();
    }

    private void recalcDistance(Finger finger) {
        double distance = GeometryHelpers.distance(finger.getX(), finger.getY(), finger.getXWhenFirstTouched(), finger.getYWhenFirstTouched());
        if (this.distance < distance) {
            this.distance = distance;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    private void notifyTimeout() {
        boolean z = getContext().getFingers().size() == 1;
        Assert.isTrue(z);
        recalcDistance(getContext().getFingers().get(0));
        if (this.distance < this.standingFingerMaxMove) {
            sendEvent(FINGER_STANDING);
        } else if (this.distance < this.aimingFingerMaxMove) {
            sendEvent(FINGER_WALKED);
        } else {
            sendEvent(FINGER_FLASHED);
        }
    }

    @Override // com.eltechs.axs.TouchEventAdapter
    public void notifyTouched(Finger finger, List<Finger> list) {
        sendEvent(FINGER_TOUCHED);
    }

    @Override // com.eltechs.axs.TouchEventAdapter
    public void notifyReleased(Finger finger, List<Finger> list) {
        Assert.state(getContext().getFingers().isEmpty());
        recalcDistance(finger);
        if (this.distance < this.tappingFingerMaxMove) {
            sendEvent(FINGER_TAPPED);
        } else {
            sendEvent(FINGER_WALKED_AND_GONE);
        }
    }

    @Override // com.eltechs.axs.TouchEventAdapter
    public void notifyMoved(Finger finger, List<Finger> list) {
        recalcDistance(finger);
        if (this.distance >= this.aimingFingerMaxMove) {
            sendEvent(FINGER_FLASHED);
        }
    }

    @Override // com.eltechs.axs.TouchEventAdapter
    public void notifyMovedOut(Finger finger, List<Finger> list) {
        sendEvent(FINGER_MOVED_OUT);
    }

    @Override // com.eltechs.axs.TouchEventAdapter
    public void notifyMovedIn(Finger finger, List<Finger> list) {
        sendEvent(FINGER_MOVED_IN);
    }
}
