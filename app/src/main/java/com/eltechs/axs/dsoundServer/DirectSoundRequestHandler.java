package com.eltechs.axs.dsoundServer;

import static com.eltechs.axs.dsoundServer.Opcodes.Attach;
import static com.eltechs.axs.dsoundServer.Opcodes.InitGlobalNotifier;
import static com.eltechs.axs.dsoundServer.Opcodes.Play;
import static com.eltechs.axs.dsoundServer.Opcodes.RecalcVolpan;
import static com.eltechs.axs.dsoundServer.Opcodes.SetCurrentPosition;
import static com.eltechs.axs.dsoundServer.Opcodes.SetNotifications;
import static com.eltechs.axs.dsoundServer.Opcodes.Stop;

import com.eltechs.axs.dsoundServer.impl.PlayFlags;
import com.eltechs.axs.helpers.Assert;
import com.eltechs.axs.proto.input.ProcessingResult;
import com.eltechs.axs.xconnectors.RequestHandler;
import com.eltechs.axs.xconnectors.XInputStream;
import com.eltechs.axs.xconnectors.XOutputStream;
import com.eltechs.axs.xconnectors.XStreamLock;
import com.eltechs.axs.xserver.impl.masks.Mask;
import java.io.IOException;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/* loaded from: classes.dex */
public class DirectSoundRequestHandler implements RequestHandler<DirectSoundClient> {
    private static final int HEADER_SIZE = 8;
    private static final int SIZE_OF_ATTACH_REQ = 4;
    private static final int SIZE_OF_INIT_NOTIFY_REQ = 0;
    private static final int SIZE_OF_INT = 4;
    private static final int SIZE_OF_PLAY_REQ = 4;
    private static final int SIZE_OF_RECALC_VOLPAN_REQ = 8;
    private static final int SIZE_OF_SET_CURRENT_POSITION_REQ = 4;
    private static final int SIZE_OF_STOP_REQ = 0;
    private final ReentrantReadWriteLock suspendLock = new ReentrantReadWriteLock(true);

    public void suspendRequestProcessing() {
        Assert.state(!this.suspendLock.isWriteLocked(), "suspendRequestProcessing() must not be called recursively.");
        this.suspendLock.writeLock().lock();
    }

    public void resumeRequestProcessing() {
        if (this.suspendLock.isWriteLocked()) {
            Assert.state(this.suspendLock.isWriteLockedByCurrentThread(), "resumeRequestProcessing() must be called by the thread that called suspendRequestProcessing()");
            this.suspendLock.writeLock().unlock();
        }
    }

    @Override // com.eltechs.axs.xconnectors.RequestHandler
    public ProcessingResult handleRequest(DirectSoundClient directSoundClient, XInputStream xInputStream, XOutputStream xOutputStream) throws IOException {
        ReentrantReadWriteLock.ReadLock readLock = this.suspendLock.readLock();
        try {
            readLock.lock();
            return handleRequestImpl(directSoundClient, xInputStream, xOutputStream);
        } finally {
            readLock.unlock();
        }
    }

    public ProcessingResult handleRequestImpl(DirectSoundClient directSoundClient, XInputStream xInputStream, XOutputStream xOutputStream) throws IOException {
        if (xInputStream.getAvailableBytesCount() < 8) {
            return ProcessingResult.INCOMPLETE_BUFFER;
        }
        int opcode = xInputStream.getInt();
        int len = xInputStream.getInt();
        if (xInputStream.getAvailableBytesCount() < len) {
            return ProcessingResult.INCOMPLETE_BUFFER;
        }
        if (opcode == InitGlobalNotifier) {
            if (len != 0) {
                return ProcessingResult.PROCESSED_KILL_CONNECTION;
            }
            return initGlobalNotifier(directSoundClient, xOutputStream);
        } else if (opcode == Attach) {
            if (len != SIZE_OF_ATTACH_REQ) {
                return ProcessingResult.PROCESSED_KILL_CONNECTION;
            }
            return attach(directSoundClient, xInputStream.getInt(), xOutputStream);
        } else if (!directSoundClient.isAttached()) {
            return ProcessingResult.PROCESSED_KILL_CONNECTION;
        } else {
            switch (opcode) {
                case Play:
                    if (len != SIZE_OF_PLAY_REQ) {
                        return ProcessingResult.PROCESSED_KILL_CONNECTION;
                    }
                    return play(directSoundClient, xInputStream.getInt(), xOutputStream);
                case Stop:
                    if (len != SIZE_OF_STOP_REQ) {
                        return ProcessingResult.PROCESSED_KILL_CONNECTION;
                    }
                    return stop(directSoundClient, xOutputStream);
                case SetCurrentPosition:
                    if (len != SIZE_OF_SET_CURRENT_POSITION_REQ) {
                        return ProcessingResult.PROCESSED_KILL_CONNECTION;
                    }
                    return setCurrentPosition(directSoundClient, xInputStream.getInt(), xOutputStream);
                case SetNotifications:
                    int i3 = xInputStream.getInt();
                    if (len != (i3 * 4 * 2) + 4) {
                        return ProcessingResult.PROCESSED_KILL_CONNECTION;
                    }
                    return setNotificationPositions(directSoundClient, i3, xInputStream, xOutputStream);
                case RecalcVolpan:
                    int i4 = xInputStream.getInt();
                    int i5 = xInputStream.getInt();
                    if (len != SIZE_OF_RECALC_VOLPAN_REQ) {
                        return ProcessingResult.PROCESSED_KILL_CONNECTION;
                    }
                    return recalcVolpan(directSoundClient, i4, i5, xOutputStream);
                default:
                    return ProcessingResult.PROCESSED_KILL_CONNECTION;
            }
        }
    }

