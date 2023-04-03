package com.eltechs.axs.dsoundServer;

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
        int i = xInputStream.getInt();
        int i2 = xInputStream.getInt();
        if (xInputStream.getAvailableBytesCount() < i2) {
            return ProcessingResult.INCOMPLETE_BUFFER;
        }
        if (i == 255) {
            if (i2 != 0) {
                return ProcessingResult.PROCESSED_KILL_CONNECTION;
            }
            return initGlobalNotifier(directSoundClient, xOutputStream);
        } else if (i == 0) {
            if (i2 != 4) {
                return ProcessingResult.PROCESSED_KILL_CONNECTION;
            }
            return attach(directSoundClient, xInputStream.getInt(), xOutputStream);
        } else if (!directSoundClient.isAttached()) {
            return ProcessingResult.PROCESSED_KILL_CONNECTION;
        } else {
            switch (i) {
                case 1:
                    if (i2 != 4) {
                        return ProcessingResult.PROCESSED_KILL_CONNECTION;
                    }
                    return play(directSoundClient, xInputStream.getInt(), xOutputStream);
                case 2:
                    if (i2 != 0) {
                        return ProcessingResult.PROCESSED_KILL_CONNECTION;
                    }
                    return stop(directSoundClient, xOutputStream);
                case 3:
                    if (i2 != 4) {
                        return ProcessingResult.PROCESSED_KILL_CONNECTION;
                    }
                    return setCurrentPosition(directSoundClient, xInputStream.getInt(), xOutputStream);
                case 4:
                    int i3 = xInputStream.getInt();
                    if (i2 != (i3 * 4 * 2) + 4) {
                        return ProcessingResult.PROCESSED_KILL_CONNECTION;
                    }
                    return setNotificationPositions(directSoundClient, i3, xInputStream, xOutputStream);
                case 5:
                    int i4 = xInputStream.getInt();
                    int i5 = xInputStream.getInt();
                    if (i2 != 8) {
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
        XStreamLock lock = xOutputStream.lock();
        try {
            xOutputStream.writeInt(0);
            if (lock != null) {
                lock.close();
            }
            return ProcessingResult.PROCESSED;
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

    private ProcessingResult play(DirectSoundClient directSoundClient, int i, XOutputStream xOutputStream) throws IOException {
        Mask<PlayFlags> create = Mask.create(PlayFlags.class, i);
        if (create == null) {
            return ProcessingResult.PROCESSED_KILL_CONNECTION;
        }
        directSoundClient.play(create);
        XStreamLock lock = xOutputStream.lock();
        try {
            xOutputStream.writeInt(0);
            if (lock != null) {
                lock.close();
            }
            return ProcessingResult.PROCESSED;
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

    private ProcessingResult stop(DirectSoundClient directSoundClient, XOutputStream xOutputStream) throws IOException {
        directSoundClient.stop();
        XStreamLock lock = xOutputStream.lock();
        try {
            xOutputStream.writeInt(0);
            if (lock != null) {
                lock.close();
            }
            return ProcessingResult.PROCESSED;
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

    private ProcessingResult recalcVolpan(DirectSoundClient directSoundClient, int i, int i2, XOutputStream xOutputStream) throws IOException {
        directSoundClient.recalcVolpan(i, i2);
        XStreamLock lock = xOutputStream.lock();
        try {
            xOutputStream.writeInt(0);
            if (lock != null) {
                lock.close();
            }
            return ProcessingResult.PROCESSED;
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

    private ProcessingResult setCurrentPosition(DirectSoundClient directSoundClient, int i, XOutputStream xOutputStream) throws IOException {
        if (!directSoundClient.setCurrentPosition(i)) {
            return ProcessingResult.PROCESSED_KILL_CONNECTION;
        }
        XStreamLock lock = xOutputStream.lock();
        try {
            xOutputStream.writeInt(0);
            if (lock != null) {
                lock.close();
            }
            return ProcessingResult.PROCESSED;
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
        XStreamLock lock = xOutputStream.lock();
        try {
            xOutputStream.writeInt(0);
            if (lock != null) {
                lock.close();
            }
            return ProcessingResult.PROCESSED;
        } finally {
        }
    }

    private ProcessingResult initGlobalNotifier(DirectSoundClient directSoundClient, XOutputStream xOutputStream) throws IOException {
        DirectSoundGlobalNotifier.createInstance(directSoundClient, xOutputStream);
        XStreamLock lock = xOutputStream.lock();
        try {
            xOutputStream.writeInt(0);
            if (lock != null) {
                lock.close();
            }
            return ProcessingResult.PROCESSED;
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
