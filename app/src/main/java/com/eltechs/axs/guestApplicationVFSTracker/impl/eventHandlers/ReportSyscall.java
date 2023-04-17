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
        int i = xInputStream.getInt();
        int i2 = xInputStream.getInt();
        int i3 = xInputStream.getInt();
        if (i < 0 || i > 350) {
            return ProcessingResult.PROCESSED_KILL_CONNECTION;
        }
        if (i3 < 0 || i3 >= 16) {
            return ProcessingResult.PROCESSED_KILL_CONNECTION;
        }
        this.handler.handleSyscall(new SyscallReportData(i, i2, i3), xOutputStream);
        return ProcessingResult.PROCESSED;
    }
}