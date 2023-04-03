package com.eltechs.ed.controls.touchControls;

import com.eltechs.axs.GestureStateMachine.GestureContext;
import com.eltechs.axs.GestureStateMachine.GestureState1FingerMeasureSpeed;
import com.eltechs.axs.GestureStateMachine.GestureState1FingerMoveToMouseDragAndDrop;
import com.eltechs.axs.GestureStateMachine.GestureState1FingerMoveToScrollSync;
import com.eltechs.axs.GestureStateMachine.GestureState1FingerToZoomMove;
import com.eltechs.axs.GestureStateMachine.GestureState2FingersToZoom;
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
import com.eltechs.axs.helpers.AndroidHelpers;
import com.eltechs.axs.widgets.viewOfXServer.TransformationHelpers;
import com.eltechs.axs.widgets.viewOfXServer.ViewOfXServer;
import com.eltechs.ed.controls.uiOverlays.DefaultUIOverlay;

/* loaded from: classes.dex */
public class DefaultTCF extends AbstractTCF {
    private static final float clickAlignThresholdInches = 0.3f;
    private static final float doubleClickMaxDistanceInches = 0.15f;
    private static final int doubleClickMaxIntervalMs = 200;
    private static final float fingerStandingMaxMoveInches = 0.15f;
    private static final int fingerToLongTimeMs = 250;
    private static final int maxTapTimeMs = 100;
    private static final int mouseActionSleepMs = 50;
    private static final float pixelsInScrollUnit = 20.0f;
    private static final long scrollPeriodMs = 30;

