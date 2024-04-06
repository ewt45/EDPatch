package com.eltechs.axs.GestureStateMachine;

import com.eltechs.axs.GestureStateMachine.GestureMouseMode;
import com.eltechs.axs.finiteStateMachine.FSMEvent;

/* loaded from: classes.dex */
public class GestureStateCheckMouseMode extends AbstractGestureFSMState {
    public static FSMEvent MOUSE_MODE_LEFT = new FSMEvent();
    public static FSMEvent MOUSE_MODE_RIGHT = new FSMEvent();
    private final GestureMouseMode mouseMode;

    @Override // com.eltechs.axs.finiteStateMachine.FSMState
    public void notifyBecomeInactive() {
    }

    public GestureStateCheckMouseMode(GestureContext gestureContext, GestureMouseMode gestureMouseMode) {
        super(gestureContext);
        this.mouseMode = gestureMouseMode;
    }

    @Override // com.eltechs.axs.finiteStateMachine.FSMState
    public void notifyBecomeActive() {
        sendEvent(this.mouseMode.getState() == GestureMouseMode.MouseModeState.MOUSE_MODE_RIGHT
                ? MOUSE_MODE_RIGHT
                : MOUSE_MODE_LEFT);
    }
}
