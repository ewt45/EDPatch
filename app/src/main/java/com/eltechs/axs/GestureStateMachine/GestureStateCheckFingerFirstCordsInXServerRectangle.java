package com.eltechs.axs.GestureStateMachine;

import com.eltechs.axs.Finger;
import com.eltechs.axs.finiteStateMachine.FSMEvent;
import com.eltechs.axs.geom.Rectangle;
import com.eltechs.axs.widgets.viewOfXServer.TransformationHelpers;

/* loaded from: classes.dex */
public class GestureStateCheckFingerFirstCordsInXServerRectangle extends AbstractGestureFSMState {
    public static FSMEvent INSIDE = new FSMEvent();
    public static FSMEvent OUTSIDE = new FSMEvent();
    private final Rectangle rect;

    @Override // com.eltechs.axs.finiteStateMachine.FSMState
    public void notifyBecomeInactive() {
    }

    public GestureStateCheckFingerFirstCordsInXServerRectangle(GestureContext gestureContext, Rectangle rectInXUnit) {
        super(gestureContext);
        this.rect = rectInXUnit;
    }

    @Override // com.eltechs.axs.finiteStateMachine.FSMState
    public void notifyBecomeActive() {
        Finger finger = getContext().getTouchArea().getLastFingerAction().getFinger();
        float firstX = finger.getXWhenFirstTouched();
        float firstY = finger.getYWhenFirstTouched();
        float[] leftTop = {this.rect.x, this.rect.y};
        float[] rightBottom = {this.rect.x + this.rect.width, this.rect.y + this.rect.height};
        TransformationHelpers.mapPoints(getContext().getHostView().getXServerToViewTransformationMatrix(), leftTop);
        TransformationHelpers.mapPoints(getContext().getHostView().getXServerToViewTransformationMatrix(), rightBottom);
        if (leftTop[0] <= firstX && firstX < rightBottom[0] && leftTop[1] <= firstY && firstY < rightBottom[1]) {
            sendEvent(INSIDE);
        } else {
            sendEvent(OUTSIDE);
        }
    }
}
