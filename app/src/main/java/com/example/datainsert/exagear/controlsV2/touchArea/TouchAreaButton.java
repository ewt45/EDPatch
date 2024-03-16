package com.example.datainsert.exagear.controlsV2.touchArea;

import static com.example.datainsert.exagear.controlsV2.Const.BtnColorStyle.FILL;
import static com.example.datainsert.exagear.controlsV2.Const.BtnColorStyle.STROKE;
import static com.example.datainsert.exagear.controlsV2.Const.dp8;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.DynamicLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.Log;

import com.example.datainsert.exagear.QH;
import com.example.datainsert.exagear.controlsV2.Const;
import com.example.datainsert.exagear.controlsV2.TestHelper;
import com.example.datainsert.exagear.controlsV2.TouchAdapter;
import com.example.datainsert.exagear.controlsV2.TouchArea;
import com.example.datainsert.exagear.controlsV2.touchAdapter.ButtonPressAdapter;
import com.example.datainsert.exagear.controlsV2.model.OneButton;

public class TouchAreaButton extends TouchArea<OneButton> {
    TextPaint mTextPaint = new TextPaint(); //支持换行需要StaticLayout或Dynamic，但是new了之后不让改Paint，真麻烦
    GradientDrawable mDrawable = new GradientDrawable();
    PorterDuffXfermode mDSTOVERMode = new PorterDuffXfermode(PorterDuff.Mode.CLEAR);
    private String lastText = ""; //用于在update文字大小时，与model当前文字对比，如果没变就不重新计算大小了。（每次刷新都计算的话，大小会变来变去）
    private String renderText = ""; //可能在lastText的基础上做一些调整
    private int[] lastWH = {0,0}; //草了宽高也要记录

    public TouchAreaButton(@NonNull OneButton data) {
        this(data, new ButtonPressAdapter(data.getKeycodes()));
    }
    /**
     * @param adapter 若为null，则处于非编辑模式。自己构建运行时adapter
     */
    public TouchAreaButton(@NonNull OneButton data, @Nullable TouchAdapter adapter) {
        super(data, adapter!=null?adapter:new ButtonPressAdapter(data.getKeycodes()));
        updatePaint();
    }


    //TODO 这三个area的绘制刷新数据方式能不能改一下，比如现在color改tint，setbound改canvas缩放，能提高效率吗
    // 或者每次都update，但是不是编辑模式的话，drawable的有些属性不用修改？
    private void updatePaint() {
//        mPaint.setStyle(mData.shape== Const.BtnShape.RECT? Paint.Style.FILL_AND_STROKE: Paint.Style.STROKE);
//        mPaint.setColor(mData.bgColor);
        int tmpStyle = mModel.colorStyle;
        if (mModel.isPressed())
            tmpStyle = tmpStyle == STROKE ? FILL : STROKE; //如果按下了，颜色反转

        mDrawable.setCornerRadius(mModel.shape == Const.BtnShape.RECT ? 10 : 400);
        mDrawable.setStroke(tmpStyle == STROKE ? 4 : 0, mModel.mainColor);
        mDrawable.setColor(tmpStyle == STROKE ? 0x00000000 : mModel.mainColor);
        mDrawable.setBounds(mModel.getLeft(), mModel.getTop(), mModel.getLeft() + mModel.getWidth(), mModel.getTop() + mModel.getHeight());

        //仅当文字变化时才重新计算文字大小
        if(!lastText.equals(mModel.getName()) || lastWH[0]!=mModel.getWidth() || lastWH[1]!=mModel.getHeight()){
            renderText = lastText = mModel.getName();
            lastWH = new int[]{mModel.getWidth(), mModel.getHeight()};
//            float currTxtWidthIfDraw = mTextPaint.measureText(lastText);//以当前的文字大小，绘制全部文本需要多少宽度
//            float firstAdjustTxtSize = mTextPaint.getTextSize() * mModel.getWidth() / currTxtWidthIfDraw;
            float min = dp8*5/4f, max = mModel.getWidth()*2/3f;
            float decidedAdjustSize = getTextSize(renderText);
            decidedAdjustSize = Math.min(decidedAdjustSize,max);
            decidedAdjustSize = Math.max(min,decidedAdjustSize);
//            if (decidedAdjustSize < min && renderText.length()>1) { //过小时，首先考虑分两行
//                if(renderText.contains(" "))
//                    renderText = renderText.replaceFirst(" ","\n");
//                else if(!renderText.contains("\n"))
//                    renderText = renderText.substring(0,renderText.length()/2)+"\n"+renderText.substring(renderText.length()/2);
//
//                String[] parts = renderText.split("\n",2);
//                decidedAdjustSize = Math.min(getTextSize(parts[0]),getTextSize(parts[1]));
//                decidedAdjustSize = Math.max(min,decidedAdjustSize);
//            }

            //TODO 文字大小限制：应该去宽度和高度较小的那一方用来做为限制。 大小应该有个上限，比如1/3宽度，还有下限
            mTextPaint.setTextSize(decidedAdjustSize);
        }
//        float tmpTextSize = getTextSize(mModel.getName());
//        float min = dp8*5/4f, max = mModel.getWidth()*2/3f;
//        tmpTextSize = Math.min(tmpTextSize,max);
//        tmpTextSize = Math.max(min,tmpTextSize);
//        mTextPaint.setTextSize(tmpTextSize);

        mTextPaint.setColor(tmpStyle == STROKE ? mModel.mainColor : TestHelper.getContrastColor(mModel.mainColor));
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setStyle(Paint.Style.FILL);
//        mTextPaint.setXfermode(mData.colorStyle == STROKE?null:mDSTOVERMode ); //设置颜色混合

//        Log.d(TAG, "updatePaint: 文字大小=" + finalTxtSize + ", w=" + mModel.getWidth());
    }

    private float getTextSize(String str){
        float currTxtWidthIfDraw = mTextPaint.measureText(str);//以当前的文字大小，绘制全部文本需要多少宽度
        return  mTextPaint.getTextSize() * mModel.getWidth() / currTxtWidthIfDraw;
    }

    @Override
    public void onDraw(Canvas canvas) {
        //TODO 修改颜色，按下状态时，改为函数调用，同时修改paint吧
        updatePaint();

//        int sc =canvas.saveLayer(mData.getLeft(),mData.getTop(),mData.getLeft()+mData.getWidth(),mData.getTop()+mData.getHeight(),null);

//        if ( !canvas.isHardwareAccelerated())
//            Log.d(TAG, "onDraw: canvas没有硬件加速");
        mDrawable.draw(canvas);
        float centerX = mModel.getLeft() + mModel.getWidth() / 2f;
        float centerY = mModel.getTop() + mModel.getHeight() / 2f;
        canvas.save();
        //把文字超出框外的部分剪切掉。注意这个clip要放在save restore之内，因为会影响到整个画布，否则会导致其他部分无法显示
        canvas.clipRect(mModel.getLeft(),mModel.getTop(),mModel.getLeft()+mModel.getWidth(),mModel.getTop()+mModel.getHeight());
        canvas.scale(0.8f, 0.8f, centerX, centerY); //这个是把画出来的东西缩放，而不是把画布缩放
        canvas.drawText(renderText,
                centerX,
                centerY - (mTextPaint.ascent() + mTextPaint.descent()) / 2f,
                mTextPaint);
        canvas.restore();

//        canvas.restoreToCount(sc);
    }


}
