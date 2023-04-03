package com.eltechs.axs.GestureStateMachine;

import com.eltechs.axs.Finger;
import com.eltechs.axs.GestureStateMachine.PointerContext;
import com.eltechs.axs.GuestAppActionAdapters.MouseClickAdapter;
import com.eltechs.axs.GuestAppActionAdapters.MouseMoveAdapter;
import com.eltechs.axs.TouchEventAdapter;
import com.eltechs.axs.finiteStateMachine.FSMEvent;
import com.eltechs.axs.helpers.Assert;
import java.util.List;

/* loaded from: classes.dex */
public class GestureState1FingerMoveToMouseMoveWithClick extends AbstractGestureFSMState implements TouchEventAdapter {
    public static FSMEvent GESTURE_COMPLETED = new FSMEvent() { // from class: com.eltechs.axs.GestureStateMachine.GestureState1FingerMoveToMouseMoveWithClick.1
    };
    private final MouseClickAdapter clickAdapter;
    private Finger f;
    private final MouseMoveAdapter moveAdapter;
    private PointerContext pointerContext;

    @Override // com.eltechs.axs.TouchEventAdapter
    public void notifyMovedIn(Finger finger, List<Finger> list) {
    }

    public GestureState1FingerMoveToMouseMoveWithClick(GestureContext gestureContext, PointerContext pointerContext, MouseMoveAdapter mouseMoveAdapter, MouseClickAdapter mouseClickAdapter) {
        super(gestureContext);
        this.pointerContext = pointerContext;
        this.moveAdapter = mouseMoveAdapter;
        this.clickAdapter = mouseClickAdapter;
    }

    @Override // com.eltechs.axs.finiteStateMachine.FSMState
    public void notifyBecomeActive() {
        getContext().getFingerEventsSource().addListener(this);
        boolean z = true;
        if (getContext().getFingers().size() != 1) {
            z = false;
        }
        Assert.state(z);
        this.f = getContext().getFingers().get(0);
        this.moveAdapter.prepareMoving(this.f.getXWhenFirstTouched(), this.f.getYWhenFirstTouched());
    }

    @Override // com.eltechs.axs.finiteStateMachine.FSMState
    public void notifyBecomeInactive() {
        this.f = null;
        getContext().getFingerEventsSource().removeListener(this);
    }

    @Override // com.eltechs.axs.TouchEventAdapter
    public void notifyMoved(Finger finger, List<Finger> list) {
        if (finger == this.f) {
            this.moveAdapter.moveTo(finger.getX(), finger.getY());
            this.pointerContext.setLastMoveMethod(PointerContext.MoveMethod.AIM);
        }
    }

    @Override // com.eltechs.axs.TouchEventAdapter
    public void notifyTouched(Finger finger, List<Finger> list) {
        if (list.size() == 2) {
            this.clickAdapter.click();
            this.clickAdapter.finalizeClick();
        }
    }

    @Override // com.eltechs.axs.TouchEventAdapter
    public void notifyReleased(Finger finger, List<Finger> list) {
        if (list.isEmpty()) {
            sendEvent(GESTURE_COMPLETED);
        }
    }

    @Override // com.eltechs.axs.TouchEventAdapter
    public void notifyMovedOut(Finger finger, List<Finger> list) {
        if (list.isEmpty()) {
            sendEvent(GESTURE_COMPLETED);
        }
    }
}
