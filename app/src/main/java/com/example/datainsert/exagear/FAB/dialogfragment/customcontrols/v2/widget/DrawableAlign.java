package com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.widget;

import static com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.Const.dp8;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.TestHelper;
import com.example.datainsert.exagear.RR;

/**
 * 将一个drawable绘制到右上角
 */
public class DrawableAlign extends Drawable {
    private final Drawable mInner;
    private final View mHostView;
    private final int radius =dp8;
    private final int CIRCLE_WIDTH = 4;
    private final int PADDING_BETWEEN_TEXT_AND_CIRCLE = dp8/2;
    private final int PADDING_BETWEEN_CIRCLE_AND_BOUNDARY = 0;
    public DrawableAlign(View host){
        mInner = TestHelper.getAssetsDrawable(host.getContext(),"controls/help.xml");
        mInner.setTint(RR.attr.colorControlNormal(host.getContext()));
        mHostView = host;
    }
    @Override
    public void draw(@NonNull Canvas canvas) {
        mInner.setBounds(mHostView.getWidth()-radius*2,0,mHostView.getWidth(),radius*2);
        mInner.draw(canvas);

    }

    @Override
    public void setAlpha(int alpha) {
        mInner.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {
        mInner.setColorFilter(colorFilter);
    }

    @Override
    public int getOpacity() {
        return mInner.getOpacity();
    }
}
