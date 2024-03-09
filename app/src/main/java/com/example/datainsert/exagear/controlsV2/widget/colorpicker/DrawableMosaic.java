package com.example.datainsert.exagear.controlsV2.widget.colorpicker;

import static com.example.datainsert.exagear.controlsV2.Const.dp8;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * 绘制一个马赛克背景 用于标识透明
 * <br/> 用于 {@link ColorPicker} 中
 */
public class DrawableMosaic extends Drawable {
    private static final int white = 0x00FAFAFA;
    private static final int grey = 0x00B3B3B3;
    private final Paint paint;
    private int alpha = 255;

    public DrawableMosaic() {
        paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
    }


    @Override
    public void draw(@NonNull Canvas canvas) {
        Rect bound = getBounds();
        float centerX = bound.left + bound.width() / 2f;
        float centerY = bound.top + bound.height() / 2f;
        //灰色（左上，右下）
        paint.setColor(grey | (alpha << 24));
        canvas.drawRect(bound.left, bound.top, centerX, centerY, paint);
        canvas.drawRect(centerX, centerY, bound.right, bound.bottom, paint);
        //白色（右上，左下）
        paint.setColor(white | (alpha << 24));
        canvas.drawRect(centerX, bound.top, bound.right, centerY, paint);
        canvas.drawRect(bound.left, centerY, centerX, bound.bottom, paint);
    }

    @Override
    public void setAlpha(int alpha) {
        this.alpha = alpha;
    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {

    }

    @Override
    public int getOpacity() {
        return PixelFormat.UNKNOWN;
    }
}
