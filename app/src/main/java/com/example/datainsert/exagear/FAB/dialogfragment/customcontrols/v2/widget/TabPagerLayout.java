package com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.widget;

import android.animation.LayoutTransition;
import android.content.Context;
import android.support.design.widget.TabLayout;
import android.support.v4.widget.NestedScrollView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.Const;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.TestHelper;
import com.example.datainsert.exagear.QH;

import java.util.ArrayList;
import java.util.List;

/**
 * 实现一个简单的tab+pager。基本逻辑：点击tab，下方布局容器先删除当前page再添加新的对应page。
 * <br/> pager是个framelayout，放在了HorizontalScrollView和NestedScrollView 所以子布局应该不需要再添加滚动的视图
 * <br/> 基本使用：new TabPagerLayout(c).addTabAndPage("属性",propEditView)
 * <br/> 由于可以从布局中移除tablayout，所以不应对tablayout做布局相关的操作
 */
public class TabPagerLayout extends LinearLayout {
    private final TabLayout mTabLayout;
    private final FrameLayout mPager;
    private final List<View> mPageList = new ArrayList<>();
    private final List<Runnable> mTabChangeCallback = new ArrayList<>();
    private boolean isScrollable = true;
    public TabPagerLayout(Context c) {
        super(c);
        setOrientation(VERTICAL);
        mTabLayout = new TabLayout(c);
        mPager = new FrameLayout(c);

        addView(mTabLayout, QH.LPLinear.one(-2,-2).to());
        addScrollablePager(c);

        mPager.setLayoutTransition(new LayoutTransition());

        mTabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        TestHelper.setTabLayoutTabMinWidth0(mTabLayout);

        //点击tab时切换pager的视图，并执行回调。这个应该在添加第一个tab前设置，否则添加第一个tab时自动调动select就无法触发这个监听器了
        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int index = -1;
                for(int i=0; i<mTabLayout.getTabCount(); i++)
                    if(mTabLayout.getTabAt(i) == tab){
                        index = i;
                        break;
                    }
                mPager.removeAllViews();
                mPager.addView(mPageList.get(index),new FrameLayout.LayoutParams(-2,-2));
                if(mTabChangeCallback.get(index)!=null)
                    mTabChangeCallback.get(index).run();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                onTabSelected(tab);
            }
        });
    }

    private void addScrollablePager(Context c){
        HorizontalScrollView horizonScrollPagerWrapper  = new HorizontalScrollView(c);
        horizonScrollPagerWrapper.addView(mPager);
        NestedScrollView verticalScrollPagerWrapper = new NestedScrollView(c);
        verticalScrollPagerWrapper.addView(horizonScrollPagerWrapper);
        addView(verticalScrollPagerWrapper, QH.LPLinear.one(-2,-2).to());
    }

    /**
     * 添加一个tab和对应的视图
     */
    public TabPagerLayout addTabAndPage(String title, View page){
        mPageList.add(page);
        mTabChangeCallback.add(null);

        mTabLayout.addTab(mTabLayout.newTab().setText(title));
        return this;
    }

    /**
     * tabLayout默认会被添加到自身布局中作为子布局。如果想拿出来放到别的地方，可以调用此方法。
     */
    public TabLayout detachTabLayout(){
        for(int i=0; i<getChildCount(); i++)
            if(getChildAt(i) == mTabLayout)
                removeView(mTabLayout);

        return mTabLayout;
    }

    /**
     * 设置是否自动为pager添加HorizontalScrollView和NestedScrollView。
     * <br/> 默认为true。即TabPagerLayout->NestedScrollView->HorizontalScrollView->pager这样的子布局关系
     * <br/> 若为false，则TabPagerLayout->->pager
     */
    public TabPagerLayout setContentScrollable(boolean scrollable){
        View lastView = getChildAt(getChildCount()-1);
        boolean currentScrollable = lastView == mPager;

        if(currentScrollable!=scrollable){
            if(scrollable){
                removeView(mPager);
                addScrollablePager(getContext());
            }else {
                ViewGroup subScrollView = (ViewGroup) ((ViewGroup)lastView).getChildAt(0);
                subScrollView.removeAllViews();
                ((ViewGroup) lastView).removeView(subScrollView);
                removeView(lastView);
                addView(mPager);
            }
        }

        return this;
    }

}
