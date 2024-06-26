package com.example.datainsert.exagear.controlsV2.widget;

import static android.graphics.Shader.TileMode.REPEAT;
import static com.example.datainsert.exagear.controlsV2.Const.dp8;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.Region;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

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

    /**
     * 原马赛克只有四格，此函数返回一个TileMode为REPEAT的BitmapDrawable，重复平铺填满画面
     * @param resources 一般是context.getResources()
     * @param cornerRadius bitmapDrawable的圆角大小
     * @return BitmapDrawable
     */
    public static BitmapDrawable repeatedBitmapDrawable(Resources resources, float cornerRadius){
        Drawable bgAlertSingleDrawable = new DrawableMosaic();//c.getDrawable(R.drawable.alpha_bg);
        Bitmap bgAlertSingleBitmap = Bitmap.createBitmap(dp8*2,dp8*2, Bitmap.Config.ARGB_8888);
        Canvas tmpCanvas = new Canvas(bgAlertSingleBitmap);
        bgAlertSingleDrawable.setBounds(0, 0, bgAlertSingleBitmap.getWidth(), bgAlertSingleBitmap.getHeight());
        bgAlertSingleDrawable.draw(tmpCanvas);
        //BitmapDrawable没有自带设置圆角的函数，直接重写draw，里面clipRoundRect。
        BitmapDrawable bgAlertRepeatDrawable = new BitmapDrawable(resources, bgAlertSingleBitmap) {
            private final Path roundRectPath = new Path();

            @Override
            protected void onBoundsChange(Rect b) {
                super.onBoundsChange(b);
                roundRectPath.reset();
                roundRectPath.addRoundRect(b.left, b.top, b.right, b.bottom, cornerRadius, cornerRadius, Path.Direction.CW);
            }

            @Nullable
            @Override
            public Region getTransparentRegion() {
                Log.d("TAG", "getTransparentRegion: 半径="+cornerRadius);
                return null;
            }

            @Override
            public void draw(Canvas canvas) {
                canvas.save();
                canvas.clipPath(roundRectPath);
                super.draw(canvas);
                canvas.restore();
            }
        };
        bgAlertRepeatDrawable.setTileModeXY(REPEAT, REPEAT);
        return bgAlertRepeatDrawable;
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
    public void setColorFilter(@Nullable ColorFilter colorFilter) {}

    @Override
    public int getOpacity() {
        return PixelFormat.UNKNOWN;
    }
}
