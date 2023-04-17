package com.eltechs.axs.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.eltechs.axs.AppConfig;
import com.eltechs.axs.Globals;
import com.eltechs.ed.R;
import com.eltechs.axs.applicationState.ApplicationStateBase;
import com.eltechs.axs.applicationState.ApplicationStateObject;
import com.eltechs.axs.applicationState.DroidApplicationContextAware;
import com.eltechs.axs.applicationState.StartupActionsCollectionAware;
import com.eltechs.axs.configuration.startup.StartupAction;
import com.eltechs.axs.configuration.startup.StartupActionInfo;
import com.eltechs.axs.configuration.startup.StartupActionsCollection;
import com.eltechs.axs.configuration.startup.actions.StartupActionCompletionListener;
import com.eltechs.axs.configuration.startup.actions.StartupStepInfoListener;
import com.eltechs.axs.configuration.startup.actions.UserInteractionRequestListener;
import com.eltechs.axs.helpers.AndroidHelpers;
import com.eltechs.axs.helpers.Assert;
//import com.eltechs.axs.helpers.GAHelpers;
import com.eltechs.axs.helpers.UiThread;
import com.example.datainsert.exagear.obb.SelectObbFragment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.Calendar;

/* loaded from: classes.dex */
public abstract class StartupActivity<StateClass extends ApplicationStateBase<StateClass>> extends FrameworkActivity<StateClass> implements StartupActionCompletionListener, UserInteractionRequestListener, StartupStepInfoListener {
    public static final int REQUEST_CODE_GET_PERMISSIONS = 10002;
    public static final int REQUEST_CODE_GET_USER_INPUT = 10001;
    public static final int RESULT_CODE_GOT_USER_INPUT = 2;
    public static final String STATE_PROGRESS_DESCR = "PROGRESS_DESCR";
    public static final String STATE_PROGRESS_FILENAME = "PROGRESS_FILENAME";
    public static final String TAG = "StartupActivity";
    private static final String RESTART_AFTER_SHUTDOWN_FLAG_VALUE = "restart";
    private static final String SHUTDOWN_REQUEST_FLAG = "just die already";
    private static final long progressUpdateInterval = 200;
    private static boolean shutdownInProgress;
    private String progressDescription = null;
    private String progressFileName = null;
    private boolean isUpdateProgress = false;
    private Class<? extends FrameworkActivity> mainActivityClass = XServerDisplayActivity.class;

    /* JADX INFO: Access modifiers changed from: protected */
    protected StartupActivity(Class<StateClass> cls) {
        if (Globals.getApplicationState() == null) {
            Globals.setApplicationState(new ApplicationStateObject(cls));
        }
        enableLogging(true);
    }

    public static void shutdownAXSApplication() {
        shutdownAXSApplication(false);
    }

    @SuppressLint("WrongConstant")
    public static void shutdownAXSApplication(boolean z) {
        if (shutdownInProgress) {
            return;
        }
        ApplicationStateBase applicationStateBase = Globals.getApplicationState();
        boolean z2 = applicationStateBase.getCurrentActivity() != null;
        Context context = z2 ? applicationStateBase.getCurrentActivity() : applicationStateBase.getAndroidApplicationContext();
        Intent intent = new Intent(context, AndroidHelpers.getAppLaunchActivityClass(context));
        intent.setFlags(1149239296);
        if (!z2) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        intent.putExtra(SHUTDOWN_REQUEST_FLAG, z ? RESTART_AFTER_SHUTDOWN_FLAG_VALUE : "");
        context.startActivity(intent);
        shutdownInProgress = true;
    }

    @Override // android.app.Activity
    public void finish() {
    }

    protected abstract void initialiseStartupActions();

    protected void setMainActivityClass(Class<? extends FrameworkActivity> cls) {
        this.mainActivityClass = cls;
    }

    @Override
    // android.support.v7.app.AppCompatActivity, android.support.v4.app.FragmentActivity, android.support.v4.app.SupportActivity, android.app.Activity
    public void onSaveInstanceState(Bundle bundle) {
        bundle.putString(STATE_PROGRESS_DESCR, this.progressDescription);
        bundle.putString(STATE_PROGRESS_FILENAME, this.progressFileName);
        super.onSaveInstanceState(bundle);
    }

