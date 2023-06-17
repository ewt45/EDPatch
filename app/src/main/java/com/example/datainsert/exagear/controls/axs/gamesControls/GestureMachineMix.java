package com.example.datainsert.exagear.controls.axs.gamesControls;

import static com.example.datainsert.exagear.controls.ControlsResolver.PREF_FILE_NAME_SETTING;
import static com.example.datainsert.exagear.controls.ControlsResolver.PREF_KEY_MOUSE_MOVE_RELATIVE;
import static com.example.datainsert.exagear.controls.ControlsResolver.PREF_KEY_MOUSE_OFFWINDOW_DISTANCE;
import static com.example.datainsert.exagear.controls.ControlsResolver.PREF_KEY_MOUSE_SENSITIVITY;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.PopupMenu;

import com.eltechs.axs.GestureStateMachine.GestureContext;
import com.eltechs.axs.GestureStateMachine.GestureState1FingerMeasureSpeed;
import com.eltechs.axs.GestureStateMachine.GestureState1FingerMoveToMouseDragAndDrop;
import com.eltechs.axs.GestureStateMachine.GestureState1FingerMoveToScrollSync;
import com.eltechs.axs.GestureStateMachine.GestureState1FingerToZoomMove;
import com.eltechs.axs.GestureStateMachine.GestureState2FingersToZoom;
import com.eltechs.axs.GestureStateMachine.GestureState3FingersToZoom;
import com.eltechs.axs.GestureStateMachine.GestureStateClickToFingerFirstCoords;
import com.eltechs.axs.GestureStateMachine.GestureStateNeutral;
import com.eltechs.axs.GestureStateMachine.GestureStateWaitFingersNumberChangeWithTimeout;
import com.eltechs.axs.GestureStateMachine.GestureStateWaitForNeutral;
import com.eltechs.axs.GestureStateMachine.PointerContext;
import com.eltechs.axs.GuestAppActionAdapters.AlignedMouseClickAdapter;
import com.eltechs.axs.GuestAppActionAdapters.MouseClickAdapterWithCheckPlacementContext;
import com.eltechs.axs.GuestAppActionAdapters.PressAndHoldMouseClickAdapter;
import com.eltechs.axs.GuestAppActionAdapters.PressAndReleaseMouseClickAdapter;
import com.eltechs.axs.GuestAppActionAdapters.ScrollAdapterMouseWheel;
import com.eltechs.axs.GuestAppActionAdapters.SimpleDragAndDropAdapter;
import com.eltechs.axs.GuestAppActionAdapters.SimpleMouseMoveAdapter;
import com.eltechs.axs.GuestAppActionAdapters.SimpleMousePointAndClickAdapter;
import com.eltechs.axs.TouchArea;
import com.eltechs.axs.TouchEventMultiplexor;
import com.eltechs.axs.finiteStateMachine.FiniteStateMachine;
import com.eltechs.axs.finiteStateMachine.generalStates.FSMStateRunRunnable;
import com.eltechs.axs.widgets.viewOfXServer.TransformationHelpers;
import com.eltechs.axs.widgets.viewOfXServer.ViewOfXServer;
import com.example.datainsert.exagear.controls.axs.GuestAppActionAdapters.RelativeMouseMoveCstmSpdAdapter;
import com.example.datainsert.exagear.controls.axs.GestureStateMachine.State1FMouseMove;
import com.example.datainsert.exagear.controls.axs.GestureStateMachine.State2FDragNDrop;
import com.example.datainsert.exagear.controls.axs.GestureStateMachine.State2FScrollSyncRel;
import com.example.datainsert.exagear.controls.axs.GestureStateMachine.State2FToZoomMove;
import com.example.datainsert.exagear.controls.axs.GestureStateMachine.StateClickRel;
import com.example.datainsert.exagear.controls.axs.GestureStateMachine.StateMesOneFSpd;
import com.example.datainsert.exagear.controls.menus.ShowKeyboardA11;

