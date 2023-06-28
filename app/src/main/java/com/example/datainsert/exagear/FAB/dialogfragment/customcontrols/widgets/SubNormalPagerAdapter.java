package com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.widgets;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

public class SubNormalPagerAdapter extends PagerAdapter implements WrapContentViewPager.ItemHelper {
    private static final String TAG= "SubNormalPagerAdapter";
    private final String[] mTabTitles;
    private final View[] mViewPages;
    public SubNormalPagerAdapter(View[] viewPages,String[] tabTitles) {
        mTabTitles = tabTitles;
        mViewPages = viewPages;
    }

    @Override
    public int getCount() {
        return mTabTitles.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view==o;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
//        Log.d(TAG, "instantiateItem: 第"+position+"个视图的高度为"+        mViewPages[position].getMeasuredHeight());

        container.addView(mViewPages[position]);
        return mViewPages[position];
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView(mViewPages[position]);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mTabTitles[position];
    }

    @Override
    public Object getCurrentItem(int position) {
        return mViewPages[position];
    }
}
