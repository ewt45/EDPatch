package com.eltechs.ed.controls;

import com.eltechs.axs.activities.XServerDisplayActivityInterfaceOverlay;
import com.eltechs.axs.gamesControls.HybridInterfaceOverlay;
import com.eltechs.ed.R_original;

import java.util.Arrays;
import java.util.List;

/* loaded from: classes.dex */
public class HybridControls extends Controls {
    @Override // com.eltechs.ed.controls.Controls
    public String getId() {
        return "hybrid";
    }

    @Override // com.eltechs.ed.controls.Controls
    public String getName() {
        return "Hybrid";
    }

    @Override // com.eltechs.ed.controls.Controls
    public List<ControlsInfoElem> getInfoElems() {
        return Arrays.asList(new ControlsInfoElem(0, "Hybrid", "These controls use hybrid mode."), new ControlsInfoElem(R_original.drawable.gesture_lclick, "Left Click", "Short tap"), new ControlsInfoElem(R_original.drawable.gesture_rclick_long, "Long Right Click", "Long tap"), new ControlsInfoElem(R_original.drawable.gesture_move_cursor, "Move Cursor", "Fast finger move"), new ControlsInfoElem(R_original.drawable.gesture_dnd_left, "Drag'n'Drop (Left Button)", "Two fingers tap and move one finger"), new ControlsInfoElem(R_original.drawable.gesture_scroll_mouse, "Scroll (Mouse)", "Two fingers tap & move"), new ControlsInfoElem(R_original.drawable.gesture_enter, "Press Enter", "Two fingers tap"), new ControlsInfoElem(R_original.drawable.gesture_space, "Press Space", "Three fingers tap"), new ControlsInfoElem(R_original.drawable.gesture_menu, "App Menu", "Four fingers tap"), new ControlsInfoElem(0, "Left Toolbar Buttons", "Left toolbar contains:\n- Some useful keys buttons"), new ControlsInfoElem(0, "Right Toolbar Buttons", "Right toolbar contains:\n- Some useful keys buttons"));
    }

    @Override // com.eltechs.ed.controls.Controls
    public XServerDisplayActivityInterfaceOverlay create() {
        return new HybridInterfaceOverlay();
    }
}
