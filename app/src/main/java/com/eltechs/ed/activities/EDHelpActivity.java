package com.eltechs.ed.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import com.eltechs.axs.activities.FrameworkActivity;
import com.eltechs.ed.R;
import com.eltechs.ed.fragments.help.HelpRootFragment;

/* loaded from: classes.dex */
public class EDHelpActivity extends FrameworkActivity {
    @Override // com.eltechs.axs.activities.AxsActivity, android.support.v7.app.AppCompatActivity, android.support.v4.app.FragmentActivity, android.support.v4.app.SupportActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.ex_help);
        setSupportActionBar((Toolbar) findViewById(R.id.ed_help_toolbar));
        ActionBar supportActionBar = getSupportActionBar();
        supportActionBar.setDisplayHomeAsUpEnabled(true);
        supportActionBar.setHomeAsUpIndicator(R.drawable.ic_close_24dp);
        if (bundle == null) {
            setHelpFragment(new HelpRootFragment());
        }
    }

    @Override // android.app.Activity
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == 16908332) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(menuItem);
    }

    public void setHelpFragment(Fragment fragment) {
        FragmentTransaction beginTransaction = getSupportFragmentManager().beginTransaction();
        beginTransaction.replace(R.id.ed_help_fragment_container, fragment);
        beginTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        if (!(fragment instanceof HelpRootFragment)) {
            beginTransaction.addToBackStack(null);
        }
        beginTransaction.commit();
    }
}