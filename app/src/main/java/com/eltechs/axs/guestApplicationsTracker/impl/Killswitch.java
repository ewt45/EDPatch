package com.eltechs.axs.guestApplicationsTracker.impl;

import com.eltechs.axs.annotations.UsedByNativeCode;
import com.eltechs.axs.helpers.Assert;
import java.io.IOException;

/* loaded from: classes.dex */
public class Killswitch {
    @UsedByNativeCode
    private int controlPipeFd;
    @UsedByNativeCode
    private int killswitchPid;

    private static native boolean initialiseNativeParts();

    private native int startKillswitch(String elfLoaderPath, String killSwitchPath, String libUbtPath, String outputFilePath);

    private native void stopKillswitch(int killswitchPid, int controlPipeFd);

    static {
        System.loadLibrary("ubt-helpers");
        Assert.state(initialiseNativeParts(), "Managed and native parts of Killswitch do not match one another.");
    }

    public Killswitch(String elfLoaderPath, String killSwitchPath, String libUbtPath) throws IOException {
        int startKillswitch = startKillswitch(elfLoaderPath, killSwitchPath, libUbtPath, "/mnt/sdcard/killswitch.txt");
        if (startKillswitch < 0) {
            throw new IOException(String.format("Failed to start the killswitch; errno = %d.", -startKillswitch));
        }
    }

    public void stop() {
        stopKillswitch(this.killswitchPid, this.controlPipeFd);
        this.killswitchPid = -1;
        this.controlPipeFd = -1;
    }
}