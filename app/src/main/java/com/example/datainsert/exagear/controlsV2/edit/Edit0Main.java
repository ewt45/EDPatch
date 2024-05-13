package com.example.datainsert.exagear.controlsV2.edit;

import static com.example.datainsert.exagear.RR.getS;

import android.animation.LayoutTransition;
import android.content.Context;
import android.support.design.widget.TabLayout;
import android.view.View;
import android.widget.RelativeLayout;

import com.example.datainsert.exagear.FAB.dialogfragment.drived.SimpleTabSelectListener;
import com.example.datainsert.exagear.QH;
import com.example.datainsert.exagear.RR;
import com.example.datainsert.exagear.controlsV2.TestHelper;

import java.lang.reflect.InvocationTargetException;

/**
 * 编辑模式下，用于修改设置的悬浮窗
 */
public class Edit0Main extends RelativeLayout implements EditConfigWindow.OnReEnterListener, EditConfigWindow.RequestFullScreen{
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


        QH.addTabLayoutListener(tabToolbar,(TestHelper.SimpleTabListener) tab -> {
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

        String[] tabTitles = RR.getSArr(RR.ctr2_editTab_titles);
        tabToolbar.addTab(tabToolbar.newTab().setText(tabTitles[0]).setTag("key")); //按键
        tabToolbar.addTab(tabToolbar.newTab().setText(tabTitles[1]).setTag("gesture"));//手势
        tabToolbar.addTab(tabToolbar.newTab().setText(tabTitles[2]).setTag("profile"));//多配置
        tabToolbar.addTab(tabToolbar.newTab().setText(tabTitles[3]).setTag("other"));//其他

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

    @Override
    public boolean isApplyLimit() {
        return true;
    }
}
