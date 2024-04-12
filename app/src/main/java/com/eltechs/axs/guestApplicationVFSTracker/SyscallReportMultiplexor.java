package com.eltechs.axs.guestApplicationVFSTracker;

import android.util.Log;
import com.eltechs.axs.helpers.Assert;
import com.eltechs.axs.xconnectors.XOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

/* loaded from: classes.dex */
public class SyscallReportMultiplexor implements SyscallReportHandler, Serializable {
    private final Collection<SyscallReportHandler> handlers = new ArrayList<>();

    public void addHandler(SyscallReportHandler handler) {
        this.handlers.add(handler);
    }

    public void removeHandler(SyscallReportHandler handler) {
        this.handlers.remove(handler);
    }

    @Override // com.eltechs.axs.guestApplicationVFSTracker.SyscallReportHandler
    public boolean handleSyscall(SyscallReportData syscallReportData, XOutputStream xOutputStream) {
        Log.d("SYSCALL", String.format("Handling syscall for %d : nr : %d fl: %d", syscallReportData.getFileIndex(), syscallReportData.getSyscallNr(), syscallReportData.getFlags()));
        for (SyscallReportHandler syscallReportHandler : this.handlers) {
            if (syscallReportHandler.handleSyscall(syscallReportData, xOutputStream)) {
                return true;
            }
        }
        Assert.unreachable(String.format("No handlers found for file %d; ubt hung up!", syscallReportData.getFileIndex()));
        return false;
    }
}