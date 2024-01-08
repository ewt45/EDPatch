package com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2;

import static com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.Const.BtnType.DPAD;
import static com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.Const.BtnType.NORMAL;
import static com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.Const.BtnType.STICK;

import android.content.Context;
import android.support.annotation.IntDef;
import android.util.SparseArray;

import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.model.OneButton;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.model.OneDpad;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.model.OneGestureArea;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.model.OneStick;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.model.TouchAreaModel;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.widget.KeyOnBoardView;
import com.example.datainsert.exagear.QH;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.ref.WeakReference;

public class Const {
    public static WeakReference<Context> ctxRef=null;
    public static int dp8;
    public static int minTouchSize;
    public static int minBtnAreaSize;
    public static int minStickAreaSize;
    public static int defaultBgColor = 0xffFFFAFA;

    public static int keycodeMaxCount = 256 + 7; //还有 7个鼠标按键
    public static String[] keyNames=null;
    /**
     * 记录model的全部类型及其对应int
     * <br/>注意TouchAreaModel可能有继承关系，所以不能用instanceOf，应该用getClass().equals
     * <br/> 找不到index的时候会返回 负数，不一定是-1？
     */
    public static final SparseArray<Class<? extends TouchAreaModel>> modelTypeArray = new SparseArray<>();
    static {
        modelTypeArray.put(TouchAreaModel.TYPE_BUTTON, OneButton.class);
        modelTypeArray.put(TouchAreaModel.TYPE_STICK, OneStick.class);
        modelTypeArray.put(TouchAreaModel.TYPE_DPAD, OneDpad.class);
        modelTypeArray.put(TouchAreaModel.TYPE_GESTURE, OneGestureArea.class);
        modelTypeArray.put(TouchAreaModel.TYPE_NONE, TouchAreaModel.class);

    }

    /**
     * 有些数据需要context才能获取。此函数必须在访问Const成员变量前调用一次。
     */
    public static void init(Context c) {
        if (ctxRef == null || ctxRef.get() == null)
            ctxRef = new WeakReference<>(c);

        dp8 = QH.px(c, 8);
        minTouchSize = QH.px(c, 32);
        minBtnAreaSize = QH.px(c,48);
        minStickAreaSize = minBtnAreaSize*2;
        if(keyNames==null)
            keyNames = KeyOnBoardView.initXKeyCodesAndNames(c,keycodeMaxCount);
    }

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

    @IntDef({BtnColorStyle.STROKE, BtnColorStyle.FILL})
    @Retention(RetentionPolicy.SOURCE)
    public @interface BtnColorStyle {
        int STROKE = 0;
        int FILL = 1;
    }


}
