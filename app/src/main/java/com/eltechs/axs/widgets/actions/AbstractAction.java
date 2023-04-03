package com.eltechs.axs.widgets.actions;

import com.eltechs.axs.Globals;
import com.eltechs.axs.activities.FrameworkActivity;
import com.eltechs.axs.activities.XServerDisplayActivity;
import com.eltechs.axs.applicationState.ApplicationStateBase;
import com.eltechs.axs.helpers.Assert;

public abstract class AbstractAction implements Action {
    protected static final boolean CHECKABLE = true;
    protected static final boolean NOT_CHECKABLE = false;
    private final boolean checkable;
    private boolean enabled;
    private String name;

    /* JADX INFO: Access modifiers changed from: protected */
    protected AbstractAction(String str) {
        this(str, false);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    protected AbstractAction(String str, boolean z) {
        this.name = str;
        this.enabled = true;
        this.checkable = z;
    }

    @Override // com.eltechs.axs.widgets.actions.Action
    public String getName() {
        return this.name;
    }

    protected void setName(String str) {
        this.name = str;
    }

    @Override // com.eltechs.axs.widgets.actions.Action
    public boolean isEnabled() {
        return this.enabled;
    }

    protected void setEnabled(boolean z) {
        this.enabled = z;
    }

    @Override // com.eltechs.axs.widgets.actions.Action
    public final boolean isCheckable() {
        return this.checkable;
    }

    @Override // com.eltechs.axs.widgets.actions.Action
    public boolean isChecked() {
        Assert.state(!this.checkable, "Checkable Actions must implement isChecked().");
        return false;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    protected final XServerDisplayActivity getCurrentXServerDisplayActivity() {
        FrameworkActivity currentActivity = ((ApplicationStateBase) Globals.getApplicationState()).getCurrentActivity();
        Assert.state(currentActivity instanceof XServerDisplayActivity, String.format("A menu was requested by %s which is not a XServerDisplayActivity.", currentActivity));

        return (XServerDisplayActivity) currentActivity;
    }
}
