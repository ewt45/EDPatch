package com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.toucharea;

import static com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.Const.dp8;

import android.graphics.Canvas;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;

import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.Finger;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.TestHelper;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.TouchAdapter;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.TouchArea;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.TouchAreaView;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.model.OneStick;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class TouchAreaStick extends TouchArea<OneStick> {
    protected final static int FINGER_AT_LEFT = 1 << 1;
    protected final static int FINGER_AT_RIGHT = 1 << 2;
    protected final static int FINGER_AT_TOP = 1 << 3;
    protected final static int FINGER_AT_BOTTOM = 1 << 4;
    protected final static int FINGER_AT_CENTER = 0;
    private static final double tan_small = Math.tan(30);//Math.tan(Math.PI/8); //22.5度的x比y
    private static final double tan_big = Math.tan(60);//Math.tan(Math.PI*3/8);//67.5度的x比y
    //35 30 22.5
    private static final float tan35d = 0.70020753f;//0.57735026f;//0.414213562f;
    private static final float cot35d = 1.42814800f;//1.73205080f;//2.414213562f;
    protected float realCenterX = 0;
    protected float realCenterY = 0;
    protected float innerCenterX = 0;
    protected float innerCenterY = 0;
    protected GradientDrawable mRoundDraw = new GradientDrawable();
    /**
     * 当前手指在哪个方向上，默认为FINGER_AT_CENTER
     */
    @FingerAt
    protected int nowFingerAt = FINGER_AT_CENTER;

    public TouchAreaStick(TouchAreaView host, @NonNull OneStick data, @NonNull TouchAdapter adapter) {
        super(host, data, adapter);
    }

    private void updatePaint() {
//        mPaint.setStyle(mData.shape== Const.BtnShape.RECT? Paint.Style.FILL_AND_STROKE: Paint.Style.STROKE);
//        mPaint.setColor(mData.bgColor);
//        if (mModel.isPressed())
//            tmpStyle = tmpStyle == STROKE ? FILL : STROKE; //如果按下了，颜色反转

        mRoundDraw.setCornerRadius(400);
        mRoundDraw.setStroke(dp8 / 2, TestHelper.darkenColor(mModel.mainColor));
        mRoundDraw.setColor(mModel.mainColor);
//        mDrawable.setBounds(mModel.getLeft(), mModel.getTop(), mModel.getLeft() + mModel.getSize(), mModel.getTop() + mModel.getSize());

    }

    @Override
    public void onDraw(Canvas canvas) {
        //TODO 修改颜色，按下状态时，改为函数调用，同时修改paint吧
        updatePressPos();
        updatePaint();

//        if (!canvas.isHardwareAccelerated())
//            Log.d(TAG, "onDraw: canvas没有硬件加速");
//        int sc =canvas.saveLayer(mData.getLeft(),mData.getTop(),mData.getLeft()+mData.getWidth(),mData.getTop()+mData.getHeight(),null);

        //绘制外层，如果是按下状态（有关联手指），则绘制到手指初次按下时的位置，否则绘制到区域中心
        int outRadius = mModel.getSize() / 2;
        mRoundDraw.setBounds((int) (realCenterX - outRadius), (int) (realCenterY - outRadius), (int) (realCenterX + outRadius), (int) (realCenterY + outRadius));
        mRoundDraw.draw(canvas);

        //绘制内层，如果是按下状态且有关联手指，则绘制到手指方向的位置
        canvas.save();
        canvas.scale(0.5f, 0.5f, innerCenterX, innerCenterY); //这个是把画出来的东西缩放，而不是把画布缩放
        mRoundDraw.draw(canvas);
        canvas.restore();

//        canvas.restoreToCount(sc);
    }

    /**
     * 计算手指当前位置，存入realCenterXY和innerXY和nowFingerAt
     */
    protected void updatePressPos() {
        boolean isTouching =
//                !isEditing() &&
                mModel.isPressed() && activeFingers.size() > 0;
        updateRealCenterXY(isTouching);

        nowFingerAt = FINGER_AT_CENTER;

        if (!isTouching){
            innerCenterX = realCenterX;
            innerCenterY = realCenterY;
        }else {
            Finger finger = activeFingers.get(0);
            float xDiff = finger.getX() - realCenterX;
            float yDiff = finger.getY() - realCenterY;
            double unLimitedRadius = Math.hypot(xDiff, yDiff);
            if (unLimitedRadius > mModel.getSize()) {
                double ratio = mModel.getSize() / unLimitedRadius;
                xDiff *= ratio;
                yDiff *= ratio;
            }
            innerCenterX = realCenterX + xDiff;
            innerCenterY = realCenterY + yDiff;

            //先判断是否在0-3/8π范围内，确定上下，然后再判断是否在1/8π-1/2π范围内，叠加左右
            float tanCurrent = Math.abs(xDiff / yDiff);
            //不允许斜向
            if (mModel.direction == OneStick.WAY_4) {
                if (tanCurrent <= 1 && yDiff < 0) {
                    nowFingerAt |= FINGER_AT_TOP;
                } else if (tanCurrent <= 1) {
                    nowFingerAt |= FINGER_AT_BOTTOM;
                } else if (tanCurrent > 1 && xDiff < 0) {
                    nowFingerAt |= FINGER_AT_LEFT;
                } else if (tanCurrent > 1) {
                    nowFingerAt |= FINGER_AT_RIGHT;
                }
            }
            //允许斜向
            else {
                //注意dy是向下的大小，如果大于0说明是手指向下移动。。。然后上下和左右之间不要用else，否则没法斜向了
                if (tanCurrent < cot35d && yDiff < 0)
                    nowFingerAt |= FINGER_AT_TOP;
                else if (tanCurrent < cot35d && yDiff > 0)
                    nowFingerAt |= FINGER_AT_BOTTOM;

                if (tanCurrent > tan35d && xDiff < 0)
                    nowFingerAt |= FINGER_AT_LEFT;
                else if (tanCurrent > tan35d && xDiff > 0)
                    nowFingerAt |= FINGER_AT_RIGHT;
            }
        }
    }

    /**
     * updatePressPos时调用，更新整体中心位置。因为dpad的中心位置不会在按下时改变，所以单独抽出来作为一个方法，dpad里重写
     *
     * @param isTouching 是否有手指在这个区域内按着
     */
    protected void updateRealCenterXY(boolean isTouching) {
        realCenterX = isTouching ? activeFingers.get(0).getXWhenFirstTouched() : (mModel.getLeft() + mModel.getSize() / 2f);
        realCenterY = isTouching ? activeFingers.get(0).getYWhenFirstTouched() : (mModel.getTop() + mModel.getSize() / 2f);
    }

    @IntDef(flag = true,
            value = {FINGER_AT_LEFT, FINGER_AT_RIGHT, FINGER_AT_TOP, FINGER_AT_BOTTOM, FINGER_AT_CENTER})
    @Retention(RetentionPolicy.SOURCE)
    public @interface FingerAt {
    }
}
