package com.example.datainsert.exagear;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.graphics.ColorUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;

import com.eltechs.axs.Globals;
import com.example.datainsert.exagear.controls.OneColPrefs;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

public class QH {
    private final static String TAG = "Helpers";

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp(相对大小)
     */
    public static int dp(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }
    /**
     * convert sp to its equivalent px
     */
    public static int sp2px(Context c,int sp){
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp,c.getResources().getDisplayMetrics());
    }


    /**
     * 反序列化，获取NewKeyPrefs数组
     *
     * @param pathStr 序列化文件路径。null会设定为默认位置
     * @return OneColPrefs[][]数组
     */
    public static OneColPrefs[][] deserialize(String pathStr) {
        if (pathStr == null) {
            pathStr = "/storage/emulated/0/Download/1.txt";
        }
        //试试反序列化
        OneColPrefs[][] localList = null;
        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(pathStr));
            localList = (OneColPrefs[][]) ois.readObject();
//            Log.d(TAG, "onViewCreated: 反序列化结果：" + Arrays.toString(localList));
            ois.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return localList;
    }

    public static void logD(String s){
        if(s==null)
            s="空字符串";

        Log.d("@myLog", s);
    }

    /**
     * 获取与背景色有足够对比度的文字颜色
     *
     * @param bgColor 背景色
     * @return 文字颜色
     */
    public static int getTextColorByContrast(int bgColor) {
        int bgColorNoAlpha = bgColor | 0xff000000; //计算明度貌似要不透明？
        //先用白色计算一下最小透明度，如果是-1，就换黑色
        int minA = ColorUtils.calculateMinimumAlpha(Color.WHITE, bgColorNoAlpha, 4.5f);
        int solid = minA != -1 ? Color.WHITE : Color.BLACK;
        if(minA == -1)
            minA = ColorUtils.calculateMinimumAlpha(Color.BLACK, bgColorNoAlpha, 4.5f);
//         return ((Color.alpha(bgColor)==0xff)?minA<<24:bgColor&0xff000000)+(solid&0x00ffffff);
        return (((255 + Color.alpha(bgColor)) / 2) << 24) + (solid & 0x00ffffff);
    }

    /**
     * 一次性添加多个子布局（为什么官方api没有这种功能啊）
     * @param parent 父布局
     * @param subs 多个子布局
     */
    public static void addAllViews(ViewGroup parent, View... subs){
        for(View v:subs)
            parent.addView(v);
    }

    /**
     * 用于判断当前包是否是自己的测试apk而非exagear
     */
    public static boolean isTesting(){
        return  Globals.getAppContext().getPackageName().equals("com.ewt45.exagearsupportv7");
    }
}
