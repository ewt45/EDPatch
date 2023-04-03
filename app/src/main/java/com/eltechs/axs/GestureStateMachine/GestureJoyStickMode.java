package com.eltechs.axs.GestureStateMachine;

import java.util.ArrayList;
import java.util.List;

/**
 * 非原版代码
 */
@Deprecated
public class GestureJoyStickMode {
    private final List<JoyStickModeChangeListener> listeners = new ArrayList();
    private JoyStickModeState state;

    /* loaded from: classes.dex */
    public interface JoyStickModeChangeListener {
        void joyStickModeChanged(GestureJoyStickMode gestureJoyStickMode, JoyStickModeState joyStickModeState);
    }

    /* loaded from: classes.dex */
    public enum JoyStickModeState {
        JOYSTICK_MODE_ON,
        JOYSTICK_MODE_OFF
    }

    public GestureJoyStickMode(JoyStickModeState joyStickModeState) {
        this.state = joyStickModeState;
    }

    public void addListener(JoyStickModeChangeListener joyStickModeChangeListener) {
        this.listeners.add(joyStickModeChangeListener);
    }

    public JoyStickModeState getState() {
        return this.state;
    }

    public void removeListener(JoyStickModeChangeListener joyStickModeChangeListener) {
        this.listeners.remove(joyStickModeChangeListener);
    }

    public void setState(JoyStickModeState joyStickModeState) {
        this.state = joyStickModeState;
        for (JoyStickModeChangeListener joyStickModeChangeListener : this.listeners) {
            joyStickModeChangeListener.joyStickModeChanged(this, this.state);
        }
    }
}
