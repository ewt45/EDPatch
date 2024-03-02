package com.example.datainsert.exagear.controlsV2.edit.props;

import static com.example.datainsert.exagear.controlsV2.edit.Edit1KeyView.buildOptionsGroup;

import android.content.Context;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.example.datainsert.exagear.controlsV2.Const;
import com.example.datainsert.exagear.controlsV2.model.OneButton;
import com.example.datainsert.exagear.controlsV2.TouchAreaModel;

public class Prop1Shape extends Prop<TouchAreaModel> {
    RadioGroup groupShape;
    public Prop1Shape(Host<TouchAreaModel> host, Context c) {
        super(host, c);
    }

    @Override
    public String getTitle() {
        return "形状";
    }

    @Override
    protected View createMainEditView(Context c) {
        //形状
        HorizontalScrollView scrollGroupShape = buildOptionsGroup(c,
                new String[]{"矩形", "圆形",},
                new int[]{Const.BtnShape.RECT, Const.BtnShape.OVAL,},
                (group, btn, intValue) -> {
                    if (mHost.getModel() instanceof OneButton)
                        ((OneButton) mHost.getModel()).shape = intValue;

                    onWidgetListener();
                });
        groupShape = (RadioGroup) scrollGroupShape.getChildAt(0);
        return scrollGroupShape;
    }

    @Override
    protected View createAltEditView(Context c) {
        return null;
    }

    @Override
    public void updateUIFromModel(TouchAreaModel model) {
        if(model instanceof OneButton)
            ((RadioButton) groupShape.getChildAt(((OneButton)model).shape)).setChecked(true);

    }
}
