package com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.edit;

import android.animation.LayoutTransition;
import android.content.Context;
import android.support.design.widget.TabLayout;
import android.support.v4.widget.NestedScrollView;
import android.transition.TransitionManager;
import android.view.MotionEvent;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.TestHelper;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.TouchAreaView;
import com.example.datainsert.exagear.QH;

/**
 * 编辑模式下，用于修改设置的悬浮窗
 */
public class Edit0Main extends LinearLayout {
    private static final String TAG = "EditConfigWindow";
    final TouchAreaView mHost;
    final EditConfigWindow mWindow;
    final View mToolbar;

    public Edit0Main(TouchAreaView touchAreaView, EditConfigWindow window) {
        super(touchAreaView.getContext());
        mHost = touchAreaView;
        mWindow = window;
        Context c = touchAreaView.getContext();

        setOrientation(VERTICAL);

        //TODO 颜色适配
//        setBackgroundColor(getBGColor(context));

        //顶部工具栏
        TabLayout tabToolbar = new TabLayout(c);
        //TODO 设置fixed在宽度窄的时候，文字较多的会变成两排。设置scrollable在宽度宽的时候较少文字也会占很大一块空白
        // 原因是每个tab宽度强制相等了。能否变成自适应宽度？
        tabToolbar.setTabMode(TabLayout.MODE_SCROLLABLE);
        TestHelper.setTabLayoutTabMinWidth0(tabToolbar);
//        tabToolbar.setTabGravity(TabLayout.GRAVITY_CENTER);
//        tabToolbar.setTabIndicatorFullWidth(false);

        LinearLayout linearPager = new LinearLayout(c);
        linearPager.setOrientation(VERTICAL);
        linearPager.setLayoutTransition(new LayoutTransition()); //如果在每次切换tab时，用beginDelayedTransition，不知为何会导致profile切到key的tab的时候，key高度变高，但现显示的区域还是profile那么一点，下面都空着

//        NestedScrollView scrollPager = TestHelper.wrapAsScrollView(linearPager);
        addView(linearPager,QH.LPLinear.one(-2,-2).to());

        tabToolbar.addOnTabSelectedListener((TestHelper.SimpleTabListener) tab -> {
            linearPager.removeAllViews();
//            TransitionManager.beginDelayedTransition(this);
            if ("key".equals(tab.getTag()))
                linearPager.addView(new Edit1KeyView(this),QH.LPLinear.one(-2,-2).to());
            else if("gesture".equals(tab.getTag()))
                linearPager.addView(new Edit2GestureView(this),QH.LPLinear.one(-2,-2).to());
            else if ("profile".equals(tab.getTag()))
                linearPager.addView(new Edit3ProfilesView(this),QH.LPLinear.one(-2,-2).to());

        });
        tabToolbar.addTab(tabToolbar.newTab().setText("按键").setTag("key"));
        tabToolbar.addTab(tabToolbar.newTab().setText("手势").setTag("gesture"));
        tabToolbar.addTab(tabToolbar.newTab().setText("多配置").setTag("profile"));
        tabToolbar.addTab(tabToolbar.newTab().setText("其他").setTag("other"));

        HorizontalScrollView scrollToolbar = new HorizontalScrollView(c);
        scrollToolbar.addView(tabToolbar);
        mToolbar = scrollToolbar;
        mToolbar.setLayoutParams(QH.LPLinear.one(0, -2).weight().to());
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


}
