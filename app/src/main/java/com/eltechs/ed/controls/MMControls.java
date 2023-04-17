package com.eltechs.ed.controls;

import com.eltechs.axs.activities.XServerDisplayActivityInterfaceOverlay;
import com.eltechs.axs.gamesControls.MM6InterfaceOverlay;
import com.eltechs.ed.R_original;

import java.util.Arrays;
import java.util.List;

/* loaded from: classes.dex */
public class MMControls extends Controls {
    @Override // com.eltechs.ed.controls.Controls
    public String getId() {
        return "mm";
    }

    @Override // com.eltechs.ed.controls.Controls
    public String getName() {
        return "Might and Magic";
    }

    @Override // com.eltechs.ed.controls.Controls
    public List<ControlsInfoElem> getInfoElems() {
        return Arrays.asList(new ControlsInfoElem(0, "Might and Magic", "These controls are optimized for Might and Magic 6/7/8. But you can also try them with other similar games."), new ControlsInfoElem(R_original.drawable.gesture_lclick, "Left Click", "Short tap"), new ControlsInfoElem(R_original.drawable.gesture_lclick_long, "Long Left Click", "Long tap when [Mouse mode] is 'Left'"), new ControlsInfoElem(R_original.drawable.gesture_rclick_long, "Long Right Click", "Long tap when [Mouse mode] is 'Right'"), new ControlsInfoElem(R_original.drawable.gesture_move, "Move And Strafe", "Move finger in left part of screen"), new ControlsInfoElem(R_original.drawable.gesture_turn_lr, "Turn Left/Right", "Move finger in right part of screen"), new ControlsInfoElem(R_original.drawable.gesture_zoom, "Zoom", "Two fingers long tap & move"), new ControlsInfoElem(R_original.drawable.gesture_enter, "Press Enter", "Two fingers tap"), new ControlsInfoElem(R_original.drawable.gesture_space, "Press Space", "Three fingers tap"), new ControlsInfoElem(R_original.drawable.gesture_menu, "App Menu", "Four fingers tap"), new ControlsInfoElem(0, "Left Toolbar Buttons", "Left toolbar contains:\n- 'Run On/Off' button (toggles Shift key state to switch between run/walk)\n- Some useful keys buttons"), new ControlsInfoElem(0, "Right Toolbar Buttons", "Right toolbar contains:\n- 'Switch Mouse Mode' button (see below)\n- Some useful keys buttons"), new ControlsInfoElem(R_original.drawable.mouse_right, "Switch Mouse Mode", "Switches [Mouse Mode] between 'Left' and 'Right'"));
    }

    @Override // com.eltechs.ed.controls.Controls
    public XServerDisplayActivityInterfaceOverlay create() {
        return new MM6InterfaceOverlay();
    }
}
