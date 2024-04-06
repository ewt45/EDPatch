package com.eltechs.axs.GestureStateMachine;

import com.eltechs.axs.Finger;
import com.eltechs.axs.GuestAppActionAdapters.MousePointAndClickAdapter;
import com.eltechs.axs.finiteStateMachine.FSMEvent;

/* loaded from: classes.dex */
public class GestureStateClickToFingerFirstCoords extends AbstractGestureFSMState {
    public static FSMEvent GESTURE_COMPLETED = new FSMEvent();
    private final MousePointAndClickAdapter clicker;

    @Override // com.eltechs.axs.finiteStateMachine.FSMState
    public void notifyBecomeInactive() {
    }

    public GestureStateClickToFingerFirstCoords(GestureContext gestureContext, MousePointAndClickAdapter mousePointAndClickAdapter) {
        super(gestureContext);
        this.clicker = mousePointAndClickAdapter;
    }

    @Override // com.eltechs.axs.finiteStateMachine.FSMState
    public void notifyBecomeActive() {
        Finger finger = getContext().getTouchArea().getLastFingerAction().getFinger();
        this.clicker.click(finger.getXWhenFirstTouched(), finger.getYWhenFirstTouched());
        this.clicker.finalizeClick(finger.getXWhenFirstTouched(), finger.getYWhenFirstTouched());
        sendEvent(GESTURE_COMPLETED);
    }
}
