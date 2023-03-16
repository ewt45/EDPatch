package com.example.datainsert.exagear.FAB.dialogfragment;

import static com.example.datainsert.exagear.RR.getS;

import android.content.Context;
import android.content.DialogInterface;
import android.text.Html;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.datainsert.exagear.RR;

public class AboutFab extends BaseFragment{
    @Override
    protected ViewGroup buildUI() {
        Context c = requireContext();
        LinearLayout linearLayout = new LinearLayout(c);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        TextView tvInfo = new TextView(c);
        tvInfo.setLineSpacing(0,1.2f);
        tvInfo.setText(Html.fromHtml(getS(RR.abtFab_info)));
        tvInfo.setClickable(true);
        tvInfo.setTextIsSelectable(true);
        LinearLayout.LayoutParams tvInfoParams = new LinearLayout.LayoutParams(-2,-2);
        tvInfoParams.topMargin = 20;

        linearLayout.addView(tvInfo,tvInfoParams);

        return linearLayout;
    }

    @Override
    public void callWhenFirstStart() {

    }

    @Override
    public String getTitle() {
        return getS(RR.abtFab_title);
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {

    }
}
