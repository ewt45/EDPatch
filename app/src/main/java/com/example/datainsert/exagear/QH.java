package com.example.datainsert.exagear;

import static android.content.pm.ApplicationInfo.FLAG_TEST_ONLY;
import static android.view.View.NO_ID;
import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static android.widget.LinearLayout.HORIZONTAL;
import static com.eltechs.ed.guestContainers.GuestContainerConfig.CONTAINER_CONFIG_FILE_KEY_PREFIX;
import static com.example.datainsert.exagear.RR.dimen.dialogPadding;
import static com.example.datainsert.exagear.RR.dimen.margin8Dp;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RippleDrawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.graphics.ColorUtils;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.transition.TransitionManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.eltechs.axs.Globals;
import com.eltechs.axs.activities.FrameworkActivity;
import com.eltechs.axs.applicationState.ApplicationStateBase;
import com.eltechs.axs.applicationState.ExagearImageAware;
import com.example.datainsert.exagear.controlsV2.TestHelper;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

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
        textView.setLineSpacing(0, 1.2f);
        textView.setText(tips);

        CheckBox checkBox = new CheckBox(a);
        checkBox.setText(RR.getS(RR.shortcut_DontShowUp));
        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> QH.getPreference().edit().putBoolean(PREF_KEY_SHOULD_SHOW_TIP, !isChecked).apply());
        LinearLayout.LayoutParams checkParams = new LinearLayout.LayoutParams(-2, -2);
        checkParams.topMargin = margin8Dp() * 2;

        LinearLayout linearLayout = new LinearLayout(a);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        int padding = dialogPadding();
        linearLayout.setPadding(padding, padding, padding, padding);
        linearLayout.addView(textView);
        linearLayout.addView(checkBox, checkParams);
        new AlertDialog.Builder(a).setView(QH.wrapAsDialogScrollView(linearLayout)).setPositiveButton(android.R.string.yes, null).show();
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

        if (info != null) {
            linearRoot.addView(getInfoIconView(c, info));

            if (view.getClass().equals(TextView.class))  //如果是纯textview，点击事件也设置到它身上
                view.setOnClickListener(getInfoShowClickListener(info));
        }
        return linearRoot;
    }

    /**
     * 在视图右侧添加一个"  ⓘ  "，可点击弹出dialog显示说明。并将两者用线性布局包裹。
     */
    public static LinearLayout addInfoTrail(View view, String info) {
        Context c = view.getContext();
        LinearLayout linearRoot = new LinearLayout(c);
        linearRoot.setOrientation(LinearLayout.HORIZONTAL);
        linearRoot.addView(view);
        linearRoot.addView(getInfoIconView(c, info));

        if (view.getClass().equals(TextView.class))  //如果是纯textview，点击事件也设置到它身上
            view.setOnClickListener(getInfoShowClickListener(info));
        return linearRoot;
    }

    /**
     * 生成纯 ⓘ textview（带点击事件和marginStart）
     */
    public static TextView getInfoIconView(Context c, String info) {
        TextView btnInfo = new TextView(c);
        btnInfo.setText("ⓘ");
        btnInfo.getPaint().setFakeBoldText(true);
        btnInfo.setOnClickListener(getInfoShowClickListener(info));
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-2, -2);
        layoutParams.setMarginStart(px(c, 16));
        btnInfo.setLayoutParams(layoutParams);
        return btnInfo;
    }

    /**
     * 生成一个点击事件，用于点击 ⓘ 后显示说明dialog
     */
    private static View.OnClickListener getInfoShowClickListener(String info) {
        return v -> new AlertDialog.Builder(v.getContext()).setMessage(info).setPositiveButton(android.R.string.yes, null).show();
    }

    /**
     * 通过反射获取某个类的私有成员变量
     *
     * @param clz       类名
     * @param clzInst   类的实例。static变量的话可为null
     * @param fieldName 变量名
     * @return 变量实例
     */
    public static Object reflectPrivateMember(Class<?> clz, Object clzInst, String fieldName) {
        Object fieldInst = null;
        try {
            Field field = clz.getDeclaredField(fieldName);
            field.setAccessible(true);
            fieldInst = field.get(clzInst);
            field.setAccessible(false);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return fieldInst;
    }

    public static Object reflectPrivateMethod(Class<?> clz, String methodName, Class<?>[] clzs, Object inst, Object... params) {
        try {
            Method method = clz.getDeclaredMethod(methodName, clzs);
            method.setAccessible(true);
            Object result = method.invoke(inst, params);
            method.setAccessible(false);
            return result;
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取容器设置对应的pref
     */
    public static SharedPreferences getContPref(long contId) {
        return Globals.getAppContext().getSharedPreferences(CONTAINER_CONFIG_FILE_KEY_PREFIX + contId, Context.MODE_PRIVATE);
    }

    /**
     * 设置按钮为无边框样式。需要sdk>=24才能。（设置textAppearance）
     */
    public static void setButtonBorderless(Button button) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            button.setTextAppearance(android.R.style.TextAppearance_Material_Widget_Button_Borderless_Colored);
//            RippleDrawable rippleDrawable = new RippleDrawable(ColorStateList.valueOf(0x44444444), null, button.getBackground());
//            button.setBackground(rippleDrawable);
            button.setBackground(RR.attr.selectableItemBackground(button.getContext()));
            int padding = margin8Dp();
            button.setPadding(padding, padding, padding, padding);
            button.setMinWidth(0);
            button.setMinimumWidth(0);
            button.setMinHeight(0);
            button.setMinimumHeight(0);
        }

    }

    /**
     * 生成一个线性布局，带一个标题和跟在后面的多个视图，水平排列
     * 如果要设置后面的视图的layoutparams的宽高，可以在传入之前setlayoutparams设置一次
     *
     * @param title    标题，可以没有
     * @param view     视图
     * @param vertical 是否垂排列
     * @return 线性布局
     */
    public static LinearLayout getOneLineWithTitle(Context c, @Nullable String title, @Nullable View view, boolean vertical) {
        LinearLayout linearLayout = new LinearLayout(c);
        linearLayout.setOrientation(vertical ? LinearLayout.VERTICAL : LinearLayout.HORIZONTAL);
        if (title != null && !title.equals(""))
            linearLayout.addView(getTitleTextView(c, title));

        if (view != null) {
            LinearLayout.LayoutParams params = view.getLayoutParams() != null
                    ? new LinearLayout.LayoutParams(view.getLayoutParams())
                    : new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
            if (linearLayout.getChildCount() > 0)
                params.setMarginStart(margin8Dp());
            if (vertical)
                params.topMargin = margin8Dp();
            linearLayout.addView(view, params);
        }
        linearLayout.setPadding(0, margin8Dp(), 0, 0);
        return linearLayout;
    }

    /**
     * 返回一个用作标题的textview，深色，加粗
     */
    public static TextView getTitleTextView(Context c, String title) {
        TextView textView = new TextView(c);
        textView.setText(title);
        textView.setTextColor(RR.attr.textColorPrimary(c));
        //加粗一下吧
        textView.getPaint().setFakeBoldText(true);
//        textView.setTypeface(Typeface.DEFAULT_BOLD);
        textView.invalidate();
        return textView;
    }

    /**
     * 返回一个视图，包含一个下拉按钮（textview）和一个标题。点击后可展开一个视图
     * 例如：
     * LinearLayout linearPart1Title = QH.getExpandableTitleView(c, wrapperTitles[0], linearPart1Expand)
     * linearRoot.addView(addInfoTrail(linearPart1Title,getS(RR.othArg_taskset_info)));
     * linearRoot.addView(linearPart1Expand);
     *
     * @param expandableView 可被展开的视图，默认被隐藏
     */
    public static LinearLayout getExpandableTitleView(Context c, String title, View expandableView) {
        TextView tvIcon = new TextView(c);
        tvIcon.setText("▼");
        tvIcon.setPadding(0, 0, margin8Dp(), 0);

        TextView tvTitle = getTitleTextView(c, title);
        tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);

        View.OnClickListener clickListener = v -> {
            boolean isCurrExpanded = tvIcon.getRotationX() != 0;
            tvIcon.animate().rotationX(isCurrExpanded ? 0 : 180).setDuration(300).start();
            TransitionManager.beginDelayedTransition((ViewGroup) expandableView.getParent());
            expandableView.setVisibility(isCurrExpanded ? View.GONE : View.VISIBLE);
        };

        tvIcon.setOnClickListener(clickListener);
        tvTitle.setOnClickListener(clickListener);
        expandableView.setVisibility(View.GONE);

        LinearLayout linearTitle = new LinearLayout(c);
        linearTitle.setOrientation(HORIZONTAL);
        linearTitle.addView(tvIcon);
        linearTitle.addView(tvTitle);
        return linearTitle;
    }

    /**
     * 将一个textview设置为单行，末尾为... 点击后展开全部文字
     */
    public static void setTextViewExpandable(TextView tv) {
        tv.setSingleLine(true);
        tv.setEllipsize(TextUtils.TruncateAt.END);
        tv.setOnClickListener(v -> tv.setSingleLine(tv.getMaxLines() != 1));
    }

    /**
     * dialog的自定义视图，最外部加一层Nested滚动视图，并且添加padding<br/>
     * 滚动视图会获取焦点，以阻止edittext自动弹出输入法，和解决自动滚动到回收视图的位置而非第一个视图位置的问题
     */
    public static NestedScrollView wrapAsDialogScrollView(View view) {
        Context c = view.getContext();
        NestedScrollView scrollView = new NestedScrollView(c);
        scrollView.setPadding(dialogPadding(), 0, dialogPadding(), 0);
        scrollView.addView(view);
        //阻止edittext获取焦点弹出输入法 / 回收视图获取焦点自动滚动到回收视图的位置
        scrollView.setFocusable(true);
        scrollView.setFocusableInTouchMode(true);
        scrollView.requestFocus();
        return scrollView;
    }

    /**
     * /exa的库里还没有 TabLayout.BaseOnTabSelectedListener,只有OnTabSelectedListener。改依赖版本又没有用。试试反射吧
     */
    public static void addTabLayoutListener(TabLayout tabToolbar, TabLayout.OnTabSelectedListener listener) {
        if(QH.isTesting()){
            tabToolbar.addOnTabSelectedListener(listener);
        }else{
            try {
                TabLayout.class.getDeclaredMethod("addOnTabSelectedListener", TabLayout.OnTabSelectedListener.class)
                        .invoke(tabToolbar, listener);
            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }


    public interface SimpleTextWatcher extends TextWatcher {

        @Override
        default void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        default void onTextChanged(CharSequence s, int start, int before, int count) {

        }

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

        /**
         * 第三方功能，要持久存储的，全部放到/opt/edpatch文件夹中。
         */
        public static File edPatchDir() {
            File dir = new File(((ExagearImageAware) Globals.getApplicationState()).getExagearImage().getPath(), "opt/edpatch");
            if (!dir.isDirectory())
                FileUtils.deleteQuietly(dir);
            if (!dir.exists()) {
                boolean b = dir.mkdirs();
            }
            return dir;
        }
    }

    /**
     * 常见的LinearLayout.LayoutParams构建
     */
    public static class LPLinear {
        int w = -1;
        int h = -2;
        float weight = 0;
        int[] margins = {0, 0, 0, 0};
        int gravity = -111;

        /**
         * 宽为match，高为rap
         */
        public static LPLinear one() {
            return new LPLinear();
        }

        public static LPLinear one(int w, int h) {
            LPLinear linear = new LPLinear();
            linear.w = w;
            linear.h = h;
            return linear;
        }

        public LPLinear gravity(int pg) {
            gravity = pg;
            return this;
        }

        public LPLinear weight(float pw) {
            weight = pw;
            return this;
        }

        public LPLinear weight() {
            weight = 1;
            return this;
        }

        /**
         * 顶部margin设为8dp
         */
        public LPLinear top() {
            margins[1] = margin8Dp();
            return this;
        }

        public LPLinear top(int margin) {
            margins[1] = margin;
            return this;
        }

        public LPLinear bottom() {
            margins[3] = margin8Dp();
            return this;
        }

        public LPLinear left() {
            margins[0] = margin8Dp();
            return this;
        }

        public LPLinear left(int margin) {
            margins[0] = margin;
            return this;
        }

        public LPLinear right() {
            margins[2] = margin8Dp();
            return this;
        }

        public LPLinear margin(int left, int top, int right, int bottom) {
            margins = new int[]{left, top, right, bottom};
            return this;
        }

        public LinearLayout.LayoutParams to() {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(w, h, weight);
            params.weight = weight;
            if (gravity != -111)
                params.gravity = gravity;
            params.setMargins(margins[0], margins[1], margins[2], margins[3]);
            return params;
        }
    }

    /**
     * 构建常用的RelativeLayout.LayoutParams
     */
    public static class LPRelative {
        RelativeLayout.LayoutParams lp;

        /**
         * 新建一个宽高 -2 -2的param
         */
        public static LPRelative one() {
            return one(-2, -2);
        }

        public static LPRelative one(int w, int h) {
            LPRelative lpRelative = new LPRelative();
            lpRelative.lp = new RelativeLayout.LayoutParams(w, h);
            return lpRelative;
        }

        public LPRelative alignParentWidth() {
            lp.alignWithParent = true;
            lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            return this;
        }
        public LPRelative centerInParent(){
            lp.addRule(RelativeLayout.CENTER_IN_PARENT);
            return this;
        }

        public LPRelative below(View whichView) {
            if(whichView.getId()==NO_ID)
                whichView.setId(View.generateViewId());
            lp.addRule(RelativeLayout.BELOW, whichView.getId());
            return this;
        }
        public LPRelative leftOf(View whichView) {
            if(whichView.getId()==NO_ID)
                whichView.setId(View.generateViewId());
            lp.addRule(RelativeLayout.LEFT_OF, whichView.getId());
            return this;
        }

        public LPRelative centerVertical() {
            lp.addRule(RelativeLayout.CENTER_VERTICAL);
            return this;
        }

        public RelativeLayout.LayoutParams to() {
            RelativeLayout.LayoutParams tmpLp = lp;
            lp = null;
            return tmpLp;
        }

        public LPRelative top() {
            lp.topMargin = margin8Dp();
            return this;
        }

        public LPRelative left() {
            lp.leftMargin = margin8Dp();
            return this;
        }

        public LPRelative alignParentTop() {
            lp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            return this;
        }

        public LPRelative right() {
            lp.rightMargin = margin8Dp();
            return this;
        }
    }

    public static class TV {
        TextView textView;

        public TV(Context c) {
            textView = new TextView(c);
        }

        public static TV one(Context c) {
            return new TV(c);
        }

        public TV text(String str) {
            textView.setText(str);
            return this;
        }
        //加粗
        public TV bold(){
            textView.getPaint().setFakeBoldText(true);
            return this;
        }

        /**
         * 注意是文字居中，而不是layoutparam的居中
         */
        public TV textGravity(int gravity){
            textView.setGravity(gravity);
            return this;
        }

        /**
         * 文字大小设置为16sp，默认的话更小一些
         */
        public TV text16Sp() {
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            return this;
        }

        public TV text14Sp() {
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            return this;
        }

        /**
         * 颜色使用textColorPrimary 而非默认的灰色
         */
        public TV solidColor() {
            textView.setTextColor(RR.attr.textColorPrimary(textView.getContext()));
            return this;
        }

        /**
         * 用于当做文字按钮。设置波纹背景。调整宽高，字体实色
         */
        public TV button() {
            solidColor();
            GradientDrawable contentDrawable = new GradientDrawable();
            contentDrawable.setColor(0);
            GradientDrawable maskDrawable = new GradientDrawable();
            maskDrawable.setCornerRadius(10f);
            maskDrawable.setColor(-1);
            //contentdrawable和maskdrawable用来限制波纹边界
            ViewCompat.setBackground(textView, new RippleDrawable(ColorStateList.valueOf(Color.GRAY), contentDrawable, maskDrawable));
            textView.setPadding(margin8Dp(), margin8Dp(), margin8Dp(), margin8Dp());
            return this;
        }

        public TextView to() {
            TextView tmp = textView;
            textView = null;
            return tmp;
        }


    }
}
