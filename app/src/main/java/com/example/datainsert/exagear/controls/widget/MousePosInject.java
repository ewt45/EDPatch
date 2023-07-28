package com.example.datainsert.exagear.controls.widget;

import android.graphics.Point;
import android.graphics.PointF;

import com.eltechs.axs.PointerEventReporter;
import com.eltechs.axs.helpers.InfiniteTimer;
import com.eltechs.axs.widgets.viewOfXServer.ViewOfXServer;
import com.eltechs.axs.xserver.ViewFacade;

public class MousePosInject {
    private final InfiniteTimer timer;
    private final Point mPointerPos = new Point();
    boolean isRunning = false;
    private final ViewOfXServer mViewOfXServer;
    private final int mInterval;

    public MousePosInject(ViewOfXServer viewOfXServer, PointerEventReporter mPointReporter, int fingerLocationPollIntervalMs) {
        mViewOfXServer = viewOfXServer;
        mInterval=fingerLocationPollIntervalMs;
        this.timer = new InfiniteTimer(fingerLocationPollIntervalMs) {
            @Override // android.os.CountDownTimer
            public void onTick(long j) {
                viewOfXServer.getXServerFacade().injectPointerMove(mPointerPos.x, mPointerPos.y);
//                mPointReporter.pointerMove(mPointerPos.x, mPointerPos.y);
            }
        };
    }

    /**
     * 更新应该注入鼠标移动事件的位置 x y 为xserver单位的坐标
     */
    public void updatePointPos(int x, int y) {
        mPointerPos.x = x;
        mPointerPos.y = y;
    }

    long lastSendTime = System.currentTimeMillis();

    public void start() {
        long nowTime = System.currentTimeMillis();
        if(nowTime-lastSendTime < mInterval)
            return;
        lastSendTime = nowTime;
        mViewOfXServer.getXServerFacade().injectPointerMove(mPointerPos.x, mPointerPos.y);


        if (isRunning)
            return;
        this.isRunning=true;
        this.timer.start();
    }

    public void stop() {
        if (!isRunning)
            return;
        this.isRunning=false;
        this.timer.cancel();

    }

}

