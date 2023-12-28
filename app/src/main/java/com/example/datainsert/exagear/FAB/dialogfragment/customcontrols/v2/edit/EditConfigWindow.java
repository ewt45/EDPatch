package com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.edit;

import static android.view.View.MeasureSpec.AT_MOST;
import static android.view.View.MeasureSpec.makeMeasureSpec;
import static com.example.datainsert.exagear.RR.dimen.minCheckSize;

import android.content.Context;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.support.design.widget.TabLayout;
import android.support.v4.widget.NestedScrollView;
import android.view.Gravity;
import android.view.MotionEvent;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.eltechs.ed.R;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.TestHelper;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.TouchAreaView;
import com.example.datainsert.exagear.QH;
import com.example.datainsert.exagear.RR;

/**
 * 编辑模式下，用于修改设置的悬浮窗
 */
public class EditConfigWindow extends LinearLayout {
    private static final String TAG = "EditConfigWindow";
    final TouchAreaView mHost;
    private final int margin = RR.dimen.margin8Dp();
    private final int minTouchSize = RR.dimen.minCheckSize();
    private final int mMaxWidth;
    private final int mMaxHeight;
    private boolean isMinimized = false;
    private ImageView mIconView;//最小化时只显示这个图标

    public EditConfigWindow(TouchAreaView touchAreaView) {
        super(touchAreaView.getContext());
        mHost = touchAreaView;
        Context c = touchAreaView.getContext();

        setOrientation(VERTICAL);
        mMaxWidth = QH.px(c, 400);
        mMaxHeight = QH.px(c, 600);

        //宽高怎么适配比较好呢？
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(-1, -2);
        params.setMargins(margin, margin, margin, margin);
        params.gravity = Gravity.CENTER_HORIZONTAL;
        setLayoutParams(params);

        ShapeDrawable bgDrawable = new ShapeDrawable();
        bgDrawable.setShape(new RoundRectShape(new float[]{20, 20, 20, 20, 20, 20, 20, 20}, null, null));
        bgDrawable.setTint(TestHelper.getBGColor(c));
        setBackground(bgDrawable);
        //TODO 颜色适配
//        setBackgroundColor(getBGColor(context));


        mIconView = new ImageView(c);
        mIconView.setPadding(margin, margin, margin, margin);
        //TODO 放入assets
        mIconView.setImageDrawable(c.getDrawable(R.drawable.gamepad));
        QH.setRippleBackground(mIconView);


        //顶部工具栏
        TabLayout tabToolbar = new TabLayout(c);
        tabToolbar.setTabMode(TabLayout.MODE_FIXED);

        LinearLayout linearTopToolbar = new LinearLayout(c);
        linearTopToolbar.setOrientation(HORIZONTAL);
        linearTopToolbar.addView(mIconView, new LinearLayout.LayoutParams(minCheckSize(), minCheckSize()));
        linearTopToolbar.addView(tabToolbar, QH.LPLinear.one(0, -2).weight().to());
        addView(linearTopToolbar);

        LinearLayout linearPager = new LinearLayout(c);
        linearPager.setOrientation(VERTICAL);
        NestedScrollView scrollPager = TestHelper.wrapAsScrollView(linearPager);
        addView(scrollPager);


        tabToolbar.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if ("key".equals(tab.getTag()))
                    onTabKey(linearPager);

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                onTabSelected(tab);
            }
        });
        tabToolbar.addTab(tabToolbar.newTab().setText("按键").setTag("key"));
        tabToolbar.addTab(tabToolbar.newTab().setText("手势").setTag("gesture"));
        tabToolbar.addTab(tabToolbar.newTab().setText("多配置").setTag("profile"));
        tabToolbar.addTab(tabToolbar.newTab().setText("其他").setTag("other"));

//        TestHelper.onTouchMoveView(this, this);
        TestHelper.onTouchMoveView(mIconView, this);
//        //TODO 为什么在onTouch里performClick的话，会触发两次点击事件呢？另外一个调用到底哪来的
        mIconView.setOnClickListener(v -> {
            tabToolbar.setVisibility(isMinimized ? VISIBLE : GONE);
            scrollPager.setVisibility(isMinimized ? VISIBLE : GONE);
            isMinimized = !isMinimized;
            requestLayout();
        });

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }

    /**
     * 点击 tab ”按键“ 后
     */
    private void onTabKey(LinearLayout linearPager) {
        Context c = getContext();
        linearPager.removeAllViews();
        linearPager.addView(new KeyPropertiesView(this));

    }

//    /**
//     * 移动时只能左右，上下铺满
//     */
//    @Override
//    public void setLayoutParams(ViewGroup.LayoutParams params) {
//        if (params instanceof FrameLayout.LayoutParams) {
//            FrameLayout.LayoutParams fParams = (FrameLayout.LayoutParams) params;
//            if (fParams.topMargin < margin)
//                fParams.topMargin = margin;
////
////            if(fParams.leftMargin <margin)
////                fParams.leftMargin = margin;
//
//            if (getParent() != null) {
//                // rect获取子视图在父视图中的位置。但是只有左和上，右下还是左上位置
//                // getChildVisibleRect 获取的rect，子视图出界时为负数，但会返回false
//                // 因为初始时gravity为center，导致leftMargin为0时不是贴的父视图左边，所以不能直接读写leftMargin的值
//                Rect rect = new Rect();
//                getParent().getChildVisibleRect(this, rect, null); //出界的时候变为负数，这里会返回false
//                Log.d(TAG, "setLayoutParams: rect="+ rect.toString());
//                if(rect.left<margin)
//                    fParams.leftMargin += margin-rect.left;
//            }
//        }
//        super.setLayoutParams(params);
//    }

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

        //TODO 宽度过小时，给定一个固定的精确值，保证getRight()能够超出父边界，这样触摸监听里才能判断出右侧出界以便移回来（但把这个判断放到触摸监听里，右-左<最小宽度 也行吧
        int finalWidthSpec = makeMeasureSpec(Math.min(oriWidthSize, mMaxWidth), AT_MOST);
        int finalHeightSpec = makeMeasureSpec(Math.min(oriHeightSize, mMaxHeight), AT_MOST);

        super.onMeasure(finalWidthSpec, finalHeightSpec);
    }
}
