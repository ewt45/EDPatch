package com.example.datainsert.exagear.controlsV2.options;

import com.eltechs.axs.activities.StartupActivity;

public class OptionQuit extends AbstractOption{
    @Override
    public void run() {
        StartupActivity.shutdownAXSApplication(true);
    }

    @Override
    public String getName() {
        return "退出";
    }
}
