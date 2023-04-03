package com.eltechs.axs.GestureStateMachine;

import com.eltechs.axs.Finger;
import com.eltechs.axs.GeometryHelpers;
import com.eltechs.axs.GuestAppActionAdapters.DragAndDropAdapter;
import com.eltechs.axs.TouchEventAdapter;
import com.eltechs.axs.finiteStateMachine.FSMEvent;
import java.util.List;

/* loaded from: classes.dex */
public class GestureState1FingerMoveToMouseDragAndDrop extends AbstractGestureFSMState implements TouchEventAdapter {
    public static FSMEvent GESTURE_COMPLETED = new FSMEvent() ;
    private final DragAndDropAdapter adapter;
    private final boolean cancelBy2ndFinger;
    private Finger f;
    private final float moveThreshold;
    private boolean moveThresholdExceeded;
    final PointerContext pointerContext;
    private final boolean useMoveThreshold;

    public GestureState1FingerMoveToMouseDragAndDrop(
            GestureContext gestureContext, DragAndDropAdapter dragAndDropAdapter, PointerContext pointerContext,
            boolean cancelBy2ndFinger, float moveThreshold) {
        super(gestureContext);
        this.pointerContext = pointerContext;
        this.adapter = dragAndDropAdapter;
        this.cancelBy2ndFinger = cancelBy2ndFinger;
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
        if (list.isEmpty()) {
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
        if (this.cancelBy2ndFinger) {
            this.adapter.cancel(this.f.getX(), this.f.getY());
            sendEvent(GESTURE_COMPLETED);
        }
    }

    @Override // com.eltechs.axs.TouchEventAdapter
    public void notifyMovedIn(Finger finger, List<Finger> list) {
        notifyTouched(finger, list);
    }
}
