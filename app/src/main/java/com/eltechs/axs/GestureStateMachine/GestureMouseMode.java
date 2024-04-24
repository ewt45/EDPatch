package com.eltechs.axs.GestureStateMachine;

import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class GestureMouseMode {
    private final List<MouseModeChangeListener> listeners = new ArrayList<>();
    private MouseModeState state;

    /* loaded from: classes.dex */
    public interface MouseModeChangeListener {
        void mouseModeChanged(GestureMouseMode gestureMouseMode, MouseModeState mouseModeState);
    }

    /* loaded from: classes.dex */
    public enum MouseModeState {
        MOUSE_MODE_LEFT,
        MOUSE_MODE_RIGHT
    }

    public GestureMouseMode(MouseModeState mouseModeState) {
        this.state = mouseModeState;
    }

    public MouseModeState getState() {
        return this.state;
    }

    public void setState(MouseModeState mouseModeState) {
        this.state = mouseModeState;
        for (MouseModeChangeListener mouseModeChangeListener : this.listeners) {
            mouseModeChangeListener.mouseModeChanged(this, this.state);
        }
    }

    public void addListener(MouseModeChangeListener mouseModeChangeListener) {
        this.listeners.add(mouseModeChangeListener);
    }

    public void removeListener(MouseModeChangeListener mouseModeChangeListener) {
        this.listeners.remove(mouseModeChangeListener);
    }
}
