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
import android.graphics.drawable.RippleDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.Shape;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.graphics.ColorUtils;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.widget.ToggleButton;

import com.eltechs.ed.R;
import com.example.datainsert.exagear.FAB.FabMenu;
import com.example.datainsert.exagear.QH;

import java.util.Arrays;

/**
 * 分析一下drawable
 *  = InsetDrawable
 * InsetDrawable(getBackground())
 * |- LayerDrawable(getDrawable())
 * |- |- RippleDrawable (getDrawable(0))
 * |- |- GradientDrawable(getDrawable(1))
 */
public class ToggleButtonHighContrast extends ToggleButton {
    boolean shouldChangeColor = false;

    //按钮背景
    RippleDrawable mRippleDrawable;
    //勾选时颜色变化的底部那一条
    GradientDrawable mGradientDrawable;

    public ToggleButtonHighContrast(Context context) {
        super(context);

        //更换选中颜色
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            setupHighContrastCheckedColor();
        }

    }
    //重新分配勾选颜色
    ColorStateList botColorStateList = new ColorStateList(
            new int[][]{
                    new int[]{android.R.attr.state_enabled, -android.R.attr.state_checked},
                    new int[]{android.R.attr.state_enabled},
                    new int[]{}
            },
            new int[]{0xb0000000,0xb0ffffff,0x24000000}
    );

    ColorStateList btnBgStateList = new ColorStateList(
            new int[][]{
                    new int[]{android.R.attr.state_enabled, -android.R.attr.state_checked},
                    new int[]{android.R.attr.state_enabled},
                    new int[]{}
            },
            new int[]{0xffffffff,0xff000000,0xff444444}
    );
    /**
     * hugo的按钮勾选和不勾选看不出来了。设置成白色和黑色？
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void setupHighContrastCheckedColor(){



        if(shouldChangeColor()){
            //底端那一条和文字颜色相同，二者和背景颜色相反
            setTextColor(botColorStateList);
            setBackgroundTintList(btnBgStateList); //用了这个方法，默认样式的drawable就被替换掉了
//        mGradientDrawable.setColor(botColorStateList);
//        ((GradientDrawable)mRippleDrawable.getDrawable(0)).setColor(ColorStateList.valueOf(Color.GREEN));
        }



    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private boolean shouldChangeColor(){
        if(shouldChangeColor)
            return true;
        else{
            if(!(getBackground() instanceof InsetDrawable) || !(((InsetDrawable) getBackground()).getDrawable() instanceof LayerDrawable))
                return false;
            LayerDrawable layerDrawable =(LayerDrawable) ( (InsetDrawable) getBackground()).getDrawable();
            if(layerDrawable==null)
                return false;

            //获取内层drawable，找到显示颜色的那个
            for(int i=0; i<layerDrawable.getNumberOfLayers(); i++){
                Drawable layer = layerDrawable.getDrawable(i) ;
                if(layer instanceof GradientDrawable){
                    mGradientDrawable = (GradientDrawable)layer;
                }else if(layer instanceof RippleDrawable)
                    mRippleDrawable = (RippleDrawable) layer;
            }

            //getstate是获取各种定义的状态(不对，是当前状态）: new int[]{android.R.attr.state_enabled, android.R.attr.state_multiline,0};//(16842910,16833597,0)
            // getcolor是对应状态的颜色  三个颜色0x24000000 半透明黑; 0xFF03DAC5 绿色; 0x8a000000 还是黑
//                    ColorStateList toggleColors = ((GradientDrawable) sub).getColor();

            //stateList有私有成员变量mStateSpec，不过没法直接获取。
            // 这里通过state获取颜色的话，注意虽然选中时的state colorStateList里只需要写一个enable就行了，

            ColorStateList currentBotColors = mGradientDrawable.getColor();
            if(currentBotColors==null)
                 return false;

            // 不过用getColorForState获取颜色的话需要写全enable和checked
            int originUncheckColor = 0xff000000| currentBotColors.getColorForState(
                    new int[]{android.R.attr.state_enabled,-android.R.attr.state_checked},0xffffffff );
            int originCheckColor =0xff000000| currentBotColors.getColorForState(
                    new int[]{android.R.attr.state_enabled,android.R.attr.state_checked},0xffffffff);

            //如果对比度不够就设置成黑白
            shouldChangeColor = ColorUtils.calculateContrast(originUncheckColor,originCheckColor)<4.5;
        }
        return shouldChangeColor;
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
