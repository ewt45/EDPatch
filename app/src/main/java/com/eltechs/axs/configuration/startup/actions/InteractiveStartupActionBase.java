package com.eltechs.axs.configuration.startup.actions;

import com.eltechs.axs.activities.FrameworkActivity;
import com.eltechs.axs.configuration.startup.InteractiveStartupAction;
//import com.eltechs.axs.configuration.startup.InteractiveStartupAction;
import java.io.Serializable;

/* loaded from: classes.dex */
public abstract class InteractiveStartupActionBase<StateClass, UserResponseType extends Serializable> extends AbstractStartupAction<StateClass> implements InteractiveStartupAction<StateClass, UserResponseType> {
    /* JADX INFO: Access modifiers changed from: protected */
    public final void requestUserInput(Class<? extends FrameworkActivity> cls) {
        getStartupActions().requestUserInput(cls, null);
    }


    /* JADX INFO: Access modifiers changed from: protected */
    public final void requestUserInput(Class<? extends FrameworkActivity> cls, Serializable serializable) {
        getStartupActions().requestUserInput(cls, serializable);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final void requestUserInput() {
        getStartupActions().requestUserInput();
    }
}