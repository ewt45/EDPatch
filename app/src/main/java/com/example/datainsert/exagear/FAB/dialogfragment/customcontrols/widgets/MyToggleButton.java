package com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.widgets;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.ToggleButton;

public class MyToggleButton extends ToggleButton {
    public MyToggleButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void setOnCheckedChangeListener(@Nullable OnCheckedChangeListener listener) {
        super.setOnCheckedChangeListener(listener);
        setChecked(false);
        toggle();

    }


}
