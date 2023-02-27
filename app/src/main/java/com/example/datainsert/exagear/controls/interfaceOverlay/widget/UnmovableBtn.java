package com.example.datainsert.exagear.controls.interfaceOverlay.widget;

import android.content.Context;
import android.view.MotionEvent;

import com.eltechs.axs.widgets.viewOfXServer.ViewOfXServer;
import com.example.datainsert.exagear.controls.model.OneKey;

public class UnmovableBtn extends RegularKeyBtn {
    public UnmovableBtn(Context context, OneKey oneKey, ViewOfXServer viewOfXServer) {
        super(context, oneKey, viewOfXServer);
        setOnClickListener(null);
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
        if(event.getActionMasked() == MotionEvent.ACTION_DOWN){
            setPressed(true);
            injectPress(null);
        }else if(event.getActionMasked() == MotionEvent.ACTION_UP || event.getActionMasked() == MotionEvent.ACTION_CANCEL){
            setPressed(false);
            injectRelease(null);
        }
        return true;
    }
}
