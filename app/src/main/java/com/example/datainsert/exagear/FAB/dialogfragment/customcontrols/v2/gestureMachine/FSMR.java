package com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.gestureMachine;

import android.util.SparseArray;
import android.util.SparseIntArray;

import com.example.datainsert.exagear.RR;

/**
 * helper类，存储field的tag和对应本地翻译
 * <br/> 由于注解需要值为final，所以一旦field设置了tag，就不要修改tag以及这个tag的值
 */
public class FSMR {


    private static final SparseArray<String> stateArr = new SparseArray<>();
    private static final SparseArray<String> fieldArr = new SparseArray<>();
    private static final SparseIntArray eventIdToStrId = new SparseIntArray();

    static {
//        stateArr.put(state.一指测速, RR.getS(RR.一直测速));

        eventIdToStrId.put(event.完成, RR.fsm_event_complete);
        eventIdToStrId.put(event.某手指松开, RR.fsm_event_release);
        eventIdToStrId.put(event.新手指按下, RR.fsm_event_new_touch);
        eventIdToStrId.put(event.手指距离指针_近, RR.fsm_event_near);
        eventIdToStrId.put(event.手指距离指针_远, RR.fsm_event_far);
        eventIdToStrId.put(event.手指_未移动, RR.fsm_event_noMove);
        eventIdToStrId.put(event.手指_移动_慢速, RR.fsm_event_slowMove);
        eventIdToStrId.put(event.手指_移动_快速, RR.fsm_event_fastMove);
        eventIdToStrId.put(event.某手指_未移动并松开, RR.fsm_event_noMoveThenRelease);
        eventIdToStrId.put(event.某手指_移动并松开, RR.fsm_event_moveThenRelease);

    }

    public static String getStateS(int id) {
        return stateArr.get(id);
    }

    public static String getFieldS(int id) {
        return fieldArr.get(id);
    }

    /**
     * 获取事件对应的字符串
     *
     * @param eventId 对应 {@link FSMR.event} 中的值
     * @return 从RR中获取对应字符串，若没有字符串则返回"eventId@"+eventId
     */
    public static String getEventS(int eventId) {
        int strId = eventIdToStrId.get(eventId);
        return strId == 0 ? "eventId@" + eventId : RR.getS(strId);
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
        /**
         * 时间段内有手指松开且此时移动距离小于等于tapMaxDistance，立刻发送此事件
         */
        public static final int 某手指_未移动并松开 = 9;
        /**
         * 时间段内有手指松开且此时移动距离超过tapMaxDistance，立刻发送此事件
         */
        public static final int 某手指_移动并松开 = 10;
        /**
         * 用于检测手指数量变化时，倒计时结束时手指数量没有变化
         */
        public static final int 手指数量不变 = 11;
    }

    public static class field {

        public static final int 一指测速_超时倒计时 = 1;
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


        /**
         * 将fingerIndex设置为此值时，StateCountDownMeasureSpeed不会产生移动事件
         */
        public static final int 观测手指序号_无 = -1;
        /**
         * 将fingerIndex设置为此值时，StateCountDownMeasureSpeed会观测全部手指
         */
        public static final int 观测手指序号_全部 = -2;
    }


}
