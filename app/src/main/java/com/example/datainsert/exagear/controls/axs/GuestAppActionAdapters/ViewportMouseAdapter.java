package com.example.datainsert.exagear.controls.axs.GuestAppActionAdapters;

import android.graphics.Matrix;

import com.eltechs.axs.GeometryHelpers;
import com.eltechs.axs.GestureStateMachine.GestureContext;
import com.eltechs.axs.GuestAppActionAdapters.MouseMoveAdapter;
import com.eltechs.axs.PointerEventReporter;
import com.eltechs.axs.geom.Point;
import com.eltechs.axs.widgets.viewOfXServer.TransformationHelpers;
import com.eltechs.axs.widgets.viewOfXServer.ViewOfXServer;
import com.eltechs.axs.xserver.LocksManager;
import com.eltechs.axs.xserver.ScreenInfo;


public class ViewportMouseAdapter implements MouseMoveAdapter {
    /**
     * 视角移动状态下，固定鼠标移动偏移量（到中心的距离）
     */
    final static int fixedDistance  = 100;

    PointerEventReporter mReporter;
    ViewOfXServer viewOfXServer;

    Point downPoint = new Point(0,0);
    //手指按下时的 指针位置， 单位是view
    float[] downPointerXYInView = {0,0};
    //上次moveTo接收到的dx和dy。如果都是0就不注入这次事件了
    float[] lastDeltaXY = {0,0};
    public ViewportMouseAdapter(GestureContext gestureContext) {
        this.viewOfXServer = gestureContext.getHostView();
        this.mReporter  =gestureContext.getPointerReporter();
    }

    @Override
    public void moveTo(float dx, float dy) {
//        //移动的时候。指针坐标其实一直是固定在中心了。在这里需要把dx dy缩放到距离中心长度100
//        float ratio = (float) (fixedDistance/ Math.sqrt(dx*dx+dy*dy));
//        //然后用父类的方法，移动到指针+偏移量的距离就行了
//        super.moveTo(dx*ratio, dy*ratio);

        //如果原地move，不发送事件
        if(dx==lastDeltaXY[0] && dy==lastDeltaXY[1]){
            return;
        }



//
//        //固定视角不行啊。那移动到边界时warp？也不行会回正 那不如反向思考，去掉多余的原地move事件（写在父类里了）
//
//        float[] peakMoveXYInXServer = {downPointerXYInView[0]+dx,downPointerXYInView[1]+dy};
//        TransformationHelpers.mapPoints(viewOfXServer.getViewToXServerTransformationMatrix(),peakMoveXYInXServer);
//        float[] screenInfo = {
//                this.viewOfXServer.getXServerFacade().getXServer().getScreenInfo().widthInPixels,
//                this.viewOfXServer.getXServerFacade().getXServer().getScreenInfo().heightInPixels
//        };
//
//        float[] dXYInXServer = {dx,dy};
//        TransformationHelpers.mapPoints(viewOfXServer.getViewToXServerTransformationMatrix(),dXYInXServer);
//
//        //如果超出视图，固定超出1/32(这个1/32要用XServer的长度计算）
//        if(peakMoveXYInXServer[0]>screenInfo[0])
//            dx = screenInfo[0]*33f/32-downPoint.x;
//        else if(peakMoveXYInXServer[0]<0)
//            dx = -screenInfo[0]/32f - downPoint.x;
//        else
//            dx =dXYInXServer[0];
//
//        if(peakMoveXYInXServer[1]>screenInfo[1])
//            dy = screenInfo[1]*33f/32 - downPoint.y;
//        else if(peakMoveXYInXServer[1]<0)
//            dy = -screenInfo[1]/32f - downPoint.y;
//        else dy = dXYInXServer[1];
//
//        viewOfXServer.getXServerFacade().injectPointerMove((int) (downPoint.x+dx), (int) (downPoint.y+dy));
//
//        lastDeltaXY[0]=dx;
//        lastDeltaXY[1]= dy;


    }


    public void warpToCenter() {
        LocksManager.XLock lockForInputDevicesManipulation = viewOfXServer.getXServerFacade().getXServer().getLocksManager().lockForInputDevicesManipulation();
        this.viewOfXServer.getXServerFacade().getXServer().getPointer().warpOnCoordinates(
                viewOfXServer.getXServerFacade().getScreenInfo().widthInPixels/2,
                viewOfXServer.getXServerFacade().getScreenInfo().heightInPixels/2
        );
        if (lockForInputDevicesManipulation != null) {
            lockForInputDevicesManipulation.close();
        }
    }


    @Override
    public void prepareMoving(float x, float y) {
        //获取指针当前位置(XServer单位）
        downPoint = viewOfXServer.getXServerFacade().getPointerLocation();
        downPointerXYInView = new float[]{downPoint.x,downPoint.y};
        TransformationHelpers.mapPoints(viewOfXServer.getXServerToViewTransformationMatrix(),downPointerXYInView);
    }

}
