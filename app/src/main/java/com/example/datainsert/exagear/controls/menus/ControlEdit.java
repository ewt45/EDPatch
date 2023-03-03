package com.example.datainsert.exagear.controls.menus;

import android.util.Log;

import com.eltechs.axs.Globals;
import com.eltechs.axs.activities.XServerDisplayActivityInterfaceOverlay;
import com.eltechs.axs.activities.XServerDisplayActivityUiOverlaySidePanels;
import com.eltechs.axs.applicationState.XServerDisplayActivityConfigurationAware;
import com.eltechs.axs.helpers.Assert;
import com.eltechs.axs.widgets.actions.AbstractAction;
import com.example.datainsert.exagear.RR;
import com.example.datainsert.exagear.controls.interfaceOverlay.FalloutInterfaceOverlay2;

public class ControlEdit extends AbstractAction {

    public ControlEdit() {
        super(null);
    }

    private XServerDisplayActivityUiOverlaySidePanels getUiOverlaySidePanels() {
        XServerDisplayActivityUiOverlaySidePanels xServerDisplayActivityUiOverlaySidePanels = (XServerDisplayActivityUiOverlaySidePanels) ((XServerDisplayActivityConfigurationAware) Globals.getApplicationState()).getXServerDisplayActivityInterfaceOverlay();
        Assert.notNull(xServerDisplayActivityUiOverlaySidePanels, "ToggleUiOverlaySidePanels should be used with UiOverlays with SidePanels");

        Log.d("TAG", "getUiOverlaySidePanels: 能直接获取到ovelay实例吗"+((XServerDisplayActivityConfigurationAware) Globals.getApplicationState()).getXServerDisplayActivityInterfaceOverlay()
                +' '+(((XServerDisplayActivityConfigurationAware) Globals.getApplicationState()).getXServerDisplayActivityInterfaceOverlay() instanceof FalloutInterfaceOverlay2));

        return xServerDisplayActivityUiOverlaySidePanels;
    }

    @Override // com.eltechs.axs.widgets.actions.AbstractAction, com.eltechs.axs.widgets.actions.Action
    public String getName() {
//        if (getUiOverlaySidePanels().isSidePanelsVisible()) {
//            return "退出按键编辑";
//        }
        return RR.getS(RR.cmCtrl_actionEdit);
    }

    @Override // com.eltechs.axs.widgets.actions.Action
    public void run() {
        XServerDisplayActivityInterfaceOverlay ui = ((XServerDisplayActivityConfigurationAware) Globals.getApplicationState()).getXServerDisplayActivityInterfaceOverlay();
        if(ui instanceof FalloutInterfaceOverlay2){
            ((FalloutInterfaceOverlay2) ui).startEditing();
        }
    }
}
