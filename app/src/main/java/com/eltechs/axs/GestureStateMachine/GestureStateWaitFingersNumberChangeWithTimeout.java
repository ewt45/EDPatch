package com.eltechs.axs.GestureStateMachine;

import com.eltechs.axs.Finger;
import com.eltechs.axs.TouchEventAdapter;
import com.eltechs.axs.finiteStateMachine.FSMEvent;
import com.eltechs.axs.helpers.OneShotTimer;
import java.util.List;

/* loaded from: classes.dex */
public class GestureStateWaitFingersNumberChangeWithTimeout extends AbstractGestureFSMState implements TouchEventAdapter {
    private final int timeoutMs;
    private OneShotTimer timer;
    public static FSMEvent FINGER_TOUCHED = new FSMEvent();
    public static FSMEvent FINGER_RELEASED = new FSMEvent();
    public static FSMEvent FINGER_MOVED_IN = new FSMEvent();
    public static FSMEvent FINGER_MOVED_OUT = new FSMEvent();
    public static FSMEvent TIMED_OUT = new FSMEvent();

    @Override // com.eltechs.axs.TouchEventAdapter
    public void notifyMoved(Finger finger, List<Finger> list) {
    }

    public GestureStateWaitFingersNumberChangeWithTimeout(GestureContext gestureContext, int timeoutMs) {
        super(gestureContext);
        this.timeoutMs = timeoutMs;
    }

    @Override // com.eltechs.axs.finiteStateMachine.FSMState
    public void notifyBecomeActive() {
        getContext().getFingerEventsSource().addListener(this);
        this.timer = new OneShotTimer(this.timeoutMs) { // from class: com.eltechs.axs.GestureStateMachine.GestureStateWaitFingersNumberChangeWithTimeout.6
            @Override // android.os.CountDownTimer
            public void onFinish() {
                if (getContext().getMachine().isActiveState(GestureStateWaitFingersNumberChangeWithTimeout.this)) {
                    notifyTimeout();
                }
            }
        };
        this.timer.start();
    }

    @Override // com.eltechs.axs.finiteStateMachine.FSMState
    public void notifyBecomeInactive() {
        getContext().getFingerEventsSource().removeListener(this);
        this.timer.cancel();
    }

    private void notifyTimeout() {
        sendEvent(TIMED_OUT);
    }

    @Override // com.eltechs.axs.TouchEventAdapter
    public void notifyTouched(Finger finger, List<Finger> list) {
        sendEvent(FINGER_TOUCHED);
    }

    @Override // com.eltechs.axs.TouchEventAdapter
    public void notifyReleased(Finger finger, List<Finger> list) {
        sendEvent(FINGER_RELEASED);
    }

    @Override // com.eltechs.axs.TouchEventAdapter
    public void notifyMovedIn(Finger finger, List<Finger> list) {
        sendEvent(FINGER_MOVED_IN);
    }

    @Override // com.eltechs.axs.TouchEventAdapter
    public void notifyMovedOut(Finger finger, List<Finger> list) {
        sendEvent(FINGER_MOVED_OUT);
    }
}
