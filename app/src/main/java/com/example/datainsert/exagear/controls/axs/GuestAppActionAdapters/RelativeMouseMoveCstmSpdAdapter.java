package com.example.datainsert.exagear.controls.axs.GuestAppActionAdapters;

import com.eltechs.axs.GuestAppActionAdapters.MouseMoveAdapter;
import com.eltechs.axs.widgets.viewOfXServer.ViewOfXServer;
import com.eltechs.axs.xserver.ViewFacade;


/**
 * 和RelativeToCurrentPositionMouseMoveAdapter一样但是可以自定义移动速度
 */
public class RelativeMouseMoveCstmSpdAdapter implements MouseMoveAdapter {
    private final static String TAG = "RelMoveCstmSpdAdapter";
    /**
     * 移动速率
     */
    public static float speedRatio = 1;
    private final ViewFacade facade;
    private final ViewOfXServer host;
//    private final MouseMoveAdapter subSubAdapter;
//    private OffsetMouseMoveAdapter subAdapter;
    /**
     * 现在只用这个adapter。在初始化的时候新建，不会每次按下手指新建.
     */
    private final MouseMoveAdapter offsetAdapter;
    //上次调用move的时候，传入的坐标(不对，是按下手指是的xy坐标）
    private float downX;
    private float downY;
    //调用move时传入的xy

    public RelativeMouseMoveCstmSpdAdapter(MouseMoveAdapter mouseMoveAdapter, ViewFacade viewFacade, ViewOfXServer viewOfXServer) {
        offsetAdapter = mouseMoveAdapter;
//        this.subSubAdapter = mouseMoveAdapter;
        this.facade = viewFacade;
        this.host = viewOfXServer;
    }

    @Override // com.eltechs.axs.GuestAppActionAdapters.MouseMoveAdapter
    public void prepareMoving(float x, float y) {
        downX = x;
        downY = y;
        //准备移动，记录手指按下时的位置
        offsetAdapter.prepareMoving(x,y);
//        Point pointerLocation = this.facade.getPointerLocation();
//        float[] fArr;
//        if(FalloutInterfaceOverlay2.isCursorLocked){
//            //从pointer获取移动起始坐标改为自己维护起始坐标试试？（或者在pointer里存储，然后通过一个新的方法调用）
//            //不太行，还是想办法不创建新的OffsetAdapter了，就用旧的吧？
//            fArr = new float[]{latestX, latestY};
//        }else{
//            fArr = new float[]{pointerLocation.x, pointerLocation.y};
//            TransformationHelpers.mapPoints(this.host.getXServerToViewTransformationMatrix(), fArr);
//
//        }
//
//        this.subAdapter = new OffsetMouseMoveAdapter(this.subSubAdapter, fArr[0] - x, fArr[1] - y);
    }

    @Override // com.eltechs.axs.GuestAppActionAdapters.MouseMoveAdapter
    public void moveTo(float x, float y) {
        //正式移动,将移动距离乘以一个加速度. 只传距离上次的位移值不准，还是要传距离起始位置的偏移值
        offsetAdapter.moveTo((x - downX) * speedRatio, (y - downY) * speedRatio);
        //        offsetAdapter.moveTo(lastX + (x - lastX) * speedRatio, lastY + (y - lastY) * speedRatio);
//        Assert.state(this.subAdapter != null);
//        latestX = x;
//        latestY = y;
//        this.subAdapter.moveTo(lastX + (x - lastX) * speedRatio, lastY + (y - lastY) * speedRatio);
    }
}