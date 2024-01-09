package com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.edit;

import android.content.Context;
import android.widget.LinearLayout;

public class ProfilesView extends LinearLayout {
    EditMain mHost;
    public ProfilesView(EditMain host) {
        super(host.getContext());
        mHost = host;
        Context c = host.getContext();
        setOrientation(VERTICAL);
    }
}
