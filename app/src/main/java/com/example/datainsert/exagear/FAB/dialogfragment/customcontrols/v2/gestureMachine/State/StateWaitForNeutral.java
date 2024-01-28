package com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.gestureMachine.State;

import static com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.gestureMachine.FSMR.event.完成;

import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.Finger;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.TouchAdapter;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.gestureMachine.AbstractFSMState2;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.gestureMachine.FSMR;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.annotation.StateTag;

import java.util.List;

/**
 * 备用状态，当某个状态没有指定下一个状态时，则进入此备用状态。
 * <br/> 此状态等待全部手指松开，然后走向初始态。
 * <br/> 进入活跃态时，若已经没有手指按下，则直接结束。否则等到全部手指松开了就结束。
 */
@StateTag(tag = FSMR.state.回归初始状态,events = 完成)
public class StateWaitForNeutral extends AbstractFSMState2 implements TouchAdapter {

    @Override
    protected void onAttach() {

    }

    @Override
    public void notifyMoved(Finger finger, List<Finger> list) {

    }

    @Override
    public void notifyReleased(Finger finger, List<Finger> list) {
        if (list.isEmpty()) {
            sendEvent(完成);
        }
    }

    @Override
    public void notifyTouched(Finger finger, List<Finger> list) {

    }

    @Override
    public void notifyBecomeActive() {
        addTouchListener(this);
        if (getContext().getFingers().isEmpty()) {
            sendEvent(完成);
        }
    }

    @Override
    public void notifyBecomeInactive() {
        removeTouchListener(this);
    }
}
