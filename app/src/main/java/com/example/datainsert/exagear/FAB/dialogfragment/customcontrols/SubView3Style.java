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
import static com.example.datainsert.exagear.controls.ControlsResolver.PREF_KEY_BTN_ROUND_SHAPE;
import static com.example.datainsert.exagear.controls.ControlsResolver.PREF_KEY_BTN_TXT_COLOR;
import static com.example.datainsert.exagear.controls.ControlsResolver.PREF_KEY_BTN_WIDTH;
import static com.example.datainsert.exagear.controls.ControlsResolver.PREF_KEY_BTN__TXT_SIZE;
import static com.example.datainsert.exagear.controls.ControlsResolver.PREF_KEY_CUSTOM_BTN_POS;
import static com.example.datainsert.exagear.controls.ControlsResolver.PREF_KEY_SIDEBAR_COLOR;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.InsetDrawable;
import android.graphics.drawable.RippleDrawable;
import android.os.Build;
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
import android.widget.CompoundButton;
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
import com.example.datainsert.exagear.controls.interfaceOverlay.widget.UnmovableBtn;

import java.util.Timer;
import java.util.TimerTask;

public class SubView3Style extends LinearLayout {
    public static final String TAG = "SubView3Style";

    public SubView3Style(Context c) {
        super(c);
        setOrientation(VERTICAL);
        LinearLayout.LayoutParams eqWidthParams = new LinearLayout.LayoutParams(0, -2);
        eqWidthParams.weight = 1;
        eqWidthParams.gravity = Gravity.CENTER_VERTICAL;

        //????????????
//        Button btnSample = new Button(c);
//        btnSample.setText(getS(RR.cmCtrl_s3_sampleBtn));
//        btnSample.setTextSize(TypedValue.COMPLEX_UNIT_SP,getPreference().getInt(PREF_KEY_BTN__TXT_SIZE,4)+10);
////        btnSample.setBackground(getPreference().getBoolean(PREF_KEY_BTN_BG_RIPPLE,false)
////                ?new RippleDrawable(ColorStateList.valueOf(getPreference().getInt(PREF_KEY_BTN_BG_COLOR,0xffffffff)),null,null)
////                :new ColorDrawable(getPreference().getInt(PREF_KEY_BTN_BG_COLOR,0xffffffff)));
////        Log.d(TAG, "buildUI: ???????????????????????????" + btnSample.getBackground().getAlpha());
//        btnSample.setBackgroundTintList(ColorStateList.valueOf(getPreference().getInt(PREF_KEY_BTN_BG_COLOR, Color.WHITE)));
//        btnSample.setTextColor(getPreference().getInt(PREF_KEY_BTN_TXT_COLOR, Color.BLACK));
//        RippleDrawable spdrawable = (RippleDrawable) btnSample.getBackground();
//        spdrawable.setColor(ColorStateList.valueOf(getPreference().getInt(PREF_KEY_BTN_TXT_COLOR, Color.BLACK) & 0x50ffffff));
//        //???????????????drawable??????????????????setBackgroundTintList????????????????????????????????????255 (????????????????????????mutate()?????????????????????????????????
//        spdrawable.setAlpha(getPreference().getInt(PREF_KEY_BTN_ALPHA, 255));
        Button btnSample = UnmovableBtn.getSample(c);
        btnSample.setText(getS(RR.cmCtrl_s3_sampleBtn));
        LinearLayout.LayoutParams btnSampleParams = new LinearLayout.LayoutParams(getPreference().getInt(PREF_KEY_BTN_WIDTH, -2), getPreference().getInt(PREF_KEY_BTN_HEIGHT, -2));
        btnSampleParams.gravity = Gravity.CENTER;
        addView(btnSample,btnSampleParams);

        //????????????
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
        linearWHSeek.addView(seekBarWidth, generateLayoutParams(eqWidthParams));
        linearWHSeek.addView(seekBarHeight, generateLayoutParams(eqWidthParams));

        LinearLayout oneLineWHSeek = getOneLineWithTitle(c, getS(RR.cmCtrl_s3_btnSize), linearWHSeek, true);
        setDialogTooltip(oneLineWHSeek.getChildAt(0), getS(RR.cmCtrl_s3_btnSizeTip));
        addView(oneLineWHSeek);

        //????????????
        SeekBar seekTxtSize = new SeekBar(c);
        //10sp~30sp ??????14sp . progress???0~20
        seekTxtSize.setMax(20);
        seekTxtSize.setProgress(getPreference().getInt(PREF_KEY_BTN__TXT_SIZE,4));
        seekTxtSize.setOnSeekBarChangeListener(new SimpleSeekBarChangeListener((seekBar, progress, fromUser) -> {
            btnSample.setTextSize(TypedValue.COMPLEX_UNIT_SP,progress+10);
            getPreference().edit().putInt(PREF_KEY_BTN__TXT_SIZE,progress).apply();
        }));
        LinearLayout linearTxtSize = getOneLineWithTitle(c,getS(RR.cmCtrl_s2_txtSize),seekTxtSize,true);
        addView(linearTxtSize);

        //????????????

        //?????????????????????????????????????????????????????????????????????generateParams???????????????????????????

        EditText editInColor = new EditText(c);
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
            int colorAlpha = (int) (getPreference().getInt(PREF_KEY_BTN_ALPHA, 255) * 2.0f / 3 + 85); //??????????????????????????????1/3
            txColor = txColor & (colorAlpha << 24 | 0x00ffffff);
            ((RippleDrawable) btnSample.getBackground()).setColor(ColorStateList.valueOf(txColor & 0x50ffffff));
            btnSample.setBackgroundTintList(ColorStateList.valueOf(bgColor));
            btnSample.setTextColor(txColor);
            getPreference().edit()
                    .putInt(PREF_KEY_BTN_BG_COLOR, bgColor)
                    .putInt(PREF_KEY_BTN_TXT_COLOR, txColor).apply();
        }));
        //?????????????????????dialog???window???flag ??????????????? ????????????
