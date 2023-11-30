package com.ewt45.patchapp.widget;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.transition.TransitionManager;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ewt45.patchapp.AndroidUtils;
import com.ewt45.patchapp.MyApplication;
import com.ewt45.patchapp.R;

public class AppbarSwipeBehavior extends CoordinatorLayout.Behavior<AppBarLayout> {
    private static final String TAG = "AppbarSwipeBehavior";
    private final int STATE_FOLDED = 0, STATE_EXPANDING = 1, STATE_EXPANDED = 2, STATE_FOLDING = 3;
    private int touchSlop = 20;
    private int lastMotionY;
    private boolean isBeingDragged; //默认接收全部兄弟视图的触摸事件，所以这个flag不能删
    //    private boolean isExpanding;//true为下拉扩展，false为上滑缩小 在intercept返回true之前刷新
    private float maxMarginTop = 0; //下拉时，顶部最多能展开多高
    private float minExpandDiff = 0; //下拉时，手指至少要移动多少距离，松开后才会展开全屏
    private int currState = STATE_FOLDED;

    public AppbarSwipeBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
        //足够显示图标之后，高度不再变化，只变进度
        maxMarginTop = AndroidUtils.toPx(context, 56);
        minExpandDiff = AndroidUtils.toPx(context, 56);

    }


    @Override
    public boolean onInterceptTouchEvent(@NonNull CoordinatorLayout parent, @NonNull AppBarLayout child, MotionEvent ev) {
//        Log.d(TAG, "onInterceptTouchEvent: " + ev.getAction());

        if(currState ==STATE_EXPANDING || currState == STATE_FOLDING)
            return false;
        //参考HeaderBehavior的
//        return super.onInterceptTouchEvent(parent, child, ev);
        //ViewConfiguration.getScaledTouchSlop(); 居然有这种东西！可以判断手势是在滚动
        if (this.touchSlop < 0) {
            this.touchSlop = ViewConfiguration.get(parent.getContext()).getScaledTouchSlop();
        }

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastMotionY = (int) ev.getY();
                isBeingDragged = false;
                return false;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL: {
                isBeingDragged = false;
                return false;
            }
            case MotionEvent.ACTION_MOVE: {
                if (!isBeingDragged && shouldIntercept(parent, child, ev)) {
                    this.lastMotionY = (int) ev.getY();
//                    ViewGroup.LayoutParams lp = child.getLayoutParams();//如果已经全屏
//                    this.isExpanding = lp.height == WRAP_CONTENT;
                    currState = (currState + 1) % 4;
                    isBeingDragged = true;
                    return true;
                }
            }
        }
        return false;
    }

    private View getDragBar(ViewGroup parent) {
        return parent.findViewById(R.id.btn_drag);
    }

    private boolean shouldIntercept(CoordinatorLayout parent, AppBarLayout child, MotionEvent ev) {
        int y = (int) ev.getY();
        int yDiff = y - this.lastMotionY;
        View dragBar = getDragBar(child);
        if (dragBar == null || !dragBar.isShown() || Math.abs(yDiff) <= touchSlop)
            return false;
        //全屏时不处理了吧，点击fab关闭。上滑容易触发系统任务
        if(currState == STATE_FOLDING || currState == STATE_EXPANDED)
            return false;
        //下拉时，要求手指起始位置在appbarlayout内，且方向为向下
        //上滑时，手指起始位置在dragbar内，且方向为向上

        if (currState == STATE_FOLDED && yDiff > 0
                && parent.isPointInChildBounds(child.findViewById(R.id.appbar_patch_step_main), (int) ev.getX(), (int) ev.getY())) {
            return true;
        } else if (currState == STATE_EXPANDED && yDiff < 0
                && parent.isPointInChildBounds(getDragBar(child), (int) ev.getX(), (int) ev.getY())) {
            return true;
        } else
            return false;


//        ViewGroup.LayoutParams lp = child.getLayoutParams();//如果已经全屏
//        if(!(lp instanceof CoordinatorLayout.LayoutParams) || lp.height == MATCH_PARENT)
//            return false;
    }

    @Override
    public boolean onTouchEvent(@NonNull CoordinatorLayout parent, @NonNull AppBarLayout child, MotionEvent ev) {
        Log.d(TAG, "onTouchEvent: " + ev.getAction());

        //默认接收全部兄弟视图的触摸事件，所以这个flag不能删
        if (!isBeingDragged || (currState != STATE_EXPANDING && currState != STATE_FOLDING))
            return false;
        //拦截返回一次true之后，后续所有的event都会被送到这里
        //保证到这里的话，相关view都是可见的，也就是设置，帮助界面应该不会进到这里
        //一旦进到这里，lastMotionY就不变
        if (this.touchSlop < 0) {
            this.touchSlop = ViewConfiguration.get(parent.getContext()).getScaledTouchSlop();
        }

        int yDiff = (int) (ev.getY() - lastMotionY);

        switch (ev.getAction()) {
            //dragbar的margin归零。如果移动距离超出最小距离，则变化状态
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL: {


                if (Math.abs(yDiff) > minExpandDiff) {
                    //使用constraintSet切换的话，margin会自动重置吧
                    switchLogViewFullScreen(parent, child);
                    currState = (currState + 1) % 4;
                } else {
                    //松手回弹
                    setDragBarMarginOffset(parent, child, 0);
                    currState = (currState - 1 + 4) % 4;
                }
                isBeingDragged = false;
                return true;
            }
            //修改dragbar的margin实现拖拽效果
            case MotionEvent.ACTION_MOVE: {
                setDragBarMarginOffset(parent, child, yDiff);
            }
        }

        return true;
//        return super.onTouchEvent(parent, child, ev);
    }


    /**
     * 开始处理触摸事件后，通过修改margin移动dragbar，进行下拉或上滑。ydiff可能为负数注意处理
     */
    private void setDragBarMarginOffset(CoordinatorLayout parent, AppBarLayout child, int yDiff) {
        View dragBar = getDragBar(child);
        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) dragBar.getLayoutParams();

        int readyMargin = (int) Math.min(Math.abs(yDiff), maxMarginTop);
        //下拉时
        if (currState == STATE_EXPANDING && yDiff>=0) {
            params.topMargin = readyMargin;
        } else if (currState == STATE_FOLDING && yDiff<=0) {
            params.bottomMargin = readyMargin;
        }
        dragBar.setLayoutParams(params);

        //谷歌浏览器那种，返回手势的时候，颜色变化
