package com.eltechs.axs.dsoundServer;

import com.eltechs.axs.dsoundServer.impl.DirectSoundBufferFactory;
import com.eltechs.axs.proto.input.ConnectionHandler;
import com.eltechs.axs.sysvipc.SHMEngine;
import com.eltechs.axs.xconnectors.XInputStream;
import com.eltechs.axs.xconnectors.XOutputStream;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Collection;

/* loaded from: classes.dex */
public class DirectSoundConnectionHandler implements ConnectionHandler<DirectSoundClient> {
    private final Collection<DirectSoundClient> clients = new ArrayList<>();
    private final DirectSoundBufferFactory directSoundBufferFactory;
    private final SHMEngine shmEngine;

    /* loaded from: classes.dex */
    public interface ClientCallback {
        void apply(DirectSoundClient directSoundClient);
    }

    public DirectSoundConnectionHandler(SHMEngine sHMEngine, DirectSoundBufferFactory directSoundBufferFactory) {
        this.shmEngine = sHMEngine;
        this.directSoundBufferFactory = directSoundBufferFactory;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // com.eltechs.axs.proto.input.ConnectionHandler
    /* renamed from: handleNewConnection */
    public synchronized DirectSoundClient handleNewConnection(XInputStream xInputStream, XOutputStream xOutputStream) {
        DirectSoundClient directSoundClient;
        xInputStream.setByteOrder(ByteOrder.LITTLE_ENDIAN);
        xOutputStream.setByteOrder(ByteOrder.LITTLE_ENDIAN);
        directSoundClient = new DirectSoundClient(this.shmEngine, this.directSoundBufferFactory);
        this.clients.add(directSoundClient);
        return directSoundClient;
    }

    @Override // com.eltechs.axs.proto.input.ConnectionHandler
    public synchronized void handleConnectionShutdown(DirectSoundClient directSoundClient) {
        this.clients.remove(directSoundClient);
        directSoundClient.destroy();
    }

    public synchronized void forEachClient(ClientCallback clientCallback) {
        for (DirectSoundClient directSoundClient : this.clients) {
            clientCallback.apply(directSoundClient);
        }
    }
}
