package com.eltechs.axs.guestApplicationVFSTracker.impl;

import com.eltechs.axs.guestApplicationVFSTracker.SyscallReportHandler;
import com.eltechs.axs.guestApplicationVFSTracker.impl.eventHandlers.ReportSyscall;
import com.eltechs.axs.helpers.ArithHelpers;
import com.eltechs.axs.proto.input.ProcessingResult;

import com.eltechs.axs.xconnectors.RequestHandler;
import com.eltechs.axs.xconnectors.XInputStream;
import com.eltechs.axs.xconnectors.XOutputStream;
import java.io.IOException;
import java.util.EnumMap;
import java.util.Map;

/* loaded from: classes.dex */
public class VFSTrackerRequestsDispatcher implements RequestHandler<VFSTrackerConnection> {
    public static final int MAGIC = 1263488840;
    public static final int REQUEST_LENGTH = 20;
    public static final int RESPONSE_LENGTH = 10;
    private static final int SIZE_OF_INT = 4;
    private static final int SIZE_OF_SHORT = 2;
    private final Map<RequestCodes, RequestHandler<VFSTrackerConnection>> requestHandlers = new EnumMap<>(RequestCodes.class);

    public VFSTrackerRequestsDispatcher(SyscallReportHandler syscallReportHandler) {
        this.requestHandlers.put(RequestCodes.REPORT_SYSCALL, new ReportSyscall(syscallReportHandler));
    }

    @Override // com.eltechs.axs.xconnectors.RequestHandler
    public ProcessingResult handleRequest(VFSTrackerConnection vFSTrackerConnection, XInputStream xInputStream, XOutputStream xOutputStream) throws IOException {
        if (xInputStream.getAvailableBytesCount() < REQUEST_LENGTH) {
            return ProcessingResult.INCOMPLETE_BUFFER;
        }
        int rqMagic = xInputStream.getInt();
        int length = ArithHelpers.extendAsUnsigned(xInputStream.getShort()) - REQUEST_LENGTH;
        int requestCode = ArithHelpers.extendAsUnsigned(xInputStream.getShort());
        if (rqMagic != MAGIC || requestCode < 0 || requestCode >= this.requestHandlers.size()) {
            return ProcessingResult.PROCESSED_KILL_CONNECTION;
        }
        if (xInputStream.getAvailableBytesCount() < length) {
            return ProcessingResult.INCOMPLETE_BUFFER;
        }
        return this.requestHandlers.get(RequestCodes.values()[requestCode]).handleRequest(vFSTrackerConnection, xInputStream, xOutputStream);
    }
}