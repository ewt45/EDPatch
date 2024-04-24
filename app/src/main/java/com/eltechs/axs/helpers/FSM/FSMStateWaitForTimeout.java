package com.eltechs.axs.helpers.FSM;

import com.eltechs.axs.finiteStateMachine.AbstractFSMState;
import com.eltechs.axs.finiteStateMachine.FSMEvent;
import com.eltechs.axs.helpers.OneShotTimer;

/* loaded from: classes.dex */
public class FSMStateWaitForTimeout extends AbstractFSMState {
    public static final FSMEvent TIMEOUT = new FSMEvent() { // from class: com.eltechs.axs.helpers.FSM.FSMStateWaitForTimeout.1
    };
    private final int timeoutMs;
    private OneShotTimer timer;

    public FSMStateWaitForTimeout(int i) {
        this.timeoutMs = i;
    }

    @Override // com.eltechs.axs.finiteStateMachine.FSMState
    public void notifyBecomeActive() {
        this.timer = new OneShotTimer(this.timeoutMs) { // from class: com.eltechs.axs.helpers.FSM.FSMStateWaitForTimeout.2
            @Override // android.os.CountDownTimer
            public void onFinish() {
                FSMStateWaitForTimeout.this.sendEvent(FSMStateWaitForTimeout.TIMEOUT);
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
