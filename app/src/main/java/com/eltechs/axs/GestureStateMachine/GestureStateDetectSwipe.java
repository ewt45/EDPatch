package com.eltechs.axs.GestureStateMachine;

import com.eltechs.axs.Finger;
import com.eltechs.axs.finiteStateMachine.FSMEvent;
import com.eltechs.axs.helpers.Assert;

/* loaded from: classes.dex */
public class GestureStateDetectSwipe extends AbstractGestureFSMState {
    private final double coordsRatioThreshold;
    public static FSMEvent SWIPE_UP = new FSMEvent();
    public static FSMEvent SWIPE_DOWN = new FSMEvent();
    public static FSMEvent SWIPE_LEFT = new FSMEvent();
    public static FSMEvent SWIPE_RIGHT = new FSMEvent();
    public static FSMEvent NOT_SWIPE = new FSMEvent();

    @Override // com.eltechs.axs.finiteStateMachine.FSMState
    public void notifyBecomeInactive() {
    }

    public GestureStateDetectSwipe(GestureContext gestureContext, double coordsRatioThreshold) {
        super(gestureContext);
        this.coordsRatioThreshold = coordsRatioThreshold;
    }

    @Override // com.eltechs.axs.finiteStateMachine.FSMState
    public void notifyBecomeActive() {
        Assert.state(getContext().getFingers().size() == 1);
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
