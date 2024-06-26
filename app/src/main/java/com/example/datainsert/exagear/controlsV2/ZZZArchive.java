package com.example.datainsert.exagear.controlsV2;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static android.widget.LinearLayout.HORIZONTAL;
import static com.example.datainsert.exagear.controlsV2.Const.dp8;
import static com.example.datainsert.exagear.controlsV2.TestHelper.getTextButton;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.graphics.ColorUtils;
import android.util.TypedValue;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.datainsert.exagear.QH;
import com.example.datainsert.exagear.RR;

public class ZZZArchive {
    private void popupWindow(TouchAreaModel mModel, Context c) {

        //坐标
        TextView tvCoordinate = getTextButton(c, "");
        tvCoordinate.setText("编辑");
        //改成dialog？ 添加宽高编辑
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
            popupWindow.setBackgroundDrawable(new ColorDrawable(RR.attr.colorBackground(c)));
            popupWindow.setOutsideTouchable(true);
            popupWindow.setFocusable(true);
            popupWindow.showAsDropDown((View) v.getParent());
        });
    }

    public static int getBGColor(Context c) {
        int color = RR.attr.colorBackground(c);
        return ColorUtils.blendARGB(0xababab, color, 0.3f);

    }

    public void inchToPx(){ //from: StateCountDownMeasureSpeed
        //感觉有问题，如果这个距离单位是inch的话，应该获取xdpi才对
        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_IN, 0.03f, Const.getContext().getResources().getDisplayMetrics());
        float dpi = Const.getDpi();
        float maxMovePx = Const.fingerStandingMaxMoveInches * dpi;
    }
}