//        editInColor.setOnClickListener(v->{
//            editInColor.setFocusable(true);
//            editInColor.setFocusableInTouchMode(true);
//            editInColor.requestFocus();
////            CustomControls.checkFocus();
//            InputMethodManager inputManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
////                            inputManager.showSoftInput(editInColor, 0);
//            inputManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
//        });

        //???????????????
        SeekBar seekAlpha = new SeekBar(c);
        seekAlpha.setMax(255);
        seekAlpha.setProgress(getPreference().getInt(PREF_KEY_BTN_ALPHA, 255));
        seekAlpha.setOnSeekBarChangeListener(new SimpleSeekBarChangeListener((seekBar, progress, fromUser) -> {
            btnSample.getBackground().setAlpha(progress);
            int txColor = getPreference().getInt(PREF_KEY_BTN_TXT_COLOR, Color.BLACK);
            int colorAlpha = (int) (progress * 2.0f / 3 + 85); //??????????????????????????????1/3
            txColor = (txColor & 0x00ffffff) | (colorAlpha << 24);
            btnSample.setTextColor(txColor);
            getPreference().edit().putInt(PREF_KEY_BTN_ALPHA, progress).putInt(PREF_KEY_BTN_TXT_COLOR, txColor).apply();
        }));


        LinearLayout linearCAContent = new LinearLayout(c);
        linearCAContent.addView(editInColor, generateLayoutParams(eqWidthParams));
        linearCAContent.addView(seekAlpha, generateLayoutParams(eqWidthParams));

