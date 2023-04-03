package com.eltechs.axs.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;
import com.eltechs.axs.Globals;
import com.eltechs.axs.R;
import com.eltechs.axs.applicationState.ApplicationStateBase;

/* loaded from: classes.dex */
public class FatalErrorActivity extends FrameworkActivity<ApplicationStateBase<?>> {
    public static void showFatalError(String str) {
        Context androidApplicationContext;
        ApplicationStateBase applicationStateBase = (ApplicationStateBase) Globals.getApplicationState();
        boolean z = applicationStateBase.getCurrentActivity() != null;
        if (z) {
            androidApplicationContext = applicationStateBase.getCurrentActivity();
        } else {
            androidApplicationContext = applicationStateBase.getAndroidApplicationContext();
        }
        Intent intent = new Intent(androidApplicationContext, FatalErrorActivity.class);
        intent.setFlags(z ? 0 : Intent.FLAG_ACTIVITY_NEW_TASK);
        writeExtraParameter(intent, str);
        androidApplicationContext.startActivity(intent);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.eltechs.axs.activities.AxsActivity, android.support.v7.app.AppCompatActivity, android.support.v4.app.FragmentActivity, android.support.v4.app.SupportActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(com.ewt45.exagearsupportv7.R.layout.ed_fatal_error);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) TextView textView = (TextView) findViewById(com.ewt45.exagearsupportv7.R.id.fe_text_view);
        textView.setText(Html.fromHtml((String) getExtraParameter()));
        textView.setMovementMethod(LinkMovementMethod.getInstance());
    }

    @Override // android.app.Activity
    public void finish() {
        super.finish();
        StartupActivity.shutdownAXSApplication();
    }
}