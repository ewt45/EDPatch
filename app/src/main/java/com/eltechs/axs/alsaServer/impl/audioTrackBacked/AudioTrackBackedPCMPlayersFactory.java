package com.eltechs.axs.alsaServer.impl.audioTrackBacked;

import static android.media.AudioFormat.CHANNEL_OUT_MONO;
import static android.media.AudioFormat.CHANNEL_OUT_STEREO;
import static android.media.AudioFormat.ENCODING_PCM_16BIT;
import static android.media.AudioFormat.ENCODING_PCM_8BIT;
import static android.media.AudioManager.STREAM_MUSIC;
import static android.media.AudioTrack.MODE_STREAM;
import static android.media.AudioTrack.STATE_INITIALIZED;

import android.media.AudioManager;
import android.media.AudioTrack;
import com.eltechs.axs.alsaServer.ClientFormats;
import com.eltechs.axs.alsaServer.impl.PCMPlayer;
import com.eltechs.axs.alsaServer.impl.PCMPlayersFactory;
import com.eltechs.axs.helpers.Assert;

/* loaded from: classes.dex */
public class AudioTrackBackedPCMPlayersFactory implements PCMPlayersFactory {
    @Override // com.eltechs.axs.alsaServer.impl.PCMPlayersFactory
    public PCMPlayer create(int sampleRateInHz, int channels, ClientFormats clientFormats) {
        boolean initialzedSuccess = true;
        int channelConfig = channels == 1 ? CHANNEL_OUT_MONO : CHANNEL_OUT_STEREO;
        int audioFormat = clientFormats == ClientFormats.U8 ? ENCODING_PCM_8BIT : ENCODING_PCM_16BIT;

        AudioTrack audioTrack = new AudioTrack(
                STREAM_MUSIC,
                sampleRateInHz,
                channelConfig,
                audioFormat,
                AudioTrack.getMinBufferSize(sampleRateInHz, channelConfig, audioFormat),
                MODE_STREAM);
        if (audioTrack.getState() != STATE_INITIALIZED) {
            initialzedSuccess = false;
        }
        Assert.state(initialzedSuccess);
        return new AudioTrackBackedPCMPlayer(audioTrack);
    }
}
