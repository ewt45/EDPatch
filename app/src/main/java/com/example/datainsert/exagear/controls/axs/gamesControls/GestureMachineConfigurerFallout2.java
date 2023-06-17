package com.example.datainsert.exagear.controls.axs.gamesControls;

import com.eltechs.axs.GestureStateMachine.GestureContext;
import com.eltechs.axs.GestureStateMachine.GestureState1FingerMeasureSpeed;
import com.eltechs.axs.GestureStateMachine.GestureState1FingerMoveToMouseDragAndDrop;
import com.eltechs.axs.GestureStateMachine.GestureState1FingerMoveToScrollAsync;
import com.eltechs.axs.GestureStateMachine.GestureState1FingerToZoomMove;
import com.eltechs.axs.GestureStateMachine.GestureState2FingersToZoom;
import com.eltechs.axs.GestureStateMachine.GestureState3FingersToZoom;
import com.eltechs.axs.GestureStateMachine.GestureStateCheckIfZoomed;
import com.eltechs.axs.GestureStateMachine.GestureStateClickToFingerFirstCoords;
import com.eltechs.axs.GestureStateMachine.GestureStateNeutral;
import com.eltechs.axs.GestureStateMachine.GestureStateWaitFingersNumberChangeWithTimeout;
import com.eltechs.axs.GestureStateMachine.GestureStateWaitForNeutral;
import com.eltechs.axs.GestureStateMachine.PointerContext;
import com.eltechs.axs.GuestAppActionAdapters.AlignedMouseClickAdapter;
import com.eltechs.axs.GuestAppActionAdapters.AsyncScrollAdapterWithPointer;
import com.eltechs.axs.GuestAppActionAdapters.MouseClickAdapterWithCheckPlacementContext;
import com.eltechs.axs.GuestAppActionAdapters.PressAndHoldMouseClickAdapter;
import com.eltechs.axs.GuestAppActionAdapters.PressAndHoldWithPauseMouseClickAdapter;
import com.eltechs.axs.GuestAppActionAdapters.PressAndReleaseMouseClickAdapter;
import com.eltechs.axs.GuestAppActionAdapters.SimpleDragAndDropAdapter;
import com.eltechs.axs.GuestAppActionAdapters.SimpleMouseMoveAdapter;
import com.eltechs.axs.GuestAppActionAdapters.SimpleMousePointAndClickAdapter;
import com.eltechs.axs.TouchArea;
import com.eltechs.axs.TouchEventMultiplexor;
import com.eltechs.axs.finiteStateMachine.FiniteStateMachine;
import com.eltechs.axs.finiteStateMachine.generalStates.FSMStateRunRunnable;
import com.eltechs.axs.geom.Rectangle;
import com.eltechs.axs.widgets.viewOfXServer.ViewOfXServer;

public class GestureMachineConfigurerFallout2 {
    private static final float clickAlignThresholdInches = 0.3f;
    private static final float doubleClickMaxDistance = 0.15f;
    private static final int doubleClickMaxIntervalMs = 200;
    private static final float fingerAimingMaxMoveInches = 0.2f;
    private static final int fingerSpeedCheckTimeMs = 400;
    private static final float fingerStandingMaxMoveInches = 0.12f;
    private static final float fingerTapMaxMoveInches = 0.2f;
    private static final int fingerTapMaxTimeMs = 400;
    private static final int maxTapTimeMs = 300;
    private static final int mouseActionSleepMs = 150;
    private static final float pixelsInScrollUnitX = 50.0f;
    private static final float pixelsInScrollUnitY = 50.0f;
    private static final int pointerMarginXPixels = 50;
    private static final long scrollTimerPeriod = 150;

