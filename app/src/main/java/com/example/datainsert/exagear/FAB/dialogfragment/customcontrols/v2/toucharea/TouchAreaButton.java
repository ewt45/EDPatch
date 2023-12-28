package com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.toucharea;

import static com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.Const.BtnColorStyle.STROKE;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.Const;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.TestHelper;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.TouchAdapter;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.TouchArea;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.TouchAreaView;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.adapter.EditMoveAdapter;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.model.OneButton;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.model.TouchAreaModel;

public class TouchAreaButton extends TouchArea<OneButton> {
    Paint mTextPaint= new Paint();
    GradientDrawable mDrawable = new GradientDrawable();
    PorterDuffXfermode mDSTOVERMode = new PorterDuffXfermode(PorterDuff.Mode.CLEAR);
    public TouchAreaButton(TouchAreaView host, @NonNull OneButton data, @NonNull TouchAdapter adapter) {
        super(host, data, adapter);
        updatePaint();
    }


    private void updatePaint() {
//        mPaint.setStyle(mData.shape== Const.BtnShape.RECT? Paint.Style.FILL_AND_STROKE: Paint.Style.STROKE);
//        mPaint.setColor(mData.bgColor);
        mDrawable.setCornerRadius(mModel.shape== Const.BtnShape.RECT?10:400);
        mDrawable.setStroke(mModel.colorStyle == STROKE?4:0, mModel.mainColor);
        mDrawable.setColor(mModel.colorStyle == STROKE? 0x00000000: mModel.mainColor);
        mDrawable.setBounds(mModel.getLeft(), mModel.getTop(), mModel.getLeft()+ mModel.getWidth(), mModel.getTop()+ mModel.getHeight());


        float  currTxtWidthIfDraw =mTextPaint.measureText(mModel.name);//以当前的文字大小，绘制全部文本需要多少宽度
        //TODO 文字大小限制：应该去宽度和高度较小的那一方用来做为限制。 大小应该有个上限，比如1/3宽度
        float finalTxtSize = mTextPaint.getTextSize()* mModel.getWidth()/currTxtWidthIfDraw;//再根据当前实际宽度，缩放文字大小
        mTextPaint.setTextSize(finalTxtSize);
        mTextPaint.setColor(mModel.colorStyle == STROKE? mModel.mainColor : TestHelper.getContrastColor(mModel.mainColor));
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setStyle(Paint.Style.FILL);
//        mTextPaint.setXfermode(mData.colorStyle == STROKE?null:mDSTOVERMode ); //设置颜色混合


    }

    @Override
    public void onDraw(Canvas canvas) {
        //TODO 修改颜色，按下状态时，改为函数调用，同时修改paint吧
        updatePaint();

//        int sc =canvas.saveLayer(mData.getLeft(),mData.getTop(),mData.getLeft()+mData.getWidth(),mData.getTop()+mData.getHeight(),null);

        if(!canvas.isHardwareAccelerated())
            Log.d(TAG, "onDraw: canvas没有硬件加速");
        mDrawable.draw(canvas);
        float centerX = mModel.getLeft()+ mModel.getWidth()/2f;
        float centerY = mModel.getTop()+ mModel.getHeight()/2f;
        canvas.save();
        canvas.scale(0.8f,0.8f,centerX,centerY); //这个是把画出来的东西缩放，而不是把画布缩放
        canvas.drawText(mModel.name,
                centerX,
                centerY-(mTextPaint.ascent()+mTextPaint.descent())/2f,
                mTextPaint);
        canvas.restore();

//        canvas.restoreToCount(sc);
    }



}
