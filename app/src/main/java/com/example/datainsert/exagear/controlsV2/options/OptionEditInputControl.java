package com.example.datainsert.exagear.controlsV2.options;

import com.example.datainsert.exagear.controlsV2.Const;

public class OptionEditInputControl extends AbstractOption{
    @Override
    public void run() {
        Const.getTouchView().startEdit();
    }

    @Override
    public String getName() {
        return "编辑操作模式";
    }
}
