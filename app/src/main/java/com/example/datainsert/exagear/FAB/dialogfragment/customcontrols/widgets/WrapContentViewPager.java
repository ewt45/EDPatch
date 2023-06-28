package com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.widgets;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.view.View;

public class WrapContentViewPager extends ViewPager {

    public WrapContentViewPager(@NonNull Context context) {
        super(context);
        this.addOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                requestLayout();//激活onMeasure
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {


        if (getAdapter() == null || !(getAdapter() instanceof ItemHelper)) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            return;
        }

        int height = 0;
        //获取当前的视图
        View v = (View) ((ItemHelper) getAdapter()).getCurrentItem(getCurrentItem());
        if (v != null) {
            v.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
            height = v.getMeasuredHeight();
        }
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public interface ItemHelper {
        public Object getCurrentItem(int position);
    }
}
