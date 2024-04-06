package com.eltechs.axs.finiteStateMachine.generalStates;

import com.eltechs.axs.finiteStateMachine.AbstractFSMState;
import com.eltechs.axs.finiteStateMachine.FSMEvent;

/* loaded from: classes.dex */
public class FSMStateRunRunnable extends AbstractFSMState {
    public static FSMEvent COMPLETED = new FSMEvent();
    private final Runnable runnable;

    @Override // com.eltechs.axs.finiteStateMachine.FSMState
    public void notifyBecomeInactive() {
    }

    public FSMStateRunRunnable(Runnable runnable) {
        this.runnable = runnable;
    }

    @Override // com.eltechs.axs.finiteStateMachine.FSMState
    public void notifyBecomeActive() {
        this.runnable.run();
        sendEvent(COMPLETED);
    }
}
