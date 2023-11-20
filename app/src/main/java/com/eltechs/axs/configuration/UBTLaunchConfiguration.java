package com.eltechs.axs.configuration;

import android.annotation.SuppressLint;

import com.eltechs.axs.environmentService.AXSEnvironment;
import com.eltechs.axs.environmentService.components.ALSAServerComponent;
import com.eltechs.axs.environmentService.components.DirectSoundServerComponent;
import com.eltechs.axs.environmentService.components.VirglServerComponent;
import com.eltechs.axs.environmentService.components.XServerComponent;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/* loaded from: classes.dex */
public class UBTLaunchConfiguration implements Serializable {
    public static String gallium_driver; //之前老虎山用于修改环境变量的，初始化在StartGuest中
    private String socketPathSuffix;
    private String fsRoot = null;
    private String guestExecutablePath = null;
    private String guestExecutable = null;
    private List<String> guestArguments = Collections.emptyList();
    private List<String> guestEnvironmentVariables = Collections.emptyList();
    private boolean isStraceEnabled = false;
    private Set<VFSHacks> vfsHacks = EnumSet.noneOf(VFSHacks.class);
    private Map<String, String> fileNameReplacements = Collections.emptyMap();

    /* loaded from: classes.dex */
    public enum VFSHacks {
        PRERESOLVE_WINE_DRIVE_SYMLINKS("pwds"),
        PRERESOLVE_EXPLICITLY_LISTED_SYMLINKS("pels"),
        ASSUME_NO_SYMLINKS_EXCEPT_PRERESOLVED("ansep"),
        TREAT_LSTAT_SOCKET_AS_STATTING_WINESERVER_SOCKET("tlsasws"),
        TRUNCATE_STAT_INODE("tsi"),
        SIMPLE_PASS_DEV("spd");

        private final String shortName;

        VFSHacks(String str) {
            this.shortName = str;
        }

        public String getShortName() {
            return this.shortName;
        }
    }

    public String getFsRoot() {
        return this.fsRoot;
    }

    public void setFsRoot(String str) {
        this.fsRoot = str;
    }

    public String getGuestExecutablePath() {
        return this.guestExecutablePath;
    }

    public void setGuestExecutablePath(String str) {
        this.guestExecutablePath = str;
    }

    public String getGuestExecutable() {
        return this.guestExecutable;
    }

    public void setGuestExecutable(String str) {
        this.guestExecutable = str;
    }

    public List<String> getGuestArguments() {
        return this.guestArguments;
    }

    public void setGuestArguments(String[] strArr) {
        this.guestArguments = Arrays.asList(strArr);
    }

    public void setGuestArguments(List<String> list) {
        this.guestArguments = list;
    }

    public List<String> getGuestEnvironmentVariables() {
        return this.guestEnvironmentVariables;
    }

    public void setGuestEnvironmentVariables(String[] strArr) {
        this.guestEnvironmentVariables = new ArrayList<>(strArr.length);
        Collections.addAll(this.guestEnvironmentVariables, strArr);
    }

    public void setGuestEnvironmentVariables(List<String> list) {
        this.guestEnvironmentVariables = new ArrayList<>(list);
    }

    public void addEnvironmentVariable(String str, String str2) {
        this.guestEnvironmentVariables.add(String.format("%s=%s", str, str2));
    }

    @SuppressLint("DefaultLocale")
    public void addArgumentsToEnvironment(AXSEnvironment aXSEnvironment) {
        XServerComponent xServerComponent = (XServerComponent) aXSEnvironment.getComponent(XServerComponent.class);
        if (xServerComponent != null) {
            this.guestEnvironmentVariables.add(String.format("DISPLAY=:%d", xServerComponent.getDisplayNumber()));
        }
        VirglServerComponent virglServerComponent = (VirglServerComponent) aXSEnvironment.getComponent(VirglServerComponent.class);
        if (virglServerComponent != null) {
            this.guestEnvironmentVariables.add("GALLIUM_DRIVER=virpipe");
            this.guestEnvironmentVariables.add(String.format("VTEST_SOCKET=%s", virglServerComponent.getAddress()));
        }
        ALSAServerComponent aLSAServerComponent = (ALSAServerComponent) aXSEnvironment.getComponent(ALSAServerComponent.class);
        if (aLSAServerComponent != null) {
            this.guestEnvironmentVariables.add(String.format("AXS_SOUND_SERVER_PORT=%s", aLSAServerComponent.getAddress()));
        }
        DirectSoundServerComponent directSoundServerComponent = (DirectSoundServerComponent) aXSEnvironment.getComponent(DirectSoundServerComponent.class);
        if (directSoundServerComponent != null) {
            this.guestEnvironmentVariables.add(String.format("AXS_DSOUND_SERVER_PORT=%s", directSoundServerComponent.getAddress()));
        }
    }

    public boolean isStraceEnabled() {
        return this.isStraceEnabled;
    }

    public void setStraceEnabled(boolean z) {
        this.isStraceEnabled = z;
    }

    public void setVfsHacks(Set<VFSHacks> set) {
        this.vfsHacks = set;
    }

    public Set<VFSHacks> getVfsHacks() {
        Set<VFSHacks> set = this.vfsHacks;
        if (!this.fileNameReplacements.isEmpty()) {
            set.add(VFSHacks.PRERESOLVE_EXPLICITLY_LISTED_SYMLINKS);
        }
        return set;
    }

    public void setFileNameReplacements(Map<String, String> map) {
        this.fileNameReplacements = map;
    }

    public Map<String, String> getFileNameReplacements() {
        return this.fileNameReplacements;
    }

    public String getSocketPathSuffix() {
        return this.socketPathSuffix;
    }

    public void setSocketPathSuffix(String str) {
        this.socketPathSuffix = str;
    }
}