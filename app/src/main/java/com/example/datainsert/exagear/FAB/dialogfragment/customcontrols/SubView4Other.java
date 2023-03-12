package com.example.datainsert.exagear.FAB.dialogfragment.customcontrols;

import static com.example.datainsert.exagear.FAB.dialogfragment.BaseFragment.getOneLineWithTitle;
import static com.example.datainsert.exagear.FAB.dialogfragment.BaseFragment.setDialogTooltip;
import static com.example.datainsert.exagear.RR.getS;

import android.content.Context;
import android.text.Html;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.datainsert.exagear.RR;
import com.example.datainsert.exagear.controls.model.FormatHelper;

/**
 * 一些说明。导入导出功能
 */
public class SubView4Other extends LinearLayout {
    TransferCallback mCallback;
    public SubView4Other(Context context) {
        super(context);

        setOrientation(VERTICAL);
        Context c = context;

        //试试html格式的textview？
        TextView textView = new TextView(c);
        textView.setLineSpacing(0,1.2f);
        textView.setText(Html.fromHtml(getS(RR.cmCtrl_s4_tips)));
        addView(textView);

        Button btnExport = new Button(c);
        btnExport.setText(getS(RR.cmCtrl_s4_export));
        btnExport.setOnClickListener(v->{
        mCallback.exportData();
        });
        Button btnImport  = new Button(c);
        btnImport.setText(getS(RR.cmCtrl_s4_import));
        btnImport.setOnClickListener(v->{
            mCallback.importData();
        });
        LinearLayout linear2Btn = new LinearLayout(c);
        linear2Btn.addView(btnExport,new LayoutParams(-2,-2));
        linear2Btn.addView(btnImport,new LayoutParams(-2,-2));
        LinearLayout linearTransfer = getOneLineWithTitle(c,getS(RR.cmCtrl_s4_trsportTitle),linear2Btn,true);
        setDialogTooltip(linearTransfer.getChildAt(0),getS(RR.cmCtrl_s4_trsportTip));

        addView(linearTransfer);

    }

    /**
     * 初始化时设置导入或导出数据的回调
     */
    public void setCallback(TransferCallback mCallback) {
        this.mCallback = mCallback;
    }

    public interface TransferCallback{
        public void exportData();
        public void importData();
    }
}