//        LinearLayout linearCAOuter = getOneLineWithTitle(c,"????????????&?????????",linearCAContent,true  );
//        setDialogTooltip(linearCAOuter.getChildAt(0),"???????????????6?????????????????????????????????FAFAFA???\n????????????0-255????????????????????????0?????????????????????????????????????????????1/3???????????????");

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




        //??????????????????
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
        ((LayoutParams)linearSideColor.getChildAt(1).getLayoutParams()).topMargin=0;
        linearSideColor.getChildAt(1).setLayoutParams(linearSideColor.getChildAt(1).getLayoutParams());
        setDialogTooltip(linearSideColor.getChildAt(0),getS(RR.cmCtrl_s3_sideColorTip));
        addView(linearSideColor);

        //?????????????????????????????????
        CheckBox checkShapeRound = new CheckBox(c);
        checkShapeRound.setText(getS(RR.cmCtrl_s3_btnRoundShape));
        boolean initIsRound = getPreference().getBoolean(PREF_KEY_BTN_ROUND_SHAPE,false);
        checkShapeRound.setChecked(initIsRound);
        checkShapeRound.setOnCheckedChangeListener((buttonView, isChecked) -> setBtnShape(btnSample,isChecked));
        LayoutParams shapeRoundParams = new LayoutParams(-2,-2);
        shapeRoundParams.topMargin = 20;
        addView(checkShapeRound,shapeRoundParams);
    }


    /**
     * ???????????????????????????????????????preference
     *
     * @param btnSample ??????
     * @param isWidth   ?????????????????????
     * @param size      ???????????????dp
     */
    private void setBtnLayout(Button btnSample, boolean isWidth, int size) {
        ViewGroup.LayoutParams params = btnSample.getLayoutParams();
        int value = size < 10 ? -2 : AndroidHelpers.dpToPx(size);
        if (isWidth) params.width = value;
        else params.height = value;
        btnSample.setLayoutParams(params);
        getPreference().edit().putInt(isWidth ? PREF_KEY_BTN_WIDTH : PREF_KEY_BTN_HEIGHT, value).apply();

    }

    /**
     * ????????????????????????????????????,??????????????????sp
     * ???????????????drawable???????????????????????????????????????????????????drawable??????????????????????????????
     */
    private void setBtnShape(Button btnSample, boolean isRound){
        getPreference().edit().putBoolean(PREF_KEY_BTN_ROUND_SHAPE,isRound).apply();
//        RippleDrawable->InsetDrawable->GradientDrawable ????????????????????????insetDrawable???????????????
//        ???????????????mutate()????????????????????????????????????????????????????????????????????????
//        ??????????????????????????????drawable??????
        try {

            GradientDrawable gradientDrawable = new GradientDrawable();
            gradientDrawable.setCornerRadius(QH.px(getContext(),4));
            gradientDrawable.setShape(isRound?GradientDrawable.OVAL:GradientDrawable.RECTANGLE);
            gradientDrawable.setColor(ColorStateList.valueOf(getPreference().getInt(PREF_KEY_BTN_BG_COLOR, Color.WHITE)));
            InsetDrawable insetDrawable = new InsetDrawable(gradientDrawable,QH.px(getContext(),4));
            RippleDrawable rippleDrawable = new RippleDrawable(
                    ColorStateList.valueOf(getPreference().getInt(PREF_KEY_BTN_TXT_COLOR, Color.BLACK) & 0x50ffffff),
                    insetDrawable,
                    null
            );
            btnSample.setBackground(rippleDrawable);
            rippleDrawable.setAlpha(getPreference().getInt(PREF_KEY_BTN_ALPHA, 255));

//            RippleDrawable rippleDrawable = (RippleDrawable) btnSample.getBackground();
//            GradientDrawable drawable = new GradientDrawable();
//            drawable.setShape(isRound?GradientDrawable.OVAL:GradientDrawable.RECTANGLE);
//            drawable.setColor(getPreference().getInt(PREF_KEY_BTN_BG_COLOR, Color.WHITE));
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                rippleDrawable.addLayer(drawable);
//            }
////            InsetDrawable insetDrawable = new InsetDrawable()
//            gradientDrawable = (GradientDrawable) ((InsetDrawable)rippleDrawable.getDrawable(0)).getDrawable();
//            assert gradientDrawable != null;
//            gradientDrawable.setShape(isRound?GradientDrawable.OVAL:GradientDrawable.RECTANGLE);
        }catch (Exception e){
            Log.e(TAG, "setBtnShape: ?????????????????????GradientDrawable?????????????????????", e);
        }


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
