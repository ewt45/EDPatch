package com.eltechs.axs.activities;

import com.eltechs.axs.Globals;
import com.eltechs.axs.applicationState.ApplicationStateBase;

import java.io.Serializable;

public class FrameworkActivity<StateClass extends ApplicationStateBase> extends AxsActivity {


    public <T extends Serializable> void signalUserInteractionFinished(T t) {
//        setResultEx(2, t);
        finish();
    }

    public final StateClass getApplicationState() {
        return (StateClass) Globals.getApplicationState();
    }
}
