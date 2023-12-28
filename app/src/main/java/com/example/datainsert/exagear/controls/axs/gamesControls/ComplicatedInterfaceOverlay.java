package com.example.datainsert.exagear.controls.axs.gamesControls;

import android.app.Activity;
import android.view.View;
import android.widget.FrameLayout;

import com.eltechs.axs.activities.XServerDisplayActivity;
import com.eltechs.axs.activities.XServerDisplayActivityInterfaceOverlay;
import com.eltechs.axs.widgets.viewOfXServer.ViewOfXServer;
import com.eltechs.ed.EDApplicationState;



public class ComplicatedInterfaceOverlay implements XServerDisplayActivityInterfaceOverlay {
    @Override
    public View attach(XServerDisplayActivity xServerDisplayActivity, ViewOfXServer viewOfXServer) {
        return new FrameLayout(xServerDisplayActivity);
    }

    @Override
    public void detach() {

    }
}
