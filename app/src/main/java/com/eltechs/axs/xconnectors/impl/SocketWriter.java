package com.eltechs.axs.xconnectors.impl;

import java.io.IOException;
import java.nio.ByteBuffer;

/* loaded from: classes.dex */
public interface SocketWriter {
    int write(ByteBuffer byteBuffer) throws IOException;
}