//package com.eltechs.axs.gamesControls;
//
//import com.eltechs.axs.GestureStateMachine.GestureContext;
//import com.eltechs.axs.GestureStateMachine.GestureState1FingerMeasureSpeed;
//import com.eltechs.axs.GestureStateMachine.GestureState1FingerMoveToMouseMoveWithWait;
//import com.eltechs.axs.GestureStateMachine.GestureState2FingerMeasureSpeed;
//import com.eltechs.axs.GestureStateMachine.GestureState2FingerMoveToMouseMoveWithClick;
//import com.eltechs.axs.GestureStateMachine.GestureState2FingerMoveToScrollSync;
//import com.eltechs.axs.GestureStateMachine.GestureState3FingersToZoom;
//import com.eltechs.axs.GestureStateMachine.GestureStateClickToFingerFirstCoords;
//import com.eltechs.axs.GestureStateMachine.GestureStateNeutral;
//import com.eltechs.axs.GestureStateMachine.GestureStatePressKey;
//import com.eltechs.axs.GestureStateMachine.GestureStateWaitFingersNumberChangeWithTimeout;
//import com.eltechs.axs.GestureStateMachine.GestureStateWaitForNeutral;
//import com.eltechs.axs.GestureStateMachine.PointerContext;
//import com.eltechs.axs.GuestAppActionAdapters.MouseMoveAdapter;
//import com.eltechs.axs.GuestAppActionAdapters.PressAndHoldWithPauseMouseClickAdapter;
//import com.eltechs.axs.GuestAppActionAdapters.PressAndReleaseMouseClickAdapter;
//import com.eltechs.axs.GuestAppActionAdapters.RelativeToCurrentPositionMouseMoveAdapterAccelerate;
//import com.eltechs.axs.GuestAppActionAdapters.ScrollAdapterMouseWheel;
//import com.eltechs.axs.GuestAppActionAdapters.SimpleMouseMoveAdapter;
//import com.eltechs.axs.GuestAppActionAdapters.SimpleMousePointAndClickAdapter;
//import com.eltechs.axs.KeyCodesX;
//import com.eltechs.axs.TouchArea;
//import com.eltechs.axs.TouchEventMultiplexor;
//import com.eltechs.axs.finiteStateMachine.FiniteStateMachine;
//import com.eltechs.axs.finiteStateMachine.generalStates.FSMStateRunRunnable;
//import com.eltechs.axs.widgets.viewOfXServer.TransformationHelpers;
//import com.eltechs.axs.widgets.viewOfXServer.ViewOfXServer;
//
///* loaded from: classes.dex */
//public class GestureMachineConfigurerTouchPad {
//    private static final float dragNDropThresholdInches = 0.0f;
//    private static final int finger2FlashMaxTimeMs = 64;
//    private static final int finger2SpeedCheckTimeMs = 300;
//    private static final float finger2StandingMaxMoveInches = 0.01f;
//    private static final float fingerAimingMaxMoveInches = 0.004f;
//    private static final int fingerSpeedCheckTimeMs = 300;
//    private static final float fingerStandingMaxMoveInches = 0.004f;
//    private static final float fingerTapMaxMoveInches = 0.2f;
//    private static final int fingerTapMaxTimeMs = 200;
//    private static final int maxTapTimeMs = 100;
//    private static final int mouseActionSleepMs = 30;
//    private static final long scrollPeriodMs = 30;
//
//    public static GestureContext createGestureContext(ViewOfXServer viewOfXServer, TouchArea touchArea, TouchEventMultiplexor touchEventMultiplexor, int i, Runnable runnable) {
//        GestureContext gestureContext = new GestureContext(viewOfXServer, touchArea, touchEventMultiplexor);
//        PointerContext pointerContext = new PointerContext();
//        GestureStateNeutral gestureStateNeutral = new GestureStateNeutral(gestureContext);
//        GestureStateWaitForNeutral gestureStateWaitForNeutral = new GestureStateWaitForNeutral(gestureContext);
//        float f = i;
//        float f2 = f * 0.004f;
//        GestureState1FingerMeasureSpeed gestureState1FingerMeasureSpeed = new GestureState1FingerMeasureSpeed(gestureContext, 300, f2, f2, f * fingerTapMaxMoveInches, 200.0f);
//        GestureState2FingerMeasureSpeed gestureState2FingerMeasureSpeed = new GestureState2FingerMeasureSpeed(gestureContext, 300, f * 0.01f, 64.0f);
//        GestureStateClickToFingerFirstCoords gestureStateClickToFingerFirstCoords = new GestureStateClickToFingerFirstCoords(gestureContext, new SimpleMousePointAndClickAdapter(new MouseMoveAdapter() { // from class: com.eltechs.axs.gamesControls.GestureMachineConfigurerTouchPad.1_fix
//            @Override // com.eltechs.axs.GuestAppActionAdapters.MouseMoveAdapter
//            public void moveTo(float f3, float f4) {
//            }
//
//            @Override // com.eltechs.axs.GuestAppActionAdapters.MouseMoveAdapter
//            public void prepareMoving(float f3, float f4) {
//            }
//        }, new PressAndReleaseMouseClickAdapter(gestureContext.getPointerReporter(), 1, 30), pointerContext));
//        GestureStateClickToFingerFirstCoords gestureStateClickToFingerFirstCoords2 = new GestureStateClickToFingerFirstCoords(gestureContext, new SimpleMousePointAndClickAdapter(new MouseMoveAdapter() { // from class: com.eltechs.axs.gamesControls.GestureMachineConfigurerTouchPad.2_fix
//            @Override // com.eltechs.axs.GuestAppActionAdapters.MouseMoveAdapter
//            public void moveTo(float f3, float f4) {
//            }
//
//            @Override // com.eltechs.axs.GuestAppActionAdapters.MouseMoveAdapter
//            public void prepareMoving(float f3, float f4) {
//            }
//        }, new PressAndReleaseMouseClickAdapter(gestureContext.getPointerReporter(), 3, 30), pointerContext));
//        GestureStateClickToFingerFirstCoords gestureStateClickToFingerFirstCoords3 = new GestureStateClickToFingerFirstCoords(gestureContext, new SimpleMousePointAndClickAdapter(new MouseMoveAdapter() { // from class: com.eltechs.axs.gamesControls.GestureMachineConfigurerTouchPad.3_fix
//            @Override // com.eltechs.axs.GuestAppActionAdapters.MouseMoveAdapter
//            public void moveTo(float f3, float f4) {
//            }
//
//            @Override // com.eltechs.axs.GuestAppActionAdapters.MouseMoveAdapter
//            public void prepareMoving(float f3, float f4) {
//            }
//        }, new PressAndReleaseMouseClickAdapter(gestureContext.getPointerReporter(), 1, 30), pointerContext));
//        GestureState1FingerMoveToMouseMoveWithWait gestureState1FingerMoveToMouseMoveWithWait = new GestureState1FingerMoveToMouseMoveWithWait(gestureContext, pointerContext, new RelativeToCurrentPositionMouseMoveAdapterAccelerate(new SimpleMouseMoveAdapter(gestureContext.getPointerReporter()), gestureContext.getViewFacade(), gestureContext.getHostView(), 1));
//        GestureState2FingerMoveToMouseMoveWithClick gestureState2FingerMoveToMouseMoveWithClick = new GestureState2FingerMoveToMouseMoveWithClick(gestureContext, pointerContext, new RelativeToCurrentPositionMouseMoveAdapterAccelerate(new SimpleMouseMoveAdapter(gestureContext.getPointerReporter()), gestureContext.getViewFacade(), gestureContext.getHostView(), 2), new PressAndHoldWithPauseMouseClickAdapter(gestureContext.getPointerReporter(), 1, 0));
//        GestureStateWaitFingersNumberChangeWithTimeout gestureStateWaitFingersNumberChangeWithTimeout = new GestureStateWaitFingersNumberChangeWithTimeout(gestureContext, 100);
//        GestureStateWaitFingersNumberChangeWithTimeout gestureStateWaitFingersNumberChangeWithTimeout2 = new GestureStateWaitFingersNumberChangeWithTimeout(gestureContext, 300);
//        GestureStateWaitFingersNumberChangeWithTimeout gestureStateWaitFingersNumberChangeWithTimeout3 = new GestureStateWaitFingersNumberChangeWithTimeout(gestureContext, 300);
//        FSMStateRunRunnable fSMStateRunRunnable = new FSMStateRunRunnable(runnable);
//        GestureStatePressKey gestureStatePressKey = new GestureStatePressKey(gestureContext, KeyCodesX.KEY_SPACE);
//        GestureState3FingersToZoom gestureState3FingersToZoom = new GestureState3FingersToZoom(gestureContext);
//        GestureState2FingerMoveToScrollSync gestureState2FingerMoveToScrollSync = new GestureState2FingerMoveToScrollSync(gestureContext, new ScrollAdapterMouseWheel(gestureContext.getPointerReporter()), TransformationHelpers.getScaleX(viewOfXServer.getViewToXServerTransformationMatrix()) * 0.05f, TransformationHelpers.getScaleY(viewOfXServer.getViewToXServerTransformationMatrix()) * 0.05f, 0.0f, false, 0, scrollPeriodMs, true);
//        FiniteStateMachine finiteStateMachine = new FiniteStateMachine();
//        finiteStateMachine.setStatesList(gestureStateNeutral, gestureState1FingerMeasureSpeed, gestureState2FingerMeasureSpeed, gestureStateClickToFingerFirstCoords, gestureStateClickToFingerFirstCoords2, gestureStateClickToFingerFirstCoords3, gestureState1FingerMoveToMouseMoveWithWait, gestureState2FingerMoveToMouseMoveWithClick, gestureStateWaitFingersNumberChangeWithTimeout, gestureStateWaitFingersNumberChangeWithTimeout2, gestureStateWaitFingersNumberChangeWithTimeout3, fSMStateRunRunnable, gestureStatePressKey, gestureState3FingersToZoom, gestureState2FingerMoveToScrollSync, gestureStateWaitForNeutral);
//        finiteStateMachine.addTransition(gestureStateWaitForNeutral, GestureStateWaitForNeutral.GESTURE_COMPLETED, gestureStateNeutral);
//        finiteStateMachine.addTransition(gestureStateNeutral, GestureStateNeutral.FINGER_TOUCHED, gestureState1FingerMeasureSpeed);
//        finiteStateMachine.addTransition(gestureState1FingerMeasureSpeed, GestureState1FingerMeasureSpeed.FINGER_TAPPED, gestureStateClickToFingerFirstCoords);
//        finiteStateMachine.addTransition(gestureState1FingerMeasureSpeed, GestureState1FingerMeasureSpeed.FINGER_STANDING, gestureState1FingerMoveToMouseMoveWithWait);
//        finiteStateMachine.addTransition(gestureState1FingerMeasureSpeed, GestureState1FingerMeasureSpeed.FINGER_WALKED, gestureState1FingerMoveToMouseMoveWithWait);
//        finiteStateMachine.addTransition(gestureState1FingerMeasureSpeed, GestureState1FingerMeasureSpeed.FINGER_WALKED_AND_GONE, gestureStateClickToFingerFirstCoords);
//        finiteStateMachine.addTransition(gestureState1FingerMeasureSpeed, GestureState1FingerMeasureSpeed.FINGER_FLASHED, gestureState1FingerMoveToMouseMoveWithWait);
//        finiteStateMachine.addTransition(gestureState1FingerMeasureSpeed, GestureState1FingerMeasureSpeed.FINGER_TOUCHED, gestureState2FingerMeasureSpeed);
//        finiteStateMachine.addTransition(gestureState1FingerMoveToMouseMoveWithWait, GestureState1FingerMoveToMouseMoveWithWait.FINGER_TOUCHED, gestureState2FingerMeasureSpeed);
//        finiteStateMachine.addTransition(gestureState2FingerMeasureSpeed, GestureState2FingerMeasureSpeed.FINGER_TOUCHED, gestureStateWaitFingersNumberChangeWithTimeout2);
//        finiteStateMachine.addTransition(gestureState2FingerMeasureSpeed, GestureState2FingerMeasureSpeed.FINGER_FLASHED, gestureState2FingerMoveToMouseMoveWithClick);
//        finiteStateMachine.addTransition(gestureState2FingerMeasureSpeed, GestureState2FingerMeasureSpeed.FINGER_SCROLL, gestureState2FingerMoveToScrollSync);
//        finiteStateMachine.addTransition(gestureState2FingerMeasureSpeed, GestureState2FingerMeasureSpeed.FINGER_TAPPED, gestureStateClickToFingerFirstCoords3);
//        finiteStateMachine.addTransition(gestureStateClickToFingerFirstCoords2, GestureStateClickToFingerFirstCoords.GESTURE_COMPLETED, gestureState1FingerMoveToMouseMoveWithWait);
//        finiteStateMachine.addTransition(gestureStateClickToFingerFirstCoords3, GestureStateClickToFingerFirstCoords.GESTURE_COMPLETED, gestureState1FingerMoveToMouseMoveWithWait);
//        finiteStateMachine.addTransition(gestureState2FingerMoveToMouseMoveWithClick, GestureState2FingerMoveToMouseMoveWithClick.FINGER_TOUCHED, gestureStateWaitFingersNumberChangeWithTimeout2);
//        finiteStateMachine.addTransition(gestureState2FingerMoveToMouseMoveWithClick, GestureState2FingerMoveToMouseMoveWithClick.FINGER_RELEASED, gestureState1FingerMoveToMouseMoveWithWait);
//        finiteStateMachine.addTransition(gestureState2FingerMoveToScrollSync, GestureState2FingerMoveToScrollSync.FINGER_TOUCHED, gestureStateWaitFingersNumberChangeWithTimeout2);
//        finiteStateMachine.addTransition(gestureState2FingerMoveToScrollSync, GestureState2FingerMoveToScrollSync.FINGER_RELEASED, gestureState1FingerMoveToMouseMoveWithWait);
//        finiteStateMachine.addTransition(gestureStateWaitFingersNumberChangeWithTimeout2, GestureStateWaitFingersNumberChangeWithTimeout.FINGER_RELEASED, gestureStatePressKey);
//        finiteStateMachine.addTransition(gestureStatePressKey, GestureStatePressKey.GESTURE_COMPLETED, gestureStateWaitFingersNumberChangeWithTimeout3);
//        finiteStateMachine.addTransition(gestureStateWaitFingersNumberChangeWithTimeout2, GestureStateWaitFingersNumberChangeWithTimeout.TIMED_OUT, gestureState3FingersToZoom);
//        finiteStateMachine.addTransition(gestureStateWaitFingersNumberChangeWithTimeout2, GestureStateWaitFingersNumberChangeWithTimeout.FINGER_TOUCHED, fSMStateRunRunnable);
//        finiteStateMachine.addTransition(gestureStateWaitFingersNumberChangeWithTimeout3, GestureStateWaitFingersNumberChangeWithTimeout.FINGER_TOUCHED, gestureStateWaitFingersNumberChangeWithTimeout2);
//        finiteStateMachine.addTransition(gestureStateWaitFingersNumberChangeWithTimeout3, GestureStateWaitFingersNumberChangeWithTimeout.FINGER_RELEASED, gestureState1FingerMoveToMouseMoveWithWait);
//        finiteStateMachine.addTransition(gestureStateWaitFingersNumberChangeWithTimeout3, GestureStateWaitFingersNumberChangeWithTimeout.TIMED_OUT, gestureStateWaitFingersNumberChangeWithTimeout3);
//        finiteStateMachine.addTransition(gestureState3FingersToZoom, GestureState3FingersToZoom.FINGER_RELEASED, gestureStateWaitFingersNumberChangeWithTimeout3);
//        finiteStateMachine.setInitialState(gestureStateNeutral);
//        finiteStateMachine.setDefaultState(gestureStateWaitForNeutral);
//        finiteStateMachine.configurationCompleted();
//        gestureContext.setMachine(finiteStateMachine);
//        return gestureContext;
//    }
//}
