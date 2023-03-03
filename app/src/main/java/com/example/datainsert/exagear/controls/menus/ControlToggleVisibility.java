package com.example.datainsert.exagear.controls.menus;

import com.eltechs.axs.Globals;
import com.eltechs.axs.activities.XServerDisplayActivityUiOverlaySidePanels;
import com.eltechs.axs.applicationState.XServerDisplayActivityConfigurationAware;
import com.eltechs.axs.widgets.actions.AbstractAction;
import com.example.datainsert.exagear.RR;

public class ControlToggleVisibility extends AbstractAction {
    public ControlToggleVisibility() {
        super(null);
    }

    @Override
    public String getName() {
        XServerDisplayActivityUiOverlaySidePanels sidePanels = (XServerDisplayActivityUiOverlaySidePanels) ((XServerDisplayActivityConfigurationAware) Globals.getApplicationState()).getXServerDisplayActivityInterfaceOverlay();

        return sidePanels.isSidePanelsVisible()? RR.getS(RR.cmCtrl_actionCtrlHide) :RR.getS(RR.cmCtrl_actionCtrlShow);
    }

    @Override
    public void run() {
        XServerDisplayActivityUiOverlaySidePanels sidePanels = (XServerDisplayActivityUiOverlaySidePanels) ((XServerDisplayActivityConfigurationAware) Globals.getApplicationState()).getXServerDisplayActivityInterfaceOverlay();
        if(sidePanels!=null){
            sidePanels.toggleSidePanelsVisibility();
        }
    }
}
