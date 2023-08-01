package com.example.datainsert.exagear.FAB.dialogfragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.eltechs.axs.helpers.AndroidHelpers;
import com.example.datainsert.exagear.RR;
import com.termux.x11.CmdEntryPoint;

/**
 * 新x11服务端 xegw（termux-x11）的说明和设置
 * 请先确保存在xegw 2.0 再显示该dialog选项。dialog选项从CmdEntryPoint获取吧，以确保是最新的内容
 */
public class Xegw2 extends BaseFragment{

    @SuppressLint("SetTextI18n")
    @Override
    protected ViewGroup buildUI() {
        return CmdEntryPoint.getViewForFabDialog(this);
    }

    @Override
    public void callWhenFirstStart(AppCompatActivity activity) {

    }

    @Override
    public String getTitle() {
        return RR.getS(RR.xegw_title);
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {

    }


}
