package com.eltechs.ed.controls.touchControls;

import android.util.DisplayMetrics;
import com.eltechs.axs.GestureStateMachine.GestureContext;
import com.eltechs.axs.GestureStateMachine.GestureState1FingerMeasureSpeed;
import com.eltechs.axs.GestureStateMachine.GestureState1FingerMoveToMouseDragAndDrop;
import com.eltechs.axs.GestureStateMachine.GestureState1FingerMoveToMouseMoveWithClick;
import com.eltechs.axs.GestureStateMachine.GestureState1FingerMoveToScrollAsync;
import com.eltechs.axs.GestureStateMachine.GestureState1FingerToZoomMove;
import com.eltechs.axs.GestureStateMachine.GestureState2FingersToZoom;
import com.eltechs.axs.GestureStateMachine.GestureStateClickToFingerFirstCoords;
import com.eltechs.axs.GestureStateMachine.GestureStateNeutral;
import com.eltechs.axs.GestureStateMachine.GestureStateWaitFingersNumberChangeWithTimeout;
import com.eltechs.axs.GestureStateMachine.GestureStateWaitForNeutral;
import com.eltechs.axs.GestureStateMachine.PointerContext;
import com.eltechs.axs.GuestAppActionAdapters.AlignedMouseClickAdapter;
import com.eltechs.axs.GuestAppActionAdapters.AsyncScrollAdapterWithPointer;
import com.eltechs.axs.GuestAppActionAdapters.MouseClickAdapterWithCheckPlacementContext;
import com.eltechs.axs.GuestAppActionAdapters.OffsetMouseMoveAdapter;
import com.eltechs.axs.GuestAppActionAdapters.PressAndHoldMouseClickAdapter;
import com.eltechs.axs.GuestAppActionAdapters.PressAndReleaseMouseClickAdapter;
import com.eltechs.axs.GuestAppActionAdapters.SimpleDragAndDropAdapter;
import com.eltechs.axs.GuestAppActionAdapters.SimpleMouseMoveAdapter;
import com.eltechs.axs.GuestAppActionAdapters.SimpleMousePointAndClickAdapter;
import com.eltechs.axs.TouchArea;
import com.eltechs.axs.TouchEventMultiplexor;
import com.eltechs.axs.finiteStateMachine.FiniteStateMachine;
import com.eltechs.axs.finiteStateMachine.generalStates.FSMStateRunRunnable;
import com.eltechs.axs.geom.Rectangle;
import com.eltechs.axs.helpers.AndroidHelpers;
import com.eltechs.axs.widgets.viewOfXServer.ViewOfXServer;
import com.eltechs.ed.controls.uiOverlays.DefaultUIOverlay;

/* loaded from: classes.dex */
public class RtsTCF extends AbstractTCF {
    private static final float clickAlignThresholdInches = 0.15f;
    private static final float doubleClickMaxDistanceInches = 0.15f;
    private static final int doubleClickMaxIntervalMs = 200;
    private static final float fingerAimingMaxMoveDiagCoeff = 0.03f;
    private static final float fingerStandingMaxMoveInches = 0.07f;
    private static final int fingerToLongTimeMs = 100;
    private static final int fingerToVeryLongTimeMs = 300;
    private static final int maxExtraTapTimeMs = 400;
    private static final int mouseActionSleepMs = 30;

