package com.eltechs.axs.guestApplicationVFSTracker;

import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class SyscallReportsReceiverListenersList {
    private final List<SyscallReportsReceiverListener> listeners = new ArrayList<>();

    public void addListener(SyscallReportsReceiverListener syscallReportsReceiverListener) {
        this.listeners.add(syscallReportsReceiverListener);
    }

    public void removeListener(SyscallReportsReceiverListener syscallReportsReceiverListener) {
        this.listeners.remove(syscallReportsReceiverListener);
    }

    public void notifySyscallReported(SyscallReportsReceiver syscallReportsReceiver) {
        for (SyscallReportsReceiverListener syscallReportsReceiverListener : this.listeners) {
            syscallReportsReceiverListener.syscallReported(syscallReportsReceiver);
        }
    }
}