package com.example.datainsert.exagear.controls.interfaceOverlay.gesture;

import com.eltechs.axs.GestureStateMachine.GestureContext;
import com.eltechs.axs.GestureStateMachine.GestureState1FingerMoveToMouseDragAndDrop;
import com.eltechs.axs.GestureStateMachine.PointerContext;
import com.eltechs.axs.GuestAppActionAdapters.DragAndDropAdapter;
import com.eltechs.axs.GuestAppActionAdapters.PressAndHoldMouseClickAdapter;
import com.eltechs.axs.GuestAppActionAdapters.SimpleDragAndDropAdapter;
import com.example.datainsert.exagear.controls.interfaceOverlay.GuestAppActionAdapters.OffsetMouseAdapter;
import com.example.datainsert.exagear.controls.interfaceOverlay.GuestAppActionAdapters.RelativeMouseMoveCstmSpdAdapter;

/**
 * 和GestureState1FingerMoveToMouseDragAndDrop一致，
 */
public class State1FDragNDropRel extends GestureState1FingerMoveToMouseDragAndDrop {
    public State1FDragNDropRel(GestureContext gestureContext, DragAndDropAdapter dragAndDropAdapter, PointerContext pointerContext, boolean cancelBy2ndFinger, float moveThreshold) {
        super(gestureContext, dragAndDropAdapter, pointerContext, cancelBy2ndFinger, moveThreshold);
    }


    public static class SimpleBuilder{
        public State1FDragNDropRel create(GestureContext gestureContext,PointerContext pointerContext){
            return new State1FDragNDropRel(
                    gestureContext,
                    new SimpleDragAndDropAdapter(
                            new RelativeMouseMoveCstmSpdAdapter(
                                    new OffsetMouseAdapter(gestureContext),
                                    gestureContext.getViewFacade(), gestureContext.getHostView()),
                            new PressAndHoldMouseClickAdapter(gestureContext.getPointerReporter(), 1),
                            () -> gestureContext.getPointerReporter().click(3, 50)//3
                    ),
                    pointerContext, false, 0.0f);
        }
    }
}
