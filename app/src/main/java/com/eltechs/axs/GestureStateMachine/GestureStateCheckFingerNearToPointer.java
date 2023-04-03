package com.eltechs.axs.GestureStateMachine;

import android.graphics.PointF;
import com.eltechs.axs.Finger;
import com.eltechs.axs.GeometryHelpers;
import com.eltechs.axs.finiteStateMachine.FSMEvent;
import com.eltechs.axs.geom.Point;
import com.eltechs.axs.helpers.Assert;
import com.eltechs.axs.widgets.viewOfXServer.TransformationHelpers;

/* loaded from: classes.dex */
public class GestureStateCheckFingerNearToPointer extends AbstractGestureFSMState {
    private final double distThreshold;
    private final boolean isTwoFingersAllowed;
    public static FSMEvent NEAR = new FSMEvent() { // from class: com.eltechs.axs.GestureStateMachine.GestureStateCheckFingerNearToPointer.1
    };
    public static FSMEvent FAR = new FSMEvent() { // from class: com.eltechs.axs.GestureStateMachine.GestureStateCheckFingerNearToPointer.2
    };

    @Override // com.eltechs.axs.finiteStateMachine.FSMState
    public void notifyBecomeInactive() {
    }

    public GestureStateCheckFingerNearToPointer(GestureContext gestureContext, double d, boolean z) {
        super(gestureContext);
        this.distThreshold = d;
        this.isTwoFingersAllowed = z;
    }

    @Override // com.eltechs.axs.finiteStateMachine.FSMState
    public void notifyBecomeActive() {
        PointF center;
        Point pointerLocation = getContext().getViewFacade().getPointerLocation();
        float[] fArr = {pointerLocation.x, pointerLocation.y};
        TransformationHelpers.mapPoints(getContext().getHostView().getXServerToViewTransformationMatrix(), fArr);
        if (!this.isTwoFingersAllowed) {
            Assert.state(getContext().getFingers().size() < 2);
        } else {
            Assert.state(getContext().getFingers().size() > 0 && getContext().getFingers().size() <= 2);
        }
        if (getContext().getFingers().size() == 0) {
            Finger finger = getContext().getTouchArea().getLastFingerAction().getFinger();
            center = new PointF(finger.getXWhenFirstTouched(), finger.getYWhenFirstTouched());
        } else if (getContext().getFingers().size() == 1) {
            center = new PointF(getContext().getFingers().get(0).getXWhenFirstTouched(), getContext().getFingers().get(0).getYWhenFirstTouched());
        } else {
            center = getContext().getFingers().size() == 2 ? GeometryHelpers.center(new PointF(getContext().getFingers().get(0).getXWhenFirstTouched(), getContext().getFingers().get(0).getYWhenFirstTouched()), new PointF(getContext().getFingers().get(1).getXWhenFirstTouched(), getContext().getFingers().get(1).getYWhenFirstTouched())) : null;
        }
        if (GeometryHelpers.distance(fArr[0], fArr[1], center.x, center.y) < this.distThreshold) {
            sendEvent(NEAR);
        } else {
            sendEvent(FAR);
        }
    }
}
