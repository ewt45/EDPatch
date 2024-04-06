package com.eltechs.axs.GestureStateMachine;

import com.eltechs.axs.Finger;
import com.eltechs.axs.GeometryHelpers;
import com.eltechs.axs.finiteStateMachine.FSMEvent;
import com.eltechs.axs.helpers.Assert;
import java.util.List;

/* loaded from: classes.dex */
public class GestureStateCheckShortZoom extends AbstractGestureFSMState {
    private static final int zoomActivateThresold = 400;
    public static FSMEvent ZOOM_SHORT = new FSMEvent();
    public static FSMEvent ZOOM_LONG = new FSMEvent();

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
        sendEvent(getContext().getZoomController().isZoomed() || getDistanceBetweenFingers(fingers) < zoomActivateThresold
                ? ZOOM_SHORT
                : ZOOM_LONG);
    }
}
