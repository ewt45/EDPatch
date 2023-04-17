package com.eltechs.ed.fragments.help;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.eltechs.ed.R_original;
import com.eltechs.ed.activities.EDHelpActivity;

/* loaded from: classes.dex */
public class HelpRootFragment extends Fragment {
    @Override // android.support.v4.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        return layoutInflater.inflate(R_original.layout.help_root, viewGroup, false);
    }

    @Override // android.support.v4.app.Fragment
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        getView().findViewById(R_original.id.overview).setOnClickListener(new View.OnClickListener() { // from class: com.eltechs.ed.fragments.help.HelpRootFragment.1


            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                ((EDHelpActivity) HelpRootFragment.this.getActivity()).setHelpFragment(new HelpOverviewFragment());
            }
        });
        getView().findViewById(R_original.id.controls).setOnClickListener(new View.OnClickListener() { // from class: com.eltechs.ed.fragments.help.HelpRootFragment.2


            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                ((EDHelpActivity) HelpRootFragment.this.getActivity()).setHelpFragment(new HelpControlsFragment());
            }
        });
        getView().findViewById(R_original.id.about).setOnClickListener(new View.OnClickListener() { // from class: com.eltechs.ed.fragments.help.HelpRootFragment.3


            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                ((EDHelpActivity) HelpRootFragment.this.getActivity()).setHelpFragment(new HelpAboutFragment());
            }
        });
    }
}