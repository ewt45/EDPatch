package com.eltechs.axs.alsaServer.impl;

/* loaded from: classes.dex */
public interface PCMPlayer {
    void drainBuffer();

    void flushBuffer();

    int getPlayingPosition();

    void pausePlaying();

    void startPlaying();

    void stopAndReleaseResources();

    void writeData(byte[] bArr, int off, int len);

    void writeData(short[] sArr, int off, int len);
}
