package com.eltechs.axs.finiteStateMachine.generalStates;

import com.eltechs.axs.finiteStateMachine.AbstractFSMState;
import com.eltechs.axs.finiteStateMachine.FSMEvent;
import com.eltechs.axs.helpers.FileHelpers;
import java.io.File;
import java.io.IOException;

/* loaded from: classes.dex */
public class FSMStateCopyFileIfExists extends AbstractFSMState {
    public static final FSMEvent COMPLETED = new FSMEvent();
    public static final FSMEvent FAILED = new FSMEvent();
    private final File from;
    private final File to;

    @Override // com.eltechs.axs.finiteStateMachine.FSMState
    public void notifyBecomeInactive() {
    }

    public FSMStateCopyFileIfExists(File from, File to) {
        this.from = from;
        this.to = to;
    }

    @Override // com.eltechs.axs.finiteStateMachine.FSMState
    public void notifyBecomeActive() {
        if (this.from.exists()) {
            try {
                FileHelpers.copyFile(this.from, this.to);
                sendEvent(COMPLETED);
                return;
            } catch (IOException e) {
                e.printStackTrace();
                sendEvent(FAILED);
                return;
            }
        }
        sendEvent(COMPLETED);
    }
}
