package com.eltechs.axs.finiteStateMachine;

import com.eltechs.axs.helpers.Assert;

/* loaded from: classes.dex */
public abstract class AbstractFSMState implements FSMState {
    private FiniteStateMachine machine;

    public final void attach(FiniteStateMachine finiteStateMachine) {
        Assert.state(this.machine == null, "Already attached to FSM!");
        this.machine = finiteStateMachine;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    protected final void sendEvent(FSMEvent fSMEvent) {
        this.machine.sendEvent(this, fSMEvent);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    protected final void sendEventIfActive(FSMEvent fSMEvent) {
        synchronized (this.machine) {
            if (this.machine.isActiveState(this)) {
                sendEvent(fSMEvent);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    protected final FiniteStateMachine getMachine() {
        return this.machine;
    }
}
