package com.eltechs.axs.activities;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import java.util.HashMap;

/* loaded from: classes.dex */
public class AxsDataFragment extends Fragment {
    HashMap<String, DialogInfo> tag2dialogInfo = new HashMap<>();

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static class DialogInfo {
        Dialog dialog;
        boolean isShown;
    }

    @Override // android.support.v4.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setRetainInstance(true);
    }
}