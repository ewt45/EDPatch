package com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.gestureMachine;

import java.util.ArrayList;
import java.util.List;

public class FSMListenersList2 {
    private final List<FSMListener> listeners = new ArrayList<>();

    public void addListener(FSMListener fSMListener) {
        this.listeners.add(fSMListener);
    }

    public void removeListener(FSMListener fSMListener) {
        this.listeners.remove(fSMListener);
    }

    public void sendLeftState(AbstractFSMState2 fSMState) {
        for (FSMListener fSMListener : this.listeners) {
            fSMListener.leftState(fSMState);
        }
    }

    public void sendEnteredState(AbstractFSMState2 fSMState) {
        for (FSMListener fSMListener : this.listeners) {
            fSMListener.enteredState(fSMState);
        }
    }

    public interface FSMListener {
        void enteredState(AbstractFSMState2 fSMState);

        void leftState(AbstractFSMState2 fSMState);
    }

}
