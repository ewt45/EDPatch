package com.eltechs.axs.xserver.sysvipc;

import java.nio.MappedByteBuffer;

public interface AttachedSHMSegment {
    MappedByteBuffer getContent();
}
