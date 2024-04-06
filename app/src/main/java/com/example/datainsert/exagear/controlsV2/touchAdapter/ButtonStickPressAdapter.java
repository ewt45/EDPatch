package com.example.datainsert.exagear.controlsV2.touchAdapter;

import static com.example.datainsert.exagear.controlsV2.Const.stickMouse_distXYPerMove;
import static com.example.datainsert.exagear.controlsV2.Const.stickMouse_howManyFragment;

import android.graphics.PointF;
import android.os.CountDownTimer;
import android.support.annotation.IntDef;

import com.example.datainsert.exagear.controlsV2.Const;
import com.example.datainsert.exagear.controlsV2.Finger;
import com.example.datainsert.exagear.controlsV2.TouchAdapter;
import com.example.datainsert.exagear.controlsV2.model.OneStick;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

public class ButtonStickPressAdapter implements TouchAdapter {
    public final static int FINGER_AT_LEFT = 1 << 1;
    public final static int FINGER_AT_RIGHT = 1 << 2;
    public final static int FINGER_AT_TOP = 1 << 3;
    public final static int FINGER_AT_BOTTOM = 1 << 4;
    public final static int FINGER_AT_CENTER = 0;
    //35 30 22.5
    private static final float tan35d = 0.70020753f;//0.57735026f;//0.414213562f;
    private static final float cot35d = 1.42814800f;//1.73205080f;//2.414213562f;
    /**
     * 当前手指在哪个方向上，默认为FINGER_AT_CENTER
     */
    @FingerAt
    public int nowFingerAt = FINGER_AT_CENTER;
    @FingerAt
    protected int lastFingerAt = FINGER_AT_CENTER;
    protected PointF fingerFirstDown = new PointF();
    protected PointF outerCenter = new PointF();
    protected PointF innerCenter = new PointF();
    protected final OneStick mModel;
    protected Finger mFinger;
    private final JoyStickMouseMoveInjector mMouseMoveInjector = new JoyStickMouseMoveInjector();

    public ButtonStickPressAdapter(OneStick model) {
        mModel = model;
        updateRealOuterCenterXYAndFingerDownXY(false);
    }

    /**
     * 计算手指当前位置，存入realCenterXY和innerXY和nowFingerAt<
     * <br/> mFinger不为null时，认为是正在按下，应该计算实际位置，否则按model的原始位置来
     */
    public void updatePressPos() {
        boolean isTouching = mFinger != null;
//                !isEditing() &&
//                 activeFingers.size() > 0
//                 mModel.isPressed();

        nowFingerAt = FINGER_AT_CENTER;


        if (!isTouching) {
            innerCenter.x = outerCenter.x;
            innerCenter.y = outerCenter.y;
        } else {
            //内圆中心偏移 计算是从外圆圆心到手指当前按下，并非手指初始按下到当前按下，因为前者可能大于后者
            float xDiffUnlimited = mFinger.getX() - outerCenter.x;
            float yDiffUnlimited = mFinger.getY() - outerCenter.y;

            double unlimitedDist = Math.hypot(xDiffUnlimited,yDiffUnlimited);

            //内圆中心点 当前位置 (不能超过允许的最大移动范围）
            double maxAndUnlimitedDistRatio = mModel.getInnerMaxOffsetFromOuterCenter() / unlimitedDist;
            float xDiffLimited = (float) (xDiffUnlimited * (maxAndUnlimitedDistRatio < 1 ? maxAndUnlimitedDistRatio : 1));
            float yDiffLimited = (float) (yDiffUnlimited * (maxAndUnlimitedDistRatio < 1 ? maxAndUnlimitedDistRatio : 1));

            innerCenter.x = outerCenter.x + xDiffLimited;
            innerCenter.y = outerCenter.y + yDiffLimited;

            //先判断是否在0-3/8π范围内，确定上下，然后再判断是否在1/8π-1/2π范围内，叠加左右
            float tanCurrent = Math.abs(xDiffLimited / yDiffLimited);
            //当移动过小时忽略
            if(unlimitedDist < Const.stickMoveThreshold){
                nowFingerAt = FINGER_AT_CENTER;
            }else{
                //斜向分界线，用于判断是否属于竖向或横向移动
                float tanInVertical = mModel.direction == OneStick.WAY_4 ? 1: cot35d;
                float tanInHorizontal = mModel.direction == OneStick.WAY_4 ? 1: tan35d;

                //注意dy是向下的大小，如果大于0说明是手指向下移动。。。然后上下和左右之间不要用else，否则没法斜向了
                if (tanCurrent <= tanInVertical && yDiffLimited < 0)
                    nowFingerAt |= FINGER_AT_TOP;
                else if (tanCurrent <= tanInVertical && yDiffLimited > 0)
                    nowFingerAt |= FINGER_AT_BOTTOM;

                if (tanCurrent > tanInHorizontal && xDiffLimited < 0)
                    nowFingerAt |= FINGER_AT_LEFT;
                else if (tanCurrent > tanInHorizontal && xDiffLimited > 0)
                    nowFingerAt |= FINGER_AT_RIGHT;
            }
        }
    }

