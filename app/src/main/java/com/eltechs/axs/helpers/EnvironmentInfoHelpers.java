package com.eltechs.axs.helpers;

import com.eltechs.axs.environmentService.components.NativeLibsConfiguration;

/* loaded from: classes.dex */
public class EnvironmentInfoHelpers {
    private static native boolean isCpuFeaturesOk(boolean z);

    private static native boolean runNativeProgram(String str, String str2);

    static {
        System.loadLibrary("ubt-helpers");
    }

    public static boolean canRunUbtOnCpu(boolean z) {
        if (isSupportedCpu()) {
            return isCpuFeaturesOk(z);
        }
        return false;
    }

    public static final boolean isMemSplit3g1g(NativeLibsConfiguration nativeLibsConfiguration) {
        return runNativeProgram(nativeLibsConfiguration.getElfLoaderPath(), nativeLibsConfiguration.getIsMemSplit3g1gPath());
    }

    private static final boolean isSupportedCpu() {
        String property = System.getProperty("os.arch");
        return property.contains("armv7") || property.contains("aarch64");
    }
}