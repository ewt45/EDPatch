package com.eltechs.axs.activities;

import android.os.Bundle;
import android.view.View;

import com.eltechs.ed.R;

/* loaded from: classes.dex */
public class CheckLicenseRetryActivity extends FrameworkActivity {
    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.eltechs.axs.activities.AxsActivity, android.support.v7.app.AppCompatActivity, android.support.v4.app.FragmentActivity, android.support.v4.app.SupportActivity, android.app.Activity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.check_license_retry);
    }

    public void onRetry(View view) {
        setResult(2);
        finish();
    }
}