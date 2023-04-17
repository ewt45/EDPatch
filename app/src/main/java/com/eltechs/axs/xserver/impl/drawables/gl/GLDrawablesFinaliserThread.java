package com.eltechs.axs.xserver.impl.drawables.gl;

import android.util.Log;

import com.eltechs.axs.helpers.Assert;
import com.eltechs.axs.xserver.Drawable;

import java.lang.ref.PhantomReference;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.util.HashMap;
import java.util.Map;

/* loaded from: classes.dex */
public class GLDrawablesFinaliserThread extends Thread {
    private final Map<Reference<Drawable>, Runnable> finalisationHandlers = new HashMap<>();
    private final ReferenceQueue<Drawable> referenceQueue;

    /* JADX INFO: Access modifiers changed from: package-private */
    GLDrawablesFinaliserThread(ReferenceQueue<Drawable> referenceQueue) {
        this.referenceQueue = referenceQueue;
    }

    public void registerFinalisationHandler(Drawable obj, Runnable runnable) {
        PhantomReference<Drawable> phantomReference = new PhantomReference<>(obj, this.referenceQueue);
        synchronized (this.finalisationHandlers) {
            this.finalisationHandlers.put(phantomReference, runnable);
        }
    }

    @Override // java.lang.Thread, java.lang.Runnable
    public void run() {
        while (true) {
            Log.d("TAG", "run: 尝试删除drawable？");
            Reference<? extends Drawable> drawableRef;
            Runnable destroyer;
            try {
                drawableRef = this.referenceQueue.remove();
                synchronized (this.finalisationHandlers) {
                    destroyer = this.finalisationHandlers.remove(drawableRef);
                    Assert.isTrue(destroyer != null);
                    destroyer.run();

                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


//        while (true) {
//            try {
//                Reference<?> drawableRef = this.referenceQueue.remove();
//                Runnable destroyer;
//                synchronized (this.finalisationHandlers) {
//                    destroyer = this.finalisationHandlers.remove(drawableRef);
//                }
//                assert destroyer != null;
//                destroyer.run();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
    }

}