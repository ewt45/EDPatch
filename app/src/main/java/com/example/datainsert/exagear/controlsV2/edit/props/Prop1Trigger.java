package com.example.datainsert.exagear.controlsV2.edit.props;

import android.content.Context;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.example.datainsert.exagear.RR;
import com.example.datainsert.exagear.controlsV2.model.OneButton;
import com.example.datainsert.exagear.controlsV2.TouchAreaModel;

public class Prop1Trigger extends Prop<TouchAreaModel>{
    CompoundButton checkTrigger;
    public Prop1Trigger(Host<TouchAreaModel> host, Context c) {
        super(host, c);
    }

    @Override
    public String getTitle() {
        return "";
    }

    @Override
    protected View createMainEditView(Context c) {
        checkTrigger = new CheckBox(c);
        checkTrigger.setText(RR.getS(RR.cmCtrl_BtnEditTrigger));//按下不回弹
        checkTrigger.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(!(mHost.getModel() instanceof OneButton))
                return;

            ((OneButton) mHost.getModel()).setTrigger(isChecked);
            onWidgetListener();
        });
        return checkTrigger;
    }

    @Override
    protected View createAltEditView(Context c) {
        return null;
    }

    @Override
    public void updateUIFromModel(TouchAreaModel model) {
        if(!(mHost.getModel() instanceof OneButton))
            return;
        checkTrigger.setChecked(((OneButton)model).isTrigger());
    }
}
