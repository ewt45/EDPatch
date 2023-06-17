package com.example.datainsert.exagear.controls.axs.GuestAppActionAdapters;

import android.view.View;

import com.eltechs.axs.GestureStateMachine.GestureContext;
import com.eltechs.axs.GuestAppActionAdapters.MouseMoveAdapter;
import com.eltechs.axs.PointerEventReporter;
import com.eltechs.axs.geom.Point;
import com.eltechs.axs.widgets.viewOfXServer.TransformationHelpers;
import com.eltechs.axs.widgets.viewOfXServer.ViewOfXServer;
import com.eltechs.axs.xserver.Pointer;

/**
 * 用于相对移动的adapter。接收的xy是偏移量.
 * 移动逻辑：每次开始移动前获取指针当前位置，之后接收到从开始移动到当前的总偏移量，加上指针位置传给PointerEventReporter
 */
public class OffsetMouseAdapter implements MouseMoveAdapter {
    PointerEventReporter mReporter;
    ViewOfXServer viewOfXServer;
    float downX;
    float downY;
    //手指按下时的 指针位置， 单位是view
    float[] downPointerXYInView = {0,0};
    //上次moveTo接收到的dx和dy。如果都是0就不注入这次事件了
    float[] lastDeltaXY = {0,0};
    public OffsetMouseAdapter(GestureContext gestureContext) {
        this.viewOfXServer = gestureContext.getHostView();
        this.mReporter  =gestureContext.getPointerReporter();
    }


    @Override
    public void moveTo(float dx, float dy) {
        //不如反向思考，去掉多余的原地move事件(还不够，慢速移动也不行）
        if(dx==lastDeltaXY[0] && dy==lastDeltaXY[1]){
            return;
        }

        mReporter.pointerMove(downPointerXYInView[0]+dx,downPointerXYInView[1]+dy);

        lastDeltaXY[0]=dx;
        lastDeltaXY[1]= dy;
    }

    @Override
    public void prepareMoving(float x, float y) {
        downX=x;
        downY=y;

        //获取指针当前位置，并转为view单位
        Point downPointerXY = viewOfXServer.getXServerFacade().getPointerLocation();
        downPointerXYInView[0] = downPointerXY.x;
        downPointerXYInView[1] = downPointerXY.y;
        TransformationHelpers.mapPoints(viewOfXServer.getXServerToViewTransformationMatrix(),downPointerXYInView);


    }
}
