package com.eltechs.ed.controls.touchControls;

import android.util.DisplayMetrics;
import android.view.View;
import com.eltechs.axs.GestureStateMachine.GestureContext;
import com.eltechs.axs.SimpleTouchScreenControl;
import com.eltechs.axs.TouchArea;
import com.eltechs.axs.TouchEventMultiplexor;
import com.eltechs.axs.TouchScreenControls;
import com.eltechs.axs.TouchScreenControlsFactory;
import com.eltechs.axs.activities.XServerDisplayActivityInterfaceOverlay;
import com.eltechs.axs.graphicsScene.GraphicsSceneConfigurer;
import com.eltechs.axs.helpers.AndroidHelpers;
import com.eltechs.axs.widgets.viewOfXServer.ViewOfXServer;

/* loaded from: classes.dex */
public abstract class AbstractTCF implements TouchScreenControlsFactory {
    private GestureContext mGestureContext;
    protected XServerDisplayActivityInterfaceOverlay mUIOverlay;

    public abstract GestureContext createGestureContext(ViewOfXServer viewOfXServer, TouchArea touchArea, TouchEventMultiplexor touchEventMultiplexor, int i);

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

    public void setUIOverlay(XServerDisplayActivityInterfaceOverlay xServerDisplayActivityInterfaceOverlay) {
        this.mUIOverlay = xServerDisplayActivityInterfaceOverlay;
    }

    private void fillTouchScreenControls(TouchScreenControls touchScreenControls, View view, ViewOfXServer viewOfXServer) {
        DisplayMetrics displayMetrics = AndroidHelpers.getDisplayMetrics();
        if (view.getWidth() <= displayMetrics.widthPixels / 2) {
            return;
        }
        TouchEventMultiplexor touchEventMultiplexor = new TouchEventMultiplexor();
        TouchArea touchArea = new TouchArea(0.0f, 0.0f, view.getWidth(), view.getHeight(), touchEventMultiplexor);
        this.mGestureContext = createGestureContext(viewOfXServer, touchArea, touchEventMultiplexor, displayMetrics.densityDpi);
        touchScreenControls.add(new SimpleTouchScreenControl(new TouchArea[]{touchArea}, null));
    }
}