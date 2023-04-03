package com.eltechs.axs.GestureStateMachine;

import com.eltechs.axs.Finger;
import com.eltechs.axs.PointerMoveToKeyAdapter;
import com.eltechs.axs.TouchEventAdapter;
import com.eltechs.axs.finiteStateMachine.FSMEvent;
import com.eltechs.axs.helpers.Assert;
import java.util.List;

/* loaded from: classes.dex */
public class GestureStateFingersMoveToKey extends AbstractGestureFSMState implements TouchEventAdapter {
    public static FSMEvent GESTURE_COMPLETED = new FSMEvent() { // from class: com.eltechs.axs.GestureStateMachine.GestureStateFingersMoveToKey.1
    };
    private final PointerMoveToKeyAdapter leftAdapter;
    private Finger leftFinger;
    private final PointerMoveToKeyAdapter rightAdapter;
    private Finger rightFinger;

    public GestureStateFingersMoveToKey(GestureContext gestureContext, PointerMoveToKeyAdapter pointerMoveToKeyAdapter, PointerMoveToKeyAdapter pointerMoveToKeyAdapter2) {
        super(gestureContext);
        this.leftAdapter = pointerMoveToKeyAdapter;
        this.rightAdapter = pointerMoveToKeyAdapter2;
    }

    private boolean isFingerAtRightArea(Finger finger) {
        return finger.getX() > ((float) (getContext().getHostView().getWidth() / 2));
    }

    @Override // com.eltechs.axs.TouchEventAdapter
    public void notifyMoved(Finger finger, List<Finger> list) {
        if (finger == this.leftFinger) {
            this.leftAdapter.pointerMove(finger.getX(), finger.getY());
        } else if (finger != this.rightFinger) {
        } else {
            this.rightAdapter.pointerMove(finger.getX(), finger.getY());
        }
    }

    @Override // com.eltechs.axs.TouchEventAdapter
    public void notifyTouched(Finger finger, List<Finger> list) {
        switch (list.size()) {
            case 2:
                if (isFingerAtRightArea(finger)) {
                    this.rightFinger = finger;
                    this.rightAdapter.pointerEntered(this.rightFinger.getX(), this.rightFinger.getY());
                    return;
                }
                this.leftFinger = finger;
                this.leftAdapter.pointerEntered(this.leftFinger.getX(), this.leftFinger.getY());
                return;
            case 3:
                sendEvent(GESTURE_COMPLETED);
                return;
            default:
                Assert.unreachable();
                return;
        }
    }

    @Override // com.eltechs.axs.TouchEventAdapter
    public void notifyMovedIn(Finger finger, List<Finger> list) {
        notifyTouched(finger, list);
    }

    @Override // com.eltechs.axs.TouchEventAdapter
    public void notifyReleased(Finger finger, List<Finger> list) {
        switch (list.size()) {
            case 0:
                sendEvent(GESTURE_COMPLETED);
                return;
            case 1:
                if (finger == this.rightFinger) {
                    this.rightAdapter.pointerExited(this.rightFinger.getX(), this.rightFinger.getY());
                    this.rightFinger = null;
                    return;
                } else if (finger != this.leftFinger) {
                    return;
                } else {
                    this.leftAdapter.pointerExited(this.leftFinger.getX(), this.leftFinger.getY());
                    this.leftFinger = null;
                    return;
                }
            default:
                Assert.unreachable();
                return;
        }
    }

    @Override // com.eltechs.axs.TouchEventAdapter
    public void notifyMovedOut(Finger finger, List<Finger> list) {
        notifyReleased(finger, list);
    }

    @Override // com.eltechs.axs.finiteStateMachine.FSMState
    public void notifyBecomeActive() {
        getContext().getFingerEventsSource().addListener(this);
        boolean z = true;
        if (getContext().getFingers().size() != 1) {
            z = false;
        }
        Assert.state(z);
        Finger finger = getContext().getFingers().get(0);
        if (isFingerAtRightArea(finger)) {
            this.rightFinger = finger;
        } else {
            this.leftFinger = finger;
        }
        if (this.leftFinger != null) {
            this.leftAdapter.pointerEntered(this.leftFinger.getX(), this.leftFinger.getY());
        }
        if (this.rightFinger != null) {
            this.rightAdapter.pointerEntered(this.rightFinger.getX(), this.rightFinger.getY());
        }
    }

    @Override // com.eltechs.axs.finiteStateMachine.FSMState
    public void notifyBecomeInactive() {
        getContext().getFingerEventsSource().removeListener(this);
        if (this.leftFinger != null) {
            this.leftAdapter.pointerExited(this.leftFinger.getX(), this.leftFinger.getY());
        }
        if (this.rightFinger != null) {
            this.rightAdapter.pointerExited(this.rightFinger.getX(), this.rightFinger.getY());
        }
        this.leftFinger = null;
        this.rightFinger = null;
    }
}
