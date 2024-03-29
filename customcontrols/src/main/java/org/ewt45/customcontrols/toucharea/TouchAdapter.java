package org.ewt45.customcontrols.toucharea;

import java.util.List;

public interface TouchAdapter {
    void notifyMoved(Finger finger, List<Finger> list);

    default void notifyMovedIn(Finger finger, List<Finger> list){notifyTouched(finger,list);};

    default void notifyMovedOut(Finger finger, List<Finger> list){notifyReleased(finger,list);};

    void notifyReleased(Finger finger, List<Finger> list);

    void notifyTouched(Finger finger, List<Finger> list);
}
