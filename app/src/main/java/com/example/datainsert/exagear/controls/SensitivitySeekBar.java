package com.example.datainsert.exagear.controls;

import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.SeekBar;

import com.eltechs.axs.Globals;
import com.eltechs.axs.helpers.AndroidHelpers;

public class SensitivitySeekBar {
    static String TAG = "SensitivitySeekBar";
    public static float MOUSE_SENSITIVITY = 3.0f;
    public static void create(ViewGroup root){
        Context c = Globals.getAppContext();
        LinearLayout linear = new LinearLayout(c);
        linear.setOrientation(LinearLayout.VERTICAL);
        linear.setClipChildren(true);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(AndroidHelpers.dpToPx(50),-2);
        root.addView(linear,params);

        SeekBar seekBar = new SeekBar(c);
        seekBar.setBackgroundColor(0x6B000000);
        seekBar.setMax(100);
        seekBar.setRotation(-90);
        LinearLayout.LayoutParams seekParams = new LinearLayout.LayoutParams(AndroidHelpers.dpToPx(180),AndroidHelpers.dpToPx(180));
        seekParams.gravity = Gravity.CENTER;
        linear.addView(seekBar,seekParams);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                MOUSE_SENSITIVITY = progress/10f;
                Log.d(TAG, "鼠标灵敏度改变： "+MOUSE_SENSITIVITY);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }
}
