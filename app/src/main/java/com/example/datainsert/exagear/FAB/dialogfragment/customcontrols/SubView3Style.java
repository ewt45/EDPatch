package com.example.datainsert.exagear.FAB.dialogfragment.customcontrols;

import static android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL;
import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static com.example.datainsert.exagear.FAB.dialogfragment.BaseFragment.getOneLineWithTitle;
import static com.example.datainsert.exagear.FAB.dialogfragment.BaseFragment.getPreference;
import static com.example.datainsert.exagear.FAB.dialogfragment.BaseFragment.getTextViewWithText;
import static com.example.datainsert.exagear.FAB.dialogfragment.BaseFragment.setDialogTooltip;
import static com.example.datainsert.exagear.RR.getS;
import static com.example.datainsert.exagear.controls.ControlsResolver.PREF_KEY_BTN_ALPHA;
import static com.example.datainsert.exagear.controls.ControlsResolver.PREF_KEY_BTN_BG_COLOR;
import static com.example.datainsert.exagear.controls.ControlsResolver.PREF_KEY_BTN_HEIGHT;
import static com.example.datainsert.exagear.controls.ControlsResolver.PREF_KEY_BTN_TXT_COLOR;
import static com.example.datainsert.exagear.controls.ControlsResolver.PREF_KEY_BTN_WIDTH;
import static com.example.datainsert.exagear.controls.ControlsResolver.PREF_KEY_BTN__TXT_SIZE;
import static com.example.datainsert.exagear.controls.ControlsResolver.PREF_KEY_CUSTOM_BTN_POS;
import static com.example.datainsert.exagear.controls.ControlsResolver.PREF_KEY_SIDEBAR_COLOR;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.RippleDrawable;
import android.support.v4.graphics.ColorUtils;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.TextViewCompat;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.eltechs.axs.Globals;
import com.eltechs.axs.applicationState.ApplicationStateBase;
import com.eltechs.axs.helpers.AndroidHelpers;
//import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.widgets.ColorPickerView;
import com.example.datainsert.exagear.FAB.widget.SimpleSeekBarChangeListener;
import com.example.datainsert.exagear.FAB.widget.SimpleTextWatcher;
import com.example.datainsert.exagear.QH;
import com.example.datainsert.exagear.RR;

import java.util.Timer;
import java.util.TimerTask;

public class SubView3Style extends LinearLayout {
    public static final String TAG = "SubView3Style";

