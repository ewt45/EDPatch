package com.eltechs.axs.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.CountDownTimer;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import com.eltechs.axs.AppConfig;
import com.eltechs.axs.Globals;
import com.eltechs.axs.applicationState.ApplicationStateBase;
import com.eltechs.axs.helpers.AndroidHelpers;
import com.eltechs.axs.helpers.DateHelper;
import com.eltechs.axs.helpers.GAHelpers;
import java.io.Serializable;
import java.util.Calendar;

/* loaded from: classes.dex */
public class FrameworkActivity<StateClass extends ApplicationStateBase> extends AxsActivity {
    private static final String EXTRA_PARAMETER_NAME = "ExtraParameter";
    private static final int GA_KEEPALIVE_DELAY = 30000;
    private CountDownTimer gaKeepaliveTimer = null;

    @Override // com.eltechs.axs.activities.AxsActivity, android.support.v7.app.AppCompatActivity, android.support.v4.app.FragmentActivity, android.app.Activity
    protected void onStart() {
        super.onStart();
        GAHelpers.GAStart(this);
        if (this.gaKeepaliveTimer == null) {
            this.gaKeepaliveTimer = new CountDownTimer(DateHelper.MSEC_IN_DAY, 30000L) { // from class: com.eltechs.axs.activities.FrameworkActivity.1
                @Override // android.os.CountDownTimer
                public void onTick(long j) {
                    if (FrameworkActivity.this.isActivityResumed()) {
                        GAHelpers.GASendKeepalive(FrameworkActivity.this);
                        AppConfig.getInstance(FrameworkActivity.this.getApplicationContext()).setLastSessionTime(Calendar.getInstance().getTime());
                    }
                }

                @Override // android.os.CountDownTimer
                public void onFinish() {
                    FrameworkActivity.this.gaKeepaliveTimer.start();
                }
            };
            this.gaKeepaliveTimer.start();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.eltechs.axs.activities.AxsActivity, android.support.v4.app.FragmentActivity, android.app.Activity
    protected void onResume() {
        super.onResume();
        getApplicationState().setCurrentActivity(this);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.eltechs.axs.activities.AxsActivity, android.support.v4.app.FragmentActivity, android.app.Activity
    protected void onPause() {
        super.onPause();
        StateClass applicationState = getApplicationState();
        if (applicationState != null) {
            applicationState.setCurrentActivity(null);
        }
    }

    @Override // com.eltechs.axs.activities.AxsActivity, android.support.v7.app.AppCompatActivity, android.support.v4.app.FragmentActivity, android.app.Activity
    protected void onStop() {
        super.onStop();
        GAHelpers.GAStop(this);
        if (this.gaKeepaliveTimer != null) {
            this.gaKeepaliveTimer.cancel();
            this.gaKeepaliveTimer = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.eltechs.axs.activities.AxsActivity, android.support.v4.app.FragmentActivity, android.app.Activity
    protected void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    protected final StateClass getApplicationState() {
        return (StateClass) Globals.getApplicationState();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    protected static <T extends Serializable> void writeExtraParameter(Intent intent, T t) {
        intent.putExtra(EXTRA_PARAMETER_NAME, t);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    protected final <T extends Serializable> T getExtraParameter() {
        return (T) getIntent().getSerializableExtra(EXTRA_PARAMETER_NAME);
    }

    public static Intent createIntent(FrameworkActivity frameworkActivity, Class<? extends FrameworkActivity> cls, Serializable serializable) {
        Intent intent = new Intent(frameworkActivity, cls);
        if (serializable != null) {
            writeExtraParameter(intent, serializable);
        }
        return intent;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void startActivity(Class<? extends FrameworkActivity> cls) {
        startActivity(createIntent(this, cls, null));
    }

    protected void startActivity(Class<? extends FrameworkActivity> cls, Serializable serializable) {
        startActivity(createIntent(this, cls, serializable));
    }

    /* JADX INFO: Access modifiers changed from: protected */
    protected void startActivityForResult(int i, Class<? extends FrameworkActivity> cls) {
        startActivityForResult(i, cls, (Serializable) null);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    protected void startActivityForResult(int i, Class<? extends FrameworkActivity> cls, Serializable serializable) {
        startActivityForResult(createIntent(this, cls, serializable), i);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SuppressLint("ResourceType")
    protected void resizeRootViewToStandardDialogueSize() {
        resizeViewToFractionOfScreenSize(getWindow().getDecorView().findViewById(16908290), 80, 80);
    }

    protected void resizeViewToFractionOfScreenSize(View view, int i, int i2) {
        DisplayMetrics displayMetrics = AndroidHelpers.getDisplayMetrics();
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.width = (displayMetrics.widthPixels * i) / 100;
        layoutParams.height = (displayMetrics.heightPixels * i2) / 100;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    protected void signalUserInteractionFinished() {
        signalUserInteractionFinished(null);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    protected <T extends Serializable> void signalUserInteractionFinished(T t) {
        setResultEx(2, t);
        finish();
    }
}