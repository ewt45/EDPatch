package com.eltechs.axs.gamesControls;

import com.eltechs.axs.GestureStateMachine.GestureContext;
import com.eltechs.axs.GestureStateMachine.GestureJoyStickMode;
import com.eltechs.axs.GestureStateMachine.GestureMouseMode;
import com.eltechs.axs.GestureStateMachine.GestureState1FingerMeasureSpeed;
import com.eltechs.axs.GestureStateMachine.GestureState1FingerMoveToMouseDragAndDrop;
import com.eltechs.axs.GestureStateMachine.GestureState1FingerMoveToMouseMove;
import com.eltechs.axs.GestureStateMachine.GestureState1FingerMoveToScrollSync;
import com.eltechs.axs.GestureStateMachine.GestureState1FingerToZoomMove;
import com.eltechs.axs.GestureStateMachine.GestureState2FingersToZoom;
import com.eltechs.axs.GestureStateMachine.GestureState3FingersToZoom;
import com.eltechs.axs.GestureStateMachine.GestureStateCheckIfZoomed;
import com.eltechs.axs.GestureStateMachine.GestureStateCheckJoyStickMode;
import com.eltechs.axs.GestureStateMachine.GestureStateCheckMouseMode;
import com.eltechs.axs.GestureStateMachine.GestureStateClickToFingerFirstCoords;
import com.eltechs.axs.GestureStateMachine.GestureStateNeutral;
import com.eltechs.axs.GestureStateMachine.GestureStatePressAndHoldKeyUntilFingerRelease;
import com.eltechs.axs.GestureStateMachine.GestureStateWaitFingersNumberChangeWithTimeout;
import com.eltechs.axs.GestureStateMachine.GestureStateWaitForNeutral;
import com.eltechs.axs.GestureStateMachine.PointerContext;
import com.eltechs.axs.GuestAppActionAdapters.AlignedMouseClickAdapter;
import com.eltechs.axs.GuestAppActionAdapters.MouseClickAdapterWithCheckPlacementContext;
import com.eltechs.axs.GuestAppActionAdapters.OffsetMouseMoveAdapter;
import com.eltechs.axs.GuestAppActionAdapters.PressAndHoldMouseClickAdapter;
import com.eltechs.axs.GuestAppActionAdapters.PressAndHoldWithPauseMouseClickAdapter;
import com.eltechs.axs.GuestAppActionAdapters.PressAndReleaseMouseClickAdapter;
import com.eltechs.axs.GuestAppActionAdapters.ScrollAdapterArrowOnly;
import com.eltechs.axs.GuestAppActionAdapters.SimpleDragAndDropAdapter;
import com.eltechs.axs.GuestAppActionAdapters.SimpleMouseMoveAdapter;
import com.eltechs.axs.GuestAppActionAdapters.SimpleMousePointAndClickAdapter;
import com.eltechs.axs.KeyCodesX;
import com.eltechs.axs.TouchArea;
import com.eltechs.axs.TouchEventMultiplexor;
import com.eltechs.axs.finiteStateMachine.FiniteStateMachine;
import com.eltechs.axs.finiteStateMachine.generalStates.FSMStateRunRunnable;
import com.eltechs.axs.widgets.viewOfXServer.TransformationHelpers;
import com.eltechs.axs.widgets.viewOfXServer.ViewOfXServer;

/**
 * 非原版代码。暗黑2操作模式
 */
@Deprecated
public class GestureMachineConfigurerDiablo {
    private static final float clickAlignThresholdInches = 0.3f;
    private static final float doubleClickMaxDistance = 0.15f;
    private static final int doubleClickMaxIntervalMs = 200;
    private static final float fingerAimingMaxMoveInches = 0.12f;
    private static final int fingerSpeedCheckTimeMs = 400;
    private static final float fingerStandingMaxMoveInches = 0.12f;
    private static final float fingerTapMaxMoveInches = 0.12f;
    private static final int fingerTapMaxTimeMs = 400;
    private static final int maxTapTimeMs = 300;
    private static final int mouseActionSleepMs = 30;
    private static final float pixelsInScrollUnitX = 50.0f;
    private static final float pixelsInScrollUnitY = 50.0f;
    private static final int pointerMarginXPixels = 50;
    private static final long scrollPeriodMs = 30;
    private static final long scrollTimerPeriod = 150;

