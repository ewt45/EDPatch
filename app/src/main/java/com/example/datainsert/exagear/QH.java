package com.example.datainsert.exagear;

import static android.content.pm.ApplicationInfo.FLAG_TEST_ONLY;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RippleDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.graphics.ColorUtils;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.eltechs.axs.Globals;
import com.eltechs.axs.activities.FrameworkActivity;
import com.eltechs.axs.applicationState.ApplicationStateBase;

import java.io.File;

/**
 * 不要随意修改已有方法的定义，否则会与旧功能不兼容。但是可以修改其内容
 */
public class QH {
    public final static String MY_SHARED_PREFERENCE_SETTING = "some_settings";
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
    public static int sp2px(Context c, int sp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, c.getResources().getDisplayMetrics());
    }


    public static void logD(String s) {
        if (s == null)
            s = "空字符串";

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
        if (minA == -1)
            minA = ColorUtils.calculateMinimumAlpha(Color.BLACK, bgColorNoAlpha, 4.5f);
//         return ((Color.alpha(bgColor)==0xff)?minA<<24:bgColor&0xff000000)+(solid&0x00ffffff);
        return (((255 + Color.alpha(bgColor)) / 2) << 24) + (solid & 0x00ffffff);
    }

    /**
     * 一次性添加多个子布局（为什么官方api没有这种功能啊）
     *
     * @param parent 父布局
     * @param subs   多个子布局
     */
    public static void addAllViews(ViewGroup parent, View... subs) {
        for (View v : subs)
            parent.addView(v);
    }

    /**
     * 用于判断当前包是否是自己的测试apk而非exagear
     */
    public static boolean isTesting() {
        //包名改成一样的了，换一种方式？ 用manifest里的application label试试
        return (Globals.getAppContext().getApplicationInfo().flags & FLAG_TEST_ONLY) != 0;
//        return  Globals.getAppContext().getPackageName().equals("com.ewt45.exagearsupportv7");
    }

    /**
     * 自己的代码都用同一个sharePreference吧。通过这个函数获取。 preference名 {@link #MY_SHARED_PREFERENCE_SETTING}
     * 写入或读取。写入:.edit().apply()
     */
    public static SharedPreferences getPreference() {
        return Globals.getAppContext().getSharedPreferences(MY_SHARED_PREFERENCE_SETTING, Context.MODE_PRIVATE);
    }

    /**
     * (原本在RSIDHelper类，不过就这一个方法单独一个类而且还要用到QH，不如直接整合到QH里了）
     * 用于处理使用的资源id。用自己的工程测试的时候，返回gradle自动分配的id，添加到待修改的apk后使用apk原有id。
     * 省的每次编译成smali都要手动替换，麻烦死了
     *
     * @param my  我自己的apk的资源id
     * @param ori 别人apk的资源id
     * @return 应该使用的资源id
     */
    public static int rslvID(int my, int ori) {
        return isTesting() ? my : ori;
    }

    /**
     * ((ApplicationStateBase) Globals.getApplicationState()).getCurrentActivity()的缩写
     * <p/>
     * 获取当前acitivity，若在activity的onCreate阶段 获取到的是null
     */
    public static FrameworkActivity getCurrentActivity() {
        return ((ApplicationStateBase) Globals.getApplicationState()).getCurrentActivity();
    }

    /**
     * 给布局设置一个背景，平时透明，点击时有波纹效果
     *
     * @param view
     */
    public static void setRippleBackground(View view) {
        GradientDrawable contentDrawable = new GradientDrawable();
        contentDrawable.setColor(0);
        Drawable background;
        GradientDrawable maskDrawable = new GradientDrawable();
        maskDrawable.setCornerRadius(10f);
        maskDrawable.setColor(-1);
        //contentdrawable和maskdrawable用来限制波纹边界
        background = new RippleDrawable(ColorStateList.valueOf(Color.GRAY), contentDrawable, maskDrawable);
        ViewCompat.setBackground(view, background);
    }

    /**
     * 测试某个包名是否存在。用于多个功能使用同一个类时，该类判断哪些功能是添加的，哪些是没添加的
     *
     * @param name 类完整名（包名，类名）
     * @return 是否存在. 在自己测试apk中始终返回 true
     */
    public static boolean classExist(String name) {
        if (QH.isTesting())
            return true;
        boolean exist = false;
        try {
            Class.forName(name);
            exist = true;
        } catch (Throwable ignored) {
        }
        return exist;
    }

    /**
     * 构建一个对话框。显示一条消息以及 下次不再提示的按钮。
     * 若已经设置过不再提示则不会显示
     *
     * @param a                        context 不能为global获取的
     * @param tips                     文字内容
     * @param PREF_KEY_SHOULD_SHOW_TIP 写到QH.getPreference()里的key。若为true则显示对话框
     */
    public static void showTipDialogWithDisable(Context a, String tips, String PREF_KEY_SHOULD_SHOW_TIP) {
        //如果已经设置过，就不再显示
        if (!QH.isTesting() && !QH.getPreference().getBoolean(PREF_KEY_SHOULD_SHOW_TIP, true))
            return;

        TextView textView = new TextView(a);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        textView.setLineSpacing(0, 1.5f);
        textView.setText(tips);

        CheckBox checkBox = new CheckBox(a);
        checkBox.setText(RR.getS(RR.shortcut_DontShowUp));
        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> QH.getPreference().edit().putBoolean(PREF_KEY_SHOULD_SHOW_TIP, !isChecked).apply());
        LinearLayout.LayoutParams checkParams = new LinearLayout.LayoutParams(-2, -2);
        checkParams.topMargin = 20;

        LinearLayout linearLayout = new LinearLayout(a);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        int padding = QH.px(a, RR.attr.dialogPaddingDp);
        linearLayout.setPadding(padding, padding, padding, padding);
        linearLayout.addView(textView);
        linearLayout.addView(checkBox, checkParams);
        ScrollView scrollView = new ScrollView(a);
        scrollView.addView(linearLayout);
        new AlertDialog.Builder(a).setView(scrollView).setPositiveButton(android.R.string.yes, null).create().show();

    }

    /**
     * 生成一个带简介的设置项，类似于preference那种的
     * pref存到自己的pref中。所以不能用于容器设置
     *
     * @param view    可用来切换选项的按钮之类的
     * @param title   按钮文字
     * @param info    按钮说明。
     * @param prefKey 按钮对应pref的key，默认为false
     * @return 包含按钮的一个线性布局
     */
    public static LinearLayout getOnePrefLine(@NonNull View view, String title, @Nullable String info, @Nullable String prefKey) {
        Context c = view.getContext();

        LinearLayout linearRoot = new LinearLayout(c);
        linearRoot.setOrientation(LinearLayout.HORIZONTAL);
        linearRoot.addView(view);

        if (view instanceof EditText) {
            throw new RuntimeException("edittext尚未实现");
        } else if (view instanceof TextView)
            ((TextView) view).setText(title);

        if (view instanceof CompoundButton) {
            ((CompoundButton) view).setChecked(getPreference().getBoolean(prefKey, false)); //默认都为false吧
            ((CompoundButton) view).setOnCheckedChangeListener((buttonView, isChecked) -> getPreference().edit().putBoolean(prefKey, isChecked).apply());
        }

        View.OnClickListener clickListener = v -> new android.app.AlertDialog.Builder(v.getContext())
                .setMessage(info)
                .setPositiveButton(android.R.string.yes, null)
                .create().show();


        if (info != null) {
            TextView btnInfo = new TextView(c);

            btnInfo.setText("  ⓘ  ");
            btnInfo.getPaint().setFakeBoldText(true);
            btnInfo.setOnClickListener(clickListener);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-2, -2);
            layoutParams.setMarginStart(px(c, 20));
            linearRoot.addView(btnInfo, layoutParams);

            if(view.getClass().equals(TextView.class))  //如果是纯textview，点击事件也设置到它身上
                view.setOnClickListener(clickListener);
        }

        return linearRoot;
    }

    /**
     * 在视图右侧添加一个"  ⓘ  "，可点击弹出dialog显示说明。并将两者用线性布局包裹。
     */
    public static LinearLayout addInfoTrail(View view,String info){
        View.OnClickListener clickListener = v -> new android.app.AlertDialog.Builder(v.getContext())
                .setMessage(info)
                .setPositiveButton(android.R.string.yes, null)
                .create().show();

        Context c = view.getContext();
        TextView btnInfo = new TextView(c);

        btnInfo.setText("  ⓘ  ");
        btnInfo.getPaint().setFakeBoldText(true);
        btnInfo.setOnClickListener(clickListener);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-2, -2);
        layoutParams.setMarginStart(px(c, 20));
        LinearLayout linearRoot = new LinearLayout(c);
        linearRoot.setOrientation(LinearLayout.HORIZONTAL);
        linearRoot.addView(view);
        linearRoot.addView(btnInfo, layoutParams);

        if(view.getClass().equals(TextView.class))  //如果是纯textview，点击事件也设置到它身上
            view.setOnClickListener(clickListener);
        return linearRoot;
    }



    //    /**
//     * d8 （android gradle plugin 8.1.0) 优化会把不同类的api判断整合到一个类中，导致复制时无法判断应该复制哪些。试试用这种方法回避
//     */
//    public static boolean requireMinSDK(int version){
//        return Build.VERSION.SDK_INT >= version;
//    }
    public static class Files {
        /**
         * 日志输出的文件夹。设为Android/data/包名/files/logs
         *
         * @return file对象。确保该文件夹已经创建
         */
        public static File logsDir() {
            File logDir = new File(Globals.getAppContext().getExternalFilesDir(null), "logs");
            if (!logDir.exists()) {
                boolean b = logDir.mkdirs();
            }
            return logDir;
        }
    }


}
