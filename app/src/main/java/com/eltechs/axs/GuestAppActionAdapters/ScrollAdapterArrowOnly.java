package com.eltechs.axs.GuestAppActionAdapters;

import com.eltechs.axs.GuestAppActionAdapters.ScrollDirections;
import com.eltechs.axs.KeyCodesX;
import com.eltechs.axs.KeyEventReporter;
import java.util.EnumMap;
import java.util.Map;

/* loaded from: classes.dex */
public class ScrollAdapterArrowOnly implements SyncScrollAdapter {
    private static final Map<ScrollDirections.DirectionX, KeyCodesX> directionToKeyCodeX = new EnumMap(ScrollDirections.DirectionX.class);
    private static final Map<ScrollDirections.DirectionY, KeyCodesX> directionToKeyCodeY;
    private final KeyEventReporter ker;

    @Override // com.eltechs.axs.GuestAppActionAdapters.SyncScrollAdapter
    public void notifyStart() {
    }

    @Override // com.eltechs.axs.GuestAppActionAdapters.SyncScrollAdapter
    public void notifyStop() {
    }

    static {
        directionToKeyCodeX.put(ScrollDirections.DirectionX.LEFT, KeyCodesX.KEY_LEFT);
        directionToKeyCodeX.put(ScrollDirections.DirectionX.NONE, KeyCodesX.KEY_NONE);
        directionToKeyCodeX.put(ScrollDirections.DirectionX.RIGHT, KeyCodesX.KEY_RIGHT);
        directionToKeyCodeY = new EnumMap(ScrollDirections.DirectionY.class);
        directionToKeyCodeY.put(ScrollDirections.DirectionY.UP, KeyCodesX.KEY_UP);
        directionToKeyCodeY.put(ScrollDirections.DirectionY.NONE, KeyCodesX.KEY_NONE);
        directionToKeyCodeY.put(ScrollDirections.DirectionY.DOWN, KeyCodesX.KEY_DOWN);
    }

    public ScrollAdapterArrowOnly(KeyEventReporter keyEventReporter) {
        this.ker = keyEventReporter;
    }

    private void scrollImpl(KeyCodesX keyCodesX, int i) {
        if (keyCodesX == KeyCodesX.KEY_NONE) {
            return;
        }
        while (true) {
            int i2 = i - 1;
            if (i <= 0) {
                return;
            }
            this.ker.reportKeyPressReleaseNoDelay(keyCodesX);
            i = i2;
        }
    }

    @Override // com.eltechs.axs.GuestAppActionAdapters.SyncScrollAdapter
    public void scroll(ScrollDirections.DirectionX directionX, ScrollDirections.DirectionY directionY, int scrollTimes) {
        scrollImpl(directionToKeyCodeX.get(directionX), scrollTimes);
        scrollImpl(directionToKeyCodeY.get(directionY), scrollTimes);
    }
}
