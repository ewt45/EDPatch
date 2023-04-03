package com.eltechs.axs.GestureStateMachine;

import com.eltechs.axs.Finger;
import com.eltechs.axs.GeometryHelpers;
import com.eltechs.axs.finiteStateMachine.FSMEvent;
import com.eltechs.axs.helpers.Assert;
import java.util.List;

/* loaded from: classes.dex */
public class GestureStateCheckShortZoom extends AbstractGestureFSMState {
    private static final int zoomActivateThresold = 400;
    public static FSMEvent ZOOM_SHORT = new FSMEvent() { // from class: com.eltechs.axs.GestureStateMachine.GestureStateCheckShortZoom.1
    };
    public static FSMEvent ZOOM_LONG = new FSMEvent() { // from class: com.eltechs.axs.GestureStateMachine.GestureStateCheckShortZoom.2
    };

    @Override // com.eltechs.axs.finiteStateMachine.FSMState
    public void notifyBecomeInactive() {
    }

    private static float getDistanceBetweenFingers(List<Finger> list) {
        Assert.state(list.size() == 2);
        Finger finger = list.get(0);
        Finger finger2 = list.get(1);
        return GeometryHelpers.distance(finger.getX(), finger.getY(), finger2.getX(), finger2.getY());
    }

    public GestureStateCheckShortZoom(GestureContext gestureContext) {
        super(gestureContext);
    }

    @Override // com.eltechs.axs.finiteStateMachine.FSMState
    public void notifyBecomeActive() {
        List<Finger> fingers = getContext().getFingers();
        if (getContext().getZoomController().isZoomed() || getDistanceBetweenFingers(fingers) < 400.0f) {
            sendEvent(ZOOM_SHORT);
        } else {
            sendEvent(ZOOM_LONG);
        }
    }
}
