package com.example.datainsert.exagear.FAB.dialogfragment;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

import static com.example.datainsert.exagear.RR.dimen.dialogPadding;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.datainsert.exagear.QH;
import com.example.datainsert.exagear.RR;
import com.example.datainsert.exagear.controls.ControlsResolver;

public abstract class BaseFragment extends DialogFragment implements DialogInterface.OnClickListener {
    protected final static String SHARED_PREFERENCE_SETTING = ControlsResolver.PREF_FILE_NAME_SETTING;

    /**
     * 重写了onCreateDialog，这样子fragment只需重写buildUI创建视图即可。
     * @param savedInstanceState
     * @return
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        setCancelable(false);//禁止中途退出
        ViewGroup rootUI = buildUI();
        return new AlertDialog.Builder(requireContext())
                .setTitle(getTitle())
                .setPositiveButton(android.R.string.ok, this)
                .setNegativeButton(android.R.string.cancel, null)
                .setView(QH.wrapAsDialogScrollView(rootUI))
                .create();
    }

    /**
     * onCreateDialog时构建界面。无需创建最外层scrollview和最外层view的padding
     */
    protected abstract ViewGroup buildUI();
    public static TextView getTextViewWithText(Context c, String s) {
        TextView tv = new TextView(c);
        tv.setText(s);
        tv.setLineSpacing(0,1.5f);
        tv.setPadding(0, 20, 0, 0);
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
     * @deprecated 请使用QH的同名函数
     */
    public static LinearLayout getOneLineWithTitle(Context c, @Nullable String s, @Nullable View view, boolean vertical) {
        return QH.getOneLineWithTitle(c, s, view, vertical);
    }

    /**
     * 获取设置相关的sharepreference 写入或读取。 写入：.edit().apply()
     *
     * @return editor
     */
    public static SharedPreferences getPreference() {
        return QH.getPreference();
    }

    /**
     * 长按某些选项，弹出提示（对话框，因为原本tooltip文字太长会显示不全）
     *
     * @param view    视图
     * @param tooltip 说明文字
     */
    public static void setDialogTooltip(View view, String tooltip) {
        view.setOnLongClickListener(v -> {
            TextView textView = new TextView(v.getContext());
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP,16);
            textView.setLineSpacing(0,1.5f);
            textView.setText(tooltip);
            int padding = RR.dimen.dialogPadding();
            textView.setPadding(padding,padding,padding,padding);
            ScrollView scrollView = new ScrollView(v.getContext());
            scrollView.addView(textView);
            new AlertDialog.Builder(v.getContext()).setView(scrollView).create().show();
            return true;
        });
    }

    /**
     * 用于初次打开ex应用时，即使不点开对话框，也进行初始化的操作。注意这个时候可能某些成员变量还没有初始化？还是传一个activity进去吧，这时候global获取不到
     */
    public abstract void callWhenFirstStart(AppCompatActivity activity);

    public abstract String getTitle();

}
