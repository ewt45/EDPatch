package com.eltechs.axs.fragments;

import android.support.v4.app.Fragment;
import com.eltechs.axs.Globals;
import com.eltechs.axs.applicationState.ApplicationStateBase;

/* loaded from: classes.dex */
public class FrameworkFragment<StateClass extends ApplicationStateBase<StateClass>> extends Fragment {
    protected final StateClass getApplicationState() {
        return Globals.getApplicationState();
    }
}