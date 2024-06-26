package com.eltechs.axs.finiteStateMachine;

import com.eltechs.axs.helpers.Assert;

/* loaded from: classes.dex */
public abstract class AbstractFSMState implements FSMState {
    private FiniteStateMachine machine;

    public final void attach(FiniteStateMachine finiteStateMachine) {
        Assert.state(this.machine == null, "Already attached to FSM!");
        this.machine = finiteStateMachine;
    }

    protected final void sendEvent(FSMEvent fSMEvent) {
        this.machine.sendEvent(this, fSMEvent);
    }

    protected final void sendEventIfActive(FSMEvent fSMEvent) {
        synchronized (this.machine) {
            if (this.machine.isActiveState(this)) {
                sendEvent(fSMEvent);
            }
        }
    }

    protected final FiniteStateMachine getMachine() {
        return this.machine;
    }
}
