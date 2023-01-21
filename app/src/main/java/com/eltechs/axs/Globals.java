package com.eltechs.axs;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;

import com.eltechs.axs.activities.FrameworkActivity;
import com.eltechs.axs.applicationState.ApplicationStateBase;
import com.eltechs.axs.applicationState.ApplicationStateObject;
import com.eltechs.axs.helpers.Assert;

/**
 * 仿照ex写一个可以全局静态调用context的类
 */
public abstract class Globals {
    static Context  mAppContext;
    private static ApplicationStateObject applicationState;
    private static FrameworkActivity frameworkActivity;
    private Globals() {
    }
    public static void setAppContext(Context c){
        mAppContext = c;
    }
    public static Context getAppContext() {
        return mAppContext;
    }




    public static <T extends ApplicationStateBase> void setApplicationState(ApplicationStateObject<T> applicationStateObject) {
        Assert.state(applicationState == null, "Application state object already created.");
        applicationState = applicationStateObject;
    }

    public static <T> T getApplicationState() {
        if (applicationState != null) {
            return (T) applicationState.getState();
        }
        return null;
    }

    public static FrameworkActivity getFrameworkActivity() {
        return frameworkActivity;
    }

    public static void setFrameworkActivity(FrameworkActivity frameworkActivity) {
        Globals.frameworkActivity = frameworkActivity;
    }
}
