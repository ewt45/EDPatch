package com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.touchArea;

import static com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.Const.dp8;

import android.graphics.Canvas;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.TestHelper;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.TouchAdapter;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.TouchArea;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.touchAdapter.ButtonStickPressAdapter;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.model.OneStick;

public class TouchAreaStick extends TouchArea<OneStick> {
    protected GradientDrawable mRoundDraw = new GradientDrawable();
    protected ButtonStickPressAdapter mStickAdapter;

    public TouchAreaStick(@NonNull OneStick data){
        this(data,null);
    }
    /**
     * @param adapter 若为null，则处于非编辑模式。自己构建运行时adapter
     */
    public TouchAreaStick(@NonNull OneStick data, @Nullable TouchAdapter adapter) {
        super(data, getAdapter(data,adapter));
        mStickAdapter = (ButtonStickPressAdapter) mAdapter;
    }

    private static TouchAdapter getAdapter(@NonNull OneStick data,@Nullable TouchAdapter adapter){
        if(adapter instanceof ButtonStickPressAdapter)
            return adapter;

        return new ButtonStickPressAdapter(data,adapter);
    }

    private void updatePaint() {
        mRoundDraw.setCornerRadius(400);
        mRoundDraw.setStroke(dp8 / 2, TestHelper.darkenColor(mModel.mainColor));
        mRoundDraw.setColor(mModel.mainColor);

    }

    @Override
    public void onDraw(Canvas canvas) {
        //TODO 修改颜色，按下状态时，改为函数调用，同时修改paint吧
//        if(mAdapter instanceof ButtonStickPressAdapter)
//            ((ButtonStickPressAdapter)mAdapter).updatePressPos();
        mStickAdapter.updatePressPos();
        updatePaint();

        int outRadius = mModel.getSize() / 2;
        float startCenterX = mStickAdapter.getStartCenterX();
        float startCenterY = mStickAdapter.getStartCenterY();
        float innerCenterX = mStickAdapter.getInnerCenterX();
        float innerCenterY = mStickAdapter.getInnerCenterY();

        //绘制外层，如果是按下状态（有关联手指），则绘制到手指初次按下时的位置，否则绘制到区域中心
//        mRoundDraw.setBounds(mModel.getLeft(),mModel.getTop(),mModel.getLeft()+mModel.getWidth(),mModel.getTop()+mModel.getHeight());
        mRoundDraw.setBounds((int) (startCenterX - outRadius), (int) (startCenterY - outRadius), (int) (startCenterX + outRadius), (int) (startCenterY + outRadius));
        mRoundDraw.draw(canvas);

        //绘制内层，如果是按下状态且有关联手指，则绘制到手指方向的位置
        canvas.save();
        canvas.scale(0.5f, 0.5f, innerCenterX, innerCenterY); //这个是把画出来的东西缩放，而不是把画布缩放
        mRoundDraw.draw(canvas);
        canvas.restore();

//        canvas.restoreToCount(sc);
    }


}
