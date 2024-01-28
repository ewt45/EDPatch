package com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.gestureMachine.State;

import static com.eltechs.axs.GuestAppActionAdapters.ScrollDirections.DirectionX.LEFT;
import static com.eltechs.axs.GuestAppActionAdapters.ScrollDirections.DirectionX.RIGHT;
import static com.eltechs.axs.GuestAppActionAdapters.ScrollDirections.DirectionY.DOWN;
import static com.eltechs.axs.GuestAppActionAdapters.ScrollDirections.DirectionY.UP;
import static com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.gestureMachine.FSMR.event.新手指按下;
import static com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.gestureMachine.FSMR.event.某手指松开;
import static com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.gestureMachine.adapter.MovementAccumulator2.Direction.ASC;

import android.graphics.Matrix;

import com.eltechs.axs.GuestAppActionAdapters.ScrollDirections;
import com.eltechs.axs.helpers.Assert;
import com.eltechs.axs.helpers.InfiniteTimer;
import com.eltechs.axs.widgets.viewOfXServer.TransformationHelpers;
import com.eltechs.axs.widgets.viewOfXServer.ViewOfXServer;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.Const;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.Finger;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.TouchAdapter;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.gestureMachine.AbstractFSMState2;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.gestureMachine.FSMR;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.gestureMachine.adapter.MouseScrollAdapter;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.gestureMachine.adapter.MovementAccumulator2;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.annotation.StateTag;
import com.google.gson.annotations.SerializedName;

import java.util.List;

@StateTag(tag = FSMR.state.手指移动_鼠标滚轮, events = {某手指松开,新手指按下})
public class StateFingerMoveToMouseScroll extends AbstractFSMState2 implements TouchAdapter {

    transient public long mFingerLocationPollIntervalMs = 30;
    transient public float mUnitsOfOneAndroidPixelX = 0.05f;
    transient public float mUnitsOfOneAndroidPixelY = 0.05f;
    @SerializedName(value = Const.GsonField.st_fingerIndex)
    public int mFingerIndex = 0;
    transient  private float movementUnitsInOnePixelX;
    transient private float movementUnitsInOnePixelY;
    transient private MouseScrollAdapter scrollAdapter;
    transient private MovementAccumulator2 movementX;
    transient private MovementAccumulator2 movementY;
    transient private Finger savedFinger;
    transient private InfiniteTimer timer;
    transient private ViewOfXServer viewOfXServer;
    public StateFingerMoveToMouseScroll() {

    }

    @Override
    protected void onAttach() {
        this.scrollAdapter = new MouseScrollAdapter();
        this.viewOfXServer = Const.viewOfXServerRef.get();
        if(viewOfXServer!=null){
            Matrix aToXMatrix = viewOfXServer.getViewToXServerTransformationMatrix();
            this.movementUnitsInOnePixelX = mUnitsOfOneAndroidPixelX * TransformationHelpers.getScaleX(aToXMatrix);
            this.movementUnitsInOnePixelY = mUnitsOfOneAndroidPixelY * TransformationHelpers.getScaleY(aToXMatrix);
        }
    }

    @Override
    public void notifyBecomeActive() {
        addTouchListener(this);
        this.timer = new InfiniteTimer(this.mFingerLocationPollIntervalMs) {
            @Override // android.os.CountDownTimer
            public void onTick(long millisUntilFinished) {
                if (getContext().getMachine().isActiveState(StateFingerMoveToMouseScroll.this)) {
                    notifyTimer();
                }
            }
        };
        this.timer.start();
        Assert.isTrue(getContext().getFingers().size() > mFingerIndex);
        this.savedFinger = getContext().getFingers().get(mFingerIndex);
        this.movementX = new MovementAccumulator2(this.movementUnitsInOnePixelX, 0.0f);
        this.movementY = new MovementAccumulator2(this.movementUnitsInOnePixelY, 0.0f);
        this.movementX.reset(this.savedFinger.getXWhenFirstTouched());
        this.movementY.reset(this.savedFinger.getYWhenFirstTouched());
        this.scrollAdapter.notifyStart();
    }

    @Override
    public void notifyBecomeInactive() {
        this.scrollAdapter.notifyStop();
        this.timer.cancel();
        removeTouchListener(this);
        savedFinger = null;
    }

    private void scrollImpl(MovementAccumulator2.Direction xDirection, MovementAccumulator2.Direction yDirection, boolean xShouldScroll, boolean yShouldScroll) {
        ScrollDirections.DirectionX directionX;
        ScrollDirections.DirectionY directionY;
        if (!xShouldScroll)
            directionX = ScrollDirections.DirectionX.NONE;
        else
            directionX = xDirection == ASC ? LEFT : RIGHT;

        if (!yShouldScroll)
            directionY = ScrollDirections.DirectionY.NONE;
        else
            directionY = yDirection == ASC ? UP : DOWN;

        this.scrollAdapter.scroll(directionX, directionY, 1);
    }

    /**
     * 为什么每次要减1？(好像意思时在这个时间间隔内移动了1像素的意思？
     */
    private void changeMovementUnits(MovementAccumulator2 accumulator, float fingerPos) {
        float movementUnitsAccumulated = accumulator.getMovementUnitsAccumulated() - 1.0f;
        if (movementUnitsAccumulated > 0.0f) {
            accumulator.setMovementUnitsAccumulated(movementUnitsAccumulated);
        } else {
            accumulator.stop(fingerPos);
        }
    }

    /**
     * 每隔 {@link #mFingerLocationPollIntervalMs} 毫秒 执行一次
     */
    private void notifyTimer() {
        long currentTimeMillis = System.currentTimeMillis();
        this.movementX.processFingerMovement(false, this.savedFinger.getX(), currentTimeMillis);
        this.movementY.processFingerMovement(false, this.savedFinger.getY(), currentTimeMillis);
        MovementAccumulator2.Direction xDirection = this.movementX.getDirection();
        MovementAccumulator2.Direction yDirection = this.movementY.getDirection();
        boolean xShouldScroll = xDirection != MovementAccumulator2.Direction.NEUTRAL && this.movementX.getMovementUnitsAccumulated() >= 1.0f;
        boolean yShouldScroll = yDirection != MovementAccumulator2.Direction.NEUTRAL && this.movementY.getMovementUnitsAccumulated() >= 1.0f;
        scrollImpl(xDirection, yDirection, xShouldScroll, yShouldScroll);
        if (xShouldScroll)
            changeMovementUnits(this.movementX, this.savedFinger.getX());
        if (yShouldScroll)
            changeMovementUnits(this.movementY, this.savedFinger.getY());
//        if (getContext().getFingers().isEmpty()) {
//            if (!this.breakIfFingerReleased && (xShouldScroll || yShouldScroll)) {
//                return;
//            }
//            sendEvent(COMPLETED);
//        }
    }

    @Override
    public void notifyMoved(Finger finger, List<Finger> list) {
        //TODO 如果手指移动多远，视图就滚多远，那么可以在这里写。
        // 但是如果要手指移动然后定住，但是视图一直滚动，就要用计时器了
    }

    @Override
    public void notifyReleased(Finger finger, List<Finger> list) {
        sendEvent(某手指松开);
    }

    @Override
    public void notifyTouched(Finger finger, List<Finger> list) {
        sendEvent(新手指按下);
    }
}
