package com.example.datainsert.exagear.controls.menus;

import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE;
import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT;

import com.eltechs.axs.activities.XServerDisplayActivity;
import com.eltechs.axs.widgets.actions.AbstractAction;
import com.example.datainsert.exagear.RR;

public class RotateScreen extends AbstractAction {
    public RotateScreen() {
        super(RR.getS(RR.cmCtrl_actionRotate));
    }

    @Override
    public void run() {
        XServerDisplayActivity a =getCurrentXServerDisplayActivity();

        a.setRequestedOrientation(
                a.getRequestedOrientation()!=SCREEN_ORIENTATION_SENSOR_PORTRAIT
                ? SCREEN_ORIENTATION_SENSOR_PORTRAIT
                :SCREEN_ORIENTATION_SENSOR_LANDSCAPE);

    }
}
