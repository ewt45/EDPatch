package com.eltechs.axs.configuration;

import com.eltechs.axs.Globals;
import com.eltechs.axs.environmentService.components.NativeLibsConfiguration;
import com.eltechs.axs.helpers.EnvironmentInfoHelpers;

/* loaded from: classes.dex */
public class MemsplitConfiguration {
    private final boolean force3g;
    private boolean firstRun = true;
    private boolean detected3g = false;

    public boolean isMemsplit3g() {
        if (!this.firstRun) {
            return this.detected3g;
        }
        this.firstRun = false;
        if (this.force3g) {
            this.detected3g = true;
        } else {
            this.detected3g = EnvironmentInfoHelpers.isMemSplit3g1g(new NativeLibsConfiguration(Globals.getAppContext()));
        }
        return this.detected3g;
    }

    public MemsplitConfiguration(boolean z) {
        this.force3g = z;
    }
}