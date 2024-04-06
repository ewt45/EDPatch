package com.eltechs.axs.activities;

import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;
import com.eltechs.ed.R;
import com.eltechs.axs.applicationState.ApplicationStateBase;
import com.eltechs.axs.applicationState.UserApplicationsDirectoryNameAware;

/* loaded from: classes.dex */
public class AddGameWizard<StateClass extends ApplicationStateBase<StateClass> & UserApplicationsDirectoryNameAware> extends FrameworkActivity<StateClass> {
    @Override // com.eltechs.axs.activities.AxsActivity, android.support.v7.app.AppCompatActivity, android.support.v4.app.FragmentActivity, android.support.v4.app.SupportActivity, android.app.Activity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        requestWindowFeature(1);
        setContentView(R.layout.add_game_wizard);
        resizeRootViewToStandardDialogueSize();
        Integer num = getExtraParameter();
        if (num == null) {
            num = R.string.agw_basic_instruction;
        }
        String string = getResources().getString(num);
        TextView textView = findViewById(R.id.agw_text_view);
        textView.setText(Html.fromHtml(string));
        textView.setMovementMethod(LinkMovementMethod.getInstance());
    }
}