package com.eltechs.axs.configuration.startup.actions;

import com.eltechs.axs.ExagearImageConfiguration.ExagearImageConfigurationHelpers;
import com.eltechs.axs.applicationState.EnvironmentAware;
import com.eltechs.axs.applicationState.ExagearImageAware;
import com.eltechs.axs.applicationState.SelectedExecutableFileAware;
import com.eltechs.axs.applicationState.UBTLaunchConfigurationAware;
import com.eltechs.axs.configuration.UBTLaunchConfiguration;
import com.eltechs.axs.configuration.startup.EnvironmentCustomisationParameters;
import com.eltechs.axs.helpers.FileHelpers;
import com.eltechs.axs.proto.output.replies.Str;

import java.io.File;
import java.io.IOException;
import java.util.EnumSet;
import java.util.LinkedHashMap;

/* loaded from: classes.dex */
public class CreateTypicalWineLaunchConfiguration<StateClass extends EnvironmentAware & ExagearImageAware & UBTLaunchConfigurationAware & SelectedExecutableFileAware<StateClass>> extends AbstractStartupAction<StateClass> {
    private final boolean additionalDiskPointsToGameDir;
    private final String[] argv;
    private final String[] envp;
    private final String homeDir;
    private final boolean putAdditionalDisks;
    private final String socketPathSuffix;

    public CreateTypicalWineLaunchConfiguration(String str, String[] strArr, String[] strArr2, String str2, boolean z, boolean z2) {
        this.homeDir = str;
        this.argv = strArr;
        this.envp = strArr2;
        this.socketPathSuffix = str2;
        this.putAdditionalDisks = z;
        this.additionalDiskPointsToGameDir = z2;
    }

    @Override // com.eltechs.axs.configuration.startup.StartupAction
    public void execute() {
        File parentDir = getApplicationState().getSelectedExecutableFile().getParentDir();
        EnvironmentCustomisationParameters environmentCustomisationParameters = getApplicationState().getSelectedExecutableFile().getEnvironmentCustomisationParameters();
        String exagearRootFromPath = FileHelpers.getExagearRootFromPath(parentDir);
        UBTLaunchConfiguration uBTLaunchConfiguration = new UBTLaunchConfiguration();
        uBTLaunchConfiguration.setFsRoot(getApplicationState().getExagearImage().getPath().getAbsolutePath());
        uBTLaunchConfiguration.setGuestExecutablePath(parentDir.getAbsolutePath());
        uBTLaunchConfiguration.setGuestExecutable("/usr/bin/wine");
        uBTLaunchConfiguration.setGuestArguments(this.argv);
        uBTLaunchConfiguration.setGuestEnvironmentVariables(this.envp);
        uBTLaunchConfiguration.addEnvironmentVariable("LC_ALL", environmentCustomisationParameters.getLocaleName());
        uBTLaunchConfiguration.addArgumentsToEnvironment(getApplicationState().getEnvironment());
        uBTLaunchConfiguration.addEnvironmentVariable("EXADROID_DISABLE_SHORT_NAMES", "y");
        try {
            if (FileHelpers.checkCaseInsensitivityInDirectory(new File(exagearRootFromPath))) {
                uBTLaunchConfiguration.addEnvironmentVariable("EXADROID_FS_CASE_INSENSITIVE", "y");
            }
            String absolutePath = parentDir.getAbsolutePath();
            LinkedHashMap<String, String> linkedHashMap = new LinkedHashMap<>();
            try {
                ExagearImageConfigurationHelpers helper = new ExagearImageConfigurationHelpers(getApplicationState().getExagearImage());
                String winePrefix = this.homeDir + ".wine/";
                linkedHashMap.put(winePrefix + "dosdevices/c_", winePrefix + "drive_c");
                linkedHashMap.put(winePrefix + "dosdevices/d_", exagearRootFromPath);
                helper.createFakeSymlink(winePrefix + "dosdevices", "d_", exagearRootFromPath);
                if (this.putAdditionalDisks) {
                    if (this.additionalDiskPointsToGameDir) {
                        linkedHashMap.put(winePrefix + "dosdevices/e_", absolutePath);
                        helper.createFakeSymlink(winePrefix + "dosdevices", "e_", absolutePath);
                    } else {
                        linkedHashMap.put(winePrefix + "dosdevices/e_", exagearRootFromPath);
                        helper.createFakeSymlink(winePrefix + "dosdevices", "e_", exagearRootFromPath);
                    }
                }
                uBTLaunchConfiguration.setFileNameReplacements(linkedHashMap);
                uBTLaunchConfiguration.setVfsHacks(EnumSet.of(UBTLaunchConfiguration.VFSHacks.ASSUME_NO_SYMLINKS_EXCEPT_PRERESOLVED, UBTLaunchConfiguration.VFSHacks.TREAT_LSTAT_SOCKET_AS_STATTING_WINESERVER_SOCKET));
                uBTLaunchConfiguration.setSocketPathSuffix(this.socketPathSuffix);
                getApplicationState().setUBTLaunchConfiguration(uBTLaunchConfiguration);
                sendDone();
            } catch (IOException e) {
                sendError("Failed to create fake symlink.", e);
            }
        } catch (IOException e2) {
            sendError("Failed to check exagear directory properties.", e2);
        }
    }
}