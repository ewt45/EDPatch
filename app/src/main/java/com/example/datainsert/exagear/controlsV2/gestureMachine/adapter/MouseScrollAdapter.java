package com.example.datainsert.exagear.controlsV2.gestureMachine.adapter;

import android.graphics.PointF;

import com.example.datainsert.exagear.controlsV2.Const;

public class MouseScrollAdapter {
    /** 发送滚动事件后，该坐标与最新手指位置同步。直到下次发送滚动事件之前，这个值都不变，用于计算与最新手指位置的距离 */
    private final PointF posSinceLastSenScroll = new PointF();
    /** 发送滚动事件后，剩余的不够滚动一次的距离记录在这里。下次计算距离时加上这个数值 */
    private float distLessThanOneScroll = 0;

    public void stop(){

    }

    public void start(float x, float y) {
        posSinceLastSenScroll.set(x,y);
        distLessThanOneScroll = 0;
    }
    //TODO 如何能横向滚动？（看看tx11？）
    public void scroll(float nowX, float nowY) {
        //计算从上次滚动以来，手指移动距离。如果足够滚动一次或以上，则滚动并更新历史手指位置。
        //最好再加个变量记录本次滚动整数次之后余下的距离，下次计算时再加上这个距离才更准确
        float dist = nowY - posSinceLastSenScroll.y + distLessThanOneScroll;
        int times = (int) (dist/ Const.fingerMoveDistToOneScrollUnit);
        if(times != 0){//注意times可能为负数，所以不能直接>0
            posSinceLastSenScroll.set(nowX, nowY);
            distLessThanOneScroll = dist - (times * Const.fingerMoveDistToOneScrollUnit);
            //这里有一点注意事项，就是如果手指往上滑，那么用户应该是想将视图向下滚动，而不是视图向上滚动
            if(dist > 0)
                Const.getXServerHolder().injectPointerWheelUp(times);
            else
                Const.getXServerHolder().injectPointerWheelDown(-times);
        }
    }
}
