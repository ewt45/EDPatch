package com.eltechs.ed.controls;

import com.eltechs.axs.activities.XServerDisplayActivityInterfaceOverlay;
import com.eltechs.axs.gamesControls.BasicStrategiesUI;
import com.eltechs.axs.gamesControls.Disciples2TouchScreenControlsFactory;
import com.eltechs.ed.R_original;

import java.util.Arrays;
import java.util.List;

/* loaded from: classes.dex */
public class Disciples2Controls extends Controls {
    @Override // com.eltechs.ed.controls.Controls
    public String getId() {
        return "disciples2";
    }

    @Override // com.eltechs.ed.controls.Controls
    public String getName() {
        return "Disciples 2";
    }

    @Override // com.eltechs.ed.controls.Controls
    public List<ControlsInfoElem> getInfoElems() {
        return Arrays.asList(new ControlsInfoElem(0, "Disciples 2", "These controls are optimized for Disciples 2. But you can also try them with other similar games."), new ControlsInfoElem(R_original.drawable.gesture_lclick, "Left Click", "Short tap"), new ControlsInfoElem(R_original.drawable.gesture_rclick_long, "Long Right Click", "Long tap"), new ControlsInfoElem(R_original.drawable.gesture_dnd_left, "Drag'n'Drop (Left Button)", "Slow finger move"), new ControlsInfoElem(R_original.drawable.gesture_scroll_mouse, "Scroll (Mouse)", "Fast finger move"), new ControlsInfoElem(R_original.drawable.gesture_zoom, "Zoom", "Two fingers long tap & move"), new ControlsInfoElem(R_original.drawable.gesture_enter, "Press Enter", "Two fingers tap"), new ControlsInfoElem(R_original.drawable.gesture_space, "Press Space", "Three fingers tap"), new ControlsInfoElem(R_original.drawable.gesture_menu, "App Menu", "Four fingers tap"));
    }

    @Override // com.eltechs.ed.controls.Controls
    public XServerDisplayActivityInterfaceOverlay create() {
        return new BasicStrategiesUI(new Disciples2TouchScreenControlsFactory());
    }
}
