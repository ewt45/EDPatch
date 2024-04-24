package com.example.datainsert.exagear.controlsV2.widget;

import static com.example.datainsert.exagear.controlsV2.Const.dp8;
import static com.example.datainsert.exagear.controlsV2.Const.fingerTapMaxMs;
import static com.example.datainsert.exagear.controlsV2.TestHelper.getContrastColor;
import static com.example.datainsert.exagear.controlsV2.TestHelper.setColorAlpha;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Gravity;

import com.example.datainsert.exagear.QH;
import com.example.datainsert.exagear.RR;
import com.example.datainsert.exagear.controlsV2.TestHelper;

/**
 * 显示一个圆形背景为填充色，文字为序号（镂空）的drawable。
 * <br/> 将需要显示的序号数字通过setLevel传入。若传入 {@link #LEVEL_NONE} 则不绘制任何内容
 * <br/> 可通过setTint设置颜色。
 *
 * <br/> 一些数值更新：
 * 半径：设置最大半径和setBounds时重新计算。
 * 文字大小：半径更新后重新计算。
 *
 *
 */
public class DrawableNumber extends Drawable {
    /** 当setLevel传入此数值时，draw不绘制任何内容 */
    public final int LEVEL_NONE = 0;
    public static final int GRAVITY_CENTER = 0;
    public static final int GRAVITY_RIGHT_BOTTOM = 1;
    @IntDef({GRAVITY_CENTER, GRAVITY_RIGHT_BOTTOM})
    public @interface GravityInt{

    }

    private final int TEXT_WIDTH = 4;
    private final int STROKE_WIDTH = TEXT_WIDTH/2;
    private final int PADDING_BETWEEN_TEXT_AND_CIRCLE = dp8/2;
    private final int textColorPrimaryInverse;
    private final Paint mPaint = new Paint();
    private final Paint mTextPaint = new Paint();
    private  int textSize; //不知道这个大小测量方式是什么，但是设置成和半径一样正好，还有点边距
    private int radius;
    @GravityInt private int gravity = GRAVITY_CENTER;
    private Bitmap bitmap; //用于存储镂空的图像。宽高等于圆直径，不包含左和上的大小。绘制时注意区分
    private int maxRadius;// 允许的最大半径。小于等于0时认作无限制


    public DrawableNumber(Context c, @GravityInt int gravity){
        this.gravity = gravity;
        textColorPrimaryInverse = RR.attr.textColorPrimaryInverse(c);

        //设置两个paint的颜色
        setTint(RR.attr.colorControlNormal(c));

        mPaint.setStrokeWidth(TEXT_WIDTH); //是因为STROKE和FILL_AND_STROKE的缘故吗，这个二倍的宽度和文字宽度才差不多
        mPaint.setStyle(Paint.Style.FILL);

        mTextPaint.setStrokeWidth(STROKE_WIDTH);
        mTextPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
//        mTextPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));//将文字部分镂空
//        textSize = QH.px(c,14);
//        mTextPaint.setTextSize(textSize);

//        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,14,Resources.getSystem().getDisplayMetrics())
    }
    
    public DrawableNumber(Context c){
        this(c, GRAVITY_CENTER);
    }

    public void setGravity(int gravity) {
        this.gravity = gravity;
    }

    /**
     * 设置允许的最大宽高。小于等于0认作是无限制
     */
    public void setMaxRadius(int maxRadius) {
        this.maxRadius = maxRadius;
        onRadiusMayChanged();
    }

    @Override
    protected boolean onLevelChange(int level) {
        drawOntoBitmap();
        return true;
    }

    @Override
    public void setBounds(int left, int top, int right, int bottom) {
        super.setBounds(left, top, right, bottom);
        onRadiusMayChanged();
    }

    /**
     * 当修改最大半径或setBounds时，半径可能会改变，需要重新绘制图像。所以需要调用此函数。
     */
    private void onRadiusMayChanged(){
        Rect rect = getBounds();
        int oldRadius = radius;
        radius =
                Math.min(rect.width(),rect.height())/2;
//                textSize;
//                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,14,Resources.getSystem().getDisplayMetrics());
//                Math.min(rect.width(),rect.height())/2 - STROKE_WIDTH;
        if(maxRadius > 0)
            radius = Math.min(radius, maxRadius);

        textSize = radius;
        mTextPaint.setTextSize(textSize * 2f *2/3);

        if(oldRadius != radius){
            bitmap = Bitmap.createBitmap(radius*2, radius*2, Bitmap.Config.ARGB_8888);
            drawOntoBitmap();
        }
    }


    @Override
    public void setTintList(@Nullable ColorStateList tint) {
        super.setTintList(tint);
        if(tint!=null){
            mPaint.setColor(tint.getDefaultColor());
//            mTextPaint.setColor(setColorAlpha(getContrastColor(mPaint.getColor()), 0x40));
            mTextPaint.setColor(textColorPrimaryInverse);
            drawOntoBitmap();
        }
    }

    /**
     * 每当属性发生变化时，调用此函数重新绘制图像到bitmap上。以便draw时直接绘制bitmap到canvas，而不用每次draw都新建bitmap
     * <br/> 函数内部会清除原先bitmap的所有像素以防重叠
     */
    private void drawOntoBitmap(){
        if(getLevel() == 0 || bitmap == null)
            return;

        bitmap.eraseColor(Color.TRANSPARENT); //如果是setLevel，bitmap不新建，那么就会在原bitmap再画一层，所以需要先清除原先的图像
        Canvas canvas = new Canvas(bitmap);
        // bitmap只有直径宽高。往
        int centerX = radius;
        int centerY = radius;

        canvas.drawCircle(centerX, centerY, radius,mPaint);
        canvas.drawText(String.valueOf(getLevel()),
                centerX,
                centerY - (mTextPaint.ascent() + mTextPaint.descent()) / 2f,
                mTextPaint);
    }


    @Override
    public void draw(@NonNull Canvas canvas) {
        if(getLevel() == 0 || bitmap == null)
            return;

//        if(gravity == GRAVITY_CENTER){
//            centerX = rect.left + rect.width()/2;
//            centerY = rect.top + rect.height()/2;
//        }else if(gravity == GRAVITY_RIGHT_BOTTOM) {
//            centerX = rect.right - radius;
//            centerY = rect.bottom - radius;
//        }else throw new RuntimeException("非法gravity");

        //bitmap只有圆的直径那么大，需要调整左和上的偏移
        int left, top;
        Rect rect = getBounds();
        if(gravity == GRAVITY_CENTER){
            left = (rect.left + rect.right)/2 - radius;
            top = (rect.top + rect.bottom)/2 -radius;
        }else if(gravity == GRAVITY_RIGHT_BOTTOM) {
            left = rect.right - radius*2;
            top = rect.bottom - radius*2;
        }else throw new RuntimeException("非法gravity");

        canvas.save();
        canvas.drawBitmap(bitmap, left,top, null);
        canvas.restore();
    }


    @Override
    public void setAlpha(int alpha) {
//        mPaint.setAlpha(alpha);
//        mTextPaint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {

    }

    @Override
    public int getOpacity() {
        return PixelFormat.UNKNOWN;
    }
}
