package com.eltechs.axs.dsoundServer;

import com.eltechs.axs.xconnectors.XOutputStream;
import com.eltechs.axs.xconnectors.XStreamLock;
import java.io.IOException;

/* loaded from: classes.dex */
public class DirectSoundGlobalNotifier {
    private static DirectSoundGlobalNotifier instance;
    private final DirectSoundClient client;
    private final XOutputStream outputStream;

    public static DirectSoundGlobalNotifier getInstance() {
        return instance;
    }

    public static void createInstance(DirectSoundClient directSoundClient, XOutputStream xOutputStream) {
        instance = new DirectSoundGlobalNotifier(directSoundClient, xOutputStream);
    }

    public static void handleClientDestroyed(DirectSoundClient directSoundClient) {
        if (instance == null || instance.client != directSoundClient) {
            return;
        }
        instance = null;
    }

    public DirectSoundGlobalNotifier(DirectSoundClient directSoundClient, XOutputStream xOutputStream) {
        this.client = directSoundClient;
        this.outputStream = xOutputStream;
    }

    public void notifyPositionReached(int i) throws IOException {
        XStreamLock lock = this.outputStream.lock();
        try {
            this.outputStream.writeInt(1);
            this.outputStream.writeInt(i);
            this.outputStream.flush();
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
}
