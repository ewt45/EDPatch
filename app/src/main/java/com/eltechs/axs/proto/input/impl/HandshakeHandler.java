package com.eltechs.axs.proto.input.impl;

import com.eltechs.axs.proto.input.ProcessingResult;
import com.eltechs.axs.proto.output.PODWriter;
import com.eltechs.axs.proto.output.replies.AuthDenial;
import com.eltechs.axs.proto.output.replies.ServerInfo;
import com.eltechs.axs.xconnectors.XInputStream;
import com.eltechs.axs.xconnectors.XOutputStream;
import com.eltechs.axs.xconnectors.XStreamLock;
import com.eltechs.axs.xserver.IdInterval;
import com.eltechs.axs.xserver.LocksManager;
import com.eltechs.axs.xserver.XServer;
import com.eltechs.axs.xserver.client.XClient;
import java.io.IOException;
import java.nio.ByteOrder;

/* loaded from: classes.dex */
public class HandshakeHandler {
    private static final byte LSB_MARKER = 108;
    private static final int MINIMAL_AUTH_REQUEST_LENGTH = 12;
    private static final byte MSB_MARKER = 66;
    private final XServer target;

    public HandshakeHandler(XServer xServer) {
        this.target = xServer;
    }

    public ProcessingResult handleAuthRequest(XClient xClient, XInputStream xInputStream, XOutputStream xOutputStream) throws IOException {
        if (xInputStream.getAvailableBytesCount() < 12) {
            return ProcessingResult.INCOMPLETE_BUFFER;
        }
        byte b = xInputStream.getByte();
        if (b == 66) {
            xInputStream.setByteOrder(ByteOrder.BIG_ENDIAN);
            xOutputStream.setByteOrder(ByteOrder.BIG_ENDIAN);
        } else if (b == 108) {
            xInputStream.setByteOrder(ByteOrder.LITTLE_ENDIAN);
            xOutputStream.setByteOrder(ByteOrder.LITTLE_ENDIAN);
        } else {
            return denyAuthentication(xOutputStream, "Byte order marker is invalid.");
        }
        xInputStream.getByte();
        short s = xInputStream.getShort();
        xInputStream.getShort();
        if (s != 11) {
            return denyAuthentication(xOutputStream, "Unsupported major X protocol version");
        }
        if (xClient.getIdInterval() == null) {
            return denyAuthentication(xOutputStream, "Too many connections.");
        }
        short s2 = xInputStream.getShort();
        short s3 = xInputStream.getShort();
        xInputStream.getShort();
        int roundUpLength4 = ProtoHelpers.roundUpLength4(s2) + ProtoHelpers.roundUpLength4(s3);
        if (xInputStream.getAvailableBytesCount() < roundUpLength4) {
            return ProcessingResult.INCOMPLETE_BUFFER;
        }
        xInputStream.get(new byte[roundUpLength4]);
        sendServerInformation(xOutputStream, xClient.getIdInterval());
        xClient.setAuthenticated(true);
        return ProcessingResult.PROCESSED;
    }

    private void sendServerInformation(XOutputStream xOutputStream, IdInterval idInterval) throws IOException {
        LocksManager.XLock lock = this.target.getLocksManager().lock(new LocksManager.Subsystem[]{LocksManager.Subsystem.WINDOWS_MANAGER, LocksManager.Subsystem.DRAWABLES_MANAGER});
        try {
            XStreamLock lock2 = xOutputStream.lock();
            PODWriter.write(xOutputStream, new ServerInfo(this.target, idInterval));
            if (lock2 != null) {
                lock2.close();
            }
            if (lock == null) {
                return;
            }
            lock.close();
        } catch (Throwable th) {
            try {
                throw th;
            } catch (Throwable th2) {
                if (lock != null) {
                    if (th != null) {
                        try {
                            lock.close();
                        } catch (Throwable th3) {
                            th.addSuppressed(th3);
                        }
                    } else {
                        lock.close();
                    }
                }
                throw th2;
            }
        }
    }

    private ProcessingResult denyAuthentication(XOutputStream xOutputStream, String str) throws IOException {
        XStreamLock lock = xOutputStream.lock();
        try {
            PODWriter.write(xOutputStream, new AuthDenial(str));
            if (lock != null) {
                lock.close();
            }
            return ProcessingResult.PROCESSED_KILL_CONNECTION;
        } catch (Throwable th) {
            try {
                throw th;
            } catch (Throwable th2) {
                if (lock != null) {
                    if (th != null) {
                        try {
                            lock.close();
                        } catch (Throwable th3) {
                            th.addSuppressed(th3);
                        }
                    } else {
                        lock.close();
                    }
                }
                throw th2;
            }
        }
    }
}
