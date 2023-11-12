package com.ewt45.patchapp.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.util.AttributeSet;
import android.view.View;

public class FAB extends FloatingActionButton implements View.OnClickListener {


    /**
     * 记录上一个（0）和当前点击监听（1）
     */
    private OnClickListener[] backListener =  new OnClickListener[2];
    private OnClickListener currOnClickListener = null;
    private OnClickListener logOnClickListener = null;
    public FAB(Context context) {
        super(context);
    }

    public FAB(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FAB(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 修复 切换后图标不显示问题。先hide，再设置图标，再show
     * @param resId the resource identifier of the drawable
     *
     */
    @Override
    public void setImageResource(int resId) {
        hide();
        super.setImageResource(resId);
        show();

    }


    @Override
    public void setImageDrawable(@Nullable Drawable drawable) {
        hide();
        super.setImageDrawable(drawable);
        show();
    }

    /**
     * 保存上一个listener，用于日志全屏退出后恢复点击监听
     * @param l The callback that will run
     *
     */
    @Override
    public void setOnClickListener(@Nullable OnClickListener l) {
//        backListener[0]  = backListener[1];
//        backListener[1] = l;
//        super.setOnClickListener(l);
        currOnClickListener = l;
        super.setOnClickListener(this);
    }

//    /**
//     * 回退到上一个点击监听（用于全屏日志退出后）
//     */
//    public void restoreLastListener(){
//        backListener[1] = backListener[0]; //防止连续点击两次。。
//        setOnClickListener(backListener[0]);
//    }

    /**
     * 用于全屏日志 想退出时。 若不为null，则之后执行此listener，若为null，则之后执行正常listener
     */
    public void setLogClickListener(OnClickListener listener){
        logOnClickListener = listener;

    }



    @Override
    public void onClick(View v) {
        if(logOnClickListener!=null)
            logOnClickListener.onClick(v);
        else if(currOnClickListener!=null)
            currOnClickListener.onClick(v);
    }
}
