package com.example.datainsert.exagear.controls.axs.GestureStateMachine;

import com.eltechs.axs.Finger;
import com.eltechs.axs.GeometryHelpers;
import com.eltechs.axs.GestureStateMachine.AbstractGestureFSMState;
import com.eltechs.axs.GestureStateMachine.GestureContext;
import com.eltechs.axs.GestureStateMachine.PointerContext;
import com.eltechs.axs.GuestAppActionAdapters.DragAndDropAdapter;
import com.eltechs.axs.GuestAppActionAdapters.PressAndHoldMouseClickAdapter;
import com.eltechs.axs.GuestAppActionAdapters.SimpleDragAndDropAdapter;
import com.eltechs.axs.TouchEventAdapter;
import com.eltechs.axs.finiteStateMachine.FSMEvent;
import com.eltechs.axs.xserver.Pointer;
import com.example.datainsert.exagear.controls.axs.GuestAppActionAdapters.OffsetMouseAdapter;
import com.example.datainsert.exagear.controls.axs.GuestAppActionAdapters.RelativeMouseMoveCstmSpdAdapter;

import java.util.List;

/**
 * 可以设置手指个数变化时是否上报
 */
public class State2FDragNDrop extends AbstractGestureFSMState implements TouchEventAdapter {
    public static FSMEvent GESTURE_COMPLETED = new FSMEvent("GESTURE_COMPLETED") ;
    public static FSMEvent FINGER_TOUCHED = new FSMEvent("FINGER_TOUCHED") ;
    public static FSMEvent FINGER_RELEASED = new FSMEvent("FINGER_RELEASED") ;
    private final DragAndDropAdapter adapter;
    private final boolean reportFingerNumChange;
    private Finger f;
    private final float moveThreshold;
    private boolean moveThresholdExceeded;
    final PointerContext pointerContext;
    private final boolean useMoveThreshold;

    public State2FDragNDrop(
            GestureContext gestureContext, DragAndDropAdapter dragAndDropAdapter, PointerContext pointerContext,
            boolean reportFingerNumChange, float moveThreshold) {
        super(gestureContext);
        this.pointerContext = pointerContext;

        this.adapter = dragAndDropAdapter;
        this.reportFingerNumChange = reportFingerNumChange;
        this.moveThreshold = moveThreshold;
        this.useMoveThreshold = moveThreshold != 0.0f;
    }

    @Override // com.eltechs.axs.finiteStateMachine.FSMState
    public void notifyBecomeActive() {
        getContext().getFingerEventsSource().addListener(this);
        if (getContext().getFingers().size() == 1) {
            this.f = getContext().getFingers().get(0);
        } else {
            this.f = getContext().getFingers().get(1);
        }
        this.adapter.start(this.f.getXWhenFirstTouched(), this.f.getYWhenFirstTouched());
        this.moveThresholdExceeded = false;
    }

    @Override // com.eltechs.axs.finiteStateMachine.FSMState
    public void notifyBecomeInactive() {
        this.f = null;
        getContext().getFingerEventsSource().removeListener(this);
    }

    @Override // com.eltechs.axs.TouchEventAdapter
    public void notifyMoved(Finger finger, List<Finger> list) {
        if (finger == this.f) {
            if (!this.useMoveThreshold) {
                this.adapter.move(this.f.getX(), this.f.getY());
                return;
            }
            if (!this.moveThresholdExceeded && GeometryHelpers.distance(this.f.getX(), this.f.getY(), this.f.getXWhenFirstTouched(), this.f.getYWhenFirstTouched()) > this.moveThreshold) {
                this.moveThresholdExceeded = true;
            }
            if (!this.moveThresholdExceeded) {
                return;
            }
            this.adapter.move(this.f.getX(), this.f.getY());
        }
    }

    @Override // com.eltechs.axs.TouchEventAdapter
    public void notifyReleased(Finger finger, List<Finger> list) {
        if (reportFingerNumChange) {
            this.adapter.stop(finger.getX(), finger.getY());
            sendEvent(FINGER_RELEASED);
        }else if (list.isEmpty()) {
            this.adapter.stop(finger.getX(), finger.getY());
            sendEvent(GESTURE_COMPLETED);
        }
    }

    @Override // com.eltechs.axs.TouchEventAdapter
    public void notifyMovedOut(Finger finger, List<Finger> list) {
        notifyReleased(finger, list);
    }

    @Override // com.eltechs.axs.TouchEventAdapter
    public void notifyTouched(Finger finger, List<Finger> list) {
        if (this.reportFingerNumChange) {
            //不要取消了，不然三指触屏的时候会触发取消，弹窗菜单和右键同时触发了
//            this.adapter.cancel(this.f.getX(), this.f.getY());
            this.adapter.stop(finger.getX(), finger.getY());
            sendEvent(FINGER_TOUCHED);
        }
    }

    @Override // com.eltechs.axs.TouchEventAdapter
    public void notifyMovedIn(Finger finger, List<Finger> list) {
        notifyTouched(finger, list);
    }


    public static class SimpleBuilder{
        public State2FDragNDrop create(GestureContext gestureContext, PointerContext pointerContext, boolean reportFingerNumChange,boolean isLeft){
            int pressBtn = isLeft?Pointer.BUTTON_LEFT:Pointer.BUTTON_RIGHT;
            int cancelBtn = isLeft?Pointer.BUTTON_RIGHT:Pointer.BUTTON_LEFT;
            return new State2FDragNDrop(
                    gestureContext,
                    new SimpleDragAndDropAdapter(
                            new RelativeMouseMoveCstmSpdAdapter(
                                    new OffsetMouseAdapter(gestureContext),
                                    gestureContext.getViewFacade(), gestureContext.getHostView()),
                            new PressAndHoldMouseClickAdapter(gestureContext.getPointerReporter(), pressBtn),
//                            ()->{}
                            () -> gestureContext.getPointerReporter().click(cancelBtn, 50)//用于取消移动的按键，默认是左键拖拽 所以取消是右键
                    ),
                    pointerContext,
                    reportFingerNumChange,
                    0.0f);
        }
    }
}
