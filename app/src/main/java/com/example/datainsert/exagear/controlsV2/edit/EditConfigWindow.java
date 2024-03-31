package com.example.datainsert.exagear.controlsV2.edit;

import static android.view.View.MeasureSpec.AT_MOST;
import static android.view.View.MeasureSpec.makeMeasureSpec;
import static com.example.datainsert.exagear.controlsV2.Const.dp8;

import android.animation.LayoutTransition;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Outline;
import android.graphics.Point;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.support.annotation.NonNull;
import android.support.v4.widget.NestedScrollView;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.eltechs.ed.R;
import com.example.datainsert.exagear.controlsV2.TestHelper;
import com.example.datainsert.exagear.QH;
import com.example.datainsert.exagear.RR;

import java.util.ArrayList;
import java.util.List;

/**
 * 编辑时，装编辑视图的容器。用于处理视图切换（就不用fragment了）
 * <br/> 视图结构：
 * <br/> 第一行：linearlayout，横向，第一个是homeBtn，第二个是titleView
 * <br/> 第二行：自定义视图 （加一个竖向的滚动视图吧）
 */
@SuppressLint("ViewConstructor")
public class EditConfigWindow extends RelativeLayout {
    private static final String TAG = "EditConfigWindow";
    public final int mMaxWidth, mMaxHeight;
    private final LinearLayout mLinearTitle;
    private final RelativeLayout mPager;
    private final List<View> mTitles = new ArrayList<>();
    private final List<View> mSubViews = new ArrayList<>();
    private final ImageView mIconView;//最小化时只显示这个图标
    private boolean isMinimized = false;

    public EditConfigWindow(Context c) {
        super(c);

        LayoutTransition configWindowLayoutTransition = new LayoutTransition();
        // (但是用layoutTransition的话必须要等动画完全结束再检查，所以放这了）（不对，不是自身的动画，是mLinearTitle的动画导致的）

//        configWindowLayoutTransition.addTransitionListener((TestHelper.LayoutTransitionEndListener) (tr, ct, vi, trt) ->
//                postDelayed(()->TestHelper.restrictViewInsideParent(EditConfigWindow.this,(FrameLayout) getParent()),500));
        setLayoutTransition(configWindowLayoutTransition);
//        setOrientation(VERTICAL);
        mMaxWidth = QH.px(c, 400);
        mMaxHeight = QH.px(c, 600);

        //宽高怎么适配比较好呢？(用相对布局然后wrap就完事了）
        // gravity如果是横向居中的话，会导致拖动时横向移动速度不对。还是去掉吧。直接获取Display宽高然后再设置leftMargin
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(-2, -2);
        params.setMargins(dp8, dp8, dp8, dp8);
        Point point = new Point();
        ((WindowManager)c.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getSize(point);
        Log.d(TAG, "EditConfigWindow: 小窗时是否能正确获取宽高？："+ point);//可以
        if(point.x > mMaxWidth)
            params.leftMargin = (point.x - mMaxWidth)/2;
//        params.gravity = Gravity.CENTER_HORIZONTAL;
        setLayoutParams(params);

        ShapeDrawable bgDrawable = new ShapeDrawable();
        bgDrawable.setShape(new RectShape());
        bgDrawable.setTint(TestHelper.setColorAlpha(RR.attr.colorBackground(c),0xe9));
        setBackground(bgDrawable);

        //用outline来切割圆角，记得setClipToOutline(true)
        setOutlineProvider(new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                outline.setRoundRect(0, 0, view.getWidth(), view.getHeight(), dp8);
            }
        });
        setClipToOutline(true);

        View dragbarView = TestHelper.getDividerView(c, false);
        dragbarView.setMinimumHeight(dp8 / 4);
        if (dragbarView.getBackground() instanceof GradientDrawable) {
            dragbarView.getBackground().mutate();
            ((GradientDrawable) dragbarView.getBackground()).setColor(RR.attr.colorPrimary(c));
        }

        mIconView = new ImageView(c);
        mIconView.setPadding(dp8, dp8, dp8, dp8);
        QH.setRippleBackground(mIconView);
        TestHelper.onTouchMoveView(mIconView, this);

        //toolbar
        mLinearTitle = new LinearLayout(c);
        mLinearTitle.setOrientation(LinearLayout.HORIZONTAL);
        mLinearTitle.setVerticalGravity(Gravity.CENTER_VERTICAL);
        mLinearTitle.setLayoutTransition(new LayoutTransition());//这个图标附近还不能加动画，否则放大的时候又会出界
        mLinearTitle.addView(mIconView, new LinearLayout.LayoutParams(dp8 * 6, dp8 * 6));
        mLinearTitle.addView(new View(c));

