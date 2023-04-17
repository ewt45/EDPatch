package com.eltechs.axs.environmentService.components;

import com.eltechs.axs.environmentService.EnvironmentComponent;
import com.eltechs.axs.guestApplicationVFSTracker.SyscallReportMultiplexor;
import com.eltechs.axs.guestApplicationVFSTracker.VFSTracker;
import com.eltechs.axs.helpers.Assert;
import com.eltechs.axs.xconnectors.epoll.UnixSocketConfiguration;
import java.io.IOException;
import java.util.List;

/* loaded from: classes.dex */
public class VFSTrackerComponent extends EnvironmentComponent {
    private final String address;
    private final SyscallReportMultiplexor rootHandler;
    private final List<String> trackedFiles;
    private VFSTracker tracker;

    public VFSTrackerComponent(String str, List<String> list, SyscallReportMultiplexor syscallReportMultiplexor) {
        this.address = str;
        this.trackedFiles = list;
        this.rootHandler = syscallReportMultiplexor;
    }

    @Override // com.eltechs.axs.environmentService.EnvironmentComponent
    public void start() throws IOException {
        Assert.state(this.tracker == null, "VFS tracker is already started.");
        this.tracker = new VFSTracker(UnixSocketConfiguration.createAbstractSocket(this.address), this.rootHandler);
    }

    @Override // com.eltechs.axs.environmentService.EnvironmentComponent
    public void stop() {
        Assert.state(this.tracker != null, "VFS tracker is not yet started.");
        try {
            this.tracker.stop();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.tracker = null;
    }

    public String getSocket() {
        return this.address;
    }

    public List<String> getTrackedFiles() {
        return this.trackedFiles;
    }

    public String getTrackedFileByIndex(int i) {
        Assert.state(i >= 0 && i < this.trackedFiles.size());
        return this.trackedFiles.get(i);
    }

    public SyscallReportMultiplexor getRootHandler() {
        return this.rootHandler;
    }
}
