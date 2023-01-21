package com.eltechs.axs.widgets.touchScreenControlsOverlay;

import android.widget.FrameLayout;

import com.eltechs.axs.TouchScreenControlsFactory;
import com.eltechs.axs.activities.XServerDisplayActivity;
import com.eltechs.axs.configuration.TouchScreenControlsInputConfiguration;
import com.eltechs.axs.widgets.viewOfXServer.ViewOfXServer;

public class TouchScreenControlsWidget extends FrameLayout {


    public TouchScreenControlsWidget(XServerDisplayActivity xServerDisplayActivity, ViewOfXServer viewOfXServer, TouchScreenControlsFactory touchScreenControlsFactory, TouchScreenControlsInputConfiguration touchScreenControlsInputConfiguration) {
        super(xServerDisplayActivity);
    }


    public void setZOrderMediaOverlay(boolean z) {

    }

    public void detach() {
    }
}
