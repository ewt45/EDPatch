package com.eltechs.axs.GestureStateMachine;

import com.eltechs.axs.Finger;
import com.eltechs.axs.TouchEventAdapter;
import com.eltechs.axs.finiteStateMachine.FSMEvent;
import com.eltechs.axs.helpers.Assert;
import com.eltechs.axs.helpers.InfiniteTimer;
import java.util.List;

/* loaded from: classes.dex */
public class GestureState1FingerToZoomMove extends AbstractGestureFSMState implements TouchEventAdapter {
    private static final int timerPeriodMs = 40;
    private InfiniteTimer timer;
    public static FSMEvent FINGER_TOUCHED = new FSMEvent();
    public static FSMEvent FINGER_RELEASED = new FSMEvent();
    public static FSMEvent FINGER_MOVED_IN = new FSMEvent();
    public static FSMEvent FINGER_MOVED_OUT = new FSMEvent();

    @Override // com.eltechs.axs.TouchEventAdapter
    public void notifyMoved(Finger finger, List<Finger> list) {
    }

    public GestureState1FingerToZoomMove(GestureContext gestureContext) {
        super(gestureContext);
    }

    @Override // com.eltechs.axs.finiteStateMachine.FSMState
    public void notifyBecomeActive() {
        getContext().getFingerEventsSource().addListener(this);
        this.timer = new InfiniteTimer(timerPeriodMs) { // from class: com.eltechs.axs.GestureStateMachine.GestureState1FingerToZoomMove.5
            @Override // android.os.CountDownTimer
            public void onTick(long j) {
                if (getContext().getMachine().isActiveState(GestureState1FingerToZoomMove.this)) {
                    notifyTimer();
                }
            }
        };
        this.timer.start();
        Assert.state(getContext().getFingers().size() == 1);
        Finger finger = getContext().getFingers().get(0);
        getContext().getZoomController().setAnchorBoth(finger.getXWhenFingerCountLastChanged(), finger.getYWhenFingerCountLastChanged());
    }

    @Override // com.eltechs.axs.finiteStateMachine.FSMState
    public void notifyBecomeInactive() {
        getContext().getFingerEventsSource().removeListener(this);
        this.timer.cancel();
    }

    private void notifyTimer() {
        List<Finger> fingers = getContext().getFingers();
        Assert.state(fingers.size() == 1);
        Finger finger = fingers.get(0);
        getContext().getZoomController().setAnchorHost(finger.getX(), finger.getY());
        getContext().getZoomController().refreshZoom();
    }

    @Override // com.eltechs.axs.TouchEventAdapter
    public void notifyTouched(Finger finger, List<Finger> list) {
        sendEvent(FINGER_TOUCHED);
    }

    @Override // com.eltechs.axs.TouchEventAdapter
    public void notifyMovedIn(Finger finger, List<Finger> list) {
        sendEvent(FINGER_MOVED_IN);
    }

    @Override // com.eltechs.axs.TouchEventAdapter
    public void notifyReleased(Finger finger, List<Finger> list) {
        sendEvent(FINGER_RELEASED);
    }

    @Override // com.eltechs.axs.TouchEventAdapter
    public void notifyMovedOut(Finger finger, List<Finger> list) {
        sendEvent(FINGER_MOVED_OUT);
    }
}