    private ProcessingResult attach(DirectSoundClient directSoundClient, int i, XOutputStream xOutputStream) throws IOException {
        if (directSoundClient.isAttached()) {
            return ProcessingResult.PROCESSED_KILL_CONNECTION;
        }
        if (!directSoundClient.attach(i)) {
            return ProcessingResult.PROCESSED_KILL_CONNECTION;
        }
        try (XStreamLock ignored = xOutputStream.lock()) {
            xOutputStream.writeInt(0);
            return ProcessingResult.PROCESSED;
        }
    }

    private ProcessingResult play(DirectSoundClient directSoundClient, int i, XOutputStream xOutputStream) throws IOException {
        Mask<PlayFlags> create = Mask.create(PlayFlags.class, i);
        if (create == null) {
            return ProcessingResult.PROCESSED_KILL_CONNECTION;
        }
        directSoundClient.play(create);
        try (XStreamLock ignored = xOutputStream.lock()) {
            xOutputStream.writeInt(0);
            return ProcessingResult.PROCESSED;
        }
    }

    private ProcessingResult stop(DirectSoundClient directSoundClient, XOutputStream xOutputStream) throws IOException {
        directSoundClient.stop();
        try (XStreamLock ignored = xOutputStream.lock()) {
            xOutputStream.writeInt(0);
            return ProcessingResult.PROCESSED;
        }
    }

    private ProcessingResult recalcVolpan(DirectSoundClient directSoundClient, int i, int i2, XOutputStream xOutputStream) throws IOException {
        directSoundClient.recalcVolpan(i, i2);
        try (XStreamLock ignored = xOutputStream.lock()) {
            xOutputStream.writeInt(0);
            return ProcessingResult.PROCESSED;
        }
    }

    private ProcessingResult setCurrentPosition(DirectSoundClient directSoundClient, int position, XOutputStream xOutputStream) throws IOException {
        if (!directSoundClient.setCurrentPosition(position)) {
            return ProcessingResult.PROCESSED_KILL_CONNECTION;
        }
        try (XStreamLock ignored = xOutputStream.lock()) {
            xOutputStream.writeInt(0);
            return ProcessingResult.PROCESSED;
        }
    }

    private ProcessingResult setNotificationPositions(DirectSoundClient directSoundClient, int i, XInputStream xInputStream, XOutputStream xOutputStream) throws IOException {
        if (i != 0) {
            int[] iArr = new int[i];
            int[] iArr2 = new int[i];
            for (int i2 = 0; i2 < i; i2++) {
                iArr[i2] = xInputStream.getInt();
                iArr2[i2] = xInputStream.getInt();
            }
            directSoundClient.setNotificationPositions(iArr, iArr2);
        } else {
            directSoundClient.setNotificationPositions(null, null);
        }
        try (XStreamLock ignored = xOutputStream.lock()) {
            xOutputStream.writeInt(0);
            return ProcessingResult.PROCESSED;
        }
    }

    private ProcessingResult initGlobalNotifier(DirectSoundClient directSoundClient, XOutputStream xOutputStream) throws IOException {
        DirectSoundGlobalNotifier.createInstance(directSoundClient, xOutputStream);
        try (XStreamLock ignored = xOutputStream.lock()) {
            xOutputStream.writeInt(0);
            return ProcessingResult.PROCESSED;
        }
    }
}
