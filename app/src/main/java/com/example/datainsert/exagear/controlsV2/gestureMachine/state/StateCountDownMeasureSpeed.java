package com.example.datainsert.exagear.controlsV2.gestureMachine.state;

import static com.example.datainsert.exagear.RR.ctr2_stateProp_countDown;
import static com.example.datainsert.exagear.RR.ctr2_stateProp_fastMoveThres;
import static com.example.datainsert.exagear.RR.getS;

import android.content.Context;
import android.view.View;

import com.eltechs.axs.GeometryHelpers;
import com.eltechs.axs.helpers.Assert;
import com.eltechs.axs.helpers.OneShotTimer;
import com.example.datainsert.exagear.RR;
import com.example.datainsert.exagear.controlsV2.Const;
import com.example.datainsert.exagear.controlsV2.Finger;
import com.example.datainsert.exagear.controlsV2.TouchAdapter;
import com.example.datainsert.exagear.controlsV2.gestureMachine.StateTag;
import com.example.datainsert.exagear.controlsV2.gestureMachine.FSMR;
import com.example.datainsert.exagear.controlsV2.gestureMachine.FSMState2;
import com.example.datainsert.exagear.controlsV2.widget.LimitEditText;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * 设定一个倒计时，测量这段时间，某根手指的移动距离。
 * 如果在时间段内，
 * 手指出现move且移动距离大于flashFingerMaxMove，则发送FINGER_FLASHED
 * 某个手指松开，且移动距离小于tappingFingerMaxMove，则发送Tap事件，否则发送WalkAndGone事件
 * 新的手指按下，发送Touch事件
 * 如果在时间段内没发送任何事件，那么在倒计时结束后
 * 比standingFingerMaxMove还小，发送Stand事件，
 * 比standingFingerMaxMove大，但小于flashFingerMaxMove，发送Walk事件
 * 比flashFingerMaxMove还大，发送Flash事件
 */
@StateTag(tag = FSMR.state.限时测速,
        events = {FSMR.event.手指_未移动,
                FSMR.event.手指_移动_慢速,
                FSMR.event.手指_移动_快速,
                FSMR.event.某手指_未移动并松开,
                FSMR.event.某手指_移动并松开,
                FSMR.event.新手指按下 //时间段内有新手指按下，立刻发送此事件
        })
public class StateCountDownMeasureSpeed extends FSMState2 implements TouchAdapter {
    @SerializedName(value = Const.GsonField.st_noMoveThreshold)
    public float mNoMoveThreshold = 12f;
    @SerializedName(value = Const.GsonField.st_fastMoveThreshold)
    public float mFastMoveThreshold = mNoMoveThreshold * 3;
    @SerializedName(value = Const.GsonField.st_countDownMs)
    public int mCountDownMs = 250;
    @SerializedName(value = Const.GsonField.st_fingerIndex)
    public int mFingerIndex = 0;
    transient private double currentDistance;
    transient private Finger observeFinger;
//    @IntRangeEditable(tag = 一指测速_超时倒计时, range = {0, Integer.MAX_VALUE}, defVal = 200)

    transient private OneShotTimer timer;

    public StateCountDownMeasureSpeed() {
//        this.mNoMoveMaxDistance = maxMovePx;
//        this.mFastMoveMinDistance = maxMovePx;
//        this.mTapMaxDistance = Const.fingerTapMaxMoveInches * dpi;
    }

    @Override
    protected void onAttach() {
        //检查fastMove大于noMove
        if (mFastMoveThreshold < mNoMoveThreshold)
            mFastMoveThreshold = mNoMoveThreshold;
    }

    @Override
    public void notifyMoved(Finger finger, List<Finger> list) {
        if (mFingerIndex == FSMR.value.观测手指序号_全部) {
//            Log.d(debugName, "notifyMoved: 更换手指："+observeFinger+" -> "+finger);
            observeFinger = finger;
        }
        recalculateDistance();
        if (this.currentDistance >= this.mFastMoveThreshold) {
            sendEvent(FSMR.event.手指_移动_快速);
        }
    }

    @Override
    public void notifyReleased(Finger finger, List<Finger> list) {
//        Assert.state(getContext().getFingers().isEmpty());
        //此时finger已经被从列表中移除了，所以不能通过fingerIndex获取
        if (mFingerIndex == FSMR.value.观测手指序号_全部)
            observeFinger = finger;
        recalculateDistance();
        if (this.currentDistance <= this.mNoMoveThreshold) {
            sendEvent(FSMR.event.某手指_未移动并松开);
        } else {
            sendEvent(FSMR.event.某手指_移动并松开);
        }
    }

