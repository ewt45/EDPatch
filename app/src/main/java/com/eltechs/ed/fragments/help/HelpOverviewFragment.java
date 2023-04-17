package com.eltechs.ed.fragments.help;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.eltechs.ed.R_original;

/* loaded from: classes.dex */
public class HelpOverviewFragment extends Fragment {
    @Override // android.support.v4.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        return layoutInflater.inflate(R_original.layout.help_overview, viewGroup, false);
    }

    @Override // android.support.v4.app.Fragment
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        ((TextView) getView().findViewById(R_original.id.text)).setText(Html.fromHtml(getResources().getString(R_original.string.help_overview)));
    }
}