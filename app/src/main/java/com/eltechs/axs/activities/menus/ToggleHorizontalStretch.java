package com.eltechs.axs.activities.menus;

import com.eltechs.axs.CommonApplicationConfigurationAccessor;
import com.eltechs.axs.activities.XServerDisplayActivity;
import com.eltechs.axs.helpers.AndroidHelpers;
import com.eltechs.axs.widgets.actions.AbstractAction;

public class ToggleHorizontalStretch extends AbstractAction {
    public ToggleHorizontalStretch() {
        super(null);
    }

    @Override // com.eltechs.axs.widgets.actions.AbstractAction, com.eltechs.axs.widgets.actions.Action
    public String getName() {
        return  "拉伸全屏";

//        if (getCurrentXServerDisplayActivity().isHorizontalStretchEnabled()) {
//            return AndroidHelpers.getString(com.eltechs.axs.R.string.show_normal);
//        }else
//            return   AndroidHelpers.getString(com.eltechs.axs.R.string.show_stretched);
    }

    @Override // com.eltechs.axs.widgets.actions.Action
    public void run() {
        XServerDisplayActivity currentXServerDisplayActivity = getCurrentXServerDisplayActivity();
        CommonApplicationConfigurationAccessor commonApplicationConfigurationAccessor = new CommonApplicationConfigurationAccessor();
        boolean z = !currentXServerDisplayActivity.isHorizontalStretchEnabled();
        currentXServerDisplayActivity.setHorizontalStretchEnabled(z);
        commonApplicationConfigurationAccessor.setHorizontalStretchEnabled(z);
    }
}
