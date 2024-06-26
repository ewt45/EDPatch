package com.example.datainsert.exagear.controlsV2.edit.props;

import static com.example.datainsert.exagear.controlsV2.TestHelper.getTextButton;

import android.app.AlertDialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.example.datainsert.exagear.QH;
import com.example.datainsert.exagear.RR;
import com.example.datainsert.exagear.controlsV2.TouchAreaModel;
import com.example.datainsert.exagear.controlsV2.widget.KeyOnBoardView;

public class Prop1Key extends Prop<TouchAreaModel>{
    TextView tvKeycodes;
    public Prop1Key(Host<TouchAreaModel> host, Context c) {
        super(host, c);
    }

    @Override
    public String getTitle() {
        return RR.getS(RR.global_keycode);
    }

    @Override
    protected View createMainEditView(Context c) {
        //按键码
        tvKeycodes = QH.TV.one(c).button().text("").text16Sp().textGravity(Gravity.START).to();
        tvKeycodes.setOnClickListener(v -> {
            TouchAreaModel model = mHost.getModel();
            KeyOnBoardView keyOnBoardView = new KeyOnBoardView(v.getContext());
            keyOnBoardView.setInitSelectedKeys(model.getKeycodes());
            new AlertDialog.Builder(v.getContext())
                    .setView(keyOnBoardView)
                    .setNegativeButton(android.R.string.cancel,null)
                    .setPositiveButton(android.R.string.ok,(dialog, which) -> {
                        model.setKeycodes(keyOnBoardView.getSelectedKeys());
                        updateUIFromModel(model);
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
