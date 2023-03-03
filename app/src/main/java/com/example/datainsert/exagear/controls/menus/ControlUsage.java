package com.example.datainsert.exagear.controls.menus;

import android.os.Bundle;

import com.eltechs.axs.Globals;
import com.eltechs.axs.activities.FrameworkActivity;
import com.eltechs.axs.activities.UsageActivity;
import com.eltechs.axs.activities.XServerDisplayActivity;
import com.eltechs.axs.activities.menus.ShowUsage;
import com.eltechs.axs.applicationState.SelectedExecutableFileAware;
import com.eltechs.axs.configuration.startup.DetectedExecutableFile;
import com.eltechs.axs.widgets.actions.AbstractAction;
import com.eltechs.axs.widgets.actions.Action;
import com.eltechs.ed.controls.DefaultControls;
import com.eltechs.ed.fragments.ControlsInfoDFragment;
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
