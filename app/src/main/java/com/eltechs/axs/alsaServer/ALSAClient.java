package com.eltechs.axs.alsaServer;

import com.eltechs.axs.alsaServer.impl.PCMPlayer;
import com.eltechs.axs.helpers.ReluctantlyGarbageCollectedArrays;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/* loaded from: classes.dex */
public class ALSAClient {
    public static final int MAX_CHANNELS = 2;
    public static final int MAX_RATE = 48000;
    public static final int MIN_CHANNELS = 1;
    public static final int MIN_RATE = 8000;
    private final PCMPlayersManager pcmPlayersManager;
    private ClientFormats format = ClientFormats.U8;
    private int channels = MIN_CHANNELS;
    private int rate = MIN_RATE;
    private PCMPlayer pcmPlayer = null;
    private final ReluctantlyGarbageCollectedArrays arrays = new ReluctantlyGarbageCollectedArrays();

    public ALSAClient(PCMPlayersManager pCMPlayersManager) {
        this.pcmPlayersManager = pCMPlayersManager;
    }

    public void reset() {
        if (this.pcmPlayer != null) {
            this.pcmPlayersManager.deletePCMPlayer(this.pcmPlayer);
            this.pcmPlayer = null;
        }
    }

    private void prepareAudioTrack() {
        if (this.pcmPlayer == null) {
            this.pcmPlayer = this.pcmPlayersManager.createPCMPlayer(this.rate, this.channels, this.format);
        }
    }

    public void start() {
        prepareAudioTrack();
        this.pcmPlayer.startPlaying();
    }

    public void stop() {
        if (this.pcmPlayer != null) {
            this.pcmPlayer.pausePlaying();
            this.pcmPlayer.flushBuffer();
        }
    }

    public void drain() {
        if (this.pcmPlayer != null) {
            this.pcmPlayer.drainBuffer();
        }
    }

    public void writeDataToTrack(ByteBuffer byteBuffer, int length) {
        prepareAudioTrack();
        if (this.pcmPlayer != null) {
            if (this.format == ClientFormats.U8) {
                byte[] byteArray = this.arrays.getByteArray(length);
                byteBuffer.get(byteArray, 0, length);
                this.pcmPlayer.writeData(byteArray, 0, length);
                return;
            }
            if (this.format == ClientFormats.S16BE) {
                byteBuffer.order(ByteOrder.BIG_ENDIAN);
            } else {
                byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
            }
            int shortLen = length / 2;
            short[] shortArray = this.arrays.getShortArray(shortLen);
            byteBuffer.asShortBuffer().get(shortArray, 0, shortLen);
            this.pcmPlayer.writeData(shortArray, 0, shortLen);
        }
    }

    public int pointer() {
        if (this.pcmPlayer != null) {
            return this.pcmPlayer.getPlayingPosition();
        }
        return 0;
    }

    public boolean setFormat(int i) {
        if (!ClientFormats.checkFormat(i)) {
            return false;
        }
        this.format = ClientFormats.values()[i];
        return true;
    }

    public boolean setChannels(int channels) {
        if (channels < MIN_CHANNELS || channels > MAX_CHANNELS) {
            return false;
        }
        this.channels = channels;
        return true;
    }

    public boolean setRate(int rate) {
        if (rate < MIN_RATE || rate > MAX_RATE) {
            return false;
        }
        this.rate = rate;
        return true;
    }
}
