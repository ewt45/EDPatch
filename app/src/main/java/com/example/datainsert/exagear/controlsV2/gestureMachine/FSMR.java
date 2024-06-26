package com.example.datainsert.exagear.controlsV2.gestureMachine;

import static com.example.datainsert.exagear.RR.getS;
import static com.example.datainsert.exagear.RR.getSArr;
import static com.example.datainsert.exagear.controlsV2.Const.GsonField.st_countDownMs;
import static com.example.datainsert.exagear.controlsV2.Const.GsonField.st_fastMoveThreshold;
import static com.example.datainsert.exagear.controlsV2.Const.GsonField.st_fingerIndex;
import static com.example.datainsert.exagear.controlsV2.Const.GsonField.st_fingerXYType;
import static com.example.datainsert.exagear.controlsV2.Const.GsonField.st_ignorePixels;
import static com.example.datainsert.exagear.controlsV2.Const.GsonField.st_nearFarThreshold;
import static com.example.datainsert.exagear.controlsV2.Const.GsonField.st_noMoveThreshold;
import static com.example.datainsert.exagear.controlsV2.Const.GsonField.st_optionType;
import static com.example.datainsert.exagear.controlsV2.Const.GsonField.st_zoomFingerIndex1;
import static com.example.datainsert.exagear.controlsV2.Const.GsonField.st_zoomFingerIndex2;

import android.util.Log;
import android.util.SparseArray;

import com.example.datainsert.exagear.controlsV2.Const;
import com.example.datainsert.exagear.controlsV2.model.ModelProvider;
import com.example.datainsert.exagear.RR;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * helper类，存储field的tag和对应本地翻译
 * <br/> 由于注解需要值为final，所以一旦field设置了tag，就不要修改tag以及这个tag的值
 */
public class FSMR {
    private static final String TAG = "FSMR";

    private static final SparseArray<String> stateArr = new SparseArray<>();
    private static final Map<String, String> fieldArr = new HashMap<>();
    private static final SparseArray<String> eventIdArr = new SparseArray<>();
    private static final Map<int[],String[]> valueArr = new HashMap<>();