    @Override // com.eltechs.ed.controls.touchControls.AbstractTCF
    public GestureContext createGestureContext(ViewOfXServer viewOfXServer, TouchArea touchArea, TouchEventMultiplexor touchEventMultiplexor, int i) {
        GestureContext gestureContext = new GestureContext(viewOfXServer, touchArea, touchEventMultiplexor);
        PointerContext pointerContext = new PointerContext();
        GestureStateNeutral gestureStateNeutral = new GestureStateNeutral(gestureContext);
        GestureStateWaitForNeutral gestureStateWaitForNeutral = new GestureStateWaitForNeutral(gestureContext);
        DisplayMetrics displayMetrics = AndroidHelpers.getDisplayMetrics();
        float f = i;
        float f2 = fingerStandingMaxMoveInches * f;
        GestureState1FingerMeasureSpeed gestureState1FingerMeasureSpeed = new GestureState1FingerMeasureSpeed(gestureContext, 100, f2, ((int) Math.sqrt(Math.pow(displayMetrics.widthPixels, 2.0d) + Math.pow(displayMetrics.heightPixels, 2.0d))) * fingerAimingMaxMoveDiagCoeff, f2, 0.0f);
        GestureStateClickToFingerFirstCoords gestureStateClickToFingerFirstCoords = new GestureStateClickToFingerFirstCoords(gestureContext, new SimpleMousePointAndClickAdapter(new SimpleMouseMoveAdapter(gestureContext.getPointerReporter()), new PressAndReleaseMouseClickAdapter(gestureContext.getPointerReporter(), 3, 30), pointerContext));
        float f3 = 0.15f * f;
        GestureStateClickToFingerFirstCoords gestureStateClickToFingerFirstCoords2 = new GestureStateClickToFingerFirstCoords(gestureContext, new MouseClickAdapterWithCheckPlacementContext(new SimpleMousePointAndClickAdapter(new SimpleMouseMoveAdapter(gestureContext.getPointerReporter()), new PressAndReleaseMouseClickAdapter(gestureContext.getPointerReporter(), 1, 30), pointerContext), new AlignedMouseClickAdapter(new SimpleMouseMoveAdapter(gestureContext.getPointerReporter()), new PressAndReleaseMouseClickAdapter(gestureContext.getPointerReporter(), 1, 30), new PressAndReleaseMouseClickAdapter(gestureContext.getPointerReporter(), 1, 30), viewOfXServer, pointerContext, f3), new AlignedMouseClickAdapter(new SimpleMouseMoveAdapter(gestureContext.getPointerReporter()), new PressAndReleaseMouseClickAdapter(gestureContext.getPointerReporter(), 1, 30), new PressAndReleaseMouseClickAdapter(gestureContext.getPointerReporter(), 1, 30), viewOfXServer, pointerContext, f3), pointerContext, 200));
        GestureState1FingerMoveToMouseMoveWithClick gestureState1FingerMoveToMouseMoveWithClick = new GestureState1FingerMoveToMouseMoveWithClick(gestureContext, pointerContext, new OffsetMouseMoveAdapter(new SimpleMouseMoveAdapter(gestureContext.getPointerReporter()), 0.0f * f, (-0.2f) * f), new PressAndReleaseMouseClickAdapter(gestureContext.getPointerReporter(), 1, 30));
        GestureState1FingerMoveToScrollAsync gestureState1FingerMoveToScrollAsync = new GestureState1FingerMoveToScrollAsync(gestureContext, new AsyncScrollAdapterWithPointer(gestureContext.getViewFacade(), new Rectangle(0, 0, gestureContext.getViewFacade().getScreenInfo().widthInPixels, gestureContext.getViewFacade().getScreenInfo().heightInPixels)), 1000000.0f, 1000000.0f, f * 0.1f, true, 15, true);
        GestureState1FingerMeasureSpeed gestureState1FingerMeasureSpeed2 = new GestureState1FingerMeasureSpeed(gestureContext, fingerToVeryLongTimeMs, f2, f2, f2, 0.0f);
        GestureState1FingerMoveToMouseDragAndDrop gestureState1FingerMoveToMouseDragAndDrop = new GestureState1FingerMoveToMouseDragAndDrop(gestureContext, new SimpleDragAndDropAdapter(new SimpleMouseMoveAdapter(gestureContext.getPointerReporter()), new PressAndHoldMouseClickAdapter(gestureContext.getPointerReporter(), 1), null), pointerContext, false, 0.0f);
        GestureState1FingerMoveToMouseDragAndDrop gestureState1FingerMoveToMouseDragAndDrop2 = new GestureState1FingerMoveToMouseDragAndDrop(gestureContext, new SimpleDragAndDropAdapter(new SimpleMouseMoveAdapter(gestureContext.getPointerReporter()), new PressAndHoldMouseClickAdapter(gestureContext.getPointerReporter(), 3), null), pointerContext, false, 0.0f);
        GestureStateWaitFingersNumberChangeWithTimeout gestureStateWaitFingersNumberChangeWithTimeout = new GestureStateWaitFingersNumberChangeWithTimeout(gestureContext, maxExtraTapTimeMs);
        GestureState1FingerMeasureSpeed gestureState1FingerMeasureSpeed3 = new GestureState1FingerMeasureSpeed(gestureContext, 1000000, f2, f2, f2, 1000000.0f);
        FSMStateRunRunnable fSMStateRunRunnable = new FSMStateRunRunnable(new Runnable() { // from class: com.eltechs.ed.controls.touchControls.RtsTCF.1
            @Override // java.lang.Runnable
            public void run() {
                AndroidHelpers.toggleSoftInput();
            }
        });
        GestureState2FingersToZoom gestureState2FingersToZoom = new GestureState2FingersToZoom(gestureContext);
        GestureState1FingerToZoomMove gestureState1FingerToZoomMove = new GestureState1FingerToZoomMove(gestureContext);
        GestureStateWaitFingersNumberChangeWithTimeout gestureStateWaitFingersNumberChangeWithTimeout2 = new GestureStateWaitFingersNumberChangeWithTimeout(gestureContext, maxExtraTapTimeMs);
        FSMStateRunRunnable fSMStateRunRunnable2 = new FSMStateRunRunnable(new Runnable() { // from class: com.eltechs.ed.controls.touchControls.RtsTCF.2
            @Override // java.lang.Runnable
            public void run() {
                ((DefaultUIOverlay) RtsTCF.this.mUIOverlay).toggleToolbar();
            }
        });
        FiniteStateMachine finiteStateMachine = new FiniteStateMachine();
        finiteStateMachine.setStatesList(gestureStateWaitForNeutral, gestureStateNeutral, gestureState1FingerMeasureSpeed, gestureStateClickToFingerFirstCoords2, gestureState1FingerMoveToMouseMoveWithClick, gestureState1FingerMoveToScrollAsync, gestureState1FingerMeasureSpeed2, gestureStateClickToFingerFirstCoords, gestureState1FingerMoveToMouseDragAndDrop2, gestureState1FingerMoveToMouseDragAndDrop, gestureStateWaitFingersNumberChangeWithTimeout, gestureState1FingerMeasureSpeed3, fSMStateRunRunnable, gestureState2FingersToZoom, gestureState1FingerToZoomMove, gestureStateWaitFingersNumberChangeWithTimeout2, fSMStateRunRunnable2);
        finiteStateMachine.addTransition(gestureStateWaitForNeutral, GestureStateWaitForNeutral.GESTURE_COMPLETED, gestureStateNeutral);
        finiteStateMachine.addTransition(gestureStateNeutral, GestureStateNeutral.FINGER_TOUCHED, gestureState1FingerMeasureSpeed);
        finiteStateMachine.addTransition(gestureState1FingerMeasureSpeed, GestureState1FingerMeasureSpeed.FINGER_TAPPED, gestureStateClickToFingerFirstCoords2);
        finiteStateMachine.addTransition(gestureState1FingerMeasureSpeed, GestureState1FingerMeasureSpeed.FINGER_WALKED_AND_GONE, gestureStateClickToFingerFirstCoords2);
        finiteStateMachine.addTransition(gestureState1FingerMeasureSpeed, GestureState1FingerMeasureSpeed.FINGER_WALKED, gestureState1FingerMoveToMouseMoveWithClick);
        finiteStateMachine.addTransition(gestureState1FingerMeasureSpeed, GestureState1FingerMeasureSpeed.FINGER_FLASHED, gestureState1FingerMoveToScrollAsync);
        finiteStateMachine.addTransition(gestureState1FingerMeasureSpeed, GestureState1FingerMeasureSpeed.FINGER_STANDING, gestureState1FingerMeasureSpeed2);
        finiteStateMachine.addTransition(gestureState1FingerMeasureSpeed, GestureState1FingerMeasureSpeed.FINGER_TOUCHED, gestureStateWaitFingersNumberChangeWithTimeout);
        finiteStateMachine.addTransition(gestureState1FingerMeasureSpeed2, GestureState1FingerMeasureSpeed.FINGER_TAPPED, gestureStateClickToFingerFirstCoords);
        finiteStateMachine.addTransition(gestureState1FingerMeasureSpeed2, GestureState1FingerMeasureSpeed.FINGER_WALKED_AND_GONE, gestureStateClickToFingerFirstCoords);
        finiteStateMachine.addTransition(gestureState1FingerMeasureSpeed2, GestureState1FingerMeasureSpeed.FINGER_WALKED, gestureState1FingerMoveToMouseMoveWithClick);
        finiteStateMachine.addTransition(gestureState1FingerMeasureSpeed2, GestureState1FingerMeasureSpeed.FINGER_FLASHED, gestureState1FingerMoveToMouseMoveWithClick);
        finiteStateMachine.addTransition(gestureState1FingerMeasureSpeed2, GestureState1FingerMeasureSpeed.FINGER_STANDING, gestureState1FingerMoveToMouseDragAndDrop2);
        finiteStateMachine.addTransition(gestureState1FingerMeasureSpeed2, GestureState1FingerMeasureSpeed.FINGER_TOUCHED, gestureStateWaitFingersNumberChangeWithTimeout);
        finiteStateMachine.addTransition(gestureStateWaitFingersNumberChangeWithTimeout, GestureStateWaitFingersNumberChangeWithTimeout.TIMED_OUT, gestureState2FingersToZoom);
        finiteStateMachine.addTransition(gestureStateWaitFingersNumberChangeWithTimeout, GestureStateWaitFingersNumberChangeWithTimeout.FINGER_TOUCHED, gestureStateWaitFingersNumberChangeWithTimeout2);
        finiteStateMachine.addTransition(gestureStateWaitFingersNumberChangeWithTimeout, GestureStateWaitFingersNumberChangeWithTimeout.FINGER_RELEASED, gestureState1FingerMeasureSpeed3);
        finiteStateMachine.addTransition(gestureState2FingersToZoom, GestureState2FingersToZoom.FINGER_RELEASED, gestureState1FingerToZoomMove);
        finiteStateMachine.addTransition(gestureState1FingerToZoomMove, GestureState1FingerToZoomMove.FINGER_TOUCHED, gestureState2FingersToZoom);
        finiteStateMachine.addTransition(gestureState1FingerMeasureSpeed3, GestureState1FingerMeasureSpeed.FINGER_TAPPED, fSMStateRunRunnable);
        finiteStateMachine.addTransition(gestureState1FingerMeasureSpeed3, GestureState1FingerMeasureSpeed.FINGER_WALKED, gestureState1FingerMoveToMouseDragAndDrop);
        finiteStateMachine.addTransition(gestureState1FingerMeasureSpeed3, GestureState1FingerMeasureSpeed.FINGER_FLASHED, gestureState1FingerMoveToMouseDragAndDrop);
        finiteStateMachine.addTransition(gestureStateWaitFingersNumberChangeWithTimeout2, GestureStateWaitFingersNumberChangeWithTimeout.FINGER_RELEASED, fSMStateRunRunnable2);
        finiteStateMachine.addTransition(gestureStateWaitFingersNumberChangeWithTimeout2, GestureStateWaitFingersNumberChangeWithTimeout.TIMED_OUT, fSMStateRunRunnable2);
        finiteStateMachine.setInitialState(gestureStateNeutral);
        finiteStateMachine.setDefaultState(gestureStateWaitForNeutral);
        finiteStateMachine.configurationCompleted();
        gestureContext.setMachine(finiteStateMachine);
        return gestureContext;
    }
}