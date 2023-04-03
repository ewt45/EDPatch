package com.eltechs.axs.activities.menus;

import com.eltechs.axs.Globals;
import com.eltechs.axs.activities.FrameworkActivity;
import com.eltechs.axs.activities.UsageActivity;
import com.eltechs.axs.activities.XServerDisplayActivity;
import com.eltechs.axs.applicationState.SelectedExecutableFileAware;
import com.eltechs.axs.configuration.startup.DetectedExecutableFile;
import com.eltechs.axs.helpers.AndroidHelpers;
import com.eltechs.axs.widgets.actions.AbstractAction;

public class ShowUsage extends AbstractAction {
    public ShowUsage() {
//        super(AndroidHelpers.getString(com.eltechs.axs.R.string.show_tutorial));
        super("使用说明");
    }

    @Override // com.eltechs.axs.widgets.actions.Action
    public void run() {
        XServerDisplayActivity currentXServerDisplayActivity = getCurrentXServerDisplayActivity();
        SelectedExecutableFileAware selectedExecutableFileAware = (SelectedExecutableFileAware) Globals.getApplicationState();
        DetectedExecutableFile selectedExecutableFile = selectedExecutableFileAware.getSelectedExecutableFile();
        if (selectedExecutableFile.getControlsInfoDialog() == null) {
            currentXServerDisplayActivity.startActivity(FrameworkActivity.createIntent(currentXServerDisplayActivity, UsageActivity.class, Integer.valueOf(selectedExecutableFileAware.getSelectedExecutableFile().getEffectiveCustomizationPackage().getInfoResId())));
        } else {
            selectedExecutableFile.getControlsInfoDialog().show(currentXServerDisplayActivity.getSupportFragmentManager(), "CONTROLS_INFO");
        }
    }
}

