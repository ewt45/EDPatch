//package com.example.datainsert.exagear.controls.interfaceOverlay.GuestAppActionAdapters;
//
//import android.graphics.Matrix;
//import android.graphics.PointF;
//
//import com.eltechs.axs.GeometryHelpers;
//import com.eltechs.axs.GuestAppActionAdapters.MouseMoveAdapter;
//import com.eltechs.axs.PointerEventReporter;
//import com.eltechs.axs.widgets.viewOfXServer.TransformationHelpers;
//import com.eltechs.axs.widgets.viewOfXServer.ViewOfXServer;
//
///**
// * 用于视角移动的adapter，允许移出视图（需要Pointer类里接收到的时候，也允许将移出视图的坐标不调整直接发送给监听器）
// */
//public class ViewportMouseMoveAdapter implements MouseMoveAdapter {
//    private static final String TAG="ViewportMouseMoveAdapter";
//    private final ViewOfXServer mViewOfXServer;
//
//    float[] downXYMapped = {0,0};
//    public ViewportMouseMoveAdapter(ViewOfXServer viewOfXServer) {
//        mViewOfXServer = viewOfXServer;
//        PointerEventReporter p;
//
//    }
//
//    /**
//     * 1. 每次移动后，记录本次发送出去的坐标。下次按下手指时可能会用到
//     * 2. 移动距离固定100吧，超过或者小于都缩放到100
//     * 3. 计算本次接收坐标与按下坐标差值，并缩放到距离中心100
//     */
//
//    final static int fixedDistance  = 100;
//    @Override
//    public void moveTo(float f, float f2) {
//        //没法用pointerEventReporter了，自己转换坐标然后直接用viewFacade注入鼠标移动吧
//
//        //将坐标单位转为为xserver的
//        float[] currentXYMapped = {f,f2};
//        TransformationHelpers.mapPoints( mViewOfXServer.getViewToXServerTransformationMatrix(),currentXYMapped);
//
//        //将偏移缩放到距离
//        float[] dXYMapped = this.transformDelta(currentXYMapped);
//
//        //使用相对位移，相对原点就是pointer的xyPos也就是固定中心
//        mViewOfXServer.getXServerFacade().injectPointerDelta((int) dXYMapped[0], (int) dXYMapped[1]);
//
//
////        float[] fArr = {f, f2};
////        TransformationHelpers.mapPoints(mViewOfXServer.getViewToXServerTransformationMatrix(), fArr);
////        float x = fArr[0];
////        float y =  fArr[1];
////
////        //固定定位到窗口内一个范围
////        int centerX = mViewOfXServer.getXServerFacade().getXServer().getScreenInfo().widthInPixels/2;
////        int centerY = mViewOfXServer.getXServerFacade().getXServer().getScreenInfo().heightInPixels/2;
////        float maxDistance = 100;
////        float temptDistance = GeometryHelpers.distance(centerX,centerY,x,y);
////        float ratio = maxDistance/temptDistance;
////        x = x * ratio;
////        y = y * ratio;
//
//
//        //固定 定位到窗口外
////        if (x > mViewOfXServer.getXServerFacade().getXServer().getScreenInfo().widthInPixels)
////            x = mViewOfXServer.getXServerFacade().getXServer().getScreenInfo().widthInPixels * 33 / 32;
////        else if (x < 0)
////            x = -mViewOfXServer.getXServerFacade().getXServer().getScreenInfo().widthInPixels / 32;
////
////        if (y > mViewOfXServer.getXServerFacade().getXServer().getScreenInfo().heightInPixels)
////            y = mViewOfXServer.getXServerFacade().getXServer().getScreenInfo().heightInPixels * 33 / 32;
////        else if (y < 0)
////            y = -mViewOfXServer.getXServerFacade().getXServer().getScreenInfo().heightInPixels / 32;
////
////        mViewOfXServer.getXServerFacade().injectPointerMove((int) x, (int) y);
//    }
//
//
//    @Override
//    public void prepareMoving(float x, float y) {
//        //记录起始位置，然后
//        downXYMapped[0] = x;
//        downXYMapped[1] = y;
//        TransformationHelpers.mapPoints(mViewOfXServer.getViewToXServerTransformationMatrix(),this.downXYMapped);
//    }
//
//
//    private float[] transformDelta(float[] currentXYMapped){
//        TransformationHelpers.mapPoints( mViewOfXServer.getViewToXServerTransformationMatrix(),currentXYMapped);
//
//        //将偏移缩放到距离
//        float ratio = fixedDistance/GeometryHelpers.distance(currentXYMapped[0],currentXYMapped[1],downXYMapped[0],downXYMapped[1]);
//        float dx = currentXYMapped[0] - downXYMapped[0];
//        float dy = currentXYMapped[1] - downXYMapped[1];
//        return new float[]{dx * ratio,dy * ratio}  ;
//    }
//}
