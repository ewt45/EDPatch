package com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.gestureMachine.State;

import static com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.Const.dp8;
import static com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.gestureMachine.FSMR.event.手指_未移动;
import static com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.gestureMachine.FSMR.event.手指_移动_快速;
import static com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.gestureMachine.FSMR.event.手指_移动_慢速;
import static com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.gestureMachine.FSMR.event.新手指按下;
import static com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.gestureMachine.FSMR.event.某手指_未移动并松开;
import static com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.gestureMachine.FSMR.event.某手指_移动并松开;

import android.content.Context;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.eltechs.axs.GeometryHelpers;
import com.eltechs.axs.helpers.Assert;
import com.eltechs.axs.helpers.OneShotTimer;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.Const;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.Finger;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.TouchAdapter;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.annotation.StateTag;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.gestureMachine.FSMR;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.gestureMachine.FSMState2;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.gestureMachine.GestureContext2;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.widget.DrawableAlign;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.widget.LimitEditText;
import com.example.datainsert.exagear.QH;
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
        events = {手指_未移动,
                手指_移动_慢速,
                手指_移动_快速,
                某手指_未移动并松开,
                某手指_移动并松开,
                新手指按下 //时间段内有新手指按下，立刻发送此事件
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
        //TODO 感觉有问题，如果这个距离单位是inch的话，应该获取xdpi才对
        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_IN, 0.03f, Const.getContext().getResources().getDisplayMetrics());
        float dpi = Const.getDpi();
        float maxMovePx = Const.fingerStandingMaxMoveInches * dpi;

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
            sendEvent(手指_移动_快速);
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
            sendEvent(某手指_未移动并松开);
        } else {
            sendEvent(某手指_移动并松开);
        }
    }

    @Override
    public void notifyTouched(Finger finger, List<Finger> list) {
        sendEvent(新手指按下);
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
            sendEvent(手指_未移动);
        } else if (this.currentDistance < this.mFastMoveThreshold) {
            sendEvent(手指_移动_慢速);
        } else {
            sendEvent(手指_移动_快速);
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

        //TODO getFieldS返回数组，如果包含$字符，则第二个元素是说明，否则第二个元素是空
        return createEditViewQuickly(c,
                new String[][]{
                        {"小于此距离则算作不移动", null},
                        {"大于此距离则算作快速移动", null},
                        {"倒计时限时 (毫秒)", null},
                        {"观测第几根手指", null},},
                new View[]{
                       editNoMoveThreshold,
                       editFastMoveThreshold,
                       editCountDownMs,
                       editFingerIndex
                });
    }


    //TODO
    // map<string, adapterProperties> key为该adapter对应名称，用注解标注，adapterProperties里记录adapter的class，以及全部可编辑属性
    // editable只用于编辑，gson处理还是常规处理
    // 序列化：父类记录一个name，每个adapter唯一。（读取全部adapter放入map的时候检查一下有没有重复的吧）子类adapter用注解标注到类上，父类在构造函数里获取注解（此时能获取到吗）
    // 反序列化：通过name找到对应adapter的class，然后想办法传入gestureContext
    // editable：为用户可编辑的属性添加注解。初始化时获取这些field，根据注解类型，新建属性编辑视图（seekbar，edittext，checkbox，radiogroup）
    //   用户与视图交互时，在视图回调中调用对应的field.set 为对应adapter实例设置对应的值（再包一层，可能输入数值需要变换之后才作为属性值）
    // 放弃，注解功能还是太有限了，比如添加功能介绍
    // 反序列化要不就无参构造函数吧，然后再单独写一个init函数，在加到状态机里的时候调用一下就行了
    public static class Builder {
        private StateCountDownMeasureSpeed i;

        public Builder(GestureContext2 gesture) {
            i = new StateCountDownMeasureSpeed();
        }

        public StateCountDownMeasureSpeed create() {
            StateCountDownMeasureSpeed tmp = i;
            i = null;
            return tmp;

        }
    }


}