    @Override // com.eltechs.ed.controls.touchControls.AbstractTCF
    public GestureContext createGestureContext(ViewOfXServer viewOfXServer, TouchArea touchArea, TouchEventMultiplexor touchEventMultiplexor, int densityDpi) {
        final GestureContext gestureContext = new GestureContext(viewOfXServer, touchArea, touchEventMultiplexor);
        PointerContext pointerContext = new PointerContext();
        GestureStateNeutral gestureStateNeutral = new GestureStateNeutral(gestureContext);
        GestureStateWaitForNeutral gestureStateWaitForNeutral = new GestureStateWaitForNeutral(gestureContext);
        float maxMove = 0.15f * (float) densityDpi;
        GestureState1FingerMeasureSpeed gs1FingerMeasureSpeed = new GestureState1FingerMeasureSpeed(gestureContext, fingerToLongTimeMs, maxMove, maxMove, maxMove, fingerToLongTimeMs);
        GestureStateClickToFingerFirstCoords gsClickToFingerFirstCoords = new GestureStateClickToFingerFirstCoords(
                gestureContext,
                new MouseClickAdapterWithCheckPlacementContext(
                        new SimpleMousePointAndClickAdapter(
                                new SimpleMouseMoveAdapter(gestureContext.getPointerReporter()),
                                new PressAndReleaseMouseClickAdapter(gestureContext.getPointerReporter(), 1, mouseActionSleepMs),
                                pointerContext
                        ),
                        new AlignedMouseClickAdapter(
                                new SimpleMouseMoveAdapter(gestureContext.getPointerReporter()),
                                new PressAndReleaseMouseClickAdapter(gestureContext.getPointerReporter(), 1, mouseActionSleepMs),
                                new PressAndReleaseMouseClickAdapter(gestureContext.getPointerReporter(), 1, mouseActionSleepMs),
                                viewOfXServer,
                                pointerContext,
                                densityDpi * clickAlignThresholdInches
                        ),
                        new AlignedMouseClickAdapter(
                                new SimpleMouseMoveAdapter(gestureContext.getPointerReporter()),
                                new PressAndReleaseMouseClickAdapter(gestureContext.getPointerReporter(), 1, mouseActionSleepMs),
                                new PressAndReleaseMouseClickAdapter(gestureContext.getPointerReporter(), 1, mouseActionSleepMs),
                                viewOfXServer,
                                pointerContext,
                                maxMove
                        ),
                        pointerContext,
                        200
                )
        );
        GestureState1FingerMoveToScrollSync gs1FingerMoveToScrollSync = new GestureState1FingerMoveToScrollSync(
                gestureContext,
                new ScrollAdapterMouseWheel(gestureContext.getPointerReporter()),
                0.05f * TransformationHelpers.getScaleX(viewOfXServer.getViewToXServerTransformationMatrix()),
                0.05f * TransformationHelpers.getScaleY(viewOfXServer.getViewToXServerTransformationMatrix()),
                0.0f,
                false,
                0,
                scrollPeriodMs,
                true
        );
        GestureState1FingerMeasureSpeed gs1FingerMeasureSpeed2 = new GestureState1FingerMeasureSpeed(gestureContext, 1000000, maxMove, maxMove, maxMove, 1000000.0f);
        GestureStateClickToFingerFirstCoords gsClickToFingerFirstCoords2 = new GestureStateClickToFingerFirstCoords(
                gestureContext,
                new SimpleMousePointAndClickAdapter(
                        new SimpleMouseMoveAdapter(gestureContext.getPointerReporter()),
                        new PressAndReleaseMouseClickAdapter(gestureContext.getPointerReporter(), 3, mouseActionSleepMs),
                        pointerContext
                )
        );
        // from class: com.eltechs.ed.controls.touchControls.DefaultTCF.1
// java.lang.Runnable
        GestureState1FingerMoveToMouseDragAndDrop gs1FingerMoveToMouseDragAndDrop = new GestureState1FingerMoveToMouseDragAndDrop(
                gestureContext,
                new SimpleDragAndDropAdapter(
                        new SimpleMouseMoveAdapter(gestureContext.getPointerReporter()),
                        new PressAndHoldMouseClickAdapter(gestureContext.getPointerReporter(), 1),
                        () -> gestureContext.getPointerReporter().click(3, mouseActionSleepMs)
                ),
                pointerContext,
                false,
                0.0f
        );
        GestureStateWaitFingersNumberChangeWithTimeout gsWaitFingersNumberChangeWithTimeout = new GestureStateWaitFingersNumberChangeWithTimeout(gestureContext, maxTapTimeMs);
        // from class: com.eltechs.ed.controls.touchControls.DefaultTCF.2
// java.lang.Runnable
        FSMStateRunRunnable fSMStateRunRunnable = new FSMStateRunRunnable(AndroidHelpers::toggleSoftInput);
        GestureState2FingersToZoom gestureState2FingersToZoom = new GestureState2FingersToZoom(gestureContext);
        GestureState1FingerToZoomMove gestureState1FingerToZoomMove = new GestureState1FingerToZoomMove(gestureContext);
        GestureStateWaitFingersNumberChangeWithTimeout gsWaitFingersNumberChangeWithTimeout2 =
                new GestureStateWaitFingersNumberChangeWithTimeout(gestureContext, maxTapTimeMs);
        // from class: com.eltechs.ed.controls.touchControls.DefaultTCF.3
// java.lang.Runnable
        FSMStateRunRunnable fSMStateRunRunnable2 = new FSMStateRunRunnable(() -> ((DefaultUIOverlay) DefaultTCF.this.mUIOverlay).toggleToolbar());
        FiniteStateMachine finiteStateMachine = new FiniteStateMachine();
        finiteStateMachine.setStatesList(
                gestureStateWaitForNeutral,
                gestureStateNeutral,
                gs1FingerMeasureSpeed,
                gsClickToFingerFirstCoords,
                gs1FingerMoveToScrollSync,
                gs1FingerMeasureSpeed2,
                gsClickToFingerFirstCoords2,
                gs1FingerMoveToMouseDragAndDrop,
                gsWaitFingersNumberChangeWithTimeout,
                fSMStateRunRunnable,
                gestureState2FingersToZoom,
                gestureState1FingerToZoomMove,
                gsWaitFingersNumberChangeWithTimeout2,
                fSMStateRunRunnable2
        );
        finiteStateMachine.addTransition(gestureStateWaitForNeutral, GestureStateWaitForNeutral.GESTURE_COMPLETED, gestureStateNeutral);
        finiteStateMachine.addTransition(gestureStateNeutral, GestureStateNeutral.FINGER_TOUCHED, gs1FingerMeasureSpeed);
        finiteStateMachine.addTransition(gs1FingerMeasureSpeed, GestureState1FingerMeasureSpeed.FINGER_TAPPED, gsClickToFingerFirstCoords);
        finiteStateMachine.addTransition(gs1FingerMeasureSpeed, GestureState1FingerMeasureSpeed.FINGER_WALKED, gs1FingerMoveToScrollSync);
        finiteStateMachine.addTransition(gs1FingerMeasureSpeed, GestureState1FingerMeasureSpeed.FINGER_FLASHED, gs1FingerMoveToScrollSync);
        finiteStateMachine.addTransition(gs1FingerMeasureSpeed, GestureState1FingerMeasureSpeed.FINGER_STANDING, gs1FingerMeasureSpeed2);
        finiteStateMachine.addTransition(gs1FingerMeasureSpeed, GestureState1FingerMeasureSpeed.FINGER_TOUCHED, gsWaitFingersNumberChangeWithTimeout);
        finiteStateMachine.addTransition(gs1FingerMeasureSpeed2, GestureState1FingerMeasureSpeed.FINGER_TAPPED, gsClickToFingerFirstCoords2);
        finiteStateMachine.addTransition(gs1FingerMeasureSpeed2, GestureState1FingerMeasureSpeed.FINGER_WALKED, gs1FingerMoveToMouseDragAndDrop);
        finiteStateMachine.addTransition(gs1FingerMeasureSpeed2, GestureState1FingerMeasureSpeed.FINGER_FLASHED, gs1FingerMoveToMouseDragAndDrop);
        finiteStateMachine.addTransition(gsWaitFingersNumberChangeWithTimeout, GestureStateWaitFingersNumberChangeWithTimeout.FINGER_RELEASED, fSMStateRunRunnable);
        finiteStateMachine.addTransition(gsWaitFingersNumberChangeWithTimeout, GestureStateWaitFingersNumberChangeWithTimeout.TIMED_OUT, gestureState2FingersToZoom);
        finiteStateMachine.addTransition(gsWaitFingersNumberChangeWithTimeout, GestureStateWaitFingersNumberChangeWithTimeout.FINGER_TOUCHED, gsWaitFingersNumberChangeWithTimeout2);
        finiteStateMachine.addTransition(gestureState2FingersToZoom, GestureState2FingersToZoom.FINGER_RELEASED, gestureState1FingerToZoomMove);
        finiteStateMachine.addTransition(gestureState1FingerToZoomMove, GestureState1FingerToZoomMove.FINGER_TOUCHED, gestureState2FingersToZoom);
        finiteStateMachine.addTransition(gsWaitFingersNumberChangeWithTimeout2, GestureStateWaitFingersNumberChangeWithTimeout.FINGER_RELEASED, fSMStateRunRunnable2);
        finiteStateMachine.addTransition(gsWaitFingersNumberChangeWithTimeout2, GestureStateWaitFingersNumberChangeWithTimeout.TIMED_OUT, fSMStateRunRunnable2);
        finiteStateMachine.setInitialState(gestureStateNeutral);
        finiteStateMachine.setDefaultState(gestureStateWaitForNeutral);
        finiteStateMachine.configurationCompleted();
        gestureContext.setMachine(finiteStateMachine);
        return gestureContext;
    }
}