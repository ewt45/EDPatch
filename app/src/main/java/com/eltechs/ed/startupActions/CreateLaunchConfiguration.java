package com.eltechs.ed.startupActions;

import com.eltechs.axs.configuration.UBTLaunchConfiguration;
import com.eltechs.axs.configuration.startup.EnvironmentCustomisationParameters;
import com.eltechs.axs.configuration.startup.actions.AbstractStartupAction;
import com.eltechs.axs.helpers.SafeFileHelpers;
import com.eltechs.ed.EDApplicationState;
import java.io.File;
import java.util.EnumSet;

/* loaded from: classes.dex */
public class CreateLaunchConfiguration<StateClass extends EDApplicationState> extends AbstractStartupAction<StateClass> {
    final File applicationWorkingDir;
    private final String[] argv;
    private final String[] envp;
    private final String socketPathSuffix;
    private final String userAreaDir;
    private final String winePrefix;

    public CreateLaunchConfiguration(File file, String str, String[] strArr, String[] strArr2, String str2, String str3) {
        this.applicationWorkingDir = file;
        this.winePrefix = str;
        this.argv = strArr;
        this.envp = strArr2;
        this.socketPathSuffix = str2;
        this.userAreaDir = str3;
    }

    @Override // com.eltechs.axs.configuration.startup.StartupAction
    public void execute() {
        EDApplicationState eDApplicationState = (EDApplicationState) getApplicationState();
        EnvironmentCustomisationParameters environmentCustomisationParameters = eDApplicationState.getSelectedExecutableFile().getEnvironmentCustomisationParameters();
        UBTLaunchConfiguration uBTLaunchConfiguration = new UBTLaunchConfiguration();
        uBTLaunchConfiguration.setFsRoot(eDApplicationState.getExagearImage().getPath().getAbsolutePath());
        uBTLaunchConfiguration.setGuestExecutablePath(this.applicationWorkingDir.getAbsolutePath());
        uBTLaunchConfiguration.setGuestExecutable(this.argv[0]);
        uBTLaunchConfiguration.setGuestArguments(this.argv);
        uBTLaunchConfiguration.setGuestEnvironmentVariables(this.envp);
        uBTLaunchConfiguration.addEnvironmentVariable("LC_ALL", environmentCustomisationParameters.getLocaleName());
        uBTLaunchConfiguration.addArgumentsToEnvironment(eDApplicationState.getEnvironment());
        File imagePath = ((EDApplicationState) getApplicationState()).getExagearImage().getPath();
        SafeFileHelpers.symlink("../drive_c", new File(imagePath, this.winePrefix + "/dosdevices/c:").getAbsolutePath());
        //每次启动删除旧的链接路径
        File driveD = new File(imagePath,this.winePrefix + "/dosdevices/d:");
        driveD.delete();

        SafeFileHelpers.symlink(this.userAreaDir, driveD.getAbsolutePath());
        File path3 = ((EDApplicationState) getApplicationState()).getExagearImage().getPath();
        SafeFileHelpers.symlink("/tmp/", new File(path3, this.winePrefix + "/dosdevices/e:").getAbsolutePath());
        File path4 = ((EDApplicationState) getApplicationState()).getExagearImage().getPath();
        SafeFileHelpers.symlink("/", new File(path4, this.winePrefix + "/dosdevices/z:").getAbsolutePath());
        uBTLaunchConfiguration.setVfsHacks(EnumSet.of(UBTLaunchConfiguration.VFSHacks.TREAT_LSTAT_SOCKET_AS_STATTING_WINESERVER_SOCKET, UBTLaunchConfiguration.VFSHacks.TRUNCATE_STAT_INODE, UBTLaunchConfiguration.VFSHacks.SIMPLE_PASS_DEV));
        uBTLaunchConfiguration.setSocketPathSuffix(this.socketPathSuffix);
        eDApplicationState.setUBTLaunchConfiguration(uBTLaunchConfiguration);
        sendDone();
    }
}