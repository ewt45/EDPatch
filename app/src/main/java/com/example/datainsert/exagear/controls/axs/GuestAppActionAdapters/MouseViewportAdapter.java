package com.example.datainsert.exagear.controls.axs.GuestAppActionAdapters;

import static com.example.datainsert.exagear.FAB.dialogfragment.BaseFragment.getPreference;
import static com.example.datainsert.exagear.controls.ControlsResolver.PREF_KEY_MOUSE_OFFWINDOW_DISTANCE;
import static com.example.datainsert.exagear.controls.ControlsResolver.PREF_KEY_MOUSE_VIEWPORT_INTERVAL;

import android.graphics.Point;
import android.util.Log;

import com.eltechs.axs.GestureStateMachine.GestureContext;
import com.eltechs.axs.PointerEventReporter;
import com.eltechs.axs.helpers.InfiniteTimer;
import com.eltechs.axs.widgets.viewOfXServer.ViewOfXServer;
import com.example.datainsert.exagear.QH;
import com.example.datainsert.exagear.controls.axs.GuestAppActionAdapters.OffsetMouseAdapter;

public class MouseViewportAdapter extends OffsetMouseAdapter {
    private static final String TAG = "MousePosInject";
    final int mXWidth; //xserver 宽
    final int mXHeight; //xserver 高
    private final InfiniteTimer timer;
    private final Point mPointerPos = new Point();
    private final ViewOfXServer mViewOfXServer;
    private final int mInterval ; //固定50毫秒吧
    private final int mMaxDistance;
    boolean isRunning = false;
    long lastSendTime = System.currentTimeMillis();

    public MouseViewportAdapter(GestureContext gestureContext) {

        this(gestureContext.getHostView(),gestureContext.getPointerReporter());
    }

    public MouseViewportAdapter(ViewOfXServer viewOfXServer, PointerEventReporter mPointReporter) {
        super(viewOfXServer,mPointReporter);
        mViewOfXServer = viewOfXServer;
        mMaxDistance = QH.getPreference().getInt(PREF_KEY_MOUSE_OFFWINDOW_DISTANCE, 19) + 1;
        mInterval = QH.getPreference().getInt(PREF_KEY_MOUSE_VIEWPORT_INTERVAL,20)+5;

        mXWidth = this.mViewOfXServer.getXServerFacade().getScreenInfo().widthInPixels;
        mXHeight = this.mViewOfXServer.getXServerFacade().getScreenInfo().heightInPixels;

        this.timer = new InfiniteTimer(mInterval) {
            @Override // android.os.CountDownTimer
            public void onTick(long j) {
                viewOfXServer.getXServerFacade().injectPointerMove(mPointerPos.x, mPointerPos.y);
//                mPointReporter.pointerMove(mPointerPos.x, mPointerPos.y);
            }
        };
    }

    /**
     * 更新应该注入鼠标移动事件的位置 x y 为android单位的坐标
     *
     * @param dx 距离起始按下位置的偏移 x
     * @param dy 距离起始按下位置的偏移 y
     */
    public void moveTo(float dx, float dy) {
        double deltaLength = Math.sqrt(dx * dx + dy * dy); //偏移距离过小时误差过大，不应移动
        if(deltaLength<0.3f)
            return;

        float ratio = (float) (mMaxDistance / deltaLength);
        mPointerPos.x = (int) (mXWidth / 2f + dx * ratio);
        mPointerPos.y = (int) (mXHeight / 2f + dy * ratio);

        long nowTime = System.currentTimeMillis(); //两次间隔太短时可能卡顿，不应移动
        if (nowTime - lastSendTime < mInterval) {
            Log.d(TAG, "start: 时间间隔太短，跳过发送移动事件");
            return;
        }
        lastSendTime = nowTime;

        mViewOfXServer.getXServerFacade().injectPointerMove(mPointerPos.x, mPointerPos.y);
        Log.d(TAG, "start: 发送移动事件：" + mPointerPos.x + ", " + mPointerPos.y);
    }

    @Override
    public void prepareMoving(float x, float y) {
    }
}