    public SubView3Style(Context c) {
        super(c);
        setOrientation(VERTICAL);

        //样例按钮
        Button btnSample = new Button(c);
        btnSample.setText(getS(RR.cmCtrl_s3_sampleBtn));
        btnSample.setTextSize(TypedValue.COMPLEX_UNIT_SP,getPreference().getInt(PREF_KEY_BTN__TXT_SIZE,4)+10);
        //设置一下自动缩放文字大小
//        TextViewCompat.setAutoSizeTextTypeWithDefaults(btnSample,TextViewCompat.AUTO_SIZE_TEXT_TYPE_UNIFORM);
//        btnSample.setBackground(getPreference().getBoolean(PREF_KEY_BTN_BG_RIPPLE,false)
//                ?new RippleDrawable(ColorStateList.valueOf(getPreference().getInt(PREF_KEY_BTN_BG_COLOR,0xffffffff)),null,null)
//                :new ColorDrawable(getPreference().getInt(PREF_KEY_BTN_BG_COLOR,0xffffffff)));
//        Log.d(TAG, "buildUI: 初始化透明度不对？" + btnSample.getBackground().getAlpha());
        btnSample.setBackgroundTintList(ColorStateList.valueOf(getPreference().getInt(PREF_KEY_BTN_BG_COLOR, Color.WHITE)));
        btnSample.setTextColor(getPreference().getInt(PREF_KEY_BTN_TXT_COLOR, Color.BLACK));
        RippleDrawable spdrawable = (RippleDrawable) btnSample.getBackground();
        spdrawable.setColor(ColorStateList.valueOf(getPreference().getInt(PREF_KEY_BTN_TXT_COLOR, Color.BLACK) & 0x50ffffff));
        //卧槽，背景drawable的透明度要在setBackgroundTintList之后设置，不然会被重置为255
        spdrawable.setAlpha(getPreference().getInt(PREF_KEY_BTN_ALPHA, 255));
        LinearLayout.LayoutParams btnSampleParams = new LinearLayout.LayoutParams(getPreference().getInt(PREF_KEY_BTN_WIDTH, -2), getPreference().getInt(PREF_KEY_BTN_HEIGHT, -2));
        btnSampleParams.gravity = Gravity.CENTER;
        addView(btnSample,btnSampleParams);



        //按钮颜色
        LinearLayout.LayoutParams linearParams = new LinearLayout.LayoutParams(0, -2);
        linearParams.weight = 1;
        linearParams.gravity = Gravity.CENTER_VERTICAL;
        //垂直居中且水平平分的线性布局参数，使用的时候用generateParams复制一个新的出来吧

        EditText editInColor = new EditText(c);
        editInColor.setMaxWidth(QH.px(c,100));
        editInColor.setSingleLine();
        editInColor.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        editInColor.setFilters(new InputFilter[]{new HexInputFilter(), new InputFilter.LengthFilter(6)});
        editInColor.setText(Integer.toHexString(getPreference().getInt(PREF_KEY_BTN_BG_COLOR, Color.WHITE)).substring(2));
        editInColor.addTextChangedListener(new SimpleTextWatcher(s -> {
            String s1 = s.toString();
            while (s1.length() < 6)
                s1 = s1.concat("f");
            int bgColor = Color.parseColor("#ff" + s1);
            int txColor = ColorUtils.calculateMinimumAlpha(Color.WHITE, bgColor, 4.5f) == -1 ? Color.BLACK : Color.WHITE;
            int colorAlpha = (int) (getPreference().getInt(PREF_KEY_BTN_ALPHA, 255) * 2.0f / 3 + 85); //文本透明度至少要保留1/3
            txColor = txColor & (colorAlpha << 24 | 0x00ffffff);
            ((RippleDrawable) btnSample.getBackground()).setColor(ColorStateList.valueOf(txColor & 0x50ffffff));
            btnSample.setBackgroundTintList(ColorStateList.valueOf(bgColor));
            btnSample.setTextColor(txColor);
            getPreference().edit()
                    .putInt(PREF_KEY_BTN_BG_COLOR, bgColor)
                    .putInt(PREF_KEY_BTN_TXT_COLOR, txColor).apply();
        }));
        //不显示键盘，改dialog的window的flag 输入法相关 就好了。
//        editInColor.setOnClickListener(v->{
//            editInColor.setFocusable(true);
//            editInColor.setFocusableInTouchMode(true);
//            editInColor.requestFocus();
////            CustomControls.checkFocus();
//            InputMethodManager inputManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
////                            inputManager.showSoftInput(editInColor, 0);
//            inputManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
//        });

        SeekBar seekAlpha = new SeekBar(c);
        seekAlpha.setMax(255);
        seekAlpha.setProgress(getPreference().getInt(PREF_KEY_BTN_ALPHA, 255));
        seekAlpha.setOnSeekBarChangeListener(new SimpleSeekBarChangeListener((seekBar, progress, fromUser) -> {
            btnSample.getBackground().setAlpha(progress);
            int txColor = getPreference().getInt(PREF_KEY_BTN_TXT_COLOR, Color.BLACK);
            int colorAlpha = (int) (progress * 2.0f / 3 + 85); //文本透明度至少要保留1/3
            txColor = (txColor & 0x00ffffff) | (colorAlpha << 24);
            btnSample.setTextColor(txColor);
            getPreference().edit().putInt(PREF_KEY_BTN_ALPHA, progress).putInt(PREF_KEY_BTN_TXT_COLOR, txColor).apply();
        }));


        LinearLayout linearCAContent = new LinearLayout(c);
        linearCAContent.addView(editInColor, generateLayoutParams(linearParams));
        linearCAContent.addView(seekAlpha, generateLayoutParams(linearParams));

//        LinearLayout linearCAOuter = getOneLineWithTitle(c,"按钮颜色&透明度",linearCAContent,true  );
//        setDialogTooltip(linearCAOuter.getChildAt(0),"按钮颜色为6位的十六进制颜色，如：FAFAFA。\n透明度为0-255，当透明度设置到0时，按钮背景完全透明，文字保留1/3的透明度。");

        LinearLayout linearCAOuter = new LinearLayout(c);
        linearCAOuter.setOrientation(LinearLayout.VERTICAL);
        TextView tvTitleCA = getTextViewWithText(c, getS(RR.cmCtrl_s3_btnColor));
        tvTitleCA.getPaint().setFakeBoldText(true);
        tvTitleCA.invalidate();
        setDialogTooltip(tvTitleCA, getS(RR.cmCtrl_s3_btnColorTip));
        linearCAOuter.addView(tvTitleCA);
        LinearLayout.LayoutParams linearCAContentparams = new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
        linearCAContentparams.setMarginStart(20);
        linearCAOuter.addView(linearCAContent, linearCAContentparams);
        linearCAOuter.setPadding(0, 20, 0, 0);
        addView(linearCAOuter);

        //按钮宽高
        SeekBar seekBarWidth = new SeekBar(c);
        seekBarWidth.setMax(200);
        int width = getPreference().getInt(PREF_KEY_BTN_WIDTH, -2);
        seekBarWidth.setProgress(width == -2 ? 0 : QH.dp(c,width));
        seekBarWidth.setOnSeekBarChangeListener(new SimpleSeekBarChangeListener((seekBar, progress, fromUser) -> setBtnLayout(btnSample, true, progress)));

        SeekBar seekBarHeight = new SeekBar(c);
        seekBarHeight.setMax(200);
        int height = getPreference().getInt(PREF_KEY_BTN_HEIGHT, -2);
        seekBarHeight.setProgress(height == -2 ? 0 : QH.dp(c,height));
        seekBarHeight.setOnSeekBarChangeListener(new SimpleSeekBarChangeListener((seekBar, progress, fromUser) -> setBtnLayout(btnSample, false, progress)));

        LinearLayout linearWHSeek = new LinearLayout(c);
        linearWHSeek.addView(seekBarWidth, generateLayoutParams(linearParams));
        linearWHSeek.addView(seekBarHeight, generateLayoutParams(linearParams));

        LinearLayout oneLineWHSeek = getOneLineWithTitle(c, getS(RR.cmCtrl_s3_btnSize), linearWHSeek, true);
        setDialogTooltip(oneLineWHSeek.getChildAt(0), getS(RR.cmCtrl_s3_btnSizeTip));
        addView(oneLineWHSeek);


        //侧栏背景颜色
        EditText editSideColor = new EditText(c);
        editSideColor.setSingleLine();
        editSideColor.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        editSideColor.setFilters(new InputFilter[]{new HexInputFilter(), new InputFilter.LengthFilter(6)});
        editSideColor.setText(Integer.toHexString(getPreference().getInt(PREF_KEY_SIDEBAR_COLOR, Color.BLACK)).substring(2));
        editSideColor.addTextChangedListener(new SimpleTextWatcher(s -> {
            String s1 = s.toString();
            while (s1.length() < 6)
                s1 = s1.concat("f");
            int sideBgColor = Color.parseColor("#ff" + s1);
            getPreference().edit().putInt(PREF_KEY_SIDEBAR_COLOR, sideBgColor).apply();
        }));
        LinearLayout linearSideColor = getOneLineWithTitle(c,getS(RR.cmCtrl_s3_sideColor),editSideColor,true);

        setDialogTooltip(linearSideColor.getChildAt(0),getS(RR.cmCtrl_s3_sideColorTip));
        addView(linearSideColor);

        SeekBar seekTxtSize = new SeekBar(c);
        //10sp~30sp 默认14sp . progress在0~20

        seekTxtSize.setMax(20);
        seekTxtSize.setProgress(getPreference().getInt(PREF_KEY_BTN__TXT_SIZE,4));
        seekTxtSize.setOnSeekBarChangeListener(new SimpleSeekBarChangeListener((seekBar, progress, fromUser) -> {
            btnSample.setTextSize(TypedValue.COMPLEX_UNIT_SP,progress+10);
            getPreference().edit().putInt(PREF_KEY_BTN__TXT_SIZE,progress).apply();
        }));
        LinearLayout linearTxtSize = getOneLineWithTitle(c,"文字大小",seekTxtSize,true);
        addView(linearTxtSize);
    }


    /**
     * 修改样例按钮的宽高，并写入preference
     *
     * @param btnSample 按钮
     * @param isWidth   改的是宽还是高
     * @param size      长度，单位dp
     */
    private void setBtnLayout(Button btnSample, boolean isWidth, int size) {
        ViewGroup.LayoutParams params = btnSample.getLayoutParams();
        int value = size < 10 ? -2 : AndroidHelpers.dpToPx(size);
        if (isWidth) params.width = value;
        else params.height = value;
        btnSample.setLayoutParams(params);
        getPreference().edit().putInt(isWidth ? PREF_KEY_BTN_WIDTH : PREF_KEY_BTN_HEIGHT, value).apply();

    }

//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//
//        int width =getMeasuredWidth()*3/4;
//        colorPickerView.getLayoutParams().width=width;
//        colorPickerView.getLayoutParams().height=width;
//        colorPickerView.setLayoutParams(colorPickerView.getLayoutParams());
//
//    }
}
