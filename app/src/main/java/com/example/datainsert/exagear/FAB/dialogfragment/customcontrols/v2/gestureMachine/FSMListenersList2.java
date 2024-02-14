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

    public void sendLeftState(FSMState2 fSMState) {
        for (FSMListener fSMListener : this.listeners) {
            fSMListener.leftState(fSMState);
        }
    }

    public void sendEnteredState(FSMState2 fSMState) {
        for (FSMListener fSMListener : this.listeners) {
            fSMListener.enteredState(fSMState);
        }
    }

    public interface FSMListener {
        void enteredState(FSMState2 fSMState);

        void leftState(FSMState2 fSMState);
    }

}
