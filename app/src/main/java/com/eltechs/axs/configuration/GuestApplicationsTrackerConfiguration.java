package com.eltechs.axs.configuration;

import com.eltechs.axs.network.SocketPaths;
import java.io.Serializable;

/* loaded from: classes.dex */
public class GuestApplicationsTrackerConfiguration implements Serializable {
    private String address = SocketPaths.GUEST_APPLICATIONS_TRACKER;

    public void setSocket(String addr) {
        this.address = addr;
    }

    public String getSocket() {
        return this.address;
    }
}