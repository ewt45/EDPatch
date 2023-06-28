package com.example.datainsert.exagear.controls.menus;

import com.eltechs.axs.activities.menus.ShowUsage;
import com.example.datainsert.exagear.RR;

public class ControlUsage extends ShowUsage {
    public ControlUsage(){
        super();
    }
    @Override
    public String getName() {
        return RR.getS(RR.cmCtrl_actionCtrlTip);
    }
//    @Override // com.eltechs.axs.widgets.actions.Action
//    public void run() {
//        XServerDisplayActivity currentXServerDisplayActivity = getCurrentXServerDisplayActivity();
//        SelectedExecutableFileAware selectedExecutableFileAware = (SelectedExecutableFileAware) Globals.getApplicationState();
//        DetectedExecutableFile selectedExecutableFile = selectedExecutableFileAware.getSelectedExecutableFile();
//        if (selectedExecutableFile.getControlsInfoDialog() == null) {
//            currentXServerDisplayActivity.startActivity(FrameworkActivity.createIntent(currentXServerDisplayActivity, UsageActivity.class, Integer.valueOf(selectedExecutableFileAware.getSelectedExecutableFile().getEffectiveCustomizationPackage().getInfoResId())));
//        } else {
//            selectedExecutableFile.getControlsInfoDialog().show(currentXServerDisplayActivity.getSupportFragmentManager(), "CONTROLS_INFO");
//        }
//
//        ControlsInfoDFragment controlsInfoDFragment = new ControlsInfoDFragment();
//        Bundle bundle = new Bundle();
//        bundle.putString(ControlsInfoDFragment.ARG_CONTROLS_ID, new DefaultControls().getId());
//        controlsInfoDFragment.setArguments(bundle);
//
//    }
}
