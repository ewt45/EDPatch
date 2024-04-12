package com.eltechs.axs.guestApplicationVFSTracker;

import com.eltechs.axs.guestApplicationVFSTracker.impl.VFSTrackerRequestsDispatcher;
import com.eltechs.axs.helpers.Assert;
import com.eltechs.axs.xconnectors.XOutputStream;
import com.eltechs.axs.xconnectors.XStreamLock;

import java.io.IOException;
import java.io.Serializable;

public class StupidSyscallReportHandler implements SyscallReportHandler, Serializable {
    @Override // com.eltechs.axs.guestApplicationVFSTracker.SyscallReportHandler
    public boolean handleSyscall(SyscallReportData syscallReportData, XOutputStream xOutputStream) {
        System.out.printf("Tracker: syscall = %d, flags: %08x, index = %d\n", syscallReportData.getSyscallNr(), syscallReportData.getFlags(), syscallReportData.getFileIndex());
        try (XStreamLock lock = xOutputStream.lock()) {
            xOutputStream.writeInt(VFSTrackerRequestsDispatcher.MAGIC);
            xOutputStream.writeShort((short) VFSTrackerRequestsDispatcher.RESPONSE_LENGTH);
            xOutputStream.writeInt(0);
            xOutputStream.flush();
        } catch (IOException unused) {
            Assert.unreachable();
        }
        return true;
    }
}