package com.eltechs.axs.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import com.eltechs.ed.R;

/* loaded from: classes.dex */
public class RateMeActivity extends FrameworkActivity {
    private static final int LAUNCHES_FOR_DIALOG = 5;
    private static final int DAYS_FOR_DIALOG = 7;

    @Override // com.eltechs.axs.activities.AxsActivity, android.support.v7.app.AppCompatActivity, android.support.v4.app.FragmentActivity, android.support.v4.app.SupportActivity, android.app.Activity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        requestWindowFeature(1);
        setResult(2);
        SharedPreferences sharedPreferences = getSharedPreferences("exadroid_rate", 0);
        SharedPreferences.Editor edit = sharedPreferences.edit();
        if (sharedPreferences.getBoolean("rate_clicked", false)) {
            finish();
            return;
        }
        long j = sharedPreferences.getLong("next_dialog_date", 0L);
        if (j == 0) {
            edit.putLong("next_dialog_date", System.currentTimeMillis());
        }
        int i = sharedPreferences.getInt("launch_count", 0);
        edit.putInt("launch_count", i + 1);
        edit.commit();
        if (i < LAUNCHES_FOR_DIALOG || j > System.currentTimeMillis()) {
            finish();
            return;
        }
        setContentView(R.layout.rate_me_activity);
        ((TextView) findViewById(R.id.rate_custom_title)).setText(getString(getApplicationInfo().labelRes));
    }

    public void onRateNowClicked(View view) {
        SharedPreferences.Editor edit = getSharedPreferences("exadroid_rate", 0).edit();
        edit.putBoolean("rate_clicked", true);
        edit.commit();
        startActivity(new Intent("android.intent.action.VIEW", Uri.parse("market://details?id=" + getPackageName())));
        finish();
    }

    public void onRateLaterClicked(View view) {
        SharedPreferences.Editor edit = getSharedPreferences("exadroid_rate", 0).edit();
        edit.putLong("next_dialog_date", System.currentTimeMillis() + 604800000);
        edit.commit();
        finish();
    }
}