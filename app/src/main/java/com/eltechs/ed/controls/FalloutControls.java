package com.eltechs.ed.controls;

import com.eltechs.axs.activities.XServerDisplayActivityInterfaceOverlay;
import com.eltechs.axs.gamesControls.FalloutInterfaceOverlay;
import com.eltechs.ed.R_original;

import java.util.Arrays;
import java.util.List;

/* loaded from: classes.dex */
public class FalloutControls extends Controls {
    @Override // com.eltechs.ed.controls.Controls
    public String getId() {
        return "fallout";
    }

    @Override // com.eltechs.ed.controls.Controls
    public String getName() {
        return "Fallout";
    }

    @Override // com.eltechs.ed.controls.Controls
    public List<ControlsInfoElem> getInfoElems() {
        return Arrays.asList(new ControlsInfoElem(0, "Fallout", "These controls are optimized for Fallout 1/2. But you can also try them with other similar games."), new ControlsInfoElem(R_original.drawable.gesture_lclick, "Left Click", "Short tap"), new ControlsInfoElem(R_original.drawable.gesture_rclick, "Right Click", "Two fingers short tap"), new ControlsInfoElem(R_original.drawable.gesture_dnd_left, "Drag'n'Drop (Left Button)", "Slow finger move"), new ControlsInfoElem(R_original.drawable.gesture_dnd_right, "Drag'n'Drop (Right Button)", "Two fingers long tap & move"), new ControlsInfoElem(R_original.drawable.gesture_scroll_mouse, "Scroll (Mouse)", "Fast finger move"), new ControlsInfoElem(R_original.drawable.gesture_zoom, "Zoom", "Three fingers long tap & move"), new ControlsInfoElem(R_original.drawable.gesture_menu, "App Menu", "Four fingers tap"), new ControlsInfoElem(0, "Left Toolbar Buttons", "Left toolbar contains:\n- Some useful keys buttons"), new ControlsInfoElem(0, "Right Toolbar Buttons", "Right toolbar contains:\n- 'Sync Cursor' button (syncs cursor position with finger position)\n- Some useful keys buttons"));
    }

    @Override // com.eltechs.ed.controls.Controls
    public XServerDisplayActivityInterfaceOverlay create() {
        return new FalloutInterfaceOverlay();
    }
}
