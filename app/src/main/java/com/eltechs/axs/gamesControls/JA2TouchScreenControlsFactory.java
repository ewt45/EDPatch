package com.eltechs.axs.gamesControls;

import android.util.DisplayMetrics;
import android.view.View;
import com.eltechs.axs.GestureStateMachine.GestureContext;
import com.eltechs.axs.Globals;
import com.eltechs.axs.SimpleTouchScreenControl;
import com.eltechs.axs.TouchArea;
import com.eltechs.axs.TouchEventMultiplexor;
import com.eltechs.axs.TouchScreenControls;
import com.eltechs.axs.TouchScreenControlsFactory;
import com.eltechs.axs.activities.XServerDisplayActivity;
import com.eltechs.axs.applicationState.ApplicationStateBase;
import com.eltechs.axs.graphicsScene.GraphicsSceneConfigurer;
import com.eltechs.axs.helpers.AndroidHelpers;
import com.eltechs.axs.widgets.viewOfXServer.ViewOfXServer;

/* loaded from: classes.dex */
public class JA2TouchScreenControlsFactory implements TouchScreenControlsFactory {
    private GestureContext gestureContext;

    @Override // com.eltechs.axs.TouchScreenControlsFactory
    public boolean hasVisibleControls() {
        return false;
    }

    @Override // com.eltechs.axs.TouchScreenControlsFactory
    public TouchScreenControls create(View view, ViewOfXServer viewOfXServer) {
        GraphicsSceneConfigurer graphicsSceneConfigurer = new GraphicsSceneConfigurer();
        graphicsSceneConfigurer.setSceneViewport(0.0f, 0.0f, view.getWidth(), view.getHeight());
        TouchScreenControls touchScreenControls = new TouchScreenControls(graphicsSceneConfigurer);
        fillTouchScreenControls(touchScreenControls, view, viewOfXServer);
        return touchScreenControls;
    }

    private void fillTouchScreenControls(TouchScreenControls touchScreenControls, View view, ViewOfXServer viewOfXServer) {
        DisplayMetrics displayMetrics = AndroidHelpers.getDisplayMetrics();
        if (view.getWidth() <= displayMetrics.widthPixels / 2) {
            return;
        }
        TouchEventMultiplexor touchEventMultiplexor = new TouchEventMultiplexor();
        TouchArea touchArea = new TouchArea(0.0f, 0.0f, view.getWidth(), view.getHeight(), touchEventMultiplexor);
        this.gestureContext = GestureMachineConfigurerJA2.createGestureContext(viewOfXServer, touchArea, touchEventMultiplexor, displayMetrics.densityDpi, () -> ((XServerDisplayActivity) ((ApplicationStateBase) Globals.getApplicationState()).getCurrentActivity()).showPopupMenu());
        touchScreenControls.add(new SimpleTouchScreenControl(new TouchArea[]{touchArea}, null));
    }
}
