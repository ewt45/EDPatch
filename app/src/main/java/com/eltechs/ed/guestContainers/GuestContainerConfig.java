package com.eltechs.ed.guestContainers;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.DisplayMetrics;
import com.eltechs.axs.helpers.AndroidHelpers;
import com.eltechs.axs.xserver.ScreenInfo;
import com.eltechs.ed.Locales;
import com.eltechs.ed.controls.Controls;
import com.example.datainsert.exagear.mutiWine.MutiWine;

import java.io.File;

/* loaded from: classes.dex */
public class GuestContainerConfig {
    public static final String CONTAINER_CONFIG_FILE_KEY_PREFIX = "com.eltechs.ed.CONTAINER_CONFIG_";
    public static final String KEY_CONTROLS = "CONTROLS";
    public static final String KEY_DEFAULT_CONTROLS_NOT_SHORTCUT = "DEFAULT_CONTROLS_NOT_SHORTCUT";
    public static final String KEY_DEFAULT_RESOLUTION_NOT_SHORTCUT = "DEFAULT_RESOLUTION_NOT_SHORTCUT";
    public static final String KEY_HIDE_TASKBAR_SHORTCUT = "HIDE_TASKBAR_SHORTCUT";
    public static final String KEY_LOCALE_NAME = "LOCALE_NAME";
    public static final String KEY_NAME = "NAME";
    public static final String KEY_RUN_ARGUMENTS = "RUN_ARGUMENTS";
    public static final String KEY_RUN_GUIDE = "RUN_GUIDE";
    public static final String KEY_RUN_GUIDE_SHOWN = "RUN_GUIDE_SHOWN";
    public static final String KEY_SCREEN_COLOR_DEPTH = "SCREEN_COLOR_DEPTH";
    public static final String KEY_SCREEN_SIZE = "SCREEN_SIZE";
    public static final String KEY_STARTUP_ACTIONS = "STARTUP_ACTIONS";
    private static final String[] SUPPORTED_RESOLUTIONS = {"640,480", "800,600", "1024,768", "1280,720", "1280,1024", "1366,768", "1600,900", "1920,1080"};
    private GuestContainer mCont;
    private Context mContext;
    private SharedPreferences mSp;

