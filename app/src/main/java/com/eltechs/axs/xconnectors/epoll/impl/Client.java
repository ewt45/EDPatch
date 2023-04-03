package com.eltechs.axs.xconnectors.epoll.impl;

import com.eltechs.axs.annotations.UsedByNativeCode;
import com.eltechs.axs.xconnectors.impl.XInputStreamImpl;
import com.eltechs.axs.xconnectors.impl.XOutputStreamImpl;

@UsedByNativeCode
/* loaded from: classes.dex */
public class Client<Context> {
    private final SocketWrapper clientSocket;
    private final Context context;
    private final XInputStreamImpl inputStream;
    private boolean isQueuedForProcessingBufferedMessages;
    private final XOutputStreamImpl outputStream;

    public Client(Context context, SocketWrapper socketWrapper, XInputStreamImpl xInputStreamImpl, XOutputStreamImpl xOutputStreamImpl) {
        this.context = context;
        this.clientSocket = socketWrapper;
        this.inputStream = xInputStreamImpl;
        this.outputStream = xOutputStreamImpl;
    }

    public int getFd() {
        return this.clientSocket.getFd();
    }

    public void closeConnection() {
        this.clientSocket.close();
    }

    public Context getContext() {
        return this.context;
    }

    public XInputStreamImpl getInputStream() {
        return this.inputStream;
    }

    public XOutputStreamImpl getOutputStream() {
        return this.outputStream;
    }

    public boolean isQueuedForProcessingBufferedMessages() {
        return this.isQueuedForProcessingBufferedMessages;
    }

    public void setQueuedForProcessingBufferedMessages(boolean z) {
        this.isQueuedForProcessingBufferedMessages = z;
    }
}