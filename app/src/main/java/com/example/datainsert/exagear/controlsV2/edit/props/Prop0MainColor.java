package com.example.datainsert.exagear.controlsV2.edit.props;

import static android.widget.LinearLayout.VERTICAL;
import static com.example.datainsert.exagear.RR.getS;
import static com.example.datainsert.exagear.controlsV2.Const.dp8;
import static com.example.datainsert.exagear.controlsV2.Const.minTouchSize;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.text.InputFilter;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.datainsert.exagear.RR;
import com.example.datainsert.exagear.controlsV2.Const;
import com.example.datainsert.exagear.controlsV2.TestHelper;
import com.example.datainsert.exagear.controlsV2.TouchAreaModel;
import com.example.datainsert.exagear.controlsV2.widget.colorpicker.ColorPicker;
import com.example.datainsert.exagear.QH;

public class Prop0MainColor extends Prop<TouchAreaModel> {
    GradientDrawable mDrawable;
    EditText mEdit;
    TextView mTvColorStyle;
    boolean isEditingEdit=false;
    int[] colorStyleInts = new int[]{Const.BtnColorStyle.STROKE, Const.BtnColorStyle.FILL};
    String[] colorStyleNames = RR.getSArr(RR.ctr2_prop_colorStyle_names); //描边, 填充

    public Prop0MainColor(Host<TouchAreaModel> host, Context c) {
        super(host, c);
    }

    @Override
    public void updateUIFromModel(TouchAreaModel model) {
        mDrawable.setColor(model.mainColor);

        String modelStr = Integer.toHexString(model.mainColor);
        if (!isEditingEdit && !mEdit.getText().toString().equals(modelStr))
            mEdit.setText(modelStr);

        mTvColorStyle.setText(colorStyleNames[model.colorStyle]);
    }

    /**
     * 这个因为有多个视图，所以没法用父类的判断是否是修改源的方法，因为只有一个修改，但其他两个没修改的需要同步，如果返回true，另外两个就没法同步了
     */
    @Override
    public boolean isChangingSource() {
        return false;
    }

    @Override
    public String getTitle() {
        return getS(RR.global_color);
    }

    @Override
    protected View createMainEditView(Context c) {
        ImageView imageBgColor = new ImageView(c);
        mDrawable = new GradientDrawable();
        mDrawable.setSize(minTouchSize * 2, minTouchSize);//需要给这个设置一个宽高，否则会按照bitmap的宽高来，非常小
        imageBgColor.setImageDrawable(ColorPicker.wrapAlphaAlertBg(c, mDrawable));
        imageBgColor.setMinimumHeight(minTouchSize);
        imageBgColor.setOnClickListener(v -> {
            LinearLayout linearColorRoot = new LinearLayout(c);
            linearColorRoot.setPadding(dp8, dp8, dp8, dp8);
            linearColorRoot.setOrientation(VERTICAL);
            linearColorRoot.addView(new ColorPicker(c, mHost.getModel().mainColor, argb -> {
                mHost.getModel().mainColor = argb;
                onWidgetListener();
            }));
            new AlertDialog.Builder(c)
                    .setView(TestHelper.wrapAsScrollView(linearColorRoot))
                    .setPositiveButton(android.R.string.ok, null)
                    .setCancelable(false)
                    .show();
//            mHost.getWindow().toNextView(linearColorRoot,getTitle());
        });

        mTvColorStyle = new TextView(c);
        mTvColorStyle.setPadding(dp8/2,dp8/2,dp8/2,dp8/2);
//        mTvColorStyle.setText("描边");
        mTvColorStyle.setTag(0);
        TestHelper.setTextViewSwapDrawable(mTvColorStyle);
        QH.setRippleBackground(mTvColorStyle);
        mTvColorStyle.setOnClickListener(v->{
            int oldStyle = mHost.getModel().colorStyle;
            mHost.getModel().colorStyle = (oldStyle+1)%colorStyleInts.length;
            onWidgetListener();
        });

        LinearLayout linearRoot = new LinearLayout(c);
        linearRoot.setOrientation(LinearLayout.HORIZONTAL);
        linearRoot.setVerticalGravity(Gravity.CENTER_VERTICAL);
        linearRoot.addView(imageBgColor);
        linearRoot.addView(mTvColorStyle,QH.LPLinear.one(-2,-2).left().to());

        return linearRoot;
    }

    @Override
    protected View createAltEditView(Context c) {
        //颜色 输入十六进制
        mEdit = new EditText(c);
        mEdit.setMinWidth(dp8*8);
        mEdit.setMinimumWidth(dp8*8);
        mEdit.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        mEdit.setFilters(new InputFilter[]{new InputFilter.LengthFilter(8)});
        mEdit.addTextChangedListener((QH.SimpleTextWatcher) s -> {
            for (int i = 0; i < s.length(); i++) {
                char c1 = s.charAt(i);
                if (!((c1 >= '0' && c1 <= '9') || (c1 >= 'a' && c1 <= 'f') || (c1 >= 'A' && c1 <= 'F'))) {
                    s.delete(i, i + 1);
                    return; //只要更改一处，就不往下走了，因为这次更改会再触发一次监听
                }
            }
            //用户输入->内容改变->设置到model，调用update-> update里又重新设置到edit，内容又改变？
            mHost.getModel().mainColor = TestHelper.getColorHexFromStr(s.toString());
            isEditingEdit=true;
            onWidgetListener();
            isEditingEdit=false;

        });
        return mEdit;
    }
}
