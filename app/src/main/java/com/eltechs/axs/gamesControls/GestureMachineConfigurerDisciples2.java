package com.eltechs.axs.gamesControls;

import com.eltechs.axs.GestureStateMachine.GestureContext;
import com.eltechs.axs.GestureStateMachine.GestureState1FingerMeasureSpeed;
import com.eltechs.axs.GestureStateMachine.GestureState1FingerMoveToMouseDragAndDrop;
import com.eltechs.axs.GestureStateMachine.GestureState1FingerMoveToScrollAsync;
import com.eltechs.axs.GestureStateMachine.GestureState1FingerToLongClick;
import com.eltechs.axs.GestureStateMachine.GestureState1FingerToZoomMove;
import com.eltechs.axs.GestureStateMachine.GestureState2FingersToZoom;
import com.eltechs.axs.GestureStateMachine.GestureStateCheckIfZoomed;
import com.eltechs.axs.GestureStateMachine.GestureStateClickToFingerFirstCoords;
import com.eltechs.axs.GestureStateMachine.GestureStateMouseWarpToFingerLastCoords;
import com.eltechs.axs.GestureStateMachine.GestureStateNeutral;
import com.eltechs.axs.GestureStateMachine.GestureStatePressKey;
import com.eltechs.axs.GestureStateMachine.GestureStateWaitFingersNumberChangeWithTimeout;
import com.eltechs.axs.GestureStateMachine.GestureStateWaitForNeutral;
import com.eltechs.axs.GestureStateMachine.PointerContext;
import com.eltechs.axs.GuestAppActionAdapters.AlignedMouseClickAdapter;
import com.eltechs.axs.GuestAppActionAdapters.AsyncScrollAdapterWithPointer;
import com.eltechs.axs.GuestAppActionAdapters.MouseClickAdapterWithCheckPlacementContext;
import com.eltechs.axs.GuestAppActionAdapters.PressAndHoldMouseClickAdapter;
import com.eltechs.axs.GuestAppActionAdapters.PressAndReleaseMouseClickAdapter;
import com.eltechs.axs.GuestAppActionAdapters.SimpleDragAndDropAdapter;
import com.eltechs.axs.GuestAppActionAdapters.SimpleMouseMoveAdapter;
import com.eltechs.axs.GuestAppActionAdapters.SimpleMousePointAndClickAdapter;
import com.eltechs.axs.KeyCodesX;
import com.eltechs.axs.TouchArea;
import com.eltechs.axs.TouchEventMultiplexor;
import com.eltechs.axs.finiteStateMachine.FiniteStateMachine;
import com.eltechs.axs.finiteStateMachine.generalStates.FSMStateRunRunnable;
import com.eltechs.axs.geom.Rectangle;
import com.eltechs.axs.widgets.viewOfXServer.ViewOfXServer;

/* loaded from: classes.dex */
public class GestureMachineConfigurerDisciples2 {
    private static final float clickAlignThresholdInches = 0.3f;
    private static final float doubleClickMaxDistance = 0.15f;
    private static final int doubleClickMaxIntervalMs = 200;
    private static final float fingerAimingMaxMoveInches = 0.9f;
    private static final int fingerSpeedCheckTimeMs = 400;
    private static final float fingerStandingMaxMoveInches = 0.12f;
    private static final float fingerTapMaxMoveInches = 0.2f;
    private static final int fingerTapMaxTimeMs = 400;
    private static final int maxTapTimeMs = 100;
    private static final int mouseActionSleepMs = 50;
    private static final float pixelsInScrollUnitX = 35.0f;
    private static final float pixelsInScrollUnitY = 15.0f;
    private static final int pointerMarginXPixels = 50;
    private static final long scrollTimerPeriod = 350;

