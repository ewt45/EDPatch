package com.example.datainsert.exagear.controlsV2.touchArea;

import static com.example.datainsert.exagear.controlsV2.Const.dp8;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.datainsert.exagear.controlsV2.TestHelper;
import com.example.datainsert.exagear.controlsV2.TouchAdapter;
import com.example.datainsert.exagear.controlsV2.model.OneDpad;
import com.example.datainsert.exagear.controlsV2.touchAdapter.ButtonDpadPressAdapter;
import com.example.datainsert.exagear.controlsV2.touchAdapter.ButtonStickPressAdapter;

public class TouchAreaDpad extends TouchAreaStick {
    GradientDrawable mRectDraw = new GradientDrawable();
    Paint mCenterDraw = new Paint();
    Paint mDirectionDraw = new Paint();

    public TouchAreaDpad(@NonNull OneDpad data) {
        this(data, null);
    }

    public TouchAreaDpad(@NonNull OneDpad data, @Nullable TouchAdapter adapter) {
        super(data, adapter != null ? adapter : new ButtonDpadPressAdapter(data));
    }

    private void updatePaint() {
        int darkenColor = TestHelper.darkenColor(mModel.mainColor);
        int strokeWidth = (int) (getModel().getOuterRadius()/10);
        int barWidth = mModel.getSize() / 3; //一个横条或者竖条的宽度
        mRectDraw.setCornerRadius(4);
        mRectDraw.setStroke(strokeWidth, darkenColor);
        mRectDraw.setColor(mModel.mainColor);
        //设置成横条，然后绘制的时候旋转90度在绘制个竖条
        mRectDraw.setBounds(mModel.getLeft(), mModel.getTop() + barWidth, mModel.getLeft() + mModel.getSize(), mModel.getTop() + mModel.getSize() * 2 / 3);

        mCenterDraw.setStyle(Paint.Style.FILL);
        mCenterDraw.setColor(darkenColor);
        mCenterDraw.setStrokeWidth(strokeWidth);


        mDirectionDraw.setStrokeWidth(barWidth / 4f);
        mDirectionDraw.setColor(darkenColor);
    }

    @Override
    public void onDraw(Canvas canvas) {
        //TODO 修改颜色，按下状态时，改为函数调用，同时修改paint吧
        ButtonDpadPressAdapter runtimeAdapter = mAdapter instanceof ButtonDpadPressAdapter
                ? (ButtonDpadPressAdapter) mAdapter : null;

        if (runtimeAdapter != null)
            runtimeAdapter.updatePressPos();

        updatePaint();

        float startCenterX = mModel.getLeft() + mModel.getSize() / 2f;
        float startCenterY = mModel.getTop() + mModel.getSize() / 2f;
        int nowFingerAt = runtimeAdapter != null ? runtimeAdapter.nowFingerAt : ButtonStickPressAdapter.FINGER_AT_CENTER;

        //绘制横条
        mRectDraw.draw(canvas);
        //绘制竖条
        canvas.save();
        canvas.rotate(90, startCenterX, startCenterY);//默认旋转点应该是左上角
        mRectDraw.draw(canvas);
        canvas.restore();
        //中心
        float cellWidth = mModel.getSize() / 3f;
        float halfWidth = mModel.getSize() / 6f;
        canvas.drawRect(startCenterX - halfWidth, startCenterY - halfWidth, startCenterX + halfWidth, startCenterY + halfWidth, mCenterDraw);
        //方向指示
        if ((nowFingerAt & ButtonStickPressAdapter.FINGER_AT_CENTER) != 0)
            return;

        if ((nowFingerAt & ButtonStickPressAdapter.FINGER_AT_LEFT) != 0)
            canvas.drawPoint(startCenterX - cellWidth, startCenterY, mDirectionDraw);
        else if ((nowFingerAt & ButtonStickPressAdapter.FINGER_AT_RIGHT) != 0)
            canvas.drawPoint(startCenterX + cellWidth, startCenterY, mDirectionDraw);

        if ((nowFingerAt & ButtonStickPressAdapter.FINGER_AT_TOP) != 0)
            canvas.drawPoint(startCenterX, startCenterY - cellWidth, mDirectionDraw);
        else if ((nowFingerAt & ButtonStickPressAdapter.FINGER_AT_BOTTOM) != 0)
            canvas.drawPoint(startCenterX, startCenterY + cellWidth, mDirectionDraw);

//        canvas.drawLine(realCenterX-halfWidth,realCenterY+halfWidth*2,realCenterX+halfWidth,realCenterY+halfWidth*2,mDirectionDraw);

//        canvas.restoreToCount(sc);
    }
}
