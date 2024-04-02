package com.eltechs.axs.environmentService.components;

import com.eltechs.axs.environmentService.EnvironmentComponent;
import com.eltechs.axs.helpers.Assert;
import com.eltechs.axs.sysvipc.Emulator;
import com.eltechs.axs.sysvipc.SHMEngine;
import com.eltechs.axs.sysvipc.SHMEngineImpl;
import java.io.IOException;

/* loaded from: classes.dex */
public class SysVIPCEmulatorComponent extends EnvironmentComponent {
    /** app 包名 */
    private final String domainName;
    private Emulator emulator;
    private SHMEngineImpl shmEngine;

    public SysVIPCEmulatorComponent(String domainName) {
        this.domainName = domainName;
    }

    @Override // com.eltechs.axs.environmentService.EnvironmentComponent
    public void start() throws IOException {
        Assert.state(this.emulator == null, "Sys V IPC emulator component is already started.");
        NativeLibsConfiguration nativeLibsConfiguration = getEnvironment().getNativeLibsConfiguration();
        this.emulator = new Emulator(this.domainName, nativeLibsConfiguration.getElfLoaderPath(), nativeLibsConfiguration.getSysVIPCEmulatorPath());
        this.shmEngine = new SHMEngineImpl(this.domainName);
    }

    @Override // com.eltechs.axs.environmentService.EnvironmentComponent
    public void stop() {
        Assert.state(this.emulator != null, "Sys V IPC emulator is not yet started.");
        try {
            this.shmEngine.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            this.emulator.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.emulator = null;
        this.shmEngine = null;
    }

    public String getDomainName() {
        return this.domainName;
    }

    public SHMEngine getShmEngine() {
        return this.shmEngine;
    }
}