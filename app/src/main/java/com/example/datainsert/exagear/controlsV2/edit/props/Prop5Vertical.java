package com.example.datainsert.exagear.controlsV2.edit.props;

import static com.example.datainsert.exagear.controlsV2.edit.Edit1KeyView.buildOptionsGroup;

import android.content.Context;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.example.datainsert.exagear.RR;
import com.example.datainsert.exagear.controlsV2.Const;
import com.example.datainsert.exagear.controlsV2.TouchAreaModel;
import com.example.datainsert.exagear.controlsV2.model.OneButton;
import com.example.datainsert.exagear.controlsV2.model.OneColumn;

public class Prop5Vertical extends Prop<TouchAreaModel> {
    RadioGroup group;
    public Prop5Vertical(Host<TouchAreaModel> host, Context c) {
        super(host, c);
    }

    @Override
    public String getTitle() {
        return RR.getS(RR.ctr2_prop_vertical);
    }

    @Override
    protected View createMainEditView(Context c) {
        HorizontalScrollView scrollGroupShape = buildOptionsGroup(c,
                RR.getSArr(RR.ctr2_prop_vertical_names) , //横向，竖向
                new int[]{0, 1},
                (group, btn, intValue) -> {
                    if (mHost.getModel() instanceof OneColumn)
                        ((OneColumn) mHost.getModel()).setVertical(intValue != 0);

                    onWidgetListener();
                });
        group = (RadioGroup) scrollGroupShape.getChildAt(0);
        return scrollGroupShape;
    }

    @Override
    protected View createAltEditView(Context c) {
        return null;
    }

    @Override
    public void updateUIFromModel(TouchAreaModel model) {
        if(model instanceof OneColumn){
            int idx = ((OneColumn) model).isVertical() ? 1 : 0;
            ((RadioButton) group.getChildAt(idx)).setChecked(true);
        }
    }
}