    static {
//        stateArr.put(state.一指测速, RR.getS(RR.一直测速));
        stateArr.put(FSMR.state.限时测速, getS(RR.fsm_state_testSpd));
        stateArr.put(FSMR.state.初始状态, getS(RR.fsm_state_init));
        stateArr.put(FSMR.state.回归初始状态, getS(RR.fsm_state_fallback));
        stateArr.put(FSMR.state.一指移动带动鼠标移动, getS(RR.fsm_state_1FMouseMove));
        stateArr.put(FSMR.state.操作_点击, getS(RR.fsm_state_action_click));
        stateArr.put(FSMR.state.操作_鼠标移动, getS(RR.fsm_state_action_msMove));
        stateArr.put(FSMR.state.手指移动_鼠标滚轮, getS(RR.fsm_state_msScroll));
        stateArr.put(FSMR.state.判断_手指与鼠标位置距离, getS(RR.fsm_state_distFingerMouse));
        stateArr.put(FSMR.state.监测手指数量变化, getS(RR.fsm_state_fingerNum));
        stateArr.put(FSMR.state.两根手指缩放, getS(RR.fsm_state_2FZoom));
        stateArr.put(state.操作_直接执行选项, getS(RR.fsm_state_action_option));
        stateArr.put(state.操作_切换鼠标左右键, getS(RR.fsm_state_action_switchMouseClickMode));
        stateArr.put(state.操作_根据模式点击鼠标按键, getS(RR.fsm_state_action_clickMouseByMode));

        eventIdArr.put(event.完成, getS(RR.fsm_event_complete));
        eventIdArr.put(event.某手指松开, getS(RR.fsm_event_release));
        eventIdArr.put(event.新手指按下, getS(RR.fsm_event_new_touch));
        eventIdArr.put(event.手指数量不变, getS(RR.fsm_event_finger_num_same));
        eventIdArr.put(event.手指距离指针_近, getS(RR.fsm_event_near));
        eventIdArr.put(event.手指距离指针_远, getS(RR.fsm_event_far));
        eventIdArr.put(event.手指_未移动, getS(RR.fsm_event_noMove));
        eventIdArr.put(event.手指_移动_慢速, getS(RR.fsm_event_slowMove));
        eventIdArr.put(event.手指_移动_快速, getS(RR.fsm_event_fastMove));
        eventIdArr.put(event.某手指_未移动并松开, getS(RR.fsm_event_noMoveThenRelease));
        eventIdArr.put(event.某手指_移动并松开, getS(RR.fsm_event_moveThenRelease));

        //用int[] 做key，可以正确获取到吗

        valueArr.put(value.手指位置_全部可用选项,RR.getSArr(RR.ctr2_FSMR_value_fingerXYType));//"任意一根手指最后一次位置改变时","手指初始按下时", "手指当前位置"
        valueArr.put(value.观测手指序号_全部可用选项,RR.getSArr(RR.ctr2_FSMR_value_fingerIndex));//"全部手指","1","2","3","4","5","6","7","8","9","10
        valueArr.put(value.鼠标移动逻辑_全部可用选项,RR.getSArr(RR.ctr2_FSMR_value_pointerMoveType)); //正常 视角转动

        fieldArr.put(st_fingerIndex, getS(RR.ctr2_stateProp_fingerIndex));
        fieldArr.put(st_fingerXYType, getS(RR.ctr2_stateProp_fingerXYType));
        fieldArr.put(st_optionType, getS(RR.ctr2_stateProp_option));
        fieldArr.put(st_ignorePixels, getS(RR.ctr2_stateProp_noMoveThreshold));
        fieldArr.put(st_zoomFingerIndex1, getSArr(RR.ctr2_stateProp_zoom2Finger)[0]);
        fieldArr.put(st_zoomFingerIndex2, getSArr(RR.ctr2_stateProp_zoom2Finger)[1]);
        fieldArr.put(st_nearFarThreshold, getS(RR.ctr2_stateProp_nearFarThres));
        fieldArr.put(st_noMoveThreshold, getS(RR.ctr2_stateProp_noMoveThreshold));
        fieldArr.put(st_fastMoveThreshold, getS(RR.ctr2_stateProp_fastMoveThres));
        fieldArr.put(st_countDownMs, getS(RR.ctr2_stateProp_countDown));

    }

    /**
     * 每个state的原始名称，并非用户自定义的别名。传入的是stateTag
     */
    public static String getStateS(int stateTag) {
        String str = stateArr.get(stateTag);
        if (str == null) {
            Log.e(TAG, "getStateS: 该状态没有对应文本：" + stateTag);
            str = ModelProvider.getStateClass(stateTag).getSimpleName();
        }
        return str;
    }

    /**
     * 返回state属性对应的名称以及说明。
     * @param fieldId 应该为 {@link com.example.datainsert.exagear.controlsV2.Const.GsonField} 中的值
     * @return 包含两个元素，第一个是名称。如果没有说明，第二个元素是null
     */
    public static String[] getFieldS(String fieldId) {
        String str = fieldArr.get(fieldId);
        assert str != null;
        if(str.contains(RR.STR_SPLIT_SYMBOL))
            return str.split(RR.STR_SPLIT_SYMBOL,2);
        return new String[]{str,null};
    }

    /**
     * 获取事件对应的字符串
     *
     * @param eventId 对应 {@link FSMR.event} 中的值
     * @return 从RR中获取对应字符串，若没有字符串则返回"event@"+eventId
     */
    public static String getEventS(int eventId) {
        String str = eventIdArr.get(eventId);
        if (str == null) {
            Log.e(TAG, "getStateS: 该状态没有对应文本：" + eventId);
            str = "event@" + eventId;
        }
        return str;
    }

