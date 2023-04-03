package com.eltechs.axs.xconnectors.impl;

import java.io.IOException;
import java.nio.ByteBuffer;

/* loaded from: classes.dex */
public interface SocketReader {
    int read(ByteBuffer byteBuffer) throws IOException;
}