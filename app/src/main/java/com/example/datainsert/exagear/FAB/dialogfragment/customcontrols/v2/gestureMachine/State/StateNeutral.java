package com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.gestureMachine.State;

import static com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.gestureMachine.FSMR.event.新手指按下;

import com.eltechs.axs.helpers.Assert;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.Finger;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.TouchAdapter;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.gestureMachine.AbstractFSMState2;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.gestureMachine.FSMR;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.annotation.StateTag;

import java.util.List;

/**
 * 初始状态。任何状态走到尽头后都会回到这个状态。
 * <br/> 设置完状态机后立即将此状态设为活跃状态，因此 添加触摸监听器的这个时候，还没有手指按下。发送手指按下事件的时候 第一根手指刚按下
 */
@StateTag(tag = FSMR.state.初始状态, events = {新手指按下})
public class StateNeutral  extends AbstractFSMState2 implements TouchAdapter {


    @Override
    protected void onAttach() {

    }

    @Override // com.eltechs.axs.finiteStateMachine.FSMState
    public void notifyBecomeActive() {
        //设置完状态机后立即将此状态设为活跃状态，因此 添加监听器的这个时候，还没有手指按下。发送手指按下事件的时候 第一根手指刚按下
        addTouchListener(this);
        Assert.state(getContext().getFingers().isEmpty());
    }

    @Override // com.eltechs.axs.finiteStateMachine.FSMState
    public void notifyBecomeInactive() {
        removeTouchListener(this);
    }

    @Override
    public void notifyMoved(Finger finger, List<Finger> list) {

    }

    @Override
    public void notifyReleased(Finger finger, List<Finger> list) {

    }

    @Override
    public void notifyTouched(Finger finger, List<Finger> list) {
        sendEvent(新手指按下);

    }

    @Override
    public void notifyMovedIn(Finger finger, List<Finger> list) {
        sendEvent(新手指按下);
    }
}
