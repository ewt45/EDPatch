package com.example.datainsert.exagear.controls.interfaceOverlay;

import static com.eltechs.axs.helpers.AndroidHelpers.toggleSoftInput;
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
import com.example.datainsert.exagear.controls.interfaceOverlay.GuestAppActionAdapters.RelativeMouseMoveCstmSpdAdapter;
import com.example.datainsert.exagear.controls.interfaceOverlay.gesture.State1FMouseMove;
import com.example.datainsert.exagear.controls.interfaceOverlay.gesture.State1FMoveRel;
import com.example.datainsert.exagear.controls.interfaceOverlay.gesture.State2FDragAndDrop;
import com.example.datainsert.exagear.controls.interfaceOverlay.gesture.State2FScrollSyncRel;
import com.example.datainsert.exagear.controls.interfaceOverlay.gesture.State2FToZoomMove;
import com.example.datainsert.exagear.controls.interfaceOverlay.gesture.StateClickRel;

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
        //???????????????????????????????????????
        boolean isRelMove = sp.getBoolean(PREF_KEY_MOUSE_MOVE_RELATIVE, false);
        State1FMoveRel.isRelMove = isRelMove;
        //???????????????????????????
        RelativeMouseMoveCstmSpdAdapter.speedRatio = (20+sp.getInt(PREF_KEY_MOUSE_SENSITIVITY,80))/100f;

        GestureStateNeutral gestureStateNeutral = new GestureStateNeutral(gestureContext);
        GestureStateWaitForNeutral gestureStateWaitForNeutral = new GestureStateWaitForNeutral(gestureContext);
        //????????????0.0???????????????????????????????????????????????????
        float maxMove = 0.00f * (float) densityDpi;
//        GestureState1FingerMeasureSpeed gs1FingerMeasureSpeed = new GestureState1FingerMeasureSpeed(
//                gestureContext, fingerToLongTimeMs, maxMove, maxMove, maxMove, fingerToLongTimeMs);

        State1FMoveRel gs1FMoveRel = new State1FMoveRel(gestureContext, fingerToLongTimeMs, maxMove,false);

        StateClickRel gsLeftClickRel = new StateClickRel.SimpleBuilder().create(gestureContext,1,mouseActionSleepMs,pointerContext);
        StateClickRel gsRightClickRel = new StateClickRel.SimpleBuilder().create(gestureContext,3,mouseActionSleepMs,pointerContext);

        GestureStateClickToFingerFirstCoords gsClickAbl = new GestureStateClickToFingerFirstCoords(
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
                        pointerContext, 200));

        //1?????????
        GestureState1FingerMoveToScrollSync gs1FAbsScroll = new GestureState1FingerMoveToScrollSync(
                gestureContext,
                new ScrollAdapterMouseWheel(gestureContext.getPointerReporter()),
                0.05f * TransformationHelpers.getScaleX(viewOfXServer.getViewToXServerTransformationMatrix()),
                0.05f * TransformationHelpers.getScaleY(viewOfXServer.getViewToXServerTransformationMatrix()),
                0.0f, false, 0, scrollPeriodMs, true);

        //?????????????????????????????????
        int offsetWindow = sp.getInt(PREF_KEY_MOUSE_OFFWINDOW_DISTANCE,0);
        offsetWindow = isRelMove?offsetWindow:0;
        //????????????????????????pointer
        viewOfXServer.getXServerFacade().getXServer().getPointer().setOffWindowLimit(offsetWindow);
        //??????????????????????????????
        State1FMouseMove gs1FMoveMouseWaitDrag = new State1FMouseMove.SimpleBuilder().create(gestureContext,pointerContext,true,isRelMove && offsetWindow!=0);

        //??????????????????????????????
        State1FMoveRel gs2ndFMoveRel = new State1FMoveRel(gestureContext, fingerToLongTimeMs, maxMove,false);
        //??????????????????????????????
        State1FMoveRel gs3rdFMoveRel = new State1FMoveRel(gestureContext, fingerToLongTimeMs, maxMove,false);
        //????????????
        State2FScrollSyncRel gs2FScrollSyncRel = new State2FScrollSyncRel.SimpleBuilder().create(gestureContext,viewOfXServer,scrollPeriodMs);
        //????????????
        State2FDragAndDrop gs2FDragAndDrop = new State2FDragAndDrop.SimpleBuilder().create(gestureContext,3,mouseActionSleepMs,pointerContext);

        GestureState1FingerMeasureSpeed gs1FingerMeasureSpeed2 = new GestureState1FingerMeasureSpeed(gestureContext, 1000000, maxMove, maxMove, maxMove, 1000000.0f);

        GestureStateClickToFingerFirstCoords gsAbsRightClick = new GestureStateClickToFingerFirstCoords(
                gestureContext,
                new SimpleMousePointAndClickAdapter(
                        new SimpleMouseMoveAdapter(gestureContext.getPointerReporter()),
                        new PressAndReleaseMouseClickAdapter(gestureContext.getPointerReporter(), 3, mouseActionSleepMs),
                        pointerContext));
        //??????????????? 1???????????????
        GestureState1FingerMoveToMouseDragAndDrop gsAbsDrag = new GestureState1FingerMoveToMouseDragAndDrop(
                gestureContext,
                new SimpleDragAndDropAdapter(
                        new SimpleMouseMoveAdapter(gestureContext.getPointerReporter()),
                        new PressAndHoldMouseClickAdapter(gestureContext.getPointerReporter(), 1),
                        () -> gestureContext.getPointerReporter().click(3, 50)
                ),
                pointerContext, false, 0.0f);
        GestureStateWaitFingersNumberChangeWithTimeout wait2ndF = new GestureStateWaitFingersNumberChangeWithTimeout(gestureContext, maxTapTimeMs);
        //                AndroidHelpers::toggleSoftInput
        FSMStateRunRunnable toggleSoftInputRunnable = new FSMStateRunRunnable(()->toggleSoftInput());
        GestureState2FingersToZoom gs2FToZoom = new GestureState2FingersToZoom(gestureContext);
        GestureState1FingerToZoomMove gs1FToZoomMove = new GestureState1FingerToZoomMove(gestureContext);

        GestureState3FingersToZoom gs3FToZoom = new GestureState3FingersToZoom(gestureContext);
        State2FToZoomMove gs2FToZoomMove = new State2FToZoomMove(gestureContext);

        GestureStateWaitFingersNumberChangeWithTimeout wait3rdF = new GestureStateWaitFingersNumberChangeWithTimeout(gestureContext, maxTapTimeMs);


        FSMStateRunRunnable popupMenuRunnable = new FSMStateRunRunnable(() -> {
            if(popupMenu!=null){
                popupMenu.getMenu().clear();
                popupMenu.show();
            }
        });
        //            ((DefaultUIOverlay) DefaultTCF.this.mUIOverlay).toggleToolbar()
