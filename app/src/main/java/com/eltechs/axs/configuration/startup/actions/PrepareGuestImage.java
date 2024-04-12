package com.eltechs.axs.configuration.startup.actions;

import com.eltechs.axs.ExagearImageConfiguration.ExagearImageConfigurationHelpers;
import com.eltechs.axs.applicationState.ExagearImageAware;
import com.eltechs.axs.environmentService.components.NativeLibsConfiguration;
import com.eltechs.axs.helpers.StringHelpers;
import java.io.File;
import java.io.IOException;

/* loaded from: classes.dex */
public class PrepareGuestImage<StateClass extends ExagearImageAware> extends AbstractStartupAction<StateClass> {
    /** "/home/xdroid/" */
    private final String homeDir;
    /** /storage/emulated/0/Exagear */
    private final File hostDirInUserArea;

    public PrepareGuestImage(String homeDir, File hostDirInUserArea) {
        this.homeDir = homeDir;
        this.hostDirInUserArea = hostDirInUserArea;
    }

    @Override // com.eltechs.axs.configuration.startup.StartupAction
    public void execute() {
        try {
            ExagearImageConfigurationHelpers helper = new ExagearImageConfigurationHelpers(((ExagearImageAware) getApplicationState()).getExagearImage());
            NativeLibsConfiguration nativeLibsConfiguration = new NativeLibsConfiguration(getAppContext());
            helper.createEtcPasswd(new File(this.homeDir).getName(), this.homeDir);
            helper.createVpathsList(StringHelpers.appendTrailingSlash(this.hostDirInUserArea.getAbsolutePath()), "/proc/", "/dev/");
            helper.recreateX11SocketDir();
            helper.recreateSoundSocketDir();
            helper.prepareWineForCurrentMemoryConfiguration(nativeLibsConfiguration);
            sendDone();
        } catch (IOException e) {
            sendError("Failed to prepare the unpacked exagear image for the game being started.", e);
        }
    }
}