        //pager
        mPager = new RelativeLayout(c);
        mPager.setMinimumWidth(QH.px(c, 150));
        mPager.setLayoutTransition(new LayoutTransition());

        //为什么在onTouch里performClick的话，会触发两次点击事件呢？另外一个调用到底哪来的（现在么问题了，不知道咋解决的）
        //如果第一个视图，点击为缩小或放大，否则为返回键
        mIconView.setOnClickListener(v -> {
            if (!isFirstView())
                toPreviousView();
            else {
                disableTransitionBeforeMaximize();

                //最小化时去掉右侧对齐父布局，否则宽度不会变小
                RelativeLayout.LayoutParams toolbarParams = (LayoutParams) mLinearTitle.getLayoutParams();
                toolbarParams.addRule(ALIGN_PARENT_RIGHT, isMinimized ? TRUE : 0);
                mLinearTitle.getChildAt(1).setVisibility(isMinimized ? VISIBLE : GONE);
                this.getChildAt(0).setVisibility(isMinimized ? VISIBLE : GONE);
                this.getChildAt(2).setVisibility(isMinimized ? VISIBLE : GONE);

                enableTransitionAfterMaximize();

                //放大时，可能会导致左侧出边界然后无法移动，所以需要限制位置。但是必须放在post里否则不生效（emm缩小时也需要）
                //另外，设置LayoutTransition也会导致限制不生效。为LayoutTransition设置监听器，会导致回正用力过度不知道怎么解决，所以只好在放大的时候临时取消动画。。
                post(() -> TestHelper.restrictViewInsideParent(EditConfigWindow.this, (FrameLayout) getParent()));

                isMinimized = !isMinimized;
            }
        });

        //dragbar整不好，放弃，先占个位吧
        addView(new View(c), QH.LPLinear.one(0, 0).to());
//        addView(dragbarView,QH.LPLinear.one(-2,dp8/2).margin(dp8*6,dp8/2,dp8*6,dp8/2).weight().to());

        NestedScrollView nestedScrollView = new NestedScrollView(c);
        nestedScrollView.addView(mPager);

        View toobarView = mLinearTitle;
        View pagerView = nestedScrollView;
        toobarView.setId(generateViewId());
        pagerView.setId(generateViewId());
        addView(toobarView, QH.LPRelative.one(-2, -2).alignParentWidth().to());
        addView(pagerView, QH.LPRelative.one(-1, -2).alignParentWidth().below(toobarView).to());

