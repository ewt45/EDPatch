package com.eltechs.axs.configuration.startup;

/* loaded from: classes.dex */
public interface StartupAction<StateClass> {
    void attach(StartupActionsCollection<StateClass> startupActionsCollection);

    void execute();

    StartupActionInfo getInfo();
}