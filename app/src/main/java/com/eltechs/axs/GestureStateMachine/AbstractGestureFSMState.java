package com.eltechs.axs.GestureStateMachine;

import com.eltechs.axs.finiteStateMachine.AbstractFSMState;

/* loaded from: classes.dex */
public abstract class AbstractGestureFSMState extends AbstractFSMState {
    private final GestureContext context;

    public AbstractGestureFSMState(GestureContext gestureContext) {
        this.context = gestureContext;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    protected GestureContext getContext() {
        return this.context;
    }
}
