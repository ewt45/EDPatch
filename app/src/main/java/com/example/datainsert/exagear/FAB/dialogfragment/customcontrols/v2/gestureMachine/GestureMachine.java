package com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.gestureMachine;

import android.util.Log;

import com.eltechs.axs.helpers.Assert;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.gestureMachine.State.ActionPointerMove;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.gestureMachine.State.StateNeutral;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.gestureMachine.State.StateWaitForNeutral;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.model.OneGestureArea;

import java.util.ArrayList;
import java.util.List;

public class GestureMachine {
    private static final String TAG = "FiniteStateMachine";
    private final List<FSMTransitionTableEntry> transitionTable = new ArrayList<>();
    private final List<FSMState2> allStates = new ArrayList<>();
    private final FSMListenersList2 listeners = new FSMListenersList2();
    private FSMState2 currentState;
    private FSMTransitionTableEntry defaultEntry;
    private OneGestureArea model;

    /**
     * 初始时，最先应该调用这个函数，传入model。此时会
     * <br/> - 调用全部state的attach，将machine附加到全部状态上，state会初始化一些自身变量。
     * <br/> - 配置初始状态和默认状态，无需再调用函数单独设置这俩，如果外部要调用这俩，不要外部自己新建，调用machine.get获取
     */
    public void setModel(OneGestureArea model){
        this.model = model;

        for (FSMState2 state : model.getAllStateList())
            initStateIfNeeded(state);

        currentState = model.getInitState();
        defaultEntry = new FSMTransitionTableEntry(null, FSMR.event.完成, model.getDefaultState(), new FSMAction2[0]);

        //添加已有的转换
        for(List<Integer> transition:model.getTransitionList()){
            FSMAction2[] actions = new FSMAction2[transition.size()-3];
            for(int i=0; i<actions.length; i++)
                actions[i] = (FSMAction2) findStateById(transition.get(i+3));

            addTransition(
                    findStateById(transition.get(0)),
                    transition.get(1),
                    findStateById(transition.get(2)),
                    actions);
        }
    }

    /**
     * 新接收到一个状态时，调用此函数。如果该状态不在全部状态列表中（尚未调用attach），则进行初始化
     */
    private void initStateIfNeeded(FSMState2... states){
        for(FSMState2 state: states){
            if(!allStates.contains(state)){
                state.attach(this);
                allStates.add(state);
            }
        }
    }

    /**
     * 向状态机中添加转换时，调用此函数，如果此转换尚未存于model中，则存入
     */
    private void addTransToModelIfNeeded(FSMState2 preState, int event, FSMState2 postState, FSMAction2[] actions) {
        for(List<Integer> transition:model.getTransitionList())
            if(transition.get(0) == preState.getId() && transition.get(1) == event)
                return;//如果已经有这个转换了，就返回

        model.addTransition(preState,event,postState,actions);
    }

    public void configurationCompleted() {
        Assert.state(this.currentState != null, "Initial state not set");
        Assert.state(this.defaultEntry != null, "Default state not set");
        Assert.state(!this.transitionTable.isEmpty(), "Transitional table is not initialized");
        Assert.state(!this.allStates.isEmpty(), "States are not set");
        for(FSMState2 state:allStates){
            if(state instanceof StateNeutral && state!= model.getInitState())
                throw new RuntimeException("请勿自己创建StateNeutral，而是使用model.get获取");
            else if(state instanceof  StateWaitForNeutral && state != model.getDefaultState())
                throw new RuntimeException("请勿自己创建StateWaitForNeutral，而是使用model.get获取");
        }
        //TODO 在这里调用attach是不是也行
        this.currentState.notifyBecomeActive();
    }

    public void addTransition(FSMState2 preState, int event, FSMState2 postState) {
        addTransition(preState, event, postState, new FSMAction2[0]);
    }

