package com.eltechs.axs.xserver.impl.drawables.gl;

import java.lang.ref.PhantomReference;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.util.HashMap;
import java.util.Map;

/* loaded from: classes.dex */
public class GLDrawablesFinaliserThread extends Thread {
    private final Map<Reference<?>, Runnable> finalisationHandlers = new HashMap();
    private final ReferenceQueue<?> referenceQueue;

    /* JADX INFO: Access modifiers changed from: package-private */
    public GLDrawablesFinaliserThread(ReferenceQueue<?> referenceQueue) {
        this.referenceQueue = referenceQueue;
    }

    public void registerFinalisationHandler(Object obj, Runnable runnable) {
        PhantomReference phantomReference = new PhantomReference(obj, this.referenceQueue);
        synchronized (this.finalisationHandlers) {
            this.finalisationHandlers.put(phantomReference, runnable);
        }
    }

    @Override // java.lang.Thread, java.lang.Runnable
    public void run() {
        Runnable remove;
        while (true) {
            try {
                Reference<? extends Object> remove2 = this.referenceQueue.remove();
                synchronized (this.finalisationHandlers) {
                    remove = this.finalisationHandlers.remove(remove2);
                }
                remove.run();
            } catch (InterruptedException unused) {
            }
        }
    }
}
