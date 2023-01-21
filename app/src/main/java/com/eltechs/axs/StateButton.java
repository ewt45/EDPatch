package com.eltechs.axs;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

@SuppressLint("AppCompatCustomView")
public class StateButton extends Button implements View.OnTouchListener {

    public StateButton(Context context, ButtonEventReporter buttonEventReporter, boolean z, int i, int i2, String str) {
        super(context);

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return false;
    }
}
