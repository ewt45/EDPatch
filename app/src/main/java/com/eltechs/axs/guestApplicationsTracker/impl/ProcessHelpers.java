package com.eltechs.axs.guestApplicationsTracker.impl;

/* loaded from: classes.dex */
public abstract class ProcessHelpers {
    public static final int SIGCONT = 18;
    public static final int SIGKILL = 9;
    public static final int SIGSTOP = 19;
    public static final int SIGTERM = 15;

    private static native void sendSignal(int pid, int singal);

    static {
        System.loadLibrary("ubt-helpers");
    }

    private ProcessHelpers() {
    }

    public static void suspendProcess(int pid) {
        sendSignal(pid, SIGSTOP);
    }

    public static void resumeProcess(int pid) {
        sendSignal(pid, SIGCONT);
    }

    public static void killProcess(int pid) {
        sendSignal(pid, SIGKILL);
    }

    public static void notifyProcessOfTermination(int pid) {
        sendSignal(pid, SIGTERM);
    }
}