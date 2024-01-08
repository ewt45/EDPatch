package com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.edit;

import static com.example.datainsert.exagear.RR.dimen.minCheckSize;

import android.content.Context;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.support.design.widget.TabLayout;
import android.support.v4.widget.NestedScrollView;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
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
public class EditMain extends LinearLayout {
    private static final String TAG = "EditConfigWindow";
    final TouchAreaView mHost;
    final EditConfigWindow mWindow;
    final View mToolbar;

    public EditMain(TouchAreaView touchAreaView, EditConfigWindow window) {
        super(touchAreaView.getContext());
        mHost = touchAreaView;
        mWindow = window;
        Context c = touchAreaView.getContext();

        setOrientation(VERTICAL);

        //TODO 颜色适配
//        setBackgroundColor(getBGColor(context));

        //顶部工具栏
        TabLayout tabToolbar = new TabLayout(c);
        tabToolbar.setTabMode(TabLayout.MODE_FIXED);
        tabToolbar.setLayoutParams(QH.LPLinear.one(0, -2).weight().to());
        this.mToolbar = tabToolbar;

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
//        linearPager.addView(LayoutInflater.from(getContext()).inflate(R.layout.aaa_test_key_properties,linearPager,false));
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
