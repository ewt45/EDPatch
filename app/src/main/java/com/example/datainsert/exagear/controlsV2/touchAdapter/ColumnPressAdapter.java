package com.example.datainsert.exagear.controlsV2.touchAdapter;

import static com.example.datainsert.exagear.controlsV2.Const.fingerTapMaxMovePixels;
import static com.example.datainsert.exagear.controlsV2.TestHelper.assertTrue;
import static com.example.datainsert.exagear.controlsV2.TestHelper.distance;

import android.graphics.PointF;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.example.datainsert.exagear.QH;
import com.example.datainsert.exagear.controlsV2.Const;
import com.example.datainsert.exagear.controlsV2.Finger;
import com.example.datainsert.exagear.controlsV2.TouchAdapter;
import com.example.datainsert.exagear.controlsV2.model.OneColumn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 支持多手指吧（可同时按下多个按键）
 */
public class ColumnPressAdapter implements TouchAdapter {
    private static final String TAG = "ColumnPressAdapter";
    private static final int COUNT_DOWN = 180;
    /** 上次手指松开时，保留的滚动视图距离。如果视图相对原位置上移，则此值为负数. */
    private float lastScrollOffset = 0;
    /** 本次手指移动时，滚动视图的距离，为上次残留距离+当前手指移动距离。如果视图相对原位置上移，则此值为负数 */
    private float scrollOffset = 0;
    private final OneColumn model;
    /** 当前是否处于手指按下期间 */
    private boolean isTouched = false;
    /** 本次手指按下期间，是否滚动过视图。如果滚动过，则不应触发按键 */
    private boolean isScrolled = false;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final CountDownTimer timer;
    private long downTime;
    /** 本次手指按下期间，全部按下的按键。松开手指时应该松开对应按键 */
    private final Map<Finger, Integer> pressedKeycodes = new HashMap<>();
    /** 第一个按下的手指。因为不能立刻发送按下事件，所以没法直接放到map中，先暂存到成员变量上，倒计时结束再根据移动距离存入map中 */
    private Finger firstPressedFinger = null;

    public ColumnPressAdapter(OneColumn model){
        this.model = model;
        timer = new CountDownTimer(COUNT_DOWN,1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                handler.post(()-> onTimeUp());
            }

        };
    }
    @Override
    public void notifyMoved(Finger finger, List<Finger> list) {
        //手指移动的时候，应该检测移动距离是否超出阈值，如果超出则开始滚动，此次手指事件期间不触发按键 (只计算第一个按下的手指的移动距离）
        if(!isScrolled
                && (System.currentTimeMillis() - downTime < COUNT_DOWN)
                && distance(firstPressedFinger.getXWhenFirstTouched(),firstPressedFinger.getYWhenFirstTouched(),
                firstPressedFinger.getX(), firstPressedFinger.getY()) > fingerTapMaxMovePixels) {
            isScrolled = true;
            timer.cancel();
        }

        if(isScrolled) {
            float currScroll = model.isVertical() ? (finger.getY() - finger.getYWhenFirstTouched()) : (finger.getX() - finger.getXWhenFirstTouched());
            scrollOffset = lastScrollOffset + currScroll;
            //上移（负数）最小只能移动 被隐藏的那部分长度
            scrollOffset = Math.max(scrollOffset, model.getRestrictedLength() - model.getTotalLength());
            //下移最大是0（回到最顶端）
            scrollOffset = Math.min(scrollOffset, 0);
        }
    }

    @Override
    public void notifyReleased(Finger finger, List<Finger> list) {
        //如果第一个按下的手指的倒计时尚未结束，就要松开。此时如果没滚动，则应该按下按键
        if(finger == firstPressedFinger
                && !isScrolled && pressedKeycodes.get(firstPressedFinger) == null) {
            pressedKeycodes.put(firstPressedFinger, pressByCoordinate(firstPressedFinger));
            QH.sleep(50);
        }

        Integer key = pressedKeycodes.get(finger);
        if(key != null) {
            pressedKeycodes.remove(finger);
            Const.getXServerHolder().releaseKeyOrPointer(key);
        }


        if(list.isEmpty()) {
            //若list为空，则清空map并松开全部按键，以防某些事件未接收到而导致卡住
            while (!pressedKeycodes.keySet().isEmpty()) {
                Finger fingerLeft = pressedKeycodes.keySet().iterator().next();
                Integer keyLeft = pressedKeycodes.remove(fingerLeft);
                Const.getXServerHolder().releaseKeyOrPointer(keyLeft);
            }

            firstPressedFinger = null;
            isTouched=false;
            timer.cancel();
            lastScrollOffset = scrollOffset; //记录手指离开时残留的滚动距离

        }
    }

    @Override
    public void notifyTouched(Finger finger, List<Finger> list) {
        if (isTouched){
            pressedKeycodes.put(finger, pressByCoordinate(finger));
            return;
        }

        isTouched = true;
        isScrolled = false;
        downTime = System.currentTimeMillis();
        pressedKeycodes.clear();
        firstPressedFinger = finger;
        //按下手指后开始倒计时，倒计时结束时如果没有滚动，则按下对应按键
        timer.start();
    }

    /**
     * 获取当前滚动的距离。如果视图相对原位置上移，则此值为负数
     */
    public float getScrollOffset() {
        return scrollOffset;
    }

    /**
     * 只有当前手指没松开，且没滚动视图，才按下
     */
    private void onTimeUp(){
        if(isTouched && !isScrolled && pressedKeycodes.get(firstPressedFinger) == null)
            pressedKeycodes.put(firstPressedFinger, pressByCoordinate(firstPressedFinger));
    }

    /**
     * 根据坐标定位到应该按下的keycode，发送按下事件，并返回该keycode
     */
    private Integer pressByCoordinate(Finger finger){
        int idx;
        //以垂直为例。需要算出手指y，距离按钮组的顶部y， 包括了多少个按钮的高度。
        // 还要考虑滚动：如果向上滚动了，那么scrollOffset为负数，但实际距离顶部距离应该变大，所以是加上负的offset
        if(model.isVertical()) {
            idx = (int) (finger.getYWhenFirstTouched() - model.getTop() + (-scrollOffset)) / model.getHeight();
        }else
            idx = (int) (finger.getXWhenFirstTouched() - model.getLeft() + (-scrollOffset)) / model.getWidth();

        int keycode = model.getKeycodes().get(idx);
        Const.getXServerHolder().pressKeyOrPointer(keycode);
        return keycode;
    }

}
