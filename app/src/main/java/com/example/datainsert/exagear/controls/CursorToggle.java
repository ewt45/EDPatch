package com.example.datainsert.exagear.controls;

import android.annotation.SuppressLint;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

import com.eltechs.axs.Globals;
import com.eltechs.axs.applicationState.EnvironmentAware;
import com.eltechs.ed.R;

@SuppressLint("ViewConstructor")
public class CursorToggle extends ToggleButton {
    public CursorToggle() {
        super(Globals.getAppContext());
        setTextOn("🖱️");//🖱️
        setTextOff("🖱️");
        setBackgroundResource(R.drawable.ic_launcher_background);
        setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.d("CursorToggle", "onCheckedChanged: 切换光标显示："+isChecked);
                ((EnvironmentAware)Globals.getApplicationState()).getXServerViewConfiguration().setShowCursor(isChecked);
            }
        });
        setChecked(true);
    }


}