//            FalloutInterfaceOverlay2 interfaceOverlay2 = (FalloutInterfaceOverlay2)((XServerDisplayActivityConfigurationAware)
//            Globals.getApplicationState()).getXServerDisplayActivityInterfaceOverlay();


//        State1FMouseMove gs1FMoveMouseAndGone = new State1FMouseMove.SimpleBuilder().create(gestureContext,pointerContext,false);




        FiniteStateMachine fSM = new FiniteStateMachine();
        fSM.setStatesList(
                gestureStateWaitForNeutral,
                gestureStateNeutral,
//                gs1FingerMeasureSpeed,
                gsClickAbl,
                gs1FAbsScroll,
                gs1FingerMeasureSpeed2,
                gsAbsRightClick,
                gsAbsDrag,
                wait2ndF,
                toggleSoftInputRunnable,
                gs2FToZoom,
                gs1FToZoomMove,
                wait3rdF,
                popupMenuRunnable,
                gs1FMoveRel,
                gsLeftClickRel,
                gsRightClickRel,
                gs2ndFMoveRel,
                gs3rdFMoveRel,
                gs3FToZoom,
                gs2FScrollSyncRel,
                gs2FDragAndDrop,
                gs1FMoveMouseWaitDrag,
                gs2FToZoomMove

        );
        fSM.addTransition(gestureStateWaitForNeutral, GestureStateWaitForNeutral.GESTURE_COMPLETED, gestureStateNeutral);
        fSM.addTransition(gestureStateNeutral, GestureStateNeutral.FINGER_TOUCHED, gs1FMoveRel);
