package com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.touchAdapter;

import static com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.Const.dp8;

import android.util.Log;

import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.Finger;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.TouchAdapter;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.TouchAreaView;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.model.TouchAreaModel;

import java.util.List;

public class EditMoveAdapter implements TouchAdapter {
    private static final String TAG = "EditMoveAdapter";
    OnFocusListener mFocusListener;
    TouchAreaView mView;
    TouchAreaModel mModel;
    boolean isPressing=false;
    //记录的时候初始按下时，modelLeft-fingerX，因为每次更新model都是直接赋值没法相对增减，所以要记录初始model值。
    //每次更新时只需加上最新的fingerX即可得到最新的modelLeft
    float firstFingerX, firstFingerY;
    float firstModelX, firstModelY;

    public EditMoveAdapter(TouchAreaView view, TouchAreaModel model, OnFocusListener focusListener){
        mView=view;
        mModel=model;
        mFocusListener = focusListener;
    }
    @Override
    public void notifyMoved(Finger finger, List<Finger> list) {
        updatePosition(finger);
    }

    private void updatePosition(Finger finger){

        mModel.setLeft((int) firstModelX + minMul4(finger.getX()  - firstFingerX));
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
        if(isPressing)
            return;

        isPressing=true;

        firstModelX  = minMul4(mModel.getLeft());
        firstModelY = minMul4(mModel.getTop());
        firstFingerX = finger.getX();
        firstFingerY = finger.getY();

        mFocusListener.onFocus(mModel);
    }

    private final int mUnit = dp8;


    private int  minMul4(float a){
        return Math.floorDiv((int) a, mUnit)* mUnit;
    }

    public interface OnFocusListener{
        void onFocus(TouchAreaModel model);
    }
}
