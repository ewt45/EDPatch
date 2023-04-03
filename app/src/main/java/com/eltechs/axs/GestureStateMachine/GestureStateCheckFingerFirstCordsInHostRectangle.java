package com.eltechs.axs.GestureStateMachine;

import com.eltechs.axs.Finger;
import com.eltechs.axs.finiteStateMachine.FSMEvent;
import com.eltechs.axs.geom.Rectangle;

/* loaded from: classes.dex */
public class GestureStateCheckFingerFirstCordsInHostRectangle extends AbstractGestureFSMState {
    public static FSMEvent INSIDE = new FSMEvent() { // from class: com.eltechs.axs.GestureStateMachine.GestureStateCheckFingerFirstCordsInHostRectangle.1
    };
    public static FSMEvent OUTSIDE = new FSMEvent() { // from class: com.eltechs.axs.GestureStateMachine.GestureStateCheckFingerFirstCordsInHostRectangle.2
    };
    private final Rectangle rect;

    @Override // com.eltechs.axs.finiteStateMachine.FSMState
    public void notifyBecomeInactive() {
    }

    public GestureStateCheckFingerFirstCordsInHostRectangle(GestureContext gestureContext, Rectangle rectangle) {
        super(gestureContext);
        this.rect = rectangle;
    }

    @Override // com.eltechs.axs.finiteStateMachine.FSMState
    public void notifyBecomeActive() {
        Finger finger = getContext().getTouchArea().getLastFingerAction().getFinger();
        float xWhenFirstTouched = finger.getXWhenFirstTouched();
        float yWhenFirstTouched = finger.getYWhenFirstTouched();
        float[] fArr = {this.rect.x, this.rect.y};
        float[] fArr2 = {this.rect.x + this.rect.width, this.rect.y + this.rect.height};
        if (fArr[0] <= xWhenFirstTouched && xWhenFirstTouched < fArr2[0] && fArr[1] <= yWhenFirstTouched && yWhenFirstTouched < fArr2[1]) {
            sendEvent(INSIDE);
        } else {
            sendEvent(OUTSIDE);
        }
    }
}
