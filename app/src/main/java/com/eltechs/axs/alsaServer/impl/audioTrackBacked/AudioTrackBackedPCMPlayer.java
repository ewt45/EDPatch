package com.eltechs.axs.alsaServer.impl.audioTrackBacked;

import static android.media.AudioTrack.PLAYSTATE_PAUSED;
import static android.media.AudioTrack.PLAYSTATE_PLAYING;

import android.media.AudioTrack;
import com.eltechs.axs.alsaServer.impl.PCMPlayer;

/* loaded from: classes.dex */
public class AudioTrackBackedPCMPlayer implements PCMPlayer {
    final AudioTrack audioTrack;
    private int framesWritten = 0;

    public AudioTrackBackedPCMPlayer(AudioTrack audioTrack) {
        this.audioTrack = audioTrack;
    }

    @Override // com.eltechs.axs.alsaServer.impl.PCMPlayer
    public void stopAndReleaseResources() {
        if (this.audioTrack.getPlayState() == PLAYSTATE_PLAYING) {
            this.audioTrack.pause();
        }
        if (this.audioTrack.getPlayState() == PLAYSTATE_PAUSED) {
            this.audioTrack.flush();
        }
        this.audioTrack.release();
    }

    @Override // com.eltechs.axs.alsaServer.impl.PCMPlayer
    public void startPlaying() {
        this.audioTrack.play();
    }

    @Override // com.eltechs.axs.alsaServer.impl.PCMPlayer
    public void pausePlaying() {
        this.audioTrack.pause();
    }

    @Override // com.eltechs.axs.alsaServer.impl.PCMPlayer
    public void drainBuffer() {
        this.audioTrack.stop();
    }

    @Override // com.eltechs.axs.alsaServer.impl.PCMPlayer
    public void flushBuffer() {
        this.audioTrack.flush();
        this.framesWritten = 0;
    }

    @Override // com.eltechs.axs.alsaServer.impl.PCMPlayer
    public int getPlayingPosition() {
        return this.framesWritten;
    }

    @Override // com.eltechs.axs.alsaServer.impl.PCMPlayer
    public void writeData(byte[] bArr, int off, int len) {
        this.audioTrack.write(bArr, off, len);
        this.framesWritten += len / this.audioTrack.getChannelCount();
    }

    @Override // com.eltechs.axs.alsaServer.impl.PCMPlayer
    public void writeData(short[] sArr, int off, int len) {
        this.audioTrack.write(sArr, off, len);
        this.framesWritten += len / this.audioTrack.getChannelCount();
    }
}
