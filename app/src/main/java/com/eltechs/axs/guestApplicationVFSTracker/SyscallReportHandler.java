package com.eltechs.axs.guestApplicationVFSTracker;

import com.eltechs.axs.xconnectors.XOutputStream;

/* loaded from: classes.dex */
public interface SyscallReportHandler {
    boolean handleSyscall(SyscallReportData syscallReportData, XOutputStream xOutputStream);
}