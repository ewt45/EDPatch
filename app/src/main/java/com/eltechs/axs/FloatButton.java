package com.eltechs.axs;


import android.content.Context;
import android.support.v7.widget.AppCompatButton;
import android.view.MotionEvent;
import android.view.View;


public class FloatButton extends AppCompatButton implements View.OnTouchListener {

    public FloatButton(Context context, ButtonEventListener buttonEventListener) {
        super(context);
//        setBackgroundResource(com.eltechs.ed.R.drawable.round_button);
//        setAlpha(0.5f);
//        this.eventListener = buttonEventListener;
//        setOnTouchListener(this);
    }



    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return false;
    }
}
