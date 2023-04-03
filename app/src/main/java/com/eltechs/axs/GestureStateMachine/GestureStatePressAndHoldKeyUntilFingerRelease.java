package com.eltechs.axs.GestureStateMachine;

import com.eltechs.axs.Finger;
import com.eltechs.axs.KeyCodesX;
import com.eltechs.axs.TouchEventAdapter;
import com.eltechs.axs.finiteStateMachine.FSMEvent;
import com.eltechs.axs.helpers.Assert;
import java.util.List;

/* loaded from: classes.dex */
public class GestureStatePressAndHoldKeyUntilFingerRelease extends AbstractGestureFSMState implements TouchEventAdapter {
    public static FSMEvent GESTURE_COMPLETED = new FSMEvent() { // from class: com.eltechs.axs.GestureStateMachine.GestureStatePressAndHoldKeyUntilFingerRelease.1
    };
    private final KeyCodesX keyCode;

    @Override // com.eltechs.axs.TouchEventAdapter
    public void notifyMoved(Finger finger, List<Finger> list) {
    }

    @Override // com.eltechs.axs.TouchEventAdapter
    public void notifyMovedIn(Finger finger, List<Finger> list) {
    }

    @Override // com.eltechs.axs.TouchEventAdapter
    public void notifyTouched(Finger finger, List<Finger> list) {
    }

    public GestureStatePressAndHoldKeyUntilFingerRelease(GestureContext gestureContext, KeyCodesX keyCodesX) {
        super(gestureContext);
        this.keyCode = keyCodesX;
    }

    @Override // com.eltechs.axs.finiteStateMachine.FSMState
    public void notifyBecomeActive() {
        boolean z = true;
        if (getContext().getFingers().size() != 1) {
            z = false;
        }
        Assert.state(z);
        getContext().getFingerEventsSource().addListener(this);
        getContext().getViewFacade().injectKeyPress((byte) this.keyCode.getValue());
    }

    @Override // com.eltechs.axs.finiteStateMachine.FSMState
    public void notifyBecomeInactive() {
        getContext().getFingerEventsSource().removeListener(this);
    }

    @Override // com.eltechs.axs.TouchEventAdapter
    public void notifyReleased(Finger finger, List<Finger> list) {
        getContext().getViewFacade().injectKeyRelease((byte) this.keyCode.getValue());
        sendEvent(GESTURE_COMPLETED);
    }

    @Override // com.eltechs.axs.TouchEventAdapter
    public void notifyMovedOut(Finger finger, List<Finger> list) {
        getContext().getViewFacade().injectKeyRelease((byte) this.keyCode.getValue());
        sendEvent(GESTURE_COMPLETED);
    }
}
