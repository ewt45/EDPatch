package com.eltechs.axs.configuration.startup.actions;

import com.eltechs.axs.applicationState.ApplicationStateBase;
import com.eltechs.axs.environmentService.components.NativeLibsConfiguration;
import com.eltechs.ed.R;

/* loaded from: classes.dex */
public class CheckInstallLocation extends AbstractStartupAction<ApplicationStateBase<?>> {
    private native boolean isExecutablePermissionSet(String str);

    static {
        System.loadLibrary("ubt-helpers");
    }

    @Override // com.eltechs.axs.configuration.startup.StartupAction
    public void execute() {
        NativeLibsConfiguration nativeLibsConfiguration = new NativeLibsConfiguration(getAppContext());
        if (isExecutablePermissionSet(nativeLibsConfiguration.getLibubtPath()) && isExecutablePermissionSet(nativeLibsConfiguration.getLibubt2GPath()) && isExecutablePermissionSet(nativeLibsConfiguration.getKillswitchPath()) && isExecutablePermissionSet(nativeLibsConfiguration.getSysVIPCEmulatorPath()) && isExecutablePermissionSet(nativeLibsConfiguration.getIsMemSplit3g1gPath())) {
            sendDone();
        } else {
            sendError(getAppContext().getString(R.string.cil_installed_to_sdcard));
        }
    }
}