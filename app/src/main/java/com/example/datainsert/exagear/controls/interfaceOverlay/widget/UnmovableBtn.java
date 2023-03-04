package com.example.datainsert.exagear.controls.interfaceOverlay.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Point;
import android.graphics.PointF;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.eltechs.axs.Finger;
import com.eltechs.axs.GeometryHelpers;
import com.eltechs.axs.widgets.viewOfXServer.ViewOfXServer;
import com.example.datainsert.exagear.controls.model.OneKey;

public class UnmovableBtn extends RegularKeyBtn {
    public UnmovableBtn(Context context, OneKey oneKey, ViewOfXServer viewOfXServer) {
        super(context, oneKey, viewOfXServer);
        setOnClickListener(null);
    }

    public static UnmovableBtn getSample(Context c) {
        OneKey oneKey = new OneKey(0);
        return new UnmovableBtn(c, new OneKey(0), null) {
            final PointF point = new PointF();

            @Override
            public void onClick(View v) {
                Log.d("TAG", "onClick: 难道是自己重写的优先级高于别人设置的？");
            }

            //为啥必须要完全重写onTouchEvent，否则recyclerview里就接收不到点击事件？？（啊原来这个类里就重写了，没有调用点击事件了）
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouchEvent(MotionEvent event) {
                if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
                    setPressed(true);
                    point.x = event.getRawX();
                    point.y = event.getRawY();
                } else if (event.getActionMasked() == MotionEvent.ACTION_UP && GeometryHelpers.distance(point, new PointF(event.getRawX(), event.getRawY())) == 0){
                    setPressed(false);
                    performClick();
                } else if(event.getActionMasked() == MotionEvent.ACTION_CANCEL){
                    setPressed(false);
                }
                return true;
            }

            @Override
            public void injectMove(Finger finger) {
            }

            @Override
            public void injectRelease(Finger finger) {
            }

            @Override
            public void injectPress(Finger finger) {

            }
        };
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
//        //设置移动过，这样松手时不会触发点击事件
//        if(event.getActionMasked() == MotionEvent.ACTION_DOWN){
//            setMoved(true);
//            return true;
//        }
//        //移动过程中不调用移动的那些操作
//        else if(event.getActionMasked() == MotionEvent.ACTION_MOVE){
//            return false;
//        }
//        else
//         return super.onTouchEvent(event);
        if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
            setPressed(true);
            injectPress(null);
        } else if (event.getActionMasked() == MotionEvent.ACTION_UP || event.getActionMasked() == MotionEvent.ACTION_CANCEL) {
            setPressed(false);
            injectRelease(null);
        }
        return true;
    }
}
