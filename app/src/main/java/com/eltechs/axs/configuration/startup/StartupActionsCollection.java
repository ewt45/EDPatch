package com.eltechs.axs.configuration.startup;

import android.content.Context;
import android.util.Log;
import com.eltechs.axs.activities.BufferedListenerInvoker;
import com.eltechs.axs.activities.FrameworkActivity;
import com.eltechs.axs.activities.StartupActivity;
import com.eltechs.axs.configuration.startup.actions.StartupActionCompletionListener;
import com.eltechs.axs.configuration.startup.actions.StartupStepInfoListener;
import com.eltechs.axs.configuration.startup.actions.UserInteractionRequestListener;
import com.eltechs.axs.container.annotations.Autowired;
import com.eltechs.axs.helpers.Assert;
import com.eltechs.axs.helpers.UiThread;
import java.io.Serializable;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/* loaded from: classes.dex */
public class StartupActionsCollection<StateClass> {
    private static final String LOG_TAG = "StartupActions";
    private final Context applicationContext;
    private boolean isFinished;
    private final Deque<StartupAction> actions = new ArrayDeque();
    private ActionState currentActionState = ActionState.NOT_YET_STARTED;
    private final BufferedListenerInvoker<StartupActionCompletionListener> actionCompletionReporter = new BufferedListenerInvoker<>(StartupActionCompletionListener.class);
    private final BufferedListenerInvoker<UserInteractionRequestListener> userInteractionRequester = new BufferedListenerInvoker<>(UserInteractionRequestListener.class);
    private final BufferedListenerInvoker<StartupStepInfoListener> infoUpdater = new BufferedListenerInvoker<>(StartupStepInfoListener.class);
    private final Executor asyncStartupActionsExecutor = Executors.newSingleThreadExecutor();

    /* loaded from: classes.dex */
    private enum ActionState {
        NOT_YET_STARTED,
        RUNNING,
        AWAITING_RESPONSE
    }

    public StartupActionsCollection(Context context) {
        this.applicationContext = context;
    }

    @Autowired
    private void setStartupActivity(StartupActivity<?> startupActivity) {
        UiThread.check();
        this.actionCompletionReporter.setRealListener(startupActivity);
        this.userInteractionRequester.setRealListener(startupActivity);
        this.infoUpdater.setRealListener(startupActivity);
    }

    public void addAction(StartupAction<StateClass> startupAction) {
        UiThread.check();
        startupAction.attach(this);
        this.actions.addLast(startupAction);
    }

    public void addActions(List<StartupAction<StateClass>> list) {
        for (StartupAction<StateClass> startupAction : list) {
            addAction(startupAction);
        }
    }

    public boolean runAction() {
        UiThread.check();
        if (this.actions.isEmpty()) {
            logDebug("runAction() found no more startup actions\n", new Object[0]);
            this.isFinished = true;
            return false;
        } else if (this.currentActionState != ActionState.NOT_YET_STARTED) {
            Assert.state(false, String.format("runAction() called with the current action in invalid state %s.", this.currentActionState));
            return false;
        } else {
            this.currentActionState = ActionState.RUNNING;
            final StartupAction currentAction = getCurrentAction();
            setStepInfo(currentAction.getInfo());
            if (!isAsyncAction(currentAction)) {
                UiThread.post(new Runnable() { // from class: com.eltechs.axs.configuration.startup.StartupActionsCollection.1
                    @Override // java.lang.Runnable
                    public void run() {
                        currentAction.execute();
                    }
                });
            } else {
                this.asyncStartupActionsExecutor.execute(new Runnable() { // from class: com.eltechs.axs.configuration.startup.StartupActionsCollection.2
                    @Override // java.lang.Runnable
                    public void run() {
                        currentAction.execute();
                    }
                });
            }
            return true;
        }
    }

    public void actionDone(final StartupAction startupAction) {
        UiThread.post(new Runnable() { // from class: com.eltechs.axs.configuration.startup.StartupActionsCollection.3
            @Override // java.lang.Runnable
            public void run() {
                StartupActionsCollection.this.logDebug("actionDone(%s)\n", startupAction);
                Assert.state(StartupActionsCollection.this.currentActionState == ActionState.RUNNING, String.format("actionDone() called with the current action in invalid state %s.", StartupActionsCollection.this.currentActionState));
                Assert.state(startupAction == StartupActionsCollection.this.getCurrentAction(), "An invalid action has reported the completion of itself.");
                StartupActionsCollection.this.actions.removeFirst();
                StartupActionsCollection.this.currentActionState = ActionState.NOT_YET_STARTED;
                ((StartupActionCompletionListener) StartupActionsCollection.this.actionCompletionReporter.getProxy()).actionDone(startupAction);
            }
        });
    }

