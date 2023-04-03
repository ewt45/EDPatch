package com.eltechs.axs.GestureStateMachine;

import com.eltechs.axs.Finger;
import com.eltechs.axs.GestureStateMachine.PointerContext;
import com.eltechs.axs.GuestAppActionAdapters.MouseMoveAdapter;
import com.eltechs.axs.finiteStateMachine.FSMEvent;

/* loaded from: classes.dex */
public class GestureStateMouseWarpToFingerLastCoords extends AbstractGestureFSMState {
    public static FSMEvent GESTURE_COMPLETED = new FSMEvent() { // from class: com.eltechs.axs.GestureStateMachine.GestureStateMouseWarpToFingerLastCoords.1
    };
    private final MouseMoveAdapter mover;
    private final PointerContext pointerContext;

    @Override // com.eltechs.axs.finiteStateMachine.FSMState
    public void notifyBecomeInactive() {
    }

    public GestureStateMouseWarpToFingerLastCoords(GestureContext gestureContext, MouseMoveAdapter mouseMoveAdapter, PointerContext pointerContext) {
        super(gestureContext);
        this.mover = mouseMoveAdapter;
        this.pointerContext = pointerContext;
    }

    @Override // com.eltechs.axs.finiteStateMachine.FSMState
    public void notifyBecomeActive() {
        Finger finger = getContext().getTouchArea().getLastFingerAction().getFinger();
        this.mover.prepareMoving(finger.getX(), finger.getY());
        this.mover.moveTo(finger.getX(), finger.getY());
        this.pointerContext.setLastMoveMethod(PointerContext.MoveMethod.AIM);
        sendEvent(GESTURE_COMPLETED);
    }
}
