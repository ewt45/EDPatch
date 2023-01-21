package com.eltechs.axs.configuration.startup;

import android.content.Context;

import com.eltechs.axs.activities.FrameworkActivity;
import com.eltechs.axs.configuration.startup.actions.AbstractStartupAction;
import com.eltechs.axs.helpers.UiThread;

import java.io.Serializable;
import java.util.ArrayDeque;
import java.util.Deque;

public class StartupActionsCollection<StateClass> {
    private final Deque<StartupAction> actions = new ArrayDeque();
    private final Context applicationContext;
    public StartupActionsCollection(Context context) {
        this.applicationContext = context;
    }


    public void requestUserInput(final Class<? extends FrameworkActivity> cls, final Serializable serializable) {
//        UiThread.post(new Runnable() { // from class: com.eltechs.axs.configuration.startup.StartupActionsCollection.5
//            @Override // java.lang.Runnable
//            public void run() {
//                Assert.state(StartupActionsCollection.this.currentActionState == ActionState.RUNNING, String.format("User input has been requested in state %s; can do it only in RUNNING state.", StartupActionsCollection.this.currentActionState));
//                Assert.state(StartupActionsCollection.this.getCurrentAction() instanceof InteractiveStartupAction, "Only interactive startup actions can request user input.");
//                StartupActionsCollection.this.currentActionState = ActionState.AWAITING_RESPONSE;
//                ((UserInteractionRequestListener) StartupActionsCollection.this.userInteractionRequester.getProxy()).requestUserInput(cls, serializable);
//            }
//        });
    }

    public void requestUserInput() {
//        UiThread.post(new Runnable() { // from class: com.eltechs.axs.configuration.startup.StartupActionsCollection.6
//            @Override // java.lang.Runnable
//            public void run() {
//                Assert.state(StartupActionsCollection.this.currentActionState == ActionState.RUNNING, String.format("User input has been requested in state %s; can do it only in RUNNING state.", StartupActionsCollection.this.currentActionState));
//                Assert.state(StartupActionsCollection.this.getCurrentAction() instanceof InteractiveStartupAction, "Only interactive startup actions can request user input.");
//                StartupActionsCollection.this.currentActionState = ActionState.AWAITING_RESPONSE;
//            }
//        });
    }


    public void actionDone(final StartupAction startupAction) {
//        UiThread.post(new Runnable() { // from class: com.eltechs.axs.configuration.startup.StartupActionsCollection.3
//            @Override // java.lang.Runnable
//            public void run() {
//                StartupActionsCollection.this.logDebug("actionDone(%s)\n", startupAction);
//                Assert.state(StartupActionsCollection.this.currentActionState == ActionState.RUNNING, String.format("actionDone() called with the current action in invalid state %s.", StartupActionsCollection.this.currentActionState));
//                Assert.state(startupAction == StartupActionsCollection.this.getCurrentAction(), "An invalid action has reported the completion of itself.");
//                StartupActionsCollection.this.actions.removeFirst();
//                StartupActionsCollection.this.currentActionState = ActionState.NOT_YET_STARTED;
//                ((StartupActionCompletionListener) StartupActionsCollection.this.actionCompletionReporter.getProxy()).actionDone(startupAction);
//            }
//        });
    }

    public void addAction(StartupAction<StateClass> startupAction) {
        UiThread.check();
        startupAction.attach(this);
        this.actions.addLast(startupAction);
    }


    public Context getAndroidApplicationContext() {
        return this.applicationContext;
    }

    public void actionFailed(final StartupAction startupAction, final String str) {
//        UiThread.post(new Runnable() { // from class: com.eltechs.axs.configuration.startup.StartupActionsCollection.4
//            @Override // java.lang.Runnable
//            public void run() {
//                StartupActionsCollection.this.logDebug("actionFailed(%s, '%s')\n", startupAction, str);
//                Assert.state(StartupActionsCollection.this.currentActionState == ActionState.RUNNING, String.format("actionFailed() called with the current action in invalid state %s.", StartupActionsCollection.this.currentActionState));
//                Assert.state(startupAction == StartupActionsCollection.this.getCurrentAction(), "An invalid action has reported a failure in itself.");
//                StartupActionsCollection.this.actions.removeFirst();
//                StartupActionsCollection.this.currentActionState = ActionState.NOT_YET_STARTED;
//                ((StartupActionCompletionListener) StartupActionsCollection.this.actionCompletionReporter.getProxy()).actionFailed(startupAction, str);
//            }
//        });
    }

}
