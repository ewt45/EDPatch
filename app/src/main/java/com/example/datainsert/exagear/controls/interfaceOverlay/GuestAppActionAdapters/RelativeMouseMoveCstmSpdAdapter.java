package com.example.datainsert.exagear.controls.interfaceOverlay.GuestAppActionAdapters;

import com.eltechs.axs.GuestAppActionAdapters.MouseMoveAdapter;
import com.eltechs.axs.GuestAppActionAdapters.OffsetMouseMoveAdapter;
import com.eltechs.axs.geom.Point;
import com.eltechs.axs.helpers.Assert;
import com.eltechs.axs.widgets.viewOfXServer.TransformationHelpers;
import com.eltechs.axs.widgets.viewOfXServer.ViewOfXServer;
import com.eltechs.axs.xserver.ViewFacade;


/**
 * 和RelativeToCurrentPositionMouseMoveAdapter一样但是可以自定义移动速度
 */
public class RelativeMouseMoveCstmSpdAdapter implements MouseMoveAdapter {
    private final ViewFacade facade;
    private final ViewOfXServer host;
    private OffsetMouseMoveAdapter subAdapter;
    private final MouseMoveAdapter subSubAdapter;
    //上次调用move的时候，传入的坐标
    private float lastX;
    private float lastY;
    /**
     * 移动速率
     */
    public static float speedRatio=1;

    public RelativeMouseMoveCstmSpdAdapter(MouseMoveAdapter mouseMoveAdapter, ViewFacade viewFacade, ViewOfXServer viewOfXServer) {
        this.subSubAdapter = mouseMoveAdapter;
        this.facade = viewFacade;
        this.host = viewOfXServer;
    }

    @Override // com.eltechs.axs.GuestAppActionAdapters.MouseMoveAdapter
    public void prepareMoving(float x, float y) {
        lastX = x;
        lastY=y;
        Point pointerLocation = this.facade.getPointerLocation();
        float[] fArr = {pointerLocation.x, pointerLocation.y};
        TransformationHelpers.mapPoints(this.host.getXServerToViewTransformationMatrix(), fArr);
        this.subAdapter = new OffsetMouseMoveAdapter(this.subSubAdapter, fArr[0] - x, fArr[1] - y);
    }

    @Override // com.eltechs.axs.GuestAppActionAdapters.MouseMoveAdapter
    public void moveTo(float x, float y) {
        Assert.state(this.subAdapter != null);
        this.subAdapter.moveTo(lastX+(x-lastX)*speedRatio, lastY+(y-lastY)*speedRatio);
    }
}