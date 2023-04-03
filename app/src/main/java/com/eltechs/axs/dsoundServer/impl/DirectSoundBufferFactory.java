package com.eltechs.axs.dsoundServer.impl;

import com.eltechs.axs.sysvipc.AttachedSHMSegment;

/* loaded from: classes.dex */
public interface DirectSoundBufferFactory {
    DirectSoundBuffer createBuffer(AttachedSHMSegment attachedSHMSegment);
}
