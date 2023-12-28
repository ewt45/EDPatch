package com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2;

import static com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.Const.BtnType.DPAD;
import static com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.Const.BtnType.NORMAL;
import static com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.Const.BtnType.STICK;

import android.support.annotation.IntDef;

import com.example.datainsert.exagear.RR;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

public class Const {
    public static int margin8 = RR.dimen.margin8Dp();
    public static int minTouchSize = RR.dimen.minCheckSize();
    public static int defaultBgColor = 0xffFFFAFA;

    @IntDef({NORMAL, STICK, DPAD})
    @Retention(RetentionPolicy.SOURCE)
    public @interface BtnType {
        int NORMAL = 0;
        int STICK = 1;
        int DPAD = 2;
    }

    @IntDef({BtnShape.RECT, BtnShape.OVAL})
    @Retention(RetentionPolicy.SOURCE)
    public @interface BtnShape {
        int RECT = 0;
        int OVAL = 1;
    }

    @IntDef({BtnColorStyle.STROKE,BtnColorStyle.FILL})
    @Retention(RetentionPolicy.SOURCE)
    public @interface BtnColorStyle {
        int STROKE = 0;
        int FILL = 1;
    }


}
