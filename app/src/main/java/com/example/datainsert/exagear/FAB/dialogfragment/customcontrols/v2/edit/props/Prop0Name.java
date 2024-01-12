package com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.edit.props;

import android.content.Context;
import android.view.View;
import android.widget.EditText;

import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.model.TouchAreaModel;
import com.example.datainsert.exagear.QH;

public class Prop0Name extends Prop<TouchAreaModel>{
    EditText editName;
    public Prop0Name(Host<TouchAreaModel> host, Context c) {
        super(host, c);
    }

    @Override
    public void updateUIFromModel(TouchAreaModel model) {
        String name = model.getName();
        if(!editName.getText().toString().equals(name))
            editName.setText(name);
    }

    @Override
    public String getTitle() {
        return "名称";
    }

    @Override
    protected View createMainEditView(Context c) {
        editName = new EditText(c);
        editName.setSingleLine(true);
        editName.addTextChangedListener((QH.SimpleTextWatcher) s -> {
            mHost.getModel().setName(s.toString());
            onWidgetListener();
        });
        return editName;
    }

    @Override
    protected View createAltEditView(Context c) {
        return null;
    }
}
