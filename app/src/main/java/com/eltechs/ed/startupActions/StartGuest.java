package com.eltechs.ed.startupActions;

import android.annotation.SuppressLint;
import android.util.Log;
import com.eltechs.axs.applicationState.ApplicationStateBase;
import com.eltechs.axs.applicationState.SelectedExecutableFileAware;
import com.eltechs.axs.applicationState.XServerDisplayActivityConfigurationAware;
import com.eltechs.axs.configuration.startup.AsyncStartupAction;
import com.eltechs.axs.configuration.startup.DetectedExecutableFile;
import com.eltechs.axs.configuration.startup.EnvironmentCustomisationParameters;
import com.eltechs.axs.configuration.startup.actions.AbstractStartupAction;
import com.eltechs.axs.configuration.startup.actions.CreateTypicalEnvironmentConfiguration;
import com.eltechs.axs.configuration.startup.actions.PrepareGuestImage;
import com.eltechs.axs.configuration.startup.actions.StartEnvironmentService;
import com.eltechs.axs.configuration.startup.actions.WaitForXClientConnection;
import com.eltechs.axs.environmentService.StartGuestApplication;
import com.eltechs.axs.environmentService.TrayConfiguration;
import com.eltechs.axs.helpers.AndroidHelpers;
import com.eltechs.axs.helpers.FileHelpers;
import com.eltechs.axs.helpers.UiThread;
import com.eltechs.axs.xserver.ScreenInfo;
import com.eltechs.ed.ContainerPackage;
import com.eltechs.ed.InstallRecipe;
import com.eltechs.ed.XDGLink;
import com.eltechs.ed.activities.EDStartupActivity;
import com.eltechs.ed.controls.Controls;
import com.eltechs.ed.guestContainers.GuestContainer;
import com.eltechs.ed.guestContainers.GuestContainerConfig;
import com.eltechs.ed.guestContainers.GuestContainersManager;
import com.eltechs.ed.R;
import com.example.datainsert.exagear.FAB.dialogfragment.DriveD;
import com.example.datainsert.exagear.action.AddEnvironmentVariables;
import com.example.datainsert.exagear.mutiWine.MutiWine;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.io.FileUtils;

/* loaded from: classes.dex */
public class StartGuest<StateClass extends ApplicationStateBase<StateClass> & SelectedExecutableFileAware<StateClass> & XServerDisplayActivityConfigurationAware> extends AbstractStartupAction<StateClass> implements AsyncStartupAction<StateClass> {
    private static final String TAG = "StartGuest";
    private static final File mUserAreaDir = DriveD.getDriveDDir();
    private GuestContainer mCont;
    private String mContStartupActions;
    private Controls mControls;
    private List<String> mEnv;
    private List<String> mExeArgv;
    private File mExeWorkDir;
    private boolean mForceUseDefaultContols;
    private boolean mForceUseDefaultResolution;
    private final GuestContainersManager mGcm;
    private boolean mHideTaskbar;
    private boolean mHideXServerImage;
    private String mLocaleName;
    private String mRunArguments;
    private String mRunGuide;
    private File mRunScriptToCopy;
    private ScreenInfo mScreenInfo;

    /* loaded from: classes.dex */
    public static class InstallApp {
        private GuestContainer mCont;
        private String mExePath;
        private InstallRecipe mRecipe;

        public InstallApp(GuestContainer guestContainer, String str, InstallRecipe installRecipe) {
            this.mCont = guestContainer;
            this.mExePath = str;
            this.mRecipe = installRecipe;
        }
    }

    /* loaded from: classes.dex */
    public static class RunXDGLink {
        private XDGLink mLink;

        public RunXDGLink(XDGLink xDGLink) {
            this.mLink = xDGLink;
        }
    }

    /* loaded from: classes.dex */
    public static class RunExplorer {
        private GuestContainer mCont;

        public RunExplorer(GuestContainer guestContainer) {
            this.mCont = guestContainer;
        }
    }

    /* loaded from: classes.dex */
    public static class InstallPackage {
        private GuestContainer mCont;
        private List<ContainerPackage> mPackages;

