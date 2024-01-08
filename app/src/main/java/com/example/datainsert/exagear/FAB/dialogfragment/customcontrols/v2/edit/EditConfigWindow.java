package com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.edit;

import static android.view.View.MeasureSpec.AT_MOST;
import static android.view.View.MeasureSpec.makeMeasureSpec;
import static com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.Const.dp8;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Outline;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.support.annotation.NonNull;
import android.transition.TransitionManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewAnimator;

import com.eltechs.ed.R;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.TestHelper;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.TouchAreaView;
import com.example.datainsert.exagear.QH;

import java.util.ArrayList;
import java.util.List;

/**
 * 编辑时，装编辑视图的容器。用于处理视图切换（就不用fragment了）
 * <br/> 视图结构：
 * <br/> 第一行：linearlayout，横向，第一个是homeBtn，第二个是titleView
 * <br/> 第二行：自定义视图
 */
@SuppressLint("ViewConstructor")
public class EditConfigWindow extends LinearLayout {
    private static final String TAG = "EditConfigWindow";
    final TouchAreaView mAreaView;
    private final int mMaxWidth, mMaxHeight;
    private final LinearLayout mLinearTitle;
    private final List<View> mTitles = new ArrayList<>();
    private final List<View> mSubViews = new ArrayList<>();
    private final ImageView mIconView;//最小化时只显示这个图标
    private boolean isMinimized = false;

    public EditConfigWindow(TouchAreaView host) {
        super(host.getContext());
        mAreaView = host;
        Context c = host.getContext();

        setOrientation(VERTICAL);
        mMaxWidth = QH.px(c, 400);
        mMaxHeight = QH.px(c, 600);

        //宽高怎么适配比较好呢？
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(-1, -2);
        params.setMargins(dp8, dp8, dp8, dp8);
        params.gravity = Gravity.CENTER_HORIZONTAL;
        setLayoutParams(params);

        ShapeDrawable bgDrawable = new ShapeDrawable();
//        bgDrawable.setShape(new RoundRectShape(new float[]{20, 20, 20, 20, 20, 20, 20, 20}, null, null));
        bgDrawable.setShape(new RectShape());
        bgDrawable.setTint(TestHelper.getBGColor(c) | 0xd0000000);
        setBackground(bgDrawable);

        //用outline来切割圆角，记得setClipToOutline(true)
        setOutlineProvider(new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                outline.setRoundRect(0, 0, view.getWidth(), view.getHeight(), dp8);
            }
        });
        setClipToOutline(true);


        mIconView = new ImageView(c);
        mIconView.setPadding(dp8, dp8, dp8, dp8);
        QH.setRippleBackground(mIconView);
        TestHelper.onTouchMoveView(mIconView, this);


        mLinearTitle = new LinearLayout(c);
        mLinearTitle.setOrientation(HORIZONTAL);
        mLinearTitle.setVerticalGravity(Gravity.CENTER_VERTICAL);
        mLinearTitle.addView(mIconView, new LinearLayout.LayoutParams(dp8 * 6, dp8 * 6));

        addView(mLinearTitle);

        EditMain editMain = new EditMain(host, this);
        toNextView(editMain, editMain.mToolbar);

    }

    public void toNextView(View content, String title) {
        TextView titleView = QH.getTitleTextView(content.getContext(), title);
        titleView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        titleView.setGravity(Gravity.CENTER_VERTICAL|Gravity.START);
        titleView.setLayoutParams(QH.LPLinear.one(0,-1).weight().to());
        toNextView(content, titleView);
    }

    public void toNextView(View content, @NonNull View titleView) {
        TransitionManager.beginDelayedTransition(this);
        changeHomeButton(mSubViews.size() == 0);
        if(titleView.getClass().equals(TextView.class))
            TestHelper.onTouchMoveView(titleView,this);

        if (mLinearTitle.getChildCount() > 1)
            mLinearTitle.removeViewAt(1);
        mLinearTitle.addView(titleView);

        mSubViews.add(content);
        mTitles.add(titleView);
        if (this.getChildCount() > 1)
            this.removeViewAt(1);
        this.addView(content);
    }


    public void toPreviousView() {
        if (mSubViews.size() < 2)
            return;

        TransitionManager.beginDelayedTransition(this);

        mSubViews.remove(mSubViews.size() - 1);
        mTitles.remove(mTitles.size() - 1);
        this.removeViewAt(1);
        this.addView(mSubViews.get(mSubViews.size() - 1));
        mLinearTitle.removeViewAt(1);
        mLinearTitle.addView(mTitles.get(mTitles.size() - 1));

        changeHomeButton(mSubViews.size() == 1);
    }

    /**
     * 改变homeBtn的样式和功能
     *
     * @param isHome 如果为true，说明为第一个视图，点击为缩小或放大，否则为返回键
     */
    private void changeHomeButton(boolean isHome) {
        if (isHome) {
            //TODO 放入assets
            mIconView.setImageDrawable(getContext().getDrawable(R.drawable.aaa_editwindowicon));
//        //TODO 为什么在onTouch里performClick的话，会触发两次点击事件呢？另外一个调用到底哪来的
            mIconView.setOnClickListener(v -> {
                if (mLinearTitle.getChildCount() > 1)
                    mLinearTitle.getChildAt(1).setVisibility(isMinimized ? VISIBLE : GONE);
                if (this.getChildCount() > 1)
                    this.getChildAt(1).setVisibility(isMinimized ? VISIBLE : GONE);
                isMinimized = !isMinimized;
                //放大时，可能会导致左侧出边界然后无法移动，所以需要限制位置。但是必须放在post里否则不生效
                post(() -> TestHelper.restrictViewInsideParent(this, (FrameLayout) this.getParent()));
            });
        } else {
            mIconView.setImageDrawable(getContext().getDrawable(R.drawable.aaa_back));
            mIconView.setOnClickListener(v -> toPreviousView());
        }
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
//        Log.d(TAG, "onMeasure: 宽度：" + oriWidthSize);


        //TODO 宽度过小时，给定一个固定的精确值，保证getRight()能够超出父边界，
        // 这样触摸监听里才能判断出右侧出界以便移回来（但把这个判断放到触摸监听里，右-左<最小宽度 也行吧
        // 还要测试一下，手机竖屏，如果屏幕宽度没有mMaxWidth，那么是按屏幕宽度还是mMaxWidth
        int finalWidthSpec = makeMeasureSpec(Math.min(oriWidthSize, mMaxWidth), AT_MOST);
        int finalHeightSpec = makeMeasureSpec(Math.min(oriHeightSize, mMaxHeight), AT_MOST);

        super.onMeasure(finalWidthSpec, finalHeightSpec);
    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (changed) {
            if (l < 0) {
                Log.d(TAG, "onLayout: 左侧小于0");
                r -= l;
                l = 0;
            }
            if (t < 0) {
                Log.d(TAG, "onLayout: 上方小于0");

                b -= t;
                t = 0;
            }
        }
        super.onLayout(changed, l, t, r, b);
    }
}
