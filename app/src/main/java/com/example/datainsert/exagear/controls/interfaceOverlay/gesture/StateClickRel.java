package com.example.datainsert.exagear.controls.interfaceOverlay.gesture;

import android.app.AlertDialog;

import com.eltechs.axs.Finger;
import com.eltechs.axs.GestureStateMachine.AbstractGestureFSMState;
import com.eltechs.axs.GestureStateMachine.GestureContext;
import com.eltechs.axs.GestureStateMachine.GestureStateClickToFingerFirstCoords;
import com.eltechs.axs.GestureStateMachine.PointerContext;
import com.eltechs.axs.GuestAppActionAdapters.MousePointAndClickAdapter;
import com.eltechs.axs.GuestAppActionAdapters.PressAndReleaseMouseClickAdapter;
import com.eltechs.axs.PointerEventReporter;
import com.eltechs.axs.finiteStateMachine.FSMEvent;
import com.example.datainsert.exagear.controls.interfaceOverlay.GuestAppActionAdapters.RelativeMouseClickAdapter;

/* loaded from: classes.dex */
public class StateClickRel extends AbstractGestureFSMState {
    public static FSMEvent GESTURE_COMPLETED = new FSMEvent("GESTURE_COMPLETED") ;
    private final MousePointAndClickAdapter clicker;

    public StateClickRel(GestureContext gestureContext, MousePointAndClickAdapter mousePointAndClickAdapter) {
        super(gestureContext);
        this.clicker = mousePointAndClickAdapter;
    }

    @Override // com.eltechs.axs.finiteStateMachine.FSMState
    public void notifyBecomeInactive() {
    }

    @Override // com.eltechs.axs.finiteStateMachine.FSMState
    public void notifyBecomeActive() {
        Finger finger = getContext().getTouchArea().getLastFingerAction().getFinger();
        this.clicker.click(finger.getXWhenFirstTouched(), finger.getYWhenFirstTouched());
        this.clicker.finalizeClick(finger.getXWhenFirstTouched(), finger.getYWhenFirstTouched());
        sendEvent(GESTURE_COMPLETED);
    }

    public static class SimpleBuilder {

        public SimpleBuilder() {

        }

        public StateClickRel create(GestureContext gestureContext, int buttonCode, int mouseActionSleepMs, PointerContext pointerContext) {
            return new StateClickRel(
                    gestureContext,
                    new RelativeMouseClickAdapter(
                            new PressAndReleaseMouseClickAdapter(gestureContext.getPointerReporter(), buttonCode, mouseActionSleepMs),
                            pointerContext));
        }
    }
}
