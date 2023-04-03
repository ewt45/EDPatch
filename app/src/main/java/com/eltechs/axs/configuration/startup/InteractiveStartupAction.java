package com.eltechs.axs.configuration.startup;

import java.io.Serializable;

/* loaded from: classes.dex */
public interface InteractiveStartupAction<StateClass, UserResponseClass extends Serializable> extends StartupAction<StateClass> {
    void userInteractionCanceled();

    void userInteractionFinished(UserResponseClass userresponseclass);
}