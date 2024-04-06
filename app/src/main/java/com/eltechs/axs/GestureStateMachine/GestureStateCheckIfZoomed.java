package com.eltechs.axs.GestureStateMachine;

import com.eltechs.axs.finiteStateMachine.FSMEvent;

/* loaded from: classes.dex */
public class GestureStateCheckIfZoomed extends AbstractGestureFSMState {
    public static FSMEvent ZOOM_ON = new FSMEvent();
    public static FSMEvent ZOOM_OFF = new FSMEvent();

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
