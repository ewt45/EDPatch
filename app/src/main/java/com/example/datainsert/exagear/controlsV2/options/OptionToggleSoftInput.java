package com.example.datainsert.exagear.controlsV2.options;

import android.content.Context;
import android.view.inputmethod.InputMethodManager;

import com.example.datainsert.exagear.RR;
import com.example.datainsert.exagear.controlsV2.Const;

public class OptionToggleSoftInput extends AbstractOption {
    @Override
    public void run() {
        Const.getActivity().getWindow().getDecorView().postDelayed(() -> ((InputMethodManager) Const.getContext()
                        .getSystemService(Context.INPUT_METHOD_SERVICE)).toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
                , 200);
    }

    @Override
    public String getName() {
        return RR.getS(RR.ctr2_option_softInput);
    }
}
