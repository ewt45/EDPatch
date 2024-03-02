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
    protected float startCenterX = 0;
    protected float startCenterY = 0;
    protected float innerCenterX = 0;
    protected float innerCenterY = 0;
    private Finger mFinger;

    public ButtonStickPressAdapter(OneStick model) {
        mModel = model;
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
        updateRealCenterXY(isTouching);

        nowFingerAt = FINGER_AT_CENTER;

        if (!isTouching) {
            innerCenterX = startCenterX;
            innerCenterY = startCenterY;
        } else {
            float xDiff = mFinger.getX() - startCenterX;
            float yDiff = mFinger.getY() - startCenterY;
            double unLimitedRadius = Math.hypot(xDiff, yDiff);
            float maxRadius = mModel.getSize() / 2f;
            if (unLimitedRadius > maxRadius) {
                double ratio = maxRadius / unLimitedRadius;
                xDiff *= ratio;
                yDiff *= ratio;
            }
            innerCenterX = startCenterX + xDiff;
            innerCenterY = startCenterY + yDiff;

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
        startCenterX = isTouching ? mFinger.getXWhenFirstTouched() : (mModel.getLeft() + mModel.getSize() / 2f);
        startCenterY = isTouching ? mFinger.getYWhenFirstTouched() : (mModel.getTop() + mModel.getSize() / 2f);
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
        nowFingerAt = FINGER_AT_CENTER;
        lastFingerAt = FINGER_AT_CENTER;

        updatePressPos();
        sendKeys();
    }

    @Override
    public void notifyTouched(Finger finger, List<Finger> list) {
        if (mFinger != null)
            return;

        mFinger = finger;
        nowFingerAt = FINGER_AT_CENTER;
        lastFingerAt = FINGER_AT_CENTER;

        updatePressPos();

    }

    public float getStartCenterX() {
        return startCenterX;
    }

    public float getStartCenterY() {
        return startCenterY;
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