    public static GestureContext createGestureContext(ViewOfXServer viewOfXServer, TouchArea touchArea, TouchEventMultiplexor touchEventMultiplexor, int i, Runnable runnable) {
        GestureContext gestureContext = new GestureContext(viewOfXServer, touchArea, touchEventMultiplexor);
        PointerContext pointerContext = new PointerContext();
        GestureStateNeutral gestureStateNeutral = new GestureStateNeutral(gestureContext);
        GestureStateWaitForNeutral gestureStateWaitForNeutral = new GestureStateWaitForNeutral(gestureContext);
        float f = i;
        GestureState1FingerMeasureSpeed gestureState1FingerMeasureSpeed = new GestureState1FingerMeasureSpeed(gestureContext, 400, fingerStandingMaxMoveInches * f, fingerAimingMaxMoveInches * f, fingerTapMaxMoveInches * f, 400.0f);
        GestureStateCheckIfZoomed gestureStateCheckIfZoomed = new GestureStateCheckIfZoomed(gestureContext);
        GestureStateMouseWarpToFingerLastCoords gestureStateMouseWarpToFingerLastCoords = new GestureStateMouseWarpToFingerLastCoords(gestureContext, new SimpleMouseMoveAdapter(gestureContext.getPointerReporter()), pointerContext);
        SimpleMousePointAndClickAdapter simpleMousePointAndClickAdapter = new SimpleMousePointAndClickAdapter(new SimpleMouseMoveAdapter(gestureContext.getPointerReporter()), new PressAndReleaseMouseClickAdapter(gestureContext.getPointerReporter(), 1, 50), pointerContext);
        SimpleMouseMoveAdapter simpleMouseMoveAdapter = new SimpleMouseMoveAdapter(gestureContext.getPointerReporter());
        PressAndReleaseMouseClickAdapter pressAndReleaseMouseClickAdapter = new PressAndReleaseMouseClickAdapter(gestureContext.getPointerReporter(), 1, 50);
        PressAndReleaseMouseClickAdapter pressAndReleaseMouseClickAdapter2 = new PressAndReleaseMouseClickAdapter(gestureContext.getPointerReporter(), 1, 50);
        float f2 = clickAlignThresholdInches * f;
        GestureStateClickToFingerFirstCoords gestureStateClickToFingerFirstCoords = new GestureStateClickToFingerFirstCoords(gestureContext, new MouseClickAdapterWithCheckPlacementContext(simpleMousePointAndClickAdapter, new AlignedMouseClickAdapter(simpleMouseMoveAdapter, pressAndReleaseMouseClickAdapter, pressAndReleaseMouseClickAdapter2, viewOfXServer, pointerContext, f2), new AlignedMouseClickAdapter(new SimpleMouseMoveAdapter(gestureContext.getPointerReporter()), new PressAndReleaseMouseClickAdapter(gestureContext.getPointerReporter(), 1, 50), new PressAndReleaseMouseClickAdapter(gestureContext.getPointerReporter(), 1, 50), viewOfXServer, pointerContext, 0.15f * f), pointerContext, 200));
        GestureStateWaitFingersNumberChangeWithTimeout gestureStateWaitFingersNumberChangeWithTimeout = new GestureStateWaitFingersNumberChangeWithTimeout(gestureContext, 100);
        GestureStateWaitFingersNumberChangeWithTimeout gestureStateWaitFingersNumberChangeWithTimeout2 = new GestureStateWaitFingersNumberChangeWithTimeout(gestureContext, 100);
        FSMStateRunRunnable fSMStateRunRunnable = new FSMStateRunRunnable(runnable);
        GestureStatePressKey gestureStatePressKey = new GestureStatePressKey(gestureContext, KeyCodesX.KEY_SPACE);
        GestureStatePressKey gestureStatePressKey2 = new GestureStatePressKey(gestureContext, KeyCodesX.KEY_RETURN);
        GestureState1FingerMoveToMouseDragAndDrop gestureState1FingerMoveToMouseDragAndDrop = new GestureState1FingerMoveToMouseDragAndDrop(gestureContext, new SimpleDragAndDropAdapter(new SimpleMouseMoveAdapter(gestureContext.getPointerReporter()), new PressAndHoldMouseClickAdapter(gestureContext.getPointerReporter(), 1), new Runnable() { // from class: com.eltechs.axs.gamesControls.GestureMachineConfigurerDisciples2.1
            @Override // java.lang.Runnable
            public void run() {
            }
        }), pointerContext, false, 0.0f);
        GestureState1FingerToLongClick gestureState1FingerToLongClick = new GestureState1FingerToLongClick(gestureContext, new MouseClickAdapterWithCheckPlacementContext(new SimpleMousePointAndClickAdapter(new SimpleMouseMoveAdapter(gestureContext.getPointerReporter()), new PressAndHoldMouseClickAdapter(gestureContext.getPointerReporter(), 3), pointerContext), new AlignedMouseClickAdapter(new SimpleMouseMoveAdapter(gestureContext.getPointerReporter()), new PressAndHoldMouseClickAdapter(gestureContext.getPointerReporter(), 3), new PressAndHoldMouseClickAdapter(gestureContext.getPointerReporter(), 3), viewOfXServer, pointerContext, f2), new SimpleMousePointAndClickAdapter(new SimpleMouseMoveAdapter(gestureContext.getPointerReporter()), new PressAndHoldMouseClickAdapter(gestureContext.getPointerReporter(), 3), pointerContext), pointerContext, 200));
        GestureState1FingerMoveToScrollAsync gestureState1FingerMoveToScrollAsync = new GestureState1FingerMoveToScrollAsync(gestureContext, new AsyncScrollAdapterWithPointer(gestureContext.getViewFacade(), new Rectangle(0, 0, gestureContext.getViewFacade().getScreenInfo().widthInPixels, gestureContext.getViewFacade().getScreenInfo().heightInPixels)), 1000000.0f, 1000000.0f, 1.0f * f, true, 15, true);
        GestureState1FingerToZoomMove gestureState1FingerToZoomMove = new GestureState1FingerToZoomMove(gestureContext);
        GestureState2FingersToZoom gestureState2FingersToZoom = new GestureState2FingersToZoom(gestureContext);
        FiniteStateMachine finiteStateMachine = new FiniteStateMachine();
        finiteStateMachine.setStatesList(gestureStateNeutral, gestureState1FingerMeasureSpeed, gestureStateMouseWarpToFingerLastCoords, gestureStateClickToFingerFirstCoords, gestureStateCheckIfZoomed, gestureStateWaitFingersNumberChangeWithTimeout, gestureStateWaitFingersNumberChangeWithTimeout2, fSMStateRunRunnable, gestureStatePressKey, gestureStatePressKey2, gestureState1FingerMoveToMouseDragAndDrop, gestureState1FingerToLongClick, gestureState1FingerMoveToScrollAsync, gestureState1FingerToZoomMove, gestureState2FingersToZoom, gestureStateWaitForNeutral);
        finiteStateMachine.addTransition(gestureStateWaitForNeutral, GestureStateWaitForNeutral.GESTURE_COMPLETED, gestureStateNeutral);
        finiteStateMachine.addTransition(gestureStateNeutral, GestureStateNeutral.FINGER_TOUCHED, gestureState1FingerMeasureSpeed);
        finiteStateMachine.addTransition(gestureState1FingerMeasureSpeed, GestureState1FingerMeasureSpeed.FINGER_STANDING, gestureState1FingerToLongClick);
        finiteStateMachine.addTransition(gestureState1FingerMeasureSpeed, GestureState1FingerMeasureSpeed.FINGER_WALKED, gestureState1FingerMoveToMouseDragAndDrop);
        finiteStateMachine.addTransition(gestureState1FingerMeasureSpeed, GestureState1FingerMeasureSpeed.FINGER_FLASHED, gestureStateCheckIfZoomed);
        finiteStateMachine.addTransition(gestureState1FingerMeasureSpeed, GestureState1FingerMeasureSpeed.FINGER_TOUCHED, gestureStateWaitFingersNumberChangeWithTimeout);
        finiteStateMachine.addTransition(gestureState1FingerMeasureSpeed, GestureState1FingerMeasureSpeed.FINGER_TAPPED, gestureStateClickToFingerFirstCoords);
        finiteStateMachine.addTransition(gestureState1FingerMeasureSpeed, GestureState1FingerMeasureSpeed.FINGER_WALKED_AND_GONE, gestureStateMouseWarpToFingerLastCoords);
        finiteStateMachine.addTransition(gestureStateCheckIfZoomed, GestureStateCheckIfZoomed.ZOOM_OFF, gestureState1FingerMoveToScrollAsync);
        finiteStateMachine.addTransition(gestureStateCheckIfZoomed, GestureStateCheckIfZoomed.ZOOM_ON, gestureState1FingerToZoomMove);
        finiteStateMachine.addTransition(gestureStateWaitFingersNumberChangeWithTimeout, GestureStateWaitFingersNumberChangeWithTimeout.FINGER_TOUCHED, gestureStateWaitFingersNumberChangeWithTimeout2);
        finiteStateMachine.addTransition(gestureStateWaitFingersNumberChangeWithTimeout, GestureStateWaitFingersNumberChangeWithTimeout.FINGER_RELEASED, gestureStatePressKey2);
        finiteStateMachine.addTransition(gestureStateWaitFingersNumberChangeWithTimeout, GestureStateWaitFingersNumberChangeWithTimeout.TIMED_OUT, gestureState2FingersToZoom);
        finiteStateMachine.addTransition(gestureStateWaitFingersNumberChangeWithTimeout2, GestureStateWaitFingersNumberChangeWithTimeout.FINGER_RELEASED, gestureStatePressKey);
        finiteStateMachine.addTransition(gestureStateWaitFingersNumberChangeWithTimeout2, GestureStateWaitFingersNumberChangeWithTimeout.TIMED_OUT, gestureStatePressKey);
        finiteStateMachine.addTransition(gestureStateWaitFingersNumberChangeWithTimeout2, GestureStateWaitFingersNumberChangeWithTimeout.FINGER_TOUCHED, fSMStateRunRunnable);
        finiteStateMachine.addTransition(gestureState2FingersToZoom, GestureState2FingersToZoom.FINGER_RELEASED, gestureState1FingerToZoomMove);
        finiteStateMachine.addTransition(gestureState1FingerToZoomMove, GestureState1FingerToZoomMove.FINGER_TOUCHED, gestureState2FingersToZoom);
        finiteStateMachine.setInitialState(gestureStateNeutral);
        finiteStateMachine.setDefaultState(gestureStateWaitForNeutral);
        finiteStateMachine.configurationCompleted();
        gestureContext.setMachine(finiteStateMachine);
        return gestureContext;
    }
}
