package com.eltechs.axs.finiteStateMachine;

/* loaded from: classes.dex */
public interface FSMListener {
    void enteredState(FSMState fSMState);

    void leftState(FSMState fSMState);
}
