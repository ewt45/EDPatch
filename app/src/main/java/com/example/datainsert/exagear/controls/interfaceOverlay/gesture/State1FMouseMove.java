package com.example.datainsert.exagear.controls.interfaceOverlay.gesture;

import com.eltechs.axs.Finger;
import com.eltechs.axs.GestureStateMachine.AbstractGestureFSMState;
import com.eltechs.axs.GestureStateMachine.GestureContext;
import com.eltechs.axs.GestureStateMachine.PointerContext;
import com.eltechs.axs.GuestAppActionAdapters.MouseMoveAdapter;
import com.eltechs.axs.GuestAppActionAdapters.RelativeToCurrentPositionMouseMoveAdapter;
import com.eltechs.axs.GuestAppActionAdapters.SimpleMouseMoveAdapter;
import com.eltechs.axs.TouchEventAdapter;
import com.eltechs.axs.finiteStateMachine.FSMEvent;
import com.eltechs.axs.helpers.Assert;
import java.util.List;

/* loaded from: classes.dex */
public class State1FMouseMove extends AbstractGestureFSMState implements TouchEventAdapter {
    public static FSMEvent GESTURE_COMPLETED = new FSMEvent("GESTURE_COMPLETED") ;
    public static FSMEvent NEW_FINGER_TOUCHED = new FSMEvent("NEW_FINGER_TOUCHED") ;
    private Finger f;
    private final MouseMoveAdapter moveAdapter;
    private PointerContext pointerContext;
    private final boolean reportIfNewF;

    @Override // com.eltechs.axs.TouchEventAdapter
    public void notifyMovedIn(Finger finger, List<Finger> list) {
    }

    @Override // com.eltechs.axs.TouchEventAdapter
    public void notifyTouched(Finger finger, List<Finger> list) {
        if(reportIfNewF) sendEvent(NEW_FINGER_TOUCHED);

    }

    public State1FMouseMove(GestureContext gsContext, PointerContext ptCtxt, MouseMoveAdapter msMoveAdapter, boolean reportIfNewF) {
        super(gsContext);
        this.pointerContext = ptCtxt;
        this.moveAdapter = msMoveAdapter;
        this.reportIfNewF = reportIfNewF;
    }

    @Override // com.eltechs.axs.finiteStateMachine.FSMState
    public void notifyBecomeActive() {
        getContext().getFingerEventsSource().addListener(this);
        Assert.state(getContext().getFingers().size() == 1);
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

    public static class SimpleBuilder{
        public State1FMouseMove create(GestureContext gestureContext,PointerContext pointerContext,boolean reportIfNewF){
            return  new State1FMouseMove(gestureContext,pointerContext,
                    new RelativeToCurrentPositionMouseMoveAdapter(
                            new SimpleMouseMoveAdapter(gestureContext.getPointerReporter()),
                            gestureContext.getViewFacade(), gestureContext.getHostView()),reportIfNewF);
        }
    }

}
