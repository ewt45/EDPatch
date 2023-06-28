package com.example.datainsert.exagear.controls.axs.GestureStateMachine.v2;

import com.eltechs.axs.GestureStateMachine.AbstractGestureFSMState;
import com.eltechs.axs.GestureStateMachine.GestureContext;

import java.util.Map;

/**
 * 相对定位还是绝对定位写到子类里吧。然后在active时定位一下就行了吧
 */
public abstract class GState extends AbstractGestureFSMState {
    /**
     * 获取该手势所需的数据。用于构建输入框UI以及实例化手势
     */



    public GState(GestureContext gestureContext, Map<String, Object> map){
        super(gestureContext);

    }



}
