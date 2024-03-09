package com.example.datainsert.exagear.controlsV2.options;

import com.eltechs.axs.activities.StartupActivity;
import com.example.datainsert.exagear.RR;

public class OptionQuit extends AbstractOption{
    @Override
    public void run() {
        StartupActivity.shutdownAXSApplication(true);
    }

    @Override
    public String getName() {
        return RR.getS(RR.global_quit);
    }
}
