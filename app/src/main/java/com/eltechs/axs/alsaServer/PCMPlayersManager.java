package com.eltechs.axs.alsaServer;

import com.eltechs.axs.alsaServer.impl.PCMPlayer;
import com.eltechs.axs.alsaServer.impl.PCMPlayersFactory;
import java.util.ArrayList;
import java.util.Collection;

/* loaded from: classes.dex */
public class PCMPlayersManager {
    private PCMPlayersFactory pcmPlayersFactory;
    private Collection<PCMPlayer> tracks = new ArrayList<>();

    public PCMPlayersManager(PCMPlayersFactory pCMPlayersFactory) {
        this.pcmPlayersFactory = pCMPlayersFactory;
    }

    public PCMPlayer createPCMPlayer(int sampleRateInHz, int channels, ClientFormats clientFormats) {
        PCMPlayer create = this.pcmPlayersFactory.create(sampleRateInHz, channels, clientFormats);
        this.tracks.add(create);
        return create;
    }

    public void deletePCMPlayer(PCMPlayer pCMPlayer) {
        this.tracks.remove(pCMPlayer);
        pCMPlayer.stopAndReleaseResources();
    }
}
