package com.example.datainsert.exagear.FAB.dialogfragment.customcontrols;

import static com.example.datainsert.exagear.FAB.dialogfragment.BaseFragment.getPreference;
import static com.example.datainsert.exagear.FAB.dialogfragment.BaseFragment.setDialogTooltip;
import static com.example.datainsert.exagear.RR.cmCtrl_s1_msMoveViewport;
import static com.example.datainsert.exagear.RR.getS;
import static com.example.datainsert.exagear.controls.ControlsResolver.PREF_KEY_MOUSE_MOVE_RELATIVE;
import static com.example.datainsert.exagear.controls.ControlsResolver.PREF_KEY_MOUSE_OFFWINDOW_DISTANCE;
import static com.example.datainsert.exagear.controls.ControlsResolver.PREF_KEY_MOUSE_SENSITIVITY;
import static com.example.datainsert.exagear.controls.ControlsResolver.PREF_KEY_MOUSE_VIEWPORT_ENABLE;
import static com.example.datainsert.exagear.controls.ControlsResolver.PREF_KEY_MOUSE_VIEWPORT_INTERVAL;
import static com.example.datainsert.exagear.controls.ControlsResolver.PREF_KEY_SHOW_CURSOR;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.RippleDrawable;
import android.os.Build;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;

import com.eltechs.axs.Globals;
import com.eltechs.axs.activities.XServerDisplayActivity;
import com.eltechs.axs.applicationState.ApplicationStateBase;
import com.eltechs.axs.applicationState.EnvironmentAware;
import com.example.datainsert.exagear.FAB.widget.SimpleSeekBarChangeListener;
import com.example.datainsert.exagear.QH;
import com.example.datainsert.exagear.RR;

public class SubView1Mouse extends LinearLayout {
    private static final String TAG = "SubView1Mouse";
    SeekBar seekPointerSpeed;
    Button btnResetSpeed;
    LinearLayout viewportOptions;
    CheckBox checkMsViewport;

    public SubView1Mouse(Context c) {
        super(c);
        setOrientation(VERTICAL);
        //鼠标显隐开关
        CheckBox showCursorCheck = new CheckBox(c);
        showCursorCheck.setText(getS(RR.cmCtrl_s1_showCursor));
        showCursorCheck.setChecked(getPreference().getBoolean(PREF_KEY_SHOW_CURSOR, true));
        showCursorCheck.setOnCheckedChangeListener((buttonView, isChecked) -> {
            getPreference().edit().putBoolean(PREF_KEY_SHOW_CURSOR, isChecked).apply();
            //如果XServerViewConfiguration已经存在，就直接改了
            if (((ApplicationStateBase) Globals.getApplicationState()).getCurrentActivity() instanceof XServerDisplayActivity) {
                ((EnvironmentAware) Globals.getApplicationState()).getXServerViewConfiguration().setShowCursor(isChecked);
            }
        });
//        TooltipCompat.setTooltipText(showCursorCheck,"设置进入容器后鼠标光标显示或隐藏。若因为添加“强制显示光标”功能导致同时显示默认鼠标光标和游戏自带光标，使用该选项可隐藏默认鼠标光标。");
        setDialogTooltip(showCursorCheck, getS(RR.cmCtrl_s1_showCursorTip));
        addView(showCursorCheck);

        //鼠标灵敏度 0.2~3.0, 设置值从0到280
        seekPointerSpeed = new SeekBar(c);
        seekPointerSpeed.setMax(280);
        seekPointerSpeed.setProgress(getPreference().getInt(PREF_KEY_MOUSE_SENSITIVITY, 80));
        seekPointerSpeed.setOnSeekBarChangeListener(new SimpleSeekBarChangeListener((seekBar, progress, fromUser) -> getPreference().edit().putInt(PREF_KEY_MOUSE_SENSITIVITY, progress).apply()));
        //重置灵敏度按钮
        btnResetSpeed = new Button(c);
        btnResetSpeed.setText(getS(RR.cmCtrl_reset));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            btnResetSpeed.setTextAppearance(android.R.style.TextAppearance_Material_Widget_Button_Borderless_Colored);
            btnResetSpeed.setBackground(new RippleDrawable(ColorStateList.valueOf(0x44444444), null, btnResetSpeed.getBackground()));
        }
        btnResetSpeed.setOnClickListener(v -> seekPointerSpeed.setProgress(80));
        LinearLayout linearSeekNBtn = new LinearLayout(c);
        LayoutParams seekParams = new LayoutParams(0, -2, 1);
        seekParams.gravity = Gravity.CENTER_VERTICAL;
        linearSeekNBtn.addView(seekPointerSpeed, seekParams);
        LayoutParams btnResetParams = new LayoutParams(-2, -2);
        btnResetParams.gravity = Gravity.CENTER_VERTICAL;
        linearSeekNBtn.addView(btnResetSpeed, btnResetParams);
        LinearLayout linearSpeed = QH.getOneLineWithTitle(c, getS(RR.cmCtrl_s1_msSpd), linearSeekNBtn, true);
        //再手动设置一下顶部边距为0吧不然距离太远了
        ((LayoutParams) linearSpeed.getChildAt(1).getLayoutParams()).topMargin = 0;
        linearSpeed.getChildAt(1).setLayoutParams(linearSpeed.getChildAt(1).getLayoutParams());
        setDialogTooltip(linearSpeed.getChildAt(0), getS(RR.cmCtrl_s1_msSpdTip));


