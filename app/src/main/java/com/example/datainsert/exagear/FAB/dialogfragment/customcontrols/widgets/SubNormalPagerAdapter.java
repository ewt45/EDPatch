package com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.widgets;

import static com.example.datainsert.exagear.RR.getS;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.SubView1Mouse;
import com.example.datainsert.exagear.RR;

public class SubNormalPagerAdapter extends PagerAdapter implements WrapContentViewPager.ItemHelper {
    private static final String TAG= "SubNormalPagerAdapter";
    private final int mCount;
    private final String[] mTabTitles;
    private final View[] mViewPages;
    public SubNormalPagerAdapter(int count,View[] viewPages,String[] tabTitles) {
        mCount=count;
        mTabTitles = tabTitles;
        mViewPages = viewPages;
    }

    @Override
    public int getCount() {
        return mCount;
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