    public void actionFailed(final StartupAction startupAction, final String str) {
        UiThread.post(new Runnable() { // from class: com.eltechs.axs.configuration.startup.StartupActionsCollection.4
            @Override // java.lang.Runnable
            public void run() {
                StartupActionsCollection.this.logDebug("actionFailed(%s, '%s')\n", startupAction, str);
                Assert.state(StartupActionsCollection.this.currentActionState == ActionState.RUNNING, String.format("actionFailed() called with the current action in invalid state %s.", StartupActionsCollection.this.currentActionState));
                Assert.state(startupAction == StartupActionsCollection.this.getCurrentAction(), "An invalid action has reported a failure in itself.");
                StartupActionsCollection.this.actions.removeFirst();
                StartupActionsCollection.this.currentActionState = ActionState.NOT_YET_STARTED;
                ((StartupActionCompletionListener) StartupActionsCollection.this.actionCompletionReporter.getProxy()).actionFailed(startupAction, str);
            }
        });
    }

    public void userInteractionFinished(Serializable serializable) {
        UiThread.check();
        logDebug("userInteractionFinished()\n", new Object[0]);
        Assert.state(this.currentActionState == ActionState.AWAITING_RESPONSE, "userInteractionFinished() called but the current action expects no user input.");
        this.currentActionState = ActionState.RUNNING;
        Assert.state(getCurrentAction() instanceof InteractiveStartupAction, "Only interactive startup actions can receive nontrivial user responses.");
        ((InteractiveStartupAction) getCurrentAction()).userInteractionFinished(serializable);
    }

    public void userInteractionCanceled() {
        UiThread.check();
        logDebug("userInteractionCanceled()\n", new Object[0]);
        Assert.state(this.currentActionState == ActionState.AWAITING_RESPONSE, "userInteractionCanceled() called but the current action expects no user input.");
        this.currentActionState = ActionState.RUNNING;
        Assert.state(getCurrentAction() instanceof InteractiveStartupAction, "Only interactive startup actions can receive nontrivial user responses.");
        ((InteractiveStartupAction) getCurrentAction()).userInteractionCanceled();
    }

    public Context getAndroidApplicationContext() {
        return this.applicationContext;
    }

    public void requestUserInput(final Class<? extends FrameworkActivity> cls, final Serializable serializable) {
        UiThread.post(new Runnable() { // from class: com.eltechs.axs.configuration.startup.StartupActionsCollection.5
            @Override // java.lang.Runnable
            public void run() {
                Assert.state(StartupActionsCollection.this.currentActionState == ActionState.RUNNING, String.format("User input has been requested in state %s; can do it only in RUNNING state.", StartupActionsCollection.this.currentActionState));
                Assert.state(StartupActionsCollection.this.getCurrentAction() instanceof InteractiveStartupAction, "Only interactive startup actions can request user input.");
                StartupActionsCollection.this.currentActionState = ActionState.AWAITING_RESPONSE;
                ((UserInteractionRequestListener) StartupActionsCollection.this.userInteractionRequester.getProxy()).requestUserInput(cls, serializable);
            }
        });
    }

    public void requestUserInput() {
        UiThread.post(new Runnable() { // from class: com.eltechs.axs.configuration.startup.StartupActionsCollection.6
            @Override // java.lang.Runnable
            public void run() {
                Assert.state(StartupActionsCollection.this.currentActionState == ActionState.RUNNING, String.format("User input has been requested in state %s; can do it only in RUNNING state.", StartupActionsCollection.this.currentActionState));
                Assert.state(StartupActionsCollection.this.getCurrentAction() instanceof InteractiveStartupAction, "Only interactive startup actions can request user input.");
                StartupActionsCollection.this.currentActionState = ActionState.AWAITING_RESPONSE;
            }
        });
    }

    public boolean isWaitingForUserInput() {
        UiThread.check();
        return this.currentActionState == ActionState.AWAITING_RESPONSE;
    }

    public boolean isFinished() {
        UiThread.check();
        return this.isFinished;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public StartupAction getCurrentAction() {
        UiThread.check();
        return this.actions.getFirst();
    }

    private void setStepInfo(StartupActionInfo startupActionInfo) {
        this.infoUpdater.getProxy().setStepInfo(startupActionInfo);
    }

    private boolean isAsyncAction(StartupAction startupAction) {
        return startupAction instanceof AsyncStartupAction;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void logDebug(String str, Object... objArr) {
        Log.d(LOG_TAG, String.format(str, objArr));
    }
}