    /**
     * 构造函数、手指按下或松开时调用，更新外圆中心位置和手指初始按下位置。因为dpad的中心位置不会在按下时改变，所以单独抽出来作为一个方法，dpad里重写
     *
     * @param isTouching 是否有手指在这个区域内按着
     */
    protected void updateRealOuterCenterXYAndFingerDownXY(boolean isTouching) {
        float centerX = mModel.getLeft() + mModel.getSize() / 2f;
        float centerY = mModel.getTop() + mModel.getSize() / 2f;

        fingerFirstDown.x = isTouching ? mFinger.getXWhenFirstTouched() : centerX;
        fingerFirstDown.y = isTouching ? mFinger.getYWhenFirstTouched() : centerY;

        float xOffFromCenter = fingerFirstDown.x - centerX;
        float yOffFromCenter = fingerFirstDown.y - centerY;

        //外圆中心起始时，最大移动范围为内圆边缘
        double maxAndCurrentRadio = mModel.getInnerRadius() / Math.hypot(xOffFromCenter, yOffFromCenter);
        outerCenter.x = (float) (centerX + xOffFromCenter * (maxAndCurrentRadio >= 1 ? 1 : maxAndCurrentRadio));
        outerCenter.y = (float) (centerY + yOffFromCenter * (maxAndCurrentRadio >= 1 ? 1 : maxAndCurrentRadio));
    }

    /**
     * 根据方向发送按键/鼠标移动
     */
    private void sendKeys() {
        if(mModel.getDirection() == OneStick.WAY_MOUSE){
            //本次横向移动距离 = 一个fragment的距离 根据当前内圆圆心偏移外圆圆形的横向距离进行缩放（缩放范围0~1）
            float dx = stickMouse_distXYPerMove[0] * (innerCenter.x - outerCenter.x) / mModel.getInnerMaxOffsetFromOuterCenter();
            float dy = stickMouse_distXYPerMove[1] * (innerCenter.y - outerCenter.y) / mModel.getInnerMaxOffsetFromOuterCenter();
            mMouseMoveInjector.setDeltaFragment(dx,dy);
        }else{
            //之前有，现在没有，松开
            if ((FINGER_AT_LEFT & lastFingerAt) > 0 && (FINGER_AT_LEFT & nowFingerAt) == 0)
                Const.getXServerHolder().releaseKeyOrPointer(mModel.getKeycodeAt(OneStick.KEY_LEFT));
                //之前没有，现在有，按下
            else if ((FINGER_AT_LEFT & lastFingerAt) == 0 && (FINGER_AT_LEFT & nowFingerAt) > 0)
                Const.getXServerHolder().pressKeyOrPointer(mModel.getKeycodeAt(OneStick.KEY_LEFT));

            if ((FINGER_AT_RIGHT & lastFingerAt) > 0 && (FINGER_AT_RIGHT & nowFingerAt) == 0)
                Const.getXServerHolder().releaseKeyOrPointer(mModel.getKeycodeAt(OneStick.KEY_RIGHT));
            else if ((FINGER_AT_RIGHT & lastFingerAt) == 0 && (FINGER_AT_RIGHT & nowFingerAt) > 0)
                Const.getXServerHolder().pressKeyOrPointer(mModel.getKeycodeAt(OneStick.KEY_RIGHT));

            if ((FINGER_AT_TOP & lastFingerAt) > 0 && (FINGER_AT_TOP & nowFingerAt) == 0)
                Const.getXServerHolder().releaseKeyOrPointer(mModel.getKeycodeAt(OneStick.KEY_TOP));
            else if ((FINGER_AT_TOP & lastFingerAt) == 0 && (FINGER_AT_TOP & nowFingerAt) > 0)
                Const.getXServerHolder().pressKeyOrPointer(mModel.getKeycodeAt(OneStick.KEY_TOP));

            if ((FINGER_AT_BOTTOM & lastFingerAt) > 0 && (FINGER_AT_BOTTOM & nowFingerAt) == 0)
                Const.getXServerHolder().releaseKeyOrPointer(mModel.getKeycodeAt(OneStick.KEY_BOTTOM));
            else if ((FINGER_AT_BOTTOM & lastFingerAt) == 0 && (FINGER_AT_BOTTOM & nowFingerAt) > 0)
                Const.getXServerHolder().pressKeyOrPointer(mModel.getKeycodeAt(OneStick.KEY_BOTTOM));
        }

        lastFingerAt = nowFingerAt;
    }

