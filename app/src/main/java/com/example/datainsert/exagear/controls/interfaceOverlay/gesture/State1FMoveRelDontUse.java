package com.example.datainsert.exagear.controls.interfaceOverlay.gesture;

import android.util.Log;

import com.eltechs.axs.Finger;
import com.eltechs.axs.GeometryHelpers;
import com.eltechs.axs.GestureStateMachine.AbstractGestureFSMState;
import com.eltechs.axs.GestureStateMachine.GestureContext;
import com.eltechs.axs.TouchEventAdapter;
import com.eltechs.axs.finiteStateMachine.FSMEvent;
import com.eltechs.axs.helpers.OneShotTimer;
import java.util.List;

/**
 * 用于检测手指动作的gestureState。会根据当前是否为相对移动 返回不同的事件
 * 原本是有assert，必须要要当前手指个数为1个。不知道有什么用，我要用多个，去掉吧。
 *
 * @deprecated 太复杂了，还是用原有的吧。这个当时主要是不知道能在启动XServerDisplayActivity之后还可以动态切换状态机，所以都写到一个里面了。
 *
 */
@Deprecated
public class State1FMoveRelDontUse extends AbstractGestureFSMState implements TouchEventAdapter {
    final static String TAG="State1FMoveRel";
    public static boolean isRelMove;  //是否是相对定位
//    public static FSMEvent FINGER_STANDING = new FSMEvent();
    public static FSMEvent FINGER_TAPPED = new FSMEvent("FINGER_TAPPED");
    public static FSMEvent FINGER_TAPPED_REL = new FSMEvent("FINGER_TAPPED_REL");
    public static FSMEvent FINGER_LONG_TAPPED = new FSMEvent("FINGER_LONG_TAPPED");
    public static FSMEvent FINGER_LONG_TAPPED_REL = new FSMEvent("FINGER_LONG_TAPPED_REL");
    public static FSMEvent FINGER_IMMEDIATE_MOVED = new FSMEvent("FINGER_IMMEDIATE_MOVED");
    public static FSMEvent FINGER_IMMEDIATE_MOVED_REL = new FSMEvent("FINGER_IMMEDIATE_MOVED_REL");//用于相对定位时的事件
    public static FSMEvent FINGER_LONGPRESSED_MOVED = new FSMEvent("FINGER_LONGPRESSED_MOVED");
    public static FSMEvent FINGER_LONGPRESSED_MOVED_REL = new FSMEvent("FINGER_LONGPRESSED_MOVED_REL");

    public static FSMEvent FINGER_LONGPRESSED_STAND_REL = new FSMEvent("FINGER_LONGPRESSED_STAND_REL"); //长按但没移动，但也走到下一个状态
    public static FSMEvent FINGER_WALKED_AND_GONE = new FSMEvent("FINGER_WALKED_AND_GONE");
//    public static FSMEvent FINGER_FLASHED = new FSMEvent();
//    public static FSMEvent FINGER_FLASHED_REL = new FSMEvent();//用于相对定位时的事件
    public static FSMEvent NEW_FINGER_TOUCHED = new FSMEvent("NEW_FINGER_TOUCHED");
    public static FSMEvent NEW_FINGER_TOUCHED_REL = new FSMEvent("NEW_FINGER_TOUCHED_REL");
    public static FSMEvent FINGER_MOVED_IN = new FSMEvent("FINGER_MOVED_IN");
    public static FSMEvent FINGER_MOVED_OUT = new FSMEvent("FINGER_MOVED_OUT");
    private final int measureTime;
    private final float tappingFingerMaxMove;
    private double distance;
    private OneShotTimer timer;
    private long touchTime;
    private final boolean reportLongPress;

    public State1FMoveRelDontUse(GestureContext gestureContext, int measureTime, float tappingFingerMaxMove, boolean rptLgPrsIfRel) {
        super(gestureContext);
        this.measureTime = measureTime;
        this.tappingFingerMaxMove = tappingFingerMaxMove;
        this.distance = 0.0d;
        this.reportLongPress = rptLgPrsIfRel;
    }

    @Override // com.eltechs.axs.finiteStateMachine.FSMState
    public void notifyBecomeActive() {
        Log.d(TAG, "notifyBecomeActive: ");
//        Assert.state(getContext().getFingers().size() == 1);
        getContext().getFingerEventsSource().addListener(this);
        //当长按时，不需要松手就直接进到下一个状态
        if(isRelMove&&reportLongPress){
            this.timer = new OneShotTimer(this.measureTime) { // from class: com.eltechs.axs.GestureStateMachine.GestureState1FingerMeasureSpeed.9
                @Override // android.os.CountDownTimer
                public void onFinish() {
                    if (getContext().getMachine().isActiveState(State1FMoveRelDontUse.this)
                            && getContext().getFingers().size() == 1) {
                        recalcDistance(getContext().getFingers().get(0));
                        if(distance<tappingFingerMaxMove)
                            sendEvent(FINGER_LONGPRESSED_STAND_REL);
                    }
                }
            };
            this.timer.start();
        }
        this.touchTime = System.currentTimeMillis();
        this.distance = 0.0d;
    }

