package com.eltechs.axs.xconnectors.epoll;

import com.eltechs.axs.helpers.Assert;
import com.eltechs.axs.proto.input.ConnectionHandler;
import com.eltechs.axs.xconnectors.RequestHandler;
import com.eltechs.axs.xconnectors.epoll.impl.BufferSizeConfiguration;
import com.eltechs.axs.xconnectors.epoll.impl.ConnectionListener;
import com.eltechs.axs.xconnectors.epoll.impl.EpollProcessorThread;
import java.io.IOException;

/* loaded from: classes.dex */
public class FairEpollConnector<Context> {
    private int batchSize = 1;
    private final BufferSizeConfiguration bufferSizeConfiguration;
    private final ConnectionHandler<Context> connectionHandler;
    private final ConnectionListenerFactory connectionListenerFactory;
    private transient EpollProcessorThread processorThread;
    private final RequestHandler<Context> requestHandler;

    /* loaded from: classes.dex */
    public interface ConnectionListenerFactory {
        ConnectionListener createConnectionListener() throws IOException;
    }

    private FairEpollConnector(ConnectionListenerFactory connectionListenerFactory, ConnectionHandler<Context> connectionHandler, RequestHandler<Context> requestHandler, BufferSizeConfiguration bufferSizeConfiguration) {
        this.connectionListenerFactory = connectionListenerFactory;
        this.connectionHandler = connectionHandler;
        this.requestHandler = requestHandler;
        this.bufferSizeConfiguration = bufferSizeConfiguration;
    }

    public static <Context> FairEpollConnector<Context> listenOnSpecifiedUnixSocket(final UnixSocketConfiguration unixSocketConfiguration, ConnectionHandler<Context> connectionHandler, RequestHandler<Context> requestHandler) throws IOException {
        ConnectionListenerFactory connectionListenerFactory;
        if (unixSocketConfiguration.isAbstract()) {
            connectionListenerFactory = new ConnectionListenerFactory() { // from class: com.eltechs.axs.xconnectors.epoll.FairEpollConnector.1
                @Override // com.eltechs.axs.xconnectors.epoll.FairEpollConnector.ConnectionListenerFactory
                public ConnectionListener createConnectionListener() throws IOException {
                    return ConnectionListener.forAbstractAfUnixAddress(unixSocketConfiguration.getGuestPath());
                }
            };
        } else {
            connectionListenerFactory = new ConnectionListenerFactory() { // from class: com.eltechs.axs.xconnectors.epoll.FairEpollConnector.2
                @Override // com.eltechs.axs.xconnectors.epoll.FairEpollConnector.ConnectionListenerFactory
                public ConnectionListener createConnectionListener() throws IOException {
                    return ConnectionListener.forAfUnixAddress(unixSocketConfiguration.getHostPath());
                }
            };
        }
        return new FairEpollConnector<>(connectionListenerFactory, connectionHandler, requestHandler, BufferSizeConfiguration.createDefaultConfiguration());
    }

    public static <Context> FairEpollConnector<Context> listenOnLoopbackInetAddress(final int i, ConnectionHandler<Context> connectionHandler, RequestHandler<Context> requestHandler) throws IOException {
        return new FairEpollConnector<>(new ConnectionListenerFactory() { // from class: com.eltechs.axs.xconnectors.epoll.FairEpollConnector.3
            @Override // com.eltechs.axs.xconnectors.epoll.FairEpollConnector.ConnectionListenerFactory
            public ConnectionListener createConnectionListener() throws IOException {
                return ConnectionListener.forLoopbackInetAddress(i);
            }
        }, connectionHandler, requestHandler, BufferSizeConfiguration.createDefaultConfiguration());
    }

    public void start() throws IOException {
        Assert.state(this.processorThread == null, "The connector is already running.");
        this.processorThread = new EpollProcessorThread(this.connectionListenerFactory.createConnectionListener(), this.connectionHandler, this.requestHandler, this.bufferSizeConfiguration, this.batchSize);
        this.processorThread.startProcessing();
    }

    public void stop() throws IOException {
        Assert.state(this.processorThread != null, "The connector is not yet running.");
        this.processorThread.stopProcessing();
        while (this.processorThread.isAlive()) {
            try {
                this.processorThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        this.processorThread = null;
    }

    public void setInitialInputBufferCapacity(int i) {
        this.bufferSizeConfiguration.setInitialInputBufferCapacity(i);
    }

    public void setInitialOutputBufferCapacity(int i) {
        this.bufferSizeConfiguration.setInitialOutputBufferCapacity(i);
    }

    public void setOutputBufferSizeLimit(int i) {
        this.bufferSizeConfiguration.setOutputBufferSizeLimit(i);
    }

    public void setInputBufferSizeHardLimit(int i) {
        this.bufferSizeConfiguration.setInputBufferSizeHardLimit(i);
    }

    public void setOutputBufferSizeHardLimit(int i) {
        this.bufferSizeConfiguration.setOutputBufferSizeHardLimit(i);
    }

    public void setBatchSize(int i) {
        Assert.isTrue(i > 0, "Batch size must a positive integer.");
        this.batchSize = i;
    }
}