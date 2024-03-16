package com.example.datainsert.exagear.controlsV2.options;

import com.example.datainsert.exagear.RR;
import com.example.datainsert.exagear.controlsV2.Const;

public class OptionEditInputControl extends AbstractOption{
    @Override
    public void run() {
        Const.getTouchView().startEdit();
    }

    @Override
    public String getName() {
        return RR.getS(RR.global_edit);
    }
}
