package com.eltechs.axs.configuration.startup.actions;

import com.eltechs.axs.activities.VideoPlayerActivity;

/* loaded from: classes.dex */
public class PlayVideo<StateClass> extends SimpleInteractiveStartupActionBase<StateClass> {
    private final int videoId;

    public PlayVideo(int videoId) {
        this.videoId = videoId;
    }

    @Override // com.eltechs.axs.configuration.startup.StartupAction
    public void execute() {
        requestUserInput(VideoPlayerActivity.class, this.videoId);
    }

    @Override // com.eltechs.axs.configuration.startup.actions.SimpleInteractiveStartupActionBase
    public void userInteractionFinished() {
        sendDone();
    }

    @Override // com.eltechs.axs.configuration.startup.actions.SimpleInteractiveStartupActionBase, com.eltechs.axs.configuration.startup.InteractiveStartupAction
    public void userInteractionCanceled() {
        sendDone();
    }
}