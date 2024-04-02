package com.eltechs.axs.alsaServer.impl;

import com.eltechs.axs.alsaServer.ClientFormats;

/* loaded from: classes.dex */
public interface PCMPlayersFactory {
    PCMPlayer create(int sampleRateInHz, int channels, ClientFormats clientFormats);
}
