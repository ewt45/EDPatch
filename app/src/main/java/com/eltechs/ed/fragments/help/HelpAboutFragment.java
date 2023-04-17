package com.eltechs.ed.fragments.help;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.eltechs.ed.BuildConfig;
import com.eltechs.ed.R_original;


/* loaded from: classes.dex */
public class HelpAboutFragment extends Fragment {
    @Override // android.support.v4.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        return layoutInflater.inflate(R_original.layout.help_about, viewGroup, false);
    }

    @Override // android.support.v4.app.Fragment
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        ((TextView) getView().findViewById(R_original.id.version)).setText(BuildConfig.VERSION_NAME);
    }
}