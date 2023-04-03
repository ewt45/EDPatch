//package com.eltechs.axs.GuestAppActionAdapters;
//
//import com.eltechs.axs.GeometryHelpers;
//import com.eltechs.axs.geom.Point;
//import com.eltechs.axs.helpers.Assert;
//import com.eltechs.axs.widgets.viewOfXServer.TransformationHelpers;
//import com.eltechs.axs.widgets.viewOfXServer.ViewOfXServer;
//import com.eltechs.axs.xserver.ViewFacade;
//
///* loaded from: classes.dex */
//public class RelativeToCurrentPositionMouseMoveAdapterAccelerate implements MouseMoveAdapter {
//    private final ViewFacade facade;
//    private final int fingerCount;
//    private final ViewOfXServer host;
//    private float lastPointerX;
//    private float lastPointerY;
//    private float lastX;
//    private float lastY;
//    private OffsetMouseMoveAdapter subadapter;
//    private final MouseMoveAdapter subsubadapter;
//
//    public RelativeToCurrentPositionMouseMoveAdapterAccelerate(MouseMoveAdapter mouseMoveAdapter, ViewFacade viewFacade, ViewOfXServer viewOfXServer, int i) {
//        this.subsubadapter = mouseMoveAdapter;
//        this.facade = viewFacade;
//        this.host = viewOfXServer;
//        this.fingerCount = i;
//    }
//
//    @Override // com.eltechs.axs.GuestAppActionAdapters.MouseMoveAdapter
//    public void moveTo(float f, float f2) {
//        float f3 = 0;
//        Assert.state(this.subadapter != null);
//        float f4 = this.fingerCount;
//        float distance = GeometryHelpers.distance(this.lastX, this.lastY, f, f2) * f4;
//        if (distance > 50.0f) {
//            f3 = 4.0f;
//        } else if (distance <= 20.0f) {
//            if (distance > 5.0f) {
//                f3 = 2.0f;
//            }
//            this.lastPointerX += (f - this.lastX) * f4;
//            this.lastPointerY += (f2 - this.lastY) * f4;
//            this.lastX = f;
//            this.lastY = f2;
//            this.subadapter.moveTo(this.lastPointerX, this.lastPointerY);
//        } else {
//            f3 = 3.0f;
//        }
//        f4 *= f3;
//        this.lastPointerX += (f - this.lastX) * f4;
//        this.lastPointerY += (f2 - this.lastY) * f4;
//        this.lastX = f;
//        this.lastY = f2;
//        this.subadapter.moveTo(this.lastPointerX, this.lastPointerY);
//    }
//
//    @Override // com.eltechs.axs.GuestAppActionAdapters.MouseMoveAdapter
//    public void prepareMoving(float f, float f2) {
//        this.lastX = f;
//        this.lastY = f2;
//        this.lastPointerX = f;
//        this.lastPointerY = f2;
//        Point pointerLocation = this.facade.getPointerLocation();
//        float[] fArr = {pointerLocation.x, pointerLocation.y};
//        TransformationHelpers.mapPoints(this.host.getXServerToViewTransformationMatrix(), fArr);
//        this.subadapter = new OffsetMouseMoveAdapter(this.subsubadapter, fArr[0] - f, fArr[1] - f2);
//    }
//}
