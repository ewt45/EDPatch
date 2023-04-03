package com.eltechs.axs.environmentService.components;

import android.content.Context;
import com.eltechs.axs.environmentService.EnvironmentComponent;

/* loaded from: classes.dex */
public class NativeLibsConfiguration extends EnvironmentComponent {
    private final String nativeLibsDir;

    @Override // com.eltechs.axs.environmentService.EnvironmentComponent
    public void start() {
    }

    @Override // com.eltechs.axs.environmentService.EnvironmentComponent
    public void stop() {
    }

    public NativeLibsConfiguration(Context context) {
        this.nativeLibsDir = context.getApplicationInfo().nativeLibraryDir;
    }

    public String getLibubtPath() {
        return this.nativeLibsDir + "/libubt.so";
    }

    public String getLibubt2GPath() {
        return this.nativeLibsDir + "/libubt2g.so";
    }

    public String getElfLoaderPath() {
        return this.nativeLibsDir + "/libelfloader.so";
    }

    public String getKillswitchPath() {
        return this.nativeLibsDir + "/libkillswitch.so";
    }

    public String getSysVIPCEmulatorPath() {
        return this.nativeLibsDir + "/libipc-emulation.so";
    }

    public String getIsMemSplit3g1gPath() {
        return this.nativeLibsDir + "/libismemsplit3g1g.so";
    }
}