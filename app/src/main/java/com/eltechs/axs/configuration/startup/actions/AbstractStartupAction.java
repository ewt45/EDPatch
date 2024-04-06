package com.eltechs.axs.configuration.startup.actions;

import android.content.Context;
import android.util.Log;

import com.eltechs.axs.Globals;
import com.eltechs.axs.configuration.startup.StartupAction;
import com.eltechs.axs.configuration.startup.StartupActionInfo;
import com.eltechs.axs.configuration.startup.StartupActionsCollection;
import com.eltechs.axs.helpers.Assert;
import java.io.PrintWriter;
import java.io.StringWriter;
import org.apache.commons.lang3.StringEscapeUtils;

/* loaded from: classes.dex */
public abstract class AbstractStartupAction<StateClass> implements StartupAction<StateClass> {
    private volatile StartupActionsCollection<StateClass> startupActions;

    @Override // com.eltechs.axs.configuration.startup.StartupAction
    public final void attach(StartupActionsCollection<StateClass> startupActionsCollection) {
        Assert.state(this.startupActions == null, "Already registered within a startup actions collection.");
        this.startupActions = startupActionsCollection;
    }

    @Override // com.eltechs.axs.configuration.startup.StartupAction
    public StartupActionInfo getInfo() {
        return new StartupActionInfo("");
    }

    protected StartupActionsCollection<StateClass> getStartupActions() {
        return this.startupActions;
    }

    protected StateClass getApplicationState() {
        return Globals.getApplicationState();
    }

    protected Context getAppContext() {
        return this.startupActions.getAndroidApplicationContext();
    }

    protected final String getString(int i) {
        return getAppContext().getString(i);
    }

    protected final void sendDone() {
        this.startupActions.actionDone(this);
    }

    protected final void sendError(String str) {
        sendErrorHtml(String.format("<html><body>%s</body></html>", StringEscapeUtils.escapeHtml4(str)));
    }

    protected final void sendError(String str, Throwable th) {
        StringWriter stringWriter = new StringWriter();
        th.printStackTrace(new PrintWriter(stringWriter));
        sendErrorHtml(String.format("<html><body>%s<br><br><pre>%s</pre></body></html>", StringEscapeUtils.escapeHtml4(str), StringEscapeUtils.escapeHtml4(stringWriter.toString())));
    }

    protected final void sendErrorHtml(String str) {
        this.startupActions.actionFailed(this, str);
    }

    public String toString() {
        return String.format("[Startup action: %s]", getClass().getSimpleName());
    }
}