//        fSM.addTransition(stateCheckIsMoveRel, StateCheckIsMoveRel.MOVE_ABS, gs1FingerMeasureSpeed);
//        fSM.addTransition(stateCheckIsMoveRel, StateCheckIsMoveRel.MOVE_REL, gs1FingerMeasureSpeed);
        //?????????gs1FingerMeasureSpeed????????????????????????????????????????????????????????????????????????
        //        fSM.addTransition(gestureStateNeutral, GestureStateNeutral.FINGER_TOUCHED, gs1FingerMeasureSpeed);
        fSM.addTransition(gs1FMoveRel, State1FMoveRel.FINGER_TAPPED, gsClickAbl);
        fSM.addTransition(gs1FMoveRel, State1FMoveRel.FINGER_IMMEDIATE_MOVED, gs1FAbsScroll);
        fSM.addTransition(gs1FMoveRel, State1FMoveRel.NEW_FINGER_TOUCHED, wait2ndF);

        fSM.addTransition(gs1FMoveRel, State1FMoveRel.FINGER_LONG_TAPPED, gsAbsRightClick);//???????????????
        fSM.addTransition(gs1FMoveRel, State1FMoveRel.FINGER_LONGPRESSED_MOVED, gsAbsDrag);//?????????????????????

        fSM.addTransition(wait2ndF, GestureStateWaitFingersNumberChangeWithTimeout.FINGER_RELEASED, toggleSoftInputRunnable);
        fSM.addTransition(wait2ndF, GestureStateWaitFingersNumberChangeWithTimeout.TIMED_OUT, gs2FToZoom);
        fSM.addTransition(wait2ndF, GestureStateWaitFingersNumberChangeWithTimeout.FINGER_TOUCHED, wait3rdF);
        fSM.addTransition(gs2FToZoom, GestureState2FingersToZoom.FINGER_RELEASED, gs1FToZoomMove);
        fSM.addTransition(gs1FToZoomMove, GestureState1FingerToZoomMove.FINGER_TOUCHED, gs2FToZoom);
        fSM.addTransition(wait3rdF, GestureStateWaitFingersNumberChangeWithTimeout.FINGER_RELEASED, popupMenuRunnable);
        fSM.addTransition(wait3rdF, GestureStateWaitFingersNumberChangeWithTimeout.TIMED_OUT, popupMenuRunnable);

        //1. ???????????????
        //1.1 ????????????
        fSM.addTransition(gs1FMoveRel, State1FMoveRel.FINGER_IMMEDIATE_MOVED_REL, gs1FMoveMouseWaitDrag);
        fSM.addTransition(gs1FMoveRel, State1FMoveRel.FINGER_LONGPRESSED_MOVED_REL, gs1FMoveMouseWaitDrag);
        //1.2 ??????????????????
        fSM.addTransition(gs1FMoveRel,State1FMoveRel.FINGER_TAPPED_REL,gsLeftClickRel);
        //1.2 ????????????????????????????????????
//        fSM.addTransition(gs1FMoveRel,State1FMoveRel.FINGER_LONGPRESSED_STAND_REL,gs1FMoveMouseWaitDrag);
//        fSM.addTransition(gs1FMoveMouseWaitDrag,GestureState1FingerMoveToMouseMove.);
        //2. ?????????????????????????????????????????????????????????. ?????????????????????????????????????????????REL??????
        fSM.addTransition(gs1FMoveRel,State1FMoveRel.NEW_FINGER_TOUCHED_REL,gs2ndFMoveRel);
        fSM.addTransition(gs1FMoveMouseWaitDrag,State1FMouseMove.NEW_FINGER_TOUCHED,gs2ndFMoveRel);
        //2.1 ????????????
        fSM.addTransition(gs2ndFMoveRel,State1FMoveRel.FINGER_IMMEDIATE_MOVED_REL,gs2FScrollSyncRel);
//        fSM.addTransition(gs2FScrollSyncRel,State2FScrollSyncRel.FINGER_RELEASED, gs2ndFMoveRel);
        //2.2 ????????????
        fSM.addTransition(gs2ndFMoveRel,State1FMoveRel.FINGER_LONGPRESSED_MOVED_REL,gs2FDragAndDrop);
//        fSM.addTransition(gs2FDragAndDrop,State2FDragAndDrop.GESTURE_COMPLETED);

        //2.3 ??????????????????
        fSM.addTransition(gs2ndFMoveRel,State1FMoveRel.FINGER_TAPPED_REL, gsRightClickRel);
        fSM.addTransition(gs2ndFMoveRel,State1FMoveRel.FINGER_LONG_TAPPED_REL,gsRightClickRel);
        //3. ???????????????????????????????????????????????????????????????
        fSM.addTransition(gs2ndFMoveRel,State1FMoveRel.NEW_FINGER_TOUCHED_REL,gs3rdFMoveRel);
        //3.1 ??????
        fSM.addTransition(gs3rdFMoveRel,State1FMoveRel.FINGER_IMMEDIATE_MOVED_REL,gs3FToZoom);
        fSM.addTransition(gs3rdFMoveRel,State1FMoveRel.FINGER_LONGPRESSED_MOVED_REL,gs3FToZoom);
         //???????????????????????????????????????(emmm??????????????????????????????
        fSM.addTransition(gs3FToZoom, GestureState3FingersToZoom.FINGER_RELEASED,gs2FToZoomMove);
        fSM.addTransition(gs2FToZoomMove, State2FToZoomMove.FINGER_TOUCHED,gs3FToZoom);

        //3.2 ??????????????????
        fSM.addTransition(gs3rdFMoveRel,State1FMoveRel.FINGER_TAPPED_REL,popupMenuRunnable);
        fSM.addTransition(gs3rdFMoveRel,State1FMoveRel.FINGER_LONG_TAPPED_REL,popupMenuRunnable);



//        fSM.addTransition(gs1FMoveRel,State1FMoveRel.FINGER_LONG_TAPPED_REL,gsRightClickRel);

        fSM.setInitialState(gestureStateNeutral);
        fSM.setDefaultState(gestureStateWaitForNeutral);
        fSM.configurationCompleted();
        gestureContext.setMachine(fSM);
        return gestureContext;
    }
}
