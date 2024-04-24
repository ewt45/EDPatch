package com.example.datainsert.exagear.controlsV2.touchArea;

import static com.example.datainsert.exagear.controlsV2.Const.BtnColorStyle.FILL;
import static com.example.datainsert.exagear.controlsV2.Const.BtnColorStyle.STROKE;
import static com.example.datainsert.exagear.controlsV2.Const.TOUCH_AREA_ROUND_CORNER_RADIUS;
import static com.example.datainsert.exagear.controlsV2.Const.TOUCH_AREA_STROKE_WIDTH;
import static com.example.datainsert.exagear.controlsV2.TestHelper.adjustTextPaintCenterY;
import static com.example.datainsert.exagear.controlsV2.touchArea.TouchAreaButton.setPaintTextSize;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.datainsert.exagear.controlsV2.Finger;
import com.example.datainsert.exagear.controlsV2.TouchAdapter;
import com.example.datainsert.exagear.controlsV2.TouchArea;
import com.example.datainsert.exagear.controlsV2.model.OneColumn;
import com.example.datainsert.exagear.controlsV2.touchAdapter.ColumnPressAdapter;

/**
 * 一列按钮，高度顶满，暂不支持滚动
 */
public class TouchAreaColumn extends TouchArea<OneColumn> {
    GradientDrawable mDrawable = new GradientDrawable();
    Paint mTextPaint = new Paint();

    Paint mLinePaint = new Paint();
    /** 绘制时外部轮廓的边界 */
    Rect mBounds = new Rect();
    public TouchAreaColumn(@NonNull OneColumn data) {
        this(data, new ColumnPressAdapter(data));
    }
    /**
     * @param adapter 若为null，则处于非编辑模式。自己构建运行时adapter
     */
    public TouchAreaColumn(@NonNull OneColumn data, @Nullable TouchAdapter adapter) {
        super(data, adapter!=null?adapter:new ColumnPressAdapter(data));
        updatePaint();
    }

    /**
     * 需要重写此方法，因为getWidthHeight只能获取一个按钮的宽高
     */
    @Override
    protected boolean isInside(Finger finger) {
        mModel.getBounds(mBounds);
        return finger.getX() > mBounds.left && finger.getX() < mBounds.right
                && finger.getY() > mBounds.top && finger.getY() < mBounds.bottom;
    }

    private void updatePaint(){
        //        mPaint.setStyle(mData.shape== Const.BtnShape.RECT? Paint.Style.FILL_AND_STROKE: Paint.Style.STROKE);
//        mPaint.setColor(mData.bgColor);

        mLinePaint.setColor(mModel.mainColor);
        mLinePaint.setStyle(Paint.Style.STROKE);
        mLinePaint.setStrokeWidth(TOUCH_AREA_STROKE_WIDTH);


        int tmpStyle = mModel.colorStyle;
        if (mModel.isPressed())
            tmpStyle = tmpStyle == STROKE ? FILL : STROKE; //如果按下了，颜色反转

//        mDrawable.setBounds(mModel.getLeft(), 0, mModel.getLeft() + width, mModel.getTop()+height);
        mDrawable.setStroke(TOUCH_AREA_STROKE_WIDTH, mModel.mainColor);
        mDrawable.setCornerRadius(TOUCH_AREA_ROUND_CORNER_RADIUS);

//        mDrawable.setStroke(tmpStyle == STROKE ? 4 : 0, mModel.mainColor);
//        mDrawable.setColor(tmpStyle == STROKE ? 0x00000000 : mModel.mainColor);


        //文字大小怎么搞。。要不就整button的area列表？
//        float currTxtWidthIfDraw = mTextPaint.measureText(mModel.getName());//以当前的文字大小，绘制全部文本需要多少宽度
//        float finalTxtSize = mTextPaint.getTextSize() * mModel.getWidth() / currTxtWidthIfDraw;//再根据当前实际宽度，缩放文字大小
//        mTextPaint.setTextSize(finalTxtSize);
//        mTextPaint.setColor(tmpStyle == STROKE ? mModel.mainColor : TestHelper.getContrastColor(mModel.mainColor));
        mTextPaint.setColor(mModel.mainColor);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setStyle(Paint.Style.FILL);
//        mTextPaint.setXfermode(mData.colorStyle == STROKE?null:mDSTOVERMode ); //设置颜色混合

//        Log.d(TAG, "updatePaint: 文字大小=" + finalTxtSize + ", w=" + mModel.getWidth());
    }

