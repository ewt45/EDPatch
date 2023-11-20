package com.example.datainsert.exagear.controls.axs.GestureStateMachine.v2;

import com.eltechs.axs.Finger;
import com.eltechs.axs.GestureStateMachine.AbstractGestureFSMState;
import com.eltechs.axs.GestureStateMachine.GestureContext;
import com.eltechs.axs.finiteStateMachine.FSMEvent;

import java.util.List;

/**
 * 移动光标位置到某一个手指的位置上
 */
public class StateMoveToFingerPosition extends AbstractGestureFSMState  {
    public static FSMEvent COMPLETED = new FSMEvent("COMPLETED") ;

    int mWhich=0;
    public StateMoveToFingerPosition(GestureContext gestureContext, int fingerIndex) {
        super(gestureContext);
        mWhich = fingerIndex;
    }

    @Override
    public void notifyBecomeActive() {
        List<Finger> fingers = getContext().getFingers();
        if(fingers.size()>mWhich){
            Finger dstFinger = fingers.get(mWhich);
            getContext().getPointerReporter().pointerMove(dstFinger.getX(), dstFinger.getY());
        }
        this.sendEvent(COMPLETED);
    }

    @Override
    public void notifyBecomeInactive() {

    }
}
