package com.eltechs.ed.startupActions;

import com.eltechs.axs.applicationState.ExagearImageAware;
import com.eltechs.axs.configuration.startup.actions.AbstractStartupAction;
import com.eltechs.axs.helpers.SafeFileHelpers;
import com.eltechs.axs.helpers.ZipInstallerAssets;
import com.eltechs.ed.guestContainers.GuestContainersManager;
import java.io.File;
import java.io.IOException;

/* loaded from: classes.dex */
public class InstallRecipesFromAssets<StateClass extends ExagearImageAware> extends AbstractStartupAction<StateClass> {
    @Override // com.eltechs.axs.configuration.startup.StartupAction
    public void execute() {
        File recipesGuestDir = new File(getApplicationState().getExagearImage().getPath(), GuestContainersManager.RECIPES_GUEST_DIR);
//        try {
            //这个注释掉，改成只解压一次吧？不然每次进入安卓界面就会清空一次
//            SafeFileHelpers.removeDirectory(recipesGuestDir);
            ZipInstallerAssets.installIfNecessary(getAppContext(), new ZipInstallerAssets.InstallCallback() { // from class: com.eltechs.ed.startupActions.InstallRecipesFromAssets.1
                @Override // com.eltechs.axs.helpers.ZipInstallerAssets.InstallCallback
                public void installationFinished(String str) {
                    InstallRecipesFromAssets.this.sendDone();
                }

                @Override // com.eltechs.axs.helpers.ZipInstallerAssets.InstallCallback
                public void installationFailed(String str) {
                    InstallRecipesFromAssets.this.sendError(str);
                }
            }, recipesGuestDir, "recipe-more.zip");



//        } catch (IOException e) {
//            sendError(e.toString());
//        }
    }
}