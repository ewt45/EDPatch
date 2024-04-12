package com.eltechs.axs.finiteStateMachine;

public class FSMEvent {
    private final String name;

    public FSMEvent(String name) {
        this.name = name;
    }

    public FSMEvent() {
        this.name = FSMEvent.class.toString();
    }

    public String toString() {
        return this.name;
    }
}