    public static GestureContext createGestureContext(ViewOfXServer viewOfXServer, TouchArea touchArea, TouchEventMultiplexor touchEventMultiplexor, int i, final GestureMouseMode gestureMouseMode, GestureJoyStickMode gestureJoyStickMode, Runnable runnable) {
        GestureContext gestureContext = new GestureContext(viewOfXServer, touchArea, touchEventMultiplexor);
        PointerContext pointerContext = new PointerContext();
        GestureStateNeutral gestureStateNeutral = new GestureStateNeutral(gestureContext);
        GestureStateWaitForNeutral gestureStateWaitForNeutral = new GestureStateWaitForNeutral(gestureContext);
        float f = i;
        float f2 = f * 0.12f;
        GestureState1FingerMeasureSpeed gestureState1FingerMeasureSpeed = new GestureState1FingerMeasureSpeed(gestureContext, 400, f2, f2, f2, 400.0f);
        GestureStateCheckIfZoomed gestureStateCheckIfZoomed = new GestureStateCheckIfZoomed(gestureContext);
        GestureStateClickToFingerFirstCoords gestureStateClickToFingerFirstCoords = new GestureStateClickToFingerFirstCoords(gestureContext, new MouseClickAdapterWithCheckPlacementContext(new SimpleMousePointAndClickAdapter(new SimpleMouseMoveAdapter(gestureContext.getPointerReporter()), new PressAndReleaseMouseClickAdapter(gestureContext.getPointerReporter(), 1, 30), pointerContext), new AlignedMouseClickAdapter(new SimpleMouseMoveAdapter(gestureContext.getPointerReporter()), new PressAndReleaseMouseClickAdapter(gestureContext.getPointerReporter(), 1, 30), new PressAndReleaseMouseClickAdapter(gestureContext.getPointerReporter(), 1, 30), viewOfXServer, pointerContext, f * clickAlignThresholdInches), new AlignedMouseClickAdapter(new SimpleMouseMoveAdapter(gestureContext.getPointerReporter()), new PressAndReleaseMouseClickAdapter(gestureContext.getPointerReporter(), 1, 30), new PressAndReleaseMouseClickAdapter(gestureContext.getPointerReporter(), 1, 30), viewOfXServer, pointerContext, f * 0.15f), pointerContext, 200));
        GestureStateClickToFingerFirstCoords gestureStateClickToFingerFirstCoords2 = new GestureStateClickToFingerFirstCoords(gestureContext, new SimpleMousePointAndClickAdapter(new SimpleMouseMoveAdapter(gestureContext.getPointerReporter()), new PressAndReleaseMouseClickAdapter(gestureContext.getPointerReporter(), 3, 30), pointerContext));
        GestureStateClickToFingerFirstCoords gestureStateClickToFingerFirstCoords3 = new GestureStateClickToFingerFirstCoords(gestureContext, new SimpleMousePointAndClickAdapter(new SimpleMouseMoveAdapter(gestureContext.getPointerReporter()), new PressAndReleaseMouseClickAdapter(gestureContext.getPointerReporter(), 3, 30), pointerContext));
        GestureStateWaitFingersNumberChangeWithTimeout gestureStateWaitFingersNumberChangeWithTimeout = new GestureStateWaitFingersNumberChangeWithTimeout(gestureContext, 1000000000);
        GestureStateWaitFingersNumberChangeWithTimeout gestureStateWaitFingersNumberChangeWithTimeout2 = new GestureStateWaitFingersNumberChangeWithTimeout(gestureContext, maxTapTimeMs);
        GestureStateWaitFingersNumberChangeWithTimeout gestureStateWaitFingersNumberChangeWithTimeout3 = new GestureStateWaitFingersNumberChangeWithTimeout(gestureContext, maxTapTimeMs);
        FSMStateRunRunnable fSMStateRunRunnable = new FSMStateRunRunnable(runnable);
        FSMStateRunRunnable fSMStateRunRunnable2 = new FSMStateRunRunnable(new Runnable() { // from class: com.eltechs.axs.gamesControls.GestureMachineConfigurerDiablo.1_fix
            @Override // java.lang.Runnable
            public void run() {
                gestureMouseMode.setState(GestureMouseMode.MouseModeState.MOUSE_MODE_RIGHT);
            }
        });
        GestureState1FingerMoveToMouseDragAndDrop gestureState1FingerMoveToMouseDragAndDrop = new GestureState1FingerMoveToMouseDragAndDrop(gestureContext, new SimpleDragAndDropAdapter(new SimpleMouseMoveAdapter(gestureContext.getPointerReporter()), new PressAndHoldWithPauseMouseClickAdapter(gestureContext.getPointerReporter(), 1, 30), new Runnable() { // from class: com.eltechs.axs.gamesControls.GestureMachineConfigurerDiablo.2_fix
            @Override // java.lang.Runnable
            public void run() {
            }
        }), pointerContext, false, 0.0f);
        float f3 = f * 0.0f;
        GestureState1FingerMoveToMouseMove gestureState1FingerMoveToMouseMove = new GestureState1FingerMoveToMouseMove(gestureContext, pointerContext, new OffsetMouseMoveAdapter(new SimpleMouseMoveAdapter(gestureContext.getPointerReporter()), f3, f3));
        GestureState1FingerMoveToMouseDragAndDrop gestureState1FingerMoveToMouseDragAndDrop2 = new GestureState1FingerMoveToMouseDragAndDrop(gestureContext, new SimpleDragAndDropAdapter(new SimpleMouseMoveAdapter(gestureContext.getPointerReporter()), new PressAndHoldMouseClickAdapter(gestureContext.getPointerReporter(), 3), new Runnable() { // from class: com.eltechs.axs.gamesControls.GestureMachineConfigurerDiablo.3_fix
            @Override // java.lang.Runnable
            public void run() {
            }
        }), pointerContext, false, 0.0f);
        GestureState1FingerMoveToScrollSync gestureState1FingerMoveToScrollSync = new GestureState1FingerMoveToScrollSync(gestureContext, new ScrollAdapterArrowOnly(gestureContext.getKeyboardReporter()), TransformationHelpers.getScaleX(viewOfXServer.getViewToXServerTransformationMatrix()) * 0.05f, TransformationHelpers.getScaleY(viewOfXServer.getViewToXServerTransformationMatrix()) * 0.05f, 0.0f, false, 0, scrollPeriodMs, true);
        GestureState1FingerToZoomMove gestureState1FingerToZoomMove = new GestureState1FingerToZoomMove(gestureContext);
        GestureState3FingersToZoom gestureState3FingersToZoom = new GestureState3FingersToZoom(gestureContext);
        GestureState2FingersToZoom gestureState2FingersToZoom = new GestureState2FingersToZoom(gestureContext);
        GestureStateCheckMouseMode gestureStateCheckMouseMode = new GestureStateCheckMouseMode(gestureContext, gestureMouseMode);
        GestureStateCheckMouseMode gestureStateCheckMouseMode2 = new GestureStateCheckMouseMode(gestureContext, gestureMouseMode);
        GestureStateCheckJoyStickMode gestureStateCheckJoyStickMode = new GestureStateCheckJoyStickMode(gestureContext, gestureJoyStickMode);
        GestureStatePressAndHoldKeyUntilFingerRelease gestureStatePressAndHoldKeyUntilFingerRelease = new GestureStatePressAndHoldKeyUntilFingerRelease(gestureContext, KeyCodesX.KEY_SHIFT_LEFT);
        FiniteStateMachine finiteStateMachine = new FiniteStateMachine();
        finiteStateMachine.setStatesList(gestureStateNeutral, gestureState1FingerMeasureSpeed, gestureStateClickToFingerFirstCoords, gestureStateCheckIfZoomed, gestureStateWaitFingersNumberChangeWithTimeout, gestureStateWaitFingersNumberChangeWithTimeout2, gestureStateWaitFingersNumberChangeWithTimeout3, fSMStateRunRunnable2, fSMStateRunRunnable, gestureState1FingerMoveToMouseDragAndDrop2, gestureState1FingerMoveToMouseDragAndDrop, gestureStateClickToFingerFirstCoords2, gestureStateClickToFingerFirstCoords3, gestureState1FingerMoveToScrollSync, gestureState1FingerToZoomMove, gestureState2FingersToZoom, gestureState3FingersToZoom, gestureStateCheckMouseMode, gestureStateCheckMouseMode2, gestureStateCheckJoyStickMode, gestureStatePressAndHoldKeyUntilFingerRelease, gestureState1FingerMoveToMouseMove, gestureStateWaitForNeutral);
        finiteStateMachine.addTransition(gestureStateWaitForNeutral, GestureStateWaitForNeutral.GESTURE_COMPLETED, gestureStateNeutral);
        finiteStateMachine.addTransition(gestureStateNeutral, GestureStateNeutral.FINGER_TOUCHED, gestureStateCheckJoyStickMode);
        finiteStateMachine.addTransition(gestureStateCheckJoyStickMode, GestureStateCheckJoyStickMode.JOYSTICK_MODE_OFF, gestureState1FingerMeasureSpeed);
        finiteStateMachine.addTransition(gestureStateCheckJoyStickMode, GestureStateCheckJoyStickMode.JOYSTICK_MODE_ON, gestureStatePressAndHoldKeyUntilFingerRelease);
        finiteStateMachine.addTransition(gestureState1FingerMeasureSpeed, GestureState1FingerMeasureSpeed.FINGER_FLASHED, gestureStateCheckIfZoomed);
        finiteStateMachine.addTransition(gestureStateCheckIfZoomed, GestureStateCheckIfZoomed.ZOOM_OFF, gestureState1FingerMoveToMouseDragAndDrop);
        finiteStateMachine.addTransition(gestureStateCheckIfZoomed, GestureStateCheckIfZoomed.ZOOM_ON, gestureState1FingerToZoomMove);
        finiteStateMachine.addTransition(gestureState1FingerMeasureSpeed, GestureState1FingerMeasureSpeed.FINGER_TOUCHED, gestureStateWaitFingersNumberChangeWithTimeout2);
        finiteStateMachine.addTransition(gestureStateWaitFingersNumberChangeWithTimeout2, GestureStateWaitFingersNumberChangeWithTimeout.TIMED_OUT, gestureState1FingerMoveToMouseDragAndDrop2);
        finiteStateMachine.addTransition(gestureState1FingerToZoomMove, GestureState1FingerToZoomMove.FINGER_TOUCHED, gestureState2FingersToZoom);
        finiteStateMachine.addTransition(gestureState2FingersToZoom, GestureState2FingersToZoom.FINGER_TOUCHED, gestureState3FingersToZoom);
        finiteStateMachine.addTransition(gestureState2FingersToZoom, GestureState2FingersToZoom.FINGER_RELEASED, gestureState1FingerToZoomMove);
        finiteStateMachine.addTransition(gestureStateWaitFingersNumberChangeWithTimeout2, GestureStateWaitFingersNumberChangeWithTimeout.FINGER_TOUCHED, gestureStateWaitFingersNumberChangeWithTimeout3);
        finiteStateMachine.addTransition(gestureStateWaitFingersNumberChangeWithTimeout3, GestureStateWaitFingersNumberChangeWithTimeout.TIMED_OUT, gestureState3FingersToZoom);
        finiteStateMachine.addTransition(gestureState3FingersToZoom, GestureState3FingersToZoom.FINGER_RELEASED, gestureState2FingersToZoom);
        finiteStateMachine.addTransition(gestureStateWaitFingersNumberChangeWithTimeout3, GestureStateWaitFingersNumberChangeWithTimeout.FINGER_TOUCHED, fSMStateRunRunnable);
        finiteStateMachine.addTransition(gestureState1FingerMeasureSpeed, GestureState1FingerMeasureSpeed.FINGER_TAPPED, gestureStateCheckMouseMode);
        finiteStateMachine.addTransition(gestureState1FingerMeasureSpeed, GestureState1FingerMeasureSpeed.FINGER_WALKED, gestureStateCheckMouseMode2);
        finiteStateMachine.addTransition(gestureState1FingerMeasureSpeed, GestureState1FingerMeasureSpeed.FINGER_STANDING, gestureStateCheckMouseMode2);
        finiteStateMachine.addTransition(gestureStateCheckMouseMode, GestureStateCheckMouseMode.MOUSE_MODE_LEFT, gestureStateClickToFingerFirstCoords);
        finiteStateMachine.addTransition(gestureStateCheckMouseMode, GestureStateCheckMouseMode.MOUSE_MODE_RIGHT, gestureStateClickToFingerFirstCoords2);
        finiteStateMachine.addTransition(gestureStateCheckMouseMode2, GestureStateCheckMouseMode.MOUSE_MODE_LEFT, gestureState1FingerMoveToMouseDragAndDrop);
        finiteStateMachine.addTransition(gestureStateCheckMouseMode2, GestureStateCheckMouseMode.MOUSE_MODE_RIGHT, gestureState1FingerMoveToMouseMove);
        finiteStateMachine.addTransition(gestureStateWaitFingersNumberChangeWithTimeout2, GestureStateWaitFingersNumberChangeWithTimeout.FINGER_RELEASED, gestureStateClickToFingerFirstCoords3);
        finiteStateMachine.addTransition(gestureStateClickToFingerFirstCoords3, GestureStateClickToFingerFirstCoords.GESTURE_COMPLETED, gestureStateWaitFingersNumberChangeWithTimeout);
        finiteStateMachine.addTransition(gestureStateWaitFingersNumberChangeWithTimeout, GestureStateWaitFingersNumberChangeWithTimeout.FINGER_TOUCHED, gestureStateWaitFingersNumberChangeWithTimeout2);
        finiteStateMachine.addTransition(gestureStateWaitFingersNumberChangeWithTimeout, GestureStateWaitFingersNumberChangeWithTimeout.TIMED_OUT, gestureStateWaitFingersNumberChangeWithTimeout);
        finiteStateMachine.setInitialState(gestureStateNeutral);
        finiteStateMachine.setDefaultState(gestureStateWaitForNeutral);
        finiteStateMachine.configurationCompleted();
        gestureContext.setMachine(finiteStateMachine);
        return gestureContext;
    }
}