    @Override // android.app.Activity
    public void onRestoreInstanceState(Bundle bundle) {
        super.onRestoreInstanceState(bundle);
        this.progressDescription = bundle.getString(STATE_PROGRESS_DESCR);
        this.progressFileName = bundle.getString(STATE_PROGRESS_FILENAME);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* JADX WARN: Type inference failed for: r7v4, types: [com.eltechs.axs.applicationState.ApplicationStateBase] */
    @Override
    // com.eltechs.axs.activities.AxsActivity, android.support.v7.app.AppCompatActivity, android.support.v4.app.FragmentActivity, android.support.v4.app.SupportActivity, android.app.Activity
    protected final void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (maybeShutdown(getIntent())) {
            return;
        }
        if (!isTaskRoot()) {
            super.finish();
            return;
        }
        setupUI();
        ApplicationStateBase applicationState = getApplicationState();
        if (applicationState.getStartupActionsCollection() == null) {
            sendStatisticsForGA();
            ((DroidApplicationContextAware) applicationState).setAndroidApplicationContext(getApplicationContext());
            applicationState.setStartupActionsCollection(new StartupActionsCollection(getApplicationContext()));
            AppConfig appConfig = AppConfig.getInstance(this);
            if (appConfig.getFirstRunTime().getTime() == 0) {
                appConfig.setFirstRunTime(Calendar.getInstance().getTime());
            }
            boolean booleanExtra = getIntent().getBooleanExtra("RUN_AFTER_NOTIFICATION", false);
            appConfig.setRunAfterNotification(booleanExtra);
            appConfig.setNotificationName(booleanExtra ? getIntent().getStringExtra("NOTIFICATION_NAME") : null);
            initialiseStartupActions();
            moveToNextAction();
        } else if (applicationState.getStartupActionsCollection().isFinished()) {
            startupFinished(null);
        }
    }

    private void resetProgressToDefault() {
        ((ProgressBar) findViewById(R.id.startupProgressBar)).setIndeterminate(true);
        ((TextView) findViewById(R.id.sa_step_description)).setText(this.progressDescription);
    }

