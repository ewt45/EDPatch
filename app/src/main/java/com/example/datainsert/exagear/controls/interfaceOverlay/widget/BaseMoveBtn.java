package com.example.datainsert.exagear.controls.interfaceOverlay.widget;

import static android.view.MotionEvent.ACTION_CANCEL;
import static android.view.MotionEvent.ACTION_DOWN;
import static android.view.MotionEvent.ACTION_MOVE;
import static android.view.MotionEvent.ACTION_POINTER_DOWN;
import static android.view.MotionEvent.ACTION_POINTER_UP;
import static android.view.MotionEvent.ACTION_UP;
import static android.view.MotionEvent.INVALID_POINTER_ID;

import android.content.Context;
import android.graphics.PointF;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.eltechs.axs.Finger;
import com.eltechs.axs.GeometryHelpers;

/**
 * 可以移动自由定位的按钮。
 * getMoving获取本次按下期间是否移动过
 * getEditing获取当前是否是编辑（是否可以移动）
 * 子类重写几个onTouch方法，调用super用来处理移动，然后自己再处理
 *
 * 子类应该重写onTouchMove方法，调用super后，在isEditing时获取layoutparams的margintop和left，更新到自身的model中
 *
 * 为什么移动的时候用getRawX正常，getX(poingerIndex)就会乱飞呢
 */
public abstract class BaseMoveBtn extends AppCompatButton implements View.OnClickListener {
    private static final String TAG = "BaseMoveBtn";
    private PointF mLastXYPoint = new PointF();
    private boolean mMovedWhenTouch = false; //是否正在移动
    private int mActivePointerId =INVALID_POINTER_ID; //原始按下的那根手指，防止后续新按下的手指捣乱

    public BaseMoveBtn(Context context) {
        super(context);
        setOnClickListener(this);
    }



    public boolean isMoved() {
        return mMovedWhenTouch;
    }

    public void setMoved(boolean mMoved) {
        this.mMovedWhenTouch = mMoved;
    }

    public PointF getLastXYPoint() {
        return mLastXYPoint;
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }



    @Override
    public boolean onTouchEvent(MotionEvent event) {

        int action = event.getActionMasked();
//        //如果不在可编辑情况下，直接返回 按键码也自己处理吧
//        if(!isEditing)
//            return super.onTouchEvent(event);
//        return myGestureListener.onTouchEvent(event);

//        Log.d(TAG, "onTouchEvent: 事件为"+action+"0=down,1=up,2=move");
        //只有当移动的时候自己处理一下，其他交给gesture处理
        boolean consumed = false;
        switch (action) {
            case ACTION_DOWN: {
                //第一根手指按下时记录id
                mActivePointerId = event.getPointerId( 0);
                consumed = onTouchDown(event);
                break;
            }
            case ACTION_CANCEL: {
                mActivePointerId = INVALID_POINTER_ID;
                consumed=onTouchCancel(event);
                break;
            }
            case ACTION_UP: {
                mActivePointerId = INVALID_POINTER_ID;
                consumed = onTouchUp(event);
                break;
            }
            case ACTION_MOVE: {
                consumed = onTouchMove(event);
                break;
            }
            case ACTION_POINTER_UP:{
                Log.d(TAG, "onTouchEvent: 新手指松开");
                final int pointerIndex = event.getActionIndex();
                final int pointerId = event.getPointerId(pointerIndex);

                if (pointerId == mActivePointerId) {
                    Log.d(TAG, "onTouchEvent: ACTION_POINTER_UP简介说这是非初始手指松开，文档示例又说在这里判断是不是非初始手指。。");
                    assert false;
                }
                break;
            }
            case ACTION_POINTER_DOWN:{
                Log.d(TAG, "onTouchEvent: 新手指按下");
                //不处理新手指按下事件试试？
                //啊进到这里之后没法进到toucharea是因为btnContainer不是处理触摸时间的那个布局的子布局。。。

            }
            default:{
                consumed = onTouchOther(event);
            }

        }
//        if (this.myGestureListener.onTouchEvent(event)) {
//            return true;
//        }
        return consumed ;
    }

    /**
     * ontouch的action_down事件发生。
     * 为什么用getRawX正常，getX(poingerIndex)就会乱飞呢
     */
    protected boolean onTouchDown(MotionEvent event) {
        setMoved(false);
        setPressed(true);
//                if(!isEditing && mViewFacade!=null)
//                    mViewFacade.injectKeyPress((byte) (mOneKey.getCode()+8));
        final int pointerIndex = event.findPointerIndex(mActivePointerId);
        mLastXYPoint.y =  event.getRawY();
        mLastXYPoint.x = event.getRawX();

        return true;
    }

    /**
     * ontouch的action_up事件发生。
     */
    protected boolean onTouchUp(MotionEvent event) {
        //                Log.d(TAG, "onTouchEvent: 距离："+GeometryHelpers.distance(event.getRawX(), event.getRawY(), pressX, pressY));
        setPressed(false);
        if (!isMoved())
            performClick();
        return true;
    }

    /**
     * ontouch的action_move事件发生。
     * 子类应该重写该方法，调用super后，在isEditing时获取layoutparams的margintop和left，更新到自身的model中
     */
    protected boolean onTouchMove(MotionEvent event) {
        final int pointerIndex = event.findPointerIndex(mActivePointerId);
        //获取初始按下手指的坐标
        float tempRawX =  event.getRawX();
        float tempRawY = event.getRawY();
        float moveDistance = GeometryHelpers.distance(new PointF(tempRawX, tempRawY), mLastXYPoint);
        Log.d(TAG, "onTouchEvent: 移动距离=" + moveDistance);
        if (moveDistance > 0.01) {
            setMoved(true);
        }

        float newLeft = getLeft() + tempRawX - mLastXYPoint.x;
        float newTop = getTop() + tempRawY - mLastXYPoint.y;

        mLastXYPoint.x = tempRawX;
        mLastXYPoint.y = tempRawY;
        //把新的位置 oriLeft, newTop, oriRight, oriBottom设置到控件，实现位置移动和大小变化。

        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) getLayoutParams();
        lp.setMargins((int) newLeft, (int) newTop, 0, 0);
        setLayoutParams(lp);
        //让子类更新自身model
        updateModelMargins((int) newLeft, (int) newTop);

        return true;
    }

    /**
     * 让子类更新自身model的margin值（位置）
     */
    protected abstract void updateModelMargins(int marginLeft, int marginTop);


    protected boolean onTouchCancel(MotionEvent event){
        setPressed(false);
        return true;
    }

    /**
     * ontouch的action_其他事件发生。调用super处理
     */
    protected boolean onTouchOther(MotionEvent event){
       return  super.onTouchEvent(event);
    }

    /**
     * 用于使用模式下注入按键按下按钮的操作，由子类实现
     */
    public abstract void injectPress(Finger finger);

    public abstract void injectMove(Finger finger);

    public abstract void injectRelease(Finger finger);


}
