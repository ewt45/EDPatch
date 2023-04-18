package com.eltechs.axs.guestApplicationsTracker;

import com.eltechs.axs.ExagearImageConfiguration.TempDirMaintenanceComponent;
import com.eltechs.axs.configuration.UBTLaunchConfiguration;
import com.eltechs.axs.environmentService.AXSEnvironment;
import com.eltechs.axs.environmentService.components.GuestApplicationsTrackerComponent;
import com.eltechs.axs.environmentService.components.SysVIPCEmulatorComponent;
import com.eltechs.axs.environmentService.components.VFSTrackerComponent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/* loaded from: classes.dex */
public abstract class UBT {
    private static native int runUbt(String str, String str2, String str3, String[] strArr, String[] strArr2);

    private UBT() {
    }

    static {
        System.loadLibrary("ubt-helpers");
    }

    public static int runUbt(UBTLaunchConfiguration uBTLaunchConfiguration, AXSEnvironment aXSEnvironment, String str) {
        SysVIPCEmulatorComponent sysVIPCEmulatorComponent;
        String fsRoot = uBTLaunchConfiguration.getFsRoot();
        String guestExecutable = uBTLaunchConfiguration.getGuestExecutable();
        List<String> guestArguments = uBTLaunchConfiguration.getGuestArguments();
        List<String> guestEnvironmentVariables = uBTLaunchConfiguration.getGuestEnvironmentVariables();
        String guestExecutablePath = uBTLaunchConfiguration.getGuestExecutablePath();
        ArrayList<String> arrayList = new ArrayList<>(24 + guestArguments.size());
        arrayList.add("libubt");
        arrayList.add("--vfs-kind");
        arrayList.add("guest-first");
        arrayList.add("--path-prefix");
        arrayList.add(fsRoot);
        arrayList.add("--vpaths-list");
        arrayList.add(fsRoot + "/.exagear/vpaths-list");
        arrayList.add("-f");
        arrayList.add(guestExecutable);
//        arrayList.add("/opt/wine-stable/bin/wine");
        arrayList.add("--fork-controller");
        arrayList.add("ua:" + ((GuestApplicationsTrackerComponent) aXSEnvironment.getComponent(GuestApplicationsTrackerComponent.class)).getSocket());
        if ((sysVIPCEmulatorComponent = (SysVIPCEmulatorComponent) aXSEnvironment.getComponent(SysVIPCEmulatorComponent.class)) != null) {
            arrayList.add("--ipc-emul-server");
            arrayList.add("ua:" + sysVIPCEmulatorComponent.getDomainName());
        }
        TempDirMaintenanceComponent tempDirMaintenanceComponent = (TempDirMaintenanceComponent) aXSEnvironment.getComponent(TempDirMaintenanceComponent.class);
        if (tempDirMaintenanceComponent != null) {
            arrayList.add("--tmp-dir");
            arrayList.add(tempDirMaintenanceComponent.getTempDir().getAbsolutePath());
        }
        if (uBTLaunchConfiguration.isStraceEnabled()) {
            arrayList.add("--strace");
        }
        VFSTrackerComponent vFSTrackerComponent = (VFSTrackerComponent) aXSEnvironment.getComponent(VFSTrackerComponent.class);
        if (vFSTrackerComponent != null) {
            arrayList.add("--vfs-tracker-controller");
            arrayList.add("ua:" + vFSTrackerComponent.getSocket());
            if (vFSTrackerComponent.getTrackedFiles().size() > 0) {
                StringBuilder sb = new StringBuilder();
                sb.append("--track-files=");
                for (String str2 : vFSTrackerComponent.getTrackedFiles()) {
                    sb.append(str2);
                    sb.append(',');
                }
                arrayList.add(sb.toString());
            }
        }
        Set<UBTLaunchConfiguration.VFSHacks> vfsHacks = uBTLaunchConfiguration.getVfsHacks();
        if (!vfsHacks.isEmpty()) {
            StringBuilder sb2 = new StringBuilder();
            sb2.append("--vfs-hacks=");
            for (UBTLaunchConfiguration.VFSHacks vFSHacks : vfsHacks) {
                sb2.append(vFSHacks.getShortName());
                sb2.append(',');
            }
            arrayList.add(sb2.toString());
        }
        Map<String, String> fileNameReplacements = uBTLaunchConfiguration.getFileNameReplacements();
        if (!fileNameReplacements.isEmpty()) {
            StringBuilder sb3 = new StringBuilder();
            sb3.append("--file-name-replacements=");
            for (Map.Entry<String, String> entry : fileNameReplacements.entrySet()) {
                sb3.append(entry.getKey());
                sb3.append(',');
                sb3.append(entry.getValue());
                sb3.append(',');
            }
            arrayList.add(sb3.toString());
        }
        String socketPathSuffix = uBTLaunchConfiguration.getSocketPathSuffix();
        if (socketPathSuffix != null) {
            arrayList.add("--socket-path-suffix");
            arrayList.add(socketPathSuffix);
        }
        arrayList.add("--ubt-executable");
        arrayList.add(str);
        String elfLoaderPath = aXSEnvironment.getNativeLibsConfiguration().getElfLoaderPath();
        arrayList.add("--ubt-loader");
        arrayList.add(elfLoaderPath);
        arrayList.add("--");
        arrayList.addAll(guestArguments);
        return runUbt(guestExecutablePath, elfLoaderPath, str, (String[]) arrayList.toArray(new String[0]), (String[]) guestEnvironmentVariables.toArray(new String[guestEnvironmentVariables.size()]));
    }
}