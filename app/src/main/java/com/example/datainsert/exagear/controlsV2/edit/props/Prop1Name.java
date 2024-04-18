package com.example.datainsert.exagear.controlsV2.edit.props;

import static com.example.datainsert.exagear.RR.getS;

import android.content.Context;
import android.view.View;

import com.example.datainsert.exagear.RR;
import com.example.datainsert.exagear.controlsV2.TouchAreaModel;
import com.example.datainsert.exagear.controlsV2.model.OneButton;
import com.example.datainsert.exagear.controlsV2.widget.LimitEditText;

public class Prop1Name extends Prop<TouchAreaModel>{
    LimitEditText editName;
    public Prop1Name(Host<TouchAreaModel> host, Context c) {
        super(host, c);
    }

    @Override
    public void updateUIFromModel(TouchAreaModel model) {
        if(model instanceof OneButton) {
            String name = ((OneButton) model).getName();
            if(!editName.getText().toString().equals(name))
                editName.setStringValue(name);
        }
    }

    @Override
    public String getTitle() {
        return getS(RR.global_alias);
    }

    @Override
    protected View createMainEditView(Context c) {
        editName = new LimitEditText(c)
                .setCustomInputType(LimitEditText.TYPE_TEXT_SINGLE_LINE)
                .setUpdateListener(editText -> {
                    if(mHost.getModel() instanceof OneButton){
                        ((OneButton) mHost.getModel()).setName(editText.getStringValue());
                        onWidgetListener();
                    }
                });
        return editName;
    }

    @Override
    protected View createAltEditView(Context c) {
        return null;
    }
}