    @Override
    public void notifyMoved(Finger finger, List<Finger> list) {
        updatePressPos();
        sendKeys();
    }

    @Override
    public void notifyReleased(Finger finger, List<Finger> list) {
        mFinger = null;

        updateRealOuterCenterXYAndFingerDownXY(false);
        updatePressPos();
        sendKeys();
        //停止鼠标移动
        if(mModel.getDirection() == OneStick.WAY_MOUSE && mMouseMoveInjector.isRunning)
            mMouseMoveInjector.doStop();

        //这个手指位置应该在最后重置，否则就破坏了上面的逻辑，导致没法正常松开按键了
        nowFingerAt = FINGER_AT_CENTER;
        lastFingerAt = FINGER_AT_CENTER;
    }

    @Override
    public void notifyTouched(Finger finger, List<Finger> list) {
        if (mFinger != null)
            return;

        mFinger = finger;
        nowFingerAt = FINGER_AT_CENTER;
        lastFingerAt = FINGER_AT_CENTER;

        updateRealOuterCenterXYAndFingerDownXY(true);
        updatePressPos();
        //开启鼠标移动
        if(mModel.getDirection() == OneStick.WAY_MOUSE && !mMouseMoveInjector.isRunning)
            mMouseMoveInjector.doStart();
        sendKeys(); //按下时就要发送一次按键。否则Dpad或者stick初始距圆心较远时不会发送按键

    }

    public float getOuterCenterX() {
        return outerCenter.x;
    }

    public float getOuterCenterY() {
        return outerCenter.y;
    }

    public float getInnerCenterX() {
        return innerCenter.x;
    }

    public float getInnerCenterY() {
        return innerCenter.y;
    }

    @IntDef(flag = true,
            value = {FINGER_AT_LEFT, FINGER_AT_RIGHT, FINGER_AT_TOP, FINGER_AT_BOTTOM, FINGER_AT_CENTER})
    @Retention(RetentionPolicy.SOURCE)
    public @interface FingerAt {}

    private static class JoyStickMouseMoveInjector extends CountDownTimer{
        boolean isRunning = false;
        PointF deltaXY = new PointF();
        /** 一个片段移动多远距离 */

        public JoyStickMouseMoveInjector() {
            super(10000000, Const.stickMouse_interval);
        }

        /**
         * countDownTimer没有判断当前是否正在运行的方法。没办法，只好单独写一个开始和停止，用于更新标志位
         */
        public void doStart(){
            isRunning = true;
            start();
        }

        public void doStop(){
            cancel();
            isRunning = false;
        }
        /**
         * 设置每次发送移动事件时，横向和纵向的移动距离
         */
        public void setDeltaFragment(float x, float y){
            deltaXY.set(x, y);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            if(deltaXY.x != 0 || deltaXY.y != 0)
                Const.getXServerHolder().injectPointerDelta(deltaXY.x, deltaXY.y);
        }

        @Override
        public void onFinish() {
            this.start();
        }
    }
}
