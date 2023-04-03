package com.eltechs.axs.GuestAppActionAdapters;

import com.eltechs.axs.GuestAppActionAdapters.ScrollDirections;
import com.eltechs.axs.PointerEventReporter;
import java.util.EnumMap;
import java.util.Map;

/* loaded from: classes.dex */
public class ScrollAdapterMouseWheel implements SyncScrollAdapter {
    private static final Map<ScrollDirections.DirectionY, Integer> directionToButtonCodeY = new EnumMap<>(ScrollDirections.DirectionY.class);
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

    private void scrollImpl(int buttonCode, int scrollTimes) {
        if (buttonCode == 0) {
            return;
        }
        while (true) {
            scrollTimes --;
            if (scrollTimes <= 0) {
                return;
            }
            this.per.buttonPressed(buttonCode);
            this.per.buttonReleased(buttonCode);
        }
    }

    @Override // com.eltechs.axs.GuestAppActionAdapters.SyncScrollAdapter
    public void scroll(ScrollDirections.DirectionX directionX, ScrollDirections.DirectionY directionY, int scrollTimes) {
        scrollImpl(directionToButtonCodeY.get(directionY), scrollTimes);
    }
}