/**
 * 相对移动，尽量想办法触发多指触屏事件，能用测时尽量不用测速，如果用测速那么尽量移动了也走到新手指触屏事件，
 */
public class GestureMachineMix {
    private static final float clickAlignThresholdInches = 0.3f;
    private static final float doubleClickMaxDistanceInches = 0.15f;
    private static final int doubleClickMaxIntervalMs = 200;
    private static final float fingerStandingMaxMoveInches = 0.15f;
    private static final int fingerToLongTimeMs = 250;
    private static final int maxTapTimeMs = 300;
    private static final int mouseActionSleepMs = 50;
    private static final float pixelsInScrollUnit = 20.0f;
    private static final long scrollPeriodMs = 30;

    public static GestureContext create(ViewOfXServer viewOfXServer, TouchArea touchArea, TouchEventMultiplexor touchEventMultiplexor, int densityDpi, PopupMenu popupMenu) {
        final GestureContext gestureContext = new GestureContext(viewOfXServer, touchArea, touchEventMultiplexor);
        PointerContext pointerContext = new PointerContext();
        SharedPreferences sp = viewOfXServer.getContext().getSharedPreferences(PREF_FILE_NAME_SETTING, Context.MODE_PRIVATE);
        //设置是相对移动还是绝对移动
        boolean isRelMove = sp.getBoolean(PREF_KEY_MOUSE_MOVE_RELATIVE, false);
//        State1FMoveRel.isRelMove = isRelMove;
        //设置相对移动的速度
        RelativeMouseMoveCstmSpdAdapter.speedRatio = (20 + sp.getInt(PREF_KEY_MOUSE_SENSITIVITY, 80)) / 100f;


        FiniteStateMachine fSM = new FiniteStateMachine();
        GestureStateNeutral gestureStateNeutral = new GestureStateNeutral(gestureContext);
        GestureStateWaitForNeutral gestureStateWaitForNeutral = new GestureStateWaitForNeutral(gestureContext);
        FSMStateRunRunnable toggleSoftInputRunnable = new FSMStateRunRunnable(() -> new ShowKeyboardA11().run());
        FSMStateRunRunnable popupMenuRunnable = new FSMStateRunRunnable(() -> {
            if (popupMenu != null) {
                popupMenu.getMenu().clear();
                popupMenu.show();
            }
        });


        if (!isRelMove) {
            float maxMove = 0.15f * (float) densityDpi;
            //1指测速 (长按进入第二个1指测速）
            GestureState1FingerMeasureSpeed gs1FMesSpd = new GestureState1FingerMeasureSpeed(gestureContext, fingerToLongTimeMs, maxMove, maxMove, maxMove, fingerToLongTimeMs);
            //第2个1指测速
            GestureState1FingerMeasureSpeed gs1FMesSpd2 = new GestureState1FingerMeasureSpeed(gestureContext, 1000000, maxMove, maxMove, maxMove, 1000000);

            //1指点击 左键点击
            GestureStateClickToFingerFirstCoords gsLeftClick = new GestureStateClickToFingerFirstCoords(
                    gestureContext,
                    new MouseClickAdapterWithCheckPlacementContext(
                            new SimpleMousePointAndClickAdapter(
                                    new SimpleMouseMoveAdapter(gestureContext.getPointerReporter()),
                                    new PressAndReleaseMouseClickAdapter(gestureContext.getPointerReporter(), 1, mouseActionSleepMs),
                                    pointerContext),
                            new AlignedMouseClickAdapter(
                                    new SimpleMouseMoveAdapter(gestureContext.getPointerReporter()),
                                    new PressAndReleaseMouseClickAdapter(gestureContext.getPointerReporter(), 1, mouseActionSleepMs),
                                    new PressAndReleaseMouseClickAdapter(gestureContext.getPointerReporter(), 1, mouseActionSleepMs),
                                    viewOfXServer,
                                    pointerContext,
                                    densityDpi * clickAlignThresholdInches),
                            new AlignedMouseClickAdapter(
                                    new SimpleMouseMoveAdapter(gestureContext.getPointerReporter()),
                                    new PressAndReleaseMouseClickAdapter(gestureContext.getPointerReporter(), 1, mouseActionSleepMs),
                                    new PressAndReleaseMouseClickAdapter(gestureContext.getPointerReporter(), 1, mouseActionSleepMs),
                                    viewOfXServer,
                                    pointerContext,
                                    densityDpi * clickAlignThresholdInches),
                            pointerContext, doubleClickMaxIntervalMs));
            //1指滚动
            GestureState1FingerMoveToScrollSync gs1Scroll = new GestureState1FingerMoveToScrollSync(
                    gestureContext,
                    new ScrollAdapterMouseWheel(gestureContext.getPointerReporter()),
                    0.05f * TransformationHelpers.getScaleX(viewOfXServer.getViewToXServerTransformationMatrix()),
                    0.05f * TransformationHelpers.getScaleY(viewOfXServer.getViewToXServerTransformationMatrix()),
                    0.0f, false, 0, scrollPeriodMs, true);

//            State1FMouseMove gs1Scroll = new State1FMouseMoveFromCenterWithLeftClick.SimpleBuilder().create(gestureContext,pointerContext,true);

            //1指长按=右键
            GestureStateClickToFingerFirstCoords gsRightClick = new GestureStateClickToFingerFirstCoords(
                    gestureContext,
                    new SimpleMousePointAndClickAdapter(
                            new SimpleMouseMoveAdapter(gestureContext.getPointerReporter()),
                            new PressAndReleaseMouseClickAdapter(gestureContext.getPointerReporter(), 3, mouseActionSleepMs),
                            pointerContext));

            //1指长按拖拽
            GestureState1FingerMoveToMouseDragAndDrop gsDrag = new GestureState1FingerMoveToMouseDragAndDrop(
                    gestureContext,
                    new SimpleDragAndDropAdapter(
                            new SimpleMouseMoveAdapter(gestureContext.getPointerReporter()),
                            new PressAndHoldMouseClickAdapter(gestureContext.getPointerReporter(), 1),
                            () -> gestureContext.getPointerReporter().click(3, 50)
                    ),
                    pointerContext, false, 0.0f);

            //二指缩放
            GestureStateWaitFingersNumberChangeWithTimeout gsListen2ndF = new GestureStateWaitFingersNumberChangeWithTimeout(gestureContext, maxTapTimeMs);
            GestureState2FingersToZoom gs2FToZoom = new GestureState2FingersToZoom(gestureContext);
            GestureState1FingerToZoomMove gs1FToZoomMove = new GestureState1FingerToZoomMove(gestureContext);

            //等待第三根手指 显示菜单
            GestureStateWaitFingersNumberChangeWithTimeout gsListen3rdF = new GestureStateWaitFingersNumberChangeWithTimeout(gestureContext, maxTapTimeMs);

            //设置全部list
            fSM.setStatesList(
                    gestureStateNeutral,
                    gestureStateWaitForNeutral,
                    popupMenuRunnable,
                    toggleSoftInputRunnable,
                    gs1FMesSpd,
                    gs1FMesSpd2,
                    gsLeftClick,
                    gs1Scroll,
                    gsRightClick,
                    gsDrag,
                    gsListen2ndF,
                    gs2FToZoom,
                    gs1FToZoomMove,
                    gsListen3rdF
            );

            fSM.addTransition(gestureStateWaitForNeutral, GestureStateWaitForNeutral.GESTURE_COMPLETED, gestureStateNeutral);
            fSM.addTransition(gestureStateNeutral, GestureStateNeutral.FINGER_TOUCHED, gs1FMesSpd);
            //1指点击=左键
            fSM.addTransition(gs1FMesSpd, GestureState1FingerMeasureSpeed.FINGER_TAPPED, gsLeftClick);
            //1指滑动=滚轮
            fSM.addTransition(gs1FMesSpd, GestureState1FingerMeasureSpeed.FINGER_WALKED, gs1Scroll);
            fSM.addTransition(gs1FMesSpd, GestureState1FingerMeasureSpeed.FINGER_FLASHED, gs1Scroll);
            //1指长按后
            fSM.addTransition(gs1FMesSpd, GestureState1FingerMeasureSpeed.FINGER_STANDING, gs1FMesSpd2);
            //1指长按后松手=右键
            fSM.addTransition(gs1FMesSpd2, GestureState1FingerMeasureSpeed.FINGER_TAPPED, gsRightClick);
            //1指长按后移动=拖拽
            fSM.addTransition(gs1FMesSpd2, GestureState1FingerMeasureSpeed.FINGER_WALKED, gsDrag);
            fSM.addTransition(gs1FMesSpd2, GestureState1FingerMeasureSpeed.FINGER_FLASHED, gsDrag);

            //第二个手指按下后, 短时间内手指个数变化情况
            fSM.addTransition(gs1FMesSpd, GestureState1FingerMeasureSpeed.FINGER_TOUCHED, gsListen2ndF);
            //手指松开，进入二指点击事件
            fSM.addTransition(gsListen2ndF, GestureStateWaitFingersNumberChangeWithTimeout.FINGER_RELEASED, toggleSoftInputRunnable);
            //没变化，开始二指缩放
            fSM.addTransition(gsListen2ndF, GestureStateWaitFingersNumberChangeWithTimeout.TIMED_OUT, gs2FToZoom);
            fSM.addTransition(gs2FToZoom, GestureState2FingersToZoom.FINGER_RELEASED, gs1FToZoomMove);
            fSM.addTransition(gs1FToZoomMove, GestureState1FingerToZoomMove.FINGER_TOUCHED, gs2FToZoom);
            //新手指点击，进入新的监听
            fSM.addTransition(gsListen2ndF, GestureStateWaitFingersNumberChangeWithTimeout.FINGER_TOUCHED, gsListen3rdF);

            //第三个手指按下后, 短时间内手指个数变化情况
            fSM.addTransition(gsListen3rdF, GestureStateWaitFingersNumberChangeWithTimeout.FINGER_RELEASED, popupMenuRunnable);
            fSM.addTransition(gsListen3rdF, GestureStateWaitFingersNumberChangeWithTimeout.TIMED_OUT, popupMenuRunnable);

        }


        //触摸板模式
        /*
         * 想快速单击两次变为拖拽的话如果鼠标先点击了一次，相隔很短的时间里，第二次鼠标即使只是按下，系统也会识别为双击
         * （而且是鼠标输入的时候，触摸板输入不管两次相隔多远都会进入拖拽而不是双击，那看来要修改底层输入了）
         */
        else {
            //不设置为0.0的话，相对移动小距离时光标不会移动
            float maxMove = 0.00f * (float) densityDpi;
            int pressSecondTimeToDragMs = 100; //第一根手指松开后，相隔多久再次按下会进入拖拽
            //是否开启视角转动模式。
            int offsetWindow = sp.getInt(PREF_KEY_MOUSE_OFFWINDOW_DISTANCE, 0);
            viewOfXServer.getXServerFacade().getXServer().getPointer().setOffWindowLimit(offsetWindow); //将移出距离设置给pointer


            //第一根手指按下，测速
            StateMesOneFSpd gsMesSpd1stF = new StateMesOneFSpd(
                    gestureContext, fingerToLongTimeMs, maxMove, maxMove, maxMove, 0);
            //第一根手指移动、超时 = 移动鼠标
            State1FMouseMove gs1FMouseMove = new State1FMouseMove.SimpleBuilder().create(gestureContext, pointerContext, true);
            //第一根手指松开=鼠标左键点击
            StateClickRel gsLeftClick = new StateClickRel.SimpleBuilder().create(gestureContext, 1, mouseActionSleepMs, pointerContext);
            //第一根手指松开后，检测是否有第一根手指再次按下（准备拖拽）
            GestureStateWaitFingersNumberChangeWithTimeout gsWait1stFForDrag =
                    new GestureStateWaitFingersNumberChangeWithTimeout(gestureContext,pressSecondTimeToDragMs);
//            //1指长按拖拽
//            State1FDragNDropRel gs2FDragAndDrop = new State1FDragNDropRel.SimpleBuilder().create(gestureContext,pointerContext);

            //第二根手指按下，测速（限时0.25秒）
            StateMesOneFSpd gsMesSpd2ndF = new StateMesOneFSpd(
                    gestureContext, fingerToLongTimeMs, maxMove, maxMove, maxMove, 1);
            //第二个手指松开 = 鼠标右键点击
            StateClickRel gsRightClick = new StateClickRel.SimpleBuilder().create(gestureContext, 3, mouseActionSleepMs, pointerContext);
            //第二个手指移动 = 二指鼠标滚动
            State2FScrollSyncRel gs2FScroll = new State2FScrollSyncRel.SimpleBuilder().create(gestureContext, viewOfXServer, scrollPeriodMs);
            //第二根手指长按后的测速（限时无限）
            StateMesOneFSpd gsMesSpd2ndFLgPrs = new StateMesOneFSpd(
                    gestureContext, 1000000, maxMove, maxMove, maxMove, 1);
            //2指长按拖拽
            State2FDragNDrop gs2FDragAndDrop = new State2FDragNDrop.SimpleBuilder().create(gestureContext,pointerContext,true);

            //第三根手指按下 测时
            GestureStateWaitFingersNumberChangeWithTimeout gsWait3rdFToZoom =
                    new GestureStateWaitFingersNumberChangeWithTimeout(gestureContext,500);
            //超时未松开，三指缩放
            GestureState3FingersToZoom gs3FToZoom = new GestureState3FingersToZoom(gestureContext);
            State2FToZoomMove gs2FToZoomMove = new State2FToZoomMove(gestureContext);


            //            ((DefaultUIOverlay) DefaultTCF.this.mUIOverlay).toggleToolbar()
//            FalloutInterfaceOverlay2 interfaceOverlay2 = (FalloutInterfaceOverlay2)((XServerDisplayActivityConfigurationAware)
//            Globals.getApplicationState()).getXServerDisplayActivityInterfaceOverlay();



            fSM.setStatesList(
                    gestureStateWaitForNeutral,
                    gestureStateNeutral,
                    gsMesSpd1stF,
                    gs1FMouseMove,
                    gsLeftClick,
                    gsMesSpd2ndF,
                    gsRightClick,
                    gs2FScroll,
                    gsMesSpd2ndFLgPrs,
                    gs2FDragAndDrop,
                    gsWait3rdFToZoom,
                    gs3FToZoom,
                    gs2FToZoomMove,
                    popupMenuRunnable,
                    toggleSoftInputRunnable
            );
            fSM.addTransition(gestureStateWaitForNeutral, GestureStateWaitForNeutral.GESTURE_COMPLETED, gestureStateNeutral);
            fSM.addTransition(gestureStateNeutral, GestureStateNeutral.FINGER_TOUCHED, gsMesSpd1stF);

            //1. 第一个手指
            //1.1 移动鼠标
            fSM.addTransition(gsMesSpd1stF,StateMesOneFSpd.FINGER_STANDING,gs1FMouseMove);
            fSM.addTransition(gsMesSpd1stF,StateMesOneFSpd.FINGER_WALKED,gs1FMouseMove);
            fSM.addTransition(gsMesSpd1stF,StateMesOneFSpd.FINGER_FLASHED,gs1FMouseMove);
            //1.2 鼠标左键点击
            fSM.addTransition(gsMesSpd1stF, StateMesOneFSpd.FINGER_TAPPED, gsLeftClick);
//            fSM.addTransition(gsWait1stFForDrag, GestureStateWaitFingersNumberChangeWithTimeout.TIMED_OUT, gsLeftClick);
//            //1.2 如果快速再次点击，进入1指左键拖拽
//            fSM.addTransition(gsWait1stFForDrag,GestureStateWaitFingersNumberChangeWithTimeout.FINGER_TOUCHED,gs2FDragAndDrop);

            //2. 第二个手指按下监听动作，监听第三个手指. 相对移动从子分支开始都只需要写REL的吧
            fSM.addTransition(gsMesSpd1stF, StateMesOneFSpd.FINGER_TOUCHED, gsMesSpd2ndF);
            fSM.addTransition(gs1FMouseMove, State1FMouseMove.NEW_FINGER_TOUCHED, gsMesSpd2ndF);
            //2.1 第二个手指松开 二指点击事件 = 鼠标右键点击
            fSM.addTransition(gsMesSpd2ndF,StateMesOneFSpd.FINGER_TAPPED,gsRightClick);
            //2.2 第二个手指移动 = 鼠标滚轮
            fSM.addTransition(gsMesSpd2ndF,StateMesOneFSpd.FINGER_WALKED,gs2FScroll);
            fSM.addTransition(gsMesSpd2ndF,StateMesOneFSpd.FINGER_FLASHED,gs2FScroll);
            //2.3 第二个手指长按 = 二指鼠标左键拖拽
            fSM.addTransition(gsMesSpd2ndF,StateMesOneFSpd.FINGER_STANDING,gsMesSpd2ndFLgPrs);
            fSM.addTransition(gsMesSpd2ndFLgPrs,StateMesOneFSpd.FINGER_WALKED,gs2FDragAndDrop);
            fSM.addTransition(gsMesSpd2ndFLgPrs,StateMesOneFSpd.FINGER_FLASHED,gs2FDragAndDrop);

            //第二个手指松开后回到一指滑动
            fSM.addTransition(gsRightClick,StateClickRel.GESTURE_COMPLETED,gs1FMouseMove);//右键后变回一指滑动
            fSM.addTransition(gs2FScroll, State2FScrollSyncRel.GESTURE_COMPLETED,gs1FMouseMove); //松开变回一指滑动
            fSM.addTransition(gsMesSpd2ndFLgPrs,StateMesOneFSpd.FINGER_TAPPED,gs1FMouseMove);
            fSM.addTransition(gs2FDragAndDrop, State2FDragNDrop.FINGER_RELEASED,gs1FMouseMove);

            //3 第三个手指按下 测时 （不用测速，因为有的人没法保持送手前不移动手指）
            fSM.addTransition(gsMesSpd2ndF,StateMesOneFSpd.FINGER_TOUCHED,gsWait3rdFToZoom);
            fSM.addTransition(gs2FScroll,State2FScrollSyncRel.FINGER_TOUCHED,gsWait3rdFToZoom);
            fSM.addTransition(gsMesSpd2ndFLgPrs,StateMesOneFSpd.FINGER_TOUCHED,gsWait3rdFToZoom);
            fSM.addTransition(gs2FDragAndDrop, State2FDragNDrop.FINGER_TOUCHED,gsWait3rdFToZoom);

            //3.1 超时未松开，三指缩放
            fSM.addTransition(gsWait3rdFToZoom,GestureStateWaitFingersNumberChangeWithTimeout.TIMED_OUT, gs3FToZoom);
            fSM.addTransition(gs3FToZoom, GestureState3FingersToZoom.FINGER_RELEASED, gs2FToZoomMove);//三指缩放变二指移动
            fSM.addTransition(gs2FToZoomMove, State2FToZoomMove.FINGER_TOUCHED, gs3FToZoom);

            //3.2 0.5秒内松开或者第四根手指点击 = 三指触屏事件 = 显示弹窗菜单
            fSM.addTransition(gsWait3rdFToZoom,GestureStateWaitFingersNumberChangeWithTimeout.FINGER_TOUCHED, popupMenuRunnable);
            fSM.addTransition(gsWait3rdFToZoom,GestureStateWaitFingersNumberChangeWithTimeout.FINGER_RELEASED, popupMenuRunnable);

        }


        fSM.setInitialState(gestureStateNeutral);
        fSM.setDefaultState(gestureStateWaitForNeutral);
        fSM.configurationCompleted();
        gestureContext.setMachine(fSM);
        return gestureContext;
    }


}
