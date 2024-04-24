package com.example.datainsert.exagear.controlsV2.widget;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * 普通gradientDrawable的描边是以当前边界为中心，向外和向内延伸出一半的描边宽度
 * <br/> 半透明时就会有一半和内侧颜色重叠很难看。
 */
public class DrawableNoOverlapStroke extends Drawable {
    GradientDrawable mStroke = new GradientDrawable();
    GradientDrawable mSolid = new GradientDrawable();

    Rect mRect = new Rect();

    int mStrokeWidth;
    public DrawableNoOverlapStroke(){
//        mSolid.setShape(GradientDrawable.OVAL);
//        mStroke.setShape(GradientDrawable.RING);
    }

    @Override
    public void setBounds(@NonNull Rect bounds) {
        super.setBounds(bounds);
        //但是bound到底是填充色边界，还是描边 边界？
    }

    public void setStroke(int width, int color) {
        mStroke.setStroke(width, color);
        mStrokeWidth = width;
    }

    public void setColor(int color) {
        mSolid.setColor(color);
    }

    public void setCornerRadius(int radius) {
        mStroke.setCornerRadius(radius);
        mSolid.setCornerRadius(radius);
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        mRect.set(getBounds());
        mStroke.setBounds(mRect);
        mRect.set(mRect.left+mStrokeWidth, mRect.top+mStrokeWidth, mRect.right-mStrokeWidth, mRect.bottom-mStrokeWidth);
        mSolid.setBounds(mRect);

        mSolid.draw(canvas);
        mStroke.draw(canvas);
    }

    @Override
    public void setAlpha(int alpha) {
        mSolid.setAlpha(alpha);
        mStroke.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {
        mSolid.setColorFilter(colorFilter);
        mStroke.setColorFilter(colorFilter);
    }

    @Override
    public int getOpacity() {
        return mSolid.getOpacity();
    }


}
