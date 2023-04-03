package com.eltechs.axs.alsaServer.impl;

/* loaded from: classes.dex */
public interface PCMPlayer {
    void drainBuffer();

    void flushBuffer();

    int getPlayingPosition();

    void pausePlaying();

    void startPlaying();

    void stopAndReleaseResources();

    void writeData(byte[] bArr, int i, int i2);

    void writeData(short[] sArr, int i, int i2);
}
