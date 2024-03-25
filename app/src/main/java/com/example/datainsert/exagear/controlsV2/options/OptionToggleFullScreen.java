package com.example.datainsert.exagear.controlsV2.options;

import static com.example.datainsert.exagear.controlsV2.XServerViewHolder.SCALE_FULL_IGNORE_RATIO;
import static com.example.datainsert.exagear.controlsV2.XServerViewHolder.SCALE_FULL_WITH_RATIO;

import com.eltechs.axs.helpers.UiThread;
import com.example.datainsert.exagear.RR;
import com.example.datainsert.exagear.controlsV2.Const;
import com.example.datainsert.exagear.controlsV2.XServerViewHolder;

public class OptionToggleFullScreen extends AbstractOption{
    @Override
    public void run() {
        //圣诞树的平板，这个没有效果。试试延迟0.7秒？
        UiThread.postDelayed(700,()->{
            XServerViewHolder holder = Const.getXServerHolder();
            int currStyle = holder.getScaleStyle();
            Const.getXServerHolder().setScaleStyle(currStyle == SCALE_FULL_IGNORE_RATIO ? SCALE_FULL_WITH_RATIO : SCALE_FULL_IGNORE_RATIO);

        });
    }

    @Override
    public String getName() {
        return RR.getS(RR.ctr2_option_fullscreen);
    }
}
