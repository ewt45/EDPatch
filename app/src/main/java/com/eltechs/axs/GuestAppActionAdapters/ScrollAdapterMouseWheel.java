package com.eltechs.axs.GuestAppActionAdapters;

import com.eltechs.axs.GuestAppActionAdapters.ScrollDirections;
import com.eltechs.axs.PointerEventReporter;
import java.util.EnumMap;
import java.util.Map;

/* loaded from: classes.dex */
public class ScrollAdapterMouseWheel implements SyncScrollAdapter {
    private static final Map<ScrollDirections.DirectionY, Integer> directionToButtonCodeY = new EnumMap(ScrollDirections.DirectionY.class);
    private final PointerEventReporter per;

    @Override // com.eltechs.axs.GuestAppActionAdapters.SyncScrollAdapter
    public void notifyStart() {
    }

    @Override // com.eltechs.axs.GuestAppActionAdapters.SyncScrollAdapter
    public void notifyStop() {
    }

    static {
        directionToButtonCodeY.put(ScrollDirections.DirectionY.UP, 4);
        directionToButtonCodeY.put(ScrollDirections.DirectionY.NONE, 0);
        directionToButtonCodeY.put(ScrollDirections.DirectionY.DOWN, 5);
    }

    public ScrollAdapterMouseWheel(PointerEventReporter pointerEventReporter) {
        this.per = pointerEventReporter;
    }

    private void scrollImpl(int i, int i2) {
        if (i == 0) {
            return;
        }
        while (true) {
            int i3 = i2 - 1;
            if (i2 <= 0) {
                return;
            }
            this.per.buttonPressed(i);
            this.per.buttonReleased(i);
            i2 = i3;
        }
    }

    @Override // com.eltechs.axs.GuestAppActionAdapters.SyncScrollAdapter
    public void scroll(ScrollDirections.DirectionX directionX, ScrollDirections.DirectionY directionY, int i) {
        scrollImpl(directionToButtonCodeY.get(directionY).intValue(), i);
    }
}