package com.eltechs.ed.controls;

import static com.example.datainsert.exagear.RR.getS;
import static com.example.datainsert.exagear.controls.ControlsResolver.PREF_KEY_MOUSE_MOVE_RELATIVE;

import com.eltechs.axs.activities.XServerDisplayActivityInterfaceOverlay;
import com.eltechs.ed.R_original;
import com.example.datainsert.exagear.FAB.dialogfragment.BaseFragment;
import com.example.datainsert.exagear.RR;
import com.example.datainsert.exagear.controls.interfaceOverlay.FalloutInterfaceOverlay2;

import java.util.Arrays;
import java.util.List;

/* loaded from: classes.dex */
public class DefaultControls extends Controls {
    @Override // com.eltechs.ed.controls.Controls
    public String getId() {
        return "default";
    }

    @Override // com.eltechs.ed.controls.Controls
    public String getName() {
        return "Default";
    }

    public static List<ControlsInfoElem> infoElem1 =Arrays.asList(
            new ControlsInfoElem(0, getS(RR.cmCtrl_gs1Abs_title).split("\\$")[0], getS(RR.cmCtrl_gs1Abs_title).split("\\$")[1]),
            new ControlsInfoElem(R_original.drawable.gesture_lclick, getS(RR.cmCtrl_gs_lClick).split("\\$")[0], getS(RR.cmCtrl_gs_lClick).split("\\$")[1]),
            new ControlsInfoElem(R_original.drawable.gesture_rclick, getS(RR.cmCtrl_gs_rClick).split("\\$")[0], getS(RR.cmCtrl_gs_rClick).split("\\$")[1]),
            new ControlsInfoElem(R_original.drawable.gesture_vscroll_wheel, getS(RR.cmCtrl_gs_vScroll).split("\\$")[0], getS(RR.cmCtrl_gs_vScroll).split("\\$")[1]),
            new ControlsInfoElem(R_original.drawable.gesture_dnd_left, getS(RR.cmCtrl_gs_dndLeft).split("\\$")[0], getS(RR.cmCtrl_gs_dndLeft).split("\\$")[1]),
            new ControlsInfoElem(R_original.drawable.gesture_zoom, getS(RR.cmCtrl_gs_zoom).split("\\$")[0], getS(RR.cmCtrl_gs_zoom).split("\\$")[1]),
            new ControlsInfoElem(R_original.drawable.gesture_keyboard, getS(RR.cmCtrl_gs_keyboard).split("\\$")[0], getS(RR.cmCtrl_gs_keyboard).split("\\$")[1]),
            new ControlsInfoElem(R_original.drawable.gesture_menu, getS(RR.cmCtrl_gs_menu).split("\\$")[0], getS(RR.cmCtrl_gs_menu).split("\\$")[2]));
    public static List<ControlsInfoElem> infoElem2 = Arrays.asList(
            new ControlsInfoElem(0, getS(RR.cmCtrl_gs2Rel_title).split("\\$")[0], getS(RR.cmCtrl_gs2Rel_title).split("\\$")[1]),
            new ControlsInfoElem(R_original.drawable.gesture_move_cursor,getS(RR.cmCtrl_gs_moveCursor).split("\\$")[0],getS(RR.cmCtrl_gs_moveCursor).split("\\$")[2]),
            new ControlsInfoElem(R_original.drawable.gesture_lclick, getS(RR.cmCtrl_gs_lClick).split("\\$")[0], getS(RR.cmCtrl_gs_lClick).split("\\$")[2]),
            new ControlsInfoElem(R_original.drawable.gesture_rclick, getS(RR.cmCtrl_gs_rClick).split("\\$")[0], getS(RR.cmCtrl_gs_rClick).split("\\$")[2]),
            new ControlsInfoElem(R_original.drawable.gesture_vscroll_wheel, getS(RR.cmCtrl_gs_vScroll).split("\\$")[0], getS(RR.cmCtrl_gs_vScroll).split("\\$")[2]),
            new ControlsInfoElem(R_original.drawable.gesture_dnd_left, getS(RR.cmCtrl_gs_dndLeft).split("\\$")[0], getS(RR.cmCtrl_gs_dndLeft).split("\\$")[2]),
            new ControlsInfoElem(R_original.drawable.gesture_zoom, getS(RR.cmCtrl_gs_zoom).split("\\$")[0], getS(RR.cmCtrl_gs_zoom).split("\\$")[2]),
            new ControlsInfoElem(R_original.drawable.gesture_menu, getS(RR.cmCtrl_gs_menu).split("\\$")[0], getS(RR.cmCtrl_gs_menu).split("\\$")[2]));

    @Override // com.eltechs.ed.controls.Controls
    public List<ControlsInfoElem> getInfoElems() {
        if (BaseFragment.getPreference().getBoolean(PREF_KEY_MOUSE_MOVE_RELATIVE, false)) {
            return infoElem2;
        } else
            return infoElem1;

//        return Arrays.asList(
//                new ControlsInfoElem(0, "Default Controls", "These controls should be suitable for most regular (non-game) applications."),
//                new ControlsInfoElem(R.drawable.gesture_lclick, "Left Click", "Short tap"),
//                new ControlsInfoElem(R.drawable.gesture_rclick, "Right Click", "Long tap & release"),
//                new ControlsInfoElem(R.drawable.gesture_vscroll_wheel, "Vertical Scroll (Wheel)", "Short tap & move"),
//                new ControlsInfoElem(R.drawable.gesture_dnd_left, "Drag'n'Drop (Left Button)", "Long tap & move"),
//                new ControlsInfoElem(R.drawable.gesture_zoom, "Zoom", "Two fingers long tap & move"),
//                new ControlsInfoElem(R.drawable.gesture_keyboard, "Toggle Keyboard", "Two fingers tap"),
//                new ControlsInfoElem(R.drawable.gesture_toolbar, "Toggle Toolbar", "Three fingers tap"));
    }

    @Override // com.eltechs.ed.controls.Controls
    public XServerDisplayActivityInterfaceOverlay create() {
        return new FalloutInterfaceOverlay2();
    }
}