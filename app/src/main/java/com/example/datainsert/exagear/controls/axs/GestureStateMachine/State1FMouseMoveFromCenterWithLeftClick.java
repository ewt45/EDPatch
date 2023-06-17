package com.example.datainsert.exagear.controls.axs.GestureStateMachine;

import com.eltechs.axs.Finger;
import com.eltechs.axs.GestureStateMachine.GestureContext;
import com.eltechs.axs.GestureStateMachine.PointerContext;
import com.eltechs.axs.GuestAppActionAdapters.MouseMoveAdapter;
import com.eltechs.axs.PointerEventReporter;
import com.eltechs.axs.xserver.Pointer;
import com.example.datainsert.exagear.controls.axs.GuestAppActionAdapters.OffsetMouseFromCenterAdapter;
import com.example.datainsert.exagear.controls.axs.GuestAppActionAdapters.RelativeMouseMoveCstmSpdAdapter;

import java.util.List;

/**
 * 相对移动，起始时鼠标定位到中心，按下左键，结束时松开左键
 */
public class State1FMouseMoveFromCenterWithLeftClick extends State1FMouseMove{
    private PointerEventReporter pointerEventReporter;
    public State1FMouseMoveFromCenterWithLeftClick(GestureContext gsContext, PointerContext ptCtxt, MouseMoveAdapter msMoveAdapter, boolean reportIfNewF, PointerEventReporter pointerReporter) {
        super(gsContext, ptCtxt, msMoveAdapter, reportIfNewF);
        this.pointerEventReporter = pointerReporter;
    }

    @Override
    public void notifyBecomeActive() {
        super.notifyBecomeActive();
        pointerEventReporter.buttonPressed(Pointer.BUTTON_LEFT);

    }

    @Override
    public void notifyReleased(Finger finger, List<Finger> list) {
        pointerEventReporter.buttonReleased(Pointer.BUTTON_LEFT);

        super.notifyReleased(finger, list);
    }

    public static class SimpleBuilder {
        public State1FMouseMoveFromCenterWithLeftClick create(GestureContext gestureContext, PointerContext pointerContext, boolean reportIfNewF) {

            return new State1FMouseMoveFromCenterWithLeftClick(gestureContext, pointerContext,
                    new RelativeMouseMoveCstmSpdAdapter(
//                            isViewport ? new ViewportMouseAdapter(gestureContext):
                            new OffsetMouseFromCenterAdapter(gestureContext),
//                             new SimpleMouseMoveAdapter(gestureContext.getPointerReporter()),
                            gestureContext.getViewFacade(), gestureContext.getHostView()),
                    reportIfNewF,gestureContext.getPointerReporter());
        }
    }
}
