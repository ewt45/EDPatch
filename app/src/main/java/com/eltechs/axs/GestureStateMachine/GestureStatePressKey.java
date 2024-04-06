package com.eltechs.axs.GestureStateMachine;

import com.eltechs.axs.KeyCodesX;
import com.eltechs.axs.finiteStateMachine.FSMEvent;

/* loaded from: classes.dex */
public class GestureStatePressKey extends AbstractGestureFSMState {
    public static FSMEvent GESTURE_COMPLETED = new FSMEvent();
    private final KeyCodesX keyCode;

    @Override // com.eltechs.axs.finiteStateMachine.FSMState
    public void notifyBecomeInactive() {
    }

    public GestureStatePressKey(GestureContext gestureContext, KeyCodesX keyCodesX) {
        super(gestureContext);
        this.keyCode = keyCodesX;
    }

    @Override // com.eltechs.axs.finiteStateMachine.FSMState
    public void notifyBecomeActive() {
        getContext().getKeyboardReporter().reportKeys(this.keyCode);
        sendEvent(GESTURE_COMPLETED);
    }
}
