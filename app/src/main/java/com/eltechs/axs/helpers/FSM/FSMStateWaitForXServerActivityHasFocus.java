package com.eltechs.axs.helpers.FSM;

import com.eltechs.axs.activities.XServerDisplayActivity;
import com.eltechs.axs.applicationState.ApplicationStateBase;
import com.eltechs.axs.finiteStateMachine.AbstractFSMState;
import com.eltechs.axs.finiteStateMachine.FSMEvent;
import com.eltechs.axs.helpers.InfiniteTimer;

/* loaded from: classes.dex */
public class FSMStateWaitForXServerActivityHasFocus extends AbstractFSMState {
    public static final FSMEvent SUCCESS = new FSMEvent() { // from class: com.eltechs.axs.helpers.FSM.FSMStateWaitForXServerActivityHasFocus.1
    };
    private final ApplicationStateBase as;
    private final int timeoutMs;
    private InfiniteTimer timer;

    public FSMStateWaitForXServerActivityHasFocus(int i, ApplicationStateBase applicationStateBase) {
        this.timeoutMs = i;
        this.as = applicationStateBase;
    }

    @Override // com.eltechs.axs.finiteStateMachine.FSMState
    public void notifyBecomeActive() {
        this.timer = new InfiniteTimer(this.timeoutMs) { // from class: com.eltechs.axs.helpers.FSM.FSMStateWaitForXServerActivityHasFocus.2
            @Override // android.os.CountDownTimer
            public void onTick(long j) {
                if (FSMStateWaitForXServerActivityHasFocus.this.getMachine().isActiveState(FSMStateWaitForXServerActivityHasFocus.this) && FSMStateWaitForXServerActivityHasFocus.this.as.getCurrentActivity() != null && FSMStateWaitForXServerActivityHasFocus.this.as.getCurrentActivity().getClass() == XServerDisplayActivity.class) {
                    FSMStateWaitForXServerActivityHasFocus.this.sendEvent(FSMStateWaitForXServerActivityHasFocus.SUCCESS);
                }
            }
        };
        this.timer.start();
    }

    @Override // com.eltechs.axs.finiteStateMachine.FSMState
    public void notifyBecomeInactive() {
        this.timer.cancel();
        this.timer = null;
    }
}
