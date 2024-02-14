package com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.model;

import static com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.gestureMachine.FSMR.event.完成;

import com.eltechs.axs.helpers.Assert;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.gestureMachine.FSMAction2;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.gestureMachine.FSMState2;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.gestureMachine.GestureContext2;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.gestureMachine.GestureMachine;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.gestureMachine.State.StateNeutral;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.gestureMachine.State.StateWaitForNeutral;

import java.util.ArrayList;
import java.util.List;

public class OneGestureArea extends TouchAreaModel {
    //TODO statemachine的table和defaultState啥的都放这里

    transient private StateWaitForNeutral defaultState;
    transient private StateNeutral initState;
    transient private int maxIdValue = 0; //当前已分配状态id的最大值


    List<FSMState2> allStateList = new ArrayList<>();
    List<List<Integer>> transitionList = new ArrayList<>(); //每一项是一个转换。0是前状态，1是事件，2是后状态，3往后都是附加操作

    public OneGestureArea() {
        super(TYPE_GESTURE);
    }

    /**
     * 请务必在初始时调用一次该函数
     */
    public void init() {

        //默认和初始状态，应优先从全部状态列表中寻找。如果全部状态列表中没有，则自己新建并添加返回初始状态的转换。
        for(FSMState2 state:allStateList){
            if(state instanceof StateNeutral)
                initState = (StateNeutral) state;
            else if(state instanceof  StateWaitForNeutral)
                defaultState = (StateWaitForNeutral) state;
        }

        if(initState==null || defaultState==null){
            //初始化后必须包含初始状态，默认状态和默认状态到初始状态的转换
            initState = new StateNeutral();
            initState.setId(generateStateId());
            defaultState = new StateWaitForNeutral();
            defaultState.setId(generateStateId());
            allStateList.add(initState);
            allStateList.add(defaultState);
            List<Integer> fallbackTrans = new ArrayList<>();
            fallbackTrans.add(defaultState.getId());
            fallbackTrans.add(完成);
            fallbackTrans.add(initState.getId());
            transitionList.add(fallbackTrans);
        }

        //重新分配一遍id吧
        maxIdValue=0;
        for(FSMState2 state:allStateList){
            int oldStateId = state.getId();
            int newStateId = generateStateId();
            state.setId(newStateId);

            for(List<Integer> oneTran:transitionList){
                for(int i=0; i<oneTran.size(); i++){
                    if(i==1) continue;
                    if(oneTran.get(i)==-1)
                        throw new RuntimeException("状态id不应为-1");
                    if(oneTran.get(i).equals(oldStateId)){
                        oneTran.remove(i);
                        oneTran.add(i,newStateId);
                    }
                }
            }
        }
    }


    @Override
    protected void cloneSelfFields(TouchAreaModel ref) {
        if(ref.getClass().equals(OneGestureArea.class)){
            allStateList.clear();
            allStateList.addAll(((OneGestureArea) ref).allStateList);
            transitionList.clear();
            transitionList.addAll(((OneGestureArea) ref).transitionList);
        }
    }

    /**
     * 生成一个新的State的id，不与其他state的id冲突
     */
    public int generateStateId(){
        maxIdValue++;
        return maxIdValue;
    }

    public StateWaitForNeutral getDefaultState() {
        return defaultState;
    }

    public StateNeutral getInitState() {
        return initState;
    }

    public List<FSMState2> getAllStateList() {
        return allStateList;
    }

    public void addStates(FSMState2... states){
        for(FSMState2 state:states)
            if(!allStateList.contains(state))
                allStateList.add(state);
    }


    public List<List<Integer>> getTransitionList() {
        return transitionList;
    }

//    public void test(GestureContext2 gestureContext) {
//        GestureMachine machine = new GestureMachine();
//        machine.setModel(this);
//        for (int i = 0; i < tranPreStateList.size(); i++) {
//            int preId = tranPreStateList.get(i), postId = tranPostStateList.get(i);
//            List<Integer> actionIds = tranActionsList.get(i);
//            FSMState2 preState = null, postState = null;
//            FSMAction2[] actions = new FSMAction2[actionIds.size()];
//            //将id转为state实例
//            for (FSMState2 state : allStateList) {
//                if (state.getId() == preId)
//                    preState = state;
//                else if (state.getId() == postId)
//                    postState = state;
//                else {
//                    int actionIndex = actionIds.indexOf(state.getId());
//                    if (actionIndex != -1)
//                        actions[actionIndex] = (FSMAction2) state;
//                }
//            }
//            //交给machine
//            machine.addTransition(preState, tranEventList.get(i), postState, actions);
//
//        }
//
//        machine.configurationCompleted();
//        gestureContext.setMachine(machine);
//    }

    /**
     * 添加一个状态转换（向四个列表中都添加一项）
     * <br/> 若状态不在model的全部状态列表中，则添加进去
     */
    public void addTransition(FSMState2 preState, int event, FSMState2 postState, FSMAction2[] actions) {
        if (!allStateList.contains(preState)) allStateList.add(preState);
        if (!allStateList.contains(postState)) allStateList.add(postState);
        for (FSMAction2 action : actions)
            if (!allStateList.contains(action)) allStateList.add(action);

        List<Integer> transition = new ArrayList<>();
        transition.add(preState.getId());
        transition.add(event);
        transition.add(postState.getId());
        for (FSMAction2 action : actions)
            transition.add(action.getId());
        transitionList.add(transition);
    }

    /**
     * 删除一个状态，顺便删除与其相关的全部状态转移
     */
    public void deleteState(FSMState2 state) {
        Assert.isFalse(state instanceof StateNeutral || state instanceof StateWaitForNeutral);
        allStateList.remove(state);
        int deleteId = state.getId();

        for(int i=0; i<transitionList.size(); i++){
            List<Integer> oneTran = transitionList.get(i);
            if( oneTran.get(0).equals(deleteId) || oneTran.get(2).equals(deleteId)){
                transitionList.remove(i);
                i--;
            }
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
