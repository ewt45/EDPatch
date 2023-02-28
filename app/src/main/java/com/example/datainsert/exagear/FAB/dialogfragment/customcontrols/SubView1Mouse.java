package com.example.datainsert.exagear.FAB.dialogfragment.customcontrols;

import static com.example.datainsert.exagear.FAB.dialogfragment.BaseFragment.getPreference;
import static com.example.datainsert.exagear.FAB.dialogfragment.BaseFragment.setDialogTooltip;
import static com.example.datainsert.exagear.RR.getS;
import static com.example.datainsert.exagear.controls.ControlsResolver.PREF_KEY_MOUSE_MOVE_RELATIVE;
import static com.example.datainsert.exagear.controls.ControlsResolver.PREF_KEY_SHOW_CURSOR;

import android.content.Context;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import com.eltechs.axs.Globals;
import com.eltechs.axs.activities.XServerDisplayActivity;
import com.eltechs.axs.applicationState.ApplicationStateBase;
import com.eltechs.axs.applicationState.EnvironmentAware;
import com.example.datainsert.exagear.RR;
import com.example.datainsert.exagear.controls.interfaceOverlay.gesture.State1FMoveRel;

public class SubView1Mouse extends LinearLayout {

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

        //鼠标绝对位置或相对位置
        CheckBox switchMsMoveRel = new CheckBox(c);
        switchMsMoveRel.setText(getS(RR.cmCtrl_s1_relMove));
        switchMsMoveRel.setChecked(getPreference().getBoolean(PREF_KEY_MOUSE_MOVE_RELATIVE, false));
        switchMsMoveRel.setOnCheckedChangeListener((buttonView, isChecked) -> {
            getPreference().edit().putBoolean(PREF_KEY_MOUSE_MOVE_RELATIVE, isChecked).apply();
            State1FMoveRel.isRelMove = isChecked;
        });
        setDialogTooltip(switchMsMoveRel, getS(RR.cmCtrl_s1_relMoveTip));
        addView(switchMsMoveRel);


//        //为什么viewpager里的edittext没法调出输入法了啊
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
