package com.eltechs.axs.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import com.eltechs.axs.helpers.AndroidHelpers;

/* loaded from: classes.dex */
public class SwitchToAxsFromSystemTrayActivity extends AxsActivity {
    /* JADX INFO: Access modifiers changed from: protected */
    @SuppressLint("WrongConstant")
    @Override // com.eltechs.axs.activities.AxsActivity, android.support.v7.app.AppCompatActivity, android.support.v4.app.FragmentActivity, android.support.v4.app.SupportActivity, android.app.Activity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (!isTaskRoot()) {
            finish();
            return;
        }
        Intent intent = new Intent(this, AndroidHelpers.getAppLaunchActivityClass(this));
        intent.putExtras(getIntent());
        intent.setFlags(268468224);
        finish();
        startActivity(intent);
    }
}