        public InstallPackage(GuestContainer guestContainer, List<ContainerPackage> list) {
            this.mCont = guestContainer;
            this.mPackages = list;
        }
    }

    public StartGuest(InstallApp installApp) {
        this.mGcm = GuestContainersManager.getInstance(((ApplicationStateBase) getApplicationState()).getAndroidApplicationContext());
        this.mExeArgv = new ArrayList<>();
        this.mEnv = new ArrayList<>();
        this.mHideXServerImage = false;
        this.mForceUseDefaultContols = false;
        this.mForceUseDefaultResolution = false;
        this.mHideTaskbar = false;
        InstallRecipe installRecipe = installApp.mRecipe;
        String str = installApp.mExePath;
        this.mCont = installApp.mCont;
        this.mScreenInfo = installRecipe.mScreenInfo;
        this.mControls = installRecipe.mControls;
        this.mLocaleName = installRecipe.mLocaleName;
        this.mRunArguments = installRecipe.mRunArguments;
        this.mContStartupActions = installRecipe.mStartupActions;
        this.mRunGuide = installRecipe.mRunGuide;
        this.mRunScriptToCopy = new File(this.mGcm.getGuestImagePath(), getRecipeGuestPath(installRecipe.mRunScriptName));
        this.mExeWorkDir = new File(str).getParentFile();
        this.mExeArgv.addAll(Arrays.asList("/bin/bash", "-x", getRecipeGuestPath(installRecipe.mInstallScriptName), "eval \"wine '" + (this.mGcm.getGuestPath(this.mGcm.getGuestWinePrefixPath()) + "/dosdevices/d:" + FileHelpers.cutRootPrefixFromPath(new File(installApp.mExePath), mUserAreaDir)) + "'\""));
        if (this.mCont == null || this.mCont.mConfig.getDefaultControlsNotShortcut()) {
            this.mForceUseDefaultContols = true;
        }
        if (this.mCont == null || this.mCont.mConfig.getDefaultResolutionNotShortcut()) {
            this.mForceUseDefaultResolution = true;
        }
    }

    public StartGuest(RunXDGLink runXDGLink) {
        this.mGcm = GuestContainersManager.getInstance(((ApplicationStateBase) getApplicationState()).getAndroidApplicationContext());
        this.mExeArgv = new ArrayList();
        this.mEnv = new ArrayList();
        this.mHideXServerImage = false;
        this.mForceUseDefaultContols = false;
        this.mForceUseDefaultResolution = false;
        this.mHideTaskbar = false;
        XDGLink xDGLink = runXDGLink.mLink;
        this.mCont = xDGLink.guestCont;
        this.mExeWorkDir = new File(xDGLink.path == null ? mUserAreaDir.getAbsolutePath() : this.mGcm.getHostPath(xDGLink.path));
        this.mExeArgv.addAll(Arrays.asList(this.mGcm.getGuestPath(this.mGcm.getGuestWinePrefixPath()) + "/" + GuestContainersManager.LOCAL_RUN_SCRIPT, "eval \"" + xDGLink.exec + " " + this.mCont.mConfig.getRunArguments() + "\""));
        if (this.mCont.mConfig.getHideTaskbarOnShortcut()) {
            this.mHideTaskbar = true;
        }
    }

    public StartGuest(RunExplorer runExplorer) {
        this.mGcm = GuestContainersManager.getInstance(((ApplicationStateBase) getApplicationState()).getAndroidApplicationContext());
        this.mExeArgv = new ArrayList<>();
        this.mEnv = new ArrayList<>();
        this.mHideXServerImage = false;
        this.mForceUseDefaultContols = false;
        this.mForceUseDefaultResolution = false;
        this.mHideTaskbar = false;
        this.mCont = runExplorer.mCont;
        this.mExeWorkDir = new File(mUserAreaDir.getAbsolutePath());
//        this.mExeArgv.addAll(Arrays.asList(getRecipeGuestPath("run/simple.sh"), "eval \"wine /opt/exec_wrapper.exe /opt/TFM.exe D:/\""));
        this.mExeArgv.addAll(Arrays.asList(getRecipeGuestPath("run/simple.sh"), "eval \"wine /opt/TFM.exe D:/\""));
//        this.mExeArgv.addAll(Arrays.asList(getRecipeGuestPath("run/simple.sh"), "eval \"xterm\""));

        if (this.mCont.mConfig.getDefaultControlsNotShortcut()) {
            this.mForceUseDefaultContols = true;
        }
        if (this.mCont.mConfig.getDefaultResolutionNotShortcut()) {
            this.mForceUseDefaultResolution = true;
        }
    }