    public GuestContainerConfig(Context context, GuestContainer guestContainer) {
        this.mContext = context;
        this.mSp = context.getSharedPreferences(CONTAINER_CONFIG_FILE_KEY_PREFIX + guestContainer.mId, 0);
        this.mCont = guestContainer;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    static void cloneContainerConfig(GuestContainer guestContainer, GuestContainer guestContainer2) {
        GuestContainerConfig guestContainerConfig = guestContainer2.mConfig;
        guestContainerConfig.setName("Container_" + guestContainer2.mId);
        guestContainer2.mConfig.setScreenInfo(guestContainer.mConfig.getScreenInfo());
        guestContainer2.mConfig.setLocaleName(guestContainer.mConfig.getLocaleName());
        guestContainer2.mConfig.setRunArguments(guestContainer.mConfig.getRunArguments());
        guestContainer2.mConfig.setControls(guestContainer.mConfig.getControls());
        guestContainer2.mConfig.setHideTaskbarOnShortcut(guestContainer.mConfig.getHideTaskbarOnShortcut());
        guestContainer2.mConfig.setDefaultControlsNotShortcut(guestContainer.mConfig.getDefaultControlsNotShortcut());
        guestContainer2.mConfig.setDefaultResolutionNotShortcut(guestContainer.mConfig.getDefaultResolutionNotShortcut());
        guestContainer2.mConfig.setStartupActions(guestContainer.mConfig.getStartupActions());
        guestContainer2.mConfig.setRunGuide(guestContainer.mConfig.getRunGuide());
        guestContainer2.mConfig.setRunGuideShown(guestContainer.mConfig.getRunGuideShown());
        MutiWine.cloneWineVerToContainerConfig(guestContainer.mId,guestContainer2.mId);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
     void deleteConfig() {
        File file = new File(this.mContext.getFilesDir().getParent() + "/shared_prefs/" + CONTAINER_CONFIG_FILE_KEY_PREFIX + this.mCont.mId + ".xml");
        if (file.exists()) {
            Context context = this.mContext;
            context.getSharedPreferences(CONTAINER_CONFIG_FILE_KEY_PREFIX + this.mCont.mId, 0).edit().clear().commit();
            file.delete();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
     void loadDefaults() {
        setName("Container_" + this.mCont.mId);
        setScreenInfoDefault();
        setLocaleName(Locales.getLocaleByDevice(this.mContext));
        setRunArguments("");
        setControls(Controls.getDefault());
        setHideTaskbarOnShortcut(false);
        setDefaultControlsNotShortcut(true);
        setDefaultResolutionNotShortcut(true);
        setStartupActions("");
        setRunGuide("");
        setRunGuideShown(false);
        MutiWine.writeWineVerToContainerConfig(mCont.mId);
    }

    public String getName() {
        SharedPreferences sharedPreferences = this.mSp;
        return sharedPreferences.getString(KEY_NAME, "Container_" + this.mCont.mId);
    }

    private void setName(String str) {
        this.mSp.edit().putString(KEY_NAME, str).apply();
    }

    public ScreenInfo getScreenInfo() {
        int parseInt;
        int parseInt2;
        if (this.mSp.contains(KEY_SCREEN_SIZE) && this.mSp.contains(KEY_SCREEN_COLOR_DEPTH)) {
            String string = this.mSp.getString(KEY_SCREEN_SIZE, "default");
            if (string.equals("default")) {
                int[] defaultScreenSize = getDefaultScreenSize();
                parseInt = defaultScreenSize[0];
                parseInt2 = defaultScreenSize[1];
            } else {
                String[] split = string.split(",");
                parseInt = Integer.parseInt(split[0]);
                parseInt2 = Integer.parseInt(split[1]);
            }
            int i = parseInt2;
            int i2 = parseInt;
            return new ScreenInfo(i2, i, i2 / 10, i / 10, Integer.parseInt(this.mSp.getString(KEY_SCREEN_COLOR_DEPTH, "16")));
        }
        return null;
    }

    public void setScreenInfo(ScreenInfo screenInfo) {
        SharedPreferences.Editor edit = this.mSp.edit();
        String str = Integer.toString(screenInfo.widthInPixels) + "," + screenInfo.heightInPixels;
        String[] strArr = SUPPORTED_RESOLUTIONS;
        boolean z = false;
        int length = strArr.length;
        int i = 0;
        while (true) {
            if (i >= length) {
                break;
            } else if (str.equals(strArr[i])) {
                z = true;
                break;
            } else {
                i++;
            }
        }
        if (z) {
            edit.putString(KEY_SCREEN_SIZE, Integer.toString(screenInfo.widthInPixels) + "," + screenInfo.heightInPixels);
        } else {
            edit.putString(KEY_SCREEN_SIZE, "default");
        }
        edit.putString(KEY_SCREEN_COLOR_DEPTH, Integer.toString(screenInfo.depth));
        edit.apply();
    }

    private void setScreenInfoDefault() {
        SharedPreferences.Editor edit = this.mSp.edit();
        edit.putString(KEY_SCREEN_SIZE, "default");
        edit.putString(KEY_SCREEN_COLOR_DEPTH, Integer.toString(16));
        edit.apply();
    }

    public String getLocaleName() {
        return this.mSp.getString(KEY_LOCALE_NAME, Locales.getLocaleByDevice(this.mContext));
    }

    public void setLocaleName(String str) {
        this.mSp.edit().putString(KEY_LOCALE_NAME, str).apply();
    }

    public String getRunArguments() {
        return this.mSp.getString(KEY_RUN_ARGUMENTS, "");
    }

    public void setRunArguments(String str) {
        this.mSp.edit().putString(KEY_RUN_ARGUMENTS, str).apply();
    }

    public Controls getControls() {
        String string = this.mSp.getString(KEY_CONTROLS, null);
        if (string == null) {
            return Controls.getDefault();
        }
        Controls find = Controls.find(string);
        if (find == null) {
            setControls(Controls.getDefault());
            return Controls.getDefault();
        }
        return find;
    }

    public void setControls(Controls controls) {
        this.mSp.edit().putString(KEY_CONTROLS, controls.getId()).apply();
    }

    public boolean getHideTaskbarOnShortcut() {
        return this.mSp.getBoolean(KEY_HIDE_TASKBAR_SHORTCUT, false);
    }

    public void setHideTaskbarOnShortcut(boolean z) {
        this.mSp.edit().putBoolean(null, z).apply();
    }

    public boolean getDefaultControlsNotShortcut() {
        return this.mSp.getBoolean(KEY_DEFAULT_CONTROLS_NOT_SHORTCUT, true);
    }

    public void setDefaultControlsNotShortcut(boolean z) {
        this.mSp.edit().putBoolean(null, z).apply();
    }

    public boolean getDefaultResolutionNotShortcut() {
        return this.mSp.getBoolean(KEY_DEFAULT_RESOLUTION_NOT_SHORTCUT, true);
    }

    public void setDefaultResolutionNotShortcut(boolean z) {
        this.mSp.edit().putBoolean(null, z).apply();
    }

    public String getStartupActions() {
        return this.mSp.getString(KEY_STARTUP_ACTIONS, "");
    }

    public void setStartupActions(String str) {
        this.mSp.edit().putString(KEY_STARTUP_ACTIONS, str).apply();
    }

    public String getRunGuide() {
        return this.mSp.getString(KEY_RUN_GUIDE, "");
    }

    public void setRunGuide(String str) {
        this.mSp.edit().putString(KEY_RUN_GUIDE, str).apply();
    }

    public Boolean getRunGuideShown() {
        return this.mSp.getBoolean(KEY_RUN_GUIDE_SHOWN, false);
    }

    public void setRunGuideShown(boolean z) {
        this.mSp.edit().putBoolean(KEY_RUN_GUIDE_SHOWN, z).apply();
    }

    public static int[] getDefaultScreenSize() {
        DisplayMetrics displayMetrics = AndroidHelpers.getDisplayMetrics();
        int i = (int) (displayMetrics.widthPixels / displayMetrics.density);
        int i2 = (int) (displayMetrics.heightPixels / displayMetrics.density);
        return new int[]{Math.max(Math.max(i, i2), 800), Math.max(Math.min(i, i2), 600)};
    }
}