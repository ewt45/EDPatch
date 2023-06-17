package com.example.datainsert.exagear.controls.axs.GestureStateMachine;

import com.eltechs.axs.Finger;
import com.eltechs.axs.GeometryHelpers;
import com.eltechs.axs.GestureStateMachine.AbstractGestureFSMState;
import com.eltechs.axs.GestureStateMachine.GestureContext;
import com.eltechs.axs.TouchEventAdapter;
import com.eltechs.axs.finiteStateMachine.FSMEvent;
import com.eltechs.axs.helpers.Assert;
import com.eltechs.axs.helpers.OneShotTimer;

import java.util.List;


/**
 * 和GestureState1FingerMeasureSpeed一样, 但是
 *      从检测第一根手指变为指定检测某根手指 (传入的fingerindex从0开始）
 *      由于传入判定距离为0， 判断自身距离的distance>=改为>, distance<改为<=
 *
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
public class StateMesOneFSpd extends AbstractGestureFSMState implements TouchEventAdapter {
    //检测第几根手指
    private final int fingerIndex;
    /**
     * 维护一个检测的手指。主要用于release的时候
     */
    private Finger mFinger;
    private final float aimingFingerMaxMove;
    private double distance;
    private final int measureTime;
    private final float standingFingerMaxMove;
    private final float tappingFingerMaxMove;
    private OneShotTimer timer;
    private long touchTime;
    public static FSMEvent FINGER_STANDING = new FSMEvent("FINGER_STANDING") ;
    public static FSMEvent FINGER_TAPPED = new FSMEvent("FINGER_TAPPED") ;
    public static FSMEvent FINGER_WALKED = new FSMEvent("FINGER_WALKED");
    public static FSMEvent FINGER_WALKED_AND_GONE = new FSMEvent("FINGER_WALKED_AND_GONE") ;
    public static FSMEvent FINGER_FLASHED = new FSMEvent("FINGER_FLASHED") ;
    public static FSMEvent FINGER_TOUCHED = new FSMEvent("FINGER_TOUCHED") ;
    public static FSMEvent FINGER_MOVED_IN = new FSMEvent("FINGER_MOVED_IN") ;
    public static FSMEvent FINGER_MOVED_OUT = new FSMEvent("FINGER_MOVED_OUT") ;

    /**
     *
     * @param fingerIndex 要检测第几根手指，从0开始。
     */
    public StateMesOneFSpd(GestureContext gestureContext, int measureTime, float standingFingerMaxMove, float aimingFingerMaxMove, float tappingFingerMaxMove, int fingerIndex) {
        super(gestureContext);
        this.measureTime = measureTime;
        this.standingFingerMaxMove = standingFingerMaxMove;
        this.tappingFingerMaxMove = tappingFingerMaxMove;
        this.aimingFingerMaxMove = aimingFingerMaxMove;
        this.distance =0.0d;
        this.fingerIndex = fingerIndex;
    }

    @Override // com.eltechs.axs.finiteStateMachine.FSMState
    public void notifyBecomeActive() {
        Assert.state(getContext().getFingers().size() > fingerIndex);
        mFinger = getContext().getFingers().get(fingerIndex);
        getContext().getFingerEventsSource().addListener(this);
        this.timer = new OneShotTimer(this.measureTime) {
            @Override // android.os.CountDownTimer
            public void onFinish() {
                if (getContext().getMachine().isActiveState(StateMesOneFSpd.this)) {
                    notifyTimeout();
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

    private void notifyTimeout() {
        //固定的1改为自定义的第几根手指
        Assert.isTrue(getContext().getFingers().size()>fingerIndex);
        recalcDistance(mFinger);
        if (this.distance <= this.standingFingerMaxMove) {
            sendEvent(FINGER_STANDING);
        } else if (this.distance <= this.aimingFingerMaxMove) {
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
//        Assert.state(getContext().getFingers().isEmpty());
        //这个手指已经松了，也没法判断是不是指定的index的手指。。那只能之前的时候存一份了
        recalcDistance(mFinger);
        //只有当没移动，且松手的手指时自己检测的手指的时候，才是点击
        if (this.distance <= this.tappingFingerMaxMove && mFinger == finger) {
            sendEvent(FINGER_TAPPED);
        } else {
            sendEvent(FINGER_WALKED_AND_GONE);
        }
    }

    @Override // com.eltechs.axs.TouchEventAdapter
    public void notifyMoved(Finger finger, List<Finger> list) {
        //这个也不能检测这个finger，应该改为对应index？
        recalcDistance(mFinger);
        if (this.distance > this.aimingFingerMaxMove) {
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