        //鼠标绝对位置或相对位置
        CheckBox checkMsMoveRel = new CheckBox(c);
        checkMsMoveRel.setText(getS(RR.cmCtrl_s1_relMove));
        checkMsMoveRel.setChecked(getPreference().getBoolean(PREF_KEY_MOUSE_MOVE_RELATIVE, false));
        //没法通过setCheck在初始化的时候触发这个，因为false的话没改变。。。
        checkMsMoveRel.setOnCheckedChangeListener((buttonView, isChecked) -> clickedCheckMoveRel(isChecked));

        setDialogTooltip(checkMsMoveRel, getS(RR.cmCtrl_s1_relMoveTip));
        addView(checkMsMoveRel);
        addView(linearSpeed);

        //允许鼠标移出屏幕（视角转动模式） 用户手动调节速度吧
        //
//        seekViewport = new SeekBar(c);
//        seekViewport.setMax(60);
//        seekViewport.setProgress(getPreference().getInt(PREF_KEY_MOUSE_OFFWINDOW_DISTANCE,0));
//        seekViewport.setOnSeekBarChangeListener(new SimpleSeekBarChangeListener((seekBar, progress, fromUser) -> getPreference().edit().putInt(PREF_KEY_MOUSE_OFFWINDOW_DISTANCE,progress).apply()));
        //类型为鼠标移动时显示选项 对应发送时间间隔。
        SeekBar repeatIntervalSeek = new SeekBar(c);
        repeatIntervalSeek.setMax(100);//5-105
        repeatIntervalSeek.setOnSeekBarChangeListener(new SimpleSeekBarChangeListener((seekBar, progress, fromUser) -> getPreference().edit().putInt(PREF_KEY_MOUSE_VIEWPORT_INTERVAL, progress).apply()));
        repeatIntervalSeek.setProgress(getPreference().getInt(PREF_KEY_MOUSE_VIEWPORT_INTERVAL, 20) + 5);
        //类型为鼠标移动时显示选项 每次移动距离
        SeekBar mouseMoveLenSeek = new SeekBar(c);
        mouseMoveLenSeek.setMax(60); //1-61
        mouseMoveLenSeek.setOnSeekBarChangeListener(new SimpleSeekBarChangeListener((seekBar, progress, fromUser) -> getPreference().edit().putInt(PREF_KEY_MOUSE_OFFWINDOW_DISTANCE, progress).apply()));
        mouseMoveLenSeek.setProgress(getPreference().getInt(PREF_KEY_MOUSE_OFFWINDOW_DISTANCE, 20));

        String[] viewportStrs = getS(RR.cmCtrl_s1_msMoveViewport).split("\\$");
        viewportOptions = new LinearLayout(c);
        viewportOptions.setOrientation(LinearLayout.HORIZONTAL);
        viewportOptions.addView(QH.getOneLineWithTitle(c, viewportStrs[1], repeatIntervalSeek, true), seekParams);
        viewportOptions.addView(QH.getOneLineWithTitle(c, viewportStrs[2], mouseMoveLenSeek, true), seekParams);

        checkMsViewport = new CheckBox(c);
        checkMsViewport.setText(viewportStrs[0]);
        checkMsViewport.setOnCheckedChangeListener((buttonView, isChecked) -> {
            getPreference().edit().putBoolean(PREF_KEY_MOUSE_VIEWPORT_ENABLE, isChecked).apply();
            clickedMoveViewport(isChecked);
        });
        checkMsViewport.setChecked(getPreference().getBoolean(PREF_KEY_MOUSE_VIEWPORT_ENABLE, false));

//        LinearLayout linearSeekViewport = getOneLineWithTitle(c,getS(RR.cmCtrl_s1_msOffScr),seekViewport,true);
//        setDialogTooltip(checkMsViewport, getS(RR.cmCtrl_s1_msOffScrTip));
        addView(checkMsViewport);
        addView(viewportOptions);

        //初始化是否禁用
        clickedCheckMoveRel(getPreference().getBoolean(PREF_KEY_MOUSE_MOVE_RELATIVE, false));

//        //为什么viewpager里的edittext没法调出输入法了啊(dialogfragment里清除一下flag就好了）
//        MyTextInputEditText editInColor = new MyTextInputEditText(c,null,null,"颜色");
////        EditText editInColor = new EditText(c);
//        editInColor.setSingleLine();
//        editInColor.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
//        editInColor.setFilters(new InputFilter[]{new HexInputFilter(), new InputFilter.LengthFilter(6)});
//        editInColor.setText(Integer.toHexString(getPreference().getInt(PREF_KEY_BTN_BG_COLOR, Color.WHITE)));
//
//        TextInputLayout textInputLayout2 = new TextInputLayout(c);
//        textInputLayout2.addView(editInColor);
//        addView(textInputLayout2);
    }


    private void clickedCheckMoveRel(boolean isChecked) {
        getPreference().edit().putBoolean(PREF_KEY_MOUSE_MOVE_RELATIVE, isChecked).apply();

        seekPointerSpeed.setEnabled(isChecked);
        seekPointerSpeed.setFocusable(isChecked);
        btnResetSpeed.setEnabled(isChecked);
        btnResetSpeed.setFocusable(isChecked);
        checkMsViewport.setEnabled(isChecked);
        clickedMoveViewport(isChecked && checkMsViewport.isChecked());
    }

    private void clickedMoveViewport(boolean isChecked) {
        viewportOptions.setVisibility(isChecked ? VISIBLE : GONE);
//        ((ViewGroup)viewportOptions.getChildAt(0)).getChildAt(1).setEnabled(isChecked);
//        ((ViewGroup)viewportOptions.getChildAt(1)).getChildAt(1).setEnabled(isChecked);
    }
}
