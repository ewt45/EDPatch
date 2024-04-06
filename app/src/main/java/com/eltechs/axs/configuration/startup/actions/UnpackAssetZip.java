package com.eltechs.axs.configuration.startup.actions;

import android.content.Context;
import com.eltechs.axs.applicationState.ExagearImageAware;
import com.eltechs.axs.helpers.ZipInstallerAssets;

public class UnpackAssetZip<StateClass extends ExagearImageAware> extends AbstractStartupAction<StateClass> {
    private final Context applicationContext;
    private final String assetFileName;

    public UnpackAssetZip(Context context, String assetFileName) {
        this.applicationContext = context;
        this.assetFileName = assetFileName;
    }

    @Override // com.eltechs.axs.configuration.startup.StartupAction
    public void execute() {
        ZipInstallerAssets.installIfNecessary(this.applicationContext, new ZipInstallerAssets.InstallCallback() { // from class: com.eltechs.axs.configuration.startup.actions.UnpackAssetZip.1
            @Override // com.eltechs.axs.helpers.ZipInstallerAssets.InstallCallback
            public void installationFinished(String str) {
                sendDone();
            }

            @Override // com.eltechs.axs.helpers.ZipInstallerAssets.InstallCallback
            public void installationFailed(String str) {
                sendError(str);
            }
        }, getApplicationState().getExagearImage().getPath(), this.assetFileName);
    }
}