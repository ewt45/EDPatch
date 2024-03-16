package com.example.datainsert.exagear.controlsV2.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.TypedValue;
import android.view.View;

import com.example.datainsert.exagear.controlsV2.TestHelper;
import com.example.datainsert.exagear.RR;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 用于编辑手势时，实时显示状态转移
 */
public class TransitionHistoryView extends View {
    private static final String TAG = "TransitionHistoryView";
    private final float textHeight; //文字大小（高度）
    private final float smallerTextScale = 3/4f; //在箭头上下显示事件和操作，稍微缩小一点
    private final float lineHeight; //一行文字大小
    private final float linePadding = 4; //行间距
    private final Paint mTextPaint = new Paint();
    private final Paint mLinePaint = new Paint();
    private final List<List<String>> mHistoryList = new ArrayList<>();
    public TransitionHistoryView(Context c) {
        super(c);
        setFocusable(false);
        setFocusableInTouchMode(false);
        setWillNotDraw(false);
        setBackgroundColor(RR.attr.textColorPrimaryInverse(c));

        textHeight = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,14,c.getResources().getDisplayMetrics());
        lineHeight = Math.max(textHeight,textHeight*smallerTextScale*2);

        mTextPaint.setTextSize(textHeight);
        mTextPaint.setColor(RR.attr.textColorPrimary(c));
        mTextPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mTextPaint.setTextAlign(Paint.Align.LEFT);

        mLinePaint.setStrokeWidth(2);
        mLinePaint.setColor(RR.attr.textColorPrimary(c));
//        mLinePaint.setColor(ColorUtils.blendARGB(RR.attr.textColorPrimary(c),RR.attr.textColorPrimaryInverse(c),0.5f));

//        mHistoryList.add(Arrays.asList("中文","English","111","fsfsdf"));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        while(mHistoryList.size()*(lineHeight+linePadding) > getHeight())
            mHistoryList.remove(0);

        long startTime = System.currentTimeMillis();

        for(int i=0; i<mHistoryList.size() ; i++){
            List<String> tran = mHistoryList.get(i);
//            int centerX =  getBounds().left+getBounds().width()/2;
//            int textCenterY = getBounds().top+getBounds().height()/2;
            float topY = (lineHeight+linePadding)*i;
            float textCenterY =  TestHelper.adjustTextPaintCenter(topY+lineHeight/2f,mTextPaint);

            String part1String = tran.get(0)+"  ";
            float part1Width = mTextPaint.measureText(part1String);
            float eventWidth = mTextPaint.measureText(tran.get(1))*smallerTextScale;
            float actionsWidth = mTextPaint.measureText(tran.get(3))*smallerTextScale;
            String part3String = ">  "+tran.get(2);

            float arrowWidth = Math.max(eventWidth,actionsWidth);

            //前状态
            canvas.drawText(part1String, 0, textCenterY, mTextPaint);

            //箭头的线
            canvas.drawLine(part1Width,topY+lineHeight/2f,part1Width+arrowWidth,topY+lineHeight/2f,mLinePaint);

            //线上面，事件
            float eventLeftWidth = part1Width+(arrowWidth-eventWidth)/2f;
            canvas.save();
            canvas.scale(smallerTextScale,smallerTextScale,eventLeftWidth, TestHelper.adjustTextPaintCenter((topY+textCenterY)/2f,mTextPaint));
            canvas.drawText(tran.get(1),eventLeftWidth,(topY+textCenterY)/2f,mTextPaint);
            canvas.restore();

            //线下面，操作
            float actionsLeftWidth = part1Width+(arrowWidth-actionsWidth)/2f;
            canvas.save();
            canvas.scale(smallerTextScale,smallerTextScale,actionsLeftWidth,TestHelper.adjustTextPaintCenter((topY+lineHeight+textCenterY)/2f,mTextPaint));
            canvas.drawText(tran.get(3),actionsLeftWidth,(topY+lineHeight+textCenterY)/2f,mTextPaint);
            canvas.restore();

            //后事件
            canvas.drawText(part3String, part1Width+arrowWidth, textCenterY, mTextPaint);

        }
    }

    /**
     * 添加一条记录
     */
    public void addHistory(List<String> tran){
        mHistoryList.add(tran);
//        if(mHistoryList.size()%5==0)
//            mHistoryList.add(Arrays.asList("中文","English","111","fsfsdf"));
        postInvalidate();
    }

    /**
     * 清空全部记录
     */
    public void clearHistory() {
        mHistoryList.clear();
    }
}
