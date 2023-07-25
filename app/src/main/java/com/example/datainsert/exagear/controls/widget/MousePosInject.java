package com.example.datainsert.exagear.controls.widget;

import android.graphics.PointF;

import com.eltechs.axs.PointerEventReporter;
import com.eltechs.axs.helpers.InfiniteTimer;
import com.eltechs.axs.xserver.ViewFacade;

public class MousePosInject {
    private final InfiniteTimer timer;
    private final PointF mPointerPos = new PointF();
    boolean isRunning = false;

    public MousePosInject(PointerEventReporter mPointReporter, int fingerLocationPollIntervalMs) {

        this.timer = new InfiniteTimer(fingerLocationPollIntervalMs) {
            @Override // android.os.CountDownTimer
            public void onTick(long j) {
                mPointReporter.pointerMove(mPointerPos.x, mPointerPos.y);
            }
        };
    }

    /**
     * 更新应该注入鼠标移动事件的位置 x y 为android view单位的坐标
     */
    public void updatePointPos(float x, float y) {
        mPointerPos.x = x;
        mPointerPos.y = y;
    }


    public void start() {
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

