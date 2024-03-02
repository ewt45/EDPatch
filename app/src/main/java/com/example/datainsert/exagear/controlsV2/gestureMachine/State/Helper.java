package com.example.datainsert.exagear.controlsV2.gestureMachine.State;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.datainsert.exagear.QH;

class Helper {
    public static View createEmptyPropEditView(Context c) {
        LinearLayout linearRoot = new LinearLayout(c);
        linearRoot.setOrientation(LinearLayout.HORIZONTAL);
        TextView tv = QH.getTitleTextView(c,"无");
        tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        linearRoot.addView(tv,QH.LPLinear.one(-1,-2).gravity(Gravity.CENTER).left().right().top().bottom().to());
        return linearRoot;
    }
}
