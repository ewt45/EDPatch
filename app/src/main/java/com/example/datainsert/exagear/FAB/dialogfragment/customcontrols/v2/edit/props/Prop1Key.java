package com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.edit.props;

import static com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.TestHelper.getTextButton;

import android.app.AlertDialog;
import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.model.TouchAreaModel;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.widget.KeyOnBoardView;

public class Prop1Key extends Prop{
    TextView tvKeycodes;
    public Prop1Key(Host host, Context c) {
        super(host, c);
    }

    @Override
    public String getTitle() {
        return "按键码";
    }

    @Override
    protected View createMainEditView(Context c) {
        //按键码
        tvKeycodes = getTextButton(c, "");
        tvKeycodes.setOnClickListener(v -> {
            TouchAreaModel model = mHost.getModel();
            KeyOnBoardView keyOnBoardView = new KeyOnBoardView(c);
            keyOnBoardView.setInitSelectedKeys(model.getKeycodes());
            new AlertDialog.Builder(v.getContext())
                    .setView(keyOnBoardView)
                    .setNegativeButton(android.R.string.cancel,null)
                    .setPositiveButton(android.R.string.ok,(dialog, which) -> {
                        model.setKeycodes(keyOnBoardView.getSelectedKeys());
                        onWidgetListener();
                    })
                    .show();
        });
        return tvKeycodes;
    }

    @Override
    protected View createAltEditView(Context c) {
        return null;
    }

    @Override
    public void updateUIFromModel(TouchAreaModel model) {
        tvKeycodes.setText(model.getKeycodesString());

    }
}
