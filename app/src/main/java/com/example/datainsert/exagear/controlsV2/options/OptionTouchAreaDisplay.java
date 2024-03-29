package com.example.datainsert.exagear.controlsV2.options;

import com.example.datainsert.exagear.RR;
import com.example.datainsert.exagear.controlsV2.Const;
import com.example.datainsert.exagear.controlsV2.edit.Edit4OtherView;

/**
 * 隐藏或显示屏幕按键
 */
public class OptionTouchAreaDisplay extends AbstractOption {

    @Override
    public void run() {
        boolean currentShow = Const.getActiveProfile().isShowTouchArea();
        Edit4OtherView.setProfileShowTouchArea(!currentShow);
    }

    @Override
    public String getName() {
        return RR.getS(RR.ctr2_option_showHideTouchArea);
    }
}
