package com.eltechs.axs.guestApplicationVFSTracker;

import static com.eltechs.axs.guestApplicationVFSTracker.LinuxConsts.NR_OPEN;

import com.eltechs.axs.environmentService.components.VFSTrackerComponent;
import com.eltechs.axs.guestApplicationVFSTracker.impl.VFSTrackerRequestsDispatcher;
import com.eltechs.axs.helpers.Assert;
import com.eltechs.axs.xconnectors.XOutputStream;
import com.eltechs.axs.xconnectors.XStreamLock;

import java.io.IOException;
import java.io.Serializable;

public abstract class SimpleFileAccessDetector implements SyscallReportHandler, Serializable {
    private final String[] fileList;
    private final VFSTrackerComponent ownerComponent;

    public abstract boolean fileAccessed(String str);

    protected SimpleFileAccessDetector(String[] fileList, VFSTrackerComponent ownerComponent) {
        this.fileList = fileList;
        this.ownerComponent = ownerComponent;
    }

    @Override // com.eltechs.axs.guestApplicationVFSTracker.SyscallReportHandler
    public boolean handleSyscall(SyscallReportData syscallReportData, XOutputStream xOutputStream) {
        String file = this.ownerComponent.getTrackedFileByIndex(syscallReportData.getFileIndex());
        for (String fileName : this.fileList) {
            if (fileName.equals(file)) {
                if (syscallReportData.getSyscallNr() == NR_OPEN) {
                    sendReply(xOutputStream, fileAccessed(file));
                } else {
                    sendReply(xOutputStream, true);
                }
                return true;
            }
        }
        return false;
    }

    private void sendReply(XOutputStream xOutputStream, boolean z) {
        try (XStreamLock lock = xOutputStream.lock()){
            xOutputStream.writeInt(VFSTrackerRequestsDispatcher.MAGIC);
            xOutputStream.writeShort((short) VFSTrackerRequestsDispatcher.RESPONSE_LENGTH);
            xOutputStream.writeInt(!z ? 1 : 0);
            xOutputStream.flush();
        } catch (IOException unused) {
            Assert.unreachable();
        }
    }
}