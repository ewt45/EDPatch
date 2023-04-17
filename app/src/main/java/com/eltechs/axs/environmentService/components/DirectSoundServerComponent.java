package com.eltechs.axs.environmentService.components;

import com.eltechs.axs.dsoundServer.DirectSoundClient;
import com.eltechs.axs.dsoundServer.DirectSoundConnectionHandler;
import com.eltechs.axs.dsoundServer.DirectSoundRequestHandler;
import com.eltechs.axs.dsoundServer.impl.opensl.OpenSLDirectSoundBufferFactoryImpl;
import com.eltechs.axs.environmentService.EnvironmentComponent;
import com.eltechs.axs.helpers.Assert;
import com.eltechs.axs.sysvipc.SHMEngine;
import com.eltechs.axs.xconnectors.epoll.FairEpollConnector;
import com.eltechs.axs.xconnectors.epoll.UnixSocketConfiguration;
import java.io.IOException;

/* loaded from: classes.dex */
public class DirectSoundServerComponent extends EnvironmentComponent {
    private DirectSoundConnectionHandler connectionHandler;
    private FairEpollConnector<DirectSoundClient> connector;
    private OpenSLDirectSoundBufferFactoryImpl directSoundBufferFactory;
    private DirectSoundRequestHandler requestHandler;
    private final UnixSocketConfiguration socketConf;

    public DirectSoundServerComponent(UnixSocketConfiguration unixSocketConfiguration) {
        this.socketConf = unixSocketConfiguration;
    }

    @Override // com.eltechs.axs.environmentService.EnvironmentComponent
    public void start() throws IOException {
        boolean z = false;
        Assert.state(this.connector == null, "DirectSound server component already started.");
        SHMEngine shmEngine = ((SysVIPCEmulatorComponent) getEnvironment().getComponent(SysVIPCEmulatorComponent.class)).getShmEngine();
        if (shmEngine != null) {
            z = true;
        }
        Assert.state(z, "DirectSoundServerComponent requires SysVIPCEmulatorComponent.");
        this.directSoundBufferFactory = new OpenSLDirectSoundBufferFactoryImpl();
        this.connectionHandler = new DirectSoundConnectionHandler(shmEngine, this.directSoundBufferFactory);
        this.requestHandler = new DirectSoundRequestHandler();
        this.connector = FairEpollConnector.listenOnSpecifiedUnixSocket(this.socketConf, this.connectionHandler, this.requestHandler);
        this.connector.start();
    }

    @Override // com.eltechs.axs.environmentService.EnvironmentComponent
    public void stop() {
        Assert.state(this.connector != null, "DirectSound server component not yet started.");
        resumePlayback();
        try {
            this.connector.stop();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.connector = null;
        try {
            this.directSoundBufferFactory.destroy();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.directSoundBufferFactory = null;
        this.connectionHandler = null;
        this.requestHandler = null;
    }

    public String getAddress() {
        return this.socketConf.getGuestPath();
    }

    public void suspendPlayback() {
        Assert.state(this.connector != null, "DirectSound server component not yet started.");
        this.requestHandler.suspendRequestProcessing();
        this.connectionHandler.forEachClient(new DirectSoundConnectionHandler.ClientCallback() { // from class: com.eltechs.axs.environmentService.components.DirectSoundServerComponent.1
            @Override // com.eltechs.axs.dsoundServer.DirectSoundConnectionHandler.ClientCallback
            public void apply(DirectSoundClient directSoundClient) {
                directSoundClient.suspendPlayback();
            }
        });
    }

    public void resumePlayback() {
        Assert.state(this.connector != null, "DirectSound server component not yet started.");
        this.connectionHandler.forEachClient(new DirectSoundConnectionHandler.ClientCallback() { // from class: com.eltechs.axs.environmentService.components.DirectSoundServerComponent.2
            @Override // com.eltechs.axs.dsoundServer.DirectSoundConnectionHandler.ClientCallback
            public void apply(DirectSoundClient directSoundClient) {
                directSoundClient.resumePlayback();
            }
        });
        this.requestHandler.resumeRequestProcessing();
    }
}
