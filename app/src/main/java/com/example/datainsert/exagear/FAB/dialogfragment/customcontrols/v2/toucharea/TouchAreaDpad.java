package com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.toucharea;

import static com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.Const.dp8;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;

import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.TestHelper;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.TouchAdapter;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.TouchAreaView;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.model.OneDpad;

public class TouchAreaDpad extends TouchAreaStick {
    GradientDrawable mRectDraw = new GradientDrawable();
    Paint mCenterDraw = new Paint();
    Paint mDirectionDraw  = new Paint();
    public TouchAreaDpad(TouchAreaView host, @NonNull OneDpad data, @NonNull TouchAdapter adapter) {
        super(host, data, adapter);
    }

    private void updatePaint() {
//        mPaint.setStyle(mData.shape== Const.BtnShape.RECT? Paint.Style.FILL_AND_STROKE: Paint.Style.STROKE);
//        mPaint.setColor(mData.bgColor);
        int tmpStyle = mModel.colorStyle;
//        if (mModel.isPressed())
//            tmpStyle = tmpStyle == STROKE ? FILL : STROKE; //如果按下了，颜色反转

        int darkenColor = TestHelper.darkenColor(mModel.mainColor);
        int strokeWidth = dp8/2;
        int barWidth = mModel.getSize()/3; //一个横条或者竖条的宽度
        mRectDraw.setCornerRadius(4);
        mRectDraw.setStroke(strokeWidth, darkenColor);
        mRectDraw.setColor(mModel.mainColor);
        //设置成横条，然后绘制的时候旋转90度在绘制个竖条
        mRectDraw.setBounds(mModel.getLeft(), mModel.getTop()+barWidth, mModel.getLeft() + mModel.getSize(), mModel.getTop() + mModel.getSize()*2/3);

        mCenterDraw.setStyle(Paint.Style.FILL);
        mCenterDraw.setColor(darkenColor);
        mCenterDraw.setStrokeWidth(strokeWidth);


        mDirectionDraw.setStrokeWidth(barWidth/4f);
        mDirectionDraw.setColor(darkenColor);
    }

    @Override
    protected void updateRealCenterXY(boolean isTouching) {
        realCenterX = mModel.getLeft()+mModel.getSize()/2f;
        realCenterY = mModel.getTop() + mModel.getSize()/2f;
    }

    @Override
    public void onDraw(Canvas canvas) {
        //TODO 修改颜色，按下状态时，改为函数调用，同时修改paint吧
        updatePressPos();
        updatePaint();

        //绘制横条
        mRectDraw.draw(canvas);
        //绘制竖条
        canvas.save();
        canvas.rotate(90,realCenterX,realCenterY);//默认旋转点应该是左上角
        mRectDraw.draw(canvas);
        canvas.restore();
        //中心
        float cellWidth = mModel.getSize()/3f;
        float halfWidth = mModel.getSize()/6f;
        canvas.drawRect(realCenterX-halfWidth,realCenterY-halfWidth,realCenterX+halfWidth,realCenterY+halfWidth, mCenterDraw);
        //方向指示
        if ((nowFingerAt & FINGER_AT_CENTER) !=0)
            return;

        if((nowFingerAt & FINGER_AT_LEFT)!=0)
            canvas.drawPoint(realCenterX-cellWidth,realCenterY, mDirectionDraw);
        else if((nowFingerAt & FINGER_AT_RIGHT)!=0)
        canvas.drawPoint(realCenterX+cellWidth,realCenterY, mDirectionDraw);

        if((nowFingerAt & FINGER_AT_TOP)!=0)
            canvas.drawPoint(realCenterX,realCenterY-cellWidth, mDirectionDraw);
        else if((nowFingerAt & FINGER_AT_BOTTOM)!=0)
            canvas.drawPoint(realCenterX,realCenterY+cellWidth, mDirectionDraw);

//        canvas.drawLine(realCenterX-halfWidth,realCenterY+halfWidth*2,realCenterX+halfWidth,realCenterY+halfWidth*2,mDirectionDraw);

//        canvas.restoreToCount(sc);
    }
}
