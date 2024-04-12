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

    public static int runUbt(UBTLaunchConfiguration launchConfig, AXSEnvironment environment, String libUbtPath) {
        String fsRoot = launchConfig.getFsRoot();
        List<String> guestArguments = launchConfig.getGuestArguments();
        ArrayList<String> arrayList = new ArrayList<>(24 + guestArguments.size());
        arrayList.add("libubt");
        arrayList.add("--vfs-kind");
        arrayList.add("guest-first");
        arrayList.add("--path-prefix");
        arrayList.add(fsRoot);
        arrayList.add("--vpaths-list");
        arrayList.add(fsRoot + "/.exagear/vpaths-list");
        arrayList.add("-f");
        arrayList.add(launchConfig.getGuestExecutable());
//        arrayList.add("/opt/wine-stable/bin/wine");
        arrayList.add("--fork-controller");
        arrayList.add("ua:" + environment.getComponent(GuestApplicationsTrackerComponent.class).getSocket());
        SysVIPCEmulatorComponent sysVIPCEmulatorComponent = environment.getComponent(SysVIPCEmulatorComponent.class);
        if (sysVIPCEmulatorComponent != null) {
            arrayList.add("--ipc-emul-server");
            arrayList.add("ua:" + sysVIPCEmulatorComponent.getDomainName());
        }
        TempDirMaintenanceComponent tempDirMaintenanceComponent = environment.getComponent(TempDirMaintenanceComponent.class);
        if (tempDirMaintenanceComponent != null) {
            arrayList.add("--tmp-dir");
            arrayList.add(tempDirMaintenanceComponent.getTempDir().getAbsolutePath());
        }
        if (launchConfig.isStraceEnabled()) {
            arrayList.add("--strace");
        }
        VFSTrackerComponent vFSTrackerComponent = (VFSTrackerComponent) environment.getComponent(VFSTrackerComponent.class);
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
        Set<UBTLaunchConfiguration.VFSHacks> vfsHacks = launchConfig.getVfsHacks();
        if (!vfsHacks.isEmpty()) {
            StringBuilder sb2 = new StringBuilder();
            sb2.append("--vfs-hacks=");
            for (UBTLaunchConfiguration.VFSHacks vFSHacks : vfsHacks) {
                sb2.append(vFSHacks.getShortName());
                sb2.append(',');
            }
            arrayList.add(sb2.toString());
        }
        Map<String, String> fileNameReplacements = launchConfig.getFileNameReplacements();
        if (!fileNameReplacements.isEmpty()) {
            StringBuilder sb3 = new StringBuilder();
            sb3.append("--file-name-replacements=");
            for (Map.Entry<String, String> entry : fileNameReplacements.entrySet())
                sb3.append(entry.getKey()).append(',')
                        .append(entry.getValue()).append(',');

            arrayList.add(sb3.toString());
        }
        String socketPathSuffix = launchConfig.getSocketPathSuffix();
        if (socketPathSuffix != null) {
            arrayList.add("--socket-path-suffix");
            arrayList.add(socketPathSuffix);
        }
        arrayList.add("--ubt-executable");
        arrayList.add(libUbtPath);
        String elfLoaderPath = environment.getNativeLibsConfiguration().getElfLoaderPath();
        arrayList.add("--ubt-loader");
        arrayList.add(elfLoaderPath);
        arrayList.add("--");
        arrayList.addAll(guestArguments);
        return runUbt(launchConfig.getGuestExecutablePath(), elfLoaderPath, libUbtPath, (String[]) arrayList.toArray(new String[0]),  launchConfig.getGuestEnvironmentVariables().toArray(new String[0]));
    }
}