//        if (isExpanding) {
//            View scaleCard = parent.findViewById(R.id.scaled_bg_card);
//            float scale = (float) Math.pow(Math.min(Math.abs(yDiff), minExpandDiff) / minExpandDiff, 3);
//            scaleCard.setScaleX(scale);
//            scaleCard.setScaleY(scale);
//        }
    }

    /**
     * 切换全屏。方法：appbarlayout和约束布局 高度改为match_parent铺满高度。
     * fab隐藏
     */
    public void switchLogViewFullScreen(CoordinatorLayout parent, AppBarLayout child) {
        if (currState != STATE_EXPANDING && currState != STATE_FOLDING)
            return;

        TransitionManager.beginDelayedTransition(parent); //这是动画效果？

        CoordinatorLayout.LayoutParams appbarLP = (CoordinatorLayout.LayoutParams) child.getLayoutParams();
        ConstraintLayout cLayout = (ConstraintLayout) getDragBar(child).getParent();
        AppBarLayout.LayoutParams cLayoutLP = (AppBarLayout.LayoutParams) cLayout.getLayoutParams();
        ConstraintSet set = new ConstraintSet();
        FAB fab = getFAB(parent);

        if (currState == STATE_EXPANDING) {
            appbarLP.height = MATCH_PARENT;
            cLayoutLP.height = MATCH_PARENT;
            set.clone(parent.getContext(), R.layout.appbar_patch_step_main_2_fullscreen);//获取全屏的constraintSet
            child.findViewById(R.id.btn_close_log).setOnClickListener(v->{
                currState = STATE_FOLDING;
                switchLogViewFullScreen(parent,child);
                currState = STATE_FOLDED;
            });
            ((TextView)child.findViewById(R.id.log_content)).setText(MyApplication.data.logText);

            Log.d(TAG, "switchLogViewFullScreen: 应该隐藏fab");
            //不知道为啥，setSize，hide都没反应
            ((View)fab).setVisibility(View.INVISIBLE);
            changeToolbarVisibility(parent,false);

            MyApplication.data.isShowingLog=true;
        } else if (currState == STATE_FOLDING) {
            appbarLP.height = WRAP_CONTENT;
            cLayoutLP.height = WRAP_CONTENT;
            set.clone(parent.getContext(), R.layout.appbar_patch_step_main_2);
            Log.d(TAG, "switchLogViewFullScreen: 应该显示fab");
            fab.hide();
            fab.show();
            changeToolbarVisibility(parent,true);
;
            MyApplication.data.isShowingLog=false;
        }

        child.setLayoutParams(appbarLP);
        cLayout.setLayoutParams(cLayoutLP);//难道说set没法修改根约束布局的width和height吗（好像也合理，约束布局的lp是父布局设置的，跟set没关系了）
        set.applyTo(cLayout);

    }


    private void changeToolbarVisibility(ViewGroup parent,boolean visible) {
        Menu menu = ((Toolbar)parent.findViewById(R.id.toolbar)).getMenu();
        for(int i=0; i<menu.size(); i++){
            menu.getItem(i).setVisible(visible);
        }
    }

    private FAB getFAB(ViewGroup parent) {
        return parent.findViewById(R.id.fab);
    }



}