    @Override
    public void notifyTouched(Finger finger, List<Finger> list) {
        sendEvent(FSMR.event.新手指按下);
    }

    @Override
    public void notifyBecomeActive() {
        Assert.state(getContext().getFingers().size() > mFingerIndex);
        observeFinger = mFingerIndex >= 0 ? getContext().getFingers().get(mFingerIndex) : null;
        addTouchListener(this);
        this.timer = new OneShotTimer(this.mCountDownMs) {
            @Override // android.os.CountDownTimer
            public void onFinish() {
                if (getContext().getMachine().isActiveState(StateCountDownMeasureSpeed.this)) {
                    notifyTimeout();
                }
            }
        };

        this.currentDistance = 0.0d;
        this.timer.start();

    }

    private void notifyTimeout() {
        Assert.isTrue(getContext().getFingers().size() > mFingerIndex);
        recalculateDistance();
//        Log.d("", "notifyTimeout: 倒计时结束" + mCountDownMs + ", 移动距离=" + currentDistance);
        if (this.currentDistance <= this.mNoMoveThreshold) {
            sendEvent(FSMR.event.手指_未移动);
        } else if (this.currentDistance < this.mFastMoveThreshold) {
            sendEvent(FSMR.event.手指_移动_慢速);
        } else {
            sendEvent(FSMR.event.手指_移动_快速);
        }
    }

    @Override
    public void notifyBecomeInactive() {
        removeTouchListener(this);
        this.timer.cancel();
        observeFinger = null;
    }

    private void recalculateDistance() {
        if (observeFinger == null)
            return;
        double distance = GeometryHelpers.distance(observeFinger.getX(), observeFinger.getY(), observeFinger.getXWhenFirstTouched(), observeFinger.getYWhenFirstTouched());
//        Log.d(getNiceName(), "recalculateDistance: 计算距离：" + currentDistance + " -> " + distance);
        if (this.currentDistance < distance) {
            this.currentDistance = distance;
        }
    }

    @Override
    public View createPropEditView(Context c) {
        LimitEditText editNoMoveThreshold = new LimitEditText(c)
                .setCustomInputType(LimitEditText.TYPE_NUMBER_FLOAT)
                .setFloatValue(mNoMoveThreshold)
                .setUpdateListener(editText -> {
                    mNoMoveThreshold = editText.getFloatValue();
                    if (mNoMoveThreshold > mFastMoveThreshold)
                        mFastMoveThreshold = mNoMoveThreshold;
                });

        LimitEditText editFastMoveThreshold = new LimitEditText(c)
                .setCustomInputType(LimitEditText.TYPE_NUMBER_FLOAT)
                .setFloatValue(mFastMoveThreshold)
                .setUpdateListener(editText -> {
                    mFastMoveThreshold = editText.getFloatValue();
                    if (mNoMoveThreshold > mFastMoveThreshold)
                        mFastMoveThreshold = mNoMoveThreshold;
                });

        LimitEditText editCountDownMs = new LimitEditText(c)
                .setCustomInputType(LimitEditText.TYPE_NUMBER_INT)
                .setRange(0, Integer.MAX_VALUE)
                .setIntValue(mCountDownMs)
                .setUpdateListener(editText -> mCountDownMs = editText.getIntValue());

        LimitEditText editFingerIndex = new LimitEditText(c)
                .setCustomInputType(LimitEditText.TYPE_GIVEN_OPTIONS)
                .setSelectableOptions(FSMR.value.观测手指序号_全部可用选项)
                .setSelectedValue(mFingerIndex)
                .setUpdateListener(editText -> mFingerIndex = editText.getSelectedValue());

        return createEditViewQuickly(c,
                new String[][]{
                        {getS(RR.ctr2_stateProp_noMoveThreshold), null}, //小于此距离则算作不移动
                        {getS(ctr2_stateProp_fastMoveThres), null},//大于此距离则算作快速移动
                        {getS(ctr2_stateProp_countDown), null},//倒计时限时 (毫秒)
                        {getS(RR.ctr2_stateProp_fingerIndex), null},},//观测第几根手指
                new View[]{
                       editNoMoveThreshold,
                       editFastMoveThreshold,
                       editCountDownMs,
                       editFingerIndex
                });
    }
}
