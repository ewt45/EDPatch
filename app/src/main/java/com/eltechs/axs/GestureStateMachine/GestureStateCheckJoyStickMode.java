package com.eltechs.axs.GestureStateMachine;

import com.eltechs.axs.GestureStateMachine.GestureJoyStickMode;
import com.eltechs.axs.finiteStateMachine.FSMEvent;

/* loaded from: classes.dex */

/**
 * 非原版代码
 *
 */
@Deprecated
public class GestureStateCheckJoyStickMode extends AbstractGestureFSMState {
    private final GestureJoyStickMode joyStickMode;
    public static FSMEvent JOYSTICK_MODE_ON = new FSMEvent() { // from class: com.eltechs.axs.GestureStateMachine.GestureStateCheckJoyStickMode.1_fix
    };
    public static FSMEvent JOYSTICK_MODE_OFF = new FSMEvent() { // from class: com.eltechs.axs.GestureStateMachine.GestureStateCheckJoyStickMode.2_fix
    };

    @Override // com.eltechs.axs.finiteStateMachine.FSMState
    public void notifyBecomeInactive() {
    }

    public GestureStateCheckJoyStickMode(GestureContext gestureContext, GestureJoyStickMode gestureJoyStickMode) {
        super(gestureContext);
        this.joyStickMode = gestureJoyStickMode;
    }

    @Override // com.eltechs.axs.finiteStateMachine.FSMState
    public void notifyBecomeActive() {
        if (this.joyStickMode.getState() == GestureJoyStickMode.JoyStickModeState.JOYSTICK_MODE_ON) {
            sendEvent(JOYSTICK_MODE_ON);
        } else {
            sendEvent(JOYSTICK_MODE_OFF);
        }
    }
}
