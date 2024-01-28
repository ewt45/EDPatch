package com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.touchArea;

import static com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.Const.dp8;
import static com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.touchAdapter.ButtonStickPressAdapter.FINGER_AT_BOTTOM;
import static com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.touchAdapter.ButtonStickPressAdapter.FINGER_AT_CENTER;
import static com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.touchAdapter.ButtonStickPressAdapter.FINGER_AT_LEFT;
import static com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.touchAdapter.ButtonStickPressAdapter.FINGER_AT_RIGHT;
import static com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.touchAdapter.ButtonStickPressAdapter.FINGER_AT_TOP;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.TestHelper;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.TouchAdapter;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.touchAdapter.ButtonDpadPressAdapter;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.model.OneDpad;

public class TouchAreaDpad extends TouchAreaStick {
    GradientDrawable mRectDraw = new GradientDrawable();
    Paint mCenterDraw = new Paint();
    Paint mDirectionDraw = new Paint();

    public TouchAreaDpad(@NonNull OneDpad data) {
        this(data, null);
    }

    public TouchAreaDpad(@NonNull OneDpad data, @Nullable TouchAdapter adapter) {
        super(data, getAdapter(data, adapter));
    }

    private static TouchAdapter getAdapter(@NonNull OneDpad data, @Nullable TouchAdapter adapter) {
        if (adapter instanceof ButtonDpadPressAdapter)
            return adapter;

        return new ButtonDpadPressAdapter(data, adapter);
    }

    private void updatePaint() {
        int darkenColor = TestHelper.darkenColor(mModel.mainColor);
        int strokeWidth = dp8 / 2;
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
        mStickAdapter.updatePressPos();
        updatePaint();

        float startCenterX = mStickAdapter.getStartCenterX();
        float startCenterY = mStickAdapter.getStartCenterY();
        int nowFingerAt = mStickAdapter.nowFingerAt;

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
        if ((nowFingerAt & FINGER_AT_CENTER) != 0)
            return;

        if ((nowFingerAt & FINGER_AT_LEFT) != 0)
            canvas.drawPoint(startCenterX - cellWidth, startCenterY, mDirectionDraw);
        else if ((nowFingerAt & FINGER_AT_RIGHT) != 0)
            canvas.drawPoint(startCenterX + cellWidth, startCenterY, mDirectionDraw);

        if ((nowFingerAt & FINGER_AT_TOP) != 0)
            canvas.drawPoint(startCenterX, startCenterY - cellWidth, mDirectionDraw);
        else if ((nowFingerAt & FINGER_AT_BOTTOM) != 0)
            canvas.drawPoint(startCenterX, startCenterY + cellWidth, mDirectionDraw);

//        canvas.drawLine(realCenterX-halfWidth,realCenterY+halfWidth*2,realCenterX+halfWidth,realCenterY+halfWidth*2,mDirectionDraw);

//        canvas.restoreToCount(sc);
    }
}
