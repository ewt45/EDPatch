package com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.toucharea;

import static com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.Const.BtnColorStyle.FILL;
import static com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.Const.BtnColorStyle.STROKE;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.Const;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.TestHelper;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.TouchAdapter;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.TouchArea;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.TouchAreaView;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.model.OneColumn;

/**
 * 一列按钮，高度顶满，暂不支持滚动
 */
public class TouchAreaColumn extends TouchArea<OneColumn> {
    GradientDrawable mDrawable = new GradientDrawable();
    Paint mTextPaint = new Paint();

    public TouchAreaColumn(TouchAreaView host, @NonNull OneColumn data, @NonNull TouchAdapter adapter) {
        super(host, data, adapter);
        mDrawable.setCornerRadius(10);

    }

    private void updatePaint(){
        //        mPaint.setStyle(mData.shape== Const.BtnShape.RECT? Paint.Style.FILL_AND_STROKE: Paint.Style.STROKE);
//        mPaint.setColor(mData.bgColor);
        int tmpStyle = mModel.colorStyle;
        if (mModel.isPressed())
            tmpStyle = tmpStyle == STROKE ? FILL : STROKE; //如果按下了，颜色反转

        mDrawable.setStroke(tmpStyle == STROKE ? 4 : 0, mModel.mainColor);
        mDrawable.setColor(tmpStyle == STROKE ? 0x00000000 : mModel.mainColor);
        // 高度顶满
        mDrawable.setBounds(mModel.getLeft(), 0, mModel.getLeft() + mModel.getWidth(), mHost.getHeight());


        //文字大小怎么搞。。要不就整button的area列表？
        float currTxtWidthIfDraw = mTextPaint.measureText(mModel.getName());//以当前的文字大小，绘制全部文本需要多少宽度
        float finalTxtSize = mTextPaint.getTextSize() * mModel.getWidth() / currTxtWidthIfDraw;//再根据当前实际宽度，缩放文字大小
        mTextPaint.setTextSize(finalTxtSize);
        mTextPaint.setColor(tmpStyle == STROKE ? mModel.mainColor : TestHelper.getContrastColor(mModel.mainColor));
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setStyle(Paint.Style.FILL);
//        mTextPaint.setXfermode(mData.colorStyle == STROKE?null:mDSTOVERMode ); //设置颜色混合

        Log.d(TAG, "updatePaint: 文字大小=" + finalTxtSize + ", w=" + mModel.getWidth());
    }

    @Override
    public void onDraw(Canvas canvas) {
        updatePaint();

    }
}
