package com.example.datainsert.exagear.FAB.dialogfragment.customcontrols;

import static com.example.datainsert.exagear.RR.getS;

import android.content.Context;
import android.text.Html;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.datainsert.exagear.RR;

/**
 * 一些说明。导入导出功能
 */
public class SubView4Other extends LinearLayout {
    public SubView4Other(Context context) {
        super(context);

        setOrientation(VERTICAL);
        Context c = context;

        //试试html格式的textview？
        TextView textView = new TextView(c);
        textView.setLineSpacing(0,1.2f);
        textView.setText(Html.fromHtml(getS(RR.cmCtrl_s4_tips)));
        addView(textView);

    }
}
