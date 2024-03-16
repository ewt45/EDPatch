package com.example.datainsert.exagear.controlsV2.model;

import com.example.datainsert.exagear.controlsV2.TestHelper;
import com.example.datainsert.exagear.controlsV2.TouchAreaModel;
import com.example.datainsert.exagear.controlsV2.gestureMachine.FSMAction2;
import com.example.datainsert.exagear.controlsV2.gestureMachine.FSMR;
import com.example.datainsert.exagear.controlsV2.gestureMachine.FSMState2;
import com.example.datainsert.exagear.controlsV2.gestureMachine.state.StateNeutral;
import com.example.datainsert.exagear.controlsV2.gestureMachine.state.StateWaitForNeutral;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class OneGestureArea extends TouchAreaModel {
    public static final int IDX_TRAN_PRESTATE = 0;
    public static final int IDX_TRAN_EVENT = 1;
    public static final int IDX_TRAN_POSTSTATE = 2;
    public static final int IDX_TRAN_ACTION_START = 3;
    private final List<FSMState2> allStateList = new ArrayList<>();
    private final List<List<Integer>> transitionList = new ArrayList<>(); //每一项是一个转换。0是前状态，1是事件，2是后状态，3往后都是附加操作
    transient private StateWaitForNeutral defaultState;
    transient private StateNeutral initState;
    transient private List<FSMState2> unModifiableAllStateList;
    transient private int maxIdValue = 0; //当前已分配状态id的最大值

    //用于反射，请勿删除
    public OneGestureArea() {
        super(TYPE_GESTURE);
    }

    /**
     * 请务必在初始时调用一次该函数
     */
    public void init() {
        unModifiableAllStateList = Collections.unmodifiableList(allStateList);
        ;
        //默认和初始状态，应优先从全部状态列表中寻找。如果全部状态列表中没有，则自己新建并添加返回初始状态的转换。
        for (FSMState2 state : allStateList) {
            if (state instanceof StateNeutral)
                initState = (StateNeutral) state;
            else if (state instanceof StateWaitForNeutral)
                defaultState = (StateWaitForNeutral) state;
        }

        if (initState == null || defaultState == null) {
            //初始化后必须包含初始状态，默认状态和默认状态到初始状态的转换
            initState = new StateNeutral();
            initState.setId(generateStateId());
            defaultState = new StateWaitForNeutral();
            defaultState.setId(generateStateId());
            allStateList.add(initState);
            allStateList.add(defaultState);
            List<Integer> fallbackTrans = new ArrayList<>();
            fallbackTrans.add(defaultState.getId());
            fallbackTrans.add(FSMR.event.完成);
            fallbackTrans.add(initState.getId());
            transitionList.add(fallbackTrans);
        }

        //重新分配一遍id吧
        maxIdValue = 0;
        for (FSMState2 state : allStateList) {
            int oldStateId = state.getId();
            int newStateId = generateStateId();
            state.setId(newStateId);

            for (List<Integer> oneTran : transitionList) {
                for (int i = 0; i < oneTran.size(); i++) {
                    if (i == 1) continue;
                    if (oneTran.get(i) == -1)
                        throw new RuntimeException("状态id不应为-1");
                    if (oneTran.get(i).equals(oldStateId)) {
                        oneTran.remove(i);
                        oneTran.add(i, newStateId);
                    }
                }
            }
        }

        //检查是否有同一事件但不同PostState的，如果有，删掉旧的
        List<Integer> compareList = new ArrayList<>();
        for (int i = 0; i < transitionList.size(); i++) {
            List<Integer> oneTran = transitionList.get(i);
            int currPreAndEvent = oneTran.get(0) * 100 + oneTran.get(1);
            if (compareList.contains(currPreAndEvent)) {
                int index = compareList.indexOf(currPreAndEvent);
                compareList.remove((Integer) currPreAndEvent);
                transitionList.remove(index);
                i--;
            } else
                compareList.add(currPreAndEvent);
        }

    }


    @Override
    protected void cloneSelfFields(TouchAreaModel ref) {
        if (ref.getClass().equals(OneGestureArea.class)) {
            allStateList.clear();
            allStateList.addAll(((OneGestureArea) ref).allStateList);
            transitionList.clear();
            transitionList.addAll(((OneGestureArea) ref).transitionList);
        }
    }

    /**
     * 生成一个新的State的id，不与其他state的id冲突
     */
    public int generateStateId() {
        maxIdValue++;
        return maxIdValue;
    }

    public StateWaitForNeutral getDefaultState() {
        return defaultState;
    }

    public StateNeutral getInitState() {
        return initState;
    }

    /**
     * 获取全部状态列表，注意状态列表应该由model内部维护，外部不能直接修改
     */
    public List<FSMState2> getAllStateList() {
        return unModifiableAllStateList;
    }

    public void addStates(FSMState2... states) {
        for (FSMState2 state : states)
            if(state.getId() == FSMR.value.stateIdInvalid)
                throw new RuntimeException("请在调用attach（分配id）后再添加此状态");
            else if (state instanceof StateWaitForNeutral || state instanceof StateNeutral)
                throw new RuntimeException("新添加的状态不能是初始或默认状态");
            else if (!allStateList.contains(state))
                allStateList.add(state);
    }


    /**
     * 从全部状态列表中筛选掉action，返回剩下的状态列表。用于编辑界面展示状态列表
     */
    public List<FSMState2> getEditableStateList() {
        return TestHelper.filterList(allStateList, item -> !(item instanceof FSMAction2));
    }

    /**
     * 从全部状态列表中筛选掉state，返回剩下的action列表。用于编辑界面展示操作列表
     */
    public List<FSMAction2> getEditableActionList() {
        List<FSMAction2> returnList = new ArrayList<>();
        for (FSMState2 state : getAllStateList())
            if (state instanceof FSMAction2)
                returnList.add((FSMAction2) state);
        return returnList;
    }

    public List<List<Integer>> getTransitionList() {
        return transitionList;
    }


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
     * 删除一个状态，顺便删除与其相关的全部状态转移。如果是操作，则只删除转移中的操作id，不删除状态转移
     */
    public void removeState(FSMState2 state) {
        TestHelper.assertTrue(!(state instanceof StateNeutral || state instanceof StateWaitForNeutral));
        allStateList.remove(state);
        int deleteId = state.getId();

        for (int i = 0; i < transitionList.size(); i++) {
            List<Integer> oneTran = transitionList.get(i);
            //如果是状态，则删除整个转移
            if (oneTran.get(0).equals(deleteId) || oneTran.get(2).equals(deleteId)) {
                transitionList.remove(i);
                i--;
            }
            //如果是操作，则只删除操作id
            else {
                for (int actIdx = 3; actIdx < oneTran.size(); actIdx++)
                    if (oneTran.get(actIdx).equals(deleteId)) {
                        oneTran.remove(actIdx);
                        actIdx--;
                    }
            }
        }
    }

    /**
     * 获取某状态，发送某事件 对应的状态转移，请确保该状态已存在于全部状态列表中
     *
     * @return 状态转移。若不存在，则会首先创建一个默认的转移，将其加入的转移列表中并返回该转移
     */
    public List<Integer> getTransition(FSMState2 preState, int eventId) {
        TestHelper.assertTrue(allStateList.contains(preState), "请确保该状态已存在于全部状态列表中");
        for (List<Integer> transition : getTransitionList())
            if (transition.get(0) == preState.getId() && transition.get(1) == eventId)
                return transition;

        //若不存在，则会首先创建一个默认的转移，将其加入的转移列表中并返回该转移
        List<Integer> targetTransition = new ArrayList<>();
        targetTransition.add(preState.getId());
        targetTransition.add(eventId);
        targetTransition.add(getDefaultState().getId());
        transitionList.add(targetTransition);
        return targetTransition;
    }


    /**
     * 通过id寻找state
     */
    public <T extends FSMState2> T findStateById(int stateId) {
        for (FSMState2 state : allStateList)
            if (state.getId() == stateId)
                return (T) state;
        return null;
    }
}
