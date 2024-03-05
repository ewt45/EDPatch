package com.example.datainsert.exagear.controlsV2.touchArea;

import android.graphics.Canvas;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;

import com.eltechs.axs.helpers.AndroidHelpers;
import com.eltechs.axs.xserver.Pointer;
import com.example.datainsert.exagear.controlsV2.Const;
import com.example.datainsert.exagear.controlsV2.Finger;
import com.example.datainsert.exagear.controlsV2.TestHelper;
import com.example.datainsert.exagear.controlsV2.TouchAdapter;
import com.example.datainsert.exagear.controlsV2.TouchArea;
import com.example.datainsert.exagear.controlsV2.gestureMachine.FSMAction2;
import com.example.datainsert.exagear.controlsV2.gestureMachine.FSMR;
import com.example.datainsert.exagear.controlsV2.gestureMachine.FSMState2;
import com.example.datainsert.exagear.controlsV2.gestureMachine.GestureContext2;
import com.example.datainsert.exagear.controlsV2.gestureMachine.GestureMachine;
import com.example.datainsert.exagear.controlsV2.gestureMachine.State.ActionButtonClick;
import com.example.datainsert.exagear.controlsV2.gestureMachine.State.ActionPointerMove;
import com.example.datainsert.exagear.controlsV2.gestureMachine.State.ActionRunOption;
import com.example.datainsert.exagear.controlsV2.gestureMachine.State.State1FingerMoveToMouseMove;
import com.example.datainsert.exagear.controlsV2.gestureMachine.State.State2FingersZoom;
import com.example.datainsert.exagear.controlsV2.gestureMachine.State.StateCheckFingerNearToPointer;
import com.example.datainsert.exagear.controlsV2.gestureMachine.State.StateCountDownMeasureSpeed;
import com.example.datainsert.exagear.controlsV2.gestureMachine.State.StateCountDownWaitFingerNumChange;
import com.example.datainsert.exagear.controlsV2.gestureMachine.State.StateFingerMoveToMouseScroll;
import com.example.datainsert.exagear.controlsV2.gestureMachine.State.StateNeutral;
import com.example.datainsert.exagear.controlsV2.gestureMachine.State.StateWaitForNeutral;
import com.example.datainsert.exagear.controlsV2.model.OneGestureArea;
import com.example.datainsert.exagear.controlsV2.options.OptionsProvider;
import com.example.datainsert.exagear.controlsV2.touchAdapter.GestureDistributeAdapter;
import com.example.datainsert.exagear.controlsV2.widget.TransitionHistoryView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TouchAreaGesture extends TouchArea<OneGestureArea> implements GestureMachine.FSMListener {
    private final GestureDistributeAdapter gestureAdapter;

    private final GestureContext2 gestureContext;
    private final List<List<String>> historyGestureList = new ArrayList<>(); //记录用户按下的历史手势，用于编辑时调试
    Handler mHandler = new Handler(Looper.getMainLooper());

    public TouchAreaGesture(@NonNull OneGestureArea data, @Nullable TouchAdapter adapter) {
        super(data, new GestureDistributeAdapter());
        gestureAdapter = (GestureDistributeAdapter) getAdapter();
        if (adapter != null)
            gestureAdapter.addListener(adapter); //编辑模式adapter也加入distributeAdapter中

        gestureContext = new GestureContext2(this, gestureAdapter);
        //完善model的初始化（由于反序列化要求无参构造 导致无法一次性初始化完成）
        getModel().init();
//        inflateFromCode();
        inflateMachineFromProfile();

        //编辑模式下，添加一个状态机的监听器，在状态转移时回调，将该转移显示到textview上
        if (Const.isEditing()) {
            gestureContext.getMachine().addListener(this);
        }


        //构建gestureArea的时候，宽高应该铺满屏幕，无视记录的数据
        DisplayMetrics metrics = Const.getContext().getResources().getDisplayMetrics();
        this.mModel.setWidth(metrics.widthPixels);
        this.mModel.setHeight(metrics.heightPixels);
    }

    @Override
    public void onDraw(Canvas canvas) {
        //TODO 编辑模式下，实时显示当前走到哪一阶段
    }

    /**
     * 从配置中读取状态机内容，构建状态机存入context
     */
    public void inflateMachineFromProfile() {
        OneGestureArea areaModel = getModel();
        GestureMachine machine = new GestureMachine();
        machine.setModel(areaModel);
        machine.configurationCompleted();
        gestureContext.setMachine(machine);
    }

    /**
     * 手势区域的构造函数中调用一次，构建状态机
     */
    public void inflateFromCode() {
//        ContextPointer pointerContext = new ContextPointer();
        OneGestureArea areaModel = getModel();

        DisplayMetrics displayMetrics = AndroidHelpers.getDisplayMetrics();
        int dpi = displayMetrics.densityDpi;
        float maxMovePx = Const.fingerStandingMaxMoveInches * dpi;
        float pointerOffsetAimInchesY = -0.5f;

        StateNeutral stateNeutral = areaModel.getInitState();
        StateWaitForNeutral stateWaitForNeutral = areaModel.getDefaultState();
//        State1FingerMeasureSpeed state1FingerMeasureSpeed = new State1FingerMeasureSpeed(gestureContext, 200, maxMovePx, maxMovePx, Const.fingerTapMaxMoveInches * dpi, 400.0f);
        StateCountDownMeasureSpeed stateWatch1F = new StateCountDownMeasureSpeed();
        stateWatch1F.setNiceName("1指短时间观测");
        stateWatch1F.mFingerIndex = 0;
        stateWatch1F.mCountDownMs = 250;
//        stateWatch1F.mNoMoveMaxDistance = 0.15f * Const.getDpi();


        //一指左键拖拽前同步鼠标到手指位置
        ActionPointerMove actionMouseMoveToFinger = new ActionPointerMove();
        actionMouseMoveToFinger.setNiceName("鼠标移动到手指位置");
        actionMouseMoveToFinger.mFingerIndex = FSMR.value.手指位置_最后移动;
        //左键按下和松开
        ActionButtonClick actionPressLeftMouse = new ActionButtonClick();
        actionPressLeftMouse.mDoPress = true;
        actionPressLeftMouse.mDoRelease = false;
        actionPressLeftMouse.mKeycode = Const.keycodePointerMask | Pointer.BUTTON_LEFT;
        ActionButtonClick stateReleaseLeftMouse = new ActionButtonClick();
        stateReleaseLeftMouse.mDoPress = false;
        stateReleaseLeftMouse.mDoRelease = true;
        stateReleaseLeftMouse.mKeycode = Const.keycodePointerMask | Pointer.BUTTON_LEFT;
        //一指左键拖拽移动
        State1FingerMoveToMouseMove state1FingerMoveToMouseMove = new State1FingerMoveToMouseMove();

        //一指鼠标滚轮
        StateFingerMoveToMouseScroll stateMouseScroll = new StateFingerMoveToMouseScroll();
        stateMouseScroll.mFingerIndex = 0;

        //左键单击
        ActionButtonClick actionLeftClick = new ActionButtonClick();
        actionLeftClick.mDoPress = true;
        actionLeftClick.mDoRelease = true;
        actionLeftClick.mKeycode = Const.keycodePointerMask | Pointer.BUTTON_LEFT;

        //第一次左键单击后，短时间内检查是否有第二次左键单击形成双击，如果有则第二次点击时不移动鼠标位置，防止微小的鼠标移动导致系统不识别双击
        StateCountDownWaitFingerNumChange stateWaitDoubleTapCountDown = new StateCountDownWaitFingerNumChange();
        stateWaitDoubleTapCountDown.setNiceName("点击一次后，短时间等待第二次点击");
        stateWaitDoubleTapCountDown.mCountDownMs = 250;

        StateCheckFingerNearToPointer checkIfNearPointer = new StateCheckFingerNearToPointer();
        checkIfNearPointer.mFingerIndex = 0;
        checkIfNearPointer.mDistThreshold = 12f; //经过测试，12f（安卓像素）比较合适

        //1指长按后，等待移动或松手
        StateCountDownMeasureSpeed stateWait1FActionAfterLongPress = new StateCountDownMeasureSpeed();
        stateWait1FActionAfterLongPress.setNiceName("1指长按后，等待移动或松手");
        stateWait1FActionAfterLongPress.mCountDownMs = 1000000;
        stateWait1FActionAfterLongPress.mFingerIndex = 0;

        //1指长按松手，鼠标右键
        ActionButtonClick actionRightClick = new ActionButtonClick();
        actionRightClick.setNiceName("右键");
        actionRightClick.mDoPress = true;
        actionRightClick.mDoRelease = true;
        actionRightClick.mKeycode = Const.keycodePointerMask | Pointer.BUTTON_RIGHT;
        //        GestureState1FingerToZoomMove gestureState1FingerToZoomMove = new GestureState1FingerToZoomMove(gestureContext);
//        GestureState2FingersToZoom gestureState2FingersToZoom = new GestureState2FingersToZoom(gestureContext);

        //2指按下，短时间测速
        StateCountDownMeasureSpeed stateWatch2F = new StateCountDownMeasureSpeed();
        stateWatch2F.setNiceName("2指按下，短时间测速");
        stateWatch2F.mCountDownMs = Const.fingerTapMaxMs;
        stateWatch2F.mFastMoveThreshold = stateWatch2F.mNoMoveThreshold;
        stateWatch2F.mFingerIndex = FSMR.value.观测手指序号_全部; //应该观测全部手指，比如只动第一根 不动第二根，这也算动了应该走缩放

        //2指短时间没反应，长时间观测
        StateCountDownMeasureSpeed stateWait2FDoSth = new StateCountDownMeasureSpeed();
        stateWait2FDoSth.setNiceName("2指按下后短时间没动，长时间测速");
        stateWait2FDoSth.mCountDownMs = 100000;
        stateWait2FDoSth.mFastMoveThreshold = stateWait2FDoSth.mNoMoveThreshold; //倒计时不会结束，只能靠fast_move来通知移动
        stateWait2FDoSth.mFingerIndex = FSMR.value.观测手指序号_全部;

        //2指点击事件，显示键盘
        ActionRunOption actionShowSoftInput = new ActionRunOption();
        actionShowSoftInput.mOptionType = OptionsProvider.OPTION_SHOW_SOFT_INPUT;

        //2指移动，缩放屏幕
        State2FingersZoom state2FingersZoom = new State2FingersZoom();
        state2FingersZoom.mFingerIndex1 = 0;
        state2FingersZoom.mFingerIndex2 = 1;

        //3指按下，观察
        StateCountDownMeasureSpeed stateWait3FDoSth = new StateCountDownMeasureSpeed();
        stateWait3FDoSth.mCountDownMs = 100000;
        stateWait3FDoSth.mFingerIndex = 2;

        //3指点击事件，显示菜单
        ActionRunOption actionShowAllOptions = new ActionRunOption();
        actionShowAllOptions.mOptionType = OptionsProvider.OPTION_SHOW_ALL_OPTIONS;

        GestureMachine machine = new GestureMachine();
        machine.setModel(areaModel);
        Log.e(TAG, "inflateFromCode: 错误操作：若从代码构建状态机则不应从json恢复那些状态");
        //TODO 如果以后要保留代码构建的话，就清空model的statelist（要么就代码直接保存json）

//        areaModel.addStates(
//                stateWatch1F,
////                gestureStateCheckFingerNearToPointer,
////                gestureStateCheckFingerNearToPointer2,
////                gestureState1FingerToLongClick,
////                gestureStateClickToFingerFirstCoords3,
////                gestureStateClickToFingerFirstCoords2,
////                gestureStateClickToFingerFirstCoords,
////                gestureStateWaitFingersNumberChangeWithTimeout,
////                gestureStateWaitFingersNumberChangeWithTimeout2,
////                gestureStateMouseWarpToFingerLastCoords,
////                gestureState1FingerMoveToScrollAsync,
////                gestureStateCheckIfZoomed,
////                fSMStateRunRunnable,
//                actionLeftClick,
//                stateMouseScroll,
//                actionMouseMoveToFinger,
//                state1FingerMoveToMouseMove,
//                actionPressLeftMouse,
//                stateReleaseLeftMouse,
//                stateWaitDoubleTapCountDown,
//                checkIfNearPointer,
//                stateWait1FActionAfterLongPress,
//                actionRightClick,
//                stateWatch2F,
//                stateWait2FDoSth,
//                actionShowSoftInput,
//                state2FingersZoom,
//                stateWait3FDoSth,
//                actionShowAllOptions
//        );


        machine.addTransition(stateWaitForNeutral, FSMR.event.完成, stateNeutral);
        //1指测速
        machine.addTransition(stateNeutral, FSMR.event.新手指按下, stateWatch1F);
        //1指点击，鼠标左键点击
        machine.addTransition(stateWatch1F, FSMR.event.某手指_未移动并松开, stateWaitDoubleTapCountDown, actionMouseMoveToFinger, actionLeftClick);
        //1指立刻移动，鼠标滚轮
        machine.addTransition(stateWatch1F, FSMR.event.手指_移动_慢速, stateMouseScroll, actionMouseMoveToFinger);
        machine.addTransition(stateWatch1F, FSMR.event.手指_移动_快速, stateMouseScroll, actionMouseMoveToFinger);

        //1指长按后，等待移动或松手
        machine.addTransition(stateWatch1F, FSMR.event.手指_未移动, stateWait1FActionAfterLongPress);
        //1指长按后松开，右键点击
        machine.addTransition(stateWait1FActionAfterLongPress, FSMR.event.某手指_未移动并松开, stateWaitForNeutral, actionMouseMoveToFinger, actionRightClick);

        //1指长按后移动，左键拖拽
//        machine.addTransition(stateWait1FActionAfterLongPress, StateCountDownMeasureSpeed.SLOW_MOVE, actionPressLeftMouse);//由于不会超时，所以不会产生slow_move事件
        machine.addTransition(stateWait1FActionAfterLongPress, FSMR.event.手指_移动_快速, state1FingerMoveToMouseMove, actionMouseMoveToFinger, actionPressLeftMouse);
        machine.addTransition(state1FingerMoveToMouseMove, FSMR.event.某手指松开, stateWaitForNeutral, stateReleaseLeftMouse);

        //第一次左键单击后，短时间内检查是否有第二次左键单击形成双击，如果有则第二次点击时不移动鼠标位置，防止微小的鼠标移动导致系统不识别双击
        machine.addTransition(stateWaitDoubleTapCountDown, FSMR.event.新手指按下, checkIfNearPointer);
        //如果距离近则跳过移动鼠标位置 直接点击左键，(更精确的话此时应该只是按下左键，等手指松开了才能松开左键，不过直接点击应该也没啥问题吧）
        machine.addTransition(checkIfNearPointer, FSMR.event.手指距离指针_近, stateWaitForNeutral, actionLeftClick);
        //否则仿佛从初始状态开始触摸手指一下，移动指针位置并走到检测1指那一步
        machine.addTransition(checkIfNearPointer, FSMR.event.手指距离指针_远, stateWatch1F, actionMouseMoveToFinger);

        //2指按下，观测
        machine.addTransition(stateWatch1F, FSMR.event.新手指按下, stateWatch2F);
//        machine.addTransition(stateMouseScroll, 新手指按下, stateWatch2F);
        machine.addTransition(stateWait1FActionAfterLongPress, FSMR.event.新手指按下, stateWatch2F);
        //2指不动，观察
        machine.addTransition(stateWatch2F, FSMR.event.手指_未移动, stateWait2FDoSth);

        //2指点击，二指事件（显示键盘）
        machine.addTransition(stateWatch2F, FSMR.event.某手指_未移动并松开, stateWaitForNeutral, actionShowSoftInput);
        machine.addTransition(stateWait2FDoSth, FSMR.event.某手指_未移动并松开, stateWaitForNeutral, actionShowSoftInput);
        //2指移动，缩放
        machine.addTransition(stateWatch2F, FSMR.event.手指_移动_慢速, state2FingersZoom);
        machine.addTransition(stateWatch2F, FSMR.event.手指_移动_快速, state2FingersZoom);
        machine.addTransition(stateWait2FDoSth, FSMR.event.手指_移动_快速, state2FingersZoom);

        //3指按下并松开，或者4指按下
        machine.addTransition(stateWatch2F, FSMR.event.新手指按下, stateWait3FDoSth);
        machine.addTransition(stateWait2FDoSth, FSMR.event.新手指按下, stateWait3FDoSth);

        machine.addTransition(stateWait3FDoSth, FSMR.event.某手指_未移动并松开, stateWaitForNeutral, actionShowAllOptions);
        machine.addTransition(stateWait3FDoSth, FSMR.event.新手指按下, stateWaitForNeutral, actionShowAllOptions);
        //TODO 尚未实现缩放后一指移动。
        // 三指点击后下滑触发截屏，必定触发三指点击操作，然后会卡死在waitNeutral(解决了，onTouchEvent里 cancel的时候遍历全部手指就行了）
        // 二指点击或三指点击的时候鼠标会跟着移动一下 （解决了，状态机在状态切换时，中间加个 执行附加动作）
//        machine.setInitialState(stateNeutral);
//        machine.setDefaultState(stateWaitForNeutral);
        machine.configurationCompleted();
        gestureContext.setMachine(machine);
//        machine.addTransition(state1FingerMeasureSpeed, GestureState1FingerMeasureSpeed.FINGER_TAPPED, gestureStateCheckFingerNearToPointer);
//        machine.addTransition(state1FingerMeasureSpeed, GestureState1FingerMeasureSpeed.FINGER_STANDING, gestureState1FingerToLongClick);
//        machine.addTransition(state1FingerMeasureSpeed, GestureState1FingerMeasureSpeed.FINGER_WALKED, gestureStateCheckFingerNearToPointer2);
//        machine.addTransition(state1FingerMeasureSpeed, GestureState1FingerMeasureSpeed.FINGER_FLASHED, gestureStateCheckFingerNearToPointer2);
//        machine.addTransition(state1FingerMeasureSpeed, GestureState1FingerMeasureSpeed.FINGER_TOUCHED, gestureStateWaitFingersNumberChangeWithTimeout);
//        machine.addTransition(state1FingerMeasureSpeed, GestureState1FingerMeasureSpeed.FINGER_WALKED_AND_GONE, gestureStateMouseWarpToFingerLastCoords);
//        machine.addTransition(gestureStateWaitFingersNumberChangeWithTimeout, GestureStateWaitFingersNumberChangeWithTimeout.FINGER_TOUCHED, gestureStateWaitFingersNumberChangeWithTimeout2);
//        machine.addTransition(gestureStateWaitFingersNumberChangeWithTimeout, GestureStateWaitFingersNumberChangeWithTimeout.FINGER_RELEASED, gestureStateClickToFingerFirstCoords3);
//        machine.addTransition(gestureStateWaitFingersNumberChangeWithTimeout, GestureStateWaitFingersNumberChangeWithTimeout.TIMED_OUT, gestureState2FingersToZoom);
//        machine.addTransition(gestureStateCheckFingerNearToPointer, GestureStateCheckFingerNearToPointer.NEAR, gestureStateClickToFingerFirstCoords);
//        machine.addTransition(gestureStateCheckFingerNearToPointer, GestureStateCheckFingerNearToPointer.FAR, gestureStateClickToFingerFirstCoords2);
//        machine.addTransition(gestureStateCheckFingerNearToPointer2, GestureStateCheckFingerNearToPointer.NEAR, state1FingerMoveToMouseMove);
//        machine.addTransition(gestureStateCheckFingerNearToPointer2, GestureStateCheckFingerNearToPointer.FAR, gestureStateCheckIfZoomed);
//        machine.addTransition(gestureStateWaitFingersNumberChangeWithTimeout2, GestureStateWaitFingersNumberChangeWithTimeout.FINGER_TOUCHED, fSMStateRunRunnable);
//        machine.addTransition(gestureStateCheckIfZoomed, GestureStateCheckIfZoomed.ZOOM_ON, gestureState1FingerToZoomMove);
//        machine.addTransition(gestureStateCheckIfZoomed, GestureStateCheckIfZoomed.ZOOM_OFF, gestureState1FingerMoveToScrollAsync);
//        machine.addTransition(gestureState2FingersToZoom, GestureState2FingersToZoom.FINGER_RELEASED, gestureState1FingerToZoomMove);
//        machine.addTransition(gestureState1FingerToZoomMove, GestureState1FingerToZoomMove.FINGER_TOUCHED, gestureState2FingersToZoom);

    }


    public List<Finger> getFingers() {
        return immutableActiveFingers;
    }

    public GestureContext2 getGestureContext() {
        return gestureContext;
    }

    @Override
    public void onTransition(FSMState2 preState, int event, FSMState2 postState, List<FSMAction2> actions) {
        historyGestureList.add(Arrays.asList(preState.getNiceName(), FSMR.getEventS(event), postState.getNiceName(), TestHelper.getActionsString(actions)));
        if (historyGestureList.size() > 200)
            historyGestureList.remove(0);

        mHandler.post(() ->  {
            TransitionHistoryView view = Const.getTouchView().getGestureHistoryTextView();
            if(view.getVisibility() == View.VISIBLE)
                view.addHistory(historyGestureList.get(historyGestureList.size()-1));
        });
    }
}
