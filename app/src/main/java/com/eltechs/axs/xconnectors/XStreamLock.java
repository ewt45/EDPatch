package com.eltechs.axs.xconnectors;

import java.io.IOException;

/* loaded from: classes.dex */
public interface XStreamLock extends AutoCloseable {
    @Override // java.lang.AutoCloseable
    void close() throws IOException;
}