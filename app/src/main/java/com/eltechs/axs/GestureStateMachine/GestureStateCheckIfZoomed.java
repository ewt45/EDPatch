package com.eltechs.axs.GestureStateMachine;

import com.eltechs.axs.finiteStateMachine.FSMEvent;

/* loaded from: classes.dex */
public class GestureStateCheckIfZoomed extends AbstractGestureFSMState {
    public static FSMEvent ZOOM_ON = new FSMEvent() { // from class: com.eltechs.axs.GestureStateMachine.GestureStateCheckIfZoomed.1
    };
    public static FSMEvent ZOOM_OFF = new FSMEvent() { // from class: com.eltechs.axs.GestureStateMachine.GestureStateCheckIfZoomed.2
    };

    @Override // com.eltechs.axs.finiteStateMachine.FSMState
    public void notifyBecomeInactive() {
    }

    public GestureStateCheckIfZoomed(GestureContext gestureContext) {
        super(gestureContext);
    }

    @Override // com.eltechs.axs.finiteStateMachine.FSMState
    public void notifyBecomeActive() {
        sendEvent(getContext().getZoomController().isZoomed() ? ZOOM_ON : ZOOM_OFF);
    }
}