    public StartGuest(InstallPackage installPackage) {

        this.mGcm = GuestContainersManager.getInstance(((ApplicationStateBase) getApplicationState()).getAndroidApplicationContext());
        this.mExeArgv = new ArrayList<>();
        this.mEnv = new ArrayList<>();
        this.mHideXServerImage = false;
        this.mForceUseDefaultContols = false;
        this.mForceUseDefaultResolution = false;
        this.mHideTaskbar = false;
        this.mCont = installPackage.mCont;
        this.mExeWorkDir = new File(mUserAreaDir.getAbsolutePath());
        this.mExeArgv.addAll(Arrays.asList("/bin/bash", "-x", getRecipeGuestPath("install_packages.sh")));
        String str = "";
        Iterator it = installPackage.mPackages.iterator();
        while (installPackage.mPackages.iterator().hasNext()) {
            str = str + ((ContainerPackage) it.next()).mName + " ";
        }
        this.mEnv.add("INSTALL_PACKAGES=" + str);
        this.mHideXServerImage = true;
        if (this.mCont.mConfig.getDefaultControlsNotShortcut()) {
            this.mForceUseDefaultContols = true;
        }
        if (this.mCont.mConfig.getDefaultResolutionNotShortcut()) {
            this.mForceUseDefaultResolution = true;
        }
    }

