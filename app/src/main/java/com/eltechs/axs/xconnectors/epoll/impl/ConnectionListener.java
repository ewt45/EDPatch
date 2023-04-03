package com.eltechs.axs.xconnectors.epoll.impl;

import com.eltechs.axs.annotations.UsedByNativeCode;
import com.eltechs.axs.helpers.Assert;
import java.io.IOException;

/* loaded from: classes.dex */
public class ConnectionListener {
    @UsedByNativeCode
    private final int fd;

    private native int acceptImpl();

    private native void closeImpl();

    private static native int createAbstractAfUnixSocket(String str);

    private static native int createAfUnixSocket(String str);

    private static native int createLoopbackInetSocket(int i);

    private static native boolean initialiseNativeParts();

    static {
        System.loadLibrary("xconnector-fairepoll");
        Assert.state(initialiseNativeParts(), "Managed and native parts of EpollProcessorThread do not match one another.");
    }

    private ConnectionListener(int i) {
        this.fd = i;
    }

    public static ConnectionListener forLoopbackInetAddress(int i) throws IOException {
        int createLoopbackInetSocket = createLoopbackInetSocket(i);
        if (createLoopbackInetSocket < 0) {
            throw new IOException(String.format("Failed to create an AF_INET socket listening on 127.0.0.1:%d; errno = %d.", Integer.valueOf(i), Integer.valueOf(-createLoopbackInetSocket)));
        }
        return new ConnectionListener(createLoopbackInetSocket);
    }

    public static ConnectionListener forAbstractAfUnixAddress(String str) throws IOException {
        int createAbstractAfUnixSocket = createAbstractAfUnixSocket(str);
        if (createAbstractAfUnixSocket < 0) {
            throw new IOException(String.format("Failed to create an AF_UNIX socket listening on '\\0%s'; errno = %d.", str, Integer.valueOf(-createAbstractAfUnixSocket)));
        }
        return new ConnectionListener(createAbstractAfUnixSocket);
    }

    public static ConnectionListener forAfUnixAddress(String str) throws IOException {
        int createAfUnixSocket = createAfUnixSocket(str);
        if (createAfUnixSocket < 0) {
            throw new IOException(String.format("Failed to create an AF_UNIX socket listening on %s; errno = %d.", str, Integer.valueOf(-createAfUnixSocket)));
        }
        return new ConnectionListener(createAfUnixSocket);
    }

    public int getFd() {
        return this.fd;
    }

    public int accept() {
        return acceptImpl();
    }

    public void close() {
        closeImpl();
    }
}