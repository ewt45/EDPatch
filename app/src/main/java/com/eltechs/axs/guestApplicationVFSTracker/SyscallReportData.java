package com.eltechs.axs.guestApplicationVFSTracker;

/* loaded from: classes.dex */
public class SyscallReportData {
    private int fileIndex;
    private int flags;
    private int syscallNr;

    public SyscallReportData(int syscallNr, int flags, int fileIndex) {
        this.syscallNr = syscallNr;
        this.flags = flags;
        this.fileIndex = fileIndex;
    }

    public int getSyscallNr() {
        return this.syscallNr;
    }

    public int getFlags() {
        return this.flags;
    }

    public int getFileIndex() {
        return this.fileIndex;
    }
}