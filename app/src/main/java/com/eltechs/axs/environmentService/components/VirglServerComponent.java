package com.eltechs.axs.environmentService.components;

import com.eltechs.axs.environmentService.EnvironmentComponent;
import com.eltechs.axs.rendering.impl.virglRenderer.VirglServer;
import com.eltechs.axs.xconnectors.epoll.UnixSocketConfiguration;
import java.io.IOException;

/* loaded from: classes.dex */
public class VirglServerComponent extends EnvironmentComponent {
    private final UnixSocketConfiguration socketConf;
    private Thread t = null;
    private final VirglServer virglServer = new VirglServer();

    public VirglServerComponent(UnixSocketConfiguration unixSocketConfiguration) {
        this.socketConf = unixSocketConfiguration;
    }

    @Override // com.eltechs.axs.environmentService.EnvironmentComponent
    public void start() throws IOException {
        this.t = new Thread() { // from class: com.eltechs.axs.environmentService.components.VirglServerComponent.1
            @Override // java.lang.Thread, java.lang.Runnable
            public void run() {
                VirglServerComponent.this.virglServer.startServer(VirglServerComponent.this.socketConf.getHostPath());
            }
        };
        this.t.start();
    }

    @Override // com.eltechs.axs.environmentService.EnvironmentComponent
    public void stop() {
        this.virglServer.stopServer();
    }

    public String getAddress() {
        return this.socketConf.getGuestPath();
    }
}
