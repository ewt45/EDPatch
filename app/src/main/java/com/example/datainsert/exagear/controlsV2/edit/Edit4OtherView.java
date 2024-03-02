package com.example.datainsert.exagear.controlsV2.edit;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Switch;

import com.example.datainsert.exagear.controlsV2.Const;
import com.example.datainsert.exagear.controlsV2.model.ModelProvider;
import com.example.datainsert.exagear.controlsV2.widget.LimitEditText;
import com.example.datainsert.exagear.QH;

public class Edit4OtherView extends LinearLayout {
    @SuppressLint("SetTextI18n")
    public Edit4OtherView(Context c) {
        super(c);
        setOrientation(VERTICAL);

        //退出编辑
        Button btnExitEdit = new Button(c);
        btnExitEdit.setText("保存并退出编辑");
        btnExitEdit.setOnClickListener(v -> Const.getTouchView().exitEdit());

        //鼠标移速
        LimitEditText editMsMvSpd = new LimitEditText(c)
                .setCustomInputType(LimitEditText.TYPE_NUMBER_FLOAT)
                .setRange(0f, 4f)
                .setFloatValue(Const.getActiveProfile().getMouseMoveSpeed())
                .setUpdateListener(editText -> Const.getActiveProfile().setMouseMoveSpeed(editText.getFloatValue()));

        //按键显隐
        Switch switchShowArea = new Switch(c);
        switchShowArea.setText("显示屏幕按键");
        switchShowArea.setChecked(Const.getActiveProfile().isShowTouchArea());
        switchShowArea.setOnCheckedChangeListener((buttonView, isChecked) -> Const.getActiveProfile().setShowTouchArea(isChecked));


        addView(btnExitEdit);
        addView(QH.getOneLineWithTitle(c,"鼠标移动速度倍率",editMsMvSpd,true), QH.LPLinear.one(-1, -2).top().left().right().to());
        addView(switchShowArea,QH.LPLinear.one(-1,-2).top().left().right().to());
        addView(new View(c),QH.LPLinear.one(-1,-2).bottom().to());
    }

    /**
     * 设置屏幕按键显隐，并将修改的设置保存到本地
     */
    public static void setProfileShowTouchArea(boolean showTouchArea){
        Const.getActiveProfile().setShowTouchArea(showTouchArea);
        Const.getTouchView().invalidate();
        //TODO 这种修改了profile的，切换了之后应该存入本地
        ModelProvider.saveProfile(Const.getActiveProfile());
    }

}
