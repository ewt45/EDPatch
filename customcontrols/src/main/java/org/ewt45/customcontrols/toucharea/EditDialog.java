package org.ewt45.customcontrols.toucharea;

import android.app.AlertDialog;
import android.content.Context;
import android.widget.LinearLayout;

import org.ewt45.customcontrols.QH;

public class EditDialog  {
    public static AlertDialog showWith(Context c,TouchArea<?> touchArea){
        LinearLayout linearRoot = new LinearLayout(c);
        return new AlertDialog.Builder(c)
                .setTitle("按键编辑")
                .setView(QH.wrapAsDialogScrollView(linearRoot))
                .setNegativeButton("关闭",null)
                .show();
    }

}