    @Override // com.eltechs.axs.configuration.startup.StartupAction
    public void execute() {
        if (this.mCont == null) {
            this.mCont = this.mGcm.createContainer();
        }
        this.mGcm.makeContainerActive(this.mCont);

        //添加环境变量
        MutiWine.addEnvVars(mCont.mId,mEnv);

        if (this.mScreenInfo != null) {
            this.mCont.mConfig.setScreenInfo(this.mScreenInfo);
        } else {
            this.mScreenInfo = this.mCont.mConfig.getScreenInfo();
        }
        if (this.mControls != null) {
            this.mCont.mConfig.setControls(this.mControls);
            if (!this.mControls.getId().equals("default")) {
                this.mCont.mConfig.setHideTaskbarOnShortcut(true);
            }
        } else {
            this.mControls = this.mCont.mConfig.getControls();
        }
        if (this.mLocaleName != null) {
            this.mCont.mConfig.setLocaleName(this.mLocaleName);
        } else {
            this.mLocaleName = this.mCont.mConfig.getLocaleName();
        }
        if (this.mRunArguments != null) {
            this.mCont.mConfig.setRunArguments(this.mRunArguments);
        }
        if (this.mRunGuide != null) {
            this.mCont.mConfig.setRunGuide(this.mRunGuide);
        }
        if (this.mContStartupActions != null) {
            this.mCont.mConfig.setStartupActions(this.mContStartupActions);
        } else {
            this.mContStartupActions = this.mCont.mConfig.getStartupActions();
        }
        if (this.mRunScriptToCopy != null) {
            try {
                FileUtils.copyFile(this.mRunScriptToCopy, new File(this.mCont.mWinePrefixPath, GuestContainersManager.LOCAL_RUN_SCRIPT));
            } catch (IOException unused) {
                return;
            }
        }
        String guestPath = this.mGcm.getGuestPath(this.mGcm.getGuestWinePrefixPath());
        if (this.mForceUseDefaultContols) {
            this.mControls = Controls.getDefault();
        }
        if (this.mForceUseDefaultResolution) {
            this.mScreenInfo = setScreenInfoDefaultResolution(this.mScreenInfo);
        }
        String str = this.mExeArgv.get(this.mExeArgv.size() - 1);
        if (str.contains("wine ")) {
            String wineOptions = getWineOptions(this.mScreenInfo, this.mHideTaskbar);
            int indexOf = str.indexOf("wine ") + "wine ".length();
            this.mExeArgv.set(this.mExeArgv.size() - 1, str.substring(0, indexOf) + wineOptions + " " + str.substring(indexOf));
        }
        Log.i(TAG, "screenInfo = " + this.mScreenInfo);
        Log.i(TAG, "controls = " + this.mControls);
        Log.i(TAG, "winePrefix = " + guestPath);
        Log.i(TAG, "exeWorkingDir = " + this.mExeWorkDir);
        Log.i(TAG, "exeArgv = " + this.mExeArgv);
        final ArrayList<AbstractStartupAction> arrayList = new ArrayList<>();
        if (this.mContStartupActions != null && !this.mContStartupActions.isEmpty()) {
            arrayList.add(new ContainerStartupAction(this.mCont, this.mContStartupActions));
        }
        arrayList.add(new PrepareGuestImage<>("/home/xdroid/", mUserAreaDir));
        final EnvironmentCustomisationParameters environmentCustomisationParameters = new EnvironmentCustomisationParameters();
        environmentCustomisationParameters.setScreenInfo(this.mScreenInfo);
        environmentCustomisationParameters.setLocaleName(this.mLocaleName);
        UiThread.post(() -> {
            getApplicationState().setSelectedExecutableFile(new DetectedExecutableFile<>(environmentCustomisationParameters, mControls.getId(),mControls.createInfoDialog()));
            StartGuest.this.getApplicationState().setXServerDisplayActivityInterfaceOverlay(mControls.create());
        });
        arrayList.add(new CreateTypicalEnvironmentConfiguration<>(12, false));
        this.mEnv.addAll(Arrays.asList("HOME=/home/xdroid/", "WINEPREFIX=" + guestPath));
        arrayList.add(new CreateLaunchConfiguration<>(this.mExeWorkDir, guestPath, this.mExeArgv.toArray(new String[0]), this.mEnv.toArray(new String[0]), EDStartupActivity.SOCKET_PATH_SUFFIX, mUserAreaDir.getAbsolutePath()));

        //自己添加一个action，用于添加环境变量
        arrayList.add(new AddEnvironmentVariables<>());

        arrayList.add(new StartEnvironmentService<>(new TrayConfiguration(R.drawable.tray, R.string.ed_host_app_name, R.string.ed_host_app_name)));
        arrayList.add(new StartGuestApplication<>(true, true));
        String guestImagePath = this.mGcm.getGuestImagePath();
        arrayList.add(new WaitForXClientConnection<>(new File(guestImagePath, "/home/xdroid/.ed_progress").getAbsolutePath(), this.mHideXServerImage));
        UiThread.post(() -> {
            ((ApplicationStateBase) getApplicationState()).getStartupActionsCollection().addActions(arrayList);
            StartGuest.this.sendDone();
        });
    }

    @SuppressLint("DefaultLocale")
    private static String getWineOptions(ScreenInfo screenInfo, boolean z) {
        StringBuilder sb = new StringBuilder();
        sb.append("explorer ");
        Object[] objArr = new Object[3];
        objArr[0] = z ? "xdroid" : "shell";
        objArr[1] = screenInfo.widthInPixels;
        objArr[2] = screenInfo.heightInPixels;
        sb.append(String.format("/desktop=%s,%dx%d", objArr));
        return sb.toString();
    }

    private static ScreenInfo setScreenInfoDefaultResolution(ScreenInfo screenInfo) {
        int[] defaultScreenSize = GuestContainerConfig.getDefaultScreenSize();
        int i = defaultScreenSize[0];
        int i2 = defaultScreenSize[1];
        return new ScreenInfo(i, i2, i / 10, i2 / 10, screenInfo.depth);
    }

    private static String getRecipeGuestPath(String str) {
        return GuestContainersManager.RECIPES_GUEST_DIR + str;
    }
}