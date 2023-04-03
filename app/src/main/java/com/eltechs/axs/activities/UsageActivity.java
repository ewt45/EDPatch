package com.eltechs.axs.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.eltechs.axs.applicationState.ApplicationStateBase;

public class UsageActivity extends FrameworkActivity<ApplicationStateBase> {
    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.eltechs.axs.activities.AxsActivity, android.support.v7.app.AppCompatActivity, android.support.v4.app.FragmentActivity, android.support.v4.app.SupportActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        requestWindowFeature(1);
        setResult(2);
        setContentView(com.eltechs.axs.R.layout.tutorial_dialog);
        ((ImageView) findViewById(com.eltechs.axs.R.id.tutorial_pic)).setImageResource(((Integer) getExtraParameter()).intValue());
    }



    public void onOKClicked(View view) {
        finish();
    }
}
