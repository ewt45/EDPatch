package com.example.datainsert.exagear.controlsV2;

import static android.graphics.Color.BLACK;
import static android.graphics.Color.WHITE;
import static android.support.v4.graphics.ColorUtils.calculateContrast;
import static android.view.View.GONE;
import static android.view.View.MeasureSpec.EXACTLY;
import static android.view.View.MeasureSpec.getSize;
import static android.view.View.MeasureSpec.makeMeasureSpec;
import static android.view.View.VISIBLE;
import static android.widget.LinearLayout.HORIZONTAL;
import static com.example.datainsert.exagear.RR.dimen.dialogPadding;
import static com.example.datainsert.exagear.controlsV2.Const.dp8;

import android.animation.LayoutTransition;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.SeekBar;
import android.widget.TextView;

import com.eltechs.ed.R;
import com.example.datainsert.exagear.controlsV2.gestureMachine.FSMAction2;
import com.example.datainsert.exagear.controlsV2.model.ModelProvider;
import com.example.datainsert.exagear.controlsV2.model.OneProfile;
import com.example.datainsert.exagear.controlsV2.widget.DrawableAlign;
import com.example.datainsert.exagear.QH;
import com.example.datainsert.exagear.RR;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class TestHelper {
    private static final String TAG = "TestHelper";
    private final static float[] hsvTemp = new float[3];

    public static void addTouchAreas(Context c, TouchAreaView touchAreaView) {
//        OneProfile oneProfile = new OneProfile();
//
//        //最底层手势
//        oneProfile.addTouchArea(new TouchArea<OneGestureArea>(
//                touchAreaView,
//                new OneGestureArea(),
//                new ClickAdapter(0.1f, () -> showEditOptions(c))) {
//            @Override
//            public void onDraw(Canvas canvas) {
//
//            }
//        });
        //添加一个悬浮窗。点击可以展开或折叠
    }

    public static void onTouchMoveView(View touchedView, View movedView) {
        if (movedView.getParent() != null && !(movedView.getParent() instanceof FrameLayout))
            throw new RuntimeException("能够拖拽移动的视图父布局必须为FrameLayout");
        //TODO 这里设置了别的focusable会不会影响到TouchAreaView？
        touchedView.setFocusable(true);
        touchedView.setFocusableInTouchMode(true);
        touchedView.setClickable(true);

        touchedView.setOnTouchListener(new View.OnTouchListener() {
            int[] downPos = new int[2];
            int[] downXY = new int[2];
            boolean noClickWhenFinish = false;

            @Override
            public boolean onTouch(View touchedV, MotionEvent event) {
                int[] latestPos = new int[]{(int) event.getRawX(), (int) event.getRawY()};
                if (!(movedView.getParent() instanceof FrameLayout && movedView.getLayoutParams() instanceof FrameLayout.LayoutParams))
                    return false;

                FrameLayout.LayoutParams paramsUpd = (FrameLayout.LayoutParams) movedView.getLayoutParams();
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        noClickWhenFinish=false;
                        downPos = latestPos;
                        downXY = new int[]{paramsUpd.leftMargin, paramsUpd.topMargin};
                        touchedView.setPressed(true);
                        return true;
                    }
                    case MotionEvent.ACTION_MOVE: {
                        paramsUpd.leftMargin = downXY[0] + latestPos[0] - downPos[0];
                        paramsUpd.topMargin = downXY[1] + latestPos[1] - downPos[1];
                        movedView.setLayoutParams(paramsUpd);

                        int slop = ViewConfiguration.get(touchedView.getContext()).getScaledTouchSlop();
                        if (Math.abs(latestPos[0] - downPos[0]) > slop || Math.abs(latestPos[1] - downPos[1]) > slop)
                            noClickWhenFinish = true;
//                        Log.d(TAG, "onTouch: " + String.format("%d, %d, %d, %d, %s, %s", v.getLeft(), v.getTop(), v.getRight(), v.getBottom(), v.getWidth() == v.getRight() - v.getLeft(), v.getHeight() == v.getBottom() - v.getTop()));
//                        Log.d(TAG, "onTouch: " + String.format("%d, %d, %d, %d, %s, %s", v.getBottom(), ((FrameLayout) v.getParent()).getHeight(), v.getRight(), v.getBottom(), v.getWidth() == v.getRight() - v.getLeft(), v.getHeight() == v.getBottom() - v.getTop()));
                        return true;
                    }
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL: {
                        touchedView.setPressed(false);

                        if (!noClickWhenFinish)
                            touchedView.performClick();
                        noClickWhenFinish=false;

                        //如果移出了父视图边界，应该回到边界内
                        restrictViewInsideParent(movedView, (FrameLayout) movedView.getParent());
                        return true;
                    }
                }
                return false;
            }
        });
    }

    /**
     * 当一个视图移动了位置的时候，限制它不允许出父视图边界
     *
     * @param movedView 移动了位置的视图
     * @param parent    父视图，必须是FrameLayout
     */
    public static void restrictViewInsideParent(View movedView, FrameLayout parent) {
        FrameLayout.LayoutParams paramsUpd = (FrameLayout.LayoutParams) movedView.getLayoutParams();

        //TODO 应该是留在父视图内部的剩余部分，应该保持最小宽度即可，出一点边界没关系
        // 目前只有下方和右方这么改了，左和上还是不能出界。目前这个布局没问题因为移动的按下位置就在左上角，但是为了兼容性之后还是改一下吧）

        // 因为初始时gravity为center，导致leftMargin为0时不是贴的父视图左边，所以不能直接读写leftMargin的值
        if (movedView.getLeft() < dp8)
            paramsUpd.leftMargin += dp8 - movedView.getLeft();
        if (movedView.getTop() < dp8)
            paramsUpd.topMargin += dp8 - movedView.getTop();
        //已经调整和左右了，如果宽高还不够就说明是太靠右或下 (出界的话width仍然是right-left，是完整的宽而非裁切后剩下的宽）
        // 能够用left和top是因为一直在修改margin的left和top。right只有在width给定精确值时才会出界，如果是wrap_content这种，那么宽度已经开始变小了，right也不会出界。
//        FrameLayout parent = (FrameLayout) movedView.getParent();

        //烦死了 onMeasure那里AT_MOST的话貌似又不用限制最小宽度也可能出界了。干脆右-左和 右对比父右 都看一下就行了
        int minTouchSize = dp8 * 6;
        if (movedView.getRight() - movedView.getLeft() < minTouchSize)
            paramsUpd.leftMargin -= minTouchSize - (movedView.getRight() - movedView.getLeft());
        else if (parent.getWidth() - movedView.getLeft() < minTouchSize)
            paramsUpd.leftMargin -= minTouchSize - (parent.getWidth() - movedView.getLeft());

        if (movedView.getBottom() - movedView.getTop() < minTouchSize)
            paramsUpd.topMargin -= minTouchSize - (movedView.getBottom() - movedView.getTop());
        else if (parent.getHeight() - movedView.getTop() < minTouchSize)
            paramsUpd.topMargin -= minTouchSize - (parent.getHeight() - movedView.getTop());
        movedView.setLayoutParams(paramsUpd);
    }

    public static TextView getTextView16sp(Context c) {
        TextView tv = new TextView(c);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        return tv;
    }

    public static Button getTextButton(Context c, String text) {
        Button btn = new Button(c);
        QH.setRippleBackground(btn);
        btn.setText(text);
        btn.setTextColor(RR.attr.textColorPrimary(c));
        btn.setAllCaps(false);
        btn.setMinHeight(dp8 * 5);
        btn.setMinimumHeight(dp8 * 5);
        btn.setMinWidth(dp8 * 5);
        btn.setMinimumWidth(dp8 * 5);
        return btn;
    }

    /**
     * 计算两个坐标之间的直线距离
     */
    public static float distance(float x1, float y1, float x2, float y2) {
        float dx = x1- x2,dy = y1-y2;
        return (float) Math.sqrt((dx * dx) + (dy * dy));
    }

    /**
     * 调整为unit的倍数（向下取整）。用于调整按钮的坐标，方便对齐多个按钮
     */
    public static int makeMultipleOf(int unit, int value) {
        return Math.floorDiv(value, unit) * unit;
    }


    /**
     * 修改颜色int（argb）的a值
     */
    public int setColorA(int argb, int newA){
        return (argb & 0x00ffffff) | (newA << 24);
    }

    /**
     * 由于边距设置到外层时一直存在，导致无法判断可滚动的方向，所以不在这里设置边距了
     */
    public static NestedScrollView wrapAsScrollView(View view) {
        Context c = view.getContext();
        NestedScrollView scrollView = new NestedScrollView(c);
        scrollView.setPadding(dialogPadding(), 0, dialogPadding(), 0);
        scrollView.addView(view);
        scrollView.setPadding(0, 0, 0, 0);
        scrollView.setFocusable(true);
        scrollView.setFocusableInTouchMode(true);
        scrollView.requestFocus();
//        scrollView.getChildAt(0).setPadding(p,p,p,p);
        return scrollView;
    }

    /**
     * 在视图onMeasure时，确保宽高是相等的（变成正方形）。<br/>
     * 如果获取的宽高是-1或-2则无法正确处理
     *
     * @return 数组，第一个是宽Spec，第二个是高Spec
     */
    public static int[] makeSquareOnMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int oriW = getSize(widthMeasureSpec);
        int oriH = getSize(heightMeasureSpec);
        int sameSpec = makeMeasureSpec(Math.min(oriH, oriW), EXACTLY);
        return new int[]{sameSpec, sameSpec};
    }

    /**
     * 输出area的区域
     */
    public static void logTouchAreaRect(TouchAreaModel mModel) {
        Log.d(TAG, String.format("logTouchAreaRect: 左-上-右-下=%d,%d,%d,%d", mModel.getLeft(), mModel.getTop(), mModel.getLeft() + mModel.getWidth(), mModel.getTop() + mModel.getHeight()));
    }

    /**
     * 仅修改一个颜色中的透明度值。
     */
    public static int setColorAlpha(int argb, int alpha) {
        return (argb & 0x00ffffff) | (alpha << 24);
    }

    /**
     * 根据给定颜色，返回黑色或白色（看哪个对比度更高）
     */
    public static int getContrastColor(int oriColor) {
        int alpha = oriColor & 0xff000000;
        int colorNoA = oriColor | 0xff000000;
        int pureC = calculateContrast(WHITE, colorNoA) > calculateContrast(BLACK, colorNoA) ? WHITE : BLACK;
        return (pureC & 0x00ffffff) | alpha;
    }

    /**
     * 从apk/assets中读取一个编译后的安卓二进制xml，创建drawable
     * <br/> 使用的函数是 a.getAssets().openXmlResourceParser("assets/cc/ic_apk_document.xml")
     *
     * @param c    context
     * @param name 字符串，只需传入相对路径，如“cc/ic_apk_document.xml” 即可
     * @return drawable
     */
    public static Drawable getAssetsDrawable(Context c, String name) {
        try (XmlResourceParser parser = c.getAssets().openXmlResourceParser("assets/" + name)) {
            return Drawable.createFromXml(c.getResources(), parser, c.getTheme());
        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
            return new ColorDrawable(0xff000000);
        }
    }

    /**
     * <br/> 使用的函数是 a.getAssets().openXmlResourceParser("assets/cc/ic_apk_document.xml")
     * @param name 字符串，只需传入相对路径，如“cc/ic_apk_document.xml” 即可
     */
    public static View getAssetsView(Context c, String name){
        try (XmlResourceParser parser =
                c.getApplicationContext().getAssets().openXmlResourceParser("assets/" + name)) {
            return LayoutInflater.from(c).inflate(parser,null,false);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 按键编辑窗口。向gridview添加一行，标题,内容。如果有可切换的副内容，则在后面加一个切换按钮
     * 用grid是为了保证多行内容的开头是对齐的。
     *
     * @param altView 若没有副视图，则传null
     */
    public static void addOneGridLine(GridLayout gridLayout, String title, View mainView, @Nullable View altView) {
        Context c = gridLayout.getContext();

        LinearLayout.LayoutParams verCenLinearParams = new LinearLayout.LayoutParams(-1, -2);
        verCenLinearParams.gravity = Gravity.CENTER_VERTICAL;
        LinearLayout linear = new LinearLayout(c);
        linear.setOrientation(HORIZONTAL);
        linear.addView(mainView, verCenLinearParams);

        HorizontalScrollView scroll = new HorizontalScrollView(c);
        HorizontalScrollView.LayoutParams paramsLinear = new FrameLayout.LayoutParams(-2, -1);
        paramsLinear.gravity = Gravity.CENTER_VERTICAL;
        scroll.addView(linear, paramsLinear);

        ImageView btn = new ImageView(c);
        btn.setPadding(dp8 / 2, dp8 / 2, dp8 / 2, dp8 / 2);

        //如果副视图不为空，添加到linear中并设置切换按钮
        if (altView != null) {
            linear.addView(altView, verCenLinearParams);

//            btn.setText("⭾");  //草了这几个unicode显示不出来
//            btn.setTextSize(TypedValue.COMPLEX_UNIT_SP,16);
            btn.setImageDrawable(c.getDrawable(R.drawable.aaa_swap));
            QH.setRippleBackground(btn);
            btn.setOnClickListener(v -> {
                boolean isMainShowing = mainView.getVisibility() == VISIBLE;
                mainView.setVisibility(isMainShowing ? GONE : VISIBLE);
                altView.setVisibility(isMainShowing ? VISIBLE : GONE);
            });
            altView.setVisibility(GONE);
        }

        GridLayout.LayoutParams paramsTitle = new GridLayout.LayoutParams();
        paramsTitle.setGravity(Gravity.CENTER_VERTICAL);
        GridLayout.LayoutParams paramsScroll = new GridLayout.LayoutParams(paramsTitle);
        paramsScroll.height = dp8 * 5;
//        paramsScroll.columnSpec = GridLayout.spec(2,GridLayout.ALIGN_MARGINS,1);
        GridLayout.LayoutParams paramsBtn = new GridLayout.LayoutParams(paramsTitle);
        paramsBtn.width = dp8 * 4;
        paramsBtn.height = dp8 * 4;

        gridLayout.addView(QH.getTitleTextView(c, title), paramsTitle);
        gridLayout.addView(scroll, paramsScroll);
        gridLayout.addView(btn, paramsBtn);

    }

    public static int getColorHexFromStr(String str) {
        StringBuilder builder = new StringBuilder(str.trim());
        while (builder.length() < 8)
            builder.insert(0, "f");
        //int最大是0x7fffffff 阿这，加了alpha变8位, Integer.parseInt/ valueOf 没法转换了, 要用parseUnsignedInt 或者 Long.parseLong
        return Integer.parseUnsignedInt(builder.toString(), 16);
    }

    public static void setTextViewSwapDrawable(TextView title) {
        Drawable swapDrawable = getAssetsDrawable(title.getContext(), "controls/swap.xml");
        swapDrawable.setBounds(0, 0, dp8 * 2, dp8 * 2);
        title.setCompoundDrawablePadding(dp8 / 2);
        title.setCompoundDrawables(null, null, swapDrawable, null);
    }

    /**
     * 让颜色变黑（hsv 的v减20%，如果已经很黑了就会物极必反没那么黑
     */
    public static int darkenColor(int mainColor) {
        Color.colorToHSV(mainColor, hsvTemp);
        hsvTemp[2] = hsvTemp[2] - 0.3f;
        if (hsvTemp[2] < 0)
            hsvTemp[2] += 1;
        return Color.HSVToColor(Color.alpha(mainColor), hsvTemp);
    }

    /**
     * 根据attr获取drawable
     */
    public static Drawable getAttrDrawable(Context c, int attrId) {
        TypedArray array = c.obtainStyledAttributes(new int[]{attrId});
        Drawable drawable = array.getDrawable(0);
        array.recycle();
        return drawable;
    }

    /**
     * 由于编辑的model放在内存，有修改操作时（导出，复制，切换，重命名，退出编辑）时都应该将当前model同步到本地，然后再操作
     */
    public static void saveCurrentEditProfileToFile() {
        OneProfile profileInMem = Const.getActiveProfile();
        ModelProvider.saveProfile(profileInMem);
    }

    /**
     * 显示一个二次确认的对话框
     */
    public static void showConfirmDialog(Context c, String s, DialogInterface.OnClickListener onClickListener) {
        new AlertDialog.Builder(c)
                .setMessage(s)
                .setPositiveButton(android.R.string.ok, onClickListener)
                .setNegativeButton(android.R.string.cancel, null)
                .setCancelable(false)
                .show();
    }

    /**
     * 断言condition为true，否则抛出异常信息
     */
    public static void assertTrue(boolean condition, String error) {
        if (!condition)
            throw new RuntimeException(error);
    }

    /**
     * 断言condition为true，否则抛出异常信息
     */
    public static void assertTrue(boolean condition) {
        if (!condition)
            throw new RuntimeException("错误！未满足条件");
    }

    /**
     * 设置 tabLayout的tab最小宽度为0，以自适应宽度
     */
    public static void setTabLayoutTabMinWidth0(TabLayout tabLayout) {
        try {
            Field field = tabLayout.getClass().getDeclaredField("requestedTabMinWidth");
            field.setAccessible(true);
            field.setInt(tabLayout, 0);
            field.setAccessible(false);
        } catch (Throwable th) {
            Log.w(TAG, "setTabLayoutTabMinWidth0: "+th.getCause() );
        }

        //exa里用的是这个
        try {
            Field field = tabLayout.getClass().getDeclaredField("mRequestedTabMinWidth");
            field.setAccessible(true);
            field.setInt(tabLayout, 0);
            field.setAccessible(false);
        } catch (Throwable th) {
            Log.w(TAG, "setTabLayoutTabMinWidth0: "+th.getCause() );

        }

    }

    /**
     * 为对应视图右上角添加一个问号图标，点击可查看说明
     */
    public static void addHelpBadgeToView(View view, String helpText) {
        view.getOverlay().add(new DrawableAlign(view));
        view.setOnClickListener(v -> TestHelper.showConfirmDialog(v.getContext(), helpText, null));
    }

    /**
     * 为对应视图右上角添加一个问号图标，点击可查看说明。(是否要限制图标宽高？原视图是横向铺满还是wrap？）
     * <br/>目前view是默认（wrap），图标宽高也是默认。二者垂直居中对齐
     * @param view 若为纯TextView则点击文字同样打开说明。否则点击监听仅设置到问号图标上。
     */
    public static LinearLayout wrapWithTipBtn(View view, String helpText){
        Context c = view.getContext();
        View.OnClickListener l = v->showConfirmDialog(v.getContext(), helpText, null);
        if(view.getClass().equals(TextView.class))
            view.setOnClickListener(l);

        Drawable drawable = getAssetsDrawable(c,"controls/help.xml");
        drawable.mutate();
        drawable.setTint(RR.attr.colorControlNormal(c));
        ImageView img = new ImageView(c);
        img.setImageDrawable(drawable);
        img.setOnClickListener(l);

        LinearLayout linear = new LinearLayout(c);
        linear.setOrientation(HORIZONTAL);
        linear.setVerticalGravity(Gravity.CENTER_VERTICAL);
        linear.addView(view);
        linear.addView(img,QH.LPLinear.one(-2,-2).left().to());
        return linear;
    }

    /**
     * 分割线视图。会自动设置layoutparam（宽度为2dp）
     *
     * @param vertical true为竖向分割线，否则为横向分割线
     */
    public static View getDividerView(Context c, boolean vertical) {
        View divider = new View(c);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, QH.px(c, 2));
        divider.setLayoutParams(params);
        divider.setBackground(RR.attr.listDivider(c));
        return divider;
    }

    /**
     * 过滤列表中某些元素，返回一个新数组
     */
    public static <T> List<T> filterList(List<T> list, ArrayFilter<T> filter) {
        List<T> resultList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++)
            if (filter.accept(list.get(i)))
                resultList.add(list.get(i));

        return resultList;
    }

    public static float adjustTextPaintCenter(float v, Paint paint) {
        return v - (paint.ascent() + paint.descent()) / 2f;
    }

    public static int min(int... values) {
        int min = values[0];
        for (int i : values)
            if (i < min)
                min = i;
        return min;
    }

    public static int max(int ... values) {
        int max = values[0];
        for(int i:values)
            if(i>max)
                max = i;
        return max;
    }

    public static String getActionsString(List<FSMAction2> actions){
        StringBuilder builder = new StringBuilder();
        for(FSMAction2 action:actions)
            builder.append(action.getNiceName()).append(", ");
        if(builder.length()>=2)
            builder.delete(builder.length()-2,builder.length());
        return builder.toString();
    }


    public static interface ArrayFilter<T> {
        public boolean accept(T item);
    }


    public static interface SimpleSeekbarListener extends SeekBar.OnSeekBarChangeListener {
        @Override
        default void onStartTrackingTouch(SeekBar seekBar) {
        }

        ;

        @Override
        default void onStopTrackingTouch(SeekBar seekBar) {
        }

        ;
    }

    public interface SimpleTabListener extends TabLayout.OnTabSelectedListener {
        @Override
        default void onTabUnselected(TabLayout.Tab tab) {
        }

        ;

        @Override
        default void onTabReselected(TabLayout.Tab tab) {
            onTabSelected(tab);
        }

        ;
    }

    public interface LayoutTransitionEndListener extends LayoutTransition.TransitionListener {
        @Override
        default void startTransition(LayoutTransition transition, ViewGroup container, View view, int transitionType) {
        }
    }

    /**
     * 创建一个PopupMenu
     */
    public static class PopupMenuTemplate {
        private PopupMenu mPopupMenu;

        public PopupMenuTemplate(Context c, View anchor) {
            mPopupMenu = new PopupMenu(c, anchor);

        }

        public PopupMenuTemplate add(String itemName, Runnable action) {
            mPopupMenu.getMenu().add(itemName).setOnMenuItemClickListener(item -> {
                action.run();
                return true;
            });
            return this;
        }

        public PopupMenu build() {
            PopupMenu tmpMenu = mPopupMenu;
            mPopupMenu = null;
            return tmpMenu;

        }
    }
}
