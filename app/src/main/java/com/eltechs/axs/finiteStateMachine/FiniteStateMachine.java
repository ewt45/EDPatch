package com.eltechs.axs.finiteStateMachine;

import android.util.Log;

import com.eltechs.axs.helpers.Assert;
import java.util.ArrayList;
import java.util.Iterator;

public class FiniteStateMachine {
    private static final String TAG= "FiniteStateMachine";
    private AbstractFSMState currentState;
    private AbstractFSMState defaultState;
    private ArrayList<FSMTransitionTableEntry> transitionTable = new ArrayList<>();
    private ArrayList<AbstractFSMState> allStates = new ArrayList<>();
    private FSMListenersList listeners = new FSMListenersList();

    private static class FSMTransitionTableEntry {
        public final FSMEvent event;
        public final AbstractFSMState postState;
        public final AbstractFSMState preState;

        public FSMTransitionTableEntry(AbstractFSMState preState, FSMEvent event, AbstractFSMState postState) {
            this.preState = preState;
            this.event = event;
            this.postState = postState;
        }
    }

    public void setStatesList(AbstractFSMState... allStates) {
        for (AbstractFSMState state : allStates) {
            state.attach(this);
            this.allStates.add(state);
        }
    }

    public void setInitialState(AbstractFSMState initialState) {
        Assert.state(this.allStates.contains(initialState));
        this.currentState = initialState;
    }

    public void setDefaultState(AbstractFSMState defaultState) {
        this.defaultState = defaultState;
    }

    public void configurationCompleted() {
        Assert.state(this.currentState != null, "Initial state not set");
        Assert.state(this.defaultState != null, "Default state not set");
        Assert.state(!this.transitionTable.isEmpty(), "Transitional table is not initialized");
        Assert.state(!this.allStates.isEmpty(), "States are not set");
        this.currentState.notifyBecomeActive();
    }

    public void addTransition(AbstractFSMState preState, FSMEvent event, AbstractFSMState postState) {
        Assert.state(this.allStates.contains(preState), "Transition from unknown state");
        Assert.state(this.allStates.contains(postState), "Transition to unknown state");
        this.transitionTable.add(new FSMTransitionTableEntry(preState, event, postState));
    }

    public boolean isActiveState(AbstractFSMState abstractFSMState) {
        boolean isActive;
        synchronized (this) {
            isActive = this.currentState == abstractFSMState;
        }
        return isActive;
    }

    private void changeState(AbstractFSMState postState) {
        this.currentState.notifyBecomeInactive();
        this.listeners.sendLeftState(this.currentState);
        this.currentState = postState;
        this.currentState.notifyBecomeActive();
        this.listeners.sendEnteredState(this.currentState);
    }

    private AbstractFSMState getNextStateByCurrentStateAndEvent(AbstractFSMState currentState, FSMEvent event) {
        for (FSMTransitionTableEntry entry : this.transitionTable) {
            if (entry.preState == currentState && entry.event == event) {
                return entry.postState;
            }
        }
        return this.defaultState;
    }

    public void sendEvent(AbstractFSMState preState, FSMEvent event) {
        synchronized (this) {
            Assert.state(preState == this.currentState);
            AbstractFSMState nextState = getNextStateByCurrentStateAndEvent(this.currentState, event);
            Log.d(TAG, String.format("sendEvent: 状态改变：%s --- %s --> %s", this.currentState.getClass().getSimpleName(),event.toString(),nextState.getClass().getSimpleName()));
            changeState(nextState);
        }
    }

    public void addListener(FSMListener fSMListener) {
        synchronized (this) {
            this.listeners.addListener(fSMListener);
        }
    }

    public void removeListener(FSMListener fSMListener) {
        synchronized (this) {
            this.listeners.removeListener(fSMListener);
        }
    }
}
