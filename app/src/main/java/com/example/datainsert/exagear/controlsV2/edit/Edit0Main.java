package com.example.datainsert.exagear.controlsV2.edit;

import android.animation.LayoutTransition;
import android.content.Context;
import android.support.design.widget.TabLayout;
import android.view.View;
import android.widget.RelativeLayout;

import com.example.datainsert.exagear.QH;
import com.example.datainsert.exagear.controlsV2.TestHelper;

/**
 * 编辑模式下，用于修改设置的悬浮窗
 */
public class Edit0Main extends RelativeLayout implements EditConfigWindow.OnReEnterListener{
    private static final String TAG = "EditConfigWindow";
    final View mToolbar;

    public Edit0Main( Context c) {
        super(c);

        setLayoutTransition(new LayoutTransition()); //如果在每次切换tab时，用beginDelayedTransition，不知为何会导致profile切到key的tab的时候，key高度变高，但现显示的区域还是profile那么一点，下面都空着

        //顶部工具栏
        TabLayout tabToolbar = new TabLayout(c);
        tabToolbar.setTabMode(TabLayout.MODE_SCROLLABLE);
        TestHelper.setTabLayoutTabMinWidth0(tabToolbar);
//        tabToolbar.setTabGravity(TabLayout.GRAVITY_CENTER);
//        tabToolbar.setTabIndicatorFullWidth(false);

        tabToolbar.addOnTabSelectedListener((TestHelper.SimpleTabListener) tab -> {
            removeAllViews();
//            TransitionManager.beginDelayedTransition(this);
            LayoutParams pagerParams = QH.LPRelative.one().alignParentWidth().to();
            if ("key".equals(tab.getTag()))
                addView(new Edit1KeyView(getContext()), pagerParams);//,QH.LPLinear.one(-2,-2).to()
            else if("gesture".equals(tab.getTag()))
                addView(new Edit2GestureView(getContext()), pagerParams);
            else if ("profile".equals(tab.getTag()))
                addView(new Edit3ProfilesView(getContext()), pagerParams);
            else if("other".equals(tab.getTag()))
                addView(new Edit4OtherView(getContext()), pagerParams);
        });
        tabToolbar.addTab(tabToolbar.newTab().setText("按键").setTag("key"));
        tabToolbar.addTab(tabToolbar.newTab().setText("手势").setTag("gesture"));
        tabToolbar.addTab(tabToolbar.newTab().setText("多配置").setTag("profile"));
        tabToolbar.addTab(tabToolbar.newTab().setText("其他").setTag("other"));

        mToolbar = tabToolbar;
//        mToolbar.setLayoutParams(QH.LPLinear.one(0, -2).weight().to());
    }

    @Override
    public void onReEnter() {
        //在这里调用子视图的reenter，这样刷新手势操作重命名后
        for(int i=0; i<getChildCount(); i++){
            View sub = getChildAt(i);
            if(sub instanceof EditConfigWindow.OnReEnterListener)
                ((EditConfigWindow.OnReEnterListener) sub).onReEnter();
        }
    }
}
