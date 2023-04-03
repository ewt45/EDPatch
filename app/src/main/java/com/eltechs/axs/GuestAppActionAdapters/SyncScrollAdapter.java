package com.eltechs.axs.GuestAppActionAdapters;

import com.eltechs.axs.GuestAppActionAdapters.ScrollDirections;

/* loaded from: classes.dex */
public interface SyncScrollAdapter {
    void notifyStart();

    void notifyStop();

    void scroll(ScrollDirections.DirectionX directionX, ScrollDirections.DirectionY directionY, int scrollTimes);
}
