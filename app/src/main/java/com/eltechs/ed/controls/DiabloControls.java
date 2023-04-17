package com.eltechs.ed.controls;

import com.eltechs.axs.activities.XServerDisplayActivityInterfaceOverlay;
import com.eltechs.axs.gamesControls.DiabloInterfaceOverlay;
import com.eltechs.ed.R_original;

import java.util.Arrays;
import java.util.List;

/* loaded from: classes.dex */
public class DiabloControls extends Controls {
    @Override // com.eltechs.ed.controls.Controls
    public String getId() {
        return "diablo";
    }

    @Override // com.eltechs.ed.controls.Controls
    public String getName() {
        return "Diablo";
    }

    @Override // com.eltechs.ed.controls.Controls
    public List<ControlsInfoElem> getInfoElems() {
        return Arrays.asList(new ControlsInfoElem(0, "Diablo", "These controls are optimized for Diablo 2."), new ControlsInfoElem(R_original.drawable.gesture_lclick, "Left Click", "Short tap"), new ControlsInfoElem(R_original.drawable.gesture_rclick, "Right Click", "Two fingers short tap"), new ControlsInfoElem(R_original.drawable.gesture_dnd_left, "Drag'n'Drop (Left Button)", "Single finger tap & move"), new ControlsInfoElem(R_original.drawable.gesture_zoom, "Zoom", "Three fingers long tap & move"), new ControlsInfoElem(R_original.drawable.gesture_menu, "App Menu", "Four fingers tap"), new ControlsInfoElem(0, "Left Toolbar Buttons", "Left toolbar contains:\n- Some useful keys buttons"), new ControlsInfoElem(0, "Right Toolbar Buttons", "Right toolbar contains:\n- Some useful keys buttons"));
    }

    @Override // com.eltechs.ed.controls.Controls
    public XServerDisplayActivityInterfaceOverlay create() {
        return new DiabloInterfaceOverlay();
    }
}