        Edit0Main editMain = new Edit0Main(getContext());
        toNextView(editMain, editMain.mToolbar);
    }

    private static int min(int... values) {
        int min = values[0];
        for (int value : values)
            if (value < min)
                min = value;
        return min;
    }

    /**
     * 最大化时，动画效果会导致左侧出界，所以需要临时禁用
     */
    private void disableTransitionBeforeMaximize() {
        if (this.getLayoutTransition() != null) {
            this.getLayoutTransition().disableTransitionType(LayoutTransition.CHANGE_APPEARING);
            this.getLayoutTransition().disableTransitionType(LayoutTransition.CHANGE_DISAPPEARING);
        }
        if (mLinearTitle.getLayoutTransition() != null){
            mLinearTitle.getLayoutTransition().disableTransitionType(LayoutTransition.CHANGE_APPEARING);
            mLinearTitle.getLayoutTransition().disableTransitionType(LayoutTransition.CHANGE_DISAPPEARING);
        }
        if (mPager.getLayoutTransition() != null){
            mPager.getLayoutTransition().disableTransitionType(LayoutTransition.CHANGE_APPEARING);
            mPager.getLayoutTransition().disableTransitionType(LayoutTransition.CHANGE_DISAPPEARING);
        }
    }

    private void enableTransitionAfterMaximize() {
        if (this.getLayoutTransition() != null){
            this.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGE_APPEARING);
            this.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGE_DISAPPEARING);
        }
        if (mLinearTitle.getLayoutTransition() != null){
            mLinearTitle.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGE_APPEARING);
            mLinearTitle.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGE_DISAPPEARING);
        }
        if (mPager.getLayoutTransition() != null){
            mLinearTitle.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGE_APPEARING);
            mLinearTitle.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGE_DISAPPEARING);
        }
    }

    public void toNextView(View content, String title) {
        TextView titleView = QH.getTitleTextView(content.getContext(), title);
        titleView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        titleView.setGravity(Gravity.CENTER_VERTICAL | Gravity.START);
//        titleView.setLayoutParams(QH.LPLinear.one(0, -1).weight().right().to());
        toNextView(content, titleView);
    }

    public void toNextView(View content, @NonNull View titleView) {
//        TransitionManager.beginDelayedTransition(this);
        if (titleView.getClass().equals(TextView.class)) {
            titleView.setLayoutParams(QH.LPLinear.one(0, -1).weight().right().to());
            TestHelper.onTouchMoveView(titleView, this);
        }

        mLinearTitle.removeViewAt(1);
        mLinearTitle.addView(titleView);

        mSubViews.add(content);
        mTitles.add(titleView);
        mPager.removeAllViews();
        mPager.addView(content, QH.LPRelative.one().alignParentWidth().to());

        changeHomeButtonIcon();
    }

    public void toPreviousView() {
        if (isFirstView())
            return;

//        TransitionManager.beginDelayedTransition(this);

        mSubViews.remove(mSubViews.size() - 1);
        mTitles.remove(mTitles.size() - 1);

        mPager.removeAllViews();
        View targetSubView = mSubViews.get(mSubViews.size() - 1);
        mPager.addView(targetSubView,QH.LPRelative.one().alignParentWidth().to());
        if (targetSubView instanceof OnReEnterListener)
            ((OnReEnterListener) targetSubView).onReEnter();
        mLinearTitle.removeViewAt(1);
        mLinearTitle.addView(mTitles.get(mTitles.size() - 1));

        changeHomeButtonIcon();
    }

    /**
     * 改变homeBtn的样式和功能
     */
    private void changeHomeButtonIcon() {
        mIconView.setImageDrawable(isFirstView()
                ? TestHelper.getAssetsDrawable(getContext(),"controls/edit_window_icon.xml")
                : TestHelper.getAssetsDrawable(getContext(),"controls/back.xml"));
    }

    /**
     * 判断是否为主页（没有toNextView过），即mSubViews只有0或一个元素
     */
    private boolean isFirstView() {
        return mSubViews.size() < 2;
    }

    /**
     * layoutParams先传 width=match_parent height=wrap_content，然后如果太大了 再根据mMaxWidth/Height限制一下
     * 这里一定要在过小时给定精确宽高，否则移动时无法判断是否出界
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //先修改一下宽高，合适了再让super处理
        int oriWidthSize = MeasureSpec.getSize(widthMeasureSpec);
        int oriHeightSize = MeasureSpec.getSize(heightMeasureSpec);

        int finalWidthSpec = makeMeasureSpec(Math.min(oriWidthSize,mMaxWidth),AT_MOST);
        int finalHeightSpec = makeMeasureSpec(Math.min(oriHeightSize, mMaxHeight), AT_MOST);

        super.onMeasure(finalWidthSpec, finalHeightSpec);
        //TODO 宽度过小时，给定一个固定的精确值，保证getRight()能够超出父边界，
        // 这样触摸监听里才能判断出右侧出界以便移回来（但把这个判断放到触摸监听里，右-左<最小宽度 也行吧
        // 还要测试一下，手机竖屏，如果屏幕宽度没有mMaxWidth，那么是按屏幕宽度还是mMaxWidth
//        mLinearTitle.measure(widthMeasureSpec, heightMeasureSpec);
//        mPager.measure(widthMeasureSpec, heightMeasureSpec);
//        int subWidth = Math.max(mLinearTitle.getMeasuredWidth(), mPager.getMeasuredWidth());
//
//        //toolbar和pager取更大的，然后屏幕限制，自定义限制和视图宽度取最小的，然后pager的宽度用match_parent就行了
//        int finalWidthSpec = makeMeasureSpec(min(subWidth, oriWidthSize, mMaxWidth), AT_MOST);//Math.min(oriWidthSize, mMaxWidth)
//        int finalHeightSpec = makeMeasureSpec(min(oriHeightSize, mMaxHeight), AT_MOST);


    }

//
//    @Override
//    protected void onLayout(boolean changed, int l, int t, int r, int b) {
//        if (changed) {
//            if (l < 0) {
//                Log.d(TAG, "onLayout: 左侧小于0");
//                r -= l;
//                l = 0;
//            }
//            if (t < 0) {
//                Log.d(TAG, "onLayout: 上方小于0");
//
//                b -= t;
//                t = 0;
//            }
//        }
//        super.onLayout(changed, l, t, r, b);
//    }

    /**
     * 调用toPreviousView的时候，对即将显示的视图调用此方法，以进行一些刷新操作
     */
    public interface OnReEnterListener {
        void onReEnter();
    }
}
