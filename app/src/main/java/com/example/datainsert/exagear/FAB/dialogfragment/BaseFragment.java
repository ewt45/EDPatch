package com.example.datainsert.exagear.FAB.dialogfragment;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.eltechs.axs.Globals;

public class BaseFragment extends DialogFragment {
    protected final static String SHARED_PREFERENCE_SETTING = "some_settings";

    public static TextView getTextViewWithText(Context c, String s) {
        TextView tv = new TextView(c);
        tv.setText(s);
        tv.setPadding(0, 0, 0, 20);
        return tv;
    }

    /**
     * 生成一个线性布局，带一个标题和跟在后面的多个视图，水平排列
     * 如果要设置后面的视图的layoutparams的宽高，可以在传入之前setlayoutparams设置一次
     *
     * @param s        标题，可以没有
     * @param view     视图
     * @param vertical 是否垂排列
     * @return 线性布局
     */
    public static LinearLayout getOneLineWithTitle(Context c, @Nullable String s, @Nullable View view, boolean vertical) {
        LinearLayout linearLayout = new LinearLayout(c);
        linearLayout.setOrientation(vertical ? LinearLayout.VERTICAL : LinearLayout.HORIZONTAL);
        if (s != null && !s.equals("")) {
            TextView textView = getTextViewWithText(c, s);
            //加粗一下吧
            textView.getPaint().setFakeBoldText(true);
//        textView.setTypeface(Typeface.DEFAULT_BOLD);
            textView.invalidate();
            linearLayout.addView(textView);
        }
        if (view != null) {
            LinearLayout.LayoutParams params = view.getLayoutParams() != null
                            ? new LinearLayout.LayoutParams(view.getLayoutParams())
                            : new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
            if (linearLayout.getChildCount() > 0)
                params.setMarginStart(20);
            linearLayout.addView(view, params);
        }
        linearLayout.setPadding(0, 20, 0, 0);
        return linearLayout;
    }


    /**
     * 获取设置相关的sharepreference 写入或读取。 写入：.edit().apply()
     *
     * @return editor
     */
    public static SharedPreferences getPreference() {
        return Globals.getAppContext().getSharedPreferences(SHARED_PREFERENCE_SETTING, Context.MODE_PRIVATE);
    }

    /**
     * 长按某些选项，弹出提示（对话框，因为原本tooltip文字太长会显示不全）
     *
     * @param view    视图
     * @param tooltip 说明文字
     */
    public static void setDialogTooltip(View view, String tooltip) {
        view.setOnLongClickListener(v -> {
            new AlertDialog.Builder(v.getContext()).setMessage(tooltip).create().show();
            return true;
        });
    }


}
