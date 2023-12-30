package com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.edit;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.support.annotation.NonNull;
import android.support.v4.widget.NestedScrollView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.HorizontalScrollView;
import android.widget.ToggleButton;

import com.eltechs.ed.R;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.TestHelper;

/**
 * 用于选择按键码时，显示全键盘布局
 */
public class KeyOnBoardView extends NestedScrollView {
    public KeyOnBoardView(@NonNull Context context) {
        super(context);

        HorizontalScrollView horizontalScrollView = new HorizontalScrollView(context);
        addView(horizontalScrollView, new NestedScrollView.LayoutParams(-2, -2));

        //TODO 最后移到assets
        ViewGroup keyboardView = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.aaa_available_key_view, horizontalScrollView, false);
        setupUI(keyboardView);
        horizontalScrollView.addView(keyboardView);
    }

    /**
     * 子布局全部为CompoundButton
     * 设置textOn和textOff全部为getText
     * 设置Drawable
     */
    private void setupUI(ViewGroup keyboardView) {
        /*
        <selector xmlns:android="http://schemas.android.com/apk/res/android">
            <item android:state_checked="false" android:drawable="@drawable/btn_toggle_off" />
            <item android:state_checked="true" android:drawable="@drawable/btn_toggle_on" />
        </selector>
         */



        for(int i=0; i<keyboardView.getChildCount(); i++){
            CompoundButton btn = (CompoundButton) keyboardView.getChildAt(i);
            btn.setButtonDrawable(null);
            btn.setBackground(TestHelper.getAssetsDrawable(getContext(),"controls/keyboard_key_toggle.xml"));
            btn.setTextAlignment(TEXT_ALIGNMENT_CENTER);
            btn.setTextColor(0xFFF3F3F3);
            if(btn instanceof ToggleButton){
                ((ToggleButton) btn).setTextOn(((ToggleButton) btn).getTextOff());
            }
        }



    }
}
