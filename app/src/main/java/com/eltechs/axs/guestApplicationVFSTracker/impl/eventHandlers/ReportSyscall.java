package com.eltechs.axs.guestApplicationVFSTracker.impl.eventHandlers;

import com.eltechs.axs.guestApplicationVFSTracker.SyscallReportData;
import com.eltechs.axs.guestApplicationVFSTracker.SyscallReportHandler;
import com.eltechs.axs.guestApplicationVFSTracker.impl.VFSTrackerConnection;
import com.eltechs.axs.proto.input.ProcessingResult;
import com.eltechs.axs.xconnectors.RequestHandler;
import com.eltechs.axs.xconnectors.XInputStream;
import com.eltechs.axs.xconnectors.XOutputStream;
import java.io.IOException;

/* loaded from: classes.dex */
public class ReportSyscall implements RequestHandler<VFSTrackerConnection> {
    private final SyscallReportHandler handler;
    private final int MAX_TRACKED_FILES_COUNT = 16;
    private final int MAX_SYSCALL_NR = 350;

    public ReportSyscall(SyscallReportHandler syscallReportHandler) {
        this.handler = syscallReportHandler;
    }

    @Override // com.eltechs.axs.xconnectors.RequestHandler
    public ProcessingResult handleRequest(VFSTrackerConnection vFSTrackerConnection, XInputStream xInputStream, XOutputStream xOutputStream) throws IOException {
        int syscallNr = xInputStream.getInt();
        int flags = xInputStream.getInt();
        int fileIndex = xInputStream.getInt();
        if (syscallNr < 0 || syscallNr > MAX_SYSCALL_NR) {
            return ProcessingResult.PROCESSED_KILL_CONNECTION;
        }
        if (fileIndex < 0 || fileIndex >= MAX_TRACKED_FILES_COUNT) {
            return ProcessingResult.PROCESSED_KILL_CONNECTION;
        }
        this.handler.handleSyscall(new SyscallReportData(syscallNr, flags, fileIndex), xOutputStream);
        return ProcessingResult.PROCESSED;
    }
}