    /**
     * 获取value数组对应的字符串数组
     * @param values 应该属于{@link FSMR.value} 中的一个数组
     */
    public static String[] getValuesS(int[] values) {
        String[] strArr = valueArr.get(values);
        if (strArr == null) {
            Log.e(TAG, "getValuesS: 该value数组没有对应文本：" + Arrays.toString(values));
            strArr = new String[values.length];
            Arrays.fill(strArr, "");
        }
        return strArr;
    }

    public static class state {
        public static final int 限时测速 = 1;
        public static final int 初始状态 = 2;
        public static final int 回归初始状态 = 3;
        public static final int 一指移动带动鼠标移动 = 4;
        public static final int 操作_点击 = 5;
        public static final int 操作_鼠标移动 = 6;
        public static final int 手指移动_鼠标滚轮 = 7;
        public static final int 判断_手指与鼠标位置距离 = 8;
        public static final int 监测手指数量变化 = 9;
        public static final int 两根手指缩放 = 10;
        public static final int 操作_直接执行选项 = 11;
        public static final int 操作_切换鼠标左右键 = 12;
        public static final int 操作_根据模式点击鼠标按键 = 13;
    }


    public static class event {

        public static final int 完成 = 1;
        public static final int 某手指松开 = 2;
        public static final int 新手指按下 = 3;
        public static final int 手指距离指针_近 = 4;
        public static final int 手指距离指针_远 = 5;
        /**
         * 时间段内移动距离小于等于standingFingerMaxMove，倒计时结束时发送此事件
         */
        public static final int 手指_未移动 = 6;
        /**
         * 时间段内移动距离超过standing但小于flash，倒计时结束时发送此事件
         */
        public static final int 手指_移动_慢速 = 7;
        /**
         * 如果在时间段内移动的距离超过了fastMinDistance，立刻发送此事件
         */
        public static final int 手指_移动_快速 = 8;
        /** 时间段内有手指松开且此时移动距离小于等于tapMaxDistance，立刻发送此事件。注意可能并非是观测的那根手指 */
        public static final int 某手指_未移动并松开 = 9;
        /** 时间段内有手指松开且此时移动距离超过tapMaxDistance，立刻发送此事件。注意可能并非是观测的那根手指 */
        public static final int 某手指_移动并松开 = 10;
        /**
         * 用于检测手指数量变化时，倒计时结束时手指数量没有变化
         */
        public static final int 手指数量不变 = 11;
    }

    public static class adapter {
        public static final int 鼠标移动_基础 = 1;
    }

    public static class value {
        /**
         * 可以用于唯一的手指已经松开，没法从Finger获取坐标的时候。会从历史记录（TouchAreaGesture的touchAdapter）中获取
         * <br/>也可以用于不确定手指个数时（所以这个可以设置成默认值了？）
         */
        public static final int 手指位置_最后移动 = -1;
        public static final int 手指位置_初始按下 = 1;
        public static final int 手指位置_当前 = 2;
        //要翻译的话，直接把这个数组作为key吧，单int值作为key容易重复还太多
        public static final int[] 手指位置_全部可用选项 = new int[]{手指位置_最后移动, 手指位置_初始按下, 手指位置_当前};


//        /**
//         * 将fingerIndex设置为此值时，StateCountDownMeasureSpeed不会产生移动事件
//         */
//        public static final int 观测手指序号_无 = -1;
        /**
         * 将fingerIndex设置为此值时，StateCountDownMeasureSpeed会观测全部手指
         */
        public static final int 观测手指序号_全部 = -2;
        public static final int[] 观测手指序号_全部可用选项 = new int[]{观测手指序号_全部,0,1,2,3,4,5,6,7,8,9};

        public static final int stateIdInvalid = -1;

        public static final int 鼠标移动逻辑_普通 = 1;
        public static final int 鼠标移动逻辑_视角转动 = 2;//使用时应注意是否实现
        public static final int[] 鼠标移动逻辑_全部可用选项 = new int[]{鼠标移动逻辑_普通, 鼠标移动逻辑_视角转动};

    }

}
