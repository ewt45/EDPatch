package com.eltechs.axs.activities;

import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.TextView;
import com.eltechs.ed.R;
import com.eltechs.axs.applicationState.ApplicationStateBase;

/* loaded from: classes.dex */
public class WarningActivity extends FrameworkActivity<ApplicationStateBase<?>> {
    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.eltechs.axs.activities.AxsActivity, android.support.v7.app.AppCompatActivity, android.support.v4.app.FragmentActivity, android.support.v4.app.SupportActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.warning);
        ((TextView) findViewById(R.id.warn_text_view)).setText(Html.fromHtml((String) getExtraParameter()));
    }

    public void onOKClicked(View view) {
        finish();
    }
}