package com.eltechs.axs.guestApplicationsTracker.impl;

/* loaded from: classes.dex */
public abstract class ProcessHelpers {
    public static final int SIGCONT = 18;
    public static final int SIGKILL = 9;
    public static final int SIGSTOP = 19;
    public static final int SIGTERM = 15;

    //native
    private static native void sendSignal(int i, int i2);

    static {
        System.loadLibrary("ubt_helpers");
    }

    private ProcessHelpers() {
    }

    public static void suspendProcess(int i) {
        sendSignal(i, 19);
    }

    public static void resumeProcess(int i) {
        sendSignal(i, 18);
    }

    public static void killProcess(int i) {
        sendSignal(i, 9);
    }

    public static void notifyProcessOfTermination(int i) {
        sendSignal(i, 15);
    }
}