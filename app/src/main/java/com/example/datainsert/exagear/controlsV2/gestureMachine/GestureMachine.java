package com.example.datainsert.exagear.controlsV2.gestureMachine;

import android.util.Log;

import com.eltechs.axs.helpers.Assert;
import com.example.datainsert.exagear.controlsV2.TestHelper;
import com.example.datainsert.exagear.controlsV2.gestureMachine.State.StateNeutral;
import com.example.datainsert.exagear.controlsV2.gestureMachine.State.StateWaitForNeutral;
import com.example.datainsert.exagear.controlsV2.model.OneGestureArea;
import com.example.datainsert.exagear.QH;

import java.util.ArrayList;
import java.util.List;

public class GestureMachine {
    private static final String TAG = "FiniteStateMachine";
    private final List<FSMListener> listeners = new ArrayList<>();
    private FSMState2 currentState;
    private OneGestureArea model;

    public GestureMachine() {
        if (QH.isTesting())
            listeners.add(new Logcat());
    }

    /**
     * 初始时，最先应该调用这个函数，传入model。此时会
     * <br/> - 调用全部state的attach，将machine附加到全部状态上，state会初始化一些自身变量。
     * <br/> - 配置初始状态和默认状态，无需再调用函数单独设置这俩，如果外部要调用这俩，不要外部自己新建，调用machine.get获取
     */
    public void setModel(OneGestureArea model) {
        this.model = model;

        for (FSMState2 state : model.getAllStateList())
            initStateIfNeeded(state);

        currentState = model.getInitState();

        //添加已有的转换
        for (List<Integer> transition : model.getTransitionList()) {
            FSMAction2[] actions = new FSMAction2[transition.size() - 3];
            for (int i = 0; i < actions.length; i++)
                actions[i] = model.findStateById(transition.get(i + 3));

            addTransition(model.findStateById(transition.get(0)), transition.get(1), model.findStateById(transition.get(2)), actions);
        }
    }

    /**
     * 新接收到一个状态时，调用此函数。如果该状态不在全部状态列表中（尚未调用attach），则进行初始化
     */
    private void initStateIfNeeded(FSMState2... states) {
        for (FSMState2 state : states)
            if (state.getMachine()!=this)
                state.attach(this);
    }

    /**
     * 向状态机中添加转换时，调用此函数，如果此转换尚未存于model中，则存入
     */
    private void addTransToModelIfNeeded(FSMState2 preState, int event, FSMState2 postState, FSMAction2[] actions) {

        for (List<Integer> transition : model.getTransitionList())
            if (transition.get(0) == preState.getId() && transition.get(1) == event)
                return;//如果已经有这个转换了，就返回

        model.addTransition(preState, event, postState, actions);
    }

    public void configurationCompleted() {
        for (FSMState2 state : model.getAllStateList()) {
            if (state instanceof StateNeutral && state != model.getInitState())
                throw new RuntimeException("请勿自己创建StateNeutral，而是使用model.get获取");
            else if (state instanceof StateWaitForNeutral && state != model.getDefaultState())
                throw new RuntimeException("请勿自己创建StateWaitForNeutral，而是使用model.get获取");
        }
        //TODO 在这里调用attach是不是也行
        this.currentState.notifyBecomeActive();
    }

    public void addTransition(FSMState2 preState, int event, FSMState2 postState) {
        addTransition(preState, event, postState, new FSMAction2[0]);
    }

    public void addTransition(FSMState2 preState, int event, FSMState2 postState, FSMAction2... actions) {
        initStateIfNeeded(preState, postState);
        initStateIfNeeded(actions);
        addTransToModelIfNeeded(preState, event, postState, actions);

        if (preState instanceof FSMAction2 || postState instanceof FSMAction2)
            throw new RuntimeException("转移前后的state不能为action");
    }

    public boolean isActiveState(FSMState2 abstractFSMState) {
        synchronized (this) {
           return this.currentState == abstractFSMState;
        }
    }

    /**
     * 用于状态转移（sendEvent）时，装action列表
     */
    List<FSMAction2> actionsReuse = new ArrayList<>();
    public void sendEvent(FSMState2 preState, int fSMEvent) {
        synchronized (this) {
            TestHelper.assertTrue(preState == currentState,"当前状态非转移的preState");

            FSMState2 postState = model.getDefaultState();
            actionsReuse.clear();
            int preStateId = preState.getId();
            for(List<Integer> tran:model.getTransitionList()){
                if(tran.get(0) == preStateId && tran.get(1) == fSMEvent){
                    postState = model.findStateById(tran.get(2));
                    for(int i=3; i<tran.size(); i++)
                        actionsReuse.add(model.findStateById(tran.get(i)));
                    break;
                }
            }

            //输出logcat或供用户对照的屏幕文字
            for (FSMListener listener : listeners)
                listener.onTransition(preState, fSMEvent, postState, actionsReuse);

            //TODO 是synchronized导致的现象吗？如果把listener调用放在这里之后，且postState通知活跃后立刻sendEvent，
            // 会直接跳回到currentState.notifyBecomeInactive();没走listener那行？！（额应该是又进入sendEvent走到这的，不知道为啥调试器不是从第一行代码开始走，但单步调试看 上面代码应该是走了的）
            // 也就是说，目前会有sendEvent走到一半，又循环调用sendEvent的情况，这是否会引起异常？是否应该用单线程池按发送顺序sendEvent？
            currentState.notifyBecomeInactive();
            for (FSMAction2 action : actionsReuse) //执行过渡动作
                action.run();
            currentState = postState;
            currentState.notifyBecomeActive();
        }
    }


    //这个可以用来实现编辑下的状态监听
    public void addListener(FSMListener fSMListener) {
        if (!listeners.contains(fSMListener))
            listeners.add(fSMListener);
    }

    public void removeListener(FSMListener fSMListener) {
        listeners.remove(fSMListener);
    }

    public interface FSMListener {
        public void onTransition(FSMState2 preState, int event, FSMState2 postState, List<FSMAction2> actions);
    }
    public static class Logcat implements FSMListener {
        @Override
        public void onTransition(FSMState2 preState, int event, FSMState2 postState, List<FSMAction2> actions) {
            Log.d(TAG, String.format("sendEvent: 状态改变：%s --- %s --> %s, 操作: %s",
                    preState.getNiceName(), FSMR.getEventS(event), postState.getNiceName(), TestHelper.getActionsString(actions)));
        }
    }
//    public static class FSMTransitionTableEntry {
//        public final int event;
//        public final FSMState2 postState;
//        public final FSMState2 preState;
//        public final FSMAction2[] actions;
//
//        public FSMTransitionTableEntry(FSMState2 abstractFSMState, int fSMEvent, FSMState2 FSMState2, FSMAction2[] actions) {
//            this.preState = abstractFSMState;
//            this.event = fSMEvent;
//            this.postState = FSMState2;
//            this.actions = actions;
//        }
//    }
}