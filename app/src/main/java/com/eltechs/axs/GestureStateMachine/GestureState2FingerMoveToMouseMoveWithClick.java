//package com.eltechs.axs.GestureStateMachine;
//
//import com.eltechs.axs.Finger;
//import com.eltechs.axs.GestureStateMachine.PointerContext;
//import com.eltechs.axs.GuestAppActionAdapters.MouseClickAdapter;
//import com.eltechs.axs.GuestAppActionAdapters.MouseMoveAdapter;
//import com.eltechs.axs.TouchEventAdapter;
//import com.eltechs.axs.finiteStateMachine.FSMEvent;
//import com.eltechs.axs.helpers.Assert;
//import java.util.List;
//
///* loaded from: classes.dex */
//public class GestureState2FingerMoveToMouseMoveWithClick extends AbstractGestureFSMState implements TouchEventAdapter {
//    private final MouseClickAdapter clickAdapter;
//    private final MouseMoveAdapter moveAdapter;
//    private PointerContext pointerContext;
//    public static FSMEvent FINGER_MOVED_IN = new FSMEvent() { // from class: com.eltechs.axs.GestureStateMachine.GestureState2FingerMoveToMouseMoveWithClick.1_fix
//    };
//    public static FSMEvent FINGER_MOVED_OUT = new FSMEvent() { // from class: com.eltechs.axs.GestureStateMachine.GestureState2FingerMoveToMouseMoveWithClick.2_fix
//    };
//    public static FSMEvent FINGER_TOUCHED = new FSMEvent() { // from class: com.eltechs.axs.GestureStateMachine.GestureState2FingerMoveToMouseMoveWithClick.3_fix
//    };
//    public static FSMEvent FINGER_RELEASED = new FSMEvent() { // from class: com.eltechs.axs.GestureStateMachine.GestureState2FingerMoveToMouseMoveWithClick.4_fix
//    };
//
//    public GestureState2FingerMoveToMouseMoveWithClick(GestureContext gestureContext, PointerContext pointerContext, MouseMoveAdapter mouseMoveAdapter, MouseClickAdapter mouseClickAdapter) {
//        super(gestureContext);
//        this.pointerContext = pointerContext;
//        this.moveAdapter = mouseMoveAdapter;
//        this.clickAdapter = mouseClickAdapter;
//    }
//
//    @Override // com.eltechs.axs.finiteStateMachine.FSMState
//    public void notifyBecomeActive() {
//        getContext().getFingerEventsSource().addListener(this);
//        List<Finger> fingers = getContext().getFingers();
//        Assert.state(fingers.size() == 2);
//        this.moveAdapter.prepareMoving((fingers.get(0).getX() + fingers.get(1).getX()) / 2.0f, (fingers.get(0).getY() + fingers.get(1).getY()) / 2.0f);
//        this.clickAdapter.click();
//    }
//
//    @Override // com.eltechs.axs.finiteStateMachine.FSMState
//    public void notifyBecomeInactive() {
//        getContext().getFingerEventsSource().removeListener(this);
//    }
//
//    @Override // com.eltechs.axs.TouchEventAdapter
//    public void notifyMoved(Finger finger, List list) {
//        this.moveAdapter.moveTo((((Finger) list.get(0)).getX() + ((Finger) list.get(1)).getX()) / 2.0f, (((Finger) list.get(0)).getY() + ((Finger) list.get(1)).getY()) / 2.0f);
//        this.pointerContext.setLastMoveMethod(PointerContext.MoveMethod.AIM);
//    }
//
//    @Override // com.eltechs.axs.TouchEventAdapter
//    public void notifyMovedIn(Finger finger, List list) {
//        sendEvent(FINGER_MOVED_IN);
//    }
//
//    @Override // com.eltechs.axs.TouchEventAdapter
//    public void notifyMovedOut(Finger finger, List list) {
//        sendEvent(FINGER_MOVED_OUT);
//    }
//
//    @Override // com.eltechs.axs.TouchEventAdapter
//    public void notifyReleased(Finger finger, List list) {
//        this.clickAdapter.finalizeClick();
//        sendEvent(FINGER_RELEASED);
//    }
//
//    @Override // com.eltechs.axs.TouchEventAdapter
//    public void notifyTouched(Finger finger, List<Finger> list) {
//        this.clickAdapter.finalizeClick();
//        sendEvent(FINGER_TOUCHED);
//    }
//}