    private void updateProgressPost() {
        UiThread.postDelayed(progressUpdateInterval, new Runnable() { // from class: com.eltechs.axs.activities.StartupActivity.1
            @Override // java.lang.Runnable
            public void run() {
                StartupActivity.this.updateProgress();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    private void updateProgress() {
        int i;
        if (this.progressFileName == null) {
            resetProgressToDefault();
            this.isUpdateProgress = false;
        }
        if (this.isUpdateProgress) {
            File file = new File(this.progressFileName);
            if (!file.exists()) {
                updateProgressPost();
                return;
            }
            try {
                BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
                String readLine = bufferedReader.readLine();
                String readLine2 = bufferedReader.readLine();
                bufferedReader.close();
                try {
                    i = Math.min(Integer.parseInt(readLine), 100);
                } catch (Exception e) {
//                    e.printStackTrace();
                    i = -1;
                }
                ProgressBar progressBar = (ProgressBar) findViewById(R.id.startupProgressBar);
                if (i == -1) {
                    progressBar.setIndeterminate(true);
                } else {
                    progressBar.setIndeterminate(false);
                    progressBar.setProgress(i);
                }
                TextView textView = (TextView) findViewById(R.id.sa_step_description);
                if (readLine2 != null && !readLine2.equals("")) {
                    textView.setText(readLine2);
                } else {
                    textView.setText((CharSequence) null);
                }
            } catch (IOException e) {
//                e.printStackTrace();
            }
            updateProgressPost();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override
    // com.eltechs.axs.activities.FrameworkActivity, com.eltechs.axs.activities.AxsActivity, android.support.v4.app.FragmentActivity, android.app.Activity
    protected void onPause() {
        super.onPause();
        this.isUpdateProgress = false;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override
    // com.eltechs.axs.activities.FrameworkActivity, com.eltechs.axs.activities.AxsActivity, android.support.v4.app.FragmentActivity, android.app.Activity
    protected void onResume() {
        super.onResume();
        this.isUpdateProgress = true;
        updateProgressPost();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.support.v4.app.FragmentActivity, android.app.Activity
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        maybeShutdown(intent);
    }

    private boolean maybeShutdown(Intent intent) {
        String stringExtra = intent.getStringExtra(SHUTDOWN_REQUEST_FLAG);
        if (stringExtra != null) {
            logInfo("This is shutdown.");
            super.finish();
            Globals.clearState();
            shutdownInProgress = false;
            if (stringExtra.equals(RESTART_AFTER_SHUTDOWN_FLAG_VALUE)) {
                logInfo("Will restart after shutdown.");
                Intent intent2 = new Intent(this, AndroidHelpers.getAppLaunchActivityClass(this));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED | Intent.FLAG_ACTIVITY_CLEAR_TASK);//270565376

                startActivity(intent2);
                return true;
            }
            return true;
        }
        return false;
    }

    private void setupUI() {
        //R.layout.startup
        setContentView(R.layout.ed_startup);
    }

    private void sendStatisticsForGA() {
//        GAHelpers.GASendAndroidVersion(this);
//        GAHelpers.GASendDeviceInfo(this);
//        GAHelpers.GASendScreenParameters(this);
//        GAHelpers.GASendLinuxVersion(this);
//        GAHelpers.GASendReferrer(this);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SuppressLint("DefaultLocale")
    @Override
    // com.eltechs.axs.activities.FrameworkActivity, com.eltechs.axs.activities.AxsActivity, android.support.v4.app.FragmentActivity, android.app.Activity
    protected void onActivityResult(int i, int i2, Intent intent) {
        Log.d(TAG, "onActivityResult: 进入StartupActivity的onActivityResult");
        //fragment里发送的intent在这里接收到结果了，而且还有烦人的assert
        if (i != REQUEST_CODE_GET_USER_INPUT) {
            SelectObbFragment.receiveResultManually(this, i, i2, intent);
            return;
        }

        Assert.isTrue(i == REQUEST_CODE_GET_USER_INPUT, String.format("Received a response to an unknown request %d; can only issue REQUEST_CODE_GET_USER_INPUT.", Integer.valueOf(i)));
        Assert.isTrue(i2 == 0 || i2 == 2, String.format("Received an invalid resultCode %d.", i2));
        if (i2 == 2) {
            getStartupActions().userInteractionFinished(getResultEx(intent));
        } else {
            getStartupActions().userInteractionCanceled();
        }
    }

    @Override
    // android.support.v4.app.FragmentActivity, android.app.Activity, android.support.v4.app.ActivityCompat.OnRequestPermissionsResultCallback
    public void onRequestPermissionsResult(int i, String[] strArr, int[] iArr) {
        if (i == 10002) {
            getStartupActions().userInteractionFinished(null);
        }
    }

    /* JADX WARN: Type inference failed for: r0v0, types: [com.eltechs.axs.applicationState.ApplicationStateBase] */
    protected StartupActionsCollection getStartupActions() {
        return getApplicationState().getStartupActionsCollection();
    }

    @Override // com.eltechs.axs.configuration.startup.actions.UserInteractionRequestListener
    public void requestUserInput(Class<? extends FrameworkActivity> cls, Serializable serializable) {
        startActivityForResult(createIntent(this, cls, serializable), (int) REQUEST_CODE_GET_USER_INPUT);
    }

    @Override // com.eltechs.axs.configuration.startup.actions.StartupActionCompletionListener
    public void actionDone(StartupAction startupAction) {
        logDebug("actionDone(%s)", startupAction);
        moveToNextAction();
    }

    @Override // com.eltechs.axs.configuration.startup.actions.StartupActionCompletionListener
    public void actionFailed(StartupAction startupAction, String str) {
        logDebug("actionFailed(%s, '%s')", startupAction, str);
        FatalErrorActivity.showFatalError(str);
    }

    @Override // com.eltechs.axs.configuration.startup.actions.StartupStepInfoListener
    public void setStepInfo(StartupActionInfo startupActionInfo) {
        this.progressDescription = startupActionInfo.getStepDescription();
        this.progressFileName = startupActionInfo.getProgressFilename();
        if (this.progressFileName != null) {
            File file = new File(this.progressFileName);
            if (file.exists()) {
                file.delete();
            }
        }
        this.isUpdateProgress = true;
        updateProgress();
    }

    private void moveToNextAction() {
        setStepInfo(new StartupActionInfo(""));
        if (getStartupActions().runAction()) {
            return;
        }
        startupFinished(null);
    }

    protected void startupFinished(Class<?> cls) {
        Intent intent = new Intent(this, this.mainActivityClass);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        intent.putExtra("facadeclass", cls);
        startActivity(intent);
    }
}