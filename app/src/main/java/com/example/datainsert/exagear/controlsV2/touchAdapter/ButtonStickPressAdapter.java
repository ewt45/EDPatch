package com.example.datainsert.exagear.controlsV2.touchAdapter;

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
    //    private final TouchAdapter mAltAdapter;// 例如编辑模式的adapter，如果这个不为null，那么返回的中心点位置退化为普通按钮的那种
    public final static int FINGER_AT_CENTER = 0;
    //35 30 22.5
    private static final float tan35d = 0.70020753f;//0.57735026f;//0.414213562f;
    private static final float cot35d = 1.42814800f;//1.73205080f;//2.414213562f;
    protected final OneStick mModel;
    /**
     * 当前手指在哪个方向上，默认为FINGER_AT_CENTER
     */
    @FingerAt
    public int nowFingerAt = FINGER_AT_CENTER;
    @FingerAt
    protected int lastFingerAt = FINGER_AT_CENTER;
    protected float fingerFirstDownX = 0; //TODO 需要把startCenterX改为downX，原先用这个的现在用outerCenterX（dpad会不会有影响）
    protected float fingerFirstDownY = 0;
    protected float outerCenterX = 0;
    protected float outerCenterY = 0;
    protected float innerCenterX = 0;
    protected float innerCenterY = 0;
    protected Finger mFinger;

    public ButtonStickPressAdapter(OneStick model) {
        mModel = model;
        updateRealOuterCenterXYAndFingerDownXY(false);
    }

    /**
     * 计算手指当前位置，存入realCenterXY和innerXY和nowFingerAt<
     * <br/> mFinger不为null时，认为是正在按下，应该计算实际位置，否则按model的原始位置来
     */
    public void updatePressPos() {
        //TODO 当移动过小时忽略？
        boolean isTouching = mFinger != null;
//                !isEditing() &&
//                 activeFingers.size() > 0
//                 mModel.isPressed();

        nowFingerAt = FINGER_AT_CENTER;


        if (!isTouching) {
            innerCenterX = outerCenterX;
            innerCenterY = outerCenterY;
        } else {
            //内圆中心偏移 计算是从外圆圆心到手指当前按下，并非手指初始按下到当前按下，因为前者可能大于后者
            float xDiffUnlimited = mFinger.getX() - outerCenterX;
            float yDiffUnlimited = mFinger.getY() - outerCenterY;

            //内圆中心点 当前位置 (不能超过允许的最大移动范围）
            double maxAndUnlimitedRadiusRatio = mModel.getInnerMaxOffsetFromOuterCenter() / Math.hypot(xDiffUnlimited, yDiffUnlimited);
            float xDiffLimited = (float) (xDiffUnlimited * (maxAndUnlimitedRadiusRatio < 1 ? maxAndUnlimitedRadiusRatio : 1));
            float yDiffLimited = (float) (yDiffUnlimited * (maxAndUnlimitedRadiusRatio < 1 ? maxAndUnlimitedRadiusRatio : 1));

            innerCenterX = outerCenterX + xDiffLimited;
            innerCenterY = outerCenterY + yDiffLimited;

            //先判断是否在0-3/8π范围内，确定上下，然后再判断是否在1/8π-1/2π范围内，叠加左右
            float tanCurrent = Math.abs(xDiffLimited / yDiffLimited);
            //不允许斜向
            if (mModel.direction == OneStick.WAY_4) {
                if (tanCurrent <= 1 && yDiffLimited < 0) {
                    nowFingerAt |= FINGER_AT_TOP;
                } else if (tanCurrent <= 1) {
                    nowFingerAt |= FINGER_AT_BOTTOM;
                } else if (tanCurrent > 1 && xDiffLimited < 0) {
                    nowFingerAt |= FINGER_AT_LEFT;
                } else if (tanCurrent > 1) {
                    nowFingerAt |= FINGER_AT_RIGHT;
                }
            }
            //允许斜向
            else {
                //注意dy是向下的大小，如果大于0说明是手指向下移动。。。然后上下和左右之间不要用else，否则没法斜向了
                if (tanCurrent < cot35d && yDiffLimited < 0)
                    nowFingerAt |= FINGER_AT_TOP;
                else if (tanCurrent < cot35d && yDiffLimited > 0)
                    nowFingerAt |= FINGER_AT_BOTTOM;

                if (tanCurrent > tan35d && xDiffLimited < 0)
                    nowFingerAt |= FINGER_AT_LEFT;
                else if (tanCurrent > tan35d && xDiffLimited > 0)
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

        fingerFirstDownX = isTouching ? mFinger.getXWhenFirstTouched() : centerX;
        fingerFirstDownY = isTouching ? mFinger.getYWhenFirstTouched() : centerY;

        float xOffFromCenter = fingerFirstDownX - centerX;
        float yOffFromCenter = fingerFirstDownY - centerY;

        //外圆中心起始时，最大移动范围为内圆边缘
        double maxAndCurrentRadio = mModel.getInnerRadius() / Math.hypot(xOffFromCenter, yOffFromCenter);
        outerCenterX = (float) (centerX + xOffFromCenter * (maxAndCurrentRadio >= 1 ? 1 : maxAndCurrentRadio));
        outerCenterY = (float) (centerY + yOffFromCenter * (maxAndCurrentRadio >= 1 ? 1 : maxAndCurrentRadio));
    }

    /**
     * 根据方向发送按键
     */
    protected void sendKeys() {
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

    }

    public float getOuterCenterX() {
        return outerCenterX;
    }

    public float getOuterCenterY() {
        return outerCenterY;
    }

    public float getInnerCenterX() {
        return innerCenterX;
    }

    public float getInnerCenterY() {
        return innerCenterY;
    }

    @IntDef(flag = true,
            value = {FINGER_AT_LEFT, FINGER_AT_RIGHT, FINGER_AT_TOP, FINGER_AT_BOTTOM, FINGER_AT_CENTER})
    @Retention(RetentionPolicy.SOURCE)
    public @interface FingerAt {
    }
}
