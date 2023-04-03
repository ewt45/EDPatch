package com.eltechs.axs.xconnectors.epoll.impl;

import com.eltechs.axs.annotations.UsedByNativeCode;
import com.eltechs.axs.helpers.Assert;
import com.eltechs.axs.xconnectors.impl.SocketReader;
import com.eltechs.axs.xconnectors.impl.SocketWriter;
import java.io.IOException;
import java.nio.ByteBuffer;

/* loaded from: classes.dex */
public class SocketWrapper implements SocketReader, SocketWriter {
    @UsedByNativeCode
    private final int fd;

    private native void closeImpl();

    private static native boolean initialiseNativeParts();

    private native int readImpl(ByteBuffer byteBuffer, int i, int i2);

    private native int writeImpl(ByteBuffer byteBuffer, int i, int i2);

    static {
        System.loadLibrary("xconnector-fairepoll");
        Assert.state(initialiseNativeParts(), "Managed and native parts of EpollProcessorThread do not match one another.");
    }

    public SocketWrapper(int i) {
        this.fd = i;
    }

    public int getFd() {
        return this.fd;
    }

    @Override // com.eltechs.axs.xconnectors.impl.SocketReader
    public int read(ByteBuffer byteBuffer) throws IOException {
        int position = byteBuffer.position();
        int readImpl = readImpl(byteBuffer, position, byteBuffer.remaining());
        if (readImpl > 0) {
            byteBuffer.position(position + readImpl);
            return readImpl;
        } else if (readImpl < 0) {
            throw new IOException(String.format("recvmsg() has failed; errno = %d\n", Integer.valueOf(-readImpl)));
        } else {
            return -1;
        }
    }

    @Override // com.eltechs.axs.xconnectors.impl.SocketWriter
    public int write(ByteBuffer byteBuffer) throws IOException {
        int position = byteBuffer.position();
        int writeImpl = writeImpl(byteBuffer, 0, byteBuffer.remaining());
        if (writeImpl > 0) {
            byteBuffer.position(position + writeImpl);
        }
        if (writeImpl < 0) {
            throw new IOException(String.format("sendmsg() has failed; errno = %d\n", Integer.valueOf(-writeImpl)));
        }
        return writeImpl;
    }

    public void close() {
        closeImpl();
    }
}