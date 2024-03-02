//package com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.gestureMachine;
//
//import android.util.Log;
//
//import com.eltechs.axs.helpers.Assert;
//
//import java.util.ArrayList;
//
//public class GestureMachine_old extends GestureMachine {
//    private static final String TAG = "FiniteStateMachine";
//    private final ArrayList<FSMTransitionTableEntry> transitionTable = new ArrayList<>();
//    private final ArrayList<FSMState2> allStates = new ArrayList<>();
//    private final FSMListenersList2 listeners = new FSMListenersList2();
//    private FSMState2 currentState;
//    private FSMTransitionTableEntry defaultEntry;
//
//    public void setStatesList(FSMState2... abstractFSMStateArr) {
//        for (FSMState2 abstractFSMState : abstractFSMStateArr) {
//            abstractFSMState.attach(this);
//            this.allStates.add(abstractFSMState);
//        }
//    }
//
//    public void setInitialState(FSMState2 abstractFSMState) {
//        Assert.state(this.allStates.contains(abstractFSMState));
//        this.currentState = abstractFSMState;
//    }
//
//    /**
//     * 当某个状态发送某个事件，但没有指定这个事件的下一个状态时，就会进入这个默认状态。
//     * <br/> 这个默认状态最终应该能回到初始状态。
//     */
//    public void setDefaultState(FSMState2 postState) {
//        this.defaultEntry = new FSMTransitionTableEntry(null, FSMR.event.完成, postState, new FSMAction2[0]);
//    }
//
//    public void configurationCompleted() {
//        Assert.state(this.currentState != null, "Initial state not set");
//        Assert.state(this.defaultEntry != null, "Default state not set");
//        Assert.state(!this.transitionTable.isEmpty(), "Transitional table is not initialized");
//        Assert.state(!this.allStates.isEmpty(), "States are not set");
//        this.currentState.notifyBecomeActive();
//    }
//
//    public void addTransition(FSMState2 preState, int event, FSMState2 postState) {
//        addTransition(preState, event, postState, new FSMAction2[0]);
//    }
//
//    public void addTransition(FSMState2 preState, int event, FSMState2 postState, FSMAction2... actions) {
//        Assert.state(this.allStates.contains(preState), "Transition from unknown state");
//        Assert.state(this.allStates.contains(postState), "Transition to unknown state");
//        if (preState instanceof FSMAction2 || postState instanceof FSMAction2)
//            throw new RuntimeException("转移前后的state不能为action");
//        this.transitionTable.add(new FSMTransitionTableEntry(preState, event, postState, actions));
//    }
//
//    public boolean isActiveState(FSMState2 abstractFSMState) {
//        boolean z;
//        synchronized (this) {
//            z = this.currentState == abstractFSMState;
//        }
//        return z;
//    }
//
//    public void sendEvent(FSMState2 preState, int fSMEvent) {
//        synchronized (this) {
//            Assert.state(preState == this.currentState);
////            AbstractFSMState2 nextState = getNextStateByCurrentStateAndEvent(this.currentState, fSMEvent);
////            Log.d(TAG, String.format("sendEvent: 状态改变：%s --- %s --> %s",currentState.debugName,FSMR.getEventS(fSMEvent),nextState.debugName));
////            changeState(nextState);
//
//            FSMTransitionTableEntry entry = getTransitionEntry(preState, fSMEvent);
//            Log.d(TAG, String.format("sendEvent: 状态改变：%s --- %s%s --> %s",
//                    preState.getNiceName(), FSMR.getEventS(fSMEvent), getActionArrNames(entry.actions), entry.postState.getNiceName()));
//            this.currentState.notifyBecomeInactive();
//            this.listeners.sendLeftState(this.currentState);
//            for (FSMAction2 action : entry.actions) //执行过渡动作
//                action.run();
//            this.currentState = entry.postState;
//            this.currentState.notifyBecomeActive();
//            this.listeners.sendEnteredState(this.currentState);
//        }
//    }
//
//    private FSMTransitionTableEntry getTransitionEntry(FSMState2 preState, int event) {
//        for (FSMTransitionTableEntry entry : this.transitionTable) {
//            if (entry.preState == preState && entry.event == event) {
//                return entry;
//            }
//        }
//        return this.defaultEntry;
//    }
//
//    public static String getActionArrNames(FSMAction2[] actions) {
//        StringBuilder builder = new StringBuilder();
//        for (FSMAction2 action : actions)
//            builder.append(" ").append(action.getNiceName());
//        if (builder.length() > 0)
//            builder.insert(0, ", 执行过渡动作:");
//        return builder.toString();
//    }
//
//    public void addListener(FSMListenersList2.FSMListener fSMListener) {
//        synchronized (this) {
//            this.listeners.addListener(fSMListener);
//        }
//    }
//
//    public void removeListener(FSMListenersList2.FSMListener fSMListener) {
//        synchronized (this) {
//            this.listeners.removeListener(fSMListener);
//        }
//    }
//
//}