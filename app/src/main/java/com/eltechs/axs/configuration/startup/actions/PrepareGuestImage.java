package com.eltechs.axs.configuration.startup.actions;

import com.eltechs.axs.applicationState.ExagearImageAware;

import java.io.File;

public class PrepareGuestImage<StateClass extends ExagearImageAware> extends AbstractStartupAction<StateClass> {
    private final String homeDir;
    private final File hostDirInUserArea;

    public PrepareGuestImage(String str, File file) {
        this.homeDir = str;
        this.hostDirInUserArea = file;
    }

    @Override
    public void execute() {

    }
}
