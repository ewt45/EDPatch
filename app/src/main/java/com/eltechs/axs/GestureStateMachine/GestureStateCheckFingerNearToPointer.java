package com.eltechs.axs.GestureStateMachine;

import static com.eltechs.axs.GeometryHelpers.distance;

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
    public static FSMEvent NEAR = new FSMEvent();
    public static FSMEvent FAR = new FSMEvent();

    @Override // com.eltechs.axs.finiteStateMachine.FSMState
    public void notifyBecomeInactive() {
    }

    public GestureStateCheckFingerNearToPointer(GestureContext gestureContext, double distThreshold, boolean isTwoFingersAllowed) {
        super(gestureContext);
        this.distThreshold = distThreshold;
        this.isTwoFingersAllowed = isTwoFingersAllowed;
    }

    @Override // com.eltechs.axs.finiteStateMachine.FSMState
    public void notifyBecomeActive() {
        PointF fingerPos;
        Point pLoc = getContext().getViewFacade().getPointerLocation();
        float[] pointerPos = {pLoc.x, pLoc.y};
        TransformationHelpers.mapPoints(getContext().getHostView().getXServerToViewTransformationMatrix(), pointerPos);
        Assert.state(isTwoFingersAllowed
                ? getContext().getFingers().size() < 2
                : (!getContext().getFingers().isEmpty() && getContext().getFingers().size() <= 2));
        if (getContext().getFingers().isEmpty()) {
            Finger lastAction = getContext().getTouchArea().getLastFingerAction().getFinger();
            fingerPos = new PointF(lastAction.getXWhenFirstTouched(), lastAction.getYWhenFirstTouched());
        } else if (getContext().getFingers().size() == 1) {
            fingerPos = new PointF(getContext().getFingers().get(0).getXWhenFirstTouched(), getContext().getFingers().get(0).getYWhenFirstTouched());
        } else {
            PointF finger1Pos = new PointF(getContext().getFingers().get(0).getXWhenFirstTouched(), getContext().getFingers().get(0).getYWhenFirstTouched());
            PointF finger2Pos = new PointF(getContext().getFingers().get(1).getXWhenFirstTouched(), getContext().getFingers().get(1).getYWhenFirstTouched());
            fingerPos = GeometryHelpers.center(finger1Pos, finger2Pos);
        }

        sendEvent(distance(pointerPos[0], pointerPos[1], fingerPos.x, fingerPos.y) < this.distThreshold
                ? NEAR : FAR);
    }
}
