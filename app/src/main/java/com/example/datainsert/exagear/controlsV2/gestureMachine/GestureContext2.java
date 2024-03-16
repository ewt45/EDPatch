package com.example.datainsert.exagear.controlsV2.gestureMachine;

import com.example.datainsert.exagear.controlsV2.Const;
import com.example.datainsert.exagear.controlsV2.Finger;
import com.example.datainsert.exagear.controlsV2.TouchAdapter;
import com.example.datainsert.exagear.controlsV2.XServerViewHolder;
import com.example.datainsert.exagear.controlsV2.XZoomHandler;
import com.example.datainsert.exagear.controlsV2.axs.AndroidPointReporter;
import com.example.datainsert.exagear.controlsV2.touchAdapter.GestureDistributeAdapter;
import com.example.datainsert.exagear.controlsV2.touchArea.TouchAreaGesture;

import java.util.List;

public class GestureContext2 {
    private final XServerViewHolder host;
    private final TouchAreaGesture touchArea;
    private final GestureDistributeAdapter gestureTouchAdapter;
//    private KeyEventReporter keyboardReporter;
    private GestureMachine machine;

    public GestureContext2(TouchAreaGesture touchArea, GestureDistributeAdapter gestureTouchAdapter) {
        this.host = Const.getXServerHolder();
//        if (host != null)
//            this.keyboardReporter = new KeyEventReporter(host.getXServerFacade());
        this.touchArea = touchArea;
        this.gestureTouchAdapter = gestureTouchAdapter;
        Const.setGestureContext(this);
    }

    /**
     * 获取手指相关的位置，比如当前位置，初始按下时的位置等
     * <br/> fingerIndex为 {@link FSMR.value#手指位置_最后移动} 时，从Gesture的TouchAdapter获取历史记录坐标
     */
    public float[] getFingerXYByType(int type, int fingerIndex) {
        switch (type) {
            case FSMR.value.手指位置_最后移动:
                return getFingerEventsSource().getLatestFingerXY();
            case FSMR.value.手指位置_当前: {
                Finger finger = getFingers().get(fingerIndex);
                return new float[]{finger.getX(), finger.getY()};
            }
            case FSMR.value.手指位置_初始按下: {
                Finger finger = getFingers().get(fingerIndex);
                return new float[]{finger.getXWhenFirstTouched(), finger.getYWhenFirstTouched()};
            }

            default:
                throw new RuntimeException("不支持的XY类型");
        }
    }

    public GestureMachine getMachine() {
        return this.machine;
    }

    public void setMachine(GestureMachine finiteStateMachine) {
        this.machine = finiteStateMachine;
    }

    /**
     * @deprecated 请用 {@link FSMState2#addTouchListener(TouchAdapter)}
     */
    @Deprecated
    public GestureDistributeAdapter getFingerEventsSource() {
        return this.gestureTouchAdapter;
    }

    public TouchAreaGesture getTouchArea() {
        return this.touchArea;
    }


//    public KeyEventReporter getKeyboardReporter() {
//        return this.keyboardReporter;
//    }

    public List<Finger> getFingers() {
        return this.touchArea.getFingers();
    }


    public XServerViewHolder getXServerHolder() {
        return this.host;
    }

    public XZoomHandler getZoomController() {
        return this.host.getZoomController();
    }




}
