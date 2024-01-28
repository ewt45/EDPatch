package com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.gestureMachine;

import com.eltechs.axs.KeyEventReporter;
import com.eltechs.axs.widgets.viewOfXServer.ViewOfXServer;
import com.eltechs.axs.widgets.viewOfXServer.XZoomController;
import com.eltechs.axs.xserver.ViewFacade;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.Const;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.Finger;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.TouchAdapter;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.gestureMachine.adapter.AndroidPointReporter;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.touchAdapter.GestureDistributeAdapter;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.touchArea.TouchAreaGesture;

import java.lang.ref.WeakReference;
import java.util.List;

public class ContextGesture {
    private final ViewOfXServer host;
    private final AndroidPointReporter pointerEventReporter;
    private final TouchAreaGesture touchArea;
    private final GestureDistributeAdapter gestureTouchAdapter;
    private KeyEventReporter keyboardReporter;
    private GestureMachine machine;

    public ContextGesture(TouchAreaGesture touchArea, GestureDistributeAdapter gestureTouchAdapter) {
        this.host = Const.viewOfXServerRef.get();
        this.pointerEventReporter = new AndroidPointReporter();
        if (host != null)
            this.keyboardReporter = new KeyEventReporter(host.getXServerFacade());
        this.touchArea = touchArea;
        this.gestureTouchAdapter = gestureTouchAdapter;
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
     * @deprecated 请用 {@link AbstractFSMState2#addTouchListener(TouchAdapter)}
     */
    @Deprecated
    public GestureDistributeAdapter getFingerEventsSource() {
        return this.gestureTouchAdapter;
    }

    public TouchAreaGesture getTouchArea() {
        return this.touchArea;
    }

    public AndroidPointReporter getPointerReporter() {
        return this.pointerEventReporter;
    }

    public KeyEventReporter getKeyboardReporter() {
        return this.keyboardReporter;
    }

    public List<Finger> getFingers() {
        return this.touchArea.getFingers();
    }

    public ViewFacade getViewFacade() {
        return this.host.getXServerFacade();
    }

    public ViewOfXServer getHostView() {
        return this.host;
    }

    /**
     * @deprecated 日后实现自己的zoomController
     */
    @Deprecated
    public XZoomController getZoomController() {
        return this.host.getZoomController();
    }
}