    @Override
    public void onDraw(Canvas canvas) {
        updatePaint();
        boolean isV = mModel.isVertical();
        canvas.save();

        mModel.getBounds(mBounds);

        ColumnPressAdapter runtimeAdapter = getAdapter() instanceof ColumnPressAdapter
                ? (ColumnPressAdapter) getAdapter() : null;

        canvas.save();
        //绘制文字和分割线时，用clipRect让出轮廓描边的宽度
        canvas.clipRect(mBounds.left+TOUCH_AREA_STROKE_WIDTH, mBounds.top+TOUCH_AREA_STROKE_WIDTH, mBounds.right-TOUCH_AREA_STROKE_WIDTH, mBounds.bottom-TOUCH_AREA_STROKE_WIDTH);

        //应用当前滚动后的偏移 (先裁切再滚动，保证轮廓不跟着滚动）
        float scrollOffset = runtimeAdapter != null ? runtimeAdapter.getScrollOffset() : 0;
        float canvasTranslateX = isV ? 0 : scrollOffset, canvasTranslateY = isV ? scrollOffset : 0;
        canvas.translate(canvasTranslateX, canvasTranslateY);

        //perpendicular  parallel
        //在排列方向上的那些边，命名为start/end_alongAxis, 需要根据循环不停改变 . 如果垂直，则为两个y值，水平则为两个x轴的值
        // 与排列方向垂直的那些边，命名为 start/end_offAxis，只需计算一次

        float start_offAxis = isV ? mBounds.left : mBounds.top;
        float end_offAxis = isV ? mBounds.right : mBounds.bottom;
        float center_offAxis = (start_offAxis + end_offAxis) / 2;

        //要全部画出来然后裁掉吗？还是只画可见的部分
        float centerX = mBounds.centerX();
        for(int i=0; i<mModel.getKeycodes().size(); i++) {
            //当前按钮所对应的沿轴向两个边界
            float start_alongAxis = isV ? (mBounds.top + i*mModel.getHeight()) : (mBounds.left + i*mModel.getWidth());
            float end_alongAxis = isV ? (mBounds.top + (i+1)*mModel.getHeight()) : (mBounds.left + (i+1)*mModel.getWidth());
            float center_alongAxis = (start_alongAxis + end_alongAxis) / 2;
            //绘制文字
            String text = mModel.getNameAt(i);
            setPaintTextSize(text, mTextPaint, mModel.getWidth() - 2*TOUCH_AREA_STROKE_WIDTH);
            float textCenterX = isV ? center_offAxis : center_alongAxis;
            float textCenterY = adjustTextPaintCenterY(isV ? center_alongAxis : center_offAxis, mTextPaint);
            canvas.drawText(mModel.getNameAt(i), textCenterX, textCenterY, mTextPaint);
            //绘制底部分割线
            float endLineStartX = isV ? start_offAxis : end_alongAxis;
            float endLineStartY = isV ? end_alongAxis : start_offAxis;
            float endLineStopX = isV ? end_offAxis : end_alongAxis;
            float endLineStopY = isV ? end_alongAxis : end_offAxis;
            canvas.drawLine(endLineStartX, endLineStartY, endLineStopX, endLineStopY, mLinePaint);
        }

        canvas.restore();


        //绘制轮廓
        mDrawable.setBounds(mBounds.left, mBounds.top, mBounds.right, mBounds.bottom);
        mDrawable.draw(canvas);

        canvas.restore();

    }
}
