package com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.adapter;

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
    float firstX, firstY;

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
//        Log.d(TAG, "updatePosition: finger位移="+(int) (firstX+finger.getX())+" "+ (firstY+finger.getY()));
        mModel.setLeft((int) (firstX+finger.getX()));
        mModel.setTop((int) (firstY+finger.getY()));
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
        firstX = mModel.getLeft()-finger.getX();
        firstY = mModel.getTop()-finger.getY();
        mFocusListener.onFocus(mModel);
    }

    public interface OnFocusListener{
        void onFocus(TouchAreaModel model);
    }
}
