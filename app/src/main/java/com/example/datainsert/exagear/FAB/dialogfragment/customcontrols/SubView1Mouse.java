package com.example.datainsert.exagear.FAB.dialogfragment.customcontrols;

import static com.example.datainsert.exagear.FAB.dialogfragment.BaseFragment.getOneLineWithTitle;
import static com.example.datainsert.exagear.FAB.dialogfragment.BaseFragment.getPreference;
import static com.example.datainsert.exagear.FAB.dialogfragment.BaseFragment.setDialogTooltip;
import static com.example.datainsert.exagear.RR.getS;
import static com.example.datainsert.exagear.controls.ControlsResolver.PREF_KEY_MOUSE_MOVE_RELATIVE;
import static com.example.datainsert.exagear.controls.ControlsResolver.PREF_KEY_MOUSE_SENSITIVITY;
import static com.example.datainsert.exagear.controls.ControlsResolver.PREF_KEY_SHOW_CURSOR;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;

import com.eltechs.axs.Globals;
import com.eltechs.axs.activities.XServerDisplayActivity;
import com.eltechs.axs.activities.XServerDisplayActivityInterfaceOverlay;
import com.eltechs.axs.applicationState.ApplicationStateBase;
import com.eltechs.axs.applicationState.EnvironmentAware;
import com.eltechs.axs.applicationState.XServerDisplayActivityConfigurationAware;
import com.example.datainsert.exagear.FAB.widget.SimpleSeekBarChangeListener;
import com.example.datainsert.exagear.RR;
import com.example.datainsert.exagear.controls.interfaceOverlay.FalloutInterfaceOverlay2;
import com.example.datainsert.exagear.controls.interfaceOverlay.gesture.State1FMoveRel;
import com.example.datainsert.exagear.controls.interfaceOverlay.widget.UnmovableBtn;

public class SubView1Mouse extends LinearLayout {
    private static final String TAG= "SubView1Mouse";
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
        SeekBar seekPointerSpeed = new SeekBar(c);
        seekPointerSpeed.setMax(280);
        seekPointerSpeed.setProgress(getPreference().getInt(PREF_KEY_MOUSE_SENSITIVITY,80));
        seekPointerSpeed.setOnSeekBarChangeListener(new SimpleSeekBarChangeListener((seekBar, progress, fromUser) -> getPreference().edit().putInt(PREF_KEY_MOUSE_SENSITIVITY,progress).apply()));
        //重置灵敏度按钮
        Button btnResetSpeed = new Button(c);
        btnResetSpeed.setText(getS(RR.cmCtrl_reset));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            btnResetSpeed.setTextAppearance(android.R.style.TextAppearance_Material_Widget_Button_Borderless_Colored);
            btnResetSpeed.setBackground(new RippleDrawable(ColorStateList.valueOf(0x44444444),null,btnResetSpeed.getBackground()));
        }
        btnResetSpeed.setOnClickListener(v-> seekPointerSpeed.setProgress(80));
        LinearLayout linearSeekNBtn = new LinearLayout(c);
        LayoutParams seekParams = new LayoutParams(0,-2,1);
        seekParams.gravity= Gravity.CENTER_VERTICAL;
        linearSeekNBtn.addView(seekPointerSpeed,seekParams);
        LayoutParams btnResetParams = new LayoutParams(-2,-2);
        btnResetParams.gravity= Gravity.CENTER_VERTICAL;
        linearSeekNBtn.addView(btnResetSpeed,btnResetParams);
        LinearLayout linearSpeed = getOneLineWithTitle(c,getS(RR.cmCtrl_s1_msSpd),linearSeekNBtn,true);
        setDialogTooltip(linearSpeed.getChildAt(0),getS(RR.cmCtrl_s1_msSpdTip));
        //初始化是否禁用
        boolean initChecked = getPreference().getBoolean(PREF_KEY_MOUSE_MOVE_RELATIVE, false);
        seekPointerSpeed.setEnabled(initChecked);
        seekPointerSpeed.setFocusable(initChecked);
        btnResetSpeed.setEnabled(initChecked);
        btnResetSpeed.setFocusable(initChecked);

        //鼠标绝对位置或相对位置
        CheckBox switchMsMoveRel = new CheckBox(c);
        switchMsMoveRel.setText(getS(RR.cmCtrl_s1_relMove));
        switchMsMoveRel.setChecked(getPreference().getBoolean(PREF_KEY_MOUSE_MOVE_RELATIVE, false));
        //没法通过setCheck在初始化的时候触发这个，因为false的话没改变。。。
        switchMsMoveRel.setOnCheckedChangeListener((buttonView, isChecked) -> {
            getPreference().edit().putBoolean(PREF_KEY_MOUSE_MOVE_RELATIVE, isChecked).apply();
            State1FMoveRel.isRelMove = isChecked;
            seekPointerSpeed.setEnabled(isChecked);
            seekPointerSpeed.setFocusable(isChecked);
            btnResetSpeed.setEnabled(isChecked);
            btnResetSpeed.setFocusable(isChecked);
        });

        setDialogTooltip(switchMsMoveRel, getS(RR.cmCtrl_s1_relMoveTip));
        addView(switchMsMoveRel);
        addView(linearSpeed);

        CheckBox checkLock = new CheckBox(c);
        checkLock.setText("Cursor Locked in center");
        checkLock.setChecked(FalloutInterfaceOverlay2.isCursorLocked);
        checkLock.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (((ApplicationStateBase) Globals.getApplicationState()).getCurrentActivity() instanceof XServerDisplayActivity) {
                XServerDisplayActivityInterfaceOverlay ui = ((XServerDisplayActivityConfigurationAware) Globals.getApplicationState()).getXServerDisplayActivityInterfaceOverlay();
                if (ui instanceof FalloutInterfaceOverlay2)
                    ((FalloutInterfaceOverlay2) ui).setCursorLocked(isChecked);
            }
        });

        addView(checkLock);

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


}