    public void addTransition(FSMState2 preState, int event, FSMState2 postState, FSMAction2... actions) {
//        Assert.state(this.allStates.contains(preState), "Transition from unknown state");
//        Assert.state(this.allStates.contains(postState), "Transition to unknown state");
        initStateIfNeeded(preState,postState);
        initStateIfNeeded(actions);
        addTransToModelIfNeeded(preState,event,postState,actions);

        if (preState instanceof FSMAction2 || postState instanceof FSMAction2)
            throw new RuntimeException("转移前后的state不能为action");
        this.transitionTable.add(new FSMTransitionTableEntry(preState, event, postState, actions));
    }



    public boolean isActiveState(FSMState2 abstractFSMState) {
        boolean z;
        synchronized (this) {
            z = this.currentState == abstractFSMState;
        }
        return z;
    }

    public void sendEvent(FSMState2 preState, int fSMEvent) {
        synchronized (this) {
            Assert.state(preState == this.currentState);
//            AbstractFSMState2 nextState = getNextStateByCurrentStateAndEvent(this.currentState, fSMEvent);
//            Log.d(TAG, String.format("sendEvent: 状态改变：%s --- %s --> %s",currentState.debugName,FSMR.getEventS(fSMEvent),nextState.debugName));
//            changeState(nextState);

            FSMTransitionTableEntry entry = getTransitionEntry(preState, fSMEvent);
            Log.d(TAG, String.format("sendEvent: 状态改变：%s --- %s%s --> %s",
                    preState.getNiceName(), FSMR.getEventS(fSMEvent), getActionArrNames(entry.actions), entry.postState.getNiceName()));
            this.currentState.notifyBecomeInactive();
            this.listeners.sendLeftState(this.currentState);
            for (FSMAction2 action : entry.actions) //执行过渡动作
                action.run();
            this.currentState = entry.postState;
            this.currentState.notifyBecomeActive();
            this.listeners.sendEnteredState(this.currentState);
        }
    }

    /**
     * 根据id，在已记录的状态列表中寻找对应状态
     */
    private FSMState2 findStateById(int id){
        for(FSMState2 state:model.getAllStateList())
            if(state.getId() == id)
                return state;
        throw new RuntimeException("id为"+id+"的state没有记录在全部状态列表中");
    }




    private FSMTransitionTableEntry getTransitionEntry(FSMState2 preState, int event) {
        for (FSMTransitionTableEntry entry : this.transitionTable) {
            if (entry.preState == preState && entry.event == event) {
                return entry;
            }
        }
        return this.defaultEntry;
    }

//    private void logWhenTransition(AbstractFSMState2 preState, int event, AbstractFSMState2  postState, List<AbstractFSMAction2> actions){
//        Log.d(TAG, String.format("sendEvent: 状态改变：%s --- %s%s --> %s",
//                preState.getNiceName(), FSMR.getEventS(fSMEvent), getActionArrNames(model.getTranActionsList().get(index)), entry.postState.getNiceName()));
//
//    }

    private String getActionArrNames(FSMAction2... actions) {
        StringBuilder builder = new StringBuilder();
        for (FSMAction2 action : actions)
            builder.append(" ").append(action.getNiceName());
        if (builder.length() > 0)
            builder.insert(0, ", 执行过渡动作:");
        return builder.toString();
    }

    //TODO 这个可以用来实现编辑下的状态监听？
    public void addListener(FSMListenersList2.FSMListener fSMListener) {
        synchronized (this) {
            this.listeners.addListener(fSMListener);
        }
    }

    public void removeListener(FSMListenersList2.FSMListener fSMListener) {
        synchronized (this) {
            this.listeners.removeListener(fSMListener);
        }
    }

    public static class FSMTransitionTableEntry {
        public final int event;
        public final FSMState2 postState;
        public final FSMState2 preState;
        //TODO 注意这个actions有先后顺序之分。写用户ui的时候记得添加调整顺序的功能
        public final FSMAction2[] actions;

        public FSMTransitionTableEntry(FSMState2 abstractFSMState, int fSMEvent, FSMState2 FSMState2, FSMAction2[] actions) {
            this.preState = abstractFSMState;
            this.event = fSMEvent;
            this.postState = FSMState2;
            this.actions = actions;
        }
    }
}