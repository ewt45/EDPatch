package com.example.datainsert.exagear.controls.axs.gamesControls;

import android.app.Activity;
import android.view.View;
import android.widget.FrameLayout;

import com.eltechs.axs.activities.XServerDisplayActivity;
import com.eltechs.axs.activities.XServerDisplayActivityInterfaceOverlay;
import com.eltechs.axs.widgets.viewOfXServer.ViewOfXServer;
import com.eltechs.ed.EDApplicationState;

import org.ewt45.customcontrols.InterfaceOverlay;
import org.ewt45.customcontrols.XserverView;

public class ComplicatedInterfaceOverlay implements XServerDisplayActivityInterfaceOverlay ,
        InterfaceOverlay<XServerDisplayActivity,ViewOfXServer> {
    @Override
    public View attach(XServerDisplayActivity xServerDisplayActivity, ViewOfXServer viewOfXServer) {
        return new FrameLayout(xServerDisplayActivity);
    }

    @Override
    public void detach() {

    }
}
