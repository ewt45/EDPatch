package com.eltechs.axs.applicationState;

import com.eltechs.axs.configuration.startup.AvailableExecutableFiles;

/* loaded from: classes.dex */
public interface AvailableExecutableFilesAware<StateClass> {
    AvailableExecutableFiles<StateClass> getAvailableExecutableFiles();

    void setAvailableExecutableFiles(AvailableExecutableFiles<StateClass> availableExecutableFiles);
}