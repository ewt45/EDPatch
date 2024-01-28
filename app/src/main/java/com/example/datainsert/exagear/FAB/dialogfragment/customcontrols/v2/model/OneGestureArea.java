package com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.model;

import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.Const;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.gestureMachine.AbstractFSMAction2;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.gestureMachine.AbstractFSMState2;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.gestureMachine.FSMR;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class OneGestureArea extends TouchAreaModel{
    //TODO statemachine的table和defaultState啥的都放这里
    @SerializedName(value = Const.GsonField.md_fsmTable)
    //TODO 由于状态转移的下一个状态可能是之前出现过的旧状态而非新状态，所以需要一个唯一id来标识每个状态。
    //这个id应该只需要在反序列化时用到，用户编辑时可以直接显示选项时直接将实例与选项绑定起来。
    private List<FSMTransitionTableEntry> fsmTable;

    public OneGestureArea(){
        super(TYPE_GESTURE);
        fsmTable = new ArrayList<>();
    }

    /**
     * 创建状态机时用到table
     */
    public List<FSMTransitionTableEntry> getFSMTable() {
        return fsmTable;
    }

    public static class FSMTransitionTableEntry {
        public final int event;
        public final AbstractFSMState2 postState;
        public final AbstractFSMState2 preState;
        //TODO 注意这个actions有先后顺序之分。写用户ui的时候记得添加调整顺序的功能
        public final AbstractFSMAction2[] actions;

        public FSMTransitionTableEntry(AbstractFSMState2 abstractFSMState, int fSMEvent, AbstractFSMState2 abstractFSMState2, AbstractFSMAction2[] actions) {
            this.preState = abstractFSMState;
            this.event = fSMEvent;
            this.postState = abstractFSMState2;
            this.actions = actions;
        }
    }
}
