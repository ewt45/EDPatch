package com.eltechs.axs.GuestAppActionAdapters;

import com.eltechs.axs.GuestAppActionAdapters.ScrollDirections;

/* loaded from: classes.dex */
public interface AsyncScrollAdapter {
    void notifyStart();

    void notifyStop();

    void setScrolling(ScrollDirections.DirectionX directionX, ScrollDirections.DirectionY directionY);
}
