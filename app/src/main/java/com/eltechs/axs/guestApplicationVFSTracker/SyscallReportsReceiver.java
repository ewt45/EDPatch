package com.eltechs.axs.guestApplicationVFSTracker;

import static com.eltechs.axs.guestApplicationVFSTracker.LinuxConsts.NR_CLOSE;
import static com.eltechs.axs.guestApplicationVFSTracker.LinuxConsts.NR_OPEN;
import static com.eltechs.axs.guestApplicationVFSTracker.impl.VFSTrackerRequestsDispatcher.RESPONSE_LENGTH;

import android.util.Log;
import com.eltechs.axs.environmentService.components.VFSTrackerComponent;
import com.eltechs.axs.guestApplicationVFSTracker.impl.VFSTrackerRequestsDispatcher;
import com.eltechs.axs.helpers.Assert;
import com.eltechs.axs.xconnectors.XOutputStream;
import com.eltechs.axs.xconnectors.XStreamLock;

import java.io.IOException;

/* loaded from: classes.dex */
public class SyscallReportsReceiver implements SyscallReportHandler {
    private final String file;
    private boolean isOn;
    private final SyscallReportsReceiverListenersList listeners = new SyscallReportsReceiverListenersList();
    private int numCloseCalled;
    private int numOpenRDCalled;
    private int numOpenWRCalled;
    private final VFSTrackerComponent vfsTracker;

    public SyscallReportsReceiver(VFSTrackerComponent vFSTrackerComponent, String fileName) {
        this.vfsTracker = vFSTrackerComponent;
        this.file = fileName;
    }

    public void startReceiving() {
        this.isOn = true;
        this.numOpenRDCalled = 0;
        this.numOpenWRCalled = 0;
        this.numCloseCalled = 0;
    }

    public void stopReceiving() {
        this.isOn = false;
    }

    public boolean isReceiving() {
        return this.isOn;
    }

    @Override // com.eltechs.axs.guestApplicationVFSTracker.SyscallReportHandler
    public boolean handleSyscall(SyscallReportData syscallReportData, XOutputStream xOutputStream) {
        Log.d("SYSCALL", String.format("handle some syscall! file_index: %d, syscall: %d flags: %08x", Integer.valueOf(syscallReportData.getFileIndex()), Integer.valueOf(syscallReportData.getSyscallNr()), Integer.valueOf(syscallReportData.getFlags())));
        if (this.vfsTracker.getTrackedFileByIndex(syscallReportData.getFileIndex()).equals(this.file)) {
            Log.d("SYSCALL", String.format("handle %s!", this.file));
            if (this.isOn) {
                Log.d("SYSCALL", String.format("FSM: on %s!\n", this.file));
                switch (syscallReportData.getSyscallNr()) {
                    case NR_OPEN:
                        if ((syscallReportData.getFlags() & 3) != 0) {
                            this.numOpenWRCalled++;
                            break;
                        } else {
                            this.numOpenRDCalled++;
                            break;
                        }
                    case NR_CLOSE:
                        this.numCloseCalled++;
                        break;
                    default:
                        Assert.unreachable("Only open() and close() are supported in ubt");
                        break;
                }
            }
            this.listeners.notifySyscallReported(this);
            try (XStreamLock lock = xOutputStream.lock()) {
                xOutputStream.writeInt(VFSTrackerRequestsDispatcher.MAGIC);
                xOutputStream.writeShort((short) RESPONSE_LENGTH);
                xOutputStream.writeInt(0);
                xOutputStream.flush();
            } catch (IOException unused) {
                Assert.unreachable();
            }
            return true;
        }
        return false;
    }

    public int getNumOpenRDCalled() {
        return this.numOpenRDCalled;
    }

    public int getNumOpenWRCalled() {
        return this.numOpenWRCalled;
    }

    public int getNumCloseCalled() {
        return this.numCloseCalled;
    }

    public void addListener(SyscallReportsReceiverListener syscallReportsReceiverListener) {
        this.listeners.addListener(syscallReportsReceiverListener);
    }

    public void removeListener(SyscallReportsReceiverListener syscallReportsReceiverListener) {
        this.listeners.removeListener(syscallReportsReceiverListener);
    }
}