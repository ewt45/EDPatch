package com.example.datainsert.exagear.test;

import android.util.Log;

import com.eltechs.axs.applicationState.ExagearImageAware;
import com.eltechs.axs.configuration.startup.actions.AbstractStartupAction;

public class DefaultAction <StateClass extends ExagearImageAware> extends AbstractStartupAction<StateClass> {
    @Override
    public void execute() {
        Log.d("", "execute: 进入DefaultAction");
    }
}
