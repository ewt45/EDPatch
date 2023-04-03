package com.eltechs.axs.configuration.startup.actions;

import com.eltechs.axs.helpers.Assert;
import java.io.Serializable;

/* loaded from: classes.dex */
public abstract class SimpleInteractiveStartupActionBase<StateClass> extends InteractiveStartupActionBase<StateClass, Serializable> {
    public abstract void userInteractionCanceled();

    public abstract void userInteractionFinished();

    @Override // com.eltechs.axs.configuration.startup.InteractiveStartupAction
    public final void userInteractionFinished(Serializable serializable) {
        Assert.state(serializable == null, "SimpleInteractiveStartupActions must receive only trivial responses from dialogs.");
        userInteractionFinished();
    }
}