package com.example.datainsert.exagear.FAB.dialogfragment;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.eltechs.axs.Globals;

public class BaseFragment extends DialogFragment {
    protected static String SHARED_PREFERENCE_SETTING = "some_settings";

    protected TextView getTextViewWithText(String s) {
        TextView tv = new TextView(requireActivity());
        tv.setText(s);
        tv.setPadding(0, 0, 0, 20);
        return tv;
    }

    /**
     * 生成一个线性布局，带一个标题和跟在后面的多个视图，水平排列
     *
     * @param s        标题，可以没有
     * @param view     视图
     * @param vertical 是否垂排列
     * @return 线性布局
     */
    protected LinearLayout getOneLineWithTitle(@Nullable String s, @Nullable View view, boolean vertical) {
        LinearLayout linearLayout = new LinearLayout(requireActivity());
        linearLayout.setOrientation(vertical ? LinearLayout.VERTICAL : LinearLayout.HORIZONTAL);
        if (s != null && !s.equals("")) {
            TextView textView = getTextViewWithText(s + ": ");
            //加粗一下吧
            textView.getPaint().setFakeBoldText(true);
//        textView.setTypeface(Typeface.DEFAULT_BOLD);
            textView.invalidate();
            linearLayout.addView(textView);
        }
        if (view != null) {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
            if (linearLayout.getChildCount() > 0)
                params.setMarginStart(20);
            linearLayout.addView(view, params);
        }

        return linearLayout;
    }


    /**
     * 获取设置相关的sharepreference 写入或读取。 写入：.edit().apply()
     * @return editor
     */
    protected static SharedPreferences getPreference() {
        return Globals.getAppContext().getSharedPreferences(SHARED_PREFERENCE_SETTING, Context.MODE_PRIVATE);
    }



}
