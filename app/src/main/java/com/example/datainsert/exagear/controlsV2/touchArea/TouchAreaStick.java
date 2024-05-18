package com.example.datainsert.exagear.controlsV2.touchArea;

import static com.example.datainsert.exagear.controlsV2.Const.dp8;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.datainsert.exagear.controlsV2.Const;
import com.example.datainsert.exagear.controlsV2.TestHelper;
import com.example.datainsert.exagear.controlsV2.TouchAdapter;
import com.example.datainsert.exagear.controlsV2.TouchArea;
import com.example.datainsert.exagear.controlsV2.model.OneStick;
import com.example.datainsert.exagear.controlsV2.touchAdapter.ButtonStickPressAdapter;
import com.example.datainsert.exagear.controlsV2.widget.DrawableNoOverlapStroke;

/**
 * 设置描边宽度为外圆半径的十分之一
 */
public class TouchAreaStick extends TouchArea<OneStick> {
    protected DrawableNoOverlapStroke mRoundDraw = new DrawableNoOverlapStroke();

//    protected ButtonStickPressAdapter mStickAdapter;

    /**
     * @param adapter 若为null，则处于非编辑模式。自己构建运行时adapter
     */
    public TouchAreaStick(@NonNull OneStick data, @Nullable TouchAdapter adapter) {
        super(data, adapter != null ? adapter : new ButtonStickPressAdapter(data));
    }

    private void updatePaint() {
        mRoundDraw.setCornerRadius(400);
        mRoundDraw.setStroke((int) (mModel.getOuterRadius()/16), TestHelper.darkenColor(mModel.getMainColor()));//描边宽度根据大小而变化，为外圆半径的十分之一
        mRoundDraw.setColor(mModel.getMainColor());
    }

    @Override
    public void onDraw(Canvas canvas) {
        //TODO 修改颜色/按下状态时，改为函数调用，同时修改paint吧
        //如果是运行时，则应该处理手指位置，以便绘制按下位置，摇杆位置和发送按键事件
        ButtonStickPressAdapter runtimeAdapter = mAdapter instanceof ButtonStickPressAdapter
                ? (ButtonStickPressAdapter) mAdapter : null;

        if (runtimeAdapter != null)
            runtimeAdapter.updatePressPos();

        updatePaint();

        float outRadius = mModel.getOuterRadius();
        float innerCenterX,innerCenterY;
        float outerCenterX, outerCenterY;
        if (runtimeAdapter != null) {
            innerCenterX = runtimeAdapter.getInnerCenterX();
            innerCenterY = runtimeAdapter.getInnerCenterY();
            outerCenterX = runtimeAdapter.getOuterCenterX();
            outerCenterY = runtimeAdapter.getOuterCenterY();

        } else {
            outerCenterX = innerCenterX = mModel.getLeft() + mModel.getSize() / 2f;
            outerCenterY = innerCenterY = mModel.getTop() + mModel.getSize() / 2f;
        }


        //绘制外层，如果是按下状态（有关联手指），则绘制到手指初次按下时的位置，否则绘制到区域中心
//        mRoundDraw.setBounds(mModel.getLeft(),mModel.getTop(),mModel.getLeft()+mModel.getWidth(),mModel.getTop()+mModel.getHeight());
        mRoundDraw.setBounds((int) (outerCenterX - outRadius), (int) (outerCenterY - outRadius), (int) (outerCenterX + outRadius), (int) (outerCenterY + outRadius));
        mRoundDraw.draw(canvas);


        //绘制内层，如果是按下状态且有关联手指，则绘制到手指方向的位置
        canvas.save();
        canvas.scale(Const.stickInnerOuterRatio, Const.stickInnerOuterRatio, innerCenterX, innerCenterY); //这个是把画出来的东西缩放，而不是把画布缩放
        canvas.translate(innerCenterX-outerCenterX,innerCenterY-outerCenterY); //缩放后，传入坐标比例还是按未缩放时的比例，不用自己除以2
        mRoundDraw.draw(canvas);
        canvas.restore();

//        canvas.restoreToCount(sc);
    }



}
