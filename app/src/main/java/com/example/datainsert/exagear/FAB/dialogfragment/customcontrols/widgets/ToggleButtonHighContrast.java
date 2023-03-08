package com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.widgets;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.InsetDrawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.Shape;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.graphics.ColorUtils;
import android.util.Log;
import android.widget.ToggleButton;

import com.ewt45.exagearsupportv7.R;
import com.example.datainsert.exagear.FAB.FabMenu;
import com.example.datainsert.exagear.QH;

import java.util.Arrays;

public class ToggleButtonHighContrast extends ToggleButton {

    GradientDrawable mGradientDrawable;
    public ToggleButtonHighContrast(Context context) {
        super(context);

        //更换选中颜色
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            setupHighContrastCheckedColor();
        }

    }

    /**
     * hugo的按钮勾选和不勾选看不出来了。设置成白色和黑色？
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void setupHighContrastCheckedColor(){
        if(!(getBackground() instanceof InsetDrawable) || !(((InsetDrawable) getBackground()).getDrawable() instanceof LayerDrawable))
            return;
        LayerDrawable layerDrawable =(LayerDrawable) ( (InsetDrawable) getBackground()).getDrawable();
        if(layerDrawable==null)
            return;

        //获取内层drawable，找到显示颜色的那个
        for(int i=0; i<layerDrawable.getNumberOfLayers(); i++)
            if(layerDrawable.getDrawable(i) instanceof GradientDrawable){
                mGradientDrawable = (GradientDrawable) layerDrawable.getDrawable(i);
                break;
            }

        //getstate是获取各种定义的状态(不对，是当前状态）: new int[]{android.R.attr.state_enabled, android.R.attr.state_multiline,0};//(16842910,16833597,0)
        // getcolor是对应状态的颜色  三个颜色0x24000000 半透明黑; 0xFF03DAC5 绿色; 0x8a000000 还是黑
//                    ColorStateList toggleColors = ((GradientDrawable) sub).getColor();

        //重新分配勾选颜色
        if(mGradientDrawable!=null && mGradientDrawable.getColor()!=null){
            ColorStateList currentColors = mGradientDrawable.getColor();
            ColorStateList colorStateList = new ColorStateList(
                    new int[][]{
                            new int[]{android.R.attr.state_enabled, -android.R.attr.state_checked},
                            new int[]{android.R.attr.state_enabled},
                            new int[]{}
                    },
                    new int[]{0x8affffff,0x8a000000,0x24000000}
            );
//                colorStateList.getColorForState();
            int originUncheckColor = 0xff000000| currentColors.getColorForState(new int[]{android.R.attr.enabled,-android.R.attr.state_checked},0xffffffff );
            int originCheckColor =0xff000000| currentColors.getColorForState(new int[]{android.R.attr.enabled,android.R.attr.state_checked},0xffffffff);

            //如果对比度不够就设置成黑白
            if(ColorUtils.calculateContrast(originUncheckColor,originCheckColor)<4.5){
                mGradientDrawable.setColor(colorStateList);
            }
        }
    }



    private void testBackgroundColor(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Log.d(TAG, "onViewCreated: 看看togglebutton的drawable：\n"+
                    "getButtonDrawable= "+getButtonDrawable()+
                    "\ngetCompoundDrawables= "+ Arrays.toString(getCompoundDrawables())+
                    "\ngetBackground= "+getBackground());
            //getbackground = InsetDrawable,其他为null
            InsetDrawable insetDrawable = (InsetDrawable) getBackground();
            //insetDrawable.getDrawable是layoutdrawable
            LayerDrawable layerDrawable = (LayerDrawable)insetDrawable.getDrawable();
            assert layerDrawable!=null;
            for(int i=0; i<layerDrawable.getNumberOfLayers(); i++){
                android.graphics.drawable.Drawable sub = layerDrawable.getDrawable(i);
                Log.d(TAG, "onViewCreated: layoutdrawable的drawble="+sub);;
                if(sub instanceof GradientDrawable){
                    ((GradientDrawable) sub).getConstantState();
                    //三个颜色0x24000000 半透明黑0xFF03DAC5 绿色0x8a000000 还是黑
                    ColorStateList toggleColors = ((GradientDrawable) sub).getColor();

                }

            }
        }
    }
}
