package com.eltechs.axs.gamesControls;

import android.view.View;

import com.eltechs.axs.TouchScreenControls;
import com.eltechs.axs.TouchScreenControlsFactory;
import com.eltechs.axs.widgets.viewOfXServer.ViewOfXServer;

public class TouchPadScreenControlsFactory implements TouchScreenControlsFactory {
    @Override
    public TouchScreenControls create(View view, ViewOfXServer viewOfXServer) {
        return null;
    }

    @Override
    public boolean hasVisibleControls() {
        return false;
    }
}
