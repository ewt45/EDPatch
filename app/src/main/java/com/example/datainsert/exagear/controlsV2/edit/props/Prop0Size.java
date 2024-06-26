package com.example.datainsert.exagear.controlsV2.edit.props;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static android.widget.LinearLayout.HORIZONTAL;
import static com.example.datainsert.exagear.controlsV2.Const.dp8;
import static com.example.datainsert.exagear.controlsV2.TestHelper.getTextButton;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.datainsert.exagear.RR;
import com.example.datainsert.exagear.controlsV2.Const;
import com.example.datainsert.exagear.controlsV2.TestHelper;
import com.example.datainsert.exagear.controlsV2.TouchAreaModel;
import com.example.datainsert.exagear.controlsV2.widget.LimitEditText;
import com.example.datainsert.exagear.controlsV2.widget.RangeSeekbar;
import com.example.datainsert.exagear.QH;

public class Prop0Size extends Prop<TouchAreaModel> {
    RangeSeekbar seekSize;
    LimitEditText editWidth;
    LimitEditText editHeight;
    boolean isSelfEditing = false;

    public Prop0Size(Host<TouchAreaModel> host, Context c) {
        super(host, c);
    }

    @Override
    protected View createMainEditView(Context c) {
        seekSize = new RangeSeekbar(c, Const.minBtnAreaSize, QH.px(c, 200)) {
            @Override
            protected int rawToFinal(int rawValue) {
                return rawValue * 2;
            }

            @Override
            protected int finalToRaw(int finalValue) {
                return finalValue / 2;
            }
        };
        seekSize.setOnValueChangeListener((seekbar, value, fromUser) -> {
            if (mIsChangingSource)
                return;
            Log.d("TAG", "createMainEditView: 大小=" + value);
            mIsChangingSource = true;

            mHost.getModel().setWidth(value);
            mHost.getModel().setHeight(value);

            editWidth.setText(String.valueOf(mHost.getModel().getWidth()));
            editHeight.setText(String.valueOf(mHost.getModel().getHeight()));

            onWidgetListener();
            mIsChangingSource = false;
        });

        return seekSize;

    }

    @Override
    public void updateUIFromModel(TouchAreaModel model) {
        if (mIsChangingSource)
            return;

        seekSize.setValue(Math.min(model.getWidth(), model.getHeight()));
        editWidth.setText(String.valueOf(model.getWidth()));
        editHeight.setText(String.valueOf(model.getHeight()));
    }

    @Override
    public String getTitle() {
        return RR.getS(RR.global_size);
    }


    @Override
    protected View createAltEditView(Context c) {
        editWidth = new LimitEditText(c)
                .setCustomInputType(LimitEditText.TYPE_NUMBER_INT)
                .setUpdateListener(editText -> {
                    if (editText.getIntValue()==0 || mIsChangingSource)
                        return;
                    mIsChangingSource=true;
                    TouchAreaModel model = mHost.getModel();
                    model.setWidth(editText.getIntValue());
                    seekSize.setValue(Math.min(model.getWidth(), model.getHeight()));
                    onWidgetListener();
                    mIsChangingSource=false;
                });
        editWidth.setMinWidth(dp8*7);
        editWidth.setMinimumWidth(dp8*7);


        editHeight = new LimitEditText(c)
                .setCustomInputType(LimitEditText.TYPE_NUMBER_INT)
                .setUpdateListener(editText -> {
                    if (editText.getIntValue() == 0 || mIsChangingSource)
                        return;
                    mIsChangingSource=true;
                    TouchAreaModel model = mHost.getModel();
                    model.setHeight(editText.getIntValue());
                    seekSize.setValue(Math.min(model.getWidth(), model.getHeight()));
                    onWidgetListener();
                    mIsChangingSource=false;
                });
        editHeight.setMinWidth(dp8*7);
        editHeight.setMinimumWidth(dp8*7);


        TextView tvCross = new TextView(c);
        tvCross.setText("×");

        LinearLayout linearLayout = new LinearLayout(c);
        linearLayout.setOrientation(HORIZONTAL);
        linearLayout.addView(editWidth, QH.LPLinear.one(-2, -2).left().to());
        linearLayout.addView(tvCross, QH.LPLinear.one(-2, -2).left().to());
        linearLayout.addView(editHeight, QH.LPLinear.one(-2, -2).left().right().to());
        return linearLayout;
    }
}
