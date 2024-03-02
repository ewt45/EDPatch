package com.example.datainsert.exagear.controlsV2.widget;

import android.animation.LayoutTransition;
import android.content.Context;
import android.support.design.widget.TabLayout;
import android.support.v4.widget.NestedScrollView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import com.example.datainsert.exagear.controlsV2.TestHelper;
import com.example.datainsert.exagear.QH;

import java.util.ArrayList;
import java.util.List;

/**
 * 实现一个简单的tab+pager。基本逻辑：点击tab，下方布局容器先删除当前page再添加新的对应page。
 * <br/> pager是个framelayout，放在了HorizontalScrollView和NestedScrollView 所以子布局应该不需要再添加滚动的视图
 * <br/> 基本使用：new TabPagerLayout(c).addTabAndPage("属性",propEditView)
 * <br/> 由于可以从布局中移除tablayout，所以不应对tablayout做布局相关的操作
 * <bt/> 布局树：TabPagerLayout -> NestedScrollView -> HorizontalScrollView -> pager
 */
public class TabPagerLayout extends LinearLayout implements TabLayout.OnTabSelectedListener{
    private final TabLayout mTabLayout;
    private NestedScrollView mVerticalScrollView;
    private HorizontalScrollView mHorizontalScrollView;
    private final FrameLayout mPager;
    private final List<View> mPageList = new ArrayList<>();
    private final List<Runnable> mTabChangeCallback = new ArrayList<>();
    private boolean isScrollable = true;
    public TabPagerLayout(Context c) {
        super(c);
        setOrientation(VERTICAL);

        mTabLayout = new TabLayout(c);
        mTabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        TestHelper.setTabLayoutTabMinWidth0(mTabLayout);
        //点击tab时切换pager的视图，并执行回调。这个应该在添加第一个tab前设置，否则添加第一个tab时自动调动select就无法触发这个监听器了
        mTabLayout.addOnTabSelectedListener(this);

        mPager = new FrameLayout(c);
        mPager.setId(generateViewId());
        mPager.setLayoutTransition(new LayoutTransition());

        mVerticalScrollView = new NestedScrollView(c);
        mVerticalScrollView.setId(generateViewId());
        mHorizontalScrollView = new HorizontalScrollView(c);
        mHorizontalScrollView.setId(generateViewId());

        addView(mTabLayout, QH.LPLinear.one(-2,-2).to());
        addView(mPager);
        setPagerScrollable(false,false);
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
     * 找到pager及其相关滚动布局 将他们移出布局树
     */
    private void detachPagerAndScrollView(){
        ViewGroup targetVG = this;
        //从内往外移除，否则无法移除已被移除视图的子视图了

        if(findViewById(mPager.getId())!=null)
            ((ViewGroup)mPager.getParent()).removeView(mPager);
        if(findViewById(mHorizontalScrollView.getId())!=null)
            ((ViewGroup)mHorizontalScrollView.getParent()).removeView(mHorizontalScrollView);
        if(findViewById(mVerticalScrollView.getId())!=null)
            ((ViewGroup)mVerticalScrollView.getParent()).removeView(mVerticalScrollView);
    }

    /**
     * 将pager添加到布局树中。调用此函数前请确保pager，竖向和横向滚动布局 三个视图都不在布局树中
     * @param vScroll 竖向是否添加滚动布局
     * @param hScroll 横向是否添加滚动布局
     */
    private void attachPager(boolean vScroll, boolean hScroll){
        View tabPagerLayoutDirectSubview = mPager;
        if(hScroll){
            mHorizontalScrollView.addView(tabPagerLayoutDirectSubview);
            tabPagerLayoutDirectSubview = mHorizontalScrollView;
        }
        if(vScroll){
            mVerticalScrollView.addView(tabPagerLayoutDirectSubview);
            tabPagerLayoutDirectSubview = mVerticalScrollView;
        }
        addView(tabPagerLayoutDirectSubview);
    }

    /**
     * 若为true，pager外层添加滚动布局，默认为false
     * @param vScroll 竖向是否添加滚动布局NestedScrollView
     * @param hScroll 横向是否添加滚动布局HorizontalScrollView
     */
    public TabPagerLayout setPagerScrollable(boolean vScroll, boolean hScroll){
        detachPagerAndScrollView();
        attachPager(vScroll,hScroll);
        return this;
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        int index = -1;
        for(int i=0; i<mTabLayout.getTabCount(); i++)
            if(mTabLayout.getTabAt(i) == tab){
                index = i;
                break;
            }
        mPager.removeAllViews();
        mPager.addView(mPageList.get(index));
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

}
