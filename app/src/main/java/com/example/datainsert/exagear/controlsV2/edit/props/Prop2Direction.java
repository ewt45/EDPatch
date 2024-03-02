package com.example.datainsert.exagear.controlsV2.edit.props;

import static com.example.datainsert.exagear.controlsV2.edit.Edit1KeyView.buildOptionsGroup;

import android.content.Context;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.example.datainsert.exagear.controlsV2.model.OneStick;
import com.example.datainsert.exagear.controlsV2.TouchAreaModel;

public class Prop2Direction extends Prop<TouchAreaModel>{
    RadioGroup groupDirection;
    public Prop2Direction(Host<TouchAreaModel> host, Context c) {
        super(host, c);
    }

    @Override
    public String getTitle() {
        return "方向";
    }

    @Override
    protected View createMainEditView(Context c) {
        HorizontalScrollView scrollGroupDirection = buildOptionsGroup(c,
                new String[]{"4方向", "8方向",},
                new int[]{OneStick.WAY_4, OneStick.WAY_8},
                (group, btn, intValue) -> {
                    if (mHost.getModel() instanceof OneStick)
                        ((OneStick) mHost.getModel()).direction = intValue;

                    mHost.onModelChanged();
                });
        groupDirection = (RadioGroup) scrollGroupDirection.getChildAt(0);
        return scrollGroupDirection;
    }

    @Override
    protected View createAltEditView(Context c) {
        return null;
    }

    @Override
    public void updateUIFromModel(TouchAreaModel model) {
        if(model instanceof OneStick)
            ((RadioButton) groupDirection.getChildAt(((OneStick)model).direction)).setChecked(true);
    }
}