    @Override // com.eltechs.axs.finiteStateMachine.FSMState
    public void notifyBecomeInactive() {
        Log.d(TAG, "notifyBecomeInactive: ");
        getContext().getFingerEventsSource().removeListener(this);
        if(isRelMove&&reportLongPress){
            this.timer.cancel();
        }
    }

    private void recalcDistance(Finger finger) {
        double distance = GeometryHelpers.distance(finger.getX(), finger.getY(), finger.getXWhenFirstTouched(), finger.getYWhenFirstTouched());
        if (this.distance < distance) {
            this.distance = distance;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
//    private void notifyTimeout() {
//        Log.d(TAG, "notifyTimeout: ");
//        Assert.isTrue(getContext().getFingers().size() == 1);
//        recalcDistance(getContext().getFingers().get(0));
//        if (this.distance <= this.tappingFingerMaxMove) {
//            sendEvent(isRelMove?);
//        }else{
//            sendEvent(isRelMove ? FINGER_MOVED_REL : FINGER_MOVED);
//        }
////        else if (this.distance < this.aimingFingerMaxMove) {
////            sendEvent(isRelMove ? FINGER_WALKED_REL : FINGER_WALKED);
////        } else {
////            sendEvent(isRelMove ? FINGER_FLASHED_REL : FINGER_FLASHED);
////        }
//    }

    @Override // com.eltechs.axs.TouchEventAdapter
    public void notifyTouched(Finger finger, List<Finger> list) {

        Log.d(TAG, "notifyTouched: ");
        sendEvent(isRelMove? NEW_FINGER_TOUCHED_REL : NEW_FINGER_TOUCHED);
    }

    @Override // com.eltechs.axs.TouchEventAdapter
    public void notifyReleased(Finger finger, List<Finger> list) {

//        Assert.state(getContext().getFingers().isEmpty());
        //为什么会在三指按下的时候就直接触发Release事件？三指下滑截屏冲突了？
        if(!getContext().getMachine().isActiveState(this)){
            return;
        }
        recalcDistance(finger);
        Log.d(TAG, "notifyReleased: 移动距离="+distance+" 限制距离="+tappingFingerMaxMove+"");
        if (this.distance <= this.tappingFingerMaxMove) {
            if(System.currentTimeMillis()-touchTime<measureTime){
                Log.d(TAG, "notifyReleased: 点击事件");
                sendEvent(isRelMove?FINGER_TAPPED_REL:FINGER_TAPPED);
            }else{
                Log.d(TAG, "notifyReleased: 长按点击事件");
                sendEvent(isRelMove?FINGER_LONG_TAPPED_REL:FINGER_LONG_TAPPED);
            }
        } else {
            Log.d(TAG, "notifyReleased: FINGER_WALKED_AND_GONE事件");
            sendEvent(FINGER_WALKED_AND_GONE);
        }
    }

    @Override // com.eltechs.axs.TouchEventAdapter
    public void notifyMoved(Finger finger, List<Finger> list) {
        Log.d(TAG, "notifyMoved: ");
        recalcDistance(finger);
        //鸿蒙脑瘫判定，点击也算移动。只能靠移动距离来判断是不是点击了。
        if (this.distance <= this.tappingFingerMaxMove) {
            return;
        }
        //短暂按下后移动，正常移动事件
        if(System.currentTimeMillis()-touchTime<measureTime){
            sendEvent(isRelMove ? FINGER_IMMEDIATE_MOVED_REL : FINGER_IMMEDIATE_MOVED);
        }
        //长时间按下，这时候是第一次开始移动，应该是长按后移动（拖拽之类的）或者相对移动就先普通移动吧
        else{
            sendEvent(isRelMove? FINGER_LONGPRESSED_MOVED_REL :FINGER_LONGPRESSED_MOVED);
        }

//        recalcDistance(finger);
//        if (this.distance < this.standingFingerMaxMove) {
//            sendEvent(FINGER_STANDING);
//        } else if (this.distance < this.aimingFingerMaxMove) {
//            sendEvent(isRelMove ? FINGER_MOVED_REL : FINGER_MOVED);
//        } else {
//            sendEvent(isRelMove ? FINGER_FLASHED_REL : FINGER_FLASHED);
//        }

//        recalcDistance(finger);
//        if (this.distance >= this.aimingFingerMaxMove) {
//            sendEvent(isRelMove ? FINGER_FLASHED_REL : FINGER_FLASHED);
//        }
    }

    @Override // com.eltechs.axs.TouchEventAdapter
    public void notifyMovedOut(Finger finger, List<Finger> list) {
        Log.d(TAG, "notifyMovedOut: ");
        sendEvent(FINGER_MOVED_OUT);
    }

    @Override // com.eltechs.axs.TouchEventAdapter
    public void notifyMovedIn(Finger finger, List<Finger> list) {
        Log.d(TAG, "notifyMovedIn: ");
        sendEvent(FINGER_MOVED_IN);
    }
}
