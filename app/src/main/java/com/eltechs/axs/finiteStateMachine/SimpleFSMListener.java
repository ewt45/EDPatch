package com.eltechs.axs.finiteStateMachine;

/* loaded from: classes.dex */
public abstract class SimpleFSMListener implements FSMListener {
    @Override // com.eltechs.axs.finiteStateMachine.FSMListener
    public void leftState(FSMState fSMState) {
    }

    protected abstract void stateSwitched(FSMState fSMState);

    @Override // com.eltechs.axs.finiteStateMachine.FSMListener
    public void enteredState(FSMState fSMState) {
        stateSwitched(fSMState);
    }
}
