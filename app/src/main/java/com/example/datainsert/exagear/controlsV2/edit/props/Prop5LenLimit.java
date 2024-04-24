package com.example.datainsert.exagear.controlsV2.edit.props;

import android.content.Context;
import android.view.View;

import com.example.datainsert.exagear.RR;
import com.example.datainsert.exagear.controlsV2.TouchAreaModel;
import com.example.datainsert.exagear.controlsV2.model.OneColumn;
import com.example.datainsert.exagear.controlsV2.widget.LimitEditText;

public class Prop5LenLimit extends Prop<TouchAreaModel> {
    LimitEditText editRestrict;
    public Prop5LenLimit(Host<TouchAreaModel> host, Context c) {
        super(host, c);
    }

    @Override
    public String getTitle() {
        return RR.getS(RR.ctr2_prop_lengthLimit);
    }

    @Override
    protected View createMainEditView(Context c) {
        editRestrict = new LimitEditText(c)
                .setCustomInputType(LimitEditText.TYPE_NUMBER_INT)
                .setUpdateListener(editText -> {
                   if(!(mHost.getModel() instanceof OneColumn))
                       return;
                   OneColumn model = (OneColumn) mHost.getModel();
                    //因为需要调整为dp8 倍数，所以edit.getIntValue并不一定是设置后的值。需要set后再获取
                   int oldMax = model.getLengthLimit();
                   model.setLengthLimit(editText.getIntValue());
                   int newMax = model.getLengthLimit();
                   if(oldMax != newMax)
                       onWidgetListener();
                });

        return editRestrict;
    }

    @Override
    protected View createAltEditView(Context c) {
        return null;
    }

    @Override
    public void updateUIFromModel(TouchAreaModel model) {
        if(model instanceof OneColumn)
            editRestrict.setIntValue(((OneColumn) model).getLengthLimit());
    }
}
