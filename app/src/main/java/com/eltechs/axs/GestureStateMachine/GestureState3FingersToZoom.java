package com.eltechs.axs.GestureStateMachine;

import com.eltechs.axs.Finger;
import com.eltechs.axs.GeometryHelpers;
import com.eltechs.axs.TouchEventAdapter;
import com.eltechs.axs.finiteStateMachine.FSMEvent;
import com.eltechs.axs.helpers.Assert;
import com.eltechs.axs.helpers.InfiniteTimer;
import com.eltechs.axs.widgets.viewOfXServer.XZoomController;
import java.util.List;

/* loaded from: classes.dex */
public class GestureState3FingersToZoom extends AbstractGestureFSMState implements TouchEventAdapter {
    private static final int timerPeriodMs = 40;
    private float distance;
    private Finger mainFinger;
    private InfiniteTimer timer;
    public static FSMEvent FINGER_TOUCHED = new FSMEvent() ;
    public static FSMEvent FINGER_RELEASED = new FSMEvent() ;
    public static FSMEvent FINGER_MOVED_IN = new FSMEvent() ;
    public static FSMEvent FINGER_MOVED_OUT = new FSMEvent() ;

    @Override // com.eltechs.axs.TouchEventAdapter
    public void notifyMoved(Finger finger, List<Finger> list) {
    }

    public GestureState3FingersToZoom(GestureContext gestureContext) {
        super(gestureContext);
    }

    @Override // com.eltechs.axs.finiteStateMachine.FSMState
    public void notifyBecomeActive() {
        getContext().getFingerEventsSource().addListener(this);
        this.timer = new InfiniteTimer(timerPeriodMs) { // from class: com.eltechs.axs.GestureStateMachine.GestureState3FingersToZoom.5
            @Override // android.os.CountDownTimer
            public void onTick(long j) {
                if (GestureState3FingersToZoom.this.getContext().getMachine().isActiveState(GestureState3FingersToZoom.this)) {
                    GestureState3FingersToZoom.this.notifyTimer();
                }
            }
        };
        this.timer.start();
        List<Finger> fingers = getContext().getFingers();
        Assert.state(fingers.size() == 3);
        this.distance = getDistanceBetweenFingers(fingers);
        this.mainFinger = fingers.get(0);
        getContext().getZoomController().setAnchorBoth(this.mainFinger.getXWhenFingerCountLastChanged(), this.mainFinger.getYWhenFingerCountLastChanged());
    }

    @Override // com.eltechs.axs.finiteStateMachine.FSMState
    public void notifyBecomeInactive() {
        this.distance = 0.0f;
        this.mainFinger = null;
        getContext().getFingerEventsSource().removeListener(this);
        this.timer.cancel();
    }

    /* JADX INFO: Access modifiers changed from: private */
    private void notifyTimer() {
        List<Finger> fingers = getContext().getFingers();
        Assert.state(fingers.size() == 3);
        XZoomController zoomController = getContext().getZoomController();
        float distanceBetweenFingers = getDistanceBetweenFingers(fingers);
        this.distance = distanceBetweenFingers;
        boolean isZoomed = zoomController.isZoomed();
        zoomController.insertZoomFactorChange(distanceBetweenFingers / this.distance);
        zoomController.refreshZoom();
        if (isZoomed != zoomController.isZoomed()) {
            zoomController.setAnchorBoth(this.mainFinger.getX(), this.mainFinger.getY());
        } else {
            zoomController.setAnchorHost(this.mainFinger.getX(), this.mainFinger.getY());
        }
        zoomController.refreshZoom();
    }

    private float getDistanceBetweenFingers(List<Finger> list) {
        Assert.state(list.size() == 3);
        Finger finger = list.get(0);
        Finger finger2 = list.get(1);
        Finger finger3 = list.get(2);
        return Math.max(GeometryHelpers.distance(finger.getX(), finger.getY(), finger2.getX(), finger2.getY()), GeometryHelpers.distance(finger.getX(), finger.getY(), finger3.getX(), finger3.getY()));
    }

    @Override // com.eltechs.axs.TouchEventAdapter
    public void notifyTouched(Finger finger, List<Finger> list) {
        sendEvent(FINGER_TOUCHED);
    }

    @Override // com.eltechs.axs.TouchEventAdapter
    public void notifyMovedIn(Finger finger, List<Finger> list) {
        sendEvent(FINGER_MOVED_IN);
    }

    @Override // com.eltechs.axs.TouchEventAdapter
    public void notifyReleased(Finger finger, List<Finger> list) {
        sendEvent(FINGER_RELEASED);
    }

    @Override // com.eltechs.axs.TouchEventAdapter
    public void notifyMovedOut(Finger finger, List<Finger> list) {
        sendEvent(FINGER_MOVED_OUT);
    }
}
