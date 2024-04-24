package com.eltechs.axs.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import com.eltechs.axs.AppConfig;
import com.eltechs.axs.firebase.FAHelper;

public class NotificationClickActivity extends AppCompatActivity {
    @Override // android.support.v7.app.AppCompatActivity, android.support.v4.app.FragmentActivity, android.support.v4.app.SupportActivity, android.app.Activity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        String stringExtra = getIntent().getStringExtra("NOTIFICATION_NAME");
        Log.i("Notification", "Click on notification " + stringExtra);
        FAHelper.logNotificationClickEvent(this, stringExtra);
        AppConfig.getInstance(this).setRunAfterNotification(true);
        AppConfig.getInstance(this).setNotificationName(stringExtra);
        Intent intent = getIntent().getParcelableExtra("INTENT");
        intent.putExtra("RUN_AFTER_NOTIFICATION", true);
        intent.putExtra("NOTIFICATION_NAME", stringExtra);
        finish();
        startActivity(intent);
    }
}
