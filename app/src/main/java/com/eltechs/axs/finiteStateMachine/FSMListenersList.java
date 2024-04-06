package com.eltechs.axs.finiteStateMachine;

import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class FSMListenersList {
    private final List<FSMListener> listeners = new ArrayList<>();

    public void addListener(FSMListener fSMListener) {
        this.listeners.add(fSMListener);
    }

    public void removeListener(FSMListener fSMListener) {
        this.listeners.remove(fSMListener);
    }

    public void sendLeftState(FSMState fSMState) {
        for (FSMListener fSMListener : this.listeners) {
            fSMListener.leftState(fSMState);
        }
    }

    public void sendEnteredState(FSMState fSMState) {
        for (FSMListener fSMListener : this.listeners) {
            fSMListener.enteredState(fSMState);
        }
    }
}
