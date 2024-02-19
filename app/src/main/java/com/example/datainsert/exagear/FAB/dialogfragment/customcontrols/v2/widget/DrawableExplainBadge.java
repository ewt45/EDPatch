package com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.widget;

import static com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.Const.dp8;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

/**
 * 尝试画一个圆圈，里面是个问号。放到标题的viewOverlay上，用于点击查看说明。
 * <br/> 记得手动设置边界(视图宽高）,会绘制到视图最右上角4x4dp
 * <br/> 不好。自己画的话，文字一缩放 粗细就变了，而外面圆圈的粗细又是固定的
 */
public class DrawableExplainBadge extends Drawable {
    private final int radius ;
    private final int CIRCLE_WIDTH = 4;
    private final int PADDING_BETWEEN_TEXT_AND_CIRCLE = dp8/2;
    private final int PADDING_BETWEEN_CIRCLE_AND_BOUNDARY = 0;
    private final Paint mPaint = new Paint();
    private final Paint mTextPaint = new Paint();
    private final View mHostView;
    public DrawableExplainBadge(View view){

//        Bitmap bitmap = Bitmap.createBitmap(dp4,dp4, Bitmap.Config.ARGB_8888);
//        Canvas canvas = new Canvas(bitmap);
        mHostView = view;
        radius = dp8;

        mPaint.setStrokeWidth(CIRCLE_WIDTH);
        mPaint.setColor(Color.GRAY);
        mPaint.setStyle(Paint.Style.STROKE);

        mTextPaint.setColor(Color.GRAY);
        float currTxtWidthIfDraw = mTextPaint.measureText("?");//以当前的文字大小，绘制全部文本需要多少宽度
        float finalTxtSize = mTextPaint.getTextSize() * radius / currTxtWidthIfDraw - CIRCLE_WIDTH - PADDING_BETWEEN_TEXT_AND_CIRCLE;//再根据当前实际宽度，缩放文字大小
        mTextPaint.setTextSize(finalTxtSize);

        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setStyle(Paint.Style.FILL);
    }
    public DrawableExplainBadge(){
        this(null);
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        int right = mHostView!=null? mHostView.getWidth():getBounds().right;
        int centerX = right - radius - CIRCLE_WIDTH /2;
        int centerY = radius + CIRCLE_WIDTH /2;

        canvas.drawCircle(centerX, centerY, radius,mPaint);
        canvas.drawText("?",
                centerX,
                centerY - (mTextPaint.ascent() + mTextPaint.descent()) / 2f,
                mTextPaint);
    }

    @Override
    public void setAlpha(int alpha) {

    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {

    }

    @Override
    public int getOpacity() {
        return PixelFormat.UNKNOWN;
    }
}
