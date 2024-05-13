package com.example.datainsert.exagear.controlsV2.touchArea;

import static com.example.datainsert.exagear.controlsV2.Const.BtnColorStyle.FILL;
import static com.example.datainsert.exagear.controlsV2.Const.BtnColorStyle.STROKE;
import static com.example.datainsert.exagear.controlsV2.Const.TOUCH_AREA_ROUND_CORNER_RADIUS;
import static com.example.datainsert.exagear.controlsV2.Const.TOUCH_AREA_STROKE_WIDTH;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextPaint;

import com.example.datainsert.exagear.controlsV2.Const;
import com.example.datainsert.exagear.controlsV2.TestHelper;
import com.example.datainsert.exagear.controlsV2.TouchAdapter;
import com.example.datainsert.exagear.controlsV2.TouchArea;
import com.example.datainsert.exagear.controlsV2.touchAdapter.ButtonPressAdapter;
import com.example.datainsert.exagear.controlsV2.model.OneButton;

public class TouchAreaButton extends TouchArea<OneButton> {
    TextPaint mTextPaint = new TextPaint(); //支持换行需要StaticLayout或Dynamic，但是new了之后不让改Paint，真麻烦
    GradientDrawable mDrawable = new GradientDrawable();
//    PorterDuffXfermode mDSTOVERMode = new PorterDuffXfermode(PorterDuff.Mode.CLEAR);
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

        mDrawable.setCornerRadius(mModel.getShape() == Const.BtnShape.RECT ? TOUCH_AREA_ROUND_CORNER_RADIUS : 400);
        mDrawable.setStroke(tmpStyle == STROKE ? TOUCH_AREA_STROKE_WIDTH : 0, mModel.mainColor);
        mDrawable.setColor(tmpStyle == STROKE ? 0x00000000 : mModel.mainColor);
        mDrawable.setBounds(mModel.getLeft(), mModel.getTop(), mModel.getLeft() + mModel.getWidth(), mModel.getTop() + mModel.getHeight());

        //仅当文字变化时才重新计算文字大小
        if(!lastText.equals(mModel.getName()) || lastWH[0]!=mModel.getWidth() || lastWH[1]!=mModel.getHeight()){
            renderText = lastText = mModel.getName();
            lastWH = new int[]{mModel.getWidth(), mModel.getHeight()};
            //TODO 文字大小限制：应该去宽度和高度较小的那一方用来做为限制。 大小应该有个上限，比如1/3宽度，还有下限
            setPaintTextSize(renderText, mTextPaint, mModel.getWidth() - TOUCH_AREA_STROKE_WIDTH);


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
        }

        mTextPaint.setColor(tmpStyle == STROKE ? mModel.mainColor : TestHelper.getContrastColor(mModel.mainColor));
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setStyle(Paint.Style.FILL);
//        mTextPaint.setXfermode(mData.colorStyle == STROKE?null:mDSTOVERMode ); //设置颜色混合

//        Log.d(TAG, "updatePaint: 文字大小=" + finalTxtSize + ", w=" + mModel.getWidth());
    }

    /**
     * 计算该段文字的合适大小，限制在 {@link Const#TOUCH_AREA_MIN_TEXT_SIZE} 到 {@link Const#TOUCH_AREA_MAX_TEXT_SIZE} 之间
     * 并设置到给定paint上
     * @param str 文字
     * @param paint 用于绘制文字的paint
     * @param maxTotalWidth 整体文字最多可以多宽
     */
    static void setPaintTextSize(String str, Paint paint, int maxTotalWidth){
        float size = paint.measureText(str);//以当前的文字大小，绘制全部文本需要多少宽度
        size = paint.getTextSize() * maxTotalWidth / size;
        size = Math.min(size,Const.TOUCH_AREA_MAX_TEXT_SIZE);
        size = Math.max(Const.TOUCH_AREA_MIN_TEXT_SIZE,size);
        paint.setTextSize(size);
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
        canvas.clipRect(mModel.getLeft()+TOUCH_AREA_STROKE_WIDTH ,mModel.getTop(),mModel.getLeft()+mModel.getWidth()-TOUCH_AREA_STROKE_WIDTH,mModel.getTop()+mModel.getHeight());
//        canvas.scale(0.8f, 0.8f, centerX, centerY); //这个是把画出来的东西缩放，而不是把画布缩放
        canvas.drawText(renderText,
                centerX,
                TestHelper.adjustTextPaintCenterY(centerY, mTextPaint),
                mTextPaint);
        canvas.restore();

//        canvas.restoreToCount(sc);
    }


}
