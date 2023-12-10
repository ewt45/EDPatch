package org.ewt45.customcontrols.toucharea;

import android.app.AlertDialog;
import android.graphics.Canvas;
import android.support.annotation.NonNull;
import android.view.TextureView;
import android.view.View;
import android.widget.TextView;

import org.ewt45.customcontrols.model.TouchAreaModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 负责一块区域的图像绘制以及手势操作。
 */
public abstract class TouchArea<T extends TouchAreaModel> {
    protected T mData;
    /**
     * 不在自己区域内，未处理
     */
    public static int HANDLED_NOT=0;
    /**
     * 手指新进入该区域
     */
    public static int HANDLED_ADD = 2;
    /**
     * 手指仍然停留在该区域
     */
    public static int HANDLED_KEEP=2<<1;
    /**
     * 手指移出该区域
     */
    public static int HANDLED_REMOVE = 2<<2;

    public static TouchAdapter DEFAULT_ADAPTER = new TouchAdapter(){

         @Override
         public void notifyMoved(Finger finger, List<Finger> list) {

         }

         @Override
         public void notifyReleased(Finger finger, List<Finger> list) {

         }

         @Override
         public void notifyTouched(Finger finger, List<Finger> list) {

         }
     };

    private final List<Finger> activeFingers = new ArrayList<>();
    private final List<Finger> immutableActiveFingers = Collections.unmodifiableList(this.activeFingers);
    protected TouchAdapter mAdapter;
    private  TouchAdapter mRuntimeAdapter;
    private TouchAdapter mEditAdapter;
    protected TouchAreaView mHost;
    public TouchArea(TouchAreaView host,@NonNull T data, @NonNull TouchAdapter adapter){
        mHost = host;
        mData = data;
        mAdapter = adapter;
        mRuntimeAdapter = adapter;
        mEditAdapter = new TouchAdapter() {
            @Override
            public void notifyMoved(Finger finger, List<Finger> list) {

            }

            @Override
            public void notifyReleased(Finger finger, List<Finger> list) {
                new AlertDialog.Builder(mHost.getContext())
                        .setView(getEditView())
                        .setCancelable(false)
                        .setPositiveButton(android.R.string.ok,((dialog, which) -> {

                        })).show();
            }

            @Override
            public void notifyTouched(Finger finger, List<Finger> list) {

            }
        };
    }
    public abstract void onDraw( Canvas canvas);

    /**
     * 处理手指按下。如果在自身负责的范围内，添加finger到列表中，并通知adapter，返回HANDLED_ADD
     * @return HANDLED_ADD 或 HANDLED_NOT
     */
    public int handleFingerDown(Finger finger){
        if (isInside(finger)) {
            addFinger(finger);
//            this.lastFingerAction.set(finger, FingerActionType.TOUCH);
            this.mAdapter.notifyTouched(finger, this.immutableActiveFingers);
            return HANDLED_ADD;
        }
        return HANDLED_NOT;
    };

    /**
     * 处理手指移动。
     * <br/>如果以前在内，现在在内，则notifyMoved，返回HANDLED_KEEP
     * <br/> 以前在外，现在在内，notifyMovedIn HANDLED_ADD
     * <br/> 以前在内，现在在外， notifyMovedOut HANDLED_REMOVE
     * @return HANDLED_ADD HANDLED_REMOVE HANDLED_KEEP HANDLED_NOT
     */
    public int handleFingerMove(Finger finger){
        boolean inBefore = activeFingers.contains(finger);
        //内到内
        if(inBefore && (isInside(finger) || isMovedOutAllowed())){
            this.mAdapter.notifyMoved(finger, this.immutableActiveFingers);
            return HANDLED_KEEP;
        }
        //内到外
        else if(inBefore){
            removeFinger(finger);
            this.mAdapter.notifyMovedOut(finger, this.immutableActiveFingers);
            return HANDLED_REMOVE;
        }
        //外到内
        else if(isInside(finger) && isMovedInAllowed()){
            addFinger(finger);
            this.mAdapter.notifyMovedIn(finger, this.immutableActiveFingers);
            return HANDLED_ADD;
        }
        return HANDLED_NOT;
    }

    /**
     * 若在自身内，则从列表中移除finger，通知adapter，返回REMOVE
     * @return HANDLED_REMOVE HANDLED_NOT
     */
    public int handleFingerUp(Finger finger){
        if (this.activeFingers.contains(finger)) {
            removeFinger(finger);
//            this.lastFingerAction.set(finger, FingerActionType.RELEASE);
            this.mAdapter.notifyReleased(finger, this.immutableActiveFingers);
            return HANDLED_REMOVE;
        }
        return HANDLED_NOT;
    };

    /**
     * 某些类型（比如摇杆）可能希望手指在区域内按下后，不管移动多远，都不会移出区域。
     * <br/> 若返回true，则在move过程中不会移除手指，adapter不会通知notifyMovedOut
     * <br/> 虽然是isAllowed,但实际上只用于内部判断，外部始终是无条件地调用handleFingerMove
     */
    protected boolean isMovedOutAllowed(){
        return false;
    }

    /**
     * 和 {@link #isMovedOutAllowed()} 类似。是否处理移动过程中手指从外部进入自身的事件
     */
    protected boolean isMovedInAllowed(){
        return false;
    }

    /**
     * 判断手指是否在自身负责的区域内
     */
    protected boolean isInside(Finger finger) {
        float x = finger.getX();
        float y = finger.getY();
        return x > mData.left && x < mData.right && y > mData.top && y < mData.bottom;
    }

    /**
     * 列表中添加一个finger，并通知所有finger，手指数量变化
     */
    private void addFinger(Finger newFinger) {
        this.activeFingers.add(newFinger);
        for (Finger oneFinger : this.activeFingers)
            oneFinger.notifyFingersCountChanged();
    }

    /**
     * 列表中移除一个finger，并通知所有finger，手指数量变化
     */
    private void removeFinger(Finger finger) {
        for (Finger it : this.activeFingers)
            it.notifyFingersCountChanged();
        this.activeFingers.remove(finger);
    }

    /**
     * 进入编辑模式
     */
    public void startEditMode(){

    };

    protected View getEditView(){
        //TODO
        return  new TextView(mHost.getContext());
    }

    static class EditCallback{

    }
}
