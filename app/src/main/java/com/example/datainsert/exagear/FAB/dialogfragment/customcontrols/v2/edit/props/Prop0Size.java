package com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.edit.props;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static android.widget.LinearLayout.HORIZONTAL;
import static com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.Const.dp8;
import static com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.TestHelper.getTextButton;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.TestHelper;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.model.TouchAreaModel;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.widget.RangeSeekbar;
import com.example.datainsert.exagear.QH;

public class Prop0Size extends Prop<TouchAreaModel> {
    RangeSeekbar seekSize;
    EditText editWidth;
    EditText editHeight;
    boolean isSelfEditing = false;

    public Prop0Size(Host<TouchAreaModel> host, Context c) {
        super(host, c);
    }

    @Override
    protected View createMainEditView(Context c) {
        seekSize = new RangeSeekbar(c, dp8 * 6, QH.px(c, 200)) {
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

//        LinearLayout linearRoot = new LinearLayout(c);
//        linearRoot.setOrientation(LinearLayout.VERTICAL);
//        linearRoot.addView(QH.getOneLineWithTitle(c,"宽"));
//
//        TextView btn = new TextView(c);
//        btn.setText("编辑");
//        btn.setOnClickListener(v -> mHost.getWindow().toNextView(, "位置和大小"));
//        return btn;
    }

    private void popupWindow(TouchAreaModel mModel, Context c) {

        //坐标
        TextView tvCoordinate = getTextButton(c, "");
        tvCoordinate.setText("编辑");
        //TODO 改成dialog？ 添加宽高编辑
        tvCoordinate.setOnClickListener(v -> {
            EditText editPlaceX = new EditText(c);
            editPlaceX.setSingleLine(true);
            editPlaceX.setInputType(EditorInfo.TYPE_CLASS_NUMBER);

            EditText editPlaceY = new EditText(c);
            editPlaceY.setInputType(EditorInfo.TYPE_CLASS_NUMBER);
            editPlaceY.setSingleLine(true);

            LinearLayout linearPlace = new LinearLayout(c);
            linearPlace.setOrientation(HORIZONTAL);
            linearPlace.setPadding(dp8, dp8, dp8, dp8);
            linearPlace.addView(editPlaceX, QH.LPLinear.one(0, -2).weight().to());
            linearPlace.addView(editPlaceY, QH.LPLinear.one(0, -2).weight().left().to());

            PopupWindow popupWindow = new PopupWindow(c);
            Button btnConfirm = new Button(c);
            btnConfirm.setText("确定");
            btnConfirm.setOnClickListener(v2 -> {
                String xStr = editPlaceX.getText().toString().trim();
                String yStr = editPlaceY.getText().toString().trim();
                mModel.setLeft(xStr.length() == 0 ? 0 : Integer.parseInt(xStr));
                mModel.setTop(yStr.length() == 0 ? 0 : Integer.parseInt(yStr));
//                updateModel(mModel);
                popupWindow.dismiss();
            });
            LinearLayout linearPopupRoot = new LinearLayout(c);
            linearPopupRoot.setOrientation(HORIZONTAL);
            linearPopupRoot.addView(linearPlace, QH.LPLinear.one(0, -2).weight().to());
            linearPopupRoot.addView(btnConfirm, QH.LPLinear.one(-2, -2).to());
            popupWindow.setContentView(linearPopupRoot);
//            popupWindow.setWidth(getWidth());
            popupWindow.setHeight(WRAP_CONTENT);
            popupWindow.setBackgroundDrawable(new ColorDrawable(TestHelper.getBGColor(c)));
            popupWindow.setOutsideTouchable(true);
            popupWindow.setFocusable(true);
            popupWindow.showAsDropDown((View) v.getParent());
        });
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
        return "尺寸";
    }


    @Override
    protected View createAltEditView(Context c) {
        editWidth = new EditText(c);
        editWidth.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        editWidth.addTextChangedListener((QH.SimpleTextWatcher) s -> {
            if (s.toString().length() == 0 || mIsChangingSource)
                return;
            mIsChangingSource=true;
            TouchAreaModel model = mHost.getModel();
            model.setWidth(Integer.parseInt(s.toString()));
            seekSize.setValue(Math.min(model.getWidth(), model.getHeight()));
            onWidgetListener();
            mIsChangingSource=false;
        });

        editHeight = new EditText(c);
        editHeight.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        editHeight.addTextChangedListener((QH.SimpleTextWatcher) s -> {
            if (s.toString().length() == 0 || mIsChangingSource)
                return;
            mIsChangingSource=true;
            TouchAreaModel model = mHost.getModel();
            model.setHeight(Integer.parseInt(s.toString()));
            seekSize.setValue(Math.min(model.getWidth(), model.getHeight()));
            onWidgetListener();
            mIsChangingSource=false;
        });

        TextView tvCross = new TextView(c);
        tvCross.setText("×");

        LinearLayout linearLayout = new LinearLayout(c);
        linearLayout.setOrientation(HORIZONTAL);
        linearLayout.addView(editWidth, QH.LPLinear.one(dp8 * 7, -2).left().to());
        linearLayout.addView(tvCross, QH.LPLinear.one(-2, -2).left().to());
        linearLayout.addView(editHeight, QH.LPLinear.one(dp8 * 7, -2).left().to());
        return linearLayout;
    }
}
