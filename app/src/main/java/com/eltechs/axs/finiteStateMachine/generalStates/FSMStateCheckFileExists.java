package com.eltechs.axs.finiteStateMachine.generalStates;

import com.eltechs.axs.finiteStateMachine.AbstractFSMState;
import com.eltechs.axs.finiteStateMachine.FSMEvent;
import java.io.File;

/* loaded from: classes.dex */
public class FSMStateCheckFileExists extends AbstractFSMState {
    private final File file;
    public static final FSMEvent EXISTS = new FSMEvent();
    public static final FSMEvent DOESNT_EXISTS = new FSMEvent();
    @Override // com.eltechs.axs.finiteStateMachine.FSMState
    public void notifyBecomeInactive() {
    }

    public FSMStateCheckFileExists(File file) {
        this.file = file;
    }

    @Override // com.eltechs.axs.finiteStateMachine.FSMState
    public void notifyBecomeActive() {
        sendEvent(this.file.exists() ? EXISTS : DOESNT_EXISTS);
    }
}
