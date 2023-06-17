package com.example.datainsert.exagear.controls.axs.GestureStateMachine.v2;

import com.eltechs.axs.finiteStateMachine.FSMEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 存储数据
 */
public class ParamsModel {
    private final Map<String, Object> minorParams = new HashMap<>();
    private final Map<String, Object> majorParams = new HashMap<>();

    /**
     * 手势名，用于确定使用哪个java类
     */
    private String stateName;
    /**
     * 转移条件（这个不需要，直接从transitions里取？）
     */
    private final List<FSMEvent> fsmEvents = new ArrayList<>();
    /**
     * 转移事件及下一状态
     */
    private final Map<FSMEvent,GState> transitions = new HashMap<>();


    public Map<String, Object> getMinorParams() {
        return minorParams;
    }

    public Map<String, Object> getMajorParams() {
        return majorParams;
    }
    public void setParam(String s, Object o, boolean isMajor){
        if(isMajor)
            majorParams.put(s,o);
        else
            minorParams.put(s,o);
    }

    public Object getParam(String s){
        Object o;
        o = majorParams.get(s);
        if(o==null)
            o=minorParams.get(s);

        return o;
    }
}
