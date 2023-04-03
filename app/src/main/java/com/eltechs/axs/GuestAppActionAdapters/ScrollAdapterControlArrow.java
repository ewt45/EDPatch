package com.eltechs.axs.GuestAppActionAdapters;

import com.eltechs.axs.GuestAppActionAdapters.ScrollDirections;
import com.eltechs.axs.KeyCodesX;
import com.eltechs.axs.KeyEventReporter;
import java.util.EnumMap;
import java.util.Map;

/* loaded from: classes.dex */
public class ScrollAdapterControlArrow implements SyncScrollAdapter {
    private static final Map<ScrollDirections.DirectionX, Map<ScrollDirections.DirectionY, KeyCodesX>> directionToKeyCode = new EnumMap(ScrollDirections.DirectionX.class);
    private final KeyEventReporter ker;

    @Override // com.eltechs.axs.GuestAppActionAdapters.SyncScrollAdapter
    public void notifyStart() {
    }

    @Override // com.eltechs.axs.GuestAppActionAdapters.SyncScrollAdapter
    public void notifyStop() {
    }

    static {
        EnumMap enumMap = new EnumMap(ScrollDirections.DirectionY.class);
        enumMap.put(ScrollDirections.DirectionY.UP,  KeyCodesX.KEY_HOME);
        enumMap.put( ScrollDirections.DirectionY.NONE,  KeyCodesX.KEY_LEFT);
        enumMap.put( ScrollDirections.DirectionY.DOWN,  KeyCodesX.KEY_END);
        directionToKeyCode.put(ScrollDirections.DirectionX.LEFT, enumMap);
        EnumMap enumMap2 = new EnumMap(ScrollDirections.DirectionY.class);
        enumMap2.put( ScrollDirections.DirectionY.UP,  KeyCodesX.KEY_UP);
        enumMap2.put( ScrollDirections.DirectionY.NONE,  KeyCodesX.KEY_NONE);
        enumMap2.put( ScrollDirections.DirectionY.DOWN,  KeyCodesX.KEY_DOWN);
        directionToKeyCode.put(ScrollDirections.DirectionX.NONE, enumMap2);
        EnumMap enumMap3 = new EnumMap(ScrollDirections.DirectionY.class);
        enumMap3.put( ScrollDirections.DirectionY.UP,  KeyCodesX.KEY_PRIOR);
        enumMap3.put( ScrollDirections.DirectionY.NONE,  KeyCodesX.KEY_RIGHT);
        enumMap3.put( ScrollDirections.DirectionY.DOWN,  KeyCodesX.KEY_NEXT);
        directionToKeyCode.put(ScrollDirections.DirectionX.RIGHT, enumMap3);
    }

    public ScrollAdapterControlArrow(KeyEventReporter keyEventReporter) {
        this.ker = keyEventReporter;
    }

    private void pressCtrl() {
        this.ker.reportKeysPress(KeyCodesX.KEY_CONTROL_RIGHT);
    }

    private void releaseCtrl() {
        this.ker.reportKeysRelease(KeyCodesX.KEY_CONTROL_RIGHT);
    }

    private void scrollImpl(KeyCodesX keyCodesX, int i) {
        if (keyCodesX == KeyCodesX.KEY_NONE) {
            return;
        }
        pressCtrl();
        while (true) {
            int i2 = i - 1;
            if (i > 0) {
                this.ker.reportKeys(keyCodesX);
                i = i2;
            } else {
                releaseCtrl();
                return;
            }
        }
    }

    @Override // com.eltechs.axs.GuestAppActionAdapters.SyncScrollAdapter
    public void scroll(ScrollDirections.DirectionX directionX, ScrollDirections.DirectionY directionY, int scrollTimes) {
        scrollImpl(directionToKeyCode.get(directionX).get(directionY), scrollTimes);
    }
}