    public static GestureContext createGestureContext(ViewOfXServer viewOfXServer, TouchArea touchArea, TouchEventMultiplexor touchEventMultiplexor, int i, Runnable runnable) {
        GestureContext gestureContext = new GestureContext(viewOfXServer, touchArea, touchEventMultiplexor);
        PointerContext pointerContext = new PointerContext();
        GestureStateNeutral gsNeutral = new GestureStateNeutral(gestureContext);
        GestureStateWaitForNeutral gsWaitForNeutral = new GestureStateWaitForNeutral(gestureContext);
        float f2 = 0.2f * (float) i;
        GestureState1FingerMeasureSpeed gs1FingerMeasureSpeed = new GestureState1FingerMeasureSpeed(gestureContext, 400, fingerStandingMaxMoveInches * (float) i, f2, f2, 400.0f);
        GestureStateCheckIfZoomed gsCheckIfZoomed = new GestureStateCheckIfZoomed(gestureContext);
        SimpleMousePointAndClickAdapter simpleMousePointAndClickAdapter = new SimpleMousePointAndClickAdapter(
                new SimpleMouseMoveAdapter(gestureContext.getPointerReporter()),
                new PressAndReleaseMouseClickAdapter(gestureContext.getPointerReporter(), 1, mouseActionSleepMs),
                pointerContext);
        SimpleMouseMoveAdapter simpleMouseMoveAdapter = new SimpleMouseMoveAdapter(gestureContext.getPointerReporter());
        PressAndReleaseMouseClickAdapter pressAndReleaseMouseClickAdapter = new PressAndReleaseMouseClickAdapter(gestureContext.getPointerReporter(), 1, mouseActionSleepMs);
        PressAndReleaseMouseClickAdapter pressAndReleaseMouseClickAdapter2 = new PressAndReleaseMouseClickAdapter(gestureContext.getPointerReporter(), 1, mouseActionSleepMs);
        float f3 = clickAlignThresholdInches * (float) i;
        float f4 = doubleClickMaxDistance * (float) i;
        GestureStateClickToFingerFirstCoords gsClickToFingerFirstCoords = new GestureStateClickToFingerFirstCoords(
                gestureContext,
                new MouseClickAdapterWithCheckPlacementContext(
                        simpleMousePointAndClickAdapter,
                        new AlignedMouseClickAdapter(simpleMouseMoveAdapter, pressAndReleaseMouseClickAdapter, pressAndReleaseMouseClickAdapter2, viewOfXServer, pointerContext, f3),
                        new AlignedMouseClickAdapter(
                                new SimpleMouseMoveAdapter(gestureContext.getPointerReporter()),
                                new PressAndReleaseMouseClickAdapter(gestureContext.getPointerReporter(), 1, mouseActionSleepMs),
                                new PressAndReleaseMouseClickAdapter(gestureContext.getPointerReporter(), 1, mouseActionSleepMs),
                                viewOfXServer, pointerContext, f4),
                        pointerContext, 200
                )
        );
        GestureStateClickToFingerFirstCoords gsClickToFingerFirstCoords2 = new GestureStateClickToFingerFirstCoords(
                gestureContext,
                new MouseClickAdapterWithCheckPlacementContext(
                        new SimpleMousePointAndClickAdapter(
                                new SimpleMouseMoveAdapter(gestureContext.getPointerReporter()),
                                new PressAndReleaseMouseClickAdapter(gestureContext.getPointerReporter(), 3, mouseActionSleepMs),
                                pointerContext),
                        new AlignedMouseClickAdapter(
                                new SimpleMouseMoveAdapter(gestureContext.getPointerReporter()),
                                new PressAndReleaseMouseClickAdapter(gestureContext.getPointerReporter(), 3, mouseActionSleepMs),
                                new PressAndReleaseMouseClickAdapter(gestureContext.getPointerReporter(), 3, mouseActionSleepMs),
                                viewOfXServer,
                                pointerContext,
                                f3),
                        new AlignedMouseClickAdapter(
                                new SimpleMouseMoveAdapter(gestureContext.getPointerReporter()),
                                new PressAndReleaseMouseClickAdapter(gestureContext.getPointerReporter(), 3, mouseActionSleepMs),
                                new PressAndReleaseMouseClickAdapter(gestureContext.getPointerReporter(), 3, mouseActionSleepMs),
                                viewOfXServer,
                                pointerContext,
                                f4),
                        pointerContext,
                        200
                )
        );
        GestureStateWaitFingersNumberChangeWithTimeout gsWaitFingersNumberChangeWithTimeout = new GestureStateWaitFingersNumberChangeWithTimeout(gestureContext, 1000000000);
        GestureStateWaitFingersNumberChangeWithTimeout gsWaitFingersNumberChangeWithTimeout2 = new GestureStateWaitFingersNumberChangeWithTimeout(gestureContext, maxTapTimeMs);
        GestureStateWaitFingersNumberChangeWithTimeout gsWaitFingersNumberChangeWithTimeout3 = new GestureStateWaitFingersNumberChangeWithTimeout(gestureContext, maxTapTimeMs);
        FSMStateRunRunnable fSMStateRunRunnable = new FSMStateRunRunnable(runnable);
        GestureState1FingerMoveToMouseDragAndDrop gs1FingerMoveToMouseDragAndDrop = new GestureState1FingerMoveToMouseDragAndDrop(
                gestureContext,
                new SimpleDragAndDropAdapter(
                        new SimpleMouseMoveAdapter(gestureContext.getPointerReporter()),
                        new PressAndHoldWithPauseMouseClickAdapter(gestureContext.getPointerReporter(), 1, mouseActionSleepMs),
                        () -> {
                        }),
                pointerContext, false, 0.0f);
        GestureState1FingerMoveToMouseDragAndDrop gs1FingerMoveToMouseDragAndDrop2 = new GestureState1FingerMoveToMouseDragAndDrop(
                gestureContext,
                new SimpleDragAndDropAdapter(
                        new SimpleMouseMoveAdapter(gestureContext.getPointerReporter()),
                        new PressAndHoldMouseClickAdapter(gestureContext.getPointerReporter(), 3),
                        () -> {
                        }),
                pointerContext, false, 0.0f);
        GestureState1FingerMoveToScrollAsync gs1FingerMoveToScrollAsync = new GestureState1FingerMoveToScrollAsync(
                gestureContext,
                new AsyncScrollAdapterWithPointer(gestureContext.getViewFacade(),
                        new Rectangle(0, 0,
                                gestureContext.getViewFacade().getScreenInfo().widthInPixels,
                                gestureContext.getViewFacade().getScreenInfo().heightInPixels)),
                1000000.0f, 1000000.0f, (float) i, true, 15, true);
        GestureState1FingerToZoomMove gs1FingerToZoomMove = new GestureState1FingerToZoomMove(gestureContext);
        GestureState3FingersToZoom gs3FingersToZoom = new GestureState3FingersToZoom(gestureContext);
        GestureState2FingersToZoom gs2FingersToZoom = new GestureState2FingersToZoom(gestureContext);
        FiniteStateMachine finiteStateMachine = new FiniteStateMachine();
        finiteStateMachine.setStatesList(
                gsNeutral,
                gs1FingerMeasureSpeed,
                gsClickToFingerFirstCoords,
                gsCheckIfZoomed,
                gsWaitFingersNumberChangeWithTimeout,
                gsWaitFingersNumberChangeWithTimeout2,
                gsWaitFingersNumberChangeWithTimeout3,
                fSMStateRunRunnable,
                gs1FingerMoveToMouseDragAndDrop2,
                gs1FingerMoveToMouseDragAndDrop,
                gsClickToFingerFirstCoords2,
                gs1FingerMoveToScrollAsync,
                gs1FingerToZoomMove,
                gs2FingersToZoom,
                gs3FingersToZoom,
                gsWaitForNeutral);
        finiteStateMachine.addTransition(gsWaitForNeutral, GestureStateWaitForNeutral.GESTURE_COMPLETED, gsNeutral);
        finiteStateMachine.addTransition(gsNeutral, GestureStateNeutral.FINGER_TOUCHED, gs1FingerMeasureSpeed);
        finiteStateMachine.addTransition(gs1FingerMeasureSpeed, GestureState1FingerMeasureSpeed.FINGER_FLASHED, gsCheckIfZoomed);
        finiteStateMachine.addTransition(gsCheckIfZoomed, GestureStateCheckIfZoomed.ZOOM_OFF, gs1FingerMoveToScrollAsync);
        finiteStateMachine.addTransition(gsCheckIfZoomed, GestureStateCheckIfZoomed.ZOOM_OFF, gs1FingerMoveToMouseDragAndDrop);
        finiteStateMachine.addTransition(gsCheckIfZoomed, GestureStateCheckIfZoomed.ZOOM_ON, gs1FingerToZoomMove);
        finiteStateMachine.addTransition(gs1FingerMeasureSpeed, GestureState1FingerMeasureSpeed.FINGER_TOUCHED, gsWaitFingersNumberChangeWithTimeout2);
        finiteStateMachine.addTransition(gsWaitFingersNumberChangeWithTimeout2, GestureStateWaitFingersNumberChangeWithTimeout.TIMED_OUT, gs1FingerMoveToMouseDragAndDrop2);
        finiteStateMachine.addTransition(gs1FingerToZoomMove, GestureState1FingerToZoomMove.FINGER_TOUCHED, gs2FingersToZoom);
        finiteStateMachine.addTransition(gs2FingersToZoom, GestureState2FingersToZoom.FINGER_TOUCHED, gs3FingersToZoom);
        finiteStateMachine.addTransition(gs2FingersToZoom, GestureState2FingersToZoom.FINGER_RELEASED, gs1FingerToZoomMove);
        finiteStateMachine.addTransition(gsWaitFingersNumberChangeWithTimeout2, GestureStateWaitFingersNumberChangeWithTimeout.FINGER_TOUCHED, gsWaitFingersNumberChangeWithTimeout3);
        finiteStateMachine.addTransition(gsWaitFingersNumberChangeWithTimeout3, GestureStateWaitFingersNumberChangeWithTimeout.TIMED_OUT, gs3FingersToZoom);
        finiteStateMachine.addTransition(gs3FingersToZoom, GestureState3FingersToZoom.FINGER_RELEASED, gs2FingersToZoom);
        finiteStateMachine.addTransition(gsWaitFingersNumberChangeWithTimeout3, GestureStateWaitFingersNumberChangeWithTimeout.FINGER_TOUCHED, fSMStateRunRunnable);
        finiteStateMachine.addTransition(gs1FingerMeasureSpeed, GestureState1FingerMeasureSpeed.FINGER_TAPPED, gsClickToFingerFirstCoords);
        finiteStateMachine.addTransition(gs1FingerMeasureSpeed, GestureState1FingerMeasureSpeed.FINGER_WALKED, gs1FingerMoveToMouseDragAndDrop);
        finiteStateMachine.addTransition(gs1FingerMeasureSpeed, GestureState1FingerMeasureSpeed.FINGER_STANDING, gs1FingerMoveToMouseDragAndDrop);
        finiteStateMachine.addTransition(gsWaitFingersNumberChangeWithTimeout2, GestureStateWaitFingersNumberChangeWithTimeout.FINGER_RELEASED, gsClickToFingerFirstCoords2);
        finiteStateMachine.addTransition(gsClickToFingerFirstCoords2, GestureStateClickToFingerFirstCoords.GESTURE_COMPLETED, gsWaitFingersNumberChangeWithTimeout);
        finiteStateMachine.addTransition(gsWaitFingersNumberChangeWithTimeout, GestureStateWaitFingersNumberChangeWithTimeout.FINGER_TOUCHED, gsWaitFingersNumberChangeWithTimeout2);
        finiteStateMachine.addTransition(gsWaitFingersNumberChangeWithTimeout, GestureStateWaitFingersNumberChangeWithTimeout.TIMED_OUT, gsWaitFingersNumberChangeWithTimeout);
        finiteStateMachine.setInitialState(gsNeutral);
        finiteStateMachine.setDefaultState(gsWaitForNeutral);
        finiteStateMachine.configurationCompleted();
        gestureContext.setMachine(finiteStateMachine);
        return gestureContext;
    }
}
