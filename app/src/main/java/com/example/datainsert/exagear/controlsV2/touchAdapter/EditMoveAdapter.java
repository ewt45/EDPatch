package com.example.datainsert.exagear.controlsV2.touchAdapter;

import static com.example.datainsert.exagear.controlsV2.Const.dp8;

import android.util.Log;

import com.example.datainsert.exagear.controlsV2.Finger;
import com.example.datainsert.exagear.controlsV2.TouchAdapter;
import com.example.datainsert.exagear.controlsV2.TouchAreaView;
import com.example.datainsert.exagear.controlsV2.model.OneGestureArea;
import com.example.datainsert.exagear.controlsV2.TouchAreaModel;

import java.util.List;

/**
 * 对应一个编辑模式下的toucharea，当手指触碰到该区域内，移动该按钮的位置 以实现编辑位置功能，同时调用OnFocusListener通知编辑视图当前选中的model
 * <br/> 但是如果是gesture的Model，则不应该移动
 */
public class EditMoveAdapter implements TouchAdapter {
    private static final String TAG = "EditMoveAdapter";
    private final int mUnit = dp8;
    OnFocusListener mFocusListener;
    TouchAreaView mView;
    TouchAreaModel mModel;
    boolean isPressing = false;
    //记录的时候初始按下时，modelLeft-fingerX，因为每次更新model都是直接赋值没法相对增减，所以要记录初始model值。
    //每次更新时只需加上最新的fingerX即可得到最新的modelLeft
    float firstFingerX, firstFingerY;
    float firstModelX, firstModelY;

    public EditMoveAdapter(TouchAreaView view, TouchAreaModel model, OnFocusListener focusListener) {
        mView = view;
        mModel = model;
        mFocusListener = focusListener;
    }

    @Override
    public void notifyMoved(Finger finger, List<Finger> list) {
        updatePosition(finger);
    }

    private void updatePosition(Finger finger) {
        if (mModel instanceof OneGestureArea)
            return;

        mModel.setLeft((int) firstModelX + minMul4(finger.getX() - firstFingerX));
        mModel.setTop((int) firstModelY + minMul4(finger.getY() - firstFingerY));
        mView.invalidate();
//        TestHelper.logTouchAreaRect(mModel);
    }

    @Override
    public void notifyReleased(Finger finger, List<Finger> list) {
        Log.d("TAG", "notifyReleased: ");
        updatePosition(finger);
        isPressing = false;
    }

    @Override
    public void notifyTouched(Finger finger, List<Finger> list) {
        if (isPressing)
            return;

        isPressing = true;

        firstModelX = minMul4(mModel.getLeft());
        firstModelY = minMul4(mModel.getTop());
        firstFingerX = finger.getX();
        firstFingerY = finger.getY();

        mFocusListener.onFocus(mModel);
    }

    private int minMul4(float a) {
        return Math.floorDiv((int) a, mUnit) * mUnit;
    }

    public interface OnFocusListener {
        void onFocus(TouchAreaModel model);
    }
}
