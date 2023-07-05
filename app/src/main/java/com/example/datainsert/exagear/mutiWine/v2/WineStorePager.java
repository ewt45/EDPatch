package com.example.datainsert.exagear.mutiWine.v2;

import static com.example.datainsert.exagear.RR.getS;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.example.datainsert.exagear.RR;

@SuppressLint("ViewConstructor")
public class WineStorePager extends ViewPager {

    private final ViewGroup[] mViewPages;

    public WineStorePager(@NonNull Context context, ViewGroup[] pages) {
        super(context);
        mViewPages = pages;

        String tabLocal = getS(RR.mw_tabTitles).split("\\$")[0];
        String tabDownload = getS(RR.mw_tabTitles).split("\\$")[1];
        String tabTips = getS(RR.mw_tabTitles).split("\\$")[2];
        //设置适配器
        setAdapter(new PagerAdapter() {

            private final String[] mTabTitles = new String[]{tabLocal, tabDownload, tabTips};
            private final View[] mViewPages = pages;

            @Override
            public int getCount() {
                return mTabTitles.length;
            }

            @Override
            public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
                return view == o;
            }

            @NonNull
            @Override
            public Object instantiateItem(@NonNull ViewGroup container, int position) {
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
        });

        //每次进入本地页面的时候重新检测
        addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int position) {

                if (position == 0) {
                    RecyclerView recyclerView = null;
                    for (int i = 0; i < mViewPages[0].getChildCount(); i++) {
                        View view = mViewPages[0].getChildAt(i);
                        if (view instanceof RecyclerView) {
                            recyclerView = (RecyclerView) view;
                            break;
                        }
                    }
                    if (recyclerView == null)
                        return;
                    RecyclerView.Adapter adapter = recyclerView.getAdapter(); //LocalWineAdapter的回收视图
                    if (adapter instanceof LocalAdapter)
                        ((LocalAdapter) adapter).refresh(false);
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }


}
