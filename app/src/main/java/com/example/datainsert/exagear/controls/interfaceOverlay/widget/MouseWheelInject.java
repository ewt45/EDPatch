package com.example.datainsert.exagear.controls.interfaceOverlay.widget;

import android.util.Log;

import com.eltechs.axs.helpers.InfiniteTimer;
import com.eltechs.axs.widgets.viewOfXServer.ViewOfXServer;
import com.eltechs.axs.xserver.ViewFacade;

/**
 * 用于处理鼠标滚轮按钮。普通按钮应该是按下就行。鼠标滚轮按钮应该是不停按下松开按下松开，这样才能不停滚动
 */
public class MouseWheelInject {
    private final int fingerLocationPollIntervalMs; //按下的频率
    private final ViewFacade viewFacade;
    private final byte keycode;
    private InfiniteTimer timer;

    public MouseWheelInject(
            ViewFacade viewFacade,
            int fingerLocationPollIntervalMs,
            byte keycode){
        this.viewFacade = viewFacade;
        this.keycode = keycode;
        this.fingerLocationPollIntervalMs = fingerLocationPollIntervalMs;
    }


    public void start(){
        Log.d("", "start: 开始滚动鼠标滚轮");
        this.timer = new InfiniteTimer(this.fingerLocationPollIntervalMs) {
            @Override // android.os.CountDownTimer
            public void onTick(long j) {
                viewFacade.injectPointerButtonPress( keycode);
                viewFacade.injectPointerButtonRelease(keycode);

            }
        };
        this.timer.start();
    }
    public void stop(){
        Log.d("", "start: 停止滚动鼠标滚轮");
        this.timer.cancel();
    }



}
