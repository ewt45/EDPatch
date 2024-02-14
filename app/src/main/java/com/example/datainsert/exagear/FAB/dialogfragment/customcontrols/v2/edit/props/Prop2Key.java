package com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.edit.props;

import static com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.Const.dp8;
import static com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.TestHelper.getTextButton;

import android.app.AlertDialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.Const;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.model.OneStick;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.model.TouchAreaModel;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.widget.KeyOnBoardView;
import com.example.datainsert.exagear.QH;

import java.util.Collections;

public class Prop2Key extends Prop<TouchAreaModel>{
    TextView tvKeycodes;

    public Prop2Key(Host<TouchAreaModel> host, Context c) {
        super(host, c);
    }

    @Override
    public String getTitle() {
        return "按键码";
    }

    @Override
    protected View createMainEditView(Context c) {
        Button btnLeft = new Button(c);
        btnLeft.setOnClickListener(v-> onClickBtn(v.getContext(),OneStick.KEY_LEFT));
        Button btnRight = new Button(c);
        btnRight.setOnClickListener(v-> onClickBtn(v.getContext(),OneStick.KEY_RIGHT));
        Button btnTop = new Button(c);
        btnTop.setOnClickListener(v-> onClickBtn(v.getContext(),OneStick.KEY_TOP));
        Button btnBottom = new Button(c);
        btnBottom.setOnClickListener(v-> onClickBtn(v.getContext(),OneStick.KEY_BOTTOM));

        LinearLayout linearLine1 = new LinearLayout(c);
        linearLine1.addView(new TextView(c),QH.LPLinear.one(dp8*6,dp8*6).to());
        linearLine1.addView(btnTop, QH.LPLinear.one(dp8*6,dp8*6).to());

        LinearLayout linearLine2 = new LinearLayout(c);
        linearLine2.addView(btnLeft,QH.LPLinear.one(dp8*6,dp8*6).to());
        linearLine2.addView(new TextView(c),QH.LPLinear.one(dp8*6,dp8*6).to());
        linearLine2.addView(btnRight,QH.LPLinear.one(dp8*6,dp8*6).to());

        LinearLayout linearLine3 = new LinearLayout(c);
        linearLine3.addView(new TextView(c),QH.LPLinear.one(dp8*6,dp8*6).to());
        linearLine3.addView(btnBottom, QH.LPLinear.one(dp8*6,dp8*6).to());

        LinearLayout linearRoot = new LinearLayout(c);
        linearRoot.setPadding(dp8,dp8,dp8,dp8);
        linearRoot.setOrientation(LinearLayout.VERTICAL);
        linearRoot.addView(linearLine1,QH.LPLinear.one().gravity(Gravity.CENTER).to());
        linearRoot.addView(linearLine2,QH.LPLinear.one().gravity(Gravity.CENTER).to());
        linearRoot.addView(linearLine3,QH.LPLinear.one().gravity(Gravity.CENTER).to());

        tvKeycodes = getTextButton(c, "");
        tvKeycodes.setOnClickListener(v -> Const.getEditWindow().toNextView(linearRoot,"按键码"));
        return tvKeycodes;
    }

    private void onClickBtn(Context c, @OneStick.KeyPos int direction){
        if(!(mHost.getModel() instanceof  OneStick) )
            return;

        OneStick model = (OneStick) mHost.getModel();

        KeyOnBoardView keyOnBoardView = new KeyOnBoardView(c);
        keyOnBoardView.setInitSelectedKeys(Collections.singletonList(model.getKeycodeAt(direction)));
        keyOnBoardView.setOnlyAllowOne(true);
        new AlertDialog.Builder(c)
                .setView(keyOnBoardView)
                .setNegativeButton(android.R.string.cancel,null)
                .setPositiveButton(android.R.string.ok,(dialog, which) -> {
                    model.setKeycodeAt(keyOnBoardView.getSelectedKeys().get(0),direction);
                    updateUIFromModel(model);
                    onWidgetListener();
                })
                .show();
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
