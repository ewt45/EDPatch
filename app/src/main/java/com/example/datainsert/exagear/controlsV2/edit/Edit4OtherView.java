package com.example.datainsert.exagear.controlsV2.edit;

import static com.example.datainsert.exagear.RR.getS;
import static com.example.datainsert.exagear.RR.getSArr;
import static com.example.datainsert.exagear.controlsV2.model.ModelProvider.readBundledProfilesFromAssets;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Switch;

import com.example.datainsert.exagear.RR;
import com.example.datainsert.exagear.controlsV2.Const;
import com.example.datainsert.exagear.controlsV2.TestHelper;
import com.example.datainsert.exagear.controlsV2.XServerViewHolder;
import com.example.datainsert.exagear.controlsV2.model.ModelProvider;
import com.example.datainsert.exagear.controlsV2.widget.LimitEditText;
import com.example.datainsert.exagear.QH;

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
                .setRange(0f, 30f)
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

        //重新解压内置配置
        Button btnBundledProfiles = new Button(c);
        btnBundledProfiles.setAllCaps(false);
        String[] strBundledProfiles = getSArr(RR.ctr2_other_reExtract);
        btnBundledProfiles.setText(strBundledProfiles[0]);
        btnBundledProfiles.setOnClickListener(v-> TestHelper.showConfirmDialog(v.getContext(),strBundledProfiles[1],(dialog, which) -> {
            Const.getTouchView().exitEdit(); //需要刷新显示
            readBundledProfilesFromAssets(v.getContext(),true,false);
            Const.getTouchView().setProfile(ModelProvider.readCurrentProfile());//如果当前选中的配置，属于内置配置之一，那么需要舍弃内存中的，重新从文件读取
            Const.getTouchView().startEdit();
        }));

        //修复鼠标偏移
        Button btnSyncFallout = new Button(c);
        btnSyncFallout.setAllCaps(false);
        btnSyncFallout.setText(getS(RR.ctr2_other_syncFallout));
        btnSyncFallout.setOnClickListener(v->{
            //编辑模式下屏蔽了鼠标移动，所以要先退出编辑模式。。。
            btnExitEdit.performClick();
            XServerViewHolder holder = Const.getXServerHolder();
            int[] screenSize = holder.getXScreenPixels();
            holder.injectPointerMove(0,0);
            QH.sleep(50);
            holder.injectPointerMove(screenSize[0],0);
            QH.sleep(50);
            holder.injectPointerMove(screenSize[0],screenSize[1]);
            QH.sleep(50);
            holder.injectPointerMove(0,screenSize[1]);
            QH.sleep(50);
            holder.injectPointerMove(0,0);
            QH.sleep(50);
            holder.injectPointerMove(50,50);
        });
        btnSyncFallout.setLayoutParams(QH.LPLinear.one(0,-2).weight().to());
        LinearLayout linearSyncFallout = TestHelper.wrapWithTipBtn(btnSyncFallout,getS(RR.ctr2_other_syncFalloutTip));

        //为不同容器使用不同配置
        Switch btnEnablePerContainer = new Switch(c);
        btnEnablePerContainer.setText(getS(RR.ctr2_other_isProfPerCont));
        btnEnablePerContainer.setOnCheckedChangeListener((buttonView, isChecked) -> Const.Pref.setProfilePerContainer(isChecked));
        btnEnablePerContainer.setChecked(Const.Pref.isProfilePerContainer());
        btnEnablePerContainer.setLayoutParams(QH.LPLinear.one(0,-2).weight().to());
        LinearLayout linearEnablePerCont = TestHelper.wrapWithTipBtn(btnEnablePerContainer,getS(RR.ctr2_other_isProfPerContTip));


        LinearLayout linearSaves = new LinearLayout(c);
        linearSaves.setOrientation(HORIZONTAL);
        linearSaves.setVerticalGravity(Gravity.CENTER);
        linearSaves.addView(btnSave,QH.LPLinear.one(0,-2).weight().to());
        linearSaves.addView(btnExitEdit,QH.LPLinear.one(0,-2).weight().to());

        addView(linearSaves);
        addView(QH.getOneLineWithTitle(c,/*鼠标移动速度倍率*/getS(RR.ctr2_other_mouseSpeed),editMsMvSpd,true), QH.LPLinear.one(-1, -2).top().left().right().to());
        addView(switchShowArea,QH.LPLinear.one(-1,-2).top().left().right().to());
        addView(linearEnablePerCont,QH.LPLinear.one(-1,-2).top().left().right().to());
        addView(btnBundledProfiles,QH.LPLinear.one(-1,-2).top().left().right().to());
        addView(linearSyncFallout,QH.LPLinear.one(-1,-2).top().left().right().to());
//        addView(switchDetailDebug,QH.LPLinear.one(-1,-2).top().left().right().to());
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
