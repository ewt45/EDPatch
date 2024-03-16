package com.example.datainsert.exagear.controlsV2.widget;

import static com.example.datainsert.exagear.controlsV2.Const.dp8;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.datainsert.exagear.QH;
import com.example.datainsert.exagear.RR;

public class DrawableNumber extends Drawable {
    private final int TEXT_WIDTH = 2;
    private final int PADDING_BETWEEN_TEXT_AND_CIRCLE = dp8/2;
    private final Paint mPaint = new Paint();
    private final Paint mTextPaint = new Paint();
    private  int textSize; //不知道这个大小测量方式是什么，但是设置成和半径一样正好，还有点边距
    private int radius;
    
    public DrawableNumber(Context c){
        textSize = QH.px(c,14);
        int color = RR.attr.colorControlNormal(c);

        mPaint.setStrokeWidth(TEXT_WIDTH*2); //是因为STROKE和FILL_AND_STROKE的缘故吗，这个二倍的宽度和文字宽度才差不多
        mPaint.setColor(color);
        mPaint.setStyle(Paint.Style.STROKE);

        mTextPaint.setStrokeWidth(TEXT_WIDTH);
        mTextPaint.setColor(color);
        mTextPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setTextSize(textSize);

//        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,14,Resources.getSystem().getDisplayMetrics())
    }

    @Override
    public void setBounds(int left, int top, int right, int bottom) {
        super.setBounds(left, top, right, bottom);
        radius =
//                textSize;
//                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,14,Resources.getSystem().getDisplayMetrics());
                Math.min(getBounds().width(),getBounds().height())/2 - TEXT_WIDTH;
        textSize = radius;
        mTextPaint.setTextSize(textSize);
    }

    @Override
    public void draw(@NonNull Canvas canvas) {

        int centerX =  getBounds().left+getBounds().width()/2;
        int centerY = getBounds().top+getBounds().height()/2;

        if(getLevel()!=0){
            canvas.drawCircle(centerX, centerY, radius,mPaint);
            canvas.drawText(String.valueOf(getLevel()),
                    centerX,
                    centerY - (mTextPaint.ascent() + mTextPaint.descent()) / 2f,
                    mTextPaint);
        }
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
