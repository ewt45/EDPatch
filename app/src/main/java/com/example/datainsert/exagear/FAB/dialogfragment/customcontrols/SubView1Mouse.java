package com.example.datainsert.exagear.FAB.dialogfragment.customcontrols;

import static com.example.datainsert.exagear.FAB.dialogfragment.BaseFragment.getPreference;
import static com.example.datainsert.exagear.FAB.dialogfragment.BaseFragment.setDialogTooltip;
import static com.example.datainsert.exagear.controls.ControlsResolver.PREF_KEY_BTN_BG_COLOR;
import static com.example.datainsert.exagear.controls.ControlsResolver.PREF_KEY_MOUSE_MOVE_RELATIVE;
import static com.example.datainsert.exagear.controls.ControlsResolver.PREF_KEY_SHOW_CURSOR;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.text.InputFilter;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.eltechs.axs.Globals;
import com.eltechs.axs.activities.XServerDisplayActivity;
import com.eltechs.axs.applicationState.ApplicationStateBase;
import com.eltechs.axs.applicationState.EnvironmentAware;
import com.example.datainsert.exagear.FAB.dialogfragment.BaseFragment;
import com.example.datainsert.exagear.FAB.widget.MyTextInputEditText;
import com.example.datainsert.exagear.controls.interfaceOverlay.gesture.State1FMoveRel;

public class SubView1Mouse extends LinearLayout {

    public SubView1Mouse(Context c) {
        super(c);
        setOrientation(VERTICAL);
        //鼠标显隐开关
        CheckBox showCursorCheck = new CheckBox(c);
        showCursorCheck.setText("显示鼠标光标");
        showCursorCheck.setChecked(getPreference().getBoolean(PREF_KEY_SHOW_CURSOR, true));
        showCursorCheck.setOnCheckedChangeListener((buttonView, isChecked) -> {
            getPreference().edit().putBoolean(PREF_KEY_SHOW_CURSOR, isChecked).apply();
            //如果XServerViewConfiguration已经存在，就直接改了
            if (((ApplicationStateBase) Globals.getApplicationState()).getCurrentActivity() instanceof XServerDisplayActivity) {
                ((EnvironmentAware) Globals.getApplicationState()).getXServerViewConfiguration().setShowCursor(isChecked);
            }
        });
//        TooltipCompat.setTooltipText(showCursorCheck,"设置进入容器后鼠标光标显示或隐藏。若因为添加“强制显示光标”功能导致同时显示默认鼠标光标和游戏自带光标，使用该选项可隐藏默认鼠标光标。");
        setDialogTooltip(showCursorCheck, "设置进入容器后鼠标光标显示或隐藏。若因为添加“强制显示光标”功能导致同时显示默认鼠标光标和游戏自带光标，使用该选项可隐藏默认鼠标光标。");
        addView(showCursorCheck);

        //鼠标绝对位置或相对位置
        CheckBox switchMsMoveRel = new CheckBox(c);
        switchMsMoveRel.setText("鼠标移动使用相对定位");
        switchMsMoveRel.setChecked(getPreference().getBoolean(PREF_KEY_MOUSE_MOVE_RELATIVE, false));
        switchMsMoveRel.setOnCheckedChangeListener((buttonView, isChecked) -> {
            getPreference().edit().putBoolean(PREF_KEY_MOUSE_MOVE_RELATIVE, isChecked).apply();
            State1FMoveRel.isRelMove = isChecked;
        });
        setDialogTooltip(switchMsMoveRel, "鼠标默认使用绝对定位，即鼠标会移动到手指点击的位置。切换到相对定位后，手指在屏幕滑动一段距离，鼠标随着手指移动一段距离。");
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
