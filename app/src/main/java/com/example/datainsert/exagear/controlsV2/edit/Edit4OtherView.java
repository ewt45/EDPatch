package com.example.datainsert.exagear.controlsV2.edit;

import static com.example.datainsert.exagear.RR.getS;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Switch;

import com.example.datainsert.exagear.RR;
import com.example.datainsert.exagear.controlsV2.Const;
import com.example.datainsert.exagear.controlsV2.model.ModelProvider;
import com.example.datainsert.exagear.controlsV2.widget.LimitEditText;
import com.example.datainsert.exagear.QH;
import com.example.datainsert.exagear.controlsV2.widget.TabPagerLayout;

public class Edit4OtherView extends LinearLayout {
    @SuppressLint("SetTextI18n")
    public Edit4OtherView(Context c) {
        super(c);
        setOrientation(VERTICAL);

        //保存
        Button btnSave = new Button(c);
        btnSave.setAllCaps(false);
        btnSave.setText(getS(RR.global_save));
        btnSave.setOnClickListener(v->ModelProvider.saveProfile(Const.getActiveProfile()));
        //保存并退出编辑
        Button btnExitEdit = new Button(c);
        btnExitEdit.setAllCaps(false);
        btnExitEdit.setText(getS(RR.ctr2_other_saveExit));//保存并退出编辑
        btnExitEdit.setOnClickListener(v -> Const.getTouchView().exitEdit());

        //鼠标移速
        LimitEditText editMsMvSpd = new LimitEditText(c)
                .setCustomInputType(LimitEditText.TYPE_NUMBER_FLOAT)
                .setRange(0f, 4f)
                .setFloatValue(Const.getActiveProfile().getMouseMoveSpeed())
                .setUpdateListener(editText -> Const.getActiveProfile().setMouseMoveSpeed(editText.getFloatValue()));

        //按键显隐
        Switch switchShowArea = new Switch(c);
        switchShowArea.setText(getS(RR.ctr2_other_showTouchArea));//显示屏幕按键（退出编辑时生效）
        switchShowArea.setChecked(Const.getActiveProfile().isShowTouchArea());
        switchShowArea.setOnCheckedChangeListener((buttonView, isChecked) -> Const.getActiveProfile().setShowTouchArea(isChecked));

        //用于测试中的更详细调试
        Switch switchDetailDebug = new Switch(c);
        switchDetailDebug.setText("Detailed Debug (only this time)");
        switchDetailDebug.setChecked(Const.detailDebug);
        switchDetailDebug.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Const.detailDebug=isChecked;
            Const.getTouchView().invalidate();
        });

        //java 垃圾回收
        Button btnGc = new Button(c);
        btnGc.setText("Java Garbage Collection");
        btnGc.setOnClickListener(v-> System.gc());

        LinearLayout linearSaves = new LinearLayout(c);
        linearSaves.setOrientation(HORIZONTAL);
        linearSaves.setVerticalGravity(Gravity.CENTER);
        linearSaves.addView(btnSave,QH.LPLinear.one(0,-2).weight().to());
        linearSaves.addView(btnExitEdit,QH.LPLinear.one(0,-2).weight().to());

        addView(linearSaves);
        addView(QH.getOneLineWithTitle(c,/*鼠标移动速度倍率*/getS(RR.ctr2_other_mouseSpeed),editMsMvSpd,true), QH.LPLinear.one(-1, -2).top().left().right().to());
        addView(switchShowArea,QH.LPLinear.one(-1,-2).top().left().right().to());
        addView(switchDetailDebug,QH.LPLinear.one(-1,-2).top().left().right().to());
//        addView(btnGc,QH.LPLinear.one(-1,-2).top().left().right().to());
        addView(new View(c),QH.LPLinear.one(-1,-2).bottom().to());
    }

    /**
     * 设置屏幕按键显隐，并将修改的设置保存到本地
     */
    public static void setProfileShowTouchArea(boolean showTouchArea){
        Const.getActiveProfile().setShowTouchArea(showTouchArea);
        Const.getTouchView().invalidate();
        //TODO (勿删）这种修改了profile的，切换了之后应该存入本地
        ModelProvider.saveProfile(Const.getActiveProfile());
    }

}
