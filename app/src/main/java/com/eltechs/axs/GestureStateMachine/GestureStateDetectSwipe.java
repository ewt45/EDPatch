package com.eltechs.axs.GestureStateMachine;

import com.eltechs.axs.Finger;
import com.eltechs.axs.finiteStateMachine.FSMEvent;
import com.eltechs.axs.helpers.Assert;

/* loaded from: classes.dex */
public class GestureStateDetectSwipe extends AbstractGestureFSMState {
    private final double coordsRatioThreshold;
    public static FSMEvent SWIPE_UP = new FSMEvent() { // from class: com.eltechs.axs.GestureStateMachine.GestureStateDetectSwipe.1
    };
    public static FSMEvent SWIPE_DOWN = new FSMEvent() { // from class: com.eltechs.axs.GestureStateMachine.GestureStateDetectSwipe.2
    };
    public static FSMEvent SWIPE_LEFT = new FSMEvent() { // from class: com.eltechs.axs.GestureStateMachine.GestureStateDetectSwipe.3
    };
    public static FSMEvent SWIPE_RIGHT = new FSMEvent() { // from class: com.eltechs.axs.GestureStateMachine.GestureStateDetectSwipe.4
    };
    public static FSMEvent NOT_SWIPE = new FSMEvent() { // from class: com.eltechs.axs.GestureStateMachine.GestureStateDetectSwipe.5
    };

    @Override // com.eltechs.axs.finiteStateMachine.FSMState
    public void notifyBecomeInactive() {
    }

    public GestureStateDetectSwipe(GestureContext gestureContext, double d) {
        super(gestureContext);
        this.coordsRatioThreshold = d;
    }

    @Override // com.eltechs.axs.finiteStateMachine.FSMState
    public void notifyBecomeActive() {
        boolean z = true;
        if (getContext().getFingers().size() != 1) {
            z = false;
        }
        Assert.state(z);
        Finger finger = getContext().getFingers().get(0);
        float x = finger.getX() - finger.getXWhenFirstTouched();
        float y = finger.getY() - finger.getYWhenFirstTouched();
        if (Math.abs(x) > Math.abs(y) * this.coordsRatioThreshold) {
            sendEvent(x > 0.0f ? SWIPE_RIGHT : SWIPE_LEFT);
        } else if (Math.abs(y) > Math.abs(x) * this.coordsRatioThreshold) {
            sendEvent(y > 0.0f ? SWIPE_DOWN : SWIPE_UP);
        } else {
            sendEvent(NOT_SWIPE);
        